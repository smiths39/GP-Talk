package com.app.gptalk.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.gptalk.base.JSONParser;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegistrationPatient extends Activity implements View.OnClickListener {

    private GoogleCloudMessaging googleCloudMessaging;
    private Spinner spinnerDay, spinnerMonth, spinnerYear, spinnerGender, spinnerNationality, spinnerCounty;
    private EditText etUsername, etEmail, etPassword, etFirstName, etLastName;
    private TextView tvRegistrationTitle, tvSignIn;
    private Button bRegister;
    private String registrationID;

    private Typeface font;
    private ProgressDialog progressDialog;

    private JSONParser jsonParser = new JSONParser();
    private BaseClass baseClass = new BaseClass();

    public static final String REGISTRATION_ID = "registration_id";
    public static final String USER_TYPE = "patient";

    private static final String REGISTER_URL = "http://www.gptalk.ie/gptalkie_registered_users/register_patient.php";
    private static final String APP_VERSION = "appVersion";
    private static final String MESSAGE = "message";

    // Required to indicate relevant error if device registration fails
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_patient);

        initialiseWidgets();

        // Hide characters of password input
        etPassword.setTransformationMethod(new PasswordTransformationMethod());

        font = Typeface.createFromAsset(getAssets(), "DropInside.ttf");
        tvRegistrationTitle.setTypeface(font);

        // Retrieve device's registration ID for communication purposes
        if (validateGooglePlayServices()) {

            googleCloudMessaging = GoogleCloudMessaging.getInstance(this.getApplicationContext());
            registrationID = getRegistrationID(this.getApplicationContext());
        }
    }

    private void initialiseWidgets() {

        etUsername = (EditText) findViewById(R.id.etRegistrationUsername);
        etEmail = (EditText) findViewById(R.id.etRegistrationEmail);
        etPassword = (EditText) findViewById(R.id.etRegistrationPassword);
        etFirstName = (EditText) findViewById(R.id.etRegistrationFirstName);
        etLastName = (EditText) findViewById(R.id.etRegistrationLastName);
        spinnerDay = (Spinner) findViewById(R.id.sRegistrationDay);
        spinnerMonth = (Spinner) findViewById(R.id.sRegistrationMonth);
        spinnerYear = (Spinner) findViewById(R.id.sRegistrationYear);
        spinnerGender = (Spinner) findViewById(R.id.sRegistrationGender);
        spinnerNationality = (Spinner) findViewById(R.id.sRegistrationNationality);
        spinnerCounty = (Spinner) findViewById(R.id.sRegistrationCounty);
        tvRegistrationTitle = (TextView) findViewById(R.id.tvRegistrationTitle);
        tvSignIn = (TextView) findViewById(R.id.tvSignInHere);
        bRegister = (Button) findViewById(R.id.bRegister);

        initialiseClickListeners();
    }

    private void initialiseClickListeners() {

        tvSignIn.setOnClickListener(RegistrationPatient.this);
        bRegister.setOnClickListener(RegistrationPatient.this);
    }

    public boolean isConnectingToInternet() {

        ConnectivityManager connectivity = (ConnectivityManager) RegistrationPatient.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {

                for (int i = 0; i < info.length; i++) {

                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onClick(View selection) {

        switch (selection.getId()) {

            case R.id.bRegister:

                if(isConnectingToInternet()) {

                    try {
                        // Populate database with registration values
                        new RegisterUser().execute();
                    } catch (Exception e) {
                        baseClass.errorHandler(e);
                    }

                    // Register the device's identification associated with the registered username
                    registerApplicationID(etUsername.getText().toString(), USER_TYPE);
                } else {
                    Toast.makeText(this, "Please enable Wi-Fi", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.tvSignInHere:
                // Revert back to login page
                Intent launchIntent = new Intent("com.app.gptalk.main.LOGIN");
                startActivity(launchIntent);
                break;
        }
    }

    private void registerApplicationID(String username, String userType) {

        if (validateGooglePlayServices()) {

            googleCloudMessaging = GoogleCloudMessaging.getInstance(this.getApplicationContext());
            registrationID = getRegistrationID(this.getApplicationContext());

            new RegisterApplicationID(this.getApplicationContext(), googleCloudMessaging, getAppVersion(this.getApplicationContext()), username, userType).execute();
        }
    }

    // Gets the current registration ID for the application on the GCM service
    private String getRegistrationID(Context applicationContext) {

        final SharedPreferences sharedPreferences = getGoogleCloudMessagingPreferences(applicationContext);
        String deviceRegistrationID = sharedPreferences.getString(REGISTRATION_ID, "");

        if (deviceRegistrationID.isEmpty()) {
            return "";
        }

        // If the application has been updated, the registration ID must be cleared as it may not work with the new application version
        int registeredDeviceVersion = sharedPreferences.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentDeviceVersion = getAppVersion(this.getApplicationContext());

        if (registeredDeviceVersion != currentDeviceVersion) {
            return "";
        }

        return deviceRegistrationID;
    }

    private SharedPreferences getGoogleCloudMessagingPreferences(Context context) {

        return this.getSharedPreferences(RegistrationPatient.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    // Retrieve application's version code
    private static int getAppVersion(Context context) {

        PackageInfo packageInfo = null;

        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return packageInfo.versionCode;
    }

    // Check if the device has the Google Play Services APK.
    // If APK not found, display a dialog that allows the user to download the APK from the Google Play Store.
    private boolean validateGooglePlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    public class RegisterUser extends AsyncTask<String, String, String> {

        private int registerSuccess;
        private String username, password, email, firstName, lastName, day, month, year, gender, nationality, county;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog = new ProgressDialog(RegistrationPatient.this);
            baseClass.initialiseProgressDialog(progressDialog, "Registering User...");
        }

        @Override
        protected String doInBackground(String... args) {

            retrieveInputVariables();

            try {
                ArrayList<NameValuePair> registrationParameters = new ArrayList<NameValuePair>();
                addRegistrationValues(registrationParameters);

                // Insert and validate registration input
                JSONObject json = jsonParser.makeHttpRequest(REGISTER_URL, "POST", registrationParameters);
                registerSuccess = json.getInt("success");

                if (registerSuccess == 1) {

                    Intent userHomepageIntent = new Intent("com.app.gptalk.homepage.patient.PATIENTHOMEPAGE");
                    userHomepageIntent.putExtra("username", username);
                    startActivity(userHomepageIntent);
                    return json.getString(MESSAGE);
                } else {
                    return json.getString(MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void retrieveInputVariables() {

            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            email = etEmail.getText().toString();
            firstName = etFirstName.getText().toString();
            lastName = etLastName.getText().toString();
            day = spinnerDay.getItemAtPosition(spinnerDay.getSelectedItemPosition()).toString();
            month = spinnerMonth.getItemAtPosition(spinnerMonth.getSelectedItemPosition()).toString();
            year = spinnerYear.getItemAtPosition(spinnerYear.getSelectedItemPosition()).toString();
            gender = spinnerGender.getItemAtPosition(spinnerGender.getSelectedItemPosition()).toString();
            nationality = spinnerNationality.getItemAtPosition(spinnerNationality.getSelectedItemPosition()).toString();
            county = spinnerCounty.getItemAtPosition(spinnerCounty.getSelectedItemPosition()).toString();
        }

        // Add all details to be inserted into GP table in the database
        private void addRegistrationValues(ArrayList<NameValuePair> newRegistrationParameters) {

            newRegistrationParameters.add(new BasicNameValuePair("username", username));
            newRegistrationParameters.add(new BasicNameValuePair("password", password));
            newRegistrationParameters.add(new BasicNameValuePair("email", email));
            newRegistrationParameters.add(new BasicNameValuePair("first_name", firstName));
            newRegistrationParameters.add(new BasicNameValuePair("last_name", lastName));
            newRegistrationParameters.add(new BasicNameValuePair("day", day));
            newRegistrationParameters.add(new BasicNameValuePair("month", month));
            newRegistrationParameters.add(new BasicNameValuePair("year", year));
            newRegistrationParameters.add(new BasicNameValuePair("gender", gender));
            newRegistrationParameters.add(new BasicNameValuePair("nationality", nationality));
            newRegistrationParameters.add(new BasicNameValuePair("county", county));
        }

        protected void onPostExecute(String file_url) {

            progressDialog.dismiss();

            if (file_url != null) {
                Toast.makeText(RegistrationPatient.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}
