package com.app.gptalk.listview_getter;

import android.graphics.Bitmap;

public class GPAvailableItem {

    private Bitmap profilePhoto;
    private String title, firstName, lastName, address, city;

    public GPAvailableItem(Bitmap profilePhoto, String title, String firstName, String lastName, String address, String city) {

        this.profilePhoto = profilePhoto;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
    }

    // Getter methods
    public Bitmap getProfilePhoto() { return profilePhoto; }
    public String getTitle() { return title; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }
    public String getCity() { return city; }

    // Setter method
    public void setTitle(String title) { this.title = title; }
}
