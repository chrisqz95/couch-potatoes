package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
    private android.widget.TextView userID;
    private android.widget.Button logout;
    private android.widget.Button chat;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new DBHelper();

        userID = (android.widget.TextView) findViewById(R.id.userID);
        logout = (android.widget.Button) findViewById(R.id.logout);
        chat = (android.widget.Button) findViewById(R.id.viewChats);

        if ( helper.isUserLoggedIn() ) {
            userID.setText( helper.user.getEmail() );
        }
        else {
            userID.setText("Not logged in ...");
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.auth.signOut();
                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
                finish();
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( getApplicationContext(), ChatRoomActivity.class ) );
                finish();
                //String firstName = (String) getIntent().getExtras().get( "firstName" );
                //String middleName = (String) getIntent().getExtras().get( "middleName" );
                //String lastName = (String) getIntent().getExtras().get( "lastName" );

                //Intent intent = new Intent( getApplicationContext(), ChatRoomActivity.class );
                //intent.putExtra( "firstName", firstName );
                //intent.putExtra( "middleName", middleName );
                //intent.putExtra( "lastName", lastName );
                //startActivity( intent );
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
