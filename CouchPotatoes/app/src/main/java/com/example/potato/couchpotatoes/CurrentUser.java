package com.example.potato.couchpotatoes;

import java.util.ArrayList;

/**
 * Created by Admin on 11/7/17.
 */

public class CurrentUser extends User {
    private String email;
    private String city;
    private String state;
    private String country;

    /* TODO DISCUSS NEED FOR FIELDS BELOW
    //
    private int min_age_preference;
    private int max_age_preference;

    // Note: When updating fields below, need to either refetch data from Firebase after submitting
    //       the new data or add the new data here in addition to submitting the new data to Firebase
    private ArrayList<String> contacts = new ArrayList<>();
    ArrayList<String> photos = new ArrayList<>();
    ArrayList<User_Interest> interests = new ArrayList<>();
    ArrayList<String> genderPreferences = new ArrayList<>();
    */

    public CurrentUser () {}

    public CurrentUser ( String userID, String firstName, String middleName, String lastName,
                         String birth_date, String gender, String bio, String profilePic, double latitude, double longitude,
                         boolean locked, boolean suspended, String email, String city, String state, String country ) {
        super( userID, firstName, middleName, lastName, birth_date, gender, bio, profilePic,latitude, longitude, locked, suspended );

        this.setEmail(email);
        this.setCity(city);
        this.setState(state);
        this.setCountry(country);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
