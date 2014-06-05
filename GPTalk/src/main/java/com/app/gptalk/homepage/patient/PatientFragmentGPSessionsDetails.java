package com.app.gptalk.homepage.patient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.base.JSONParser;
import com.app.gptalk.database.PatientDatabaseHelper;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PatientFragmentGPSessionsDetails extends Fragment implements View.OnClickListener, LocationListener {

    private View view;
    private Dialog dialog;
    private ImageView ivProfilePhoto;
    private Bitmap profileBitmap = null;
    private Button cancelConsultationButton, directionMapButton, bSend, bCancel;
    private Typeface titleFont, titleDetailsFont, patientDetailsFont;
    private EditText etExplanation;
    private TextView tvPatientConsultationTitle, tvAddressTitle, tvBookingDateTitle, tvBookingTimeTitle, tvStatusTitle,
            tvName, tvAddress, tvBookingDate, tvBookingTime, tvStatus,
            tvDialogStatus, tvDialogDate, tvDialogTime;

    private JSONParser jsonParser = new JSONParser();
    private HashMap<String, String> bookingTimeDetails = new HashMap<String, String>();

    private LocationManager locationManager;
    private Location location;
    private double latitude, longitude;

    // A flag for GPS status
    boolean GPSActiveStatus  = false;

    // The minimum distance to change coordinate location updates in meters
    private static final long MINIMUM_DISTANCE_FOR_UPDATES = 10;

    // The minimum time between updates in milliseconds (1 minute)
    private static final long MINIMUM_TIME_FOR_UPDATES = 1000 * 60 * 1;

    private String appointmentReference = "pa" + generateRandomAppointmentReference();
    private String status, phone, email, bookingDate, bookingTime, patientUsername, gpUsername, gpPhoneNumber, gpAddress;
    private BaseClass baseClass = new BaseClass();
    private HashMap<String, String> consultationDetails = new HashMap<String, String>();

    private static final String GP_AVAILABLE_TIME = "http://www.gptalk.ie/WAMP/gcm_server_php/delete_gp_available_time_details.php";
    private static final String SELECTED_GP_DETAILS_URL = "http://www.gptalk.ie/gptalkie_registered_users/selected_gp_details.php";
    private static final String PATIENT_ACCOUNT_URL = "http://www.gptalk.ie/gptalkie_registered_users/patient_account_details.php";

    public PatientFragmentGPSessionsDetails(String status, String phone, String bookingDate, String bookingTime){

        this.status = status;
        this.phone = phone;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }

        view = inflater.inflate(R.layout.gp_session_consultation_details, container, false);

        initialiseTitleWidgets(view);
        initialiseInputWidgets(view);
        initialiseButtons(view);

        setFontStyles();
        setTitleFont();
        setInputFont();

        patientUsername = baseClass.retrieveUsername(null, PatientFragmentGPSessionsDetails.this.getActivity());

        retrieveSessionConsultationDetails();
        getCurrentDeviceLocation();

        new RetrieveGPUsername().execute();
        new RetrievePatientEmail().execute();

        return view;
    }


    public Location getCurrentDeviceLocation() {

            locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

            // Getting GPS status
            GPSActiveStatus  = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSActive()) {

                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_FOR_UPDATES, MINIMUM_DISTANCE_FOR_UPDATES, this);

                    if (locationManager != null) {

                        // Retrieve last known coordinates of GPS location
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        return location;
    }

    // Return enabled/disabled status of GPS
    public boolean isGPSActive() {

        return this.GPSActiveStatus;
    }

    private void retrieveSessionConsultationDetails() {

        PatientDatabaseHelper dbHelper = new PatientDatabaseHelper(PatientFragmentGPSessionsDetails.this.getActivity());

        // Retrieve individual details of a booking consultation, made by the patient on a specific date
        String gpProfile = dbHelper.getScheduleConsultationInformation(phone, status, bookingDate, dbHelper.KEY_GP_PROFILE);
        String gpTitle = dbHelper.getScheduleConsultationInformation(phone, status, bookingDate, dbHelper.KEY_GP_TITLE);
        String gpFirstName = dbHelper.getScheduleConsultationInformation(phone, status, bookingDate, dbHelper.KEY_GP_FIRST_NAME);
        String gpLastName = dbHelper.getScheduleConsultationInformation(phone, status, bookingDate, dbHelper.KEY_GP_LAST_NAME);
        gpAddress = dbHelper.getScheduleConsultationInformation(phone, status, bookingDate, dbHelper.KEY_GP_ADDRESS);
        String gpCity = dbHelper.getScheduleConsultationInformation(phone, status, bookingDate, dbHelper.KEY_GP_CITY);
        String gpCounty = dbHelper.getScheduleConsultationInformation(phone, status, bookingDate, dbHelper.KEY_GP_COUNTY);
        String gpPostCode = dbHelper.getScheduleConsultationInformation(phone, status, bookingDate, dbHelper.KEY_GP_POST_CODE);
        String gpBookingTime = dbHelper.getScheduleConsultationInformation(phone, status, bookingDate, dbHelper.KEY_BOOKING_TIME);

        dbHelper.close();

        tvName.setText(gpTitle + " " + " " + gpFirstName + " "  + gpLastName);
        tvAddress.setText(gpAddress + " " + gpCity + " " + gpCounty + " " + gpPostCode);
        tvBookingDate.setText(bookingDate);

        // If GP's time remains as in database or new value selected
        if (bookingTime.equals("0")) {
            tvBookingTime.setText(gpBookingTime);
        } else {
            tvBookingTime.setText(bookingTime);
        }

        tvStatus.setText(status);

        // Underline text in textview to indicate that it is clickable
        underlineTextView(tvBookingDate);
        underlineTextView(tvBookingTime);
        underlineTextView(tvStatus);

        new RetrieveGPProfileImage(gpProfile).execute();
    }

    // Underline texts to signify that they are clickable
    private void underlineTextView(TextView textview) {

        textview.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    private void initialiseButtons(View view) {

        cancelConsultationButton = (Button) view.findViewById(R.id.bSessionCancelConsultation);
        cancelConsultationButton.setOnClickListener(PatientFragmentGPSessionsDetails.this);

        directionMapButton = (Button) view.findViewById(R.id.bSessionDirectionMap);
        directionMapButton.setOnClickListener(PatientFragmentGPSessionsDetails.this);

        if (status.equals(baseClass.rejectedStatus)) {
            cancelConsultationButton.setVisibility(View.GONE);
            directionMapButton.setVisibility(View.GONE);
        }

        if (status.equals(baseClass.unconfirmedStatus)) {
            cancelConsultationButton.setVisibility(View.GONE);

            // center remaining direction button in screen
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            params.weight = 1.0f;
            params.gravity= Gravity.TOP;

            directionMapButton.setLayoutParams(params);
        }
    }

    private void initialiseTitleWidgets(View view) {

        tvPatientConsultationTitle = (TextView) view.findViewById(R.id.tvGPSessionPageTitle);
        tvAddressTitle = (TextView) view.findViewById(R.id.tvGPSessionAddressTitle);
        tvBookingDateTitle = (TextView) view.findViewById(R.id.tvGPSessionBookingDateTitle);
        tvBookingTimeTitle = (TextView) view.findViewById(R.id.tvGPSessionBookingTimeTitle);
        tvStatusTitle = (TextView) view.findViewById(R.id.tvGPSessionStatusTitle);

        ivProfilePhoto = (ImageView) view.findViewById(R.id.ivRequestGPSessionProfile);
    }

    private void initialiseInputWidgets(View view) {

        tvName = (TextView) view.findViewById(R.id.tvGPSessionName);
        tvAddress = (TextView) view.findViewById(R.id.tvGPSessionAddress);
        tvBookingDate = (TextView) view.findViewById(R.id.tvGPSessionBookingDate);
        tvBookingTime = (TextView) view.findViewById(R.id.tvGPSessionBookingTime);
        tvStatus = (TextView) view.findViewById(R.id.tvGPSessionStatus);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(PatientFragmentGPSessionsDetails.this.getActivity().getAssets(), "Roboto-Thin.ttf");
        titleDetailsFont = Typeface.createFromAsset(PatientFragmentGPSessionsDetails.this.getActivity().getAssets(), "RobotoCondensed-Bold.ttf");
        patientDetailsFont = Typeface.createFromAsset(PatientFragmentGPSessionsDetails.this.getActivity().getAssets(), "Sanseriffic.otf");
    }

    private void setTitleFont() {

        tvPatientConsultationTitle.setTypeface(titleFont);
        tvAddress.setTypeface(titleDetailsFont);
        tvBookingDateTitle.setTypeface(titleDetailsFont);
        tvBookingTimeTitle.setTypeface(titleDetailsFont);
        tvStatusTitle.setTypeface(titleDetailsFont);
    }

    private void setInputFont() {

        tvName.setTypeface(patientDetailsFont);
        tvAddress.setTypeface(patientDetailsFont);
        tvBookingDate.setTypeface(patientDetailsFont);
        tvBookingTime.setTypeface(patientDetailsFont);
        tvStatus.setTypeface(patientDetailsFont);
    }

    protected void showCustomDialog() {

        dialog = new Dialog(PatientFragmentGPSessionsDetails.this.getActivity(), android.R.style.Theme_Translucent);
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bSessionCancelConsultation:

                showCustomDialog();
                break;

            case R.id.btnSendCancellation:

                if (etExplanation.getText().toString().isEmpty()) {
                    Toast.makeText(PatientFragmentGPSessionsDetails.this.getActivity(), "Please provide an explanation for cancellation", Toast.LENGTH_SHORT).show();
                } else {
                    retrieveConsultationInformation();

                    try {
                        storeBookingTimeDetails(baseClass.rejectedStatus);
                        PatientDatabaseHelper dbHelper = new PatientDatabaseHelper(PatientFragmentGPSessionsDetails.this.getActivity());

                        // Send a push notification to the GP with the consultation reference code attached
                        new NotificationSender().execute();

                        // Delete record of consultation
                        dbHelper.deleteEntry(phone);

                        dbHelper.close();
                    } catch (Exception e) {
                        baseClass.errorHandler(e);
                    }

                    dialog.dismiss();

                    Intent userHomepageIntent = new Intent("com.app.gptalk.homepage.patient.PATIENTHOMEPAGE");
                    userHomepageIntent.putExtra("username", patientUsername);
                    startActivity(userHomepageIntent);
                }

                break;

            case R.id.btnCancelCancellation:

                dialog.dismiss();
                break;

            case R.id.bSessionDirectionMap:

                if (isGPSActive() && latitude != 0.0 && longitude != 0.0) {

                    Intent directionIntent = new Intent("com.app.gptalk.homepage.patient.PATIENTGPDIRECTIONS");
                    directionIntent.putExtra("address", tvAddress.getText().toString());
                    directionIntent.putExtra("gp_name", tvName.getText().toString());
                    directionIntent.putExtra("latitude", Double.toString(latitude));
                    directionIntent.putExtra("longitude", Double.toString(longitude));

                    startActivity(directionIntent);
                } else if (isGPSActive() && latitude == 0.0 && longitude == 0.0) {

                    Toast.makeText(PatientFragmentGPSessionsDetails.this.getActivity(), "You are in close enough range to GP address", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PatientFragmentGPSessionsDetails.this.getActivity(), "GPS not enabled", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void storeBookingTimeDetails(String status) {

        bookingTimeDetails.put("booking_date", tvBookingDate.getText().toString());
        bookingTimeDetails.put("booking_time", tvBookingTime.getText().toString());
        bookingTimeDetails.put("booking_status", status);
        bookingTimeDetails.put("gp_phone", gpPhoneNumber);

        // Store all details of the requested consultation in a remote database, which will be used for retrieval purposes
        new RegisterGPAvailableTimes(PatientFragmentGPSessionsDetails.this.getActivity(), bookingTimeDetails).execute();
    }

    // Replace all spaces in a string with '+'
    private String reformatWhiteSpace(String sentence) {

        return sentence.replaceAll(" ", "%20");
    }


    private void retrieveConsultationInformation() {

        consultationDetails.put("booking_reference", appointmentReference);
        consultationDetails.put("name", reformatWhiteSpace(tvName.getText().toString()));
        consultationDetails.put("email", email);
        consultationDetails.put("booking_date", tvBookingDate.getText().toString());
        consultationDetails.put("booking_time", tvBookingTime.getText().toString());
        consultationDetails.put("status", baseClass.confirmedStatus);
        consultationDetails.put("phone_number", gpPhoneNumber);
        consultationDetails.put("explanation", reformatWhiteSpace(etExplanation.getText().toString()));


        // Store all details of the requested consultation in a remote database, which will be used for retrieval purposes
        try {
            new RegisterConsultationCancelPatient(PatientFragmentGPSessionsDetails.this.getActivity(), consultationDetails).execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
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

    private void sendNotification(String appointmentReference) {

        URI url = null;

        try {
            // PHP script stored in the remote server tranfers the push notification to the GP
            // The GP's device is discovered via their device registration ID, as generated during registration
            url = new URI("http://www.gptalk.ie/WAMP/gcm_server_php/send_notification.php?username=" + gpUsername + "&user_type=GP&reference_number=" + appointmentReference);
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
    public void onLocationChanged(Location location) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public class RetrieveGPProfileImage extends AsyncTask<Void, Void, Boolean> {

        private String profileURL;

        public RetrieveGPProfileImage(String patientProfileURL) {

            profileURL = patientProfileURL;
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
            ivProfilePhoto.setImageBitmap(profileBitmap);
        }
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

    public class RetrievePatientEmail extends AsyncTask<String, String, String> {

        private int loginSuccess;
        private String dbEmail;

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> patientCredentials = new ArrayList<NameValuePair>();
            patientCredentials.add(new BasicNameValuePair("username", patientUsername));

            try {
                JSONObject json = jsonParser.makeHttpRequest(PATIENT_ACCOUNT_URL, "POST", patientCredentials);
                loginSuccess = json.getInt("success");

                if (loginSuccess == 1) {

                    dbEmail = json.getString("email");
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

            email = dbEmail;
        }
    }

    private class RetrieveGPUsername extends AsyncTask<String, String, String> {

        private int loginSuccess;
        private String dbGPUsername, dbGPPhoneNumber;

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> gpCredentials = new ArrayList<NameValuePair>();

            // Retrieve all GP's details via their specified unique address
            gpCredentials.add(new BasicNameValuePair("medical_centre_address", gpAddress.substring(0, gpAddress.length()-1)));

            try {
                JSONObject json = jsonParser.makeHttpRequest(SELECTED_GP_DETAILS_URL, "POST", gpCredentials);
                loginSuccess = json.getInt("success");

                if (loginSuccess == 1) {

                    dbGPUsername = json.getString("username");
                    dbGPPhoneNumber = json.getString("medical_centre_phone_number");
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

            gpUsername = dbGPUsername;
            gpPhoneNumber = dbGPPhoneNumber;
        }
   }
}