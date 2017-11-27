package com.example.potato.couchpotatoes;

/**
 * Created by chris on 11/5/17.
 */

public final class MatchedUser extends User {
    public MatchedUser () {}

    public MatchedUser ( String userID, String firstName, String middleName, String lastName,
                         String birth_date, String gender, String bio, String profilePic, double latitude, double longitude,
                         boolean locked, boolean suspended ) {
        super( userID, firstName, middleName, lastName, birth_date, gender, bio, profilePic, latitude, longitude, locked, suspended );
    }
}