package com.app.gptalk.homepage.patient;

import android.app.ProgressDialog;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.gptalk.custom_adapters.CustomHistoryConsultationListViewAdapter;
import com.app.gptalk.database.PatientDatabaseHelper;
import com.app.gptalk.listview_getter.GPHistoryListItem;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.R;

import java.io.InputStream;
import java.util.ArrayList;

public class PatientFragmentHistoryList extends Fragment {

    private View view;
    private TextView tvFirstName, tvLastName;
    private ListView lvGPHistory;
    private CustomHistoryConsultationListViewAdapter adapter;

    private Bitmap profilePhotoBitmap = null;
    private ImageView ivGPProfile;
    private ArrayList<GPHistoryListItem> detailsList;
    private ArrayList<String> phoneList = new ArrayList<String>();
    private ArrayList<String> bookingDateList = new ArrayList<String>();
    private ArrayList<String> bookingTimeList = new ArrayList<String>();
    private ArrayList<String> bookingStatusList = new ArrayList<String>();

    private String profileURL, firstName, lastName, phone;

    private BaseClass baseClass = new BaseClass();

    public PatientFragmentHistoryList(String newProfileURL, String newFirstName, String newLastName, String newPhone) {

        this.profileURL = newProfileURL;
        this.firstName = newFirstName;
        this.lastName = newLastName;
        this.phone = newPhone;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        view = inflater.inflate(R.layout.gp_history_consultation_list, container, false);
        initialiseWidgets(view);

        new RetrieveIndividualGPHistoryConsultationDetails().execute();
        setWidgets();

        displayConsultationHistory();
        updateList();

        return view;
    }

    private void setWidgets() {

        tvFirstName.setText(firstName);
        tvLastName.setText(lastName);
    }

    private void initialiseWidgets(View view) {

        ivGPProfile = (ImageView) view.findViewById(R.id.ivGPHistoryConsultationListPatientProfilePic);
        tvFirstName = (TextView) view.findViewById(R.id.tvGPHistoryConsultationListPatientFirstName);
        tvLastName  = (TextView) view.findViewById(R.id.tvGPHistoryConsultationListPatientLastName);
        lvGPHistory = (ListView) view.findViewById(R.id.lvGPHistoryConsultationScheduleList);
    }

    private void displayConsultationHistory() {

        detailsList = new ArrayList<GPHistoryListItem>();

        PatientDatabaseHelper dbHelper = new PatientDatabaseHelper(PatientFragmentHistoryList.this.getActivity());

        try {
            retrievePreviousConsultation(dbHelper);
            dbHelper.close();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    private void retrievePreviousConsultation(PatientDatabaseHelper dbHelper) throws SQLException {

        // Retrieve number of rows currently in Consultation History table
        int maxUpdateCounter = dbHelper.getLatestHistoryCount(phone);

        if (maxUpdateCounter != 0) {

            for (int index = 1; index <= maxUpdateCounter; index++) {

                String bookingDate = dbHelper.getConsultationHistoryDetail(index, phone, dbHelper.KEY_BOOKING_DATE);
                String bookingTime =  dbHelper.getConsultationHistoryDetail(index, phone, dbHelper.KEY_BOOKING_TIME);
                String bookingStatus = dbHelper.getConsultationHistoryDetail(index, phone, dbHelper.KEY_STATUS);

                GPHistoryListItem item = new GPHistoryListItem(bookingDate, bookingTime, bookingStatus);

                detailsList.add(item);

                phoneList.add(phone);
                bookingDateList.add(bookingDate);
                bookingTimeList.add(bookingTime);
                bookingStatusList.add(bookingStatus);
            }
        }
    }

    private void updateList() {

        adapter = new CustomHistoryConsultationListViewAdapter(PatientFragmentHistoryList.this.getActivity(), R.layout.gp_history_consultation_listview_item, detailsList);
        lvGPHistory.setAdapter(adapter);

        lvGPHistory.setClickable(true);
        lvGPHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String phoneID = phoneList.get(i);
                String bookingDateID = bookingDateList.get(i);
                String bookingTimeID = bookingTimeList.get(i);
                String bookingStatusID = bookingStatusList.get(i);

//                // If a listview item is pressed, display a list of the selected consultation modifications
                PatientFragmentHistoryDetails gpHistoryDetail = new PatientFragmentHistoryDetails(phoneID, bookingDateID, bookingTimeID, bookingStatusID);
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.fragmentSchedulePatientHistoryListGP, gpHistoryDetail);

                fragmentTransaction.commit();
            }
        });
    }

    public class RetrieveIndividualGPHistoryConsultationDetails extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(PatientFragmentHistoryList.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving History Details...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                if (profileURL.length() == 0) {
                    // If user does not have a profile photo, display a default image instead
                    profilePhotoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_picture);
                } else {

                    String accessPhotoURL = "http://www.gptalk.ie/" + profileURL;

                    try {
                        // Convert stored photo url into bitmap image
                        InputStream inputStream = new java.net.URL(accessPhotoURL).openStream();
                        profilePhotoBitmap = BitmapFactory.decodeStream(inputStream);
                    } catch (Exception e) {
                        baseClass.errorHandler(e);
                    }
                }
            } catch (Exception e) {
                baseClass.errorHandler(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            super.onPostExecute(result);
            progressDialog.dismiss();
            ivGPProfile.setImageBitmap(profilePhotoBitmap);
        }
    }
}
