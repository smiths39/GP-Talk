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
import com.app.gptalk.database.PatientDatabaseHelper;
import com.app.gptalk.listview_getter.GPHistoryItem;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.R;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class PatientFragmentHistory extends Fragment {

    private View view;
    private TextView tvTitle;
    private ListView lvGPDetails;
    private CustomHistoryListViewAdapter adapter;

    private Bitmap profilePhotoBitmap = null;
    private ArrayList<GPHistoryItem> detailsList;
    private ArrayList<GPHistoryItem> searchResults;

    private ArrayList<String> profileURLList = new ArrayList<String>();
    private ArrayList<String> firstNameList = new ArrayList<String>();
    private ArrayList<String> lastNameList = new ArrayList<String>();
    private ArrayList<String> phoneList = new ArrayList<String>();

    private BaseClass baseClass = new BaseClass();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getFragmentManager().popBackStack();

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        view = inflater.inflate(R.layout.gp_history_list, container, false);

        initialiseWidgets(view);
        patientDisplayDetails();

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

    private void initialiseWidgets(View view) {

        lvGPDetails = (ListView) view.findViewById(R.id.lvGPHistoryScheduleList);
        tvTitle = (TextView) view.findViewById(R.id.tvGPHistoryTitle);

        tvTitle.setText("General Practitioners");
    }

    private void patientDisplayDetails() {

        detailsList = new ArrayList<GPHistoryItem>();
        PatientDatabaseHelper dbHelper = new PatientDatabaseHelper(PatientFragmentHistory.this.getActivity());

        try {
            new RetrieveGPHistoryDetails(dbHelper).execute();

            dbHelper.close();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }

        updateList();
    }

    private void retrieveIndividualGP(PatientDatabaseHelper dbHelper) throws SQLException {

        // Retrieve number of rows currently in Consultation table
        int rowCount = dbHelper.getRowCount();

        for (long index = 1; index <= rowCount; index++) {

            // Retrieve patient details corresponding to their unique email
            String gpProfileURL = dbHelper.getIndividualGPDetails(index, phoneList, dbHelper.KEY_GP_PROFILE);
            String gpFirstName = dbHelper.getIndividualGPDetails(index, phoneList, dbHelper.KEY_GP_FIRST_NAME);
            String gpLastName =  dbHelper.getIndividualGPDetails(index, phoneList, dbHelper.KEY_GP_LAST_NAME);
            String gpPhone = dbHelper.getIndividualGPDetails(index, phoneList, dbHelper.KEY_GP_PHONE_NUMBER);

            if (gpProfileURL.length() == 0) {
                // If user does not have a profile photo, display a default image instead
                profilePhotoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_picture);
            } else {

                String accessPhotoURL = "http://www.gptalk.ie/" + gpProfileURL;

                try {
                    // Convert stored photo url into bitmap image
                    InputStream inputStream = new java.net.URL(accessPhotoURL).openStream();
                    profilePhotoBitmap = BitmapFactory.decodeStream(inputStream);
                } catch (Exception e) {
                    baseClass.errorHandler(e);
                }
            }

            // Store retrieved details that are to be displayed in custom listview layout
            GPHistoryItem item = new GPHistoryItem(profilePhotoBitmap, gpFirstName, gpLastName);

            detailsList.add(item);
            profileURLList.add(gpProfileURL);
            firstNameList.add(gpFirstName);
            lastNameList.add(gpLastName);
            phoneList.add(gpPhone);
        }
    }

    private void updateList() {

        adapter = new CustomHistoryListViewAdapter(PatientFragmentHistory.this.getActivity(), R.layout.gp_history_listview_item, detailsList);
        lvGPDetails.setAdapter(adapter);
        lvGPDetails.setClickable(true);

        lvGPDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String profileID = profileURLList.get(i);
                String firstNameID = firstNameList.get(i);
                String lastNameID = lastNameList.get(i);
                String phoneID = phoneList.get(i);

//                // If a listview item is pressed, display a list of all previous consultation modifications
                PatientFragmentHistoryList patientHistoryList = new PatientFragmentHistoryList(profileID, firstNameID, lastNameID, phoneID);
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.fragmentScheduleHistoryGP, patientHistoryList);

                fragmentTransaction.commit();
            }
        });
    }

    public class RetrieveGPHistoryDetails extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;
        private PatientDatabaseHelper dbHelper;

        public RetrieveGPHistoryDetails(PatientDatabaseHelper dbHelper) {
            this.dbHelper = dbHelper;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(PatientFragmentHistory.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving History Details...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            retrieveIndividualGP(dbHelper);
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