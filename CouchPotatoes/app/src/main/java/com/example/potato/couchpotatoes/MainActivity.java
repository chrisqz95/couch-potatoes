package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
        if ( helper.isUserLoggedIn() ) {
            // String displayName = helper.getAuthUserDisplayName();

            // userName.setText( displayName );
            startActivity(new Intent(getApplicationContext(), MatchingActivity.class));
            finish();
        }
        // Else, redirect user to login page
        else {
            startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
            finish();
        }

        // Add event handler to logout button to begin user logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.getAuth().signOut();
                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
                finish();
            }
        });

        // Add event handler to chat button to start the ChatRoomActivity
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getApplicationContext(), ChatRoomActivity.class );
                //intent.putExtra( "userName", userName.getText() );
                startActivity( intent );
            }
        });
    }
}

