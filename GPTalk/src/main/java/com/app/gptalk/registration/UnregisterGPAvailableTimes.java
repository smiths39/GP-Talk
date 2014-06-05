package com.app.gptalk.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.app.gptalk.base.BaseClass;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class UnregisterGPAvailableTimes extends AsyncTask<Void, Void, String> {

    private Activity activity;
    private ProgressDialog progressDialog;
    private String bookingDate, bookingTime, bookingStatus, bookingGPPhoneNumber;

    private BaseClass baseClass = new BaseClass();
    private HashMap<String, String> bookingTimeDetails = new HashMap<String, String>();

    public UnregisterGPAvailableTimes(Activity activity, HashMap<String, String> bookingTimeDetails) {

        this.activity = activity;
        this.bookingTimeDetails = bookingTimeDetails;
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(activity);
        baseClass.initialiseProgressDialog(progressDialog, "Resolving Booking Time Details...");
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {

        extractBookingDetails();
        storeBookingDetails();

        return "";
    }

    private void extractBookingDetails() {

        bookingDate = bookingTimeDetails.get("booking_date");
        bookingTime = bookingTimeDetails.get("booking_time");
        bookingStatus = bookingTimeDetails.get("booking_status");
        bookingGPPhoneNumber = bookingTimeDetails.get("gp_phone");
    }

    private void storeBookingDetails() {

        URI url = null;

        try {
            url = new URI("http://www.gptalk.ie/WAMP/gcm_server_php/delete_gp_available_time_details.php"
                    + "?booking_date=" + bookingDate
                    + "&booking_time=" + bookingTime
                    + "&booking_status=" + bookingStatus
                    + "&gp_phone=" + bookingGPPhoneNumber);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(url);

        try {
            httpClient.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        super.onPostExecute(result);
        progressDialog.dismiss();
    }
}

