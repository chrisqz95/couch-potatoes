package com.example.potato.couchpotatoes;

/**
 * Created by chris on 11/5/17.
 */

public class MatchedUser extends User {
    public MatchedUser () {}

    /**
     * No email.
     * @param uid
     * @param firstName
     * @param middleName
     * @param lastName
     * @param dob
     * @param gender
     * @param city
     * @param state
     * @param country
     * @param bio
     * @param latitude
     * @param longitude
     * @param locked
     * @param suspended
     */
    public MatchedUser ( String uid, String firstName, String middleName, String lastName, String dob,
                         String gender, String city, String state, String country, String bio,
                         double latitude, double longitude, boolean locked, boolean suspended ) {
        super( null, uid, firstName, middleName, lastName, dob, gender, city, state, country, bio,
                latitude, longitude, locked, suspended );
    }

    public MatchedUser ( String uid ) {
        super( null, uid, null, null, null,
                null, null, null, null, null, null,
                0, 0, false, false );
    }

    @Override
    public void setEmail(String email) { }
}
