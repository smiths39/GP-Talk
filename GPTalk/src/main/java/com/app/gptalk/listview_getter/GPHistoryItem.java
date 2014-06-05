package com.app.gptalk.listview_getter;

import android.graphics.Bitmap;

public class GPHistoryItem {

    private Bitmap profilePhoto;
    private String firstName, lastName;

    public GPHistoryItem(Bitmap profilePhoto, String firstName, String lastName) {

        this.profilePhoto = profilePhoto;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getter methods
    public Bitmap getProfilePhoto() { return profilePhoto; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
