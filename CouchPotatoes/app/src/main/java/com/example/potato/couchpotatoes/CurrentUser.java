package com.example.potato.couchpotatoes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Admin on 11/7/17.
 */

public class CurrentUser extends User {
    private static CurrentUser uniqueInstance;
    private ArrayList<String> contactList = new ArrayList<String>();
    private LinkedList<String> matchedUsersId = new LinkedList<>();
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
        if ( uniqueInstance == null ) {
            uniqueInstance = new CurrentUser(
                    email, uid, firstName, middleName, lastName, dob, gender, city, state, country, bio,
                    latitude, longitude, locked, suspended
            );
        }
        return uniqueInstance;
    }

    public ArrayList<String> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<String> contactList) {
        this.contactList = contactList;
    }
}
