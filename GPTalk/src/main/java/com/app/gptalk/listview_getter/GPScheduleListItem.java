package com.app.gptalk.listview_getter;

import android.graphics.Bitmap;

public class GPScheduleListItem {

    private String firstName, lastName, bookingTime;

    public GPScheduleListItem(String firstName, String lastName, String bookingTime) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.bookingTime = bookingTime;
    }

    // Getter methods
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getBookingTime() { return bookingTime; }
}
