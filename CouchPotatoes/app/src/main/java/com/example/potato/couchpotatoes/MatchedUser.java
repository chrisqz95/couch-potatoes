package com.example.potato.couchpotatoes;

/**
 * Created by chris on 11/5/17.
 */

public class MatchedUser extends User {
    public MatchedUser () {}

    public MatchedUser ( String email, String uid, String firstName, String middleName, String lastName, String dob,
                         String gender, String city, String state, String country, String bio,
                         double latitude, double longitude, boolean locked, boolean suspended ) {
        super( email, uid, firstName, middleName, lastName, dob, gender, city, state, country, bio,
                latitude, longitude, locked, suspended );
    }

    public MatchedUser ( String uid ) {
        super( null, uid, null, null, null,
                null, null, null, null, null, null,
                0, 0, false, false );
    }
}
