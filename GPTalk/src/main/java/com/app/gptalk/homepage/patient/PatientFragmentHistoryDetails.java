package com.app.gptalk.homepage.patient;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.gptalk.base.BaseClass;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.R;
import com.app.gptalk.database.PatientDatabaseHelper;

import java.io.InputStream;

public class PatientFragmentHistoryDetails extends Fragment {

    private View view;
    private ImageView ivProfilePhoto;
    private Bitmap profileBitmap = null;
    private Typeface titleFont, titleDetailsFont, patientDetailsFont;
    private TextView tvGPConsultationTitle, tvMainSymptomTitle,
            tvOtherSymptomsTitle, tvMainAllergyTitle, tvOtherAllergiesTitle, tvBookingDateTitle, tvBookingTimeTitle, tvExplanationTitle,
            tvName, tvMainSymptom, tvOtherSymptoms, tvMainAllergy, tvOtherAllergies, tvBookingDate, tvBookingTime, tvStatus, tvExplanation;

    private String phone, bookingDate, bookingTime, bookingStatus;

    private BaseClass baseClass = new BaseClass();

    public PatientFragmentHistoryDetails(String newPhone, String newBookingDate, String newBookingTime, String newBookingStatus) {

        this.phone = newPhone;
        this.bookingDate = newBookingDate;
        this.bookingTime = newBookingTime;
        this.bookingStatus = newBookingStatus;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        view = inflater.inflate(R.layout.patient_history_consultation_details, container, false);

        initialiseTitleWidgets(view);
        initialiseInputWidgets(view);

        setFontStyles();
        setTitleFont();
        setInputFont();

        retrieveConsultationHistoryDetails();

        return view;
    }

    private void initialiseTitleWidgets(View view) {

        tvGPConsultationTitle = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsTitle);
        tvMainSymptomTitle = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsMainSymptomTitle);
        tvOtherSymptomsTitle = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsOtherSymptomsTitle);
        tvMainAllergyTitle = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsAllergyTitle);
        tvOtherAllergiesTitle = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsOtherAllergiesTitle);
        tvBookingDateTitle = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsBookingDateTitle);
        tvBookingTimeTitle = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsBookingTimeTitle);
        tvExplanationTitle = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsExplanationTitle);

        ivProfilePhoto = (ImageView) view.findViewById(R.id.ivRequestPatientHistoryConsultationDetailsProfile);
    }

    private void initialiseInputWidgets(View view) {

        tvName = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsName);
        tvMainSymptom = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsMainSymptom);
        tvOtherSymptoms = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsOtherSymptoms);
        tvMainAllergy = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsAllergy);
        tvOtherAllergies = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsOtherAllergies);
        tvBookingDate = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsBookingDate);
        tvBookingTime = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsBookingTime);
        tvStatus = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsStatus);
        tvExplanation = (TextView) view.findViewById(R.id.tvPatientHistoryConsultationDetailsExplanation);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(PatientFragmentHistoryDetails.this.getActivity().getAssets(), "Roboto-Thin.ttf");
        titleDetailsFont = Typeface.createFromAsset(PatientFragmentHistoryDetails.this.getActivity().getAssets(), "RobotoCondensed-Bold.ttf");
        patientDetailsFont = Typeface.createFromAsset(PatientFragmentHistoryDetails.this.getActivity().getAssets(), "Sanseriffic.otf");
    }

    private void setTitleFont() {

        tvGPConsultationTitle.setTypeface(titleFont);
        tvMainSymptomTitle.setTypeface(titleDetailsFont);
        tvOtherSymptomsTitle.setTypeface(titleDetailsFont);
        tvMainAllergyTitle.setTypeface(titleDetailsFont);
        tvOtherAllergiesTitle.setTypeface(titleDetailsFont);
        tvBookingDateTitle.setTypeface(titleDetailsFont);
        tvBookingTimeTitle.setTypeface(titleDetailsFont);
        tvExplanationTitle.setTypeface(titleDetailsFont);
    }

    private void setInputFont() {

        tvName.setTypeface(patientDetailsFont);
        tvMainSymptom.setTypeface(patientDetailsFont);
        tvOtherSymptoms.setTypeface(patientDetailsFont);
        tvMainAllergy.setTypeface(patientDetailsFont);
        tvOtherAllergies.setTypeface(patientDetailsFont);
        tvBookingDate.setTypeface(patientDetailsFont);
        tvBookingTime.setTypeface(patientDetailsFont);
        tvStatus.setTypeface(patientDetailsFont);
        tvExplanation.setTypeface(patientDetailsFont);
    }

    private void retrieveConsultationHistoryDetails() {

        PatientDatabaseHelper dbHelper = new PatientDatabaseHelper(PatientFragmentHistoryDetails.this.getActivity());

        // Retrieve all individual details of a selected GP
        String gpProfileURL = dbHelper.getConsultationHistoryInformation(bookingDate, bookingTime, bookingStatus, dbHelper.KEY_GP_PROFILE);
        String gpTitle = dbHelper.getConsultationHistoryInformation(bookingDate, bookingTime, bookingStatus, dbHelper.KEY_GP_TITLE);
        String gpFirstName = dbHelper.getConsultationHistoryInformation(bookingDate, bookingTime, bookingStatus, dbHelper.KEY_GP_FIRST_NAME);
        String gpLastName = dbHelper.getConsultationHistoryInformation(bookingDate, bookingTime, bookingStatus, dbHelper.KEY_GP_LAST_NAME);
        String gpSymptom = dbHelper.getConsultationHistoryInformation(bookingDate, bookingTime, bookingStatus, dbHelper.KEY_SYMPTOM);
        String gpOtherSymptoms = dbHelper.getConsultationHistoryInformation(bookingDate, bookingTime, bookingStatus, dbHelper.KEY_OTHER_SYMPTOMS);
        String gpAllergy = dbHelper.getConsultationHistoryInformation(bookingDate, bookingTime, bookingStatus, dbHelper.KEY_ALLERGY);
        String gpOtherAllergies = dbHelper.getConsultationHistoryInformation(bookingDate, bookingTime, bookingStatus, dbHelper.KEY_OTHER_ALLERGIES);
        String gpExplanation = dbHelper.getConsultationHistoryInformation(bookingDate, bookingTime, bookingStatus, dbHelper.KEY_EXPLANATION);

        dbHelper.close();

        tvName.setText(gpTitle + " " + gpFirstName + " " + gpLastName);
        tvMainSymptom.setText(gpSymptom);
        tvMainAllergy.setText(gpAllergy);
        tvBookingDate.setText(bookingDate);
        tvBookingTime.setText(bookingTime);
        tvStatus.setText(bookingStatus);
        tvOtherSymptoms.setText(gpOtherSymptoms);
        tvOtherAllergies.setText(gpOtherAllergies);
        tvExplanation.setText(gpExplanation);

        createStatusStyle(tvStatus);

        // If no input was provided when request was made, set text as N/A
        checkDetailExists(tvOtherSymptoms);
        checkDetailExists(tvOtherAllergies);
        checkDetailExists(tvExplanation);

        new RetrieveIndividualGPSelectedHistoryDetails(gpProfileURL).execute();
    }

    // Assign appropriate background resource to corresponding status
    private void createStatusStyle(TextView textview) {

        if (textview.getText().toString().equals("confirmed")) {
            textview.setBackgroundResource(R.drawable.status_confirmed);
        } else if (textview.getText().toString().equals("unconfirmed")) {
            textview.setBackgroundResource(R.drawable.status_unconfirmed);
        } else if (textview.getText().toString().equals("rejected")) {
            textview.setBackgroundResource(R.drawable.status_rejected);
        }
    }

    // Check if value exists for this column
    private void checkDetailExists(TextView textview) {

        if(textview == null || textview.getText().equals("") || textview.getText().equals(" ")) {
            textview.setText("N/A");
        }
    }

    public class RetrieveIndividualGPSelectedHistoryDetails extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;
        private String profileURL;

        public RetrieveIndividualGPSelectedHistoryDetails(String gpProfileURL) {

            profileURL = gpProfileURL;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(PatientFragmentHistoryDetails.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving History Details...");
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
}
