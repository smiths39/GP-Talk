package com.app.gptalk.homepage.gp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.base.JSONParser;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.homepage.both.FragmentGPAvailableTimes;
import com.app.gptalk.homepage.patient.PatientFragmentGPSessionsDetails;
import com.app.gptalk.registration.RegisterConsultationCancelGP;
import com.app.gptalk.registration.RegisterConsultationCancelPatient;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class GPFragmentScheduleDetails extends Fragment implements View.OnClickListener {

    private Dialog dialog;
    private ImageView ivProfilePhoto;
    private Bitmap profileBitmap = null;
    private Button cancelConsultationButton, bSend, bCancel;
    private Typeface titleFont, titleDetailsFont, patientDetailsFont;
    private EditText etExplanation;
    private TextView tvPatientConsultationTitle, tvMainSymptomTitle,
            tvOtherSymptomsTitle, tvMainAllergyTitle, tvOtherAllergiesTitle, tvBookingDateTitle, tvBookingTimeTitle, tvStatusTitle,
            tvFirstName, tvLastName, tvMainSymptom, tvOtherSymptoms, tvMainAllergy, tvOtherAllergies, tvBookingDate, tvBookingTime, tvStatus,
            tvDialogStatus, tvDialogDate, tvDialogTime;

    private HashMap<String, String> consultationDetails = new HashMap<String, String>();
    private HashMap<String, String> bookingTimeDetails = new HashMap<String, String>();

    private String appointmentReference = "gp" + generateRandomAppointmentReference();
    private String status, email, bookingDate, bookingTime, dbMedicalCentrePhoneNumber, patientUsername;
    private BaseClass baseClass = new BaseClass();

    private JSONParser jsonParser = new JSONParser();

    private final static String GP_ACCOUNT_URL = "http://www.gptalk.ie/gptalkie_registered_users/gp_account_details.php";
    private final static String SELECTED_PATIENT_DETAILS_URL = "http://www.gptalk.ie/gptalkie_registered_users/registered_patient_username.php";

    public GPFragmentScheduleDetails(String status, String email, String bookingDate, String bookingTime){

        this.status = status;
        this.email = email;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gp_schedule_consultation_details, container, false);

        initialiseTitleWidgets(view);
        initialiseInputWidgets(view);
        initialiseButtons(view);

        setFontStyles();
        setTitleFont();
        setInputFont();

        new RetrieveGPDetails().execute();
        new RetrievePatientUsername().execute();
        retrievePatientConsultationDetails();

        return view;
    }

    private void retrievePatientConsultationDetails() {

        GPDatabaseHelper dbHelper = new GPDatabaseHelper(GPFragmentScheduleDetails.this.getActivity());

        // Retrieve individual details of a booking consultation, made by a patient on a specific date
        String patientProfileURL = dbHelper.getScheduleConsultationInformation(email, status, bookingDate, dbHelper.KEY_PROFILE);
        String patientFirstName = dbHelper.getScheduleConsultationInformation(email, status, bookingDate, dbHelper.KEY_FIRST_NAME);
        String patientLastName = dbHelper.getScheduleConsultationInformation(email, status, bookingDate, dbHelper.KEY_LAST_NAME);
        String patientSymptom = dbHelper.getScheduleConsultationInformation(email, status, bookingDate, dbHelper.KEY_SYMPTOM);
        String patientOtherSymptoms = dbHelper.getScheduleConsultationInformation(email, status, bookingDate, dbHelper.KEY_OTHER_SYMPTOMS);
        String patientAllergy = dbHelper.getScheduleConsultationInformation(email, status, bookingDate, dbHelper.KEY_ALLERGY);
        String patientOtherAllergies = dbHelper.getScheduleConsultationInformation(email, status, bookingDate, dbHelper.KEY_OTHER_ALLERGIES);
        String patientBookingTime = dbHelper.getScheduleConsultationInformation(email, status, bookingDate, dbHelper.KEY_BOOKING_TIME);

        dbHelper.close();

        tvFirstName.setText(patientFirstName);
        tvLastName.setText(patientLastName);
        tvMainSymptom.setText(patientSymptom);
        tvOtherSymptoms.setText(patientOtherSymptoms);
        tvMainAllergy.setText(patientAllergy);
        tvOtherAllergies.setText(patientOtherAllergies);
        tvBookingDate.setText(bookingDate);

        if (bookingTime.equals("0")) {
            tvBookingTime.setText(patientBookingTime);
        } else {
            tvBookingTime.setText(bookingTime);
        }

        tvStatus.setText(status);

        // If no input was provided when request was made, set text as N/A
        checkDetailExists(tvOtherSymptoms);
        checkDetailExists(tvOtherAllergies);

        // Underline text in textview to indicate that it is clickable
        underlineTextView(tvBookingDate);
        underlineTextView(tvBookingTime);
        underlineTextView(tvStatus);

        new RetrieveIndividualPatientScheduleDetails(patientProfileURL).execute();
    }

    private void underlineTextView(TextView textview) {

        textview.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    // Check if value exists for this column
    private void checkDetailExists(TextView textview) {

        if(textview == null || textview.getText().equals("") || textview.getText().equals(" ")) {
            textview.setText("N/A");
        }
    }

    private void initialiseButtons(View view) {

        cancelConsultationButton = (Button) view.findViewById(R.id.bScheduleCancelConsultation);
        cancelConsultationButton.setOnClickListener(GPFragmentScheduleDetails.this);

        if (status.equals(baseClass.rejectedStatus) || status.equals(baseClass.unconfirmedStatus)) {
            cancelConsultationButton.setVisibility(View.GONE);
        }

        tvStatus.setOnClickListener(GPFragmentScheduleDetails.this);
        tvBookingDate.setOnClickListener(GPFragmentScheduleDetails.this);
        tvBookingTime.setOnClickListener(GPFragmentScheduleDetails.this);
    }

    private void initialiseTitleWidgets(View view) {

        tvPatientConsultationTitle = (TextView)  view.findViewById(R.id.tvPatientConsultationTitle);
        tvMainSymptomTitle = (TextView) view.findViewById(R.id.tvPatientConsultationSymptomTitle);
        tvOtherSymptomsTitle = (TextView) view.findViewById(R.id.tvPatientConsultationOtherSymptomsTitle);
        tvMainAllergyTitle = (TextView) view.findViewById(R.id.tvPatientConsultationAllergyTitle);
        tvOtherAllergiesTitle = (TextView) view.findViewById(R.id.tvPatientConsultationOtherAllergiesTitle);
        tvBookingDateTitle = (TextView) view.findViewById(R.id.tvPatientConsultationBookingDateTitle);
        tvBookingTimeTitle = (TextView) view.findViewById(R.id.tvPatientConsultationBookingTimeTitle);
        tvStatusTitle = (TextView) view.findViewById(R.id.tvPatientConsultationStatusTitle);

        ivProfilePhoto = (ImageView) view.findViewById(R.id.ivRequestPatientConsultationProfile);
    }

    private void initialiseInputWidgets(View view) {

        tvFirstName = (TextView) view.findViewById(R.id.tvPatientConsultationFirstName);
        tvLastName = (TextView) view.findViewById(R.id.tvPatientConsultationLastName);
        tvMainSymptom = (TextView) view.findViewById(R.id.tvPatientConsultationSymptom);
        tvOtherSymptoms = (TextView) view.findViewById(R.id.tvPatientConsultationOtherSymptoms);
        tvMainAllergy = (TextView) view.findViewById(R.id.tvPatientConsultationAllergy);
        tvOtherAllergies = (TextView) view.findViewById(R.id.tvPatientConsultationOtherAllergies);
        tvBookingDate = (TextView) view.findViewById(R.id.tvPatientConsultationBookingDate);
        tvBookingTime = (TextView) view.findViewById(R.id.tvPatientConsultationBookingTime);
        tvStatus = (TextView) view.findViewById(R.id.tvPatientConsultationStatus);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(GPFragmentScheduleDetails.this.getActivity().getAssets(), "Roboto-Thin.ttf");
        titleDetailsFont = Typeface.createFromAsset(GPFragmentScheduleDetails.this.getActivity().getAssets(), "RobotoCondensed-Bold.ttf");
        patientDetailsFont = Typeface.createFromAsset(GPFragmentScheduleDetails.this.getActivity().getAssets(), "Sanseriffic.otf");
    }

    private void setTitleFont() {

        tvPatientConsultationTitle.setTypeface(titleFont);
        tvMainSymptomTitle.setTypeface(titleDetailsFont);
        tvOtherSymptomsTitle.setTypeface(titleDetailsFont);
        tvMainAllergyTitle.setTypeface(titleDetailsFont);
        tvOtherAllergiesTitle.setTypeface(titleDetailsFont);
        tvBookingDateTitle.setTypeface(titleDetailsFont);
        tvBookingTimeTitle.setTypeface(titleDetailsFont);
        tvStatusTitle.setTypeface(titleDetailsFont);
    }

    private void setInputFont() {

        tvFirstName.setTypeface(patientDetailsFont);
        tvLastName.setTypeface(patientDetailsFont);
        tvMainSymptom.setTypeface(patientDetailsFont);
        tvOtherSymptoms.setTypeface(patientDetailsFont);
        tvMainAllergy.setTypeface(patientDetailsFont);
        tvOtherAllergies.setTypeface(patientDetailsFont);
        tvBookingDate.setTypeface(patientDetailsFont);
        tvBookingTime.setTypeface(patientDetailsFont);
        tvStatus.setTypeface(patientDetailsFont);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnSendCancellation:

                if (etExplanation.getText().toString().isEmpty()) {
                    Toast.makeText(GPFragmentScheduleDetails.this.getActivity(), "Please provide an explanation for cancellation", Toast.LENGTH_SHORT).show();
                } else {
                    retrieveConsultationInformation();
                    storeBookingTimeDetails(baseClass.rejectedStatus);

                    try {

                        GPDatabaseHelper dbHelper = new GPDatabaseHelper(GPFragmentScheduleDetails.this.getActivity());

                        // Send a push notification to the GP with the consultation reference code attached
                        new NotificationSender().execute();

                        // Delete record of consultation
                        dbHelper.deleteEntry(email);
                        dbHelper.close();
                    } catch (Exception e) {
                        baseClass.errorHandler(e);
                    }

                    dialog.dismiss();

                    String gpUsername = baseClass.retrieveUsername(null, GPFragmentScheduleDetails.this.getActivity());

                    Intent gpHomepageIntent = new Intent("com.app.gptalk.homepage.gp.GPHOMEPAGE");
                    gpHomepageIntent.putExtra("username", gpUsername);
                    startActivity(gpHomepageIntent);
                }

                break;

            case R.id.btnCancelCancellation:
                dialog.dismiss();
                break;

            case R.id.bScheduleCancelConsultation:
                showCustomDialog();
                break;
        }
    }

    private void storeBookingTimeDetails(String status) {

        bookingTimeDetails.put("booking_date", tvBookingDate.getText().toString());
        bookingTimeDetails.put("booking_time", tvBookingTime.getText().toString());
        bookingTimeDetails.put("booking_status", status);
        bookingTimeDetails.put("gp_phone", dbMedicalCentrePhoneNumber);

        // Store all details of the requested consultation in a remote database, which will be used for retrieval purposes
        new RegisterGPAvailableTimes(GPFragmentScheduleDetails.this.getActivity(), bookingTimeDetails).execute();
    }

    private void sendNotification(String appointmentReference) {

        URI url = null;

        try {
            // PHP script stored in the remote server transfers the push notification to the patient
            // The patient device is discovered via their device registration ID, as generated during registration
            url = new URI("http://www.gptalk.ie/WAMP/gcm_server_php/send_notification.php?username=" + patientUsername + "&user_type=patient&reference_number=" + appointmentReference);
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

    private void retrieveConsultationInformation() {

        consultationDetails.put("booking_reference", appointmentReference);
        consultationDetails.put("phone_number", dbMedicalCentrePhoneNumber);
        consultationDetails.put("booking_date", tvBookingDate.getText().toString());
        consultationDetails.put("booking_time", tvBookingTime.getText().toString());
        consultationDetails.put("status", baseClass.confirmedStatus);
        consultationDetails.put("explanation", reformatWhiteSpace(etExplanation.getText().toString()));

        try {
            new RegisterConsultationCancelGP(GPFragmentScheduleDetails.this.getActivity(), consultationDetails).execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    // Replace all spaces in a string with '+'
    private String reformatWhiteSpace(String sentence) {

        return sentence.replaceAll(" ", "%20");
    }

    private String generateRandomAppointmentReference() {

        Random random = new Random();

        // Exclude 'p' for notification differences to distinguish difference between reply and request
        String alphabet = "abdefghijklmnoqrstuvwxyz";

        char[] text = new char[15];

        // generate a randomised string to be used as a reference code
        for (int index = 0; index < text.length; index++) {

            text[index] = alphabet.charAt(random.nextInt(alphabet.length()));
        }

        return new String(text);
    }

    protected void showCustomDialog() {

        dialog = new Dialog(GPFragmentScheduleDetails.this.getActivity(), android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);
        dialog.setContentView(R.layout.gp_schedule_custom_dialog);

        tvDialogDate = (TextView) dialog.findViewById(R.id.tvCustomDialogBookingDate);
        tvDialogTime = (TextView) dialog.findViewById(R.id.tvCustomDialogBookingTime);
        tvDialogStatus = (TextView) dialog.findViewById(R.id.tvCustomDialogStatus);
        etExplanation = (EditText) dialog.findViewById(R.id.etCustomDialogExplanation);

        bSend = (Button) dialog.findViewById(R.id.btnSendCancellation);
        bCancel = (Button) dialog.findViewById(R.id.btnCancelCancellation);

        bSend.setOnClickListener(this);
        bCancel.setOnClickListener(this);

        tvDialogStatus.setText(tvStatus.getText());
        tvDialogDate.setText(tvBookingDate.getText());
        tvDialogTime.setText(tvBookingTime.getText());

        dialog.show();
    }

    public class NotificationSender extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {
                sendNotification(appointmentReference);
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

    public class RetrieveIndividualPatientScheduleDetails extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;
        private String profileURL;

        public RetrieveIndividualPatientScheduleDetails(String patientProfileURL) {

            profileURL = patientProfileURL;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(GPFragmentScheduleDetails.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving Schedule Details...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                if (profileURL.length() == 0) {
                    // If user does not have a profile photo, display a default image instead
                    profileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_picture);
                } else {

                    String accessPhotoURL = "http://www.gptalk.ie/" + profileURL;

                    try {
                        // Convert stored photo url into bitmap image
                        InputStream inputStream = new java.net.URL(accessPhotoURL).openStream();
                        profileBitmap = BitmapFactory.decodeStream(inputStream);
                    } catch (Exception e) {
                        baseClass.errorHandler(e);
                    }
                }
            } catch (Exception e) {
                baseClass.errorHandler(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            super.onPostExecute(result);
            progressDialog.dismiss();

            ivProfilePhoto.setImageBitmap(profileBitmap);
        }
    }

    public class RetrieveGPDetails extends AsyncTask<String, String, String> {

        private int success;
        private String dbUsername;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> gpCredentials = new ArrayList<NameValuePair>();

            dbUsername = baseClass.retrieveUsername(null, GPFragmentScheduleDetails.this.getActivity());
            gpCredentials.add(new BasicNameValuePair("username", dbUsername));

            try {
                JSONObject json = jsonParser.makeHttpRequest(GP_ACCOUNT_URL, "POST", gpCredentials);
                success = json.getInt("success");

                if (success == 1) {

                    try {
                        dbMedicalCentrePhoneNumber = json.getString("medical_centre_phone_number");
                    } catch (JSONException e) {
                        baseClass.errorHandler(e);
                    }
                }
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }

            return null;
        }
    }
    private class RetrievePatientUsername extends AsyncTask<String, String, String> {

        private int loginSuccess;
        private String dbPatientUsername;

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> patientCredentials = new ArrayList<NameValuePair>();

            // Retrieve all patient's details via their specified unique address
            patientCredentials.add(new BasicNameValuePair("email", email));

            try {
                JSONObject json = jsonParser.makeHttpRequest(SELECTED_PATIENT_DETAILS_URL, "POST", patientCredentials);
                loginSuccess = json.getInt("success");

                if (loginSuccess == 1) {

                    dbPatientUsername = json.getString("username");
                    return null;
                } else {
                    return null;
                }
            } catch (Exception e) {
                baseClass.errorHandler(e);
            }

            return null;
        }

        protected void onPostExecute(String fileURL) {

            patientUsername = dbPatientUsername;
        }
    }

}