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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.gptalk.custom_adapters.CustomHistoryListViewAdapter;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.listview_getter.GPHistoryItem;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.R;

import java.io.InputStream;
import java.util.ArrayList;

public class GPFragmentHistory extends Fragment {

    private ListView lvPatientDetails;
    private CustomHistoryListViewAdapter adapter;

    private TextView tvHistoryTitle;
    private Bitmap profilePhotoBitmap = null;
    private ArrayList<GPHistoryItem> detailsList;

    private ArrayList<GPHistoryItem> searchResults;

    private ArrayList<String> profileURLList = new ArrayList<String>();
    private ArrayList<String> firstNameList = new ArrayList<String>();
    private ArrayList<String> lastNameList = new ArrayList<String>();
    private ArrayList<String> emailList = new ArrayList<String>();

    private BaseClass baseClass = new BaseClass();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gp_history_list, container, false);

        initialiseWidgets(view);
        patientDisplayDetails();

        return view;
    }

    private void initialiseWidgets(View view) {

        lvPatientDetails = (ListView) view.findViewById(R.id.lvGPHistoryScheduleList);
        tvHistoryTitle = (TextView) view.findViewById(R.id.tvGPHistoryTitle);

        tvHistoryTitle.setText("Patients");
    }

    private void patientDisplayDetails() {

        detailsList = new ArrayList<GPHistoryItem>();
        GPDatabaseHelper dbHelper = new GPDatabaseHelper(GPFragmentHistory.this.getActivity());

        try {
            new RetrievePatientHistoryDetails(dbHelper).execute();

            dbHelper.close();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }

        updateList();
    }

    private void retrieveIndividualPatient(GPDatabaseHelper dbHelper) throws SQLException {

        // Retrieve number of rows currently in Consultation table
        int rowCount = dbHelper.getRowCount();

        for (long index = 1; index <= rowCount; index++) {

            // Retrieve patient details corresponding to their unique email
            String patientProfileURL = dbHelper.getIndividualPatientDetails(index, emailList, dbHelper.KEY_PROFILE);
            String patientFirstName = dbHelper.getIndividualPatientDetails(index, emailList, dbHelper.KEY_FIRST_NAME);
            String patientLastName =  dbHelper.getIndividualPatientDetails(index, emailList, dbHelper.KEY_LAST_NAME);
            String patientEmail = dbHelper.getIndividualPatientDetails(index, emailList, dbHelper.KEY_EMAIL);

            if (patientProfileURL.length() == 0) {
                // If user does not have a profile photo, display a default image instead
                profilePhotoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_picture);
            } else {

                String accessPhotoURL = "http://www.gptalk.ie/" + patientProfileURL;

                try {
                    // Convert stored photo url into bitmap image
                    InputStream inputStream = new java.net.URL(accessPhotoURL).openStream();
                    profilePhotoBitmap = BitmapFactory.decodeStream(inputStream);
                } catch (Exception e) {
                    baseClass.errorHandler(e);
                }
            }

            // Store retrieved details that are to be displayed in custom listview layout
            GPHistoryItem item = new GPHistoryItem(profilePhotoBitmap, patientFirstName, patientLastName);

            detailsList.add(item);
            profileURLList.add(patientProfileURL);
            firstNameList.add(patientFirstName);
            lastNameList.add(patientLastName);
            emailList.add(patientEmail);
        }
    }

    private void updateList() {

        adapter = new CustomHistoryListViewAdapter(GPFragmentHistory.this.getActivity(), R.layout.gp_history_listview_item, detailsList);
        lvPatientDetails.setAdapter(adapter);
        lvPatientDetails.setClickable(true);

        lvPatientDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String profileID = profileURLList.get(i);
                String firstNameID = firstNameList.get(i);
                String lastNameID = lastNameList.get(i);
                String emailID = emailList.get(i);

                // If a listview item is pressed, display a list of all previous consultation modifications
                GPFragmentHistoryList gpHistoryList = new GPFragmentHistoryList(profileID, firstNameID, lastNameID, emailID);
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.fragmentScheduleHistoryGP, gpHistoryList);

                fragmentTransaction.commit();
            }
        });
    }

    public class RetrievePatientHistoryDetails extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;
        private GPDatabaseHelper dbHelper;

        public RetrievePatientHistoryDetails(GPDatabaseHelper dbHelper) {
            this.dbHelper = dbHelper;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(GPFragmentHistory.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving History Details...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            retrieveIndividualPatient(dbHelper);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            super.onPostExecute(result);
            progressDialog.dismiss();
            updateList();
        }
    }
}