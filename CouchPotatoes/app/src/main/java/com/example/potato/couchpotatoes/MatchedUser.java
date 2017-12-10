package com.example.potato.couchpotatoes;

public class MatchedUser extends User {

    public MatchedUser ( String uid ) {
        super( null, uid, null, null, null,
                null, null, null, null, null, null,
                0, 0, false, false );
    }

    @Override
    public void setEmail(String email) { }
}
