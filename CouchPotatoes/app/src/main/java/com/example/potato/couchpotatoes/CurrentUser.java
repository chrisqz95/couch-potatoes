package com.example.potato.couchpotatoes;

/**
 * Created by Admin on 11/7/17.
 */

public class CurrentUser extends User {
    private String email;
    private String city;
    private String state;
    private String country;

    public CurrentUser () {}

    public CurrentUser ( String userID, String firstName, String middleName, String lastName,
                         String birthDate, String gender, String bio, double latitude, double longitude,
                         boolean locked, boolean suspended, String email, String city, String state, String country ) {
        super( userID, firstName, middleName, lastName, birthDate, gender, bio, latitude, longitude, locked, suspended );

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
