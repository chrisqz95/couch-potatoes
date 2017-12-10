package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * The home page which shows potential matches.
 */
public class MainActivity extends AppCompatActivity {
    private DBHelper helper;

    // For the user cards
    private android.widget.Button chat;

    private String currUserID;

    protected void onCreate(Bundle savedInstanceState) {
        // Sets up the activity layout
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new DBHelper();
        chat = findViewById(R.id.viewChats);
        currUserID = helper.getAuth().getUid();

        // Display user's name if logged in
        if ( helper.isUserLoggedIn() ) {

            // reads all the user info on the current user
            pullCurrentUserInfo();

            startActivity(new Intent(getApplicationContext(), MatchingActivity.class));
            finish();
            //mProgressView.setVisibility(View.VISIBLE);

        }
        // Else, redirect user to login page
        else {
            startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
            finish();
        }

        // Add event handler to chat button to start the ChatRoomActivity
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getApplicationContext(), ChatRoomActivity.class );
                startActivity( intent );
            }
        });
    }

    // Grabs the user info of the current user from Firebase
    private void pullCurrentUserInfo() {
        final CurrentUser currentUser = CurrentUser.getInstance();

        helper.getDb().getReference( helper.getUserPath() ).child( currUserID )
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for ( DataSnapshot field : dataSnapshot.getChildren() ) {
                    switch ( field.getKey() ) {
                        case "email":
                            currentUser.setEmail( (String) field.getValue() );
                            break;
                        case "uid":
                            currentUser.setUid( (String) field.getValue() );
                            break;
                        case "firstName":
                            currentUser.setFirstName( (String) field.getValue() );
                            break;
                        case "middleName":
                            currentUser.setFirstName( (String) field.getValue() );
                            break;
                        case "lastName":
                            currentUser.setFirstName( (String) field.getValue() );
                            break;
                        case "dob":
                            currentUser.setDob( (String) field.getValue() );
                            break;
                        case "gender":
                            currentUser.setGender( (String) field.getValue() );
                            break;
                        case "city":
                            currentUser.setFirstName( (String) field.getValue() );
                            break;
                        case "state":
                            currentUser.setFirstName( (String) field.getValue() );
                            break;
                        case "country":
                            currentUser.setFirstName( (String) field.getValue() );
                            break;
                        case "bio":
                            currentUser.setBio( (String) field.getValue());
                            break;
                        case "latitude":
                            long num = (long) field.getValue();
                            currentUser.setLatitude( 0.0 );
                            if( field.getValue() != null ) {
                                currentUser.setLatitude( (double) num );
                            }
                            break;
                        case "longitude":
                            num = (long) field.getValue();
                            currentUser.setLatitude( 0.0 );
                            if( field.getValue() != null ) {
                                currentUser.setLongitude( (double) num );
                            }
                            break;
                        case "locked":
                            currentUser.setLocked( (boolean) field.getValue() );
                            break;
                        case "suspended":
                            currentUser.setSuspended( (boolean) field.getValue() );
                            break;
                        default:
                            break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}

