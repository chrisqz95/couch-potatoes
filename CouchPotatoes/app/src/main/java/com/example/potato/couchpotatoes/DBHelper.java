package com.example.potato.couchpotatoes;

import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by chris on 11/5/17.
 */

public class DBHelper {

    FirebaseAuth auth;
    FirebaseDatabase db;
    FirebaseUser user;

    private final String userPath = "User/";
    private final String userContactListPath = "User_Contact_List/";
    private final String lockedUserPath = "Locked_User/";
    private final String suspendedUserPath = "Suspended_User/";

    public DBHelper() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
    }

    public boolean loginUser(String email, String password) {
        if (auth.signInWithEmailAndPassword(email, password).isSuccessful()) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean createUser(String email, String password) {
        if (auth.createUserWithEmailAndPassword(email, password).isSuccessful()) {
            return true;
        }
        else {
            return false;
        }
    }

    public Queue<MatchedUser> getMatchedUsers(FirebaseUser user) {
        return null;
    }

    public boolean addNewUser( User user ) {
        db.getReference(getUserPath()).child( user.getUid() ).setValue( user );

        return checkExists( getUserPath() + user.getUid() );
    }

    public boolean addToContactList( CurrentUser user, User newContact ) {
        db.getReference(getUserContactListPath()).child( user.getUid() ).child( newContact.getUid() ).setValue( true );

        return checkExists( getUserContactListPath() + user.getUid() + "/" + newContact.getUid() );
    }

    public boolean addToLockedUser( User user, String timestamp, String reason ) {
        db.getReference( getLockedUserPath() ).child( user.getUid() ).child( "timestamp" ).setValue( timestamp );
        db.getReference( getLockedUserPath() ).child( user.getUid() ).child( "reason" ).setValue( reason );

        return checkExists( getLockedUserPath() + user.getUid() );
    }

    public boolean addToSuspendedUser( User user, String timestamp, String reason ) {
        db.getReference( getSuspendedUserPath() ).child( user.getUid() ).child( "timestamp" ).setValue( timestamp );
        db.getReference( getSuspendedUserPath() ).child( user.getUid() ).child( "reason" ).setValue( reason );

        return checkExists( getSuspendedUserPath() + user.getUid() );
    }

    public boolean removeUser( User user ) {
        db.getReference(getUserPath()).child( user.getUid() ).setValue( null );

        return !checkExists( getUserPath() + user.getUid() );
    }

    public boolean removeFromContactList( CurrentUser user, User newContact ) {
        db.getReference(getUserContactListPath()).child( user.getUid() ).child( newContact.getUid() ).setValue( null );

        return !checkExists( getUserContactListPath() + user.getUid() + "/" + newContact.getUid() );
    }

    public boolean removeFromLockedUser( User user ) {
        db.getReference( getLockedUserPath() ).child( user.getUid() ).setValue( null );

        return !checkExists( getLockedUserPath() + user.getUid() );
    }

    public boolean removeFromSuspendedUser( User user ) {
        db.getReference( getSuspendedUserPath() ).child( user.getUid() ).setValue( null );

        return !checkExists( getSuspendedUserPath() + user.getUid() );
    }

    public void updateUser( User user ) {
        Map<String, Object> updates = new HashMap<>();

        updates.put("email", user.getEmail());
        updates.put("uid", user.getUid());
        updates.put("firstName", user.getFirstName());
        updates.put("middleName", user.getMiddleName());
        updates.put("lastName", user.getLastName());
        updates.put("dob", user.getDob());
        updates.put("gender", user.getGender());
        updates.put("city", user.getCity());
        updates.put("state", user.getState());
        updates.put("country", user.getCountry());
        updates.put("bio", user.getBio());
        updates.put("latitude", user.getLatitude());
        updates.put("longitude", user.getLongitude());
        updates.put("locked", user.isLocked());
        updates.put("suspended", user.isSuspended());

        db.getReference(getUserPath()).child(user.getUid()).updateChildren(updates);
    }

    public boolean checkExists( String path ) {
        return ( db.getReference( path ) == null );
    }

    public String getNewChildKey( String path ) {
        return db.getReference( path ).push().getKey();
    }

    public String getUserPath() {
        return userPath;
    }

    public String getUserContactListPath() {
        return userContactListPath;
    }

    public String getLockedUserPath() {
        return lockedUserPath;
    }

    public String getSuspendedUserPath() {
        return suspendedUserPath;
    }
}
