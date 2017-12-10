package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistrationActivity extends AppCompatActivity {
    DBHelper helper = new DBHelper();
    EditText mFirstName;
    EditText mMiddleName;
    EditText mLastName;
    EditText mBirthDate;
    EditText mGender;
    Button submit;
    CurrentUser user;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirstName = findViewById(R.id.firstName);
        mMiddleName = findViewById(R.id.middleName);
        mLastName = findViewById(R.id.lastName);
        mBirthDate = findViewById(R.id.birthDate);
        mGender = findViewById(R.id.gender);
        submit = findViewById(R.id.Submit);

        // Attempt to submit user registration information
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });
    }

    protected void attemptRegistration() {
        email = (String) getIntent().getExtras().get( "email" );
        password = (String) getIntent().getExtras().get( "password" );

        // Remove password from Intent extras
        getIntent().putExtra( "password", "" );

        // Attempt to create a Firebase Authentication user account with the provided login credentials
        helper.getAuth().createUserWithEmailAndPassword( email, password ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            // TODO USE STRING VALIDATOR
            String userID = helper.getAuth().getUid();
            String firstName = mFirstName.getText().toString();
            String middleName = mMiddleName.getText().toString();
            String lastName = mLastName.getText().toString();
            String birthDate = mBirthDate.getText().toString();
            String gender = mGender.getText().toString();
            String city = "";
            String state = "";
            String country = "";
            String bio = "";
            final String displayName = helper.getFullName( firstName, middleName, lastName );
            Double latitude = 0.0;
            Double longitude = 0.0;
            boolean locked = false;
            boolean suspended = false;

            // Create new user object
            user = CurrentUser.getInstance(
                email, userID, firstName, middleName, lastName, birthDate, gender, city, state, country, bio,
                latitude, longitude, locked, suspended );

            // Create new Firebase Authentication user account update request to update user's display name
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName( displayName )
                    .build();

            // Update Firebase Authentication user's display name
            helper.getAuth().getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        // Log user into new Firebase Authentication account
                        helper.getAuth().signInWithEmailAndPassword( email, password ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                            // Add user account info to Firebase Database
                            helper.addNewUser( user );

                            // Get new FirebaseUser
                            helper.fetchCurrentUser();

                            // Add the new user to a new chat containing only the new user
                            String chatID = helper.getNewChildKey( helper.getChatUserPath() );
                            String userID = helper.getAuth().getUid();
                            String displayName = helper.getAuthUserDisplayName();

                            helper.addToChatUser( chatID, userID, displayName );
                            helper.addToUserChat( userID, chatID );

                              String messageOneID = helper.getNewChildKey(helper.getMessagePath());
                              String timestampOne = helper.getNewTimestamp();
                              String messageOne = "COUCH POTATOES:\nWelcome to Couch Potatoes!"
                                             + "\nEnjoy meeting new people with similar interests!";

                              helper.addToMessage( messageOneID, userID, "COUCH POTATOES", chatID, timestampOne, messageOne );

                              String messageTwoID = helper.getNewChildKey(helper.getMessagePath());
                              String timestampTwo = helper.getNewTimestamp();
                              String messageTwo = "COUCH POTATOES:\nThis chat is your space. Feel free to experiment with the chat here.";

                              helper.addToMessage( messageTwoID, userID, "COUCH POTATOES", chatID, timestampTwo, messageTwo );

                            // Registration complete. Redirect the new user the the main activity.
                            startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                            finish();
                            }
                        });

                    }
                    }
                });
            }
        });
    }
}
