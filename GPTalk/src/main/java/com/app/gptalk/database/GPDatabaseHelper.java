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

public class GPDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appointmentdb";

    private static final String DATABASE_CONSULTATION_TABLE = "consultationTable";
    private static final String DATABASE_CONSULTATION_HISTORY_TABLE = "consultationHistoryTable";
    private static final int DATABASE_VERSION = 1;

    public static final String KEY_ROWID = "_id";
    public static final String KEY_STATUS = "status";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE = "profile";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_DAY = "day";
    public static final String KEY_MONTH = "month";
    public static final String KEY_YEAR = "year";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_NATIONALITY = "nationality";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_SYMPTOM = "symptom";
    public static final String KEY_OTHER_SYMPTOMS = "other_symptoms";
    public static final String KEY_ALLERGY = "allergy";
    public static final String KEY_OTHER_ALLERGIES = "other_allergies";
    public static final String KEY_BOOKING_DATE = "booking_date";
    public static final String KEY_BOOKING_TIME = "booking_time";
    public static final String KEY_EXPLANATION = "explanation";
    public static final String KEY_UPDATE_COUNTER = "update_counter";

    // A list of all existing columns in Consultation table of SQLite database
    public String[] columns = new String[] { KEY_ROWID, KEY_STATUS, KEY_USERNAME, KEY_PROFILE, KEY_FIRST_NAME, KEY_LAST_NAME, KEY_DAY,
            KEY_MONTH, KEY_YEAR, KEY_GENDER, KEY_NATIONALITY, KEY_EMAIL, KEY_SYMPTOM,
            KEY_OTHER_SYMPTOMS, KEY_ALLERGY, KEY_OTHER_ALLERGIES, KEY_BOOKING_DATE,
            KEY_BOOKING_TIME, KEY_EXPLANATION, KEY_UPDATE_COUNTER };

    // A list of all existing columns of all bookings made by users in Consultation History table of SQLite database
    public String[] historyColumns = new String[] { KEY_ROWID, KEY_STATUS, KEY_USERNAME, KEY_PROFILE, KEY_FIRST_NAME, KEY_LAST_NAME,
            KEY_EMAIL, KEY_SYMPTOM, KEY_OTHER_SYMPTOMS, KEY_ALLERGY, KEY_OTHER_ALLERGIES,
            KEY_BOOKING_DATE, KEY_BOOKING_TIME, KEY_EXPLANATION, KEY_UPDATE_COUNTER };

    // The creation of all columns in Consultation table
    public static final String CREATE_CONSULTATION_TABLE = "CREATE TABLE " + DATABASE_CONSULTATION_TABLE + " (" +
            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_STATUS + " TEXT NOT NULL, " +
            KEY_USERNAME + " TEXT NOT NULL, " +
            KEY_PROFILE + " TEXT NOT NULL, " +
            KEY_FIRST_NAME + " TEXT NOT NULL, " +
            KEY_LAST_NAME + " TEXT NOT NULL, " +
            KEY_DAY + " TEXT NOT NULL, " +
            KEY_MONTH + " TEXT NOT NULL, " +
            KEY_YEAR + " TEXT NOT NULL, " +
            KEY_GENDER + " TEXT NOT NULL, " +
            KEY_NATIONALITY + " TEXT NOT NULL, " +
            KEY_EMAIL + " TEXT NOT NULL, " +
            KEY_SYMPTOM + " TEXT NOT NULL, " +
            KEY_OTHER_SYMPTOMS + " TEXT, " +
            KEY_ALLERGY + " TEXT NOT NULL, " +
            KEY_OTHER_ALLERGIES + " TEXT, " +
            KEY_BOOKING_DATE + " TEXT NOT NULL, " +
            KEY_BOOKING_TIME + " TEXT NOT NULL, " +
            KEY_EXPLANATION + " TEXT, " +
            KEY_UPDATE_COUNTER + " TEXT);";

    // The creation of all columns in Consultation History table
    public static final String CREATE_CONSULTATION_HISTORY_TABLE = "CREATE TABLE " + DATABASE_CONSULTATION_HISTORY_TABLE + " (" +
            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_STATUS + " TEXT NOT NULL, " +
            KEY_USERNAME + " TEXT NOT NULL, " +
            KEY_PROFILE + " TEXT NOT NULL, " +
            KEY_FIRST_NAME + " TEXT NOT NULL, " +
            KEY_LAST_NAME + " TEXT NOT NULL, " +
            KEY_EMAIL + " TEXT NOT NULL, " +
            KEY_SYMPTOM + " TEXT NOT NULL, " +
            KEY_OTHER_SYMPTOMS + " TEXT, " +
            KEY_ALLERGY + " TEXT NOT NULL, " +
            KEY_OTHER_ALLERGIES + " TEXT, " +
            KEY_BOOKING_DATE + " TEXT NOT NULL, " +
            KEY_BOOKING_TIME + " TEXT NOT NULL, " +
            KEY_EXPLANATION + " TEXT, " +
            KEY_UPDATE_COUNTER + " TEXT);";

    public static final String INSERT_CONSULTATION_HISTORY_TABLE_COMMAND = "INSERT INTO " + DATABASE_CONSULTATION_HISTORY_TABLE + " (" + KEY_STATUS + ", " +
            KEY_USERNAME + ", " +
            KEY_PROFILE + ", " +
            KEY_FIRST_NAME + ", " +
            KEY_LAST_NAME + ", " +
            KEY_EMAIL + ", " +
            KEY_SYMPTOM + ", " +
            KEY_OTHER_SYMPTOMS + ", " +
            KEY_ALLERGY + ", " +
            KEY_OTHER_ALLERGIES + ", " +
            KEY_BOOKING_DATE + ", " +
            KEY_BOOKING_TIME + ", " +
            KEY_EXPLANATION + ", " +
            KEY_UPDATE_COUNTER + ")" +
            " SELECT " + KEY_STATUS + ", " + KEY_USERNAME + ", " + KEY_PROFILE + ", " + KEY_FIRST_NAME + ", " + KEY_LAST_NAME + ", " +
            KEY_EMAIL + ", " + KEY_SYMPTOM + ", " + KEY_OTHER_SYMPTOMS + ", " + KEY_ALLERGY + ", " + KEY_OTHER_ALLERGIES + ", " +
            KEY_BOOKING_DATE + ", " + KEY_BOOKING_TIME + ", " + KEY_EXPLANATION + ", " + KEY_UPDATE_COUNTER +
            " FROM " + DATABASE_CONSULTATION_TABLE;

    public GPDatabaseHelper(Context context) {
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

    // Verify if consultation exists in Consultation table, defined by a patient's unique email address
    public boolean consultationExists(String email) {

        SQLiteDatabase database = this.getReadableDatabase();

        String sql = "SELECT * FROM " + DATABASE_CONSULTATION_TABLE + " WHERE " + KEY_EMAIL + "='" + email + "'";
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

        insertContentValues(historyValues, entry, "status", KEY_STATUS);
        insertContentValues(historyValues, entry, "username", KEY_USERNAME);
        insertContentValues(historyValues, entry, "profile", KEY_PROFILE);
        insertContentValues(historyValues, entry, "first_name", KEY_FIRST_NAME);
        insertContentValues(historyValues, entry, "last_name", KEY_LAST_NAME);
        insertContentValues(historyValues, entry, "email", KEY_EMAIL);
        insertContentValues(historyValues, entry, "main_symptom", KEY_SYMPTOM);
        insertContentValues(historyValues, entry, "other_symptoms", KEY_OTHER_SYMPTOMS);
        insertContentValues(historyValues, entry, "main_allergy", KEY_ALLERGY);
        insertContentValues(historyValues, entry, "other_allergies", KEY_OTHER_ALLERGIES);
        insertContentValues(historyValues, entry, "booking_date", KEY_BOOKING_DATE);
        insertContentValues(historyValues, entry, "booking_time", KEY_BOOKING_TIME);
        insertContentValues(historyValues, entry, "explanation", KEY_EXPLANATION);
        insertContentValues(historyValues, entry, "update_counter", KEY_UPDATE_COUNTER);
    }

    // Insert relevant consultation values in Consultation table
    private void getCurrentConsultationValues(ContentValues currentValues, Map.Entry<String, String> entry) {

        insertContentValues(currentValues, entry, "status", KEY_STATUS);
        insertContentValues(currentValues, entry, "username", KEY_USERNAME);
        insertContentValues(currentValues, entry, "profile", KEY_PROFILE);
        insertContentValues(currentValues, entry, "first_name", KEY_FIRST_NAME);
        insertContentValues(currentValues, entry, "last_name", KEY_LAST_NAME);
        insertContentValues(currentValues, entry, "day", KEY_DAY);
        insertContentValues(currentValues, entry, "month", KEY_MONTH);
        insertContentValues(currentValues, entry, "year", KEY_YEAR);
        insertContentValues(currentValues, entry, "gender", KEY_GENDER);
        insertContentValues(currentValues, entry, "nationality", KEY_NATIONALITY);
        insertContentValues(currentValues, entry, "email", KEY_EMAIL);
        insertContentValues(currentValues, entry, "main_symptom", KEY_SYMPTOM);
        insertContentValues(currentValues, entry, "other_symptoms", KEY_OTHER_SYMPTOMS);
        insertContentValues(currentValues, entry, "main_allergy", KEY_ALLERGY);
        insertContentValues(currentValues, entry, "other_allergies", KEY_OTHER_ALLERGIES);
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

    // Get value of most recent updated consultation in Consultation table via specified patient email
    private int getUpdateCount(String email) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_TABLE, columns, KEY_EMAIL + "=?", new String[] { email }, null, null, null, null);

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
    public String getConsultationHistoryDetail(int count, String email, String columnTitle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_HISTORY_TABLE, historyColumns, KEY_UPDATE_COUNTER + "=" + count + " AND " + KEY_EMAIL + "=?", new String[] { email }, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getString(getHistoryColumnID(columnTitle));
        }

        return null;
    }

    // Update latest consultation in Consultation table and insert duplicate in Consultation History table
    public void updateStatus(String updateStatus, String email) {

        SQLiteDatabase database = this.getWritableDatabase();
        int updateNumber = getUpdateCount(email) + 1;

        database.execSQL("UPDATE " + DATABASE_CONSULTATION_TABLE + " SET " + KEY_STATUS + "='" + updateStatus + "', " + KEY_UPDATE_COUNTER + "='" + Integer.toString(updateNumber) + "' WHERE " + KEY_EMAIL + "='" + email + "'");
        database.execSQL(INSERT_CONSULTATION_HISTORY_TABLE_COMMAND + " WHERE " + KEY_EMAIL + "='" + email + "'");
    }

    // Update latest consultation in Consultation table for scheduling purposes and insert duplicate in Consultation History table
    public void updateGPScheduleConsultation(String status, String bookingDate, String bookingTime, String explanation, String email) {

        SQLiteDatabase database = this.getWritableDatabase();
        int updateNumber = getUpdateCount(email) + 1;

        database.execSQL("UPDATE " + DATABASE_CONSULTATION_TABLE + " SET " + KEY_STATUS + "='" + status + "', " + KEY_BOOKING_DATE + "='" + bookingDate + "', " + KEY_BOOKING_TIME + "='" + bookingTime + "', " + KEY_EXPLANATION + "='" + explanation + "', " + KEY_UPDATE_COUNTER + "='" + Integer.toString(updateNumber) + "' WHERE " + KEY_EMAIL + "='" + email + "'");
        database.execSQL(INSERT_CONSULTATION_HISTORY_TABLE_COMMAND + " WHERE " + KEY_EMAIL + "='" + email + "'");
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

    // Get update count of a consultation in Consultation History table via a specified user email
    public int getLatestHistoryCount(String email) {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT " + KEY_UPDATE_COUNTER + " FROM " + DATABASE_CONSULTATION_HISTORY_TABLE + " WHERE " + KEY_EMAIL + "='" + email + "' ORDER BY " + KEY_UPDATE_COUNTER + " DESC LIMIT 1";

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

    // Retrieve a patient's detail via a specified status and date of the stored in Consultation table
    public String getPatientBioDetail(long rowID, String status, String date, String columnTitle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_TABLE, columns, KEY_ROWID + "=" + rowID + " AND " + KEY_STATUS + "=?" + " AND " + KEY_BOOKING_DATE + "=?", new String[] {status, date}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getString(getColumnID(columnTitle));
        }

        return "";
    }

    // Retrieve a patient's details if specified email is not currently stored in Consultation table
    public String getIndividualPatientDetails(long rowID, ArrayList<String> patientEmails, String columnTitle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_TABLE, columns, KEY_ROWID + "=" + rowID, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            if (!patientEmails.contains(cursor.getString(getColumnID(KEY_EMAIL)))) {
                return cursor.getString(getColumnID(columnTitle));
            }
        }

        return null;
    }

    // Retrieve consultation details in Consultation table for schedule purposes via a specified input
    public String getScheduleConsultationInformation(String email, String status, String bookingDate, String columnTitle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_TABLE, columns, KEY_EMAIL + "=?" + " AND " + KEY_STATUS + "=?" + " AND " + KEY_BOOKING_DATE + "=?", new String[] {email, status, bookingDate}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getString(getColumnID(columnTitle));
        }

        return null;
    }

    // Retrieve consultation details in Consultation History table for schedule purposes via a specified input
    public String getConsultationHistoryInformation(String email, String bookingDate, String bookingTime, String bookingStatus, String columnTitle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(DATABASE_CONSULTATION_HISTORY_TABLE, historyColumns, KEY_STATUS + "=?" + " AND " + KEY_EMAIL + "=?" + " AND " + KEY_BOOKING_DATE + "=?" + " AND " + KEY_BOOKING_TIME + "=?", new String[] {bookingStatus, email, bookingDate, bookingTime}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getString(getHistoryColumnID(columnTitle));
        }

        return "";
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
    public void deleteEntry(String email) throws SQLException {

        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(DATABASE_CONSULTATION_TABLE, KEY_EMAIL + "=?", new String [] {email});
    }

    // Close connections to database
    public void close() {

        SQLiteDatabase database = this.getReadableDatabase();

        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}