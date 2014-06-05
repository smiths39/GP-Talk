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

import com.app.gptalk.custom_adapters.CustomWebsiteListViewAdapter;
import com.app.gptalk.listview_getter.GPWebsiteItem;
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

public class PatientFragmentGPWebsites extends Fragment {

    private View view;
    private ListView lvGPWebsites;
    private ArrayList<GPWebsiteItem> websitesList;

    private JSONArray retrievedDetails = null;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();
    private ArrayList<String> websites = new ArrayList<String>();

    private String county, title, firstName, lastName, website;

    private final static String POSTS = "posts";
    private final static String COUNTY = "medical_centre_county";
    private final static String TITLE = "title";
    private final static String FIRST_NAME = "first_name";
    private final static String LAST_NAME = "last_name";
    private final static String WEBSITE = "medical_centre_website";
    private final static String GP_WEBSITE_DETAILS = "http://www.gptalk.ie/gptalkie_registered_users/registered_gp_websites.php";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        view = inflater.inflate(R.layout.website_bookmarks, container, false);

        lvGPWebsites = (ListView) view.findViewById(R.id.lvWebsiteGP);

        try {
            new RetrieveWebsites().execute();
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

        websitesList = new ArrayList<GPWebsiteItem>();

        ArrayList<NameValuePair> countyCredentials = new ArrayList<NameValuePair>();

        // Retrieve details of each GP based on county location
        countyCredentials.add(new BasicNameValuePair("medical_centre_county", COUNTY));

        try {
            JSONObject json = jsonParser.makeHttpRequest(GP_WEBSITE_DETAILS, "POST", countyCredentials);
            retrievedDetails = json.getJSONArray(POSTS);

            for (int index = 0; index < retrievedDetails.length(); index++) {

                JSONObject detailsObject = retrievedDetails.getJSONObject(index);
                county = detailsObject.getString(COUNTY);
                title = detailsObject.getString(TITLE);
                firstName = detailsObject.getString(FIRST_NAME);
                lastName = detailsObject.getString(LAST_NAME);
                website = detailsObject.getString(WEBSITE);

                GPWebsiteItem item = new GPWebsiteItem(county, title, firstName, lastName);
                websitesList.add(item);

                // Store url of each GP website
                websites.add("http://" + website);
            }
        } catch (JSONException e) {
            baseClass.errorHandler(e);
        }
    }

    private void updateList() {

        // Add retrieved details to customised website listview
        CustomWebsiteListViewAdapter adapter = new CustomWebsiteListViewAdapter(this.getActivity(), R.layout.gp_website_listview_item, websitesList);

        lvGPWebsites.setAdapter(adapter);
        lvGPWebsites.setClickable(true);
        lvGPWebsites.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String launchWebsite = websites.get(i);

                // Launch web view with selected GP website
                PatientFragmentGPWebsitesBrowser gpWebsitesBrowser = new PatientFragmentGPWebsitesBrowser(launchWebsite);
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.fragmentWebsiteBookmarks, gpWebsitesBrowser);

                fragmentTransaction.commit();
            }
        });
    }

    public class RetrieveWebsites extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(PatientFragmentGPWebsites.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving GP Websites...");
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
            updateList();
        }
    }
}