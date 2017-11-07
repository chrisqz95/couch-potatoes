package com.example.potato.couchpotatoes;

/**
 * Created by Admin on 11/7/17.
 */

public class CurrentUser extends User {
    public CurrentUser () {}

    public CurrentUser ( String email, String uid, String firstName, String middleName, String lastName, String dob,
                  String gender, String city, String state, String country, String bio,
                  double latitude, double longitude, boolean locked, boolean suspended ) {
        super( email, uid, firstName, middleName, lastName, dob, gender, city, state, country, bio,
                latitude, longitude, locked, suspended );
    }
}
