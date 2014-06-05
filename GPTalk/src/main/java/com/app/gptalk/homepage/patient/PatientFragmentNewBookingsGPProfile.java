package com.app.gptalk.homepage.patient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.gptalk.base.JSONParser;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatientFragmentNewBookingsGPProfile extends Fragment implements View.OnClickListener {

    private View view;
    private Typeface titleDetailsFont, gpDetailsFont;
    private Button bMakeBooking;
    private ImageView ivProfilePicture;
    private TextView gpAddressTitle, gpWebsiteTitle, gpPhoneNumberTitle, gpConsultationCostTitle,
                     gpTitle, gpFirstName, gpLastName, gpAddress, gpCity, gpCounty, gpPostCode, gpWebsite, gpPhoneNumber, gpConsultationCost;

    private String gpUsername, gpProfile, medical_centre_address;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();

    private HashMap<String, String> completeGPDetails = new HashMap<String, String>();

    private static final String SELECTED_GP_DETAILS_URL = "http://www.gptalk.ie/gptalkie_registered_users/selected_gp_details.php";

    public PatientFragmentNewBookingsGPProfile(String newAddress) {
        this.medical_centre_address = newAddress;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {

            ViewGroup parent = (ViewGroup) view.getParent();

            if (parent != null) {
                parent.removeView(view);
            }
        }

        try {
            view = inflater.inflate(R.layout.new_bookings_gp_profile, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        setHasOptionsMenu(true);

        initialiseTitleWidgets(view);
        initialiseInputWidgets(view);

        setClickListeners();

        setFontStyles();
        setTitleFont();
        setInputFont();

        try {
            new RetrieveGPBookingDetails().execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }

        return view;
    }

    private void setClickListeners() {

        gpWebsite.setOnClickListener(PatientFragmentNewBookingsGPProfile.this);
        gpPhoneNumber.setOnClickListener(PatientFragmentNewBookingsGPProfile.this);
        bMakeBooking.setOnClickListener(PatientFragmentNewBookingsGPProfile.this);
    }

    private void setTitleFont() {

        gpAddressTitle.setTypeface(titleDetailsFont);
        gpWebsiteTitle.setTypeface(titleDetailsFont);
        gpPhoneNumberTitle.setTypeface(titleDetailsFont);
        gpConsultationCostTitle.setTypeface(titleDetailsFont);
    }

    private void setInputFont() {
        gpTitle.setTypeface(gpDetailsFont);
        gpFirstName.setTypeface(gpDetailsFont);
        gpLastName.setTypeface(gpDetailsFont);
        gpAddress.setTypeface(gpDetailsFont);
        gpCity.setTypeface(gpDetailsFont);
        gpCounty.setTypeface(gpDetailsFont);
        gpPostCode.setTypeface(gpDetailsFont);
        gpPhoneNumber.setTypeface(gpDetailsFont, Typeface.ITALIC);
        gpWebsite.setTypeface(gpDetailsFont, Typeface.ITALIC);
        gpConsultationCost.setTypeface(gpDetailsFont);
    }

    private void setFontStyles() {

        titleDetailsFont = Typeface.createFromAsset(this.getActivity().getAssets(), "RobotoCondensed-Bold.ttf");
        gpDetailsFont = Typeface.createFromAsset(this.getActivity().getAssets(), "Sanseriffic.otf");
    }

    private void initialiseTitleWidgets(View view) {

        gpAddressTitle = (TextView) view.findViewById(R.id.tvGPProfileAddressTitle);
        gpWebsiteTitle = (TextView) view.findViewById(R.id.tvGPProfileWebsiteTitle);
        gpPhoneNumberTitle = (TextView) view.findViewById(R.id.tvGPProfilePhoneNumberTitle);
        gpConsultationCostTitle = (TextView) view.findViewById(R.id.tvGPProfileConsultationCostTitle);

        bMakeBooking = (Button) view.findViewById(R.id.bMakeBooking);
    }

    private void initialiseInputWidgets(View view) {

        ivProfilePicture = (ImageView) view.findViewById(R.id.ivGPProfilePhoto);
        gpTitle = (TextView) view.findViewById(R.id.tvGPProfileTitle);
        gpFirstName = (TextView) view.findViewById(R.id.tvGPProfileFirstName);
        gpLastName = (TextView) view.findViewById(R.id.tvGPProfileLastName);
        gpAddress = (TextView) view.findViewById(R.id.tvGPProfileAddress);
        gpCity = (TextView) view.findViewById(R.id.tvGPProfileCity);
        gpCounty = (TextView) view.findViewById(R.id.tvGPProfileCounty);
        gpPostCode = (TextView) view.findViewById(R.id.tvGPProfilePostCode);
        gpWebsite = (TextView) view.findViewById(R.id.tvGPProfileWebsite);
        gpPhoneNumber = (TextView) view.findViewById(R.id.tvGPProfilePhoneNumber);
        gpConsultationCost = (TextView) view.findViewById(R.id.tvGPProfileConsultationCost);

        gpWebsite.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        gpPhoneNumber.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    this.getActivity().finish();
                } else {
                    getFragmentManager().popBackStack();
                }
                break;
        }

        return false;
    }

    public void onClick(View v) {

        switch (v.getId()){

            case R.id.tvGPProfilePhoneNumber:

                // If GP's phone number is pressed, prompt user
                AlertDialog.Builder phoneDialogBuilder = new AlertDialog.Builder(PatientFragmentNewBookingsGPProfile.this.getActivity());
                phoneDialogBuilder.setTitle("Phone GP")
                        .setMessage("Are you sure you want to call this GP?")
                        .setCancelable(true)
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            // Launch phone call
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:+" + gpPhoneNumber.getText().toString().trim()));
                            startActivity(callIntent);
                            }
                        })
                        .create()
                        .show();
                break;

            case R.id.tvGPProfileWebsite:

                Intent launchIntent = new Intent("com.app.gptalk.option.OPTIONWEBSITE");
                launchIntent.putExtra("url", "http://" + gpWebsite.getText().toString());
                startActivity(launchIntent);

                break;

            case R.id.bMakeBooking:

                insertGPDetails();

                PatientFragmentNewBookingsDetails newBookingsDetails = new PatientFragmentNewBookingsDetails(completeGPDetails, "0", "0");
                FragmentManager fragmentBookingManager = getFragmentManager();

                FragmentTransaction fragmentBookingTransaction = fragmentBookingManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragmentBookingGPProfile, newBookingsDetails);

                fragmentBookingTransaction.commit();
        }
    }

    private void insertGPDetails() {

        String username = gpUsername;
        String profile = gpProfile;
        String title = (String) gpTitle.getText();
        String firstName = (String) gpFirstName.getText();
        String lastName = (String) gpLastName.getText();
        String address = (String) gpAddress.getText();
        String city = (String) gpCity.getText();
        String county = (String) gpCounty.getText();
        String postCode = (String) gpPostCode.getText();
        String phoneNumber = (String) gpPhoneNumber.getText();
        String consultationCost = (String) gpConsultationCost.getText();

        completeGPDetails.put("gp_username", username);
        completeGPDetails.put("gp_profile", profile);
        completeGPDetails.put("gp_title", title);
        completeGPDetails.put("gp_first_name", firstName);
        completeGPDetails.put("gp_last_name", lastName);
        completeGPDetails.put("gp_address", address);
        completeGPDetails.put("gp_city", city);
        completeGPDetails.put("gp_county", county);
        completeGPDetails.put("gp_post_code", postCode);
        completeGPDetails.put("gp_phone_number", phoneNumber);
        completeGPDetails.put("gp_consultation_cost", consultationCost);
    }

    private class RetrieveGPBookingDetails extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private int loginSuccess;
        private Bitmap profilePhotoBitmap;
        private String dbGPTitle, dbGPFirstName, dbGPLastName, dbGPCity, dbGPCounty, dbGPPostCode, dbGPPhoneNumber, dbGPWebsite, dbGPConsultationCosts;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(PatientFragmentNewBookingsGPProfile.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving GP Details...");
        }

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> gpCredentials = new ArrayList<NameValuePair>();

            // Retrieve all GP's details via their specified unique address
            gpCredentials.add(new BasicNameValuePair("medical_centre_address", PatientFragmentNewBookingsGPProfile.this.medical_centre_address));

            try {
                JSONObject json = jsonParser.makeHttpRequest(SELECTED_GP_DETAILS_URL, "POST", gpCredentials);
                loginSuccess = json.getInt("success");

                if (loginSuccess == 1) {

                    retrieveJSONValues(json);

                    if (gpProfile.length() == 0) {
                        profilePhotoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_picture);

                    } else {

                        String accessPhotoURL = "http://www.gptalk.ie/" + gpProfile;
                        try {
                            InputStream inputStream = new URL(accessPhotoURL).openStream();
                            profilePhotoBitmap = BitmapFactory.decodeStream(inputStream);
                        } catch (Exception e) {
                            baseClass.errorHandler(e);
                        }
                    }

                    return null;
                } else {
                    return null;
                }
            } catch (Exception e) {
                baseClass.errorHandler(e);
            }

            return null;
        }

        private void retrieveJSONValues(JSONObject json) {

            try {
                gpUsername = json.getString("username");
                gpProfile = json.getString("profile");
                dbGPTitle = json.getString("title");
                dbGPFirstName = json.getString("first_name");
                dbGPLastName = json.getString("last_name");
                dbGPCity = json.getString("medical_centre_city");
                dbGPCounty = json.getString("medical_centre_county");
                dbGPPostCode = json.getString("medical_centre_post_code");
                dbGPPhoneNumber = json.getString("medical_centre_phone_number");
                dbGPWebsite = json.getString("medical_centre_website");
                dbGPConsultationCosts = json.getString("medical_centre_consultation_costs");
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }
        }

        protected void onPostExecute(String fileURL) {

            progressDialog.dismiss();
            setGPDetails();
        }

        private void setGPDetails() {

            // The settings of the displayed profile picture bitmap
            ivProfilePicture.setAdjustViewBounds(true);
            ivProfilePicture.setMaxHeight(189);
            ivProfilePicture.setMaxWidth(176);

            ivProfilePicture.setImageBitmap(profilePhotoBitmap);

            gpTitle.setText(dbGPTitle);
            gpFirstName.setText(dbGPFirstName);
            gpLastName.setText(dbGPLastName);
            gpAddress.setText(medical_centre_address + ",");
            gpCity.setText(dbGPCity + ",");
            gpCounty.setText(dbGPCounty);
            gpPostCode.setText(dbGPPostCode);
            gpPhoneNumber.setText(dbGPPhoneNumber);
            gpWebsite.setText(dbGPWebsite);
            gpConsultationCost.setText("€" + dbGPConsultationCosts);
       }
    }
}