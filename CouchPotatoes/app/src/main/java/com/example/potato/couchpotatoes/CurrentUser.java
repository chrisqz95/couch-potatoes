package com.example.potato.couchpotatoes;

import java.util.ArrayList;
import java.util.LinkedList;

public class CurrentUser extends User {
    private static CurrentUser uniqueInstance;
    // may or may not need. Alternative is to populate one a linked list in HomeActivity instead.
    private LinkedList<MatchedUser> matchedUsers = new LinkedList<>();

    private CurrentUser () {}

    private CurrentUser ( String email, String uid, String firstName, String middleName, String lastName, String dob,
                  String gender, String city, String state, String country, String bio,
                  double latitude, double longitude, boolean locked, boolean suspended ) {
        super( email, uid, firstName, middleName, lastName, dob, gender, city, state, country, bio,
                latitude, longitude, locked, suspended );
    }

    public static CurrentUser getInstance( String email, String uid, String firstName, String middleName, String lastName,
                                           String dob, String gender, String city, String state, String country, String bio,
                                           double latitude, double longitude, boolean locked, boolean suspended ) {

        return (uniqueInstance == null) ? new CurrentUser(
                email, uid, firstName, middleName, lastName, dob, gender, city, state, country, bio,
                latitude, longitude, locked, suspended) : uniqueInstance;
    }

    public static CurrentUser getInstance() {
        return (uniqueInstance == null) ? new CurrentUser() : uniqueInstance;
    }
}
