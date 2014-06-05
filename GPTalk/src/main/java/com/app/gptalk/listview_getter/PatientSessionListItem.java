package com.app.gptalk.listview_getter;

public class PatientSessionListItem {

    private String title, firstName, lastName, bookingTime;

    public PatientSessionListItem(String title, String firstName, String lastName, String bookingTime) {

        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bookingTime = bookingTime;
    }

    // Getter methods
    public String getTitle() { return title; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getBookingTime() { return bookingTime; }
}
