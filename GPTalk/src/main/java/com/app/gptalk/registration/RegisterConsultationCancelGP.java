package com.app.gptalk.registration;

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

public class RegisterConsultationCancelGP extends AsyncTask<Void, Void, String> {

    private FragmentActivity activity;
    private ProgressDialog progressDialog;
    private String bookingReference, phoneNumber, bookingDate, bookingTime, status, explanation;

    private BaseClass baseClass = new BaseClass();
    private HashMap<String, String> consultationDetails = new HashMap<String, String>();

    public RegisterConsultationCancelGP(FragmentActivity activity, HashMap<String, String> consultationDetails) {

        this.activity = activity;
        this.consultationDetails = consultationDetails;
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(activity);
        baseClass.initialiseProgressDialog(progressDialog, "Storing Cancellation Details...");
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {

        extractConsultationDetails();
        storeConsultationDetails();

        return "";
    }

    private void extractConsultationDetails() {

        bookingReference = consultationDetails.get("booking_reference");
        phoneNumber = consultationDetails.get("phone_number");
        bookingDate = consultationDetails.get("booking_date");
        bookingTime = consultationDetails.get("booking_time");
        status = consultationDetails.get("status");
        explanation = consultationDetails.get("explanation");
    }

    private void storeConsultationDetails() {

        URI url = null;

        try {
            url = new URI("http://www.gptalk.ie/WAMP/gcm_server_php/store_consultation_cancel_gp.php"
                    + "?booking_reference=" + bookingReference
                    + "&phone_number=" + phoneNumber
                    + "&booking_date=" + bookingDate
                    + "&booking_time=" + bookingTime
                    + "&status=" + status
                    + "&explanation=" + explanation);
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

