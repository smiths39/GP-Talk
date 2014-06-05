package com.app.gptalk.option;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.gptalk.base.JSONParser;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class OptionPatientAccountSettings extends Activity implements View.OnClickListener {

    private Button bSaveSettings;
    private ProgressDialog progressDialog;
    private Spinner sNewDayPatient, sNewMonthPatient, sNewYearPatient, sNewGenderPatient, sNewNationalityPatient, sNewCountyPatient;
    private Typeface titleFont, titleDetailsFont, patientDetailsFont;
    private TextView tvAccountTitle, tvNewUsernameTitle, tvNewFirstNameTitle, tvNewLastNameTitle, tvNewEmailTitle, tvNewDOBTitle, tvNewGenderTitle, tvNewNationalityTitle, tvNewCountyTitle;
    private EditText etNewUsernamePatient, etNewFirstNamePatient, etNewLastNamePatient, etNewEmailPatient;

    private String username, userType;
    private String currentUserID, currentUsername, currentFirstName, currentLastName, currentEmail;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();

    private static final String UPDATE_URL = "http://www.gptalk.ie/gptalkie_registered_users/update_patient.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_patient_account_settings);

        getUsername();
        initialiseTitleVariables();
        initialiseInputVariables();

        retrieveOriginalValues();
        setInputHints();

        setFontStyles();
        setTitleFont();
        setInputFont();
    }

    private void getUsername() {

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userType = intent.getStringExtra("user_type");
    }

    private void initialiseTitleVariables() {

        tvAccountTitle = (TextView) findViewById(R.id.tvAccountTitle);
        tvNewUsernameTitle = (TextView) findViewById(R.id.tvNewUsernameTitle);
        tvNewFirstNameTitle = (TextView) findViewById(R.id.tvNewFirstNameTitle);
        tvNewLastNameTitle = (TextView) findViewById(R.id.tvNewLastNameTitle);
        tvNewEmailTitle = (TextView) findViewById(R.id.tvNewEmailTitle);
        tvNewDOBTitle = (TextView) findViewById(R.id.tvNewDOBTitle);
        tvNewGenderTitle = (TextView) findViewById(R.id.tvNewGenderTitle);
        tvNewNationalityTitle = (TextView) findViewById(R.id.tvNewNationalityTitle);
        tvNewCountyTitle = (TextView) findViewById(R.id.tvNewCountyTitle);

        bSaveSettings = (Button) findViewById(R.id.bSaveProfileSettings);

        bSaveSettings.setOnClickListener(OptionPatientAccountSettings.this);
    }

    private void initialiseInputVariables() {

        etNewUsernamePatient = (EditText) findViewById(R.id.etNewUsernamePatient);
        etNewFirstNamePatient = (EditText) findViewById(R.id.etNewFirstNamePatient);
        etNewLastNamePatient = (EditText) findViewById(R.id.etNewLastNamePatient);
        etNewEmailPatient = (EditText) findViewById(R.id.etNewEmailPatient);
        sNewDayPatient = (Spinner) findViewById(R.id.etNewDOBDayPatient);
        sNewMonthPatient = (Spinner) findViewById(R.id.etNewDOBMonthPatient);
        sNewYearPatient = (Spinner) findViewById(R.id.etNewDOBYearPatient);
        sNewGenderPatient = (Spinner) findViewById(R.id.etNewGenderPatient);
        sNewNationalityPatient = (Spinner) findViewById(R.id.etNewNationalityPatient);
        sNewCountyPatient = (Spinner) findViewById(R.id.etNewCountyPatient);
    }

    private void setInputHints() {

        etNewUsernamePatient.setHint(this.currentUsername);
        etNewFirstNamePatient.setHint(this.currentFirstName);
        etNewLastNamePatient.setHint(this.currentLastName);
        etNewEmailPatient.setHint(this.currentEmail);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        titleDetailsFont = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
        patientDetailsFont = Typeface.createFromAsset(getAssets(), "Sanseriffic.otf");
    }

    private void setTitleFont() {

        tvAccountTitle.setTypeface(titleFont);
        tvNewUsernameTitle.setTypeface(titleDetailsFont);
        tvNewFirstNameTitle.setTypeface(titleDetailsFont);
        tvNewLastNameTitle.setTypeface(titleDetailsFont);
        tvNewEmailTitle.setTypeface(titleDetailsFont);
        tvNewDOBTitle.setTypeface(titleDetailsFont);
        tvNewGenderTitle.setTypeface(titleDetailsFont);
        tvNewNationalityTitle.setTypeface(titleDetailsFont);
        tvNewCountyTitle.setTypeface(titleDetailsFont);
    }

    private void setInputFont() {

        etNewUsernamePatient.setTypeface(patientDetailsFont);
        etNewFirstNamePatient.setTypeface(patientDetailsFont);
        etNewLastNamePatient.setTypeface(patientDetailsFont);
        etNewEmailPatient.setTypeface(patientDetailsFont);
    }

    // Retrieve original account details from previous intent
    private void retrieveOriginalValues() {

        Intent intent = getIntent();
        HashMap<String, String> retrievedPatientDetails = (HashMap<String, String>)intent.getSerializableExtra("patientDetails");
        Iterator<String> keyIterator = retrievedPatientDetails.keySet().iterator();

        while (keyIterator.hasNext()) {

            String key = keyIterator.next();

            if (key.equals("user_id")) {
                this.currentUserID = retrievedPatientDetails.get(key);
            } else if (key.equals("username")) {
                this.currentUsername = retrievedPatientDetails.get(key);
            } else if (key.equals("first_name")) {
                this.currentFirstName = retrievedPatientDetails.get(key);
            } else if (key.equals("last_name")) {
                this.currentLastName = retrievedPatientDetails.get(key);
            } else if (key.equals("email")) {
                this.currentEmail = retrievedPatientDetails.get(key);
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bSaveProfileSettings:
                try {
                    new UpdateSettings().execute();
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

    public class UpdateSettings extends AsyncTask<String, String, String> {

        private int updateSuccess;
        private String username, firstName, lastName, email, day, month, year, gender, nationality, county;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog = new ProgressDialog(OptionPatientAccountSettings.this);
            baseClass.initialiseProgressDialog(progressDialog, "Updating Details...");
        }

        @Override
        protected String doInBackground(String... args) {

            retrieveInputValues();
            checkHintValues();

            try {
                ArrayList<NameValuePair> updateParameters = new ArrayList<NameValuePair>();
                updateParameters = setParameterValues(updateParameters);

                JSONObject json = jsonParser.makeHttpRequest(UPDATE_URL, "POST", updateParameters);

                updateSuccess = json.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        // Retrieve new account detail values
        private void retrieveInputValues() {

            this.username = etNewUsernamePatient.getText().toString();
            this.firstName = etNewFirstNamePatient.getText().toString();
            this.lastName = etNewLastNamePatient.getText().toString();
            this.email = etNewEmailPatient.getText().toString();
            this.day = sNewDayPatient.getSelectedItem().toString();
            this.month = sNewMonthPatient.getSelectedItem().toString();
            this.year = sNewYearPatient.getSelectedItem().toString();
            this.gender = sNewGenderPatient.getSelectedItem().toString();
            this.nationality = sNewNationalityPatient.getSelectedItem().toString();
            this.county = sNewCountyPatient.getSelectedItem().toString();
        }

        // If no value was entered in edittext, the previous account detail value (set as a hint) is taken
        private void checkHintValues() {

            this.username = retrieveHintValue(this.username, etNewUsernamePatient);
            this.firstName = retrieveHintValue(this.firstName, etNewFirstNamePatient);
            this.lastName = retrieveHintValue(this.lastName, etNewLastNamePatient);
            this.email = retrieveHintValue(this.email, etNewEmailPatient);
        }

        private String retrieveHintValue(String value, EditText editText) {

            if (value.equals("")) {
                value = editText.getHint().toString();
            }

            return value;
        }

        // All new account details to be updated in remote database
        private ArrayList<NameValuePair> setParameterValues(ArrayList<NameValuePair> updateParameters) {

            updateParameters.add(new BasicNameValuePair("user_id", OptionPatientAccountSettings.this.currentUserID));
            updateParameters.add(new BasicNameValuePair("username", this.username));
            updateParameters.add(new BasicNameValuePair("first_name", this.firstName));
            updateParameters.add(new BasicNameValuePair("last_name", this.lastName));
            updateParameters.add(new BasicNameValuePair("email", this.email));
            updateParameters.add(new BasicNameValuePair("day", this.day));
            updateParameters.add(new BasicNameValuePair("month", this.month));
            updateParameters.add(new BasicNameValuePair("year", this.year));
            updateParameters.add(new BasicNameValuePair("gender", this.gender));
            updateParameters.add(new BasicNameValuePair("nationality", this.nationality));
            updateParameters.add(new BasicNameValuePair("county", this.county));

            return updateParameters;
        }

        protected void onPostExecute(String file_url) {

            progressDialog.dismiss();

            if (updateSuccess == 1) {

                try {

                    if (spinnerDefaultValue(sNewDayPatient, "Day")) {
                        toastMaker("Day");
                    } else if (spinnerDefaultValue(sNewMonthPatient, "Month")) {
                        toastMaker("Month");
                    } else if (spinnerDefaultValue(sNewYearPatient, "Year")) {
                        toastMaker("Year");
                    } else if (spinnerDefaultValue(sNewGenderPatient, "Gender")) {
                        toastMaker("Gender");
                    } else if (spinnerDefaultValue(sNewNationalityPatient, "Nationality")) {
                        toastMaker("Nationality");
                    } else if (spinnerDefaultValue(sNewCountyPatient, "County")) {
                        toastMaker("County");
                    } else {

                        Intent accountIntent = new Intent("com.app.gptalk.option.OPTIONPATIENTACCOUNT");
                        accountIntent.putExtra("username", username);
                        startActivity(accountIntent);
                    }
                } catch (Exception e) {
                   baseClass.errorHandler(e);
                }
            }
        }

        // Check if spinner value item selected is the unacceptable default value
        private boolean spinnerDefaultValue(Spinner spinner, String item) {

            return (spinner.getSelectedItem().toString().equals(item)) ? true : false;

        }

        // Generate a toast message if a default value is selected
        private void toastMaker(String message) {

            Toast.makeText(OptionPatientAccountSettings.this, "Please select a " + message, Toast.LENGTH_SHORT).show();
        }
    }
}