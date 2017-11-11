package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
    private android.widget.TextView userName;
    private android.widget.Button logout;
    private android.widget.Button chat;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new DBHelper();

        userName = (android.widget.TextView) findViewById(R.id.userName);
        logout = (android.widget.Button) findViewById(R.id.logout);
        chat = (android.widget.Button) findViewById(R.id.viewChats);

        // Display user's name if logged in
        // Else, redirect user to login page
        if ( helper.isUserLoggedIn() ) {
            //userID.setText( helper.user.getEmail() );
            helper.db.getReference( helper.getUserPath() + helper.auth.getUid() ).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String firstName = (String) dataSnapshot.child( "firstName" ).getValue();
                    String middleName = (String) dataSnapshot.child( "middleName" ).getValue();
                    String lastName = (String) dataSnapshot.child( "lastName" ).getValue();

                    String name = "";

                    // Get user's name as a single string
                    // If null, use userID instead
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
                    if ( name == null ) {
                        name = helper.auth.getUid() .substring( name.length(), name.length() - 8 );
                    }
                    userName.setText( name );
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d( "TEST", databaseError.toString() );
                }
            });
        }
        else {
            startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
            finish();
        }

        // Add event handler to logout button to begin user logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.auth.signOut();
                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
                finish();
            }
        });

        // Add event handler to chat button to start the ChatRoomActivity
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( getApplicationContext(), ChatRoomActivity.class ) );
                //finish();
            }
        });

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

}
