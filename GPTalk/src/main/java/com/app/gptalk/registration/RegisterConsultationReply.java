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

public class RegisterConsultationReply extends AsyncTask<Void, Void, String> {

    private Activity activity;
    private ProgressDialog progressDialog;
    private String bookingReference, bookingStatus;

    private BaseClass baseClass = new BaseClass();
    private HashMap<String, String> replyDetails = new HashMap<String, String>();

    public RegisterConsultationReply(Activity activity, HashMap<String, String> replyDetails) {

        this.activity = activity;
        this.replyDetails = replyDetails;
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(activity);
        baseClass.initialiseProgressDialog(progressDialog, "Storing Consultation Reply Details...");
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {

        extractConsultationDetails();
        storeConsultationDetails();

        return "";
    }

    private void extractConsultationDetails() {

        bookingReference = replyDetails.get("reference_code");
        bookingStatus = replyDetails.get("status");
    }

    private void storeConsultationDetails() {

        URI url = null;

        try {
            url = new URI("http://www.gptalk.ie/WAMP/gcm_server_php/store_consultation_reply_details.php"
                    + "?booking_reference=" + bookingReference
                    + "&booking_status=" + bookingStatus);
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

