package com.app.gptalk.homepage.patient;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.app.gptalk.base.BaseClass;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.database.PatientDatabaseHelper;
import com.app.gptalk.expandable_listview.GPSessionsExpandableListAdapter;
import com.app.gptalk.R;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatientFragmentGPSessions extends Fragment {

    private GPSessionsExpandableListAdapter unconfirmedAdapter, confirmedAdapter, rejectedAdapter;
    private ExpandableListView lvNotConfirmed, lvConfirmed, lvRejected;
    private List<String> unconfirmedHeader, confirmedHeader, rejectedHeader;
    private HashMap<String, List<String>> listChildUnconfirm;
    private HashMap<String, List<String>> listChildConfirm;
    private HashMap<String, List<String>> listChildReject;
    private View view;

    private BaseClass baseClass = new BaseClass();

    private final String UNCONFIRMED = baseClass.unconfirmedStatus;
    private final String CONFIRMED = baseClass.confirmedStatus;
    private final String REJECTED = baseClass.rejectedStatus;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getFragmentManager().popBackStack();

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        view = inflater.inflate(R.layout.gp_schedule, container, false);

        initialiseWidgets(view);

        prepareListData();

        // Create each expandable listview with data corresponding to each type
        unconfirmedAdapter = new GPSessionsExpandableListAdapter(PatientFragmentGPSessions.this.getActivity(), unconfirmedHeader, listChildUnconfirm, "Awaiting Confirmation", Color.YELLOW);
        confirmedAdapter = new GPSessionsExpandableListAdapter(PatientFragmentGPSessions.this.getActivity(), confirmedHeader, listChildConfirm, "Confirmed", Color.GREEN);
        rejectedAdapter = new GPSessionsExpandableListAdapter(PatientFragmentGPSessions.this.getActivity(), rejectedHeader, listChildReject, "Rejected", Color.RED);

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

    @Override
    public void onDetach() {

        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // When child in expandable listview is clicked, a list of all consultations on the selected date will be displayed
    private void launchPatientScheduleList(String selectedDate, String status) {

        FragmentManager fragmentManager = getFragmentManager();

        PatientFragmentGPSessionsList gpSessionList = new PatientFragmentGPSessionsList(selectedDate, status);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .addToBackStack(null)
                .replace(R.id.fragmentGPScheduleConsultation, gpSessionList);

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

        PatientDatabaseHelper dbHelper = new PatientDatabaseHelper(PatientFragmentGPSessions.this.getActivity());

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

    private void retrieveAllDates(int count, String status, PatientDatabaseHelper dbHelper, List<String> insertList) throws SQLException {

        // Get date of each consultation with the corresponding status
        // Unique dates are inserted into a list
        for (int index = 1; index <= count; index++) {

            String date = dbHelper.getConsultationDetail(index, status, dbHelper.KEY_BOOKING_DATE);

            if (date != null && !insertList.contains(date)) {
                insertList.add(date);
            }
        }
    }

    private void initialiseWidgets(View view) {

        lvNotConfirmed = (ExpandableListView) view.findViewById(R.id.lvConsultationsNotConfirmed);
        lvConfirmed = (ExpandableListView) view.findViewById(R.id.lvConsultationsConfirmed);
        lvRejected = (ExpandableListView) view.findViewById(R.id.lvConsultationsRejected);
    }
}