package com.example.potato.couchpotatoes;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DBHelper {

    // max number of messages to fetch
    private static int MESSAGE_FETCH_LIMIT = 50;

    private static DBHelper uniqueInstance;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private FirebaseStorage storage;
    private FirebaseUser user;
    private FirebaseAuthException authException;

    /* Paths under root to particular data collections on Firebase */

    private final String userPath = "User/";
    private final String userPhotoPath = "User_Photo/";
    private final String photoPath = "Photo/";
    private final String interestPath = "Interest/";
    private final String interestSubcategoryPath = "Interest_Subcategory/";
    private final String userInterestPath = "User_Interest/";
    private final String partnerPreferencePath = "Partner_Preference/";
    private final String likePath = "Like/";
    private final String dislikePath = "Dislike/";
    private final String befriendPath = "Befriend/";
    private final String datePath = "Date/";
    private final String userChatPath = "User_Chat/";
    private final String chatUserPath = "Chat_User/";
    private final String chatMessagePath = "Chat_Message/";
    private final String messagePath = "Message/";
    private final String potentDatePath = "User_Potential_Date/";
    private final String potentFriendPath = "User_Potential_Friend/";

    private DBHelper() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
    }

    public static DBHelper getInstance() {
        return (uniqueInstance == null) ? new DBHelper() : uniqueInstance;
    }

    /* User account methods */
    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseDatabase getDb() {
        return db;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void updateAuthUserProfile( UserProfileChangeRequest changes ) {
        if ( user != null ) {
            user.updateProfile(changes);
        }
    }

    public void updateAuthUserDisplayName ( String displayName ) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName( displayName )
                .build();

        updateAuthUserProfile( profileUpdates );
    }

    public String getAuthUserDisplayName () {
        return ( user != null ) ? user.getDisplayName() : "";
    }

    public boolean isUserLoggedIn() {
        return ( user != null );
    }

    public void fetchCurrentUser() {
        user = auth.getCurrentUser();
    }

    /* Database CRUD methods */

    /* Read Methods */

    /**
     * NOTE: This can only be called after a current user has been initialized.
     * @param context used to notify the user of any errors when fetching data.
     * @param finishedCallback function to run when the data has been fetched.
     */
    public void fetchCurrentUserInfo(final Context context, @NonNull final SimpleCallback<DataSnapshot> finishedCallback) {
        getDb().getReference(getUserPath()).child(getAuth().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // data has been received. Call the callback method.
                finishedCallback.callback(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Do nothing
            }
        });
    }

    /**
     * Attempts to login. Returns whether login was successful through the callback interface.
     * @param email user email
     * @param password user password
     * @param activity used for the on complete listener
     * @param finishedCallback returns the whether or not the login was a success through the callback
     */
    public void attemptLogin(String email, String password, Activity activity,
                             @NonNull final SimpleCallback<Boolean> finishedCallback) {
        getAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                finishedCallback.callback(task.isSuccessful());
            }
        });
    }

    /**
     *
     * @param chatRoom id of the chatroom to get information of
     * @param finishedCallback returns the information of the chatroom in the callback
     */
    public void fetchChat(String chatRoom, @NonNull final SimpleCallback<DataSnapshot> finishedCallback) {
        getDb().getReference(getChatMessagePath() + chatRoom).limitToLast(MESSAGE_FETCH_LIMIT)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        finishedCallback.callback(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Do nothing
                    }
                });
    }

    /**
     *
     * @param messageId id of message to fetch
     * @param finishedCallback returns data snapshot of message in callback. Guarenteed to exist.
     */
    public void fetchMessage(String messageId, @NonNull final SimpleCallback<DataSnapshot> finishedCallback) {
        getDb().getReference(getMessagePath() + messageId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    finishedCallback.callback(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Do nothing
            }
        });
    }

    /* Methods to add data to Firebase */

    public boolean addNewUser( User user ) {
        db.getReference(getUserPath()).child( user.getUid() ).setValue( user );
        return checkExists( getUserPath() + user.getUid() );
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

    public boolean addToUserInterest( String userID, String category, String subcategory, String preference ) {
        db.getReference( getUserInterestPath() ).child( userID ).child( category ).child( subcategory ).setValue( preference );
        return checkExists( getUserInterestPath() + userID + "/" + category + "/" + subcategory );
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
        Map<String, Object> additions = new HashMap<>();

        additions.put( "timestamp", timestamp );
        additions.put( "chatCreated", false );

        db.getReference( getBefriendPath() ).child( actorUserID ).child( receiverUserID ).setValue( additions );

        return checkExists( getBefriendPath() + actorUserID + "/" + receiverUserID );
    }

    public boolean addToDate( String actorUserID, String receiverUserID, String timestamp ) {
        Map<String, Object> additions = new HashMap<>();

        additions.put( "timestamp", timestamp );
        additions.put( "chatCreated", false );

        db.getReference( getDatePath() ).child( actorUserID ).child( receiverUserID ).setValue( additions );

        return checkExists( getDatePath() + actorUserID + "/" + receiverUserID );
    }

    public boolean addToUserChat( String userID, String chatID ) {
        db.getReference( getUserChatPath() ).child( userID ).child( chatID ).setValue( true );
        return checkExists( getUserChatPath() + userID + "/" + chatID );
    }

    public boolean addToChatUser( String chatID, String userID, String userName ) {
        db.getReference( getChatUserPath() ).child( chatID ).child( userID ).setValue( userName );
        return checkExists( getChatUserPath() + chatID + "/" + userID );
    }

    public boolean addToChatMessage( String chatID, String messageID ) {
        db.getReference( getChatMessagePath() ).child( chatID ).child( messageID ).setValue( true );
        return checkExists( getChatMessagePath() + chatID + "/" + messageID );
    }

    public boolean addToMessage( String messageID, String userID, String name, String chatID, String timestamp, String text ) {
        db.getReference( getMessagePath() ).child( messageID ).child( "user_id" ).setValue( userID );
        db.getReference( getMessagePath() ).child( messageID ).child( "name" ).setValue( name );
        db.getReference( getMessagePath() ).child( messageID ).child( "chat_id" ).setValue( chatID );
        db.getReference( getMessagePath() ).child( messageID ).child( "timestamp" ).setValue( timestamp );
        db.getReference( getMessagePath() ).child( messageID ).child( "text" ).setValue( text );

        return checkExists( getMessagePath() + messageID );
    }

    /* Methods to remove data from Firebase */

    public boolean removeUser( String userID ) {
        db.getReference(getUserPath()).child( userID ).setValue( null );
        return !checkExists( getUserPath() + userID );
    }

    public boolean removeFromUserInterest( String userID, String category, String subcategory  ) {
        db.getReference( getUserInterestPath() ).child( userID ).child( category ).child( subcategory ).setValue( null );
        return !checkExists( getUserInterestPath() + userID + "/" + category + "/" + subcategory );
    }

    public boolean removeFromUserChat( String userID, String chatID ) {
        db.getReference( getUserChatPath() ).child( userID ).child( chatID ).setValue( null );
        return !checkExists( getUserChatPath() + userID + "/" + chatID );
    }

    public boolean removeFromChatUser( String chatID, String userID ) {
        db.getReference( getChatUserPath() ).child( chatID ).child( userID ).setValue( null );
        return !checkExists( getChatUserPath() + chatID + "/" + userID );
    }

    /* Helper methods */

    public boolean checkExists( String path ) {
        return ( db.getReference( path ).getKey() != null );
    }

    /* Getters */

    public String getNewChildKey( String path ) {
        return db.getReference( path ).push().getKey();
    }

    public String getNewTimestamp() {
        return (new SimpleDateFormat( "yyyy-MM-dd  HH:mm:ss" ).format( new Date()));
    }

    /**
     * Concatenates first, middle, and last names and returns result.
     * If first, middle, and last names are null, returns userID instead.
     *
     * @param firstName
     * @param middleName
     * @param lastName
     * @return Returns result of concatenating all names.
     */
    public String getFullName( String firstName, String middleName, String lastName ) {
        String name = "";

        if ( firstName != null ) {
            name += firstName;
        }
        if ( middleName != null ) {
            name += " ";
            name += middleName;
        }
        if ( lastName != null ) {
            name += " ";
            name += lastName;
        }

        return name;
    }

    public String getUserPath() {
        return userPath;
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

    public String getUserChatPath() {
        return userChatPath;
    }

    public String getChatUserPath() {
        return chatUserPath;
    }

    public String getChatMessagePath() {
        return chatMessagePath;
    }

    public String getMessagePath() {
        return messagePath;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public String getPotentDatePath() {
        return potentDatePath;
    }

    public String getPotentFriendPath() {
        return potentFriendPath;
    }
}
