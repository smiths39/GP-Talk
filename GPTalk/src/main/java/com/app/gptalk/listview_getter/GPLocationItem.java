package com.app.gptalk.listview_getter;

public class GPLocationItem {

    private String title, firstName, lastName, medical_centre_address, medical_centre_city, medical_centre_county, medical_centre_post_code, medical_centre_phone_number;

    public GPLocationItem(String title, String firstName, String lastName, String address, String city, String county, String postCode, String phoneNumber) {

        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.medical_centre_address = address;
        this.medical_centre_city = city;
        this.medical_centre_county = county;
        this.medical_centre_post_code = postCode;
        this.medical_centre_phone_number = phoneNumber;
    }

    // Getter methods
    public String getTitle() { return title; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMedicalCentreAddress() { return medical_centre_address; }
    public String getMedicalCentreCity() { return medical_centre_city; }
    public String getMedicalCentreCounty() { return medical_centre_county; }
    public String getMedicalCentrePostCode() { return medical_centre_post_code; }
    public String getMedicalCentrePhoneNumber() { return medical_centre_phone_number; }
}
