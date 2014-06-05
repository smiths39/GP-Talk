package com.app.gptalk.homepage.patient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.base.JSONParser;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.database.PatientDatabaseHelper;
import com.app.gptalk.registration.RegisterConsultationReply;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PatientReceivedBookingReply extends Activity implements View.OnClickListener {

    private Button okButton;
    private Typeface titleFont, patientDetailsFont;
    private TextView tvBookingReplyTitle, tvMessage, tvStatus;

    public String referenceCode, status;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();

    private final static String BOOKING_DETAILS_URL = "http://www.gptalk.ie/WAMP/gcm_server_php/retrieve_booking_reply.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_received_booking_request);

        initialiseWidgets();
        initialiseButtons();

        setFontStyles();
        setFonts();

        // Retrieve reference code of the requested booking sent by the patient
        Intent referenceIntent = getIntent();
        referenceCode  = referenceIntent.getExtras().getString("appointmentReference").toString();

        try {
            new RetrieveBookingReplyDetails().execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    private void initialiseButtons() {

        okButton = (Button) findViewById(R.id.bOKReply);
        okButton.setOnClickListener(PatientReceivedBookingReply.this);
    }

    private void initialiseWidgets() {

        tvBookingReplyTitle = (TextView) findViewById(R.id.tvBookingReplyTitle);
        tvMessage = (TextView) findViewById(R.id.tvGPReplyMessage);
        tvStatus = (TextView) findViewById(R.id.tvGPReplyStatus);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(this.getAssets(), "Roboto-Thin.ttf");
        patientDetailsFont = Typeface.createFromAsset(this.getAssets(), "Sanseriffic.otf");
    }

    private void setFonts() {

        tvBookingReplyTitle.setTypeface(titleFont);
        tvMessage.setTypeface(patientDetailsFont);
        tvStatus.setTypeface(patientDetailsFont);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bOKReply:
                PatientReceivedBookingReply.this.finish();
                //android.os.Process.killProcess(android.os.Process.myPid());
                //moveTaskToBack(true);
                break;
        }
    }

    public class RetrieveBookingReplyDetails extends AsyncTask<String, String, String> {

        private int success;
        private ProgressDialog progressDialog;
        private String dbReferenceCode, dbStatus;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(PatientReceivedBookingReply.this);
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving Booking Details...");
        }

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> bookingCredentials = new ArrayList<NameValuePair>();
            bookingCredentials.add(new BasicNameValuePair("booking_reference", PatientReceivedBookingReply.this.referenceCode));

            try {
                JSONObject json = jsonParser.makeHttpRequest(BOOKING_DETAILS_URL, "POST", bookingCredentials);
                success = json.getInt("success");

                if (success == 1) {
                    retrieveJSONValues(json);
                }
                return null;
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }

            return null;
        }

        private void retrieveJSONValues(JSONObject json) {

            try {
                dbReferenceCode = json.getString("reference_code");
                dbStatus = json.getString("status");
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }
        }

        protected void onPostExecute(String fileURL) {

            progressDialog.dismiss();

            setStatus(dbStatus);

            PatientDatabaseHelper dbHelper = new PatientDatabaseHelper(PatientReceivedBookingReply.this);

            try {

                // Automatically insert consultation as unconfirmed into internal database
                if (dbHelper.consultationExists(dbReferenceCode)) {
                    dbHelper.updateStatus(dbStatus, dbReferenceCode);
                }

                dbHelper.close();
            } catch (Exception e) {
                baseClass.errorHandler(e);
            }
        }

        private void setStatus(String dbStatus) {

            tvStatus.setText(dbStatus);

            if (dbStatus.equals("confirmed")) {
                tvStatus.setBackgroundResource(R.drawable.status_confirmed);
            } else if (dbStatus.equals("rejected")) {
                tvStatus.setBackgroundResource(R.drawable.status_rejected);
            }
        }
    }
}
