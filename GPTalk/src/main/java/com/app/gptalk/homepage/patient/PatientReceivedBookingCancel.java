package com.app.gptalk.homepage.patient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.base.JSONParser;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.database.PatientDatabaseHelper;
import com.app.gptalk.registration.RegisterConsultationReply;
import com.app.gptalk.registration.UnregisterGPAvailableTimes;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class PatientReceivedBookingCancel extends Activity implements View.OnClickListener {

    private Button okButton;
    private Typeface titleFont, titleDetailsFont, patientDetailsFont;
    private TextView tvBookingDeleteTitle, tvBookingDateTitle, tvBookingTimeTitle, tvExplanationTitle,
            tvGPName, tvBookingDate, tvBookingTime, tvExplanation;

    public String referenceCode, name, status, phoneNumber, explanation,
            bookingDate, bookingTime, dbBookingDate, dbBookingTime, dbStatus, dbName, dbPhoneNumber, dbExplanation;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();

    private final static String BOOKING_DETAILS_URL = "http://www.gptalk.ie/WAMP/gcm_server_php/retrieve_gp_cancel_details.php";
    private final static String GP_DETAILS_URL = "http://www.gptalk.ie/gptalkie_registered_users/registered_gp_username.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_received_booking_cancel);

        initialiseTitleVariables();
        initialiseInputVariables();
        initialiseButtons();

        setFontStyles();
        setTitleFont();
        setInputFont();

        // Retrieve reference code of the requested booking sent by the patient
        Intent referenceIntent = getIntent();
        referenceCode  = referenceIntent.getExtras().getString("appointmentReference").toString();

        try {
            new RetrieveNewChangeDetails().execute();
            new RetrieveGPUsername().execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    private void initialiseButtons() {

        okButton = (Button) findViewById(R.id.bDeleteGPOK);
        okButton.setOnClickListener(PatientReceivedBookingCancel.this);
    }

    private void initialiseTitleVariables() {

        tvBookingDeleteTitle = (TextView) findViewById(R.id.tvDeleteBookingGPTitle);
        tvBookingDateTitle = (TextView) findViewById(R.id.tvDeleteGPBookingDateTitle);
        tvBookingTimeTitle = (TextView) findViewById(R.id.tvDeleteGPBookingTimeTitle);
        tvExplanationTitle = (TextView) findViewById(R.id.tvDeleteGPExplanationTitle);
    }

    private void initialiseInputVariables() {

        tvGPName = (TextView) findViewById(R.id.tvDeletePatientCompleteName);
        tvBookingDate = (TextView) findViewById(R.id.tvDeleteGPBookingDate);
        tvBookingTime = (TextView) findViewById(R.id.tvDeleteGPBookingTime);
        tvExplanation = (TextView) findViewById(R.id.tvDeleteGPExplanation);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(this.getAssets(), "Roboto-Thin.ttf");
        titleDetailsFont = Typeface.createFromAsset(this.getAssets(), "RobotoCondensed-Bold.ttf");
        patientDetailsFont = Typeface.createFromAsset(this.getAssets(), "Sanseriffic.otf");
    }

    private void setTitleFont() {

        tvBookingDeleteTitle.setTypeface(titleFont);
        tvBookingDateTitle.setTypeface(titleDetailsFont);
        tvBookingTimeTitle.setTypeface(titleDetailsFont);
        tvExplanationTitle.setTypeface(titleDetailsFont);
    }

    private void setInputFont() {

        tvGPName.setTypeface(patientDetailsFont);
        tvBookingDate.setTypeface(patientDetailsFont);
        tvBookingTime.setTypeface(patientDetailsFont);
        tvExplanation.setTypeface(patientDetailsFont);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bDeleteGPOK:
                PatientDatabaseHelper dbHelper = new PatientDatabaseHelper(PatientReceivedBookingCancel.this);

                try {
                    dbHelper.deleteEntry(dbPhoneNumber);
                    PatientReceivedBookingCancel.this.finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                } catch (Exception e) {
                    baseClass.errorHandler(e);
                }

                break;
        }
    }

    public class RetrieveNewChangeDetails extends AsyncTask<String, String, String> {

        private int success;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(PatientReceivedBookingCancel.this);
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving Booking Details...");
        }

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> bookingCredentials = new ArrayList<NameValuePair>();
            bookingCredentials.add(new BasicNameValuePair("booking_reference", PatientReceivedBookingCancel.this.referenceCode));

            try {
                JSONObject json = jsonParser.makeHttpRequest(BOOKING_DETAILS_URL, "POST", bookingCredentials);
                success = json.getInt("success");

                if (success == 1) {

                    retrieveJSONValues(json);
                    return null;
                } else {
                    return null;
                }
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }

            return null;
        }

        private void retrieveJSONValues(JSONObject json) {

            try {
                dbPhoneNumber = json.getString("phone_number");
                dbBookingDate = json.getString("booking_date");
                dbBookingTime = json.getString("booking_time");
                dbStatus = json.getString("status");
                dbExplanation = json.getString("explanation");
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }
        }

        protected void onPostExecute(String fileURL) {

            progressDialog.dismiss();
            setBookingDetails();
            setDetailsValues();
        }

        private void setBookingDetails() {

            tvBookingDate.setText(dbBookingDate);
            tvBookingTime.setText(dbBookingTime);
            tvExplanation.setText(dbExplanation);
        }

        public void setDetailsValues() {

            phoneNumber = dbPhoneNumber;
            bookingDate = dbBookingDate;
            bookingTime = dbBookingTime;
            status = dbStatus;
            explanation = dbExplanation;
        }
    }

    private class RetrieveGPUsername extends AsyncTask<String, String, String> {

        private int success;

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> bookingCredentials = new ArrayList<NameValuePair>();
            bookingCredentials.add(new BasicNameValuePair("phone_number", PatientReceivedBookingCancel.this.phoneNumber));

            try {
                JSONObject json = jsonParser.makeHttpRequest(GP_DETAILS_URL, "POST", bookingCredentials);
                success = json.getInt("success");

                if (success == 1) {

                    dbName = json.getString("name");
                    return null;
                } else {
                    return null;
                }
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }

            return null;
        }

        protected void onPostExecute(String fileURL) {

            tvGPName.setText(dbName);
        }

    }
}
