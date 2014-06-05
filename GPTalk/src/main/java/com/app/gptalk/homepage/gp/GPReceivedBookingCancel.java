package com.app.gptalk.homepage.gp;

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

public class GPReceivedBookingCancel extends Activity implements View.OnClickListener {

    private Button okButton;
    private Typeface titleFont, titleDetailsFont, patientDetailsFont;
    private TextView tvBookingChangeTitle, tvBookingDateTitle, tvBookingTimeTitle, tvExplanationTitle,
            tvPatientName, tvBookingDate, tvBookingTime, tvExplanation;

    public String referenceCode, name, email, status, phoneNumber, explanation,
            bookingDate, bookingTime, dbBookingDate, dbBookingTime, dbStatus, dbName, dbEmail, dbPhoneNumber, dbExplanation;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();

    private HashMap<String, String> bookingTimeDetails = new HashMap<String, String>();

    private final static String BOOKING_DETAILS_URL = "http://www.gptalk.ie/WAMP/gcm_server_php/retrieve_cancel_details.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gp_received_booking_cancel);

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
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    private void initialiseButtons() {

        okButton = (Button) findViewById(R.id.bOK);
        okButton.setOnClickListener(GPReceivedBookingCancel.this);
    }

    private void initialiseTitleVariables() {

        tvBookingChangeTitle = (TextView) findViewById(R.id.tvNewBookingChangeTitle);
        tvBookingDateTitle = (TextView) findViewById(R.id.tvChangePatientBookingDateTitle);
        tvBookingTimeTitle = (TextView) findViewById(R.id.tvChangePatientBookingTimeTitle);
        tvExplanationTitle = (TextView) findViewById(R.id.tvChangePatientExplanationTitle);
    }

    private void initialiseInputVariables() {

        tvPatientName = (TextView) findViewById(R.id.tvChangePatientCompleteName);
        tvBookingDate = (TextView) findViewById(R.id.tvChangePatientBookingDate);
        tvBookingTime = (TextView) findViewById(R.id.tvChangePatientBookingTime);
        tvExplanation = (TextView) findViewById(R.id.tvChangePatientExplanation);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(this.getAssets(), "Roboto-Thin.ttf");
        titleDetailsFont = Typeface.createFromAsset(this.getAssets(), "RobotoCondensed-Bold.ttf");
        patientDetailsFont = Typeface.createFromAsset(this.getAssets(), "Sanseriffic.otf");
    }

    private void setTitleFont() {

        tvBookingChangeTitle.setTypeface(titleFont);
        tvBookingDateTitle.setTypeface(titleDetailsFont);
        tvBookingTimeTitle.setTypeface(titleDetailsFont);
        tvExplanationTitle.setTypeface(titleDetailsFont);
    }

    private void setInputFont() {

        tvPatientName.setTypeface(patientDetailsFont);
        tvBookingDate.setTypeface(patientDetailsFont);
        tvBookingTime.setTypeface(patientDetailsFont);
        tvExplanation.setTypeface(patientDetailsFont);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bOK:
                removeBookingTimeDetails();

                GPDatabaseHelper dbHelper = new GPDatabaseHelper(GPReceivedBookingCancel.this);

                try {
                    dbHelper.deleteEntry(dbEmail);
                    GPReceivedBookingCancel.this.finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                } catch (Exception e) {
                    baseClass.errorHandler(e);
                }
                break;
        }
    }


    private void removeBookingTimeDetails() {

        bookingTimeDetails.put("booking_date", bookingDate);
        bookingTimeDetails.put("booking_time", bookingTime);
        bookingTimeDetails.put("booking_status", baseClass.confirmedStatus);
        bookingTimeDetails.put("gp_phone", phoneNumber);

        // Store all details of the requested consultation in a remote database, which will be used for retrieval purposes
        new UnregisterGPAvailableTimes(GPReceivedBookingCancel.this, bookingTimeDetails).execute();
    }

    public class RetrieveNewChangeDetails extends AsyncTask<String, String, String> {

        private int success;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(GPReceivedBookingCancel.this);
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving Booking Details...");
        }

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> bookingCredentials = new ArrayList<NameValuePair>();
            bookingCredentials.add(new BasicNameValuePair("booking_reference", GPReceivedBookingCancel.this.referenceCode));

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
                dbName = json.getString("name");
                dbEmail = json.getString("email");
                dbBookingDate = json.getString("booking_date");
                dbBookingTime = json.getString("booking_time");
                dbStatus = json.getString("status");
                dbPhoneNumber = json.getString("phone_number");
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

            tvPatientName.setText(dbName);
            tvBookingDate.setText(dbBookingDate);
            tvBookingTime.setText(dbBookingTime);
            tvExplanation.setText(dbExplanation);
        }

        public void setDetailsValues() {

            name = dbName;
            email = dbEmail;
            bookingDate = dbBookingDate;
            bookingTime = dbBookingTime;
            status = dbStatus;
            phoneNumber = dbPhoneNumber;
            explanation = dbExplanation;
        }
    }
}
