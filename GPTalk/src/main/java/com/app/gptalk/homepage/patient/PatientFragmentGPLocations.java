package com.app.gptalk.homepage.patient;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.base.JSONParser;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PatientFragmentGPLocations extends Fragment {

    private View view;
    private GoogleMap map;
    private double latitude, longitude;
    private String profilePhoto, title, firstName, lastName, medical_centre_address, medical_centre_city, medical_centre_county, medical_centre_post_code;

    private JSONArray retrievedDetails = null;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();
    private ArrayList<String> markers = new ArrayList<String>();
    private ArrayList<String> gpProfilePhoto = new ArrayList<String>();
    private ArrayList<String> gpTitle = new ArrayList<String>();
    private ArrayList<String> gpFirstName = new ArrayList<String>();
    private ArrayList<String> gpLastName = new ArrayList<String>();
    private ArrayList<String> gpAddress = new ArrayList<String>();
    private ArrayList<String> gpCity = new ArrayList<String>();
    private ArrayList<String> gpCounty = new ArrayList<String>();
    private ArrayList<String> gpPostCode = new ArrayList<String>();

    private final static String POSTS = "posts";
    private final static String PROFILE = "profile";
    private final static String TITLE = "title";
    private final static String FIRST_NAME = "first_name";
    private final static String LAST_NAME = "last_name";
    private final static String ADDRESS = "medical_centre_address";
    private final static String CITY = "medical_centre_city";
    private final static String COUNTY = "medical_centre_county";
    private final static String POST_CODE = "medical_centre_post_code";
    private final static String GP_LOCATION_DETAILS = "http://www.gptalk.ie/gptalkie_registered_users/registered_gp_locations.php";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getFragmentManager().popBackStack();

        view = inflater.inflate(R.layout.gp_locations, container, false);

        try {
            new RetrieveLocationDetails().execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }

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

    public void updateJSONData() {

        ArrayList<NameValuePair> allDetails = new ArrayList<NameValuePair>();
        allDetails.add(new BasicNameValuePair("title", TITLE));

        try {
            // Retrieve all details of each GP for each marker displayed on map
            JSONObject json = jsonParser.makeHttpRequest(GP_LOCATION_DETAILS, "POST", allDetails);
            retrievedDetails = json.getJSONArray(POSTS);

            for (int index = 0; index < retrievedDetails.length(); index++) {

                JSONObject detailsObject = retrievedDetails.getJSONObject(index);

                profilePhoto = detailsObject.getString(PROFILE);
                title = detailsObject.getString(TITLE);
                firstName = detailsObject.getString(FIRST_NAME);
                lastName = detailsObject.getString(LAST_NAME);
                medical_centre_address = detailsObject.getString(ADDRESS);
                medical_centre_city = detailsObject.getString(CITY);
                medical_centre_county = detailsObject.getString(COUNTY);
                medical_centre_post_code = detailsObject.getString(POST_CODE);

                gpProfilePhoto.add(profilePhoto);
                gpTitle.add(title);
                gpFirstName.add(firstName);
                gpLastName.add(lastName);
                gpAddress.add(medical_centre_address);
                gpCity.add(medical_centre_city);
                gpCounty.add(medical_centre_county);
                gpPostCode.add(medical_centre_post_code);
            }
        } catch (JSONException e) {
            baseClass.errorHandler(e);
        }
    }

    public void initialiseMap() {

        try {
            // Initialise Google Map in fragment
            MapsInitializer.initialize(getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            baseClass.errorHandler(e);
        }

        map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.gp_location_map)).getMap();
        map.setMyLocationEnabled(true);

        setMapCoordinates();
    }

    private void setMapCoordinates() {

        Geocoder geocoder = new Geocoder(this.getActivity());
        List<Address> addresses;

        int counter = 0;

        // Retrieve latitude and longitude of each registered GP
        // The GPs address is translated into latitude and longitude coordinates
        for (String addressLocation : gpAddress) {

            try {
                addresses = geocoder.getFromLocationName(addressLocation, 1);

                if (addresses.size() > 0) {
                    latitude = addresses.get(0).getLatitude();
                    longitude = addresses.get(0).getLongitude();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            setMapMarker(latitude, longitude, counter);
            counter++;
        }
    }

    private void setMapMarker(double latitude, double longitude, int counter) {

        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(gpTitle.get(counter) + " " + gpFirstName.get(counter) + " " + gpLastName.get(counter))
                .snippet(gpAddress.get(counter) + ",\n" + gpCity.get(counter) + ",\n" + gpCounty.get(counter) + " " + gpPostCode.get(counter)));
    }

    public void onDestroyView() {

        super.onDestroyView();

        try {
            Fragment fragment = (getFragmentManager().findFragmentById(R.id.gp_location_map));
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class RetrieveLocationDetails extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(PatientFragmentGPLocations.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving GP Locations");
        }

        @Override
        protected Boolean doInBackground(Void... strings) {

            updateJSONData();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            super.onPostExecute(result);
            progressDialog.dismiss();
            initialiseMap();
        }
    }
}