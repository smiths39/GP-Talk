package com.app.gptalk.homepage.gp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.expandable_listview.GPSessionsExpandableListAdapter;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GPFragmentSchedule extends Fragment {

    private GPSessionsExpandableListAdapter unconfirmedAdapter, confirmedAdapter, rejectedAdapter;
    private ExpandableListView lvNotConfirmed, lvConfirmed, lvRejected;
    private List<String> unconfirmedHeader, confirmedHeader, rejectedHeader;
    private HashMap<String, List<String>> listChildUnconfirm;
    private HashMap<String, List<String>> listChildConfirm;
    private HashMap<String, List<String>> listChildReject;

    private BaseClass baseClass = new BaseClass();

    private final String UNCONFIRMED = baseClass.unconfirmedStatus;
    private final String CONFIRMED = baseClass.confirmedStatus;
    private final String REJECTED = baseClass.rejectedStatus;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gp_schedule, container, false);
        initialiseVariables(view);

        prepareListData();

        // Create each expandable listview with data corresponding to each type
        unconfirmedAdapter = new GPSessionsExpandableListAdapter(GPFragmentSchedule.this.getActivity(), unconfirmedHeader, listChildUnconfirm, "Awaiting Confirmation", Color.YELLOW);
        confirmedAdapter = new GPSessionsExpandableListAdapter(GPFragmentSchedule.this.getActivity(), confirmedHeader, listChildConfirm, "Confirmed", Color.GREEN);
        rejectedAdapter = new GPSessionsExpandableListAdapter(GPFragmentSchedule.this.getActivity(), rejectedHeader, listChildReject, "Rejected", Color.RED);

        // Initialise each expandable listview
        lvNotConfirmed.setAdapter(unconfirmedAdapter);
        lvConfirmed.setAdapter(confirmedAdapter);
        lvRejected.setAdapter(rejectedAdapter);

        lvNotConfirmed.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {

                String selectedDate = (String)unconfirmedAdapter.getChild(groupPosition, childPosition);
                launchPatientScheduleList(selectedDate, UNCONFIRMED);
                return false;
            }
        });

        lvConfirmed.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {

                String selectedDate = (String)confirmedAdapter.getChild(groupPosition, childPosition);
                launchPatientScheduleList(selectedDate, CONFIRMED);

                return false;
            }
        });

        lvRejected.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {

                String selectedDate = (String)rejectedAdapter.getChild(groupPosition, childPosition);
                launchPatientScheduleList(selectedDate, REJECTED);

                return false;
            }
        });

        return view;
    }

    // When child in expandable listview is clicked, a list of all consultations on the selected date will be displayed
    private void launchPatientScheduleList(String selectedDate, String status) {

        FragmentManager fragmentManager = getFragmentManager();

        GPFragmentScheduleList gpScheduleList = new GPFragmentScheduleList(selectedDate, status);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .addToBackStack(null)
                .replace(R.id.fragmentGPScheduleConsultation, gpScheduleList);

        fragmentTransaction.commit();
    }

    private void prepareListData() {

        unconfirmedHeader = new ArrayList<String>();
        confirmedHeader = new ArrayList<String>();
        rejectedHeader = new ArrayList<String>();

        listChildUnconfirm = new HashMap<String, List<String>>();
        listChildConfirm = new HashMap<String, List<String>>();
        listChildReject = new HashMap<String, List<String>>();

        unconfirmedHeader.add("Dates");
        confirmedHeader.add("Dates");
        rejectedHeader.add("Dates");

        List<String> unconfirmedData = new ArrayList<String>();
        List<String> confirmedData = new ArrayList<String>();
        List<String> rejectedData = new ArrayList<String>();

        GPDatabaseHelper dbHelper = new GPDatabaseHelper(GPFragmentSchedule.this.getActivity());

        // Retrieve last row count in database for each entry corresponding to status
        int unconfirmedCount = dbHelper.getLastRowId(UNCONFIRMED);
        int confirmedCount = dbHelper.getLastRowId(CONFIRMED);
        int rejectedCount = dbHelper.getLastRowId(REJECTED);

        try {
            if (unconfirmedCount != 0) {
                retrieveAllDates(unconfirmedCount, UNCONFIRMED, dbHelper, unconfirmedData);
            }

            if (confirmedCount != 0) {
                retrieveAllDates(confirmedCount, CONFIRMED, dbHelper, confirmedData);
            }

            if (rejectedCount != 0) {
                retrieveAllDates(rejectedCount, REJECTED, dbHelper, rejectedData);
            }

            dbHelper.close();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }

        // Populate expandable listview content with data in corresponding arraylist
        listChildUnconfirm.put(unconfirmedHeader.get(0), unconfirmedData);
        listChildConfirm.put(confirmedHeader.get(0), confirmedData);
        listChildReject.put(rejectedHeader.get(0), rejectedData);
    }

    private void retrieveAllDates(int count, String status, GPDatabaseHelper dbHelper, List<String> insertList) throws SQLException {

        // Get date of each consultation with the corresponding status
        // Unique dates are inserted into a list
        for (int index = 1; index <= count; index++) {

            String date = dbHelper.getConsultationDetail(index, status, dbHelper.KEY_BOOKING_DATE);

            if (date != null && !insertList.contains(date)) {
                insertList.add(date);
            }
        }
    }

    private void initialiseVariables(View view) {

        lvNotConfirmed = (ExpandableListView) view.findViewById(R.id.lvConsultationsNotConfirmed);
        lvConfirmed = (ExpandableListView) view.findViewById(R.id.lvConsultationsConfirmed);
        lvRejected = (ExpandableListView) view.findViewById(R.id.lvConsultationsRejected);
    }
}