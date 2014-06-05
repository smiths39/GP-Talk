package com.app.gptalk.homepage.both;

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
import android.widget.Toast;

import com.app.gptalk.custom_adapters.CustomAvailableTimeListViewAdapter;
import com.app.gptalk.custom_adapters.CustomWebsiteListViewAdapter;
import com.app.gptalk.homepage.gp.GPFragmentScheduleDetails;
import com.app.gptalk.homepage.patient.PatientFragmentGPSessionsDetails;
import com.app.gptalk.homepage.patient.PatientFragmentGPWebsites;
import com.app.gptalk.homepage.patient.PatientFragmentGPWebsitesBrowser;
import com.app.gptalk.homepage.patient.PatientFragmentNewBookingsDetails;
import com.app.gptalk.listview_getter.GPAvailableTimeItem;
import com.app.gptalk.listview_getter.GPWebsiteItem;
import com.app.gptalk.base.JSONParser;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentGPAvailableTimes extends Fragment {

    private View view;
    private ListView lvGPTimes;
    private ArrayList<GPAvailableTimeItem> timesList;

    private JSONArray retrievedDetails = null;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();

    private ArrayList<String> listedTimes = new ArrayList<String>();
    private ArrayList<String> completeUnavailableTimesList = new ArrayList<String>();
    private ArrayList<String> completePendingTimesList = new ArrayList<String>();

    private ArrayList<String> completeStatusList = new ArrayList<String>();

    private HashMap<String, String> makeBookingDetails = new HashMap<String, String>();

    private String bookingTime, bookingDate, bookingStatus, gpPhone, originalClass, email;
    private CustomAvailableTimeListViewAdapter adapter;

    private final static String POSTS = "posts";
    private final static String BOOKING_TIME = "bookingTime";
    private final static String GP_AVAILABLE_TIMES_DETAILS = "http://www.gptalk.ie/gptalkie_registered_users/gp_available_timeslot.php";

    public FragmentGPAvailableTimes(String originalClass, String phone, String date) {

        this.originalClass = originalClass;
        this.gpPhone = phone;
        this.bookingDate = date;
    }

    public FragmentGPAvailableTimes(String originalClass, String phone, String date, HashMap<String, String> makeBookingDetails) {

        this.originalClass = originalClass;
        this.gpPhone = phone;
        this.bookingDate = date;
        this.makeBookingDetails = makeBookingDetails;
    }

    public FragmentGPAvailableTimes(String originalClass, String phone, String date, String email) {

        this.originalClass = originalClass;
        this.gpPhone = phone;
        this.bookingDate = date;
        this.email = email;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.gp_available_times, container, false);
        lvGPTimes = (ListView) view.findViewById(R.id.lvAvailableTimeGP);

        try {
            new RetrieveTimes().execute();
        } catch (Exception e) {
            Toast.makeText(FragmentGPAvailableTimes.this.getActivity(), "Error occurred", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void generateTimeList() {

        timesList = new ArrayList<GPAvailableTimeItem>();
        GPAvailableTimeItem item = null;

        for (int hourIndex = 9; hourIndex <= 18; hourIndex++) {

            for (int minuteIndex = 0; minuteIndex < 60; minuteIndex += 15) {

                String timeFormat = getTimeFormat(hourIndex, minuteIndex);
                String statusInput = "";

                if (!completeUnavailableTimesList.contains(timeFormat) && completePendingTimesList.contains(timeFormat))  {
                    // If there is a booking request has already been made on this time, but is not confirmed

                    statusInput = "Pending";
                    item = new GPAvailableTimeItem(timeFormat, statusInput);
                } else if (completeUnavailableTimesList.contains(timeFormat)) {
                    // If there is currently a booking made at this time

                    statusInput = "Unavailable";
                    item = new GPAvailableTimeItem(timeFormat, statusInput);
                } else if (!completeUnavailableTimesList.contains(timeFormat) || completeUnavailableTimesList.isEmpty()
                           || !completePendingTimesList.contains(timeFormat) || completePendingTimesList.isEmpty()) {

                    statusInput = "Available";
                    item = new GPAvailableTimeItem(timeFormat, statusInput);
                }

                completeStatusList.add(statusInput);
                timesList.add(item);
                listedTimes.add(timeFormat);
            }
        }
    }

    // Convert hour and time to correct time format
    private String getTimeFormat(int hour, int minute) {

        String fullHour = (hour <= 9) ? "0" + hour : "" + hour;
        String fullMinute = (minute == 0) ? "0" + minute : "" + minute;

        return fullHour + ":" + fullMinute;
    }

    public void updateJSONDataUnavailableTimes() {

        int success = 0;

        ArrayList<NameValuePair> timesCredentials = new ArrayList<NameValuePair>();

        // Retrieve details of each GP based on current date and confirmed status
        timesCredentials.add(new BasicNameValuePair("bookingDate", this.bookingDate));
        timesCredentials.add(new BasicNameValuePair("bookingStatus", baseClass.confirmedStatus));
        timesCredentials.add(new BasicNameValuePair("gpPhoneNumber", this.gpPhone));

        try {
            JSONObject json = jsonParser.makeHttpRequest(GP_AVAILABLE_TIMES_DETAILS, "POST", timesCredentials);
            success = json.getInt("success");

            if (success == 1) {

                retrievedDetails = json.getJSONArray(POSTS);

                for (int index = 0; index < retrievedDetails.length(); index++) {

                    JSONObject detailsObject = retrievedDetails.getJSONObject(index);
                    bookingTime = detailsObject.getString(BOOKING_TIME);

                    // Store each unavailable time retrieved
                    completeUnavailableTimesList.add(bookingTime);
                }
            } else {
                completeUnavailableTimesList.clear();
            }
        } catch (JSONException e) {
            baseClass.errorHandler(e);
        }
    }

    public void updateJSONDataPendingTimes() {

        int success = 0;

        ArrayList<NameValuePair> timesCredentials = new ArrayList<NameValuePair>();

        // Retrieve details of each GP based on current date and confirmed status
        timesCredentials.add(new BasicNameValuePair("bookingDate", this.bookingDate));
        timesCredentials.add(new BasicNameValuePair("bookingStatus", baseClass.unconfirmedStatus));
        timesCredentials.add(new BasicNameValuePair("gpPhoneNumber", this.gpPhone));

        try {
            JSONObject json = jsonParser.makeHttpRequest(GP_AVAILABLE_TIMES_DETAILS, "POST", timesCredentials);
            success = json.getInt("success");

            if (success == 1) {

                retrievedDetails = json.getJSONArray(POSTS);

                for (int index = 0; index < retrievedDetails.length(); index++) {

                    JSONObject detailsObject = retrievedDetails.getJSONObject(index);
                    bookingTime = detailsObject.getString(BOOKING_TIME);

                    // Store each unavailable time retrieved
                    completePendingTimesList.add(bookingTime);
                }
            } else {
                completePendingTimesList.clear();
            }
        } catch (JSONException e) {
            baseClass.errorHandler(e);
        }
    }
    
    private void updateList() {

        // Add retrieved details to customised times listview
        adapter = new CustomAvailableTimeListViewAdapter(this.getActivity(), R.layout.gp_available_time_listview_item, timesList);

        lvGPTimes.setAdapter(adapter);
        lvGPTimes.setClickable(true);
        lvGPTimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedTime = listedTimes.get(i);
                String selectedStatus = completeStatusList.get(i);

                if (selectedStatus.equals("Unavailable")) {
                    Toast.makeText(FragmentGPAvailableTimes.this.getActivity(), "Chosen time is unavailable", Toast.LENGTH_SHORT).show();
                } else if (selectedStatus.equals("Pending")) {
                    Toast.makeText(FragmentGPAvailableTimes.this.getActivity(), "The status of this time is awaiting confirmation", Toast.LENGTH_SHORT).show();
                } else if (originalClass.equals("PatientFragmentNewBookingsDetails")) {

                    PatientFragmentNewBookingsDetails gpSessionsDetails = new PatientFragmentNewBookingsDetails(makeBookingDetails, selectedTime, bookingDate);

                    FragmentManager fragmentManager = getFragmentManager();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .addToBackStack(null)
                            .replace(R.id.fragmentAvailableGPTimes, gpSessionsDetails);

                    fragmentTransaction.commit();
                }
            }
        });
    }

    public class RetrieveTimes extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(FragmentGPAvailableTimes.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving GP Times...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            updateJSONDataUnavailableTimes();
            updateJSONDataPendingTimes();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            super.onPostExecute(result);
            progressDialog.dismiss();

            generateTimeList();
            updateList();
        }
    }
}