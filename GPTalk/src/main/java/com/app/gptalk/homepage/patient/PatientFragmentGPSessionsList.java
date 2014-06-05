package com.app.gptalk.homepage.patient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.gptalk.base.BaseClass;
import com.app.gptalk.custom_adapters.CustomScheduleListViewAdapter;
import com.app.gptalk.R;
import com.app.gptalk.custom_adapters.CustomSessionListViewAdapter;
import com.app.gptalk.database.PatientDatabaseHelper;
import com.app.gptalk.listview_getter.GPScheduleListItem;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.listview_getter.PatientSessionListItem;

import java.util.ArrayList;

public class PatientFragmentGPSessionsList extends Fragment {

    private View view;
    private TextView tvSelectedDateTitle;
    private ListView lvBookingDetails;
    private ArrayList<PatientSessionListItem> detailsList;
    private CustomSessionListViewAdapter adapter;

    private ArrayList<String> phoneList = new ArrayList<String>();

    private BaseClass baseClass = new BaseClass();

    private String selectedDate, status;

    public PatientFragmentGPSessionsList(String selectedDate, String status) {

        this.selectedDate = selectedDate;
        this.status = status;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        view = inflater.inflate(R.layout.gp_schedule_list, container, false);

        setHasOptionsMenu(true);

        initialiseWidgets(view, selectedDate);
        sessionDisplayDetails(selectedDate, status);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    this.getActivity().finish();
                } else {
                    getFragmentManager().popBackStack();
                }
                break;
        }

        return false;
    }

    private void initialiseWidgets(View view, String date) {

        lvBookingDetails = (ListView) view.findViewById(R.id.lvScheduleList);
        tvSelectedDateTitle = (TextView) view.findViewById(R.id.tvSelectedDateTitle);

        tvSelectedDateTitle.setText(date);
    }

    private void sessionDisplayDetails(String selectedDate, String status) {

        detailsList = new ArrayList<PatientSessionListItem>();

        PatientDatabaseHelper dbHelper = new PatientDatabaseHelper(PatientFragmentGPSessionsList.this.getActivity());
        long recordCount = dbHelper.getRowCount();

        try {
            if (recordCount != 0) {
                retrieveBookingDetails(selectedDate, status, recordCount, dbHelper);
            }
            dbHelper.close();
        } catch (Exception e) {
//            baseClass.errorHandler(e);
        }
        updateList(status);
    }
//
    private void retrieveBookingDetails(String newSelectedDate, String newStatus, long newRecordCount, PatientDatabaseHelper newDBHelper) {

        // Retrieve schedule details of each recorded patient
        for (int index = 0; index <= newRecordCount; index++) {

            try {
                String gpTitle = newDBHelper.getGPBioDetail(index, newStatus, newSelectedDate, newDBHelper.KEY_GP_TITLE);
                String gpFirstName =  newDBHelper.getGPBioDetail(index, newStatus, newSelectedDate, newDBHelper.KEY_GP_FIRST_NAME);
                String gpLastName = newDBHelper.getGPBioDetail(index, newStatus, newSelectedDate, newDBHelper.KEY_GP_LAST_NAME);
                String gpPhoneNumber = newDBHelper.getGPBioDetail(index, newStatus, newSelectedDate, newDBHelper.KEY_GP_PHONE_NUMBER);
                String gpBookingTime = newDBHelper.getGPBioDetail(index, newStatus, newSelectedDate, newDBHelper.KEY_BOOKING_TIME);

                // If ID corresponds to a gp on a selected date
                if (!gpPhoneNumber.equals("")) {

                    PatientSessionListItem item = new PatientSessionListItem(gpTitle, gpFirstName, gpLastName, gpBookingTime);
                    detailsList.add(item);
                    phoneList.add(gpPhoneNumber);
                }
            } catch (Exception e) {

            }
        }
    }

    private void updateList(final String status) {

        adapter = new CustomSessionListViewAdapter(PatientFragmentGPSessionsList.this.getActivity(), R.layout.patient_session_listview_item, detailsList);
        lvBookingDetails.setAdapter(adapter);
        lvBookingDetails.setClickable(true);

        // If a listview item is pressed, display a list of the most recently modified consultation
        lvBookingDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String phoneID = phoneList.get(i);

                FragmentManager fragmentManager = getFragmentManager();

                // '0' is added to signify that a new time was not selected by the user
                PatientFragmentGPSessionsDetails gpSessionsDetails = new PatientFragmentGPSessionsDetails(status, phoneID, tvSelectedDateTitle.getText().toString(), "0");

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.fragmentGPScheduleConsultationList, gpSessionsDetails);

                fragmentTransaction.commit();
            }
        });
    }
}