package com.example.potato.couchpotatoes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 11/7/17.
 */

public class CurrentUser extends User {
    private ArrayList<String> contactList = new ArrayList<String>();

    public CurrentUser () {}

    public CurrentUser ( String email, String uid, String firstName, String middleName, String lastName, String dob,
                  String gender, String city, String state, String country, String bio,
                  double latitude, double longitude, boolean locked, boolean suspended ) {
        super( email, uid, firstName, middleName, lastName, dob, gender, city, state, country, bio,
                latitude, longitude, locked, suspended );
    }

    public ArrayList<String> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<String> contactList) {
        this.contactList = contactList;
    }
}
