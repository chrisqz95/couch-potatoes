package com.example.potato.couchpotatoes;

/**
 * Created by chris on 11/5/17.
 */

public final class MatchedUser extends User {
    public MatchedUser () {}

    public MatchedUser ( String userID, String firstName, String middleName, String lastName,
                         String birthDate, String gender, String bio, double latitude, double longitude,
                         boolean locked, boolean suspended ) {
        super( userID, firstName, middleName, lastName, birthDate, gender, bio, latitude, longitude, locked, suspended );
    }
}