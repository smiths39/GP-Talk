package com.app.gptalk.homepage.gp;

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
import android.widget.Toast;

import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.base.JSONParser;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.registration.RegisterConsultationReply;
import com.app.gptalk.registration.RegisterGPAvailableTimes;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class GPReceivedBookingRequest extends Activity implements View.OnClickListener {

    private ImageView ivProfilePhoto;
    private Button confirmButton, rejectButton;
    private Typeface titleFont, titleDetailsFont, patientDetailsFont;
    private TextView tvBookingRequestTitle, tvMainSymptomTitle, tvOtherSymptomsTitle, tvMainAllergyTitle,
            tvOtherAllergiesTitle, tvBookingDateTitle, tvBookingTimeTitle,
            tvPatientName, tvDOB, tvGender, tvNationality,
            tvMainSymptom, tvOtherSymptoms, tvMainAllergy, tvOtherAllergies, tvBookingDate, tvBookingTime;

    public String referenceCode, username, firstName, lastName, bookingDate, bookingTime, email, dbUsername, dbProfileURL, dbFirstName, dbLastName,
            dbDOBDay, dbDOBMonth, dbDOBYear, dbGender, dbNationality, dbEmail, dbSymptom, dbOtherSymptoms, dbAllergies, dbOtherAllergies, dbBookingDate, dbBookingTime, gpPhoneNumber;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();

    private final String UNCONFIRMED = baseClass.unconfirmedStatus;
    private final String CONFIRMED = baseClass.confirmedStatus;
    private final String REJECTED = baseClass.rejectedStatus;

    private final static String BOOKING_DETAILS_URL = "http://www.gptalk.ie/WAMP/gcm_server_php/retrieve_booking_details.php";

    private HashMap<String, String> appointmentDetails = new HashMap<String, String>();
    private HashMap<String, String> replyDetails = new HashMap<String, String>();
    private HashMap<String, String> bookingTimeDetails = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gp_received_booking_request);

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
            new RetrieveNewBookingDetails().execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
   }

    private void initialiseButtons() {

        confirmButton = (Button) findViewById(R.id.bConfirmRequest);
        rejectButton =  (Button) findViewById(R.id.bRejectRequest);

        confirmButton.setOnClickListener(GPReceivedBookingRequest.this);
        rejectButton.setOnClickListener(GPReceivedBookingRequest.this);
    }

    private void initialiseTitleVariables() {

        tvBookingRequestTitle = (TextView) findViewById(R.id.tvNewBookingRequestTitle);
        tvMainSymptomTitle = (TextView) findViewById(R.id.tvRequestPatientSymptomTitle);
        tvOtherSymptomsTitle = (TextView) findViewById(R.id.tvRequestPatientOtherSymptomsTitle);
        tvMainAllergyTitle = (TextView) findViewById(R.id.tvRequestPatientAllergyTitle);
        tvOtherAllergiesTitle = (TextView) findViewById(R.id.tvRequestPatientOtherAllergiesTitle);
        tvBookingDateTitle = (TextView) findViewById(R.id.tvRequestPatientBookingDateTitle);
        tvBookingTimeTitle = (TextView) findViewById(R.id.tvRequestPatientBookingTimeTitle);

        ivProfilePhoto = (ImageView) findViewById(R.id.ivRequestPatientProfilePhoto);
    }

    private void initialiseInputVariables() {

        tvPatientName = (TextView) findViewById(R.id.tvRequestPatientCompleteName);
        tvDOB = (TextView) findViewById(R.id.tvRequestPatientDOB);
        tvGender = (TextView) findViewById(R.id.tvRequestPatientGender);
        tvNationality = (TextView) findViewById(R.id.tvRequestPatientNationality);
        tvMainSymptom = (TextView) findViewById(R.id.tvRequestPatientSymptom);
        tvOtherSymptoms = (TextView) findViewById(R.id.tvRequestPatientOtherSymptoms);
        tvMainAllergy = (TextView) findViewById(R.id.tvRequestPatientAllergy);
        tvOtherAllergies = (TextView) findViewById(R.id.tvRequestPatientOtherAllergies);
        tvBookingDate = (TextView) findViewById(R.id.tvRequestPatientBookingDate);
        tvBookingTime = (TextView) findViewById(R.id.tvRequestPatientBookingTime);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(this.getAssets(), "Roboto-Thin.ttf");
        titleDetailsFont = Typeface.createFromAsset(this.getAssets(), "RobotoCondensed-Bold.ttf");
        patientDetailsFont = Typeface.createFromAsset(this.getAssets(), "Sanseriffic.otf");
    }

    private void setTitleFont() {

        tvBookingRequestTitle.setTypeface(titleFont);
        tvMainSymptomTitle.setTypeface(titleDetailsFont);
        tvOtherSymptomsTitle.setTypeface(titleDetailsFont);
        tvMainAllergyTitle.setTypeface(titleDetailsFont);
        tvOtherAllergiesTitle.setTypeface(titleDetailsFont);
        tvBookingDateTitle.setTypeface(titleDetailsFont);
        tvBookingTimeTitle.setTypeface(titleDetailsFont);
    }

    private void setInputFont() {

        tvPatientName.setTypeface(patientDetailsFont);
        tvDOB.setTypeface(patientDetailsFont);
        tvGender.setTypeface(patientDetailsFont);
        tvNationality.setTypeface(patientDetailsFont);
        tvMainSymptom.setTypeface(patientDetailsFont);
        tvOtherSymptoms.setTypeface(patientDetailsFont);
        tvMainAllergy.setTypeface(patientDetailsFont);
        tvOtherAllergies.setTypeface(patientDetailsFont);
        tvBookingDate.setTypeface(patientDetailsFont);
        tvBookingTime.setTypeface(patientDetailsFont);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bConfirmRequest:

                AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(GPReceivedBookingRequest.this);
                confirmDialogBuilder.setTitle("Confirm Appointment")
                        .setMessage("Are you sure you want to accept this appointment?")
                        .setCancelable(false)
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                try {
                                    replyConsultationDetails(CONFIRMED);

                                    // Store booking times associated with GP, for all users to see
                                    storeBookingTimeDetails(baseClass.confirmedStatus);

                                    GPDatabaseHelper dbHelper = new GPDatabaseHelper(GPReceivedBookingRequest.this);

                                    // Update consultation details to confirmed status
                                    if (dbHelper.consultationExists(email)) {
                                        dbHelper.updateStatus(CONFIRMED, email);

                                        // Send a push notification reply to the patient with the consultation reference code reattached
                                        new notificationSender(username).execute();
                                    }

                                    dbHelper.close();
                                    GPReceivedBookingRequest.this.finish();
                                } catch (Exception e) {
                                    baseClass.errorHandler(e);
                                }
//                                moveTaskToBack(true);
                            }
                        })
                        .create()
                        .show();


                break;

            case R.id.bRejectRequest:

                AlertDialog.Builder rejectDialogBuilder = new AlertDialog.Builder(GPReceivedBookingRequest.this);
                rejectDialogBuilder.setTitle("Reject Appointment")
                        .setMessage("Are you sure you want to reject this appointment?")
                        .setCancelable(false)
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                try {
                                    replyConsultationDetails(REJECTED);
                                    storeBookingTimeDetails(baseClass.rejectedStatus);

                                    GPDatabaseHelper dbHelper = new GPDatabaseHelper(GPReceivedBookingRequest.this);

                                    // Update consultation details to rejected status
                                    if (dbHelper.consultationExists(email)) {
                                        dbHelper.updateStatus(REJECTED, email);

                                        // Send a push notification reply to the patient with the consultation reference code reattached
                                        new notificationSender(username).execute();
                                    }

                                    dbHelper.close();
                                } catch (Exception e) {
                                    baseClass.errorHandler(e);
                                }
                                //moveTaskToBack(true);
                            }
                        })
                        .create()
                        .show();
                break;
        }
    }

    private void storeBookingTimeDetails(String status) {

        bookingTimeDetails.put("booking_date", bookingDate);
        bookingTimeDetails.put("booking_time", bookingTime);
        bookingTimeDetails.put("booking_status", status);
        bookingTimeDetails.put("gp_phone", gpPhoneNumber);

        // Store all details of the requested consultation in a remote database, which will be used for retrieval purposes
        new RegisterGPAvailableTimes(GPReceivedBookingRequest.this, bookingTimeDetails).execute();
    }

    private void replyConsultationDetails(String replyStatus) {

        replyDetails.put("reference_code", referenceCode);
        replyDetails.put("status", replyStatus);

        // Store all details of the requested consultation in a remote database, which will be used for retrieval purposes
        new RegisterConsultationReply(GPReceivedBookingRequest.this, replyDetails).execute();
    }

    private void sendNotification(String patientUsername, String appointmentReference) {

        URI url = null;

        try {
            // PHP script stored in the remote server tranfers the push notification to the GP
            // The GP's device is discovered via their device registration ID, as generated during registration
            // 'p' is attached to beginning of reference code to indicate difference between patient and gp
            url = new URI("http://www.gptalk.ie/WAMP/gcm_server_php/send_notification.php?username=" + patientUsername + "&user_type=patient&reference_number=p" + appointmentReference);
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

    public class RetrieveNewBookingDetails extends AsyncTask<String, String, String> {

        private int success;
        private ProgressDialog progressDialog;
        private Bitmap profilePhotoBitmap = null;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(GPReceivedBookingRequest.this);
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving Booking Details...");
        }

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> bookingCredentials = new ArrayList<NameValuePair>();
            bookingCredentials.add(new BasicNameValuePair("booking_reference", GPReceivedBookingRequest.this.referenceCode));

            try {
                JSONObject json = jsonParser.makeHttpRequest(BOOKING_DETAILS_URL, "POST", bookingCredentials);
                success = json.getInt("success");

                if (success == 1) {

                    retrieveJSONValues(json);
                    String accessPhotoURL = "http://www.gptalk.ie/" + dbProfileURL;

                    if (dbProfileURL.length() == 0) {
                        // If user does not have a profile photo, display a default image instead
                        profilePhotoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_picture);
                    } else {
                        try {
                            InputStream inputStream = new java.net.URL(accessPhotoURL).openStream();
                            profilePhotoBitmap = BitmapFactory.decodeStream(inputStream);
                        } catch (Exception e) {
                            baseClass.errorHandler(e);
                        }
                    }

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
                dbUsername = json.getString("patient_username");
                dbProfileURL = json.getString("patient_profile");
                dbFirstName = json.getString("patient_first_name");
                dbLastName = json.getString("patient_last_name");

                // If day or month value is below length of 10, add an additional 0 as a prefix
                dbDOBDay = json.getString("patient_dob_day");
                if (dbDOBDay.toString().length() <= 1) {
                    dbDOBDay = '0' + dbDOBDay;
                }

                dbDOBMonth = json.getString("patient_dob_month");
                if (dbDOBMonth.toString().length() <= 1) {
                    dbDOBMonth = '0' + dbDOBMonth;
                }

                dbDOBYear = json.getString("patient_dob_year");
                dbGender = json.getString("patient_gender");
                dbNationality = json.getString("patient_nationality");
                dbEmail = json.getString("patient_email");
                dbSymptom = json.getString("patient_symptom");
                dbOtherSymptoms = json.getString("patient_other_symptoms");
                dbAllergies = json.getString("patient_allergies");
                dbOtherAllergies = json.getString("patient_other_allergies");
                dbBookingDate = json.getString("booking_date");
                dbBookingTime = json.getString("booking_time");
                gpPhoneNumber = json.getString("gp_phone_number");
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }
        }

        protected void onPostExecute(String fileURL) {

            progressDialog.dismiss();
            setBookingDetails();
            setDetailsValues();
            insertUnconfirmedBookingDetails();

            GPDatabaseHelper dbHelper = new GPDatabaseHelper(GPReceivedBookingRequest.this);

            try {

                // Automatically insert consultation as unconfirmed into internal database
                if (!dbHelper.consultationExists(dbEmail)) {
                    dbHelper.createAppointment(appointmentDetails);
                }

                dbHelper.close();
            } catch (Exception e) {
                baseClass.errorHandler(e);
            }
        }

        private void setBookingDetails() {

            ivProfilePhoto.setImageBitmap(profilePhotoBitmap);
            tvPatientName.setText(dbFirstName + " " + dbLastName);
            tvDOB.setText(dbDOBDay + " / " + dbDOBMonth + " / " + dbDOBYear);
            tvGender.setText(dbGender);
            tvNationality.setText(dbNationality);
            tvMainSymptom.setText(dbSymptom);

            if (dbOtherSymptoms.length() == 0) {
                tvOtherSymptoms.setText("N/A");
            } else {
                tvOtherSymptoms.setText(dbOtherSymptoms);
            }

            tvMainAllergy.setText(dbAllergies);

            if (dbOtherAllergies.length() == 0) {
                tvOtherAllergies.setText("N/A");
            } else {
                tvOtherAllergies.setText(dbOtherAllergies);
            }
            tvBookingDate.setText(dbBookingDate);
            tvBookingTime.setText(dbBookingTime);
        }

        public void setDetailsValues() {

            username = dbUsername;
            firstName = dbFirstName;
            lastName = dbLastName;
            bookingDate = dbBookingDate;
            bookingTime = dbBookingTime;
            email = dbEmail;
        }

        // Insert booking details into hashtable, to be used for populating database
        private void insertUnconfirmedBookingDetails() {

            appointmentDetails.put("status", UNCONFIRMED);
            appointmentDetails.put("username", dbUsername);
            appointmentDetails.put("profile", dbProfileURL);
            appointmentDetails.put("first_name", dbFirstName);
            appointmentDetails.put("last_name", dbLastName);
            appointmentDetails.put("day", dbDOBDay);
            appointmentDetails.put("month", dbDOBMonth);
            appointmentDetails.put("year", dbDOBYear);
            appointmentDetails.put("gender", dbGender);
            appointmentDetails.put("nationality", dbNationality);
            appointmentDetails.put("email", dbEmail);
            appointmentDetails.put("main_symptom", dbSymptom);
            appointmentDetails.put("other_symptoms", dbOtherSymptoms);
            appointmentDetails.put("main_allergy", dbAllergies);
            appointmentDetails.put("other_allergies", dbOtherAllergies);
            appointmentDetails.put("booking_date", dbBookingDate);
            appointmentDetails.put("booking_time", dbBookingTime);
            appointmentDetails.put("explanation", " ");
            appointmentDetails.put("update_counter", "1");
        }
    }

    public class notificationSender extends AsyncTask<Void, Void, String> {

        String username;

        public notificationSender(String username) {

            this.username = username;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                sendNotification(username, referenceCode);
            } catch (Exception e) {
                baseClass.errorHandler(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
