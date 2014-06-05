package com.app.gptalk.listview_getter;

public class GPWebsiteItem {

    private String county, title, firstName, lastName;

    public GPWebsiteItem(String county, String title, String firstName, String lastName) {

        this.county = county;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getter methods
    public String getCounty() { return county; }
    public String getTitle() { return title; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
