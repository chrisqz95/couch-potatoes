package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d( "TEST", helper.auth.getUid() );

        mFirstName = (EditText) findViewById(R.id.firstName);
        mMiddleName = (EditText) findViewById(R.id.middleName);
        mLastName = (EditText) findViewById(R.id.lastName);
        mBirthDate = (EditText) findViewById(R.id.birthDate);
        mGender = (EditText) findViewById(R.id.gender);
        submit = (Button) findViewById(R.id.Submit);

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
        helper.auth.createUserWithEmailAndPassword( email, password ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //email = helper.auth.getCurrentUser().getEmail();

                // TODO USE STRING VALIDATOR
                String userID = helper.auth.getUid();
                String firstName = mFirstName.getText().toString();
                String middleName = mMiddleName.getText().toString();
                String lastName = mLastName.getText().toString();
                String birthDate = mBirthDate.getText().toString();
                String gender = mGender.getText().toString();
                String city = "";
                String state = "";
                String country = "";
                String bio = "";
                String displayName = helper.getFullName( firstName, middleName, lastName );
                Double latitude = 0.0;
                Double longitude = 0.0;
                boolean locked = false;
                boolean suspended = false;

                // Create new user object
                user = new CurrentUser(
                    email, userID, firstName, middleName, lastName, birthDate, gender, city, state, country, bio,
                    latitude, longitude, locked, suspended );

                //helper.updateAuthUserDisplayName( displayName );

                // Create new Firebase Authentication user account update request to update user's display name
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName( displayName )
                        .build();

                // Update Firebase Authentication user's display name
                helper.auth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TEST", "User profile updated.");
                                    Log.d("TEST", "User name: " + helper.auth.getCurrentUser().getDisplayName());


                                    //password = (String) getIntent().getExtras().get( "password" );

                                    // Log user into new Firebase Authentication account
                                    helper.auth.signInWithEmailAndPassword( email, password ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            //getIntent().putExtra( "password", "" );

                                            // Add user account info to Firebase Database
                                            helper.addNewUser( user );

                                            // Get new FirebaseUser
                                            helper.fetchCurrentUser();

                                            // Add the new user to a new chat containing only the new user
                                            String newChatID = helper.getNewChildKey( helper.getChatUserPath() );
                                            String userID = helper.auth.getUid();
                                            String displayName = helper.getAuthUserDisplayName();
                                            //String displayName = helper.auth.getCurrentUser().getDisplayName();

                                            Log.d( "TEST", "DISPLAY NAME: " + displayName );

                                            helper.addToChatUser( newChatID, userID, displayName );
                                            helper.addToUserChat( userID, newChatID );

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
