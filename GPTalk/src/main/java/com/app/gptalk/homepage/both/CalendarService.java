package com.app.gptalk.homepage.both;

import android.content.Context;
import java.util.ArrayList;

public class CalendarService {

    public static ArrayList<String> startDate = new ArrayList<String>();
    public static ArrayList<String> endDate = new ArrayList<String>();
    public static ArrayList<String> patientName = new ArrayList<String>();
    public static ArrayList<String> consultationTime = new ArrayList<String>();

    public static Context context;

    public static ArrayList<String> readCalendarEvent(ArrayList<String> fName, ArrayList<String> lName, ArrayList<String> bDate, ArrayList<String> bTime) {

        // Clear all previously stored data
        startDate.clear();
        endDate.clear();
        patientName.clear();
        consultationTime.clear();

        // Retrieve and set newly retrieved data
        for (int index = 0; index < fName.size(); index++) {

            String firstName = fName.get(index);
            String lastName = lName.get(index);
            String date = bDate.get(index);
            String time = bTime.get(index);

            // Generate data in YYYY-MM-DD format
            String day = date.substring(0, 2);
            String month = date.substring(3, 5);
            String year = date.substring(6, date.length());
            String complete = year + "-" + month + "-" + day;

            patientName.add(firstName + " " + lastName);
            consultationTime.add(time);
            startDate.add(complete);
            endDate.add(complete);
        }

        return patientName;
    }
}
