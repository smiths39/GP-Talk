package com.app.gptalk.homepage.patient;

import android.app.ProgressDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.gptalk.custom_adapters.CustomAvailableGPListViewAdapter;
import com.app.gptalk.listview_getter.GPAvailableItem;
import com.app.gptalk.base.JSONParser;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class PatientFragmentNewBookingsGPList extends Fragment {

    private View view;
    private String selectedCounty;
    private TextView tvSelectedCountyTitle;
    private ListView lvGPDetails;
    private ArrayList<GPAvailableItem> detailsList;
    private ArrayList<String> addresses = new ArrayList<String>();
    private CustomAvailableGPListViewAdapter adapter;

    private Bitmap profilePhotoBitmap = null;
    private JSONArray retrievedDetails = null;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();

    private String profile, title, firstName, lastName, address, city;

    private final static String POSTS = "posts";
    private final static String PROFILE = "profile";
    private final static String TITLE = "title";
    private final static String FIRST_NAME = "first_name";
    private final static String LAST_NAME = "last_name";
    private final static String ADDRESS = "medical_centre_address";
    private final static String CITY = "medical_centre_city";
    private final static String RETRIEVE_GP_DETAILS = "http://www.gptalk.ie/gptalkie_registered_users/registered_gp_details.php";

    public PatientFragmentNewBookingsGPList(String newSelectedCounty) {

        this.selectedCounty = newSelectedCounty;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }

        view = inflater.inflate(R.layout.new_bookings_gp_list, container, false);
        setHasOptionsMenu(true);
        initialiseWidgets(view);

        try {
            new RetrieveGPDetails().execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }

        return view;
    }

    private void initialiseWidgets(View view) {

        lvGPDetails = (ListView) view.findViewById(R.id.lvGPDetails);
        tvSelectedCountyTitle = (TextView) view.findViewById(R.id.tvSelectedCountyTitle);
        tvSelectedCountyTitle.setText(this.selectedCounty);
    }

    public void updateJSONData() {

        detailsList = new ArrayList<GPAvailableItem>();

        ArrayList<NameValuePair> countyCredentials = new ArrayList<NameValuePair>();

        // Retrieve details of all GPs who reside in the selected county
        countyCredentials.add(new BasicNameValuePair("medical_centre_county", selectedCounty));

        try {

            JSONObject json = jsonParser.makeHttpRequest(RETRIEVE_GP_DETAILS, "POST", countyCredentials);
            retrievedDetails = json.getJSONArray(POSTS);

            for (int index = 0; index < retrievedDetails.length(); index++) {

                JSONObject detailsObject = retrievedDetails.getJSONObject(index);
                profile = detailsObject.getString(PROFILE);
                title = detailsObject.getString(TITLE);
                firstName = detailsObject.getString(FIRST_NAME);
                lastName = detailsObject.getString(LAST_NAME);
                address = detailsObject.getString(ADDRESS) +  ",";
                city = detailsObject.getString(CITY);

                if (profile.length() == 0) {
                    // If user does not have a profile photo, display a default image instead
                    profilePhotoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_picture);
                } else {
                    String accessPhotoURL = "http://www.gptalk.ie/" + profile;

                    try {
                        // Convert stored photo url into bitmap image
                        InputStream inputStream = new java.net.URL(accessPhotoURL).openStream();
                        profilePhotoBitmap = BitmapFactory.decodeStream(inputStream);
                    } catch (Exception e) {
                        baseClass.errorHandler(e);
                    }
                }

                GPAvailableItem item = new GPAvailableItem(profilePhotoBitmap, title, firstName, lastName, address, city);
                detailsList.add(item);

                // Store addresses for Google Map coordinates of GP location in next page
                address = address.substring(0, address.length()-1);
                addresses.add(address);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateList() {

        adapter = new CustomAvailableGPListViewAdapter(this.getActivity(), R.layout.gp_listview_item, detailsList);

        lvGPDetails.setAdapter(adapter);
        lvGPDetails.setClickable(true);
        lvGPDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String sendAddress = addresses.get(i);

                PatientFragmentNewBookingsGPProfile newBookingsGPProfile = new PatientFragmentNewBookingsGPProfile(sendAddress);
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.fragmentBookingGPList, newBookingsGPProfile);

                fragmentTransaction.commit();
            }
        });
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

    public class RetrieveGPDetails extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(PatientFragmentNewBookingsGPList.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving Current Location...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            updateJSONData();
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