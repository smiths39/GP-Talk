package com.app.gptalk.homepage.gp;

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
import com.app.gptalk.listview_getter.GPHistoryListItem;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.R;

import java.io.InputStream;
import java.util.ArrayList;

public class GPFragmentHistoryList extends Fragment {

    private TextView tvFirstName, tvLastName;
    private ListView lvPatientHistory;
    private CustomHistoryConsultationListViewAdapter adapter;

    private Bitmap profilePhotoBitmap = null;
    private ImageView ivPatientProfile;
    private ArrayList<GPHistoryListItem> detailsList;
    private ArrayList<String> emailList = new ArrayList<String>();
    private ArrayList<String> bookingDateList = new ArrayList<String>();
    private ArrayList<String> bookingTimeList = new ArrayList<String>();
    private ArrayList<String> bookingStatusList = new ArrayList<String>();

    private String profileURL, firstName, lastName, email;

    private BaseClass baseClass = new BaseClass();

    public GPFragmentHistoryList(String newProfileURL, String newFirstName, String newLastName, String newEmail) {

        this.profileURL = newProfileURL;
        this.firstName = newFirstName;
        this.lastName = newLastName;
        this.email = newEmail;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gp_history_consultation_list, container, false);
        initialiseWidgets(view);

        new RetrieveIndividualPatientHistoryConsultationDetails().execute();
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

        ivPatientProfile = (ImageView) view.findViewById(R.id.ivGPHistoryConsultationListPatientProfilePic);
        tvFirstName = (TextView) view.findViewById(R.id.tvGPHistoryConsultationListPatientFirstName);
        tvLastName  = (TextView) view.findViewById(R.id.tvGPHistoryConsultationListPatientLastName);
        lvPatientHistory = (ListView) view.findViewById(R.id.lvGPHistoryConsultationScheduleList);
    }

    private void displayConsultationHistory() {

        detailsList = new ArrayList<GPHistoryListItem>();

        GPDatabaseHelper dbHelper = new GPDatabaseHelper(GPFragmentHistoryList.this.getActivity());

        try {
            retrievePreviousConsultation(dbHelper);
            dbHelper.close();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    private void retrievePreviousConsultation(GPDatabaseHelper dbHelper) throws SQLException {

        // Retrieve number of rows currently in Consultation History table
        int maxUpdateCounter = dbHelper.getLatestHistoryCount(email);

        if (maxUpdateCounter != 0) {

            for (int index = 1; index <= maxUpdateCounter; index++) {

                String bookingDate = dbHelper.getConsultationHistoryDetail(index, email, dbHelper.KEY_BOOKING_DATE);
                String bookingTime =  dbHelper.getConsultationHistoryDetail(index, email, dbHelper.KEY_BOOKING_TIME);
                String bookingStatus = dbHelper.getConsultationHistoryDetail(index, email, dbHelper.KEY_STATUS);

                GPHistoryListItem item = new GPHistoryListItem(bookingDate, bookingTime, bookingStatus);

                detailsList.add(item);

                emailList.add(email);
                bookingDateList.add(bookingDate);
                bookingTimeList.add(bookingTime);
                bookingStatusList.add(bookingStatus);
            }
        }
    }

    private void updateList() {

        adapter = new CustomHistoryConsultationListViewAdapter(GPFragmentHistoryList.this.getActivity(), R.layout.gp_history_consultation_listview_item, detailsList);
        lvPatientHistory.setAdapter(adapter);

        lvPatientHistory.setClickable(true);
        lvPatientHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String emailID = emailList.get(i);
                String bookingDateID = bookingDateList.get(i);
                String bookingTimeID = bookingTimeList.get(i);
                String bookingStatusID = bookingStatusList.get(i);

                // If a listview item is pressed, display a list of the selected consultation modifications
                GPFragmentHistoryDetails gpHistoryDetail = new GPFragmentHistoryDetails(emailID, bookingDateID, bookingTimeID, bookingStatusID);
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.fragmentSchedulePatientHistoryListGP, gpHistoryDetail);

                fragmentTransaction.commit();
            }
        });
    }

    public class RetrieveIndividualPatientHistoryConsultationDetails extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(GPFragmentHistoryList.this.getActivity());
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
            ivPatientProfile.setImageBitmap(profilePhotoBitmap);
        }
    }
}
