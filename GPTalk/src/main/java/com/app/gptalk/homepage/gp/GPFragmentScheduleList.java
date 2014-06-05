package com.app.gptalk.homepage.gp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.gptalk.base.BaseClass;
import com.app.gptalk.custom_adapters.CustomScheduleListViewAdapter;
import com.app.gptalk.R;
import com.app.gptalk.listview_getter.GPScheduleListItem;
import com.app.gptalk.database.GPDatabaseHelper;

import java.util.ArrayList;

public class GPFragmentScheduleList extends Fragment {

    private TextView tvSelectedDateTitle;
    private ListView lvPatientDetails;
    private ArrayList<GPScheduleListItem> detailsList;
    private CustomScheduleListViewAdapter adapter;

    private ArrayList<String> emailList = new ArrayList<String>();

    private BaseClass baseClass = new BaseClass();

    private String selectedDate, status;

    public GPFragmentScheduleList(String selectedDate, String status) {

        this.selectedDate = selectedDate;
        this.status = status;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gp_schedule_list, container, false);

        initialiseWidgets(view, selectedDate);
        patientDisplayDetails(selectedDate, status);

        return view;
    }

    private void initialiseWidgets(View view, String date) {

        lvPatientDetails = (ListView) view.findViewById(R.id.lvScheduleList);
        tvSelectedDateTitle = (TextView) view.findViewById(R.id.tvSelectedDateTitle);

        tvSelectedDateTitle.setText(date);
    }

    private void patientDisplayDetails(String selectedDate, String status) {

        detailsList = new ArrayList<GPScheduleListItem>();

        GPDatabaseHelper dbHelper = new GPDatabaseHelper(GPFragmentScheduleList.this.getActivity());
        long recordCount = dbHelper.getRowCount();

        try {
            if (recordCount != 0) {
                retrievePatientDetails(selectedDate, status, recordCount, dbHelper);
            }
            dbHelper.close();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
        updateList(status);
    }

    private void retrievePatientDetails(String newSelectedDate, String newStatus, long newRecordCount, GPDatabaseHelper newDBHelper) {

        // Retrieve schedule details of each recorded patient
        for (int index = 0; index <= newRecordCount; index++) {

            try {
                String patientFirstName = newDBHelper.getPatientBioDetail(index, newStatus, newSelectedDate, newDBHelper.KEY_FIRST_NAME);
                String patientLastName =  newDBHelper.getPatientBioDetail(index, newStatus, newSelectedDate, newDBHelper.KEY_LAST_NAME);
                String patientEmail = newDBHelper.getPatientBioDetail(index, newStatus, newSelectedDate, newDBHelper.KEY_EMAIL);
                String patientBookingTime = newDBHelper.getPatientBioDetail(index, newStatus, newSelectedDate, newDBHelper.KEY_BOOKING_TIME);

                // If ID corresponds to a patient on a selected date
                if (!patientEmail.equals("")) {

                    GPScheduleListItem item = new GPScheduleListItem(patientFirstName, patientLastName, patientBookingTime);
                    detailsList.add(item);
                    emailList.add(patientEmail);
                }
            } catch (Exception e) {

            }
       }
    }

    private void updateList(final String status) {

        adapter = new CustomScheduleListViewAdapter(GPFragmentScheduleList.this.getActivity(), R.layout.gp_schedule_listview_item, detailsList);
        lvPatientDetails.setAdapter(adapter);

        lvPatientDetails.setClickable(true);

        // If a listview item is pressed, display a list of the most recently modified consultation
        lvPatientDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String emailID = emailList.get(i);

                FragmentManager fragmentManager = getFragmentManager();
                GPFragmentScheduleDetails gpScheduleDetails = new GPFragmentScheduleDetails(status, emailID, tvSelectedDateTitle.getText().toString(), "0");

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.fragmentGPScheduleConsultationList, gpScheduleDetails);

                fragmentTransaction.commit();
            }
        });
    }
}