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
    private final String userNotificationPath = "User_Notification/";
    private final String userPhotoPath = "User_Photo/";
    private final String photoPath = "Photo/";
    private final String interestPath = "Category/";
    private final String interestSubcategoryPath = "Interest_Subcategory/";
    private final String userInterestPath = "User_Interest/";
    private final String partnerPreferencePath = "Partner_Preference/";
    private final String likePath = "Like/";
    private final String dislikePath = "Dislike/";
    private final String befriendPath = "Befriend/";
    private final String datePath = "Date/";
    private final String blockPath = "Block/";
    private final String blockedUserPath = "Blocked_User/";
    private final String reportPath = "Report/";
    private final String reportedUserPath = "Reported_User/";
    private final String userMatchPath = "User_Match/";

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

    public boolean addToContactList( String currUserID, String contactUserID ) {
        db.getReference(getUserContactListPath()).child( currUserID ).child( contactUserID ).setValue( true );

        return checkExists( getUserContactListPath() + currUserID + "/" + contactUserID );
    }

    public boolean addToLockedUser( String userID, String timestamp, String reason ) {
        db.getReference( getLockedUserPath() ).child( userID ).child( "timestamp" ).setValue( timestamp );
        db.getReference( getLockedUserPath() ).child( userID ).child( "reason" ).setValue( reason );

        return checkExists( getLockedUserPath() + userID );
    }

    public boolean addToSuspendedUser( String userID, String timestamp, String reason ) {
        db.getReference( getSuspendedUserPath() ).child( userID ).child( "timestamp" ).setValue( timestamp );
        db.getReference( getSuspendedUserPath() ).child( userID ).child( "reason" ).setValue( reason );

        return checkExists( getSuspendedUserPath() + userID );
    }

    public boolean addToUserNotification( String userID, String timestamp, String description ) {
        db.getReference( getUserNotificationPath() ).child( userID ).child( timestamp ).child( "description" ).setValue( description );

        return checkExists( getUserNotificationPath() + userID + "/" + timestamp );
    }

    public boolean addToUserPhoto( String userID, String photoID ) {
        db.getReference( getUserPhotoPath() ).child( userID ).child( photoID ).setValue( true );

        return checkExists( getUserPhotoPath() + userID + "/" + photoID );
    }

    public boolean addToPhoto( String photoID, String userID, String title, String description, String uri ) {
        db.getReference( getPhotoPath() ).child( photoID ).child( "user_id" ).setValue( userID );
        db.getReference( getPhotoPath() ).child( photoID ).child( "title" ).setValue( title );
        db.getReference( getPhotoPath() ).child( photoID ).child( "description" ).setValue( description );
        db.getReference( getPhotoPath() ).child( photoID ).child( "uri" ).setValue( uri );

        return checkExists( getPhotoPath() + photoID );
    }

    public boolean addToInterest( String category ) {
        db.getReference( getInterestPath() ).child( category ).setValue( true );

        return checkExists( getInterestPath() + category );
    }

    public boolean addToInterestSubcategory( String category, String subcategory ) {
        db.getReference( getInterestSubcategoryPath() ).child( category ).child( subcategory ).setValue( true );

        return checkExists( getInterestSubcategoryPath() + category + "/" + subcategory );
    }

    public boolean addToUserInterest( String userID, String category, String subcategory, String preference ) {
        db.getReference( getUserInterestPath() ).child( userID ).child( category ).child( subcategory ).setValue( preference );

        return checkExists( getUserInterestPath() + userID + "/" + category + "/" + subcategory );
    }

    public boolean addToPartnerPreference( String userID, int min_age, int max_age, Map<String, Object> gender ) {
        db.getReference( getPartnerPreferencePath() ).child( userID ).child( "min_age" ).setValue( min_age );
        db.getReference( getPartnerPreferencePath() ).child( userID ).child( "max_age" ).setValue( max_age );
        db.getReference( getPartnerPreferencePath() ).child( userID ).child( "gender" ).setValue( gender );

        return checkExists( getPartnerPreferencePath() + userID );
    }

    public boolean addToLike( String actorUserID, String receiverUserID, String timestamp ) {
        db.getReference( getLikePath() ).child( actorUserID ).child( receiverUserID ).child( "timestamp" ).setValue( timestamp );

        return checkExists( getLikePath() + actorUserID + "/" + receiverUserID );
    }
    public boolean addToDislike( String actorUserID, String receiverUserID, String timestamp ) {
        db.getReference( getDislikePath() ).child( actorUserID ).child( receiverUserID ).child( "timestamp" ).setValue( timestamp );

        return checkExists( getDislikePath() + actorUserID + "/" + receiverUserID );
    }

    public boolean addToBefriend( String actorUserID, String receiverUserID, String timestamp ) {
        db.getReference( getBefriendPath() ).child( actorUserID ).child( receiverUserID ).child( "timestamp" ).setValue( timestamp );

        return checkExists( getBefriendPath() + actorUserID + "/" + receiverUserID );
    }

    public boolean addToDate( String actorUserID, String receiverUserID, String timestamp ) {
        db.getReference( getDatePath() ).child( actorUserID ).child( receiverUserID ).child( "timestamp" ).setValue( timestamp );

        return checkExists( getDatePath() + actorUserID + "/" + receiverUserID );
    }

    public boolean addToBlock( String actorUserID, String receiverUserID, String timestamp ) {
        db.getReference( getBlockPath() ).child( actorUserID ).child( receiverUserID ).child( "timestamp" ).setValue( timestamp );

        return checkExists( getBlockPath() + actorUserID + "/" + receiverUserID );
    }

    public boolean addToBlockedUser( String receiverUserID, String actorUserID, String timestamp ) {
        db.getReference( getBlockedUserPath() ).child( receiverUserID ).child( actorUserID ).child( "timestamp" ).setValue( timestamp );

        return checkExists( getBlockedUserPath() + receiverUserID + "/" + actorUserID );
    }

    public boolean addToReport( String actorUserID, String receiverUserID, String timestamp, String reason ) {
        db.getReference( getReportPath() ).child( actorUserID ).child( receiverUserID ).child( "timestamp" ).setValue( timestamp );
        db.getReference( getReportPath() ).child( actorUserID ).child( receiverUserID ).child( "reason" ).setValue( reason );

        return checkExists( getReportPath() + actorUserID + "/" + receiverUserID );
    }

    public boolean addToReportedUser( String receiverUserID, String actorUserID, String timestamp, String reason ) {
        db.getReference( getReportedUserPath() ).child( receiverUserID ).child( actorUserID ).child( "timestamp" ).setValue( timestamp );
        db.getReference( getReportedUserPath() ).child( receiverUserID ).child( actorUserID ).child( "reason" ).setValue( reason );

        return checkExists( getReportedUserPath() + receiverUserID + "/" + actorUserID );
    }

    public boolean removeUser( String userID ) {
        db.getReference(getUserPath()).child( userID ).setValue( null );

        return !checkExists( getUserPath() + userID );
    }

    public boolean removeFromContactList( String currUserID, String contactUserID ) {
        db.getReference(getUserContactListPath()).child( currUserID ).child( contactUserID ).setValue( null );

        return !checkExists( getUserContactListPath() + currUserID + "/" + contactUserID );
    }

    public boolean removeFromLockedUser( String userID ) {
        db.getReference( getLockedUserPath() ).child( userID ).setValue( null );

        return !checkExists( getLockedUserPath() + userID );
    }

    public boolean removeFromSuspendedUser( String userID ) {
        db.getReference( getSuspendedUserPath() ).child( userID ).setValue( null );

        return !checkExists( getSuspendedUserPath() + userID );
    }

    public boolean removeFromUserNotification( String userID, String timestamp ) {
        db.getReference( getUserNotificationPath() ).child( userID ).child( timestamp ).setValue( null );

        return !checkExists( getUserNotificationPath() + userID + "/" + timestamp );
    }

    public boolean removeFromUserPhoto( String userID, String photoID ) {
        db.getReference( getUserPhotoPath() ).child( userID ).child( photoID ).setValue( null );

        return !checkExists( getUserPhotoPath() + userID + "/" + photoID );
    }

    public boolean removeFromPhoto( String photoID, String userID, String title, String description, String uri ) {
        db.getReference( getPhotoPath() ).child( photoID ).setValue( null );

        return !checkExists( getPhotoPath() + photoID );
    }

    public boolean removeFromInterest( String category ) {
        db.getReference( getInterestPath() ).child( category ).setValue( null );

        return !checkExists( getInterestPath() + category );
    }

    public boolean removeFromInterestSubcategory( String category, String subcategory ) {
        db.getReference( getInterestSubcategoryPath() ).child( category ).child( subcategory ).setValue( null );

        return !checkExists( getInterestSubcategoryPath() + category + "/" + subcategory );
    }

    public boolean removeFromUserInterest( String userID, String category, String subcategory  ) {
        db.getReference( getUserInterestPath() ).child( userID ).child( category ).child( subcategory ).setValue( null );

        return !checkExists( getUserInterestPath() + userID + "/" + category + "/" + subcategory );
    }


    public boolean removeFromPartnerPreference( String userID ) {
        db.getReference( getPartnerPreferencePath() ).child( userID ).setValue( null );

        return !checkExists( getPartnerPreferencePath() + userID );
    }

    public boolean removeFromLike( String actorUserID, String receiverUserID ) {
        db.getReference( getLikePath() ).child( actorUserID ).child( receiverUserID ).setValue( null );

        return !checkExists( getLikePath() + actorUserID + "/" + receiverUserID );
    }

    public boolean removeFromDislike( String actorUserID, String receiverUserID ) {
        db.getReference( getDislikePath() ).child( actorUserID ).child( receiverUserID ).setValue( null );

        return !checkExists( getDislikePath() + actorUserID + "/" + receiverUserID );
    }

    public boolean removeFromBefriend( String actorUserID, String receiverUserID ) {
        db.getReference( getBefriendPath() ).child( actorUserID ).child( receiverUserID ).setValue( null );

        return !checkExists( getBefriendPath() + actorUserID + "/" + receiverUserID );
    }

    public boolean removeFromDate( String actorUserID, String receiverUserID ) {
        db.getReference( getDatePath() ).child( actorUserID ).child( receiverUserID ).setValue( null );

        return !checkExists( getDatePath() + actorUserID + "/" + receiverUserID );
    }

    public boolean removeFromBlock( String actorUserID, String receiverUserID ) {
        db.getReference( getBlockPath() ).child( actorUserID ).child( receiverUserID ).setValue( null );

        return !checkExists( getBlockPath() + actorUserID + "/" + receiverUserID );
    }

    public boolean removeFromBlockedUser( String receiverUserID, String actorUserID ) {
        db.getReference( getBlockedUserPath() ).child( receiverUserID ).child( actorUserID ).setValue( null );

        return !checkExists( getBlockedUserPath() + receiverUserID + "/" + actorUserID );
    }

    public boolean removeFromReport( String actorUserID, String receiverUserID ) {
        db.getReference( getReportPath() ).child( actorUserID ).child( receiverUserID ).setValue( null );

        return !checkExists( getReportPath() + actorUserID + "/" + receiverUserID );
    }

    public boolean removeFromReportedUser( String receiverUserID, String actorUserID ) {
        db.getReference( getReportedUserPath() ).child( receiverUserID ).child( actorUserID ).setValue( null );

        return !checkExists( getReportedUserPath() + receiverUserID + "/" + actorUserID );
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

    public void updatePhoto( String photoID, String userID, String title, String description, String uri ) {
        Map<String, Object> updates = new HashMap<>();

        updates.put( "user_id", userID );
        updates.put( "title", title );
        updates.put( "description", description );
        updates.put( "uri", uri );

        db.getReference( getPhotoPath() ).child( photoID ).updateChildren( updates );
    }

    public void updatePartnerPreference( String userID, int min_age, int max_age, Map<String, Object> gender ) {
        Map<String, Object> updates = new HashMap<>();

        updates.put( "min_age", min_age );
        updates.put( "max_age", max_age );
        updates.put( "gender", gender );

        db.getReference( getPartnerPreferencePath() ).child( userID ).updateChildren( updates );
    }

    public boolean checkExists( String path ) {
        return ( db.getReference( path ) != null );
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

    public String getUserNotificationPath() {
        return userNotificationPath;
    }

    public String getUserPhotoPath() {
        return userPhotoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getInterestPath() {
        return interestPath;
    }

    public String getInterestSubcategoryPath() {
        return interestSubcategoryPath;
    }

    public String getUserInterestPath() {
        return userInterestPath;
    }

    public String getPartnerPreferencePath() {
        return partnerPreferencePath;
    }

    public String getLikePath() {
        return likePath;
    }

    public String getBefriendPath() {
        return befriendPath;
    }

    public String getDislikePath() {
        return dislikePath;
    }

    public String getDatePath() {
        return datePath;
    }

    public String getBlockPath() {
        return blockPath;
    }

    public String getBlockedUserPath() {
        return blockedUserPath;
    }

    public String getReportPath() {
        return reportPath;
    }

    public String getReportedUserPath() {
        return reportedUserPath;
    }

    public String getUserMatchPath() {
        return userMatchPath;
    }
}
