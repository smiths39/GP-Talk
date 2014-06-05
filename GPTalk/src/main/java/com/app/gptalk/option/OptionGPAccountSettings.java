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

public class OptionGPAccountSettings extends Activity implements View.OnClickListener {

    private Button bSaveSettings;
    private ProgressDialog progressDialog;
    private Spinner sNewNameTitleGP, sNewMedicalCentreCountyGP;
    private Typeface titleFont, titleDetailsFont, gpDetailsFont;
    private TextView tvAccountTitle, tvNewUsernameTitle, tvNewNameTitle, tvNewFirstNameTitle, tvNewLastNameTitle, tvNewEmailTitle,
                     tvNewMedicalCentreAddressTitle, tvNewMedicalCentreCityTitle, tvNewMedicalCentreCountyTitle, tvNewMedicalCentrePostCodeTitle,
                     tvNewMedicalCentrePhoneNumberTitle, tvNewMedicalCentreWebsiteTitle, tvNewMedicalCentreConsultationCostsTitle;
    private EditText etNewUsernameGP, etNewFirstNameGP, etNewLastNameGP, etNewEmailGP, etNewMedicalCentreAddressGP,
                     etNewMedicalCentreCityGP, etNewMedicalCentrePostCodeGP, etNewMedicalCentrePhoneNumberGP,
                     etNewMedicalCentreWebsiteGP, etNewMedicalCentreConsultationCostsGP;

    private String username, userType;
    private String currentGPID, currentUsername, currentNameTitle, currentFirstName, currentLastName, currentEmail, currentMedicalCentreAddress, currentMedicalCentreCity,
                   currentMedicalCentreCounty, currentMedicalCentrePostCode, currentMedicalCentrePhoneNumber, currentMedicalCentreWebsite, currentMedicalCentreConsultationCosts;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();

    private static final String UPDATE_URL = "http://www.gptalk.ie/gptalkie_registered_users/update_gp.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_gp_account_settings);

        getUsername();
        initialiseTitleWidgets();
        initialiseInputWidgets();

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

    private void initialiseTitleWidgets() {

        tvAccountTitle = (TextView) findViewById(R.id.tvNewAccountTitleGP);
        tvNewUsernameTitle = (TextView) findViewById(R.id.tvNewGPUsernameTitle);
        tvNewNameTitle = (TextView) findViewById(R.id.tvNewGPNameTitle);
        tvNewFirstNameTitle = (TextView) findViewById(R.id.tvNewGPFirstNameTitle);
        tvNewLastNameTitle = (TextView) findViewById(R.id.tvNewGPLastNameTitle);
        tvNewEmailTitle = (TextView) findViewById(R.id.tvNewGPEmailTitle);
        tvNewMedicalCentreAddressTitle = (TextView) findViewById(R.id.tvNewGPMedicalCentreAddressTitle);
        tvNewMedicalCentreCityTitle = (TextView) findViewById(R.id.tvNewGPMedicalCentreCityTitle);
        tvNewMedicalCentreCountyTitle = (TextView) findViewById(R.id.tvNewGPMedicalCentreCountyTitle);
        tvNewMedicalCentrePostCodeTitle = (TextView) findViewById(R.id.tvNewGPMedicalCentrePostCodeTitle);
        tvNewMedicalCentrePhoneNumberTitle = (TextView) findViewById(R.id.tvNewGPMedicalCentrePhoneNumberTitle);
        tvNewMedicalCentreWebsiteTitle = (TextView) findViewById(R.id.tvNewGPMedicalCentreWebsiteTitle);
        tvNewMedicalCentreConsultationCostsTitle = (TextView) findViewById(R.id.tvNewGPMedicalCentreConsultationCostsTitle);

        bSaveSettings = (Button) findViewById(R.id.bSaveGPProfileSettings);

        bSaveSettings.setOnClickListener(OptionGPAccountSettings.this);
    }

    private void initialiseInputWidgets() {

        etNewUsernameGP = (EditText) findViewById(R.id.etNewUsernameGP);
        sNewNameTitleGP = (Spinner) findViewById(R.id.etNewGPTitleGP);
        etNewFirstNameGP = (EditText) findViewById(R.id.etNewFirstNameGP);
        etNewLastNameGP = (EditText) findViewById(R.id.etNewLastNameGP);
        etNewEmailGP = (EditText) findViewById(R.id.etNewEmailGP);
        etNewMedicalCentreAddressGP = (EditText) findViewById(R.id.etNewMedicalCentreAddressGP);
        etNewMedicalCentreCityGP = (EditText) findViewById(R.id.etNewMedicalCentreCityGP);
        sNewMedicalCentreCountyGP = (Spinner) findViewById(R.id.etNewMedicalCentreCountyGP);
        etNewMedicalCentrePostCodeGP = (EditText) findViewById(R.id.etNewMedicalCentrePostCodeGP);
        etNewMedicalCentrePhoneNumberGP = (EditText) findViewById(R.id.etNewMedicalCentrePhoneNumberGP);
        etNewMedicalCentreWebsiteGP = (EditText) findViewById(R.id.etNewMedicalCentreWebsiteGP);
        etNewMedicalCentreConsultationCostsGP = (EditText) findViewById(R.id.etNewMedicalCentreConsultationCostsGP);
    }

    private void setInputHints() {

        etNewUsernameGP.setHint(this.currentUsername);
        etNewFirstNameGP.setHint(this.currentFirstName);
        etNewLastNameGP.setHint(this.currentLastName);
        etNewEmailGP.setHint(this.currentEmail);
        etNewMedicalCentreAddressGP.setHint(this.currentMedicalCentreAddress);
        etNewMedicalCentreCityGP.setHint(this.currentMedicalCentreCity);
        etNewMedicalCentrePostCodeGP.setHint(this.currentMedicalCentrePostCode);
        etNewMedicalCentrePhoneNumberGP.setHint(this.currentMedicalCentrePhoneNumber);
        etNewMedicalCentreWebsiteGP.setHint(this.currentMedicalCentreWebsite);
        etNewMedicalCentreConsultationCostsGP.setHint(this.currentMedicalCentreConsultationCosts);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        titleDetailsFont = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
        gpDetailsFont = Typeface.createFromAsset(getAssets(), "Sanseriffic.otf");
    }

    private void setTitleFont() {

        tvAccountTitle.setTypeface(titleFont);
        tvNewUsernameTitle.setTypeface(titleDetailsFont);
        tvNewNameTitle.setTypeface(titleDetailsFont);
        tvNewFirstNameTitle.setTypeface(titleDetailsFont);
        tvNewLastNameTitle.setTypeface(titleDetailsFont);
        tvNewEmailTitle.setTypeface(titleDetailsFont);
        tvNewMedicalCentreAddressTitle.setTypeface(titleDetailsFont);
        tvNewMedicalCentreCityTitle.setTypeface(titleDetailsFont);
        tvNewMedicalCentreCountyTitle.setTypeface(titleDetailsFont);
        tvNewMedicalCentrePostCodeTitle.setTypeface(titleDetailsFont);
        tvNewMedicalCentrePhoneNumberTitle.setTypeface(titleDetailsFont);
        tvNewMedicalCentreWebsiteTitle.setTypeface(titleDetailsFont);
        tvNewMedicalCentreConsultationCostsTitle.setTypeface(titleDetailsFont);
    }

    private void setInputFont() {

        etNewUsernameGP.setTypeface(gpDetailsFont);
        etNewFirstNameGP.setTypeface(gpDetailsFont);
        etNewLastNameGP.setTypeface(gpDetailsFont);
        etNewEmailGP.setTypeface(gpDetailsFont);
        etNewMedicalCentreAddressGP.setTypeface(gpDetailsFont);
        etNewMedicalCentreCityGP.setTypeface(gpDetailsFont);
        etNewMedicalCentrePostCodeGP.setTypeface(gpDetailsFont);
        etNewMedicalCentrePhoneNumberGP.setTypeface(gpDetailsFont);
        etNewMedicalCentreWebsiteGP.setTypeface(gpDetailsFont);
        etNewMedicalCentreConsultationCostsGP.setTypeface(gpDetailsFont);
    }

    // Retrieve original account details from previous intent
    private void retrieveOriginalValues() {

        Intent intent = getIntent();
        HashMap<String, String> retrievedGPDetails = (HashMap<String, String>)intent.getSerializableExtra("gpDetails");
        Iterator<String> keyIterator = retrievedGPDetails.keySet().iterator();

        while (keyIterator.hasNext()) {

            String key = keyIterator.next();

            if (key.equals("gp_id")) {
                this.currentGPID = retrievedGPDetails.get(key);
            } else if (key.equals("username")) {
                this.currentUsername = retrievedGPDetails.get(key);
            } else if (key.equals("title")) {
                this.currentNameTitle = retrievedGPDetails.get(key);
            } else if (key.equals("first_name")) {
                this.currentFirstName = retrievedGPDetails.get(key);
            } else if (key.equals("last_name")) {
                this.currentLastName = retrievedGPDetails.get(key);
            } else if (key.equals("email")) {
                this.currentEmail = retrievedGPDetails.get(key);
            } else if (key.equals("medical_centre_address")) {
                this.currentMedicalCentreAddress = retrievedGPDetails.get(key);
            } else if (key.equals("medical_centre_city")) {
                this.currentMedicalCentreCity = retrievedGPDetails.get(key);
            } else if (key.equals("medical_centre_county")) {
                this.currentMedicalCentreCounty = retrievedGPDetails.get(key);
            } else if (key.equals("medical_centre_post_code")) {
                this.currentMedicalCentrePostCode = retrievedGPDetails.get(key);
            } else if (key.equals("medical_centre_phone_number")) {
                this.currentMedicalCentrePhoneNumber = retrievedGPDetails.get(key);
            } else if (key.equals("medical_centre_website")) {
                this.currentMedicalCentreWebsite = retrievedGPDetails.get(key);
            } else if (key.equals("medical_centre_consultation_costs")) {
                this.currentMedicalCentreConsultationCosts = retrievedGPDetails.get(key);
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bSaveGPProfileSettings:
                try {
                    new UpdateGPSettings().execute();
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

                launchOptionActivity("OPTIONGPACCOUNT");
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

    public class UpdateGPSettings extends AsyncTask<String, String, String> {

        private int updateSuccess;
        private String username, title, firstName, lastName, email, medical_centre_address, medical_centre_city, medical_centre_county,
                       medical_centre_post_code, medical_centre_phone_number, medical_centre_website, medical_centre_consultation_costs;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog = new ProgressDialog(OptionGPAccountSettings.this);
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
                baseClass.errorHandler(e);
            }

            return null;
        }

        // Retrieve new account detail values
        private void retrieveInputValues() {

            this.username = etNewUsernameGP.getText().toString();
            this.title = sNewNameTitleGP.getSelectedItem().toString();
            this.firstName = etNewFirstNameGP.getText().toString();
            this.lastName = etNewLastNameGP.getText().toString();
            this.email = etNewEmailGP.getText().toString();
            this.medical_centre_address = etNewMedicalCentreAddressGP.getText().toString();
            this.medical_centre_city = etNewMedicalCentreCityGP.getText().toString();
            this.medical_centre_county = sNewMedicalCentreCountyGP.getSelectedItem().toString();
            this.medical_centre_post_code = etNewMedicalCentrePostCodeGP.getText().toString();
            this.medical_centre_phone_number = etNewMedicalCentrePhoneNumberGP.getText().toString();
            this.medical_centre_website = etNewMedicalCentreWebsiteGP.getText().toString();
            this.medical_centre_consultation_costs = etNewMedicalCentreConsultationCostsGP.getText().toString();
        }

        // If no value was entered in edittext, the previous account detail value (set as a hint) is taken
        private void checkHintValues() {

            this.username = retrieveHintValue(this.username, etNewUsernameGP);
            this.firstName = retrieveHintValue(this.firstName, etNewFirstNameGP);
            this.lastName = retrieveHintValue(this.lastName, etNewLastNameGP);
            this.email = retrieveHintValue(this.email, etNewEmailGP);
            this.medical_centre_address = retrieveHintValue(this.medical_centre_address, etNewMedicalCentreAddressGP);
            this.medical_centre_city = retrieveHintValue(this.medical_centre_city, etNewMedicalCentreCityGP);
            this.medical_centre_post_code = retrieveHintValue(this.medical_centre_post_code, etNewMedicalCentrePostCodeGP);
            this.medical_centre_phone_number = retrieveHintValue(this.medical_centre_phone_number, etNewMedicalCentrePhoneNumberGP);
            this.medical_centre_website = retrieveHintValue(this.medical_centre_website, etNewMedicalCentreWebsiteGP);
            this.medical_centre_consultation_costs = retrieveHintValue(this.medical_centre_consultation_costs, etNewMedicalCentreConsultationCostsGP);
        }

        private String retrieveHintValue(String value, EditText editText) {

            if (value.equals("")) {
                value = editText.getHint().toString();
            }

            return value;
        }

        // All new account details to be updated in remote database
        private ArrayList<NameValuePair> setParameterValues(ArrayList<NameValuePair> updateParameters) {

            updateParameters.add(new BasicNameValuePair("gp_id", OptionGPAccountSettings.this.currentGPID));
            updateParameters.add(new BasicNameValuePair("username", this.username));
            updateParameters.add(new BasicNameValuePair("title", this.title));
            updateParameters.add(new BasicNameValuePair("first_name", this.firstName));
            updateParameters.add(new BasicNameValuePair("last_name", this.lastName));
            updateParameters.add(new BasicNameValuePair("email", this.email));
            updateParameters.add(new BasicNameValuePair("medical_centre_address", this.medical_centre_address));
            updateParameters.add(new BasicNameValuePair("medical_centre_city", this.medical_centre_city));
            updateParameters.add(new BasicNameValuePair("medical_centre_county", this.medical_centre_county));
            updateParameters.add(new BasicNameValuePair("medical_centre_post_code", this.medical_centre_post_code));
            updateParameters.add(new BasicNameValuePair("medical_centre_phone_number", this.medical_centre_phone_number));
            updateParameters.add(new BasicNameValuePair("medical_centre_website", this.medical_centre_website));
            updateParameters.add(new BasicNameValuePair("medical_centre_consultation_costs", this.medical_centre_consultation_costs));

            return updateParameters;
        }

        protected void onPostExecute(String file_url) {

            progressDialog.dismiss();

            if (updateSuccess == 1) {

                try {
                    if (spinnerDefaultValue(sNewNameTitleGP, "Title")) {
                        Toast.makeText(OptionGPAccountSettings.this, "Please select a title", Toast.LENGTH_SHORT).show();
                    } else if (spinnerDefaultValue(sNewMedicalCentreCountyGP, "County")) {
                        Toast.makeText(OptionGPAccountSettings.this, "Please select a county", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent loginIntent = new Intent("com.app.gptalk.main.LOGIN");
                        startActivity(loginIntent);
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
    }
}