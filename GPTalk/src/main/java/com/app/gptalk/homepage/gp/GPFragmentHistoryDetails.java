package com.app.gptalk.homepage.gp;

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

import java.io.InputStream;

public class GPFragmentHistoryDetails extends Fragment {

    private ImageView ivProfilePhoto;
    private Bitmap profileBitmap = null;
    private Typeface titleFont, titleDetailsFont, patientDetailsFont;
    private TextView tvPatientConsultationTitle, tvMainSymptomTitle,
            tvOtherSymptomsTitle, tvMainAllergyTitle, tvOtherAllergiesTitle, tvBookingDateTitle, tvBookingTimeTitle, tvExplanationTitle,
            tvFirstName, tvLastName, tvMainSymptom, tvOtherSymptoms, tvMainAllergy, tvOtherAllergies, tvBookingDate, tvBookingTime, tvStatus, tvExplanation;

    private String email, bookingDate, bookingTime, bookingStatus;

    private BaseClass baseClass = new BaseClass();

    public GPFragmentHistoryDetails(String newEmail, String newBookingDate, String newBookingTime, String newBookingStatus) {

        this.email = newEmail;
        this.bookingDate = newBookingDate;
        this.bookingTime = newBookingTime;
        this.bookingStatus = newBookingStatus;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gp_history_consultation_details, container, false);

        initialiseTitleWidgets(view);
        initialiseInputWidgets(view);

        setFontStyles();
        setTitleFont();
        setInputFont();

        retrieveConsultationHistoryDetails();

        return view;
    }

    private void initialiseTitleWidgets(View view) {

        tvPatientConsultationTitle = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsTitle);
        tvMainSymptomTitle = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsSymptomTitle);
        tvOtherSymptomsTitle = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsOtherSymptomsTitle);
        tvMainAllergyTitle = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsAllergyTitle);
        tvOtherAllergiesTitle = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsOtherAllergiesTitle);
        tvBookingDateTitle = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsBookingDateTitle);
        tvBookingTimeTitle = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsBookingTimeTitle);
        tvExplanationTitle = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsExplanationTitle);

        ivProfilePhoto = (ImageView) view.findViewById(R.id.ivRequestGPHistoryConsultationDetailsProfile);
    }

    private void initialiseInputWidgets(View view) {

        tvFirstName = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsFirstName);
        tvLastName = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsLastName);
        tvMainSymptom = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsSymptom);
        tvOtherSymptoms = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsOtherSymptoms);
        tvMainAllergy = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsAllergy);
        tvOtherAllergies = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsOtherAllergies);
        tvBookingDate = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsBookingDate);
        tvBookingTime = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsBookingTime);
        tvStatus = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsStatus);
        tvExplanation = (TextView) view.findViewById(R.id.tvGPHistoryConsultationDetailsExplanation);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(GPFragmentHistoryDetails.this.getActivity().getAssets(), "Roboto-Thin.ttf");
        titleDetailsFont = Typeface.createFromAsset(GPFragmentHistoryDetails.this.getActivity().getAssets(), "RobotoCondensed-Bold.ttf");
        patientDetailsFont = Typeface.createFromAsset(GPFragmentHistoryDetails.this.getActivity().getAssets(), "Sanseriffic.otf");
    }

    private void setTitleFont() {

        tvPatientConsultationTitle.setTypeface(titleFont);
        tvMainSymptomTitle.setTypeface(titleDetailsFont);
        tvOtherSymptomsTitle.setTypeface(titleDetailsFont);
        tvMainAllergyTitle.setTypeface(titleDetailsFont);
        tvOtherAllergiesTitle.setTypeface(titleDetailsFont);
        tvBookingDateTitle.setTypeface(titleDetailsFont);
        tvBookingTimeTitle.setTypeface(titleDetailsFont);
        tvExplanation.setTypeface(titleDetailsFont);
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
        tvExplanation.setTypeface(patientDetailsFont);
    }

    private void retrieveConsultationHistoryDetails() {

        GPDatabaseHelper dbHelper = new GPDatabaseHelper(GPFragmentHistoryDetails.this.getActivity());

        // Retrieve all individual details of a selected patient
        String patientProfileURL = dbHelper.getConsultationHistoryInformation(email, bookingDate, bookingTime, bookingStatus, dbHelper.KEY_PROFILE);
        String patientFirstName = dbHelper.getConsultationHistoryInformation(email, bookingDate, bookingTime, bookingStatus, dbHelper.KEY_FIRST_NAME);
        String patientLastName = dbHelper.getConsultationHistoryInformation(email, bookingDate, bookingTime, bookingStatus, dbHelper.KEY_LAST_NAME);
        String patientSymptom = dbHelper.getConsultationHistoryInformation(email, bookingDate, bookingTime, bookingStatus, dbHelper.KEY_SYMPTOM);
        String patientOtherSymptoms = dbHelper.getConsultationHistoryInformation(email, bookingDate, bookingTime, bookingStatus, dbHelper.KEY_OTHER_SYMPTOMS);
        String patientAllergy = dbHelper.getConsultationHistoryInformation(email, bookingDate, bookingTime, bookingStatus, dbHelper.KEY_ALLERGY);
        String patientOtherAllergies = dbHelper.getConsultationHistoryInformation(email, bookingDate, bookingTime, bookingStatus, dbHelper.KEY_OTHER_ALLERGIES);
        String patientExplanation = dbHelper.getConsultationHistoryInformation(email, bookingDate, bookingTime, bookingStatus, dbHelper.KEY_EXPLANATION);

        dbHelper.close();

        tvFirstName.setText(patientFirstName);
        tvLastName.setText(patientLastName);
        tvMainSymptom.setText(patientSymptom);
        tvMainAllergy.setText(patientAllergy);
        tvBookingDate.setText(bookingDate);
        tvBookingTime.setText(bookingTime);
        tvStatus.setText(bookingStatus);
        tvOtherSymptoms.setText(patientOtherSymptoms);
        tvOtherAllergies.setText(patientOtherAllergies);
        tvExplanation.setText(patientExplanation);

        createStatusStyle(tvStatus);

        // If no input was provided when request was made, set text as N/A
        checkDetailExists(tvOtherSymptoms);
        checkDetailExists(tvOtherAllergies);
        checkDetailExists(tvExplanation);

        new RetrieveIndividualPatientSelectedHistoryDetails(patientProfileURL).execute();
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

    public class RetrieveIndividualPatientSelectedHistoryDetails extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;
        private String profileURL;

        public RetrieveIndividualPatientSelectedHistoryDetails(String patientProfileURL) {

            profileURL = patientProfileURL;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(GPFragmentHistoryDetails.this.getActivity());
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
