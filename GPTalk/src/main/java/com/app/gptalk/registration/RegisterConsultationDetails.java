package com.app.gptalk.registration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.app.gptalk.base.BaseClass;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class RegisterConsultationDetails extends AsyncTask<Void, Void, String> {

    private FragmentActivity activity;
    private ProgressDialog progressDialog;
    private String bookingReference, patientUsername, patientProfile, patientFirstName, patientLastName, patientDOBDay, patientDOBMonth, patientDOBYear, patientGender,
                   patientNationality, patientEmail, patientSymptom, patientOtherSymptoms, patientAllergies, patientOtherAllergies, bookingDate, bookingTime, gpPhoneNumber;

    private BaseClass baseClass = new BaseClass();
    private HashMap<String, String> consultationDetails = new HashMap<String, String>();

    public RegisterConsultationDetails(FragmentActivity activity, HashMap<String, String> consultationDetails) {

        this.activity = activity;
        this.consultationDetails = consultationDetails;
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(activity);
        baseClass.initialiseProgressDialog(progressDialog, "Storing Consultation Details...");
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {

        extractConsultationDetails();
        storeConsultationDetails();

        return "";
    }

    private void extractConsultationDetails() {

        bookingReference = consultationDetails.get("booking_reference");
        patientUsername = consultationDetails.get("patient_username");
        patientProfile = consultationDetails.get("patient_profile");
        patientFirstName = consultationDetails.get("patient_first_name");
        patientLastName = consultationDetails.get("patient_last_name");
        patientDOBDay = consultationDetails.get("patient_dob_day");
        patientDOBMonth = consultationDetails.get("patient_dob_month");
        patientDOBYear = consultationDetails.get("patient_dob_year");
        patientGender = consultationDetails.get("patient_gender");
        patientNationality = consultationDetails.get("patient_nationality");
        patientEmail = consultationDetails.get("patient_email");
        patientSymptom = consultationDetails.get("patient_symptom");
        patientOtherSymptoms = consultationDetails.get("patient_other_symptoms");
        patientAllergies = consultationDetails.get("patient_allergies");
        patientOtherAllergies = consultationDetails.get("patient_other_allergies");
        bookingDate = consultationDetails.get("booking_date");
        bookingTime = consultationDetails.get("booking_time");
        gpPhoneNumber = consultationDetails.get("gp_phone_number");
    }

    // Replace all spaces in a string with '+'
    private String reformatWhiteSpace(String sentence) {

        return sentence.replaceAll(" ", "%20");
    }

    private void storeConsultationDetails() {

        URI url = null;

        try {
            url = new URI("http://www.gptalk.ie/WAMP/gcm_server_php/store_consultation_details.php"
                                + "?booking_reference=" + bookingReference
                                + "&patient_username=" + patientUsername
                                + "&patient_profile=" + patientProfile
                                + "&patient_first_name=" + patientFirstName
                                + "&patient_last_name=" + patientLastName
                                + "&patient_dob_day=" + patientDOBDay
                                + "&patient_dob_month=" + patientDOBMonth
                                + "&patient_dob_year=" + patientDOBYear
                                + "&patient_gender=" + patientGender
                                + "&patient_nationality=" + reformatWhiteSpace(patientNationality)
                                + "&patient_email=" + patientEmail
                                + "&patient_symptom=" + reformatWhiteSpace(patientSymptom)
                                + "&patient_other_symptoms=" + reformatWhiteSpace(patientOtherSymptoms)
                                + "&patient_allergies=" + reformatWhiteSpace(patientAllergies)
                                + "&patient_other_allergies=" + reformatWhiteSpace(patientOtherAllergies)
                                + "&booking_date=" + bookingDate
                                + "&booking_time=" + bookingTime
                                + "&gp_phone_number=" + gpPhoneNumber);
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
    protected void onPostExecute(String result) {

        super.onPostExecute(result);
        progressDialog.dismiss();
    }
}

