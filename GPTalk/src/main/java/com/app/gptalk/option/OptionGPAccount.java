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
import android.widget.Toast;

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

public class OptionGPAccount extends Activity implements View.OnClickListener {

    private String username, userType;
    private Button bProfileSettings;
    private ImageView ivProfilePhoto;
    private Typeface titleFont, titleDetailsFont, gpDetailsFont;
    private TextView tvAccountTitle, tvProfileTitle, tvUsernameTitle, tvNameTitle, tvFirstNameTitle, tvLastNameTitle, tvEmailTitle,
                     tvMedicalCentreAddressTitle, tvMedicalCentreCityTitle, tvMedicalCentreCountyTitle, tvMedicalCentrePostCodeTitle,
                     tvMedicalCentrePhoneNumberTitle, tvMedicalCentreWebsiteTitle, tvMedicalCentreConsultationCostTitle, tvUsernameGP, tvTitleNameGP,
                     tvFirstNameGP, tvLastNameGP, tvEmailGP, tvMedicalCentreAddress, tvMedicalCentreCity, tvMedicalCentreCounty,
                     tvMedicalCentrePostCode, tvMedicalCentrePhoneNumber, tvMedicalCentreWebsite, tvMedicalCentreConsultationCost;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();
    private HashMap<String, String> gpDetails = new HashMap<String, String>();

    private final static String GP_ACCOUNT_URL = "http://www.gptalk.ie/gptalkie_registered_users/gp_account_details.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_gp_account);

        getUsername();

        initialiseTitleWidgets();
        initialiseInputWidgets();

        setFontStyles();
        setTitleFont();
        setInputFont();

        try {
            new RetrieveGPDetails().execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    private void getUsername() {

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userType = intent.getStringExtra("user_type");
    }

    private void initialiseTitleWidgets() {

        tvAccountTitle = (TextView) findViewById(R.id.tvGPAccountTitle);
        tvProfileTitle = (TextView) findViewById(R.id.tvGPProfileTitle);
        tvUsernameTitle = (TextView) findViewById(R.id.tvGPUsernameTitle);
        tvNameTitle = (TextView) findViewById(R.id.tvGPTitleNameTitle);
        tvFirstNameTitle = (TextView) findViewById(R.id.tvGPFirstNameTitle);
        tvLastNameTitle = (TextView) findViewById(R.id.tvGPLastNameTitle);
        tvEmailTitle = (TextView) findViewById(R.id.tvGPEmailTitle);
        tvMedicalCentreAddressTitle = (TextView) findViewById(R.id.tvGPMedicalCentreAddressTitle);
        tvMedicalCentreCityTitle = (TextView) findViewById(R.id.tvGPMedicalCentreCityTitle);
        tvMedicalCentreCountyTitle = (TextView) findViewById(R.id.tvGPMedicalCentreCountyTitle);
        tvMedicalCentrePostCodeTitle = (TextView) findViewById(R.id.tvGPMedicalCentrePostCodeTitle);
        tvMedicalCentrePhoneNumberTitle = (TextView) findViewById(R.id.tvGPMedicalCentrePhoneNumberTitle);
        tvMedicalCentreWebsiteTitle = (TextView) findViewById(R.id.tvGPMedicalCentreWebsiteTitle);
        tvMedicalCentreConsultationCostTitle = (TextView) findViewById(R.id.tvGPConsultationCostTitle);

        ivProfilePhoto = (ImageView) findViewById(R.id.ivGPProfilePhoto);
        bProfileSettings = (Button) findViewById(R.id.bGPProfileSettings);

        bProfileSettings.setOnClickListener(OptionGPAccount.this);
    }

    private void initialiseInputWidgets() {

        tvUsernameGP = (TextView) findViewById(R.id.tvGPUsername);
        tvTitleNameGP = (TextView) findViewById(R.id.tvGPNameTitle);
        tvFirstNameGP = (TextView) findViewById(R.id.tvGPFirstName);
        tvLastNameGP = (TextView) findViewById(R.id.tvGPLastName);
        tvEmailGP = (TextView) findViewById(R.id.tvGPEmail);
        tvMedicalCentreAddress = (TextView) findViewById(R.id.tvGPMedicalCentreAddress);
        tvMedicalCentreCity = (TextView) findViewById(R.id.tvGPMedicalCentreCity);
        tvMedicalCentreCounty = (TextView) findViewById(R.id.tvGPMedicalCentreCounty);
        tvMedicalCentrePostCode = (TextView) findViewById(R.id.tvGPMedicalCentrePostCode);
        tvMedicalCentrePhoneNumber = (TextView) findViewById(R.id.tvGPMedicalCentrePhoneNumber);
        tvMedicalCentreWebsite = (TextView) findViewById(R.id.tvGPMedicalCentreWebsite);
        tvMedicalCentreConsultationCost = (TextView) findViewById(R.id.tvGPConsultationCost);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(this.getAssets(), "Roboto-Thin.ttf");
        titleDetailsFont = Typeface.createFromAsset(this.getAssets(), "RobotoCondensed-Bold.ttf");
        gpDetailsFont = Typeface.createFromAsset(this.getAssets(), "Sanseriffic.otf");
    }

    private void setTitleFont() {

        tvAccountTitle.setTypeface(titleFont);
        tvProfileTitle.setTypeface(titleDetailsFont);
        tvUsernameTitle.setTypeface(titleDetailsFont);
        tvNameTitle.setTypeface(titleDetailsFont);
        tvFirstNameTitle.setTypeface(titleDetailsFont);
        tvLastNameTitle.setTypeface(titleDetailsFont);
        tvEmailTitle.setTypeface(titleDetailsFont);
        tvMedicalCentreAddressTitle.setTypeface(titleDetailsFont);
        tvMedicalCentreCityTitle.setTypeface(titleDetailsFont);
        tvMedicalCentreCountyTitle.setTypeface(titleDetailsFont);
        tvMedicalCentrePostCodeTitle.setTypeface(titleDetailsFont);
        tvMedicalCentrePhoneNumberTitle.setTypeface(titleDetailsFont);
        tvMedicalCentreWebsiteTitle.setTypeface(titleDetailsFont);
        tvMedicalCentreConsultationCostTitle.setTypeface(titleDetailsFont);
    }

    private void setInputFont() {

        tvUsernameGP.setTypeface(gpDetailsFont);
        tvTitleNameGP.setTypeface(gpDetailsFont);
        tvFirstNameGP.setTypeface(gpDetailsFont);
        tvLastNameGP.setTypeface(gpDetailsFont);
        tvEmailGP.setTypeface(gpDetailsFont);
        tvMedicalCentreAddress.setTypeface(gpDetailsFont);
        tvMedicalCentreCity.setTypeface(gpDetailsFont);
        tvMedicalCentreCounty.setTypeface(gpDetailsFont);
        tvMedicalCentrePostCode.setTypeface(gpDetailsFont);
        tvMedicalCentrePhoneNumber.setTypeface(gpDetailsFont);
        tvMedicalCentreWebsite.setTypeface(gpDetailsFont);
        tvMedicalCentreConsultationCost.setTypeface(gpDetailsFont);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bGPProfileSettings:
                try {
                    Intent accountSettingsIntent = new Intent("com.app.gptalk.option.OPTIONGPACCOUNTSETTINGS");
                    accountSettingsIntent.putExtra("gpDetails", gpDetails);
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

    public class RetrieveGPDetails extends AsyncTask<String, String, String> {

        private int success;
        private String dbUsername, dbGPID, dbProfileURL, dbTitle, dbFirstName, dbLastName, dbEmail, dbMedicalCentreAddress,
                dbMedicalCentreCity, dbMedicalCentreCounty, dbMedicalCentrePostCode, dbMedicalCentrePhoneNumber,
                dbMedicalCentreWebsite, dbMedicalCentreConsultationCost;
        private ProgressDialog progressDialog;
        private Bitmap profilePhotoBitmap = null;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(OptionGPAccount.this);
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving GP Details...");
        }

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> gpCredentials = new ArrayList<NameValuePair>();

            dbUsername = username;
            gpCredentials.add(new BasicNameValuePair("username", dbUsername));

            try {
                JSONObject json = jsonParser.makeHttpRequest(GP_ACCOUNT_URL, "POST", gpCredentials);
                success = json.getInt("success");

                if (success == 1) {

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
                dbGPID = json.getString("gp_id");
                dbProfileURL = json.getString("profile");
                dbTitle = json.getString("title");
                dbFirstName = json.getString("first_name");
                dbLastName = json.getString("last_name");
                dbEmail = json.getString("email");
                dbMedicalCentreAddress = json.getString("medical_centre_address");
                dbMedicalCentreCity = json.getString("medical_centre_city");
                dbMedicalCentreCounty = json.getString("medical_centre_county");
                dbMedicalCentrePostCode = json.getString("medical_centre_post_code");
                dbMedicalCentrePhoneNumber = json.getString("medical_centre_phone_number");
                dbMedicalCentreWebsite = json.getString("medical_centre_website");
                dbMedicalCentreConsultationCost = "€" + json.getString("medical_centre_consultation_costs");
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }
        }

        protected void onPostExecute(String fileURL) {

            progressDialog.dismiss();

            setGPDetails();
            insertGPDetails();
        }

        // Set displayed texts with retrieved values
        private void setGPDetails() {

            ivProfilePhoto.setImageBitmap(profilePhotoBitmap);
            tvUsernameGP.setText(this.dbUsername);
            tvTitleNameGP.setText(dbTitle);
            tvFirstNameGP.setText(dbFirstName);
            tvLastNameGP.setText(dbLastName);
            tvEmailGP.setText(dbEmail);
            tvMedicalCentreAddress.setText(dbMedicalCentreAddress);
            tvMedicalCentreCity.setText(dbMedicalCentreCity);
            tvMedicalCentreCounty.setText(dbMedicalCentreCounty);
            tvMedicalCentrePostCode.setText(dbMedicalCentrePostCode);
            tvMedicalCentrePhoneNumber.setText(dbMedicalCentrePhoneNumber);
            tvMedicalCentreWebsite.setText(dbMedicalCentreWebsite);
            tvMedicalCentreConsultationCost.setText(dbMedicalCentreConsultationCost);
        }

        // Insert retrieved details into hashmap, to be used as edittext hints in next intent
        private void insertGPDetails() {

            gpDetails.put("gp_id", dbGPID);
            gpDetails.put("username", this.dbUsername);
            gpDetails.put("title", dbTitle);
            gpDetails.put("first_name", dbFirstName);
            gpDetails.put("last_name", dbLastName);
            gpDetails.put("email", dbEmail);
            gpDetails.put("medical_centre_address", dbMedicalCentreAddress);
            gpDetails.put("medical_centre_city", dbMedicalCentreCity);
            gpDetails.put("medical_centre_county", dbMedicalCentreCounty);
            gpDetails.put("medical_centre_post_code", dbMedicalCentrePostCode);
            gpDetails.put("medical_centre_phone_number", dbMedicalCentrePhoneNumber);
            gpDetails.put("medical_centre_website", dbMedicalCentreWebsite);
            gpDetails.put("medical_centre_consultation_costs", dbMedicalCentreConsultationCost);
        }
    }
}

