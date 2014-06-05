package com.app.gptalk.homepage.patient;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.gptalk.base.JSONParser;
import com.app.gptalk.R;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.database.PatientDatabaseHelper;
import com.app.gptalk.homepage.both.FragmentGPAvailableTimes;
import com.app.gptalk.homepage.gp.GPFragmentScheduleDetails;
import com.app.gptalk.registration.RegisterConsultationDetails;
import com.app.gptalk.base.BaseClass;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class PatientFragmentNewBookingsDetails extends Fragment implements View.OnClickListener {

    private View view;
    private Button bSendRequest;
    private Spinner sSymptom, sAllergies;
    private EditText etOtherSymptom, etOtherAllergies;
    private Typeface titleFont, titleDetailsFont, detailsFont;
    private TextView tvBasicDetailsTitle, tvSymptomAllergyDetailsTitle, tvConsultationDetailsTitle,
                     tvBasicFirstNameTitle, tvBasicLastNameTitle, tvBasicDOBTitle, tvBasicGenderTitle, tvBasicNationalityTitle,
                     tvBasicFirstName, tvBasicLastName, tvBasicDOBDay, tvBasicDOBMonth, tvBasicDOBYear, tvBasicGender, tvBasicNationality,
                     tvSymptomTitle, tvOtherSymptomTitle, tvAllergiesTitle, tvOtherAllergiesTitle,
                     tvGPTitle, tvGPNameTitle, tvGPFirstName, tvGPLastName, tvDateTitle, tvTimeTitle, tvTime, tvDate, tvConsultationCostTitle, tvConsultationCost;

    private int day, month, year, hour, minute;
    private String patientProfile, patientEmail, gpUsername, gpProfile, gpTitle, gpFirstName, gpLastName, gpAddress, gpCity, gpCounty,
                   gpPostCode, gpPhoneNumber, gpConsultationCost;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();
    private Calendar calendar = Calendar.getInstance();

    public String patientUsername, selectedTime, selectedDate;

    // A random characterised string is generated and acts as an appointment reference
    // This is used for the retrieval of the requested consultation details when received by the GP
    private String appointmentReference = generateRandomAppointmentReference();

    private HashMap<String, String> bookingTimeDetails = new HashMap<String, String>();
    private HashMap<String, String> consultationDetails = new HashMap<String, String>();
    private HashMap<String, String> gpDetails = new HashMap<String, String>();
    private HashMap<String, String> databaseAppointmentDetails = new HashMap<String, String>();

    private final static String ACCOUNT_URL = "http://www.gptalk.ie/gptalkie_registered_users/patient_account_details.php";

    public PatientFragmentNewBookingsDetails(HashMap<String, String> newDetails, String selectedTime, String selectedDate) {

        this.gpDetails = newDetails;
        this.selectedTime = selectedTime;
        this.selectedDate = selectedDate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }

        view = inflater.inflate(R.layout.new_bookings_details, container, false);
        setHasOptionsMenu(true);

        extractGPValues(gpDetails);
        initialiseTitleWidgets(view);
        initialiseInputWidgets(view);
        initialiseClickListeners();

        setFontStyles();
        setSectionFont();
        setTitleFont();
        setInputFont();

        if (selectedTime.equals("0")) {
            setCurrentTimeOnView();
        } else {
            tvTime.setText(selectedTime);

            if (selectedDate.equals("0")) {
                setCurrentDateOnly();
            } else {
                tvDate.setText(selectedDate);
            }
        }
        try {
            new RetrievePatientBasicDetails().execute();
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }

        return view;
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

    private void extractGPValues(HashMap<String, String> hashValues) {

        Iterator<String> keyIterator = gpDetails.keySet().iterator();

        while (keyIterator.hasNext()) {

            String key = keyIterator.next();

            if (key.equals("gp_username")) {
                this.gpUsername = hashValues.get(key);
            }  else if (key.equals("gp_profile")) {
                this.gpProfile = hashValues.get(key);
            } else if (key.equals("gp_title")) {
                this.gpTitle = hashValues.get(key);
            } else if (key.equals("gp_first_name")) {
                this.gpFirstName = hashValues.get(key);
            } else if (key.equals("gp_last_name")) {
                this.gpLastName = hashValues.get(key);
            } else if (key.equals("gp_address")) {
                this.gpAddress = hashValues.get(key);
            } else if (key.equals("gp_city")) {
                this.gpCity = hashValues.get(key);
            } else if (key.equals("gp_county")) {
                this.gpCounty = hashValues.get(key);
            } else if (key.equals("gp_post_code")) {
                this.gpPostCode = hashValues.get(key);
            } else if (key.equals("gp_phone_number")) {
                this.gpPhoneNumber = hashValues.get(key);
            } else if (key.equals("gp_consultation_cost")) {
                this.gpConsultationCost = hashValues.get(key);
            }
        }
    }

    private void initialiseClickListeners() {

        tvDate.setOnClickListener(PatientFragmentNewBookingsDetails.this);
        tvTime.setOnClickListener(PatientFragmentNewBookingsDetails.this);
        bSendRequest.setOnClickListener(PatientFragmentNewBookingsDetails.this);
    }

    private void retrieveConsultationDetails() {

        consultationDetails.put("booking_reference", appointmentReference);
        consultationDetails.put("patient_username", patientUsername);
        consultationDetails.put("patient_profile", patientProfile);
        consultationDetails.put("patient_first_name", tvBasicFirstName.getText().toString());
        consultationDetails.put("patient_last_name", tvBasicLastName.getText().toString());
        consultationDetails.put("patient_dob_day", tvBasicDOBDay.getText().toString());
        consultationDetails.put("patient_dob_month", tvBasicDOBMonth.getText().toString());
        consultationDetails.put("patient_dob_year", tvBasicDOBYear.getText().toString());
        consultationDetails.put("patient_gender", tvBasicGender.getText().toString());
        consultationDetails.put("patient_nationality", tvBasicNationality.getText().toString());
        consultationDetails.put("patient_email", patientEmail);
        consultationDetails.put("patient_symptom", sSymptom.getSelectedItem().toString());
        consultationDetails.put("patient_other_symptoms", etOtherSymptom.getText().toString());
        consultationDetails.put("patient_allergies", sAllergies.getSelectedItem().toString());
        consultationDetails.put("patient_other_allergies", etOtherAllergies.getText().toString());
        consultationDetails.put("booking_date", tvDate.getText().toString());
        consultationDetails.put("booking_time", tvTime.getText().toString());
        consultationDetails.put("gp_phone_number", gpPhoneNumber);

        // Store all details of the requested consultation in a remote database, which will be used for retrieval purposes
        new RegisterConsultationDetails(PatientFragmentNewBookingsDetails.this.getActivity(), consultationDetails).execute();
    }

    private void populateDatabaseAppointmentDetails() {

        databaseAppointmentDetails.put("reference_code", appointmentReference);
        databaseAppointmentDetails.put("status", baseClass.unconfirmedStatus);
        databaseAppointmentDetails.put("main_symptom", sSymptom.getSelectedItem().toString());
        databaseAppointmentDetails.put("other_symptoms", etOtherSymptom.getText().toString());
        databaseAppointmentDetails.put("main_allergy", sAllergies.getSelectedItem().toString());
        databaseAppointmentDetails.put("other_allergies", etOtherAllergies.getText().toString());
        databaseAppointmentDetails.put("gp_profile", gpProfile);
        databaseAppointmentDetails.put("gp_title", gpTitle);
        databaseAppointmentDetails.put("gp_first_name", gpFirstName);
        databaseAppointmentDetails.put("gp_last_name", gpLastName);
        databaseAppointmentDetails.put("gp_address", gpAddress);
        databaseAppointmentDetails.put("gp_city", gpCity);
        databaseAppointmentDetails.put("gp_county", gpCounty);
        databaseAppointmentDetails.put("gp_post_code", gpPostCode);
        databaseAppointmentDetails.put("gp_phone_number", gpPhoneNumber);
        databaseAppointmentDetails.put("booking_date", tvDate.getText().toString());
        databaseAppointmentDetails.put("booking_time", tvTime.getText().toString());
        databaseAppointmentDetails.put("explanation", " ");
        databaseAppointmentDetails.put("update_counter", "1");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tvBookingDetailsDate:

                // The user is prompted to select a consultation date of their choosing
                createDatePickerDialog();
                break;

            case R.id.tvBookingDetailsTime:

                // The user is prompted to select a consultation time of their choosing
                FragmentManager fragmentManager = getFragmentManager();
                FragmentGPAvailableTimes gpSessionsDetails = new FragmentGPAvailableTimes("PatientFragmentNewBookingsDetails", gpPhoneNumber, tvDate.getText().toString(), gpDetails);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.fragmentBookingDetails, gpSessionsDetails);

                fragmentTransaction.commit();
                break;

            case R.id.bMakeRequest:

                retrieveConsultationDetails();
                storeBookingTimeDetails(baseClass.unconfirmedStatus);
                populateDatabaseAppointmentDetails();

                if (sSymptom.getSelectedItem().toString().equals("Symptoms")) {
                    Toast.makeText(PatientFragmentNewBookingsDetails.this.getActivity(), "Please select a symptom", Toast.LENGTH_LONG).show();
                } else if (sAllergies.getSelectedItem().toString().equals("Allergies")) {
                    Toast.makeText(PatientFragmentNewBookingsDetails.this.getActivity(), "Please select an allergy", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        // Send a push notification to the GP with the consultation reference code attached
                        new notificationSender().execute();

                        PatientDatabaseHelper dbHelper = new PatientDatabaseHelper(PatientFragmentNewBookingsDetails.this.getActivity());

                        try {

                            // Automatically insert consultation as unconfirmed into internal database
                            if (!dbHelper.consultationExists(gpPhoneNumber)) {
                                dbHelper.createAppointment(databaseAppointmentDetails);
                            }

                            dbHelper.close();
                        } catch (Exception e) {
                            baseClass.errorHandler(e);
                        }
                    } catch (Exception e) {
                        baseClass.errorHandler(e);
                    }
                }
                break;
        }
    }

    private void storeBookingTimeDetails(String status) {

        bookingTimeDetails.put("booking_date", tvDate.getText().toString());
        bookingTimeDetails.put("booking_time", tvTime.getText().toString());
        bookingTimeDetails.put("booking_status", status);
        bookingTimeDetails.put("gp_phone", gpPhoneNumber);

        // Store all details of the requested consultation in a remote database, which will be used for retrieval purposes
        new RegisterGPAvailableTimes(PatientFragmentNewBookingsDetails.this.getActivity(), bookingTimeDetails).execute();
    }

    private String generateRandomAppointmentReference() {

        Random random = new Random();

        // Exclude 'p' for notification differences to distinguish difference between reply and request
        String alphabet = "abcdefghijklmnoqrstuvwxyz";

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

    private void createDatePickerDialog() {

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(PatientFragmentNewBookingsDetails.this.getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int dateYear, int dateMonth, int dateDay) {

                String complete="";

                // Ensures all characters of a date are concatenated correctly in the form of DD/MM/YYYY
                if (((int) Math.log10(dateDay) + 1) <= 1) {
                   complete = "0" + dateDay + "/" + (dateMonth + 1) + "/" + dateYear;

                   if (((int) Math.log10(dateMonth) + 1) <= 1) {
                       complete = "0" + dateDay + "/" + "0" + (dateMonth + 1) + "/" + dateYear;
                   }
               } else {
                    if (((int) Math.log10(dateMonth) + 1) <= 1) {
                        complete = dateDay + "/" + "0" + (dateMonth + 1) + "/" + dateYear;
                    } else {
                        complete = dateDay + "/" + (dateMonth + 1) + "/" + dateYear;
                    }
                }
               tvDate.setText(complete);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void setCurrentTimeOnView() {

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        int minutes = today.minute;

        String timeMinute = "";

        if (minutes > 0 && minutes <= 15) {
            timeMinute = "15";
        } else if (minutes > 15 && minutes <= 30) {
            timeMinute = "30";
        } else if (minutes > 30 && minutes <= 45) {
            timeMinute = "45";
        } else if (minutes > 45 && minutes <= 0) {
            timeMinute = "00";
        }

        tvDate.setText(today.format("%d/%m/%Y"));
        tvTime.setText(today.format("%H:" + timeMinute));
    }

    private void setCurrentDateOnly() {

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        tvDate.setText(today.format("%d/%m/%Y"));
    }

    private void setSectionFont() {

        tvBasicDetailsTitle.setTypeface(titleFont);
        tvSymptomAllergyDetailsTitle.setTypeface(titleFont);
        tvConsultationDetailsTitle.setTypeface(titleFont);
    }

    private void setInputFont() {

        tvBasicFirstName.setTypeface(detailsFont);
        tvBasicLastName.setTypeface(detailsFont);
        tvBasicDOBDay.setTypeface(detailsFont);
        tvBasicDOBMonth.setTypeface(detailsFont);
        tvBasicDOBYear.setTypeface(detailsFont);
        tvBasicGender.setTypeface(detailsFont);
        tvBasicNationality.setTypeface(detailsFont);
        tvGPNameTitle.setTypeface(detailsFont);
        tvGPFirstName.setTypeface(detailsFont);
        tvGPLastName.setTypeface(detailsFont);
        tvConsultationCost.setTypeface(detailsFont);
        tvDate.setTypeface(detailsFont, Typeface.ITALIC);
        tvTime.setTypeface(detailsFont, Typeface.ITALIC);

        etOtherSymptom.setTypeface(detailsFont);
        etOtherAllergies.setTypeface(detailsFont);
    }

    private void setTitleFont() {

        tvBasicFirstNameTitle.setTypeface(titleDetailsFont);
        tvBasicLastNameTitle.setTypeface(titleDetailsFont);
        tvBasicDOBTitle.setTypeface(titleDetailsFont);
        tvBasicGenderTitle.setTypeface(titleDetailsFont);
        tvBasicNationalityTitle.setTypeface(titleDetailsFont);
        tvSymptomTitle.setTypeface(titleDetailsFont);
        tvOtherSymptomTitle.setTypeface(titleDetailsFont);
        tvAllergiesTitle.setTypeface(titleDetailsFont);
        tvOtherAllergiesTitle.setTypeface(titleDetailsFont);
        tvGPTitle.setTypeface(titleDetailsFont);
        tvDateTitle.setTypeface(titleDetailsFont);
        tvTimeTitle.setTypeface(titleDetailsFont);
        tvConsultationCostTitle.setTypeface(titleDetailsFont);
    }

    private void setFontStyles() {

        titleFont = Typeface.createFromAsset(this.getActivity().getAssets(), "Roboto-ThinItalic.ttf");
        titleDetailsFont = Typeface.createFromAsset(this.getActivity().getAssets(), "RobotoCondensed-Bold.ttf");
        detailsFont = Typeface.createFromAsset(this.getActivity().getAssets(), "Sanseriffic.otf");
    }

    private void initialiseInputWidgets(View view) {

        tvBasicFirstName = (TextView) view.findViewById(R.id.tvBookingDetailsFirstName);
        tvBasicLastName = (TextView) view.findViewById(R.id.tvBookingDetailsLastName);
        tvBasicDOBDay = (TextView) view.findViewById(R.id.tvBookingDetailsDOBDay);
        tvBasicDOBMonth = (TextView) view.findViewById(R.id.tvBookingDetailsDOBMonth);
        tvBasicDOBYear = (TextView) view.findViewById(R.id.tvBookingDetailsDOBYear);
        tvBasicGender = (TextView) view.findViewById(R.id.tvBookingDetailsGender);
        tvBasicNationality = (TextView) view.findViewById(R.id.tvBookingDetailsNationality);
        tvGPNameTitle = (TextView) view.findViewById(R.id.tvBookingDetailsGPNameTitle);
        tvGPFirstName = (TextView) view.findViewById(R.id.tvBookingDetailsGPFirstName);
        tvGPLastName = (TextView) view.findViewById(R.id.tvBookingDetailsGPLastName);
        tvConsultationCost = (TextView) view.findViewById(R.id.tvBookingDetailsConsultationCost);
        tvDate = (TextView) view.findViewById(R.id.tvBookingDetailsDate);
        tvTime = (TextView) view.findViewById(R.id.tvBookingDetailsTime);

        etOtherSymptom = (EditText) view.findViewById(R.id.etBookingDetailsOtherSymptoms);
        etOtherAllergies = (EditText) view.findViewById(R.id.etBookingDetailsOtherAllergies);

        sSymptom = (Spinner) view.findViewById(R.id.sBookingDetailsSymptomType);
        sAllergies = (Spinner) view.findViewById(R.id.sBookingDetailsAllergiesType);

        tvDate.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvTime.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    private void initialiseTitleWidgets(View view) {

        tvBasicDetailsTitle = (TextView) view.findViewById(R.id.tvBookingDetailsBasicTitle);
        tvSymptomAllergyDetailsTitle = (TextView) view.findViewById(R.id.tvBookingDetailsSymptomDetailTitle);
        tvConsultationDetailsTitle = (TextView) view.findViewById(R.id.tvBookingDetailsConsultationTitle);

        tvBasicFirstNameTitle = (TextView) view.findViewById(R.id.tvBookingDetailsFirstNameTitle);
        tvBasicLastNameTitle = (TextView) view.findViewById(R.id.tvBookingDetailsLastNameTitle);
        tvBasicDOBTitle = (TextView) view.findViewById(R.id.tvBookingDetailsDOBTitle);
        tvBasicGenderTitle = (TextView) view.findViewById(R.id.tvBookingDetailsGenderTitle);
        tvBasicNationalityTitle = (TextView) view.findViewById(R.id.tvBookingDetailsNationalityTitle);
        tvSymptomTitle = (TextView) view.findViewById(R.id.tvBookingDetailsSymptomTitle);
        tvOtherSymptomTitle = (TextView) view.findViewById(R.id.tvBookingDetailsOtherSymptomTitle);
        tvAllergiesTitle = (TextView) view.findViewById(R.id.tvBookingDetailsAllergiesTitle);
        tvOtherAllergiesTitle = (TextView) view.findViewById(R.id.tvBookingDetailsOtherAllergiesTitle);
        tvGPTitle = (TextView) view.findViewById(R.id.tvBookingDetailsGPTitle);
        tvDateTitle = (TextView) view.findViewById(R.id.tvBookingDetailsDateTitle);
        tvTimeTitle = (TextView) view.findViewById(R.id.tvBookingDetailsTimeTitle);
        tvConsultationCostTitle = (TextView) view.findViewById(R.id.tvBookingDetailsConsultationCostTitle);

        bSendRequest = (Button) view.findViewById(R.id.bMakeRequest);
    }

    public class RetrievePatientBasicDetails extends AsyncTask<String, String, String> {

        private int loginSuccess;
        private String dbFirstName, dbLastName, dbDay, dbMonth, dbYear, dbGender, dbNationality;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(PatientFragmentNewBookingsDetails.this.getActivity());
            baseClass.initialiseProgressDialog(progressDialog, "Retrieving Patient Details...");
        }

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> patientCredentials = new ArrayList<NameValuePair>();

            patientUsername = baseClass.retrieveUsername(null, PatientFragmentNewBookingsDetails.this.getActivity());

            // Retrieve patient details from remote database via their username
            patientCredentials.add(new BasicNameValuePair("username", patientUsername));

            try {
                JSONObject json = jsonParser.makeHttpRequest(ACCOUNT_URL, "POST", patientCredentials);
                loginSuccess = json.getInt("success");

                if (loginSuccess == 1) {

                    retrieveJSONValues(json);
                    return null;
                } else {
                    return null;
                }
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }

            return null;
        }

        private void retrieveJSONValues(JSONObject json) {

            try {
                patientProfile = json.getString("profile");
                dbFirstName = json.getString("first_name");
                dbLastName = json.getString("last_name");
                dbDay = json.getString("day");
                dbMonth = json.getString("month");
                dbYear = json.getString("year");
                dbGender = json.getString("gender");
                dbNationality = json.getString("nationality");
                patientEmail = json.getString("email");
            } catch (JSONException e) {
                baseClass.errorHandler(e);
            }
        }

        protected void onPostExecute(String fileURL) {

            progressDialog.dismiss();
            setPatientDetails();
            setGPDetails();
        }

        private void setGPDetails() {

            tvGPNameTitle.setText(PatientFragmentNewBookingsDetails.this.gpTitle);
            tvGPFirstName.setText(PatientFragmentNewBookingsDetails.this.gpFirstName);
            tvGPLastName.setText(PatientFragmentNewBookingsDetails.this.gpLastName);
            tvConsultationCost.setText(PatientFragmentNewBookingsDetails.this.gpConsultationCost);
        }

        private void setPatientDetails() {

            tvBasicFirstName.setText(dbFirstName);
            tvBasicLastName.setText(dbLastName);
            tvBasicDOBDay.setText(dbDay);
            tvBasicDOBMonth.setText(dbMonth);
            tvBasicDOBYear.setText(dbYear);
            tvBasicGender.setText(dbGender);
            tvBasicNationality.setText(dbNationality);
        }
    }

    public class notificationSender extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

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

            String username = baseClass.retrieveUsername(null, PatientFragmentNewBookingsDetails.this.getActivity());

            Intent userHomepageIntent = new Intent("com.app.gptalk.homepage.patient.PATIENTHOMEPAGE");
            userHomepageIntent.putExtra("username", username);
            startActivity(userHomepageIntent);
        }
    }
}