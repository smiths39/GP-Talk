package com.app.gptalk.option;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.gptalk.base.JSONParser;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class OptionPatientAccount extends Activity implements View.OnClickListener {

    private Button bProfileSettings;
    private ImageView ivProfilePhoto;
    private Typeface titleFont, titleDetailsFont, patientDetailsFont;
    private TextView tvAccountTitle, tvProfileTitle, tvUsernameTitle, tvFirstNameTitle, tvLastNameTitle, tvEmailTitle, tvDOBTitle, tvGenderTitle, tvNationalityTitle, tvCountyTitle,
                     tvUsernamePatient, tvFirstNamePatient, tvLastNamePatient, tvEmailPatient, tvDayPatient, tvMonthPatient, tvYearPatient, tvGenderPatient, tvNationalityPatient, tvCountyPatient;

    private String username, userType;
    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();
    private HashMap<String, String> patientDetails = new HashMap<String, String>();

    private final static String PATIENT_ACCOUNT_URL = "http://www.gptalk.ie/gptalkie_registered_users/patient_account_details.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_patient_account);

        getUsername();
        initialiseTitleVariables();
        initialiseInputVariables();

        setFontStyles();
        setTitleFont();
        setInputFont();

        try {
            new RetrievePatientDetails().execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    private void getUsername() {

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userType = intent.getStringExtra("user_type");
    }

    private void initialiseTitleVariables() {

        tvAccountTitle = (TextView) findViewById(R.id.tvAccountTitle);
        tvProfileTitle = (TextView) findViewById(R.id.tvProfileTitle);
        tvUsernameTitle = (TextView) findViewById(R.id.tvUsernameTitle);
        tvFirstNameTitle = (TextView) findViewById(R.id.tvFirstNameTitle);
        tvLastNameTitle = (TextView) findViewById(R.id.tvLastNameTitle);
        tvEmailTitle = (TextView) findViewById(R.id.tvEmailTitle);
        tvDOBTitle = (TextView) findViewById(R.id.tvDOBTitle);
        tvGenderTitle = (TextView) findViewById(R.id.tvGenderTitle);
        tvNationalityTitle = (TextView) findViewById(R.id.tvNationalityTitle);
        tvCountyTitle = (TextView) findViewById(R.id.tvCountyTitle);

        ivProfilePhoto = (ImageView) findViewById(R.id.ivProfilePhoto);
        bProfileSettings = (Button) findViewById(R.id.bProfileSettings);

        bProfileSettings.setOnClickListener(OptionPatientAccount.this);
    }

    private void initialiseInputVariables() {

        tvUsernamePatient = (TextView) findViewById(R.id.tvUsernamePatient);
        tvFirstNamePatient = (TextView) findViewById(R.id.tvFirstNamePatient);
        tvLastNamePatient = (TextView) findViewById(R.id.tvLastNamePatient);
        tvEmailPatient = (TextView) findViewById(R.id.tvEmailPatient);
        tvDayPatient = (TextView) findViewById(R.id.tvDOBDayPatient);
        tvMonthPatient = (TextView) findViewById(R.id.tvDOBMonthPatient);
        tvYearPatient = (TextView) findViewById(R.id.tvDOBYearPatient);
        tvGenderPatient = (TextView) findViewById(R.id.tvGenderPatient);
        tvNationalityPatient = (TextView) findViewById(R.id.tvNationalityPatient);
        tvCountyPatient = (TextView) findViewById(R.id.tvCountyPatient);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(this.getAssets(), "Roboto-Thin.ttf");
        titleDetailsFont = Typeface.createFromAsset(this.getAssets(), "RobotoCondensed-Bold.ttf");
        patientDetailsFont = Typeface.createFromAsset(this.getAssets(), "Sanseriffic.otf");
    }

    private void setTitleFont() {

        tvAccountTitle.setTypeface(titleFont);
        tvProfileTitle.setTypeface(titleDetailsFont);
        tvUsernameTitle.setTypeface(titleDetailsFont);
        tvFirstNameTitle.setTypeface(titleDetailsFont);
        tvLastNameTitle.setTypeface(titleDetailsFont);
        tvEmailTitle.setTypeface(titleDetailsFont);
        tvDOBTitle.setTypeface(titleDetailsFont);
        tvGenderTitle.setTypeface(titleDetailsFont);
        tvNationalityTitle.setTypeface(titleDetailsFont);
        tvCountyTitle.setTypeface(titleDetailsFont);
    }

    private void setInputFont() {

        tvUsernamePatient.setTypeface(patientDetailsFont);
        tvFirstNamePatient.setTypeface(patientDetailsFont);
        tvLastNamePatient.setTypeface(patientDetailsFont);
        tvEmailPatient.setTypeface(patientDetailsFont);
        tvDayPatient.setTypeface(patientDetailsFont);
        tvMonthPatient.setTypeface(patientDetailsFont);
        tvYearPatient.setTypeface(patientDetailsFont);
        tvGenderPatient.setTypeface(patientDetailsFont);
        tvNationalityPatient.setTypeface(patientDetailsFont);
        tvCountyPatient.setTypeface(patientDetailsFont);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bProfileSettings:
                try {
                    Intent accountSettingsIntent = new Intent("com.app.gptalk.option.OPTIONPATIENTACCOUNTSETTINGS");
                    accountSettingsIntent.putExtra("patientDetails", patientDetails);
                    startActivity(accountSettingsIntent);
                } catch (Exception e) {
                    baseClass.errorHandler(e);
                }

                break;
        }
    }

    // Launch preference menu
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.optionalmenu, menu);
        return true;
    }

    public void launchOptionActivity(String destination) {

        try {
            Intent launchIntent = new Intent("com.app.gptalk.option." + destination);
            launchIntent.putExtra("username", username);
            launchIntent.putExtra("user_type", userType);
            startActivity(launchIntent);
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    // Launch preference menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.optionLaunch:
                return true;

            case R.id.optionHome:

                if (userType.equals("patient")) {
                    Intent userHomepageIntent = new Intent("com.app.gptalk.homepage.patient.PATIENTHOMEPAGE");
                    userHomepageIntent.putExtra("username", username);
                    startActivity(userHomepageIntent);
                } else {
                    Intent gpHomepageIntent = new Intent("com.app.gptalk.homepage.gp.GPHOMEPAGE");
                    gpHomepageIntent.putExtra("username", username);
                    startActivity(gpHomepageIntent);
                }
                return true;

            case R.id.optionAccount:
                launchOptionActivity("OPTIONPATIENTACCOUNT");
                return true;

            case R.id.optionFAQ:
                launchOptionActivity("OPTIONFAQ");
                return true;

            case R.id.optionSupport:
                launchOptionActivity("OPTIONSUPPORT");
                return true;

            case R.id.optionSettings:
                launchOptionActivity("OPTIONSETTINGS");
                return true;

            case R.id.optionWebsite:
                launchOptionActivity("OPTIONWEBSITE");
                return true;

            case R.id.exit:
                Intent loginIntent = new Intent("com.app.gptalk.main.LOGIN");
                startActivity(loginIntent);
                return true;
        }

        return false;
    }

    public class RetrievePatientDetails extends AsyncTask<String, String, String> {

        private int loginSuccess;
        private String patientUsername, dbUserID, dbProfileURL, dbFirstName, dbLastName, dbEmail, dbDay, dbMonth, dbYear, dbGender, dbNationality, dbCounty;
        private ProgressDialog progressDialog;
        private Bitmap profilePhotoBitmap = null;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(OptionPatientAccount.this);
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving Patient Details...");
        }

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> patientCredentials = new ArrayList<NameValuePair>();

            patientUsername = baseClass.retrieveUsername(OptionPatientAccount.this, null);
            patientCredentials.add(new BasicNameValuePair("username", patientUsername));

            try {
                JSONObject json = jsonParser.makeHttpRequest(PATIENT_ACCOUNT_URL, "POST", patientCredentials);
                loginSuccess = json.getInt("success");

                if (loginSuccess == 1) {

                    retrieveJSONValues(json);

                    if (dbProfileURL.length() == 0) {
                        profilePhotoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_picture);

                    } else {
                        String accessPhotoURL = "http://www.gptalk.ie/" + dbProfileURL;

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

        // Retrieve all account details from remote database with a corresponding unique username
        private void retrieveJSONValues(JSONObject json) {

            try {
                dbUserID = json.getString("user_id");
                dbProfileURL = json.getString("profile");
                dbFirstName = json.getString("first_name");
                dbLastName = json.getString("last_name");
                dbEmail = json.getString("email");
                dbDay = json.getString("day");
                dbMonth = json.getString("month");
                dbYear = json.getString("year");
                dbGender = json.getString("gender");
                dbNationality = json.getString("nationality");
                dbCounty = json.getString("county");
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }
        }

        protected void onPostExecute(String fileURL) {

            progressDialog.dismiss();

            setPatientDetails();
            insertPatientDetails();
        }

        // Set displayed texts with retrieved values
        private void setPatientDetails() {

            ivProfilePhoto.setImageBitmap(profilePhotoBitmap);
            tvUsernamePatient.setText(this.patientUsername);
            tvFirstNamePatient.setText(dbFirstName);
            tvLastNamePatient.setText(dbLastName);
            tvEmailPatient.setText(dbEmail);
            tvDayPatient.setText(dbDay);
            tvMonthPatient.setText(dbMonth);
            tvYearPatient.setText(dbYear);
            tvGenderPatient.setText(dbGender);
            tvNationalityPatient.setText(dbNationality);
            tvCountyPatient.setText(dbCounty);
        }

        // Insert retrieved details into hashmap, to be used as edittext hints in next intent
        private void insertPatientDetails() {

            patientDetails.put("user_id", dbUserID);
            patientDetails.put("username", this.patientUsername);
            patientDetails.put("first_name", dbFirstName);
            patientDetails.put("last_name", dbLastName);
            patientDetails.put("email", dbEmail);
        }
    }
}