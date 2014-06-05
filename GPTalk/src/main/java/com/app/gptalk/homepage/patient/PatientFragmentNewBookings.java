package com.app.gptalk.homepage.patient;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.gptalk.listview_getter.GPCountyItem;
import com.app.gptalk.custom_adapters.CustomCountyListViewAdapter;
import com.app.gptalk.base.JSONParser;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class PatientFragmentNewBookings extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tvCurrentLocation;
    private ListView lvCounties;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();
    private ArrayList<String> counties = new ArrayList<String>();

    private GPCountyItem item;
    private JSONArray retrievedCounties = null;

    private final static String POSTS = "posts";
    private final static String COUNTY = "medical_centre_county";
    private final static String ACCOUNT_URL = "http://www.gptalk.ie/gptalkie_registered_users/patient_account_details.php";
    private final static String REGISTERED_COUNTIES = "http://www.gptalk.ie/gptalkie_registered_users/registered_gp_counties.php";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getFragmentManager().popBackStack();
        
        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }

        view = inflater.inflate(R.layout.new_bookings, container, false);
        initialiseWidgets(view);

        try {
            new RetrieveCurrentCounty().execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }

        tvCurrentLocation.setOnClickListener(PatientFragmentNewBookings.this);

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

        tvCurrentLocation = (TextView) view.findViewById(R.id.tvCountyCurrentLocation);
        lvCounties = (ListView) view.findViewById(R.id.lvCountyGP);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tvCountyCurrentLocation:
                // Launch list of GPs in the county which the user resides in
                launchGPListFragment(tvCurrentLocation.getText().toString());
                break;
        }
    }

    public void launchGPListFragment(String selectedCounty) {

        PatientFragmentNewBookingsGPList newBookingsGPList = new PatientFragmentNewBookingsGPList(selectedCounty);
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .addToBackStack(null)
                .replace(R.id.fragmentBooking, newBookingsGPList);

        fragmentTransaction.commit();
    }

    public class RetrieveCurrentCounty extends AsyncTask<String, String, String> {

        private int success;
        private String currentLocation, patientUsername;
        private ProgressDialog progressDialog;

        private ArrayList<GPCountyItem> countiesList = new ArrayList<GPCountyItem>();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(PatientFragmentNewBookings.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving Current Location...");
        }

        @Override
        protected String doInBackground(String... args) {

            retrieveCurrentLocation();
            retrieveRegisteredGPLocations();

            return null;
        }

        private void retrieveCurrentLocation() {

            ArrayList<NameValuePair> currentCounty = new ArrayList<NameValuePair>();
            patientUsername = baseClass.retrieveUsername(null, PatientFragmentNewBookings.this.getActivity());

            // Retrieve the county of the currently logged in user
            currentCounty.add(new BasicNameValuePair("username", patientUsername));

            try {
                JSONObject json = jsonParser.makeHttpRequest(ACCOUNT_URL, "POST", currentCounty);
                success = json.getInt("success");
                if (success == 1) {
                    currentLocation = json.getString("county");
                }
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }
        }

        private void retrieveRegisteredGPLocations() {

            ArrayList<NameValuePair> countyCredentials = new ArrayList<NameValuePair>();

            // Retrieve all counties of currently registered GPs
            countyCredentials.add(new BasicNameValuePair("medical_centre_county", currentLocation));

            try {
                JSONObject json = jsonParser.makeHttpRequest(REGISTERED_COUNTIES, "POST", countyCredentials);
                retrievedCounties = json.getJSONArray(POSTS);

                for (int index = 0; index < retrievedCounties.length(); index++) {

                    JSONObject detailsObject = retrievedCounties.getJSONObject(index);
                    String county = detailsObject.getString(COUNTY);

                    item = new GPCountyItem(county);
                    countiesList.add(item);
                    counties.add(county);
                }
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }
        }

        @Override
        protected void onPostExecute(String fileURL) {

            super.onPostExecute(fileURL);
            progressDialog.dismiss();

            setClickListener();

            tvCurrentLocation.setText(currentLocation);
        }

        private void setClickListener() {

            CustomCountyListViewAdapter adapter = new CustomCountyListViewAdapter(PatientFragmentNewBookings.this.getActivity(), R.layout.gp_listview_county_item, countiesList);
            lvCounties.setAdapter(adapter);
            lvCounties.setClickable(true);

            lvCounties.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String selectedCounty = counties.get(position);
                    launchGPListFragment(selectedCounty);
                }
            });
        }
    }
}