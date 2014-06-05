package com.app.gptalk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.gptalk.base.BaseClass;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PatientDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "patientappointmentdb";

    private static final String DATABASE_CONSULTATION_TABLE = "patientConsultationTable";
    private static final String DATABASE_CONSULTATION_HISTORY_TABLE = "patientConsultationHistoryTable";
    private static final int DATABASE_VERSION = 1;

    public static final String KEY_ROWID = "_id";
    public static final String KEY_REFERENCE_CODE = "reference_code";
    public static final String KEY_STATUS = "status";
    public static final String KEY_SYMPTOM = "symptom";
    public static final String KEY_OTHER_SYMPTOMS = "other_symptoms";
    public static final String KEY_ALLERGY = "allergy";
    public static final String KEY_OTHER_ALLERGIES = "other_allergies";
    public static final String KEY_GP_PROFILE = "gp_profile";
    public static final String KEY_GP_TITLE = "gp_title";
    public static final String KEY_GP_FIRST_NAME = "gp_first_name";
    public static final String KEY_GP_LAST_NAME = "gp_last_name";
    public static final String KEY_GP_ADDRESS = "gp_address";
    public static final String KEY_GP_CITY = "gp_city";
    public static final String KEY_GP_COUNTY = "gp_county";
    public static final String KEY_GP_POST_CODE = "gp_post_code";
    public static final String KEY_GP_PHONE_NUMBER = "gp_phone_number";
    public static final String KEY_BOOKING_DATE = "booking_date";
    public static final String KEY_BOOKING_TIME = "booking_time";
    public static final String KEY_EXPLANATION = "explanation";
    public static final String KEY_UPDATE_COUNTER = "update_counter";

    // A list of all existing columns in Consultation table of SQLite database
    public String[] columns = new String[] { KEY_ROWID, KEY_REFERENCE_CODE, KEY_STATUS,
            KEY_SYMPTOM, KEY_OTHER_SYMPTOMS, KEY_ALLERGY, KEY_OTHER_ALLERGIES, KEY_GP_PROFILE,
            KEY_GP_TITLE, KEY_GP_FIRST_NAME, KEY_GP_LAST_NAME, KEY_GP_ADDRESS, KEY_GP_CITY,
            KEY_GP_COUNTY, KEY_GP_POST_CODE, KEY_GP_PHONE_NUMBER, KEY_BOOKING_DATE,
            KEY_BOOKING_TIME, KEY_EXPLANATION, KEY_UPDATE_COUNTER };

    // A list of all existing columns of all bookings made by users in Consultation History table of SQLite database
    public String[] historyColumns = new String[] { KEY_ROWID, KEY_REFERENCE_CODE, KEY_STATUS,
            KEY_SYMPTOM, KEY_OTHER_SYMPTOMS, KEY_ALLERGY, KEY_OTHER_ALLERGIES, KEY_GP_PROFILE,
            KEY_GP_TITLE, KEY_GP_FIRST_NAME, KEY_GP_LAST_NAME, KEY_GP_PHONE_NUMBER,
            KEY_BOOKING_DATE, KEY_BOOKING_TIME, KEY_EXPLANATION, KEY_UPDATE_COUNTER };

    // The creation of all columns in Consultation table
    public static final String CREATE_CONSULTATION_TABLE = "CREATE TABLE " + DATABASE_CONSULTATION_TABLE + " (" +
            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_REFERENCE_CODE + " TEXT NOT NULL, " +
            KEY_STATUS + " TEXT NOT NULL, " +
            KEY_SYMPTOM + " TEXT NOT NULL, " +
            KEY_OTHER_SYMPTOMS + " TEXT, " +
            KEY_ALLERGY + " TEXT NOT NULL, " +
            KEY_OTHER_ALLERGIES + " TEXT, " +
            KEY_GP_PROFILE + " TEXT, " +
            KEY_GP_TITLE + " TEXT NOT NULL, " +
            KEY_GP_FIRST_NAME + " TEXT NOT NULL, " +
            KEY_GP_LAST_NAME + " TEXT NOT NULL, " +
            KEY_GP_ADDRESS + " TEXT NOT NULL, " +
            KEY_GP_CITY + " TEXT NOT NULL, " +
            KEY_GP_COUNTY + " TEXT NOT NULL, " +
            KEY_GP_POST_CODE + " TEXT NOT NULL, " +
            KEY_GP_PHONE_NUMBER + " TEXT NOT NULL, " +
            KEY_BOOKING_DATE + " TEXT NOT NULL, " +
            KEY_BOOKING_TIME + " TEXT NOT NULL, " +
            KEY_EXPLANATION + " TEXT, " +
            KEY_UPDATE_COUNTER + " TEXT);";

    // The creation of all columns in Consultation History table
    public static final String CREATE_CONSULTATION_HISTORY_TABLE = "CREATE TABLE " + DATABASE_CONSULTATION_HISTORY_TABLE + " (" +
            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_REFERENCE_CODE + " TEXT NOT NULL, " +
            KEY_STATUS + " TEXT NOT NULL, " +
            KEY_SYMPTOM + " TEXT NOT NULL, " +
            KEY_OTHER_SYMPTOMS + " TEXT, " +
            KEY_ALLERGY + " TEXT NOT NULL, " +
            KEY_OTHER_ALLERGIES + " TEXT, " +
            KEY_GP_PROFILE + " TEXT, " +
            KEY_GP_TITLE + " TEXT NOT NULL, " +
            KEY_GP_FIRST_NAME + " TEXT NOT NULL, " +
            KEY_GP_LAST_NAME + " TEXT NOT NULL, " +
            KEY_GP_PHONE_NUMBER + " TEXT NOT NULL, " +
            KEY_BOOKING_DATE + " TEXT NOT NULL, " +
            KEY_BOOKING_TIME + " TEXT NOT NULL, " +
            KEY_EXPLANATION + " TEXT, " +
            KEY_UPDATE_COUNTER + " TEXT);";

    public static final String INSERT_CONSULTATION_HISTORY_TABLE_COMMAND = "INSERT INTO " + DATABASE_CONSULTATION_HISTORY_TABLE + " (" +
            KEY_REFERENCE_CODE + ", " +
            KEY_STATUS + ", " +
            KEY_SYMPTOM + ", " +
            KEY_OTHER_SYMPTOMS + ", " +
            KEY_ALLERGY + ", " +
            KEY_OTHER_ALLERGIES + ", " +
            KEY_GP_PROFILE + ", " +
            KEY_GP_TITLE + ", " +
            KEY_GP_FIRST_NAME + ", " +
            KEY_GP_LAST_NAME + ", " +
            KEY_GP_PHONE_NUMBER + ", " +
            KEY_BOOKING_DATE + ", " +
            KEY_BOOKING_TIME + ", " +
            KEY_EXPLANATION + ", " +
            KEY_UPDATE_COUNTER + ")" +
            " SELECT " + KEY_REFERENCE_CODE + ", " + KEY_STATUS + ", " + KEY_SYMPTOM + ", " + KEY_OTHER_SYMPTOMS + ", " + KEY_ALLERGY + ", " + KEY_OTHER_ALLERGIES + ", " +
            KEY_GP_PROFILE + ", " + KEY_GP_TITLE + ", " + KEY_GP_FIRST_NAME + ", " + KEY_GP_LAST_NAME + ", " + KEY_GP_PHONE_NUMBER + ", " + KEY_BOOKING_DATE + ", " + KEY_BOOKING_TIME + ", " +
            KEY_EXPLANATION + ", " + KEY_UPDATE_COUNTER +
            " FROM " + DATABASE_CONSULTATION_TABLE;

    public PatientDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create database tables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_CONSULTATION_TABLE);
        sqLiteDatabase.execSQL(CREATE_CONSULTATION_HISTORY_TABLE);
    }

    // Drop tables if they were previously created in previous version
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_CONSULTATION_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_CONSULTATION_HISTORY_TABLE);

        onCreate(sqLiteDatabase);
    }

    // Verify if consultation exists in Consultation table, defined by a reference code
    public boolean consultationExists(String reference) {

        SQLiteDatabase database = this.getReadableDatabase();

        String sql = "SELECT * FROM " + DATABASE_CONSULTATION_TABLE + " WHERE " + KEY_REFERENCE_CODE + "='" + reference + "'";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    // Verify if a consultation with a corresponding reference code has been previously stored
    public boolean bookingReferenceExists(String referenceCode) {

        SQLiteDatabase database = this.getReadableDatabase();

        String sql = "SELECT * FROM " + DATABASE_CONSULTATION_TABLE + " WHERE " + KEY_REFERENCE_CODE + "='" + referenceCode + "'";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    // Create an appointment in Consultation Table and insert duplicate in Consultation History table
    public void createAppointment(HashMap<String, String> consultationDetails) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues currentValues = new ContentValues();
        ContentValues historyValues = new ContentValues();

        for (Map.Entry<String,String> entry : consultationDetails.entrySet()) {

            getCurrentConsultationValues(currentValues, entry);
            getConsultationHistoryValues(historyValues, entry);
        }

        database.insert(DATABASE_CONSULTATION_TABLE, null, currentValues);
        database.insert(DATABASE_CONSULTATION_HISTORY_TABLE, null, historyValues);
    }

    // Insert relevant consultation values in Consultation History table
    private void getConsultationHistoryValues(ContentValues historyValues, Map.Entry<String, String> entry) {

        insertContentValues(historyValues, entry, "reference_code", KEY_REFERENCE_CODE);
        insertContentValues(historyValues, entry, "status", KEY_STATUS);
        insertContentValues(historyValues, entry, "main_symptom", KEY_SYMPTOM);
        insertContentValues(historyValues, entry, "other_symptoms", KEY_OTHER_SYMPTOMS);
        insertContentValues(historyValues, entry, "main_allergy", KEY_ALLERGY);
        insertContentValues(historyValues, entry, "other_allergies", KEY_OTHER_ALLERGIES);
        insertContentValues(historyValues, entry, "gp_profile", KEY_GP_PROFILE);
        insertContentValues(historyValues, entry, "gp_title", KEY_GP_TITLE);
        insertContentValues(historyValues, entry, "gp_first_name", KEY_GP_FIRST_NAME);
        insertContentValues(historyValues, entry, "gp_last_name", KEY_GP_LAST_NAME);
        insertContentValues(historyValues, entry, "gp_phone_number", KEY_GP_PHONE_NUMBER);
        insertContentValues(historyValues, entry, "booking_date", KEY_BOOKING_DATE);
        insertContentValues(historyValues, entry, "booking_time", KEY_BOOKING_TIME);
        insertContentValues(historyValues, entry, "explanation", KEY_EXPLANATION);
        insertContentValues(historyValues, entry, "update_counter", KEY_UPDATE_COUNTER);
    }

    // Insert relevant consultation values in Consultation table
    private void getCurrentConsultationValues(ContentValues currentValues, Map.Entry<String, String> entry) {

        insertContentValues(currentValues, entry, "reference_code", KEY_REFERENCE_CODE);
        insertContentValues(currentValues, entry, "status", KEY_STATUS);
        insertContentValues(currentValues, entry, "main_symptom", KEY_SYMPTOM);
        insertContentValues(currentValues, entry, "other_symptoms", KEY_OTHER_SYMPTOMS);
        insertContentValues(currentValues, entry, "main_allergy", KEY_ALLERGY);
        insertContentValues(currentValues, entry, "other_allergies", KEY_OTHER_ALLERGIES);
        insertContentValues(currentValues, entry, "gp_profile", KEY_GP_PROFILE);
        insertContentValues(currentValues, entry, "gp_title", KEY_GP_TITLE);
        insertContentValues(currentValues, entry, "gp_first_name", KEY_GP_FIRST_NAME);
        insertContentValues(currentValues, entry, "gp_last_name", KEY_GP_LAST_NAME);
        insertContentValues(currentValues, entry, "gp_address", KEY_GP_ADDRESS);
        insertContentValues(currentValues, entry, "gp_city", KEY_GP_CITY);
        insertContentValues(currentValues, entry, "gp_county", KEY_GP_COUNTY);
        insertContentValues(currentValues, entry, "gp_post_code", KEY_GP_POST_CODE);
        insertContentValues(currentValues, entry, "gp_phone_number", KEY_GP_PHONE_NUMBER);
        insertContentValues(currentValues, entry, "booking_date", KEY_BOOKING_DATE);
        insertContentValues(currentValues, entry, "booking_time", KEY_BOOKING_TIME);
        insertContentValues(currentValues, entry, "explanation", KEY_EXPLANATION);
        insertContentValues(currentValues, entry, "update_counter", KEY_UPDATE_COUNTER);
    }

    private void insertContentValues(ContentValues contentValues, Map.Entry<String, String> entry, String value, String columnTitle) {

        if (entry.getKey().equals(value)) {
            contentValues.put(columnTitle, entry.getValue());
        }
    }

    // Get value of most recent updated consultation in Consultation table via specified reference code
    private int getUpdateCount(String reference) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_TABLE, columns, KEY_REFERENCE_CODE + "=?", new String[] { reference }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return Integer.parseInt(cursor.getString(getColumnID(KEY_UPDATE_COUNTER)));
        } else {
            return -1;
        }
    }

    // Get value of a selected column in Consultation table
    public String getConsultationDetail(int rowID, String status, String columnTitle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + DATABASE_CONSULTATION_TABLE + " WHERE " + KEY_ROWID + "=" + rowID + " AND " + KEY_STATUS + "=?", new String[]{status}, null);

        if (cursor.moveToFirst()) {
            return cursor.getString(getColumnID(columnTitle));
        }

        return " ";
    }

    // Get value of a selected column in Consultation table
    public String getConsultationHistoryDetail(int count, String phoneNumber, String columnTitle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_HISTORY_TABLE, historyColumns, KEY_UPDATE_COUNTER + "=" + count + " AND " + KEY_GP_PHONE_NUMBER + "=?", new String[] { phoneNumber }, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getString(getHistoryColumnID(columnTitle));
        }

        return null;
    }

    // Retrieve consultation details in Consultation History table for schedule purposes via a specified input
    public String getConsultationHistoryInformation(String bookingDate, String bookingTime, String bookingStatus, String columnTitle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_HISTORY_TABLE, historyColumns, KEY_STATUS + "=?" + " AND " + KEY_BOOKING_DATE + "=?" + " AND " + KEY_BOOKING_TIME + "=?", new String[] { bookingStatus, bookingDate, bookingTime }, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getString(getHistoryColumnID(columnTitle));
        }

        return null;
    }

    // Update latest consultation in Consultation table and insert duplicate in Consultation History table
    public void updateStatus(String updateStatus, String referenceCode) {

        SQLiteDatabase database = this.getWritableDatabase();
        int updateNumber = getUpdateCount(referenceCode) + 1;

        database.execSQL("UPDATE " + DATABASE_CONSULTATION_TABLE + " SET " + KEY_STATUS + "='" + updateStatus + "', " + KEY_UPDATE_COUNTER + "='" + Integer.toString(updateNumber) + "' WHERE " + KEY_REFERENCE_CODE + "='" + referenceCode + "'");
        database.execSQL(INSERT_CONSULTATION_HISTORY_TABLE_COMMAND + " WHERE " + KEY_REFERENCE_CODE + "='" + referenceCode + "'");
    }

    // Update latest consultation in Consultation table for scheduling purposes and insert duplicate in Consultation History table
    public void updateGPScheduleConsultation(String status, String bookingDate, String bookingTime, String explanation, String phoneNumber) {

        SQLiteDatabase database = this.getWritableDatabase();
        int updateNumber = getUpdateCount(phoneNumber) + 1;

        database.execSQL("UPDATE " + DATABASE_CONSULTATION_TABLE + " SET " + KEY_STATUS + "='" + status + "', " + KEY_BOOKING_DATE + "='" + bookingDate + "', " + KEY_BOOKING_TIME + "='" + bookingTime + "', " + KEY_EXPLANATION + "='" + explanation + "', " + KEY_UPDATE_COUNTER + "='" + Integer.toString(updateNumber) + "' WHERE " + KEY_GP_PHONE_NUMBER + "='" + phoneNumber + "'");
        database.execSQL(INSERT_CONSULTATION_HISTORY_TABLE_COMMAND + " WHERE " + KEY_GP_PHONE_NUMBER + "='" + phoneNumber + "'");
    }

    // Get last row number of a consultation in Consultation table via a specified status
    public int getLastRowId(String status) {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT " + KEY_ROWID + " FROM " + DATABASE_CONSULTATION_TABLE + " WHERE " + KEY_STATUS + "='" + status + "' ORDER BY " + KEY_ROWID + " DESC LIMIT 1";

        Cursor cursor = database.rawQuery(query, null);
        int count = 0;

        if (cursor != null && cursor.moveToFirst()) {

            count = cursor.getInt(0);
            cursor.close();
            return count;
        } else {
            return count;
        }
    }

    // Get update count of a consultation in Consultation History table via a specified GP phone number
    public int getLatestHistoryCount(String phoneNumber) {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT " + KEY_UPDATE_COUNTER + " FROM " + DATABASE_CONSULTATION_HISTORY_TABLE + " WHERE " + KEY_GP_PHONE_NUMBER + "='" + phoneNumber + "' ORDER BY " + KEY_UPDATE_COUNTER + " DESC LIMIT 1";

        Cursor cursor = database.rawQuery(query, null);
        int count = 0;

        if (cursor != null && cursor.moveToFirst()) {

            count = cursor.getInt(0);
            cursor.close();
            return count;
        } else {
            return count;
        }
    }

    // Get total number of existing rows in Consultation table
    public int getRowCount() {

        SQLiteDatabase database = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + DATABASE_CONSULTATION_TABLE;

        Cursor cursor = database.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    // Retrieve a GP's detail via a specified status and date of the stored in Consultation table
    public String getGPBioDetail(long rowID, String status, String date, String columnTitle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_TABLE, columns, KEY_ROWID + "=" + rowID + " AND " + KEY_STATUS + "=?" + " AND " + KEY_BOOKING_DATE + "=?", new String[] {status, date}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getString(getColumnID(columnTitle));
        }

        return "";
    }

    // Retrieve a GP's details if specified GP phone number is not currently stored in Consultation table
    public String getIndividualGPDetails(long rowID, ArrayList<String> gpPhoneNumbers, String columnTitle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_TABLE, columns, KEY_ROWID + "=" + rowID, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            if (!gpPhoneNumbers.contains(cursor.getString(getColumnID(KEY_GP_PHONE_NUMBER)))) {
                return cursor.getString(getColumnID(columnTitle));
            }
        }

        return null;
    }

    // Retrieve consultation details in Consultation table for schedule purposes via a specified input
    public String getScheduleConsultationInformation(String phoneNumber, String status, String bookingDate, String columnTitle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_TABLE, columns, KEY_GP_PHONE_NUMBER + "=?" + " AND " + KEY_STATUS + "=?" + " AND " + KEY_BOOKING_DATE + "=?", new String[] {phoneNumber, status, bookingDate}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getString(getColumnID(columnTitle));
        }

        return null;
    }

    // Retrieve ID of column in Consultation table via specified column title
    private int getColumnID(String columnTitle) {

        for (int index = 0; index < columns.length; index++) {

            if (columns[index].equals(columnTitle)) {
                return index;
            }
        }
        return -1;
    }

    // Retrieve ID of column in Consultation History table via specified column title
    private int getHistoryColumnID(String columnTitle) {

        for (int index = 0; index < historyColumns.length; index++) {

            if (historyColumns[index].equals(columnTitle)) {
                return index;
            }
        }
        return -1;
    }

    // Delete a consultation specified by a patient's email
    public void deleteEntry(String phoneNumber) throws SQLException {

        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(DATABASE_CONSULTATION_TABLE, KEY_GP_PHONE_NUMBER + "=?", new String [] {phoneNumber});
    }

    // Close connections to database
    public void close() {

        SQLiteDatabase database = this.getReadableDatabase();

        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}