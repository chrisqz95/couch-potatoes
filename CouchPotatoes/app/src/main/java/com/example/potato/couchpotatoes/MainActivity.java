package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
    private android.widget.TextView userName;
    private android.widget.Button logout;
    private android.widget.Button chat;

    private PullUserInfoTask mPullTask = null;

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

    /**
     * Attempt to pull the user's info
     */
    private void attemptPullUserInfo() {
        if (mPullTask == null) {
            return;
        }

        // TODO if there is any logic that needs to go before starting the task is put here

        mPullTask = new PullUserInfoTask();
        // TODO:  we can pass in anything into the method below and will appear in doInBackground.
        // see documentation for more details
        mPullTask.execute((Void) null);
    }


    // If we need to pass some sort of information to the asyncTast, replace the first
    // Void with the object type we need
    public class PullUserInfoTask extends AsyncTask<Void, Void, Boolean> {

        // TODO use firebase to fill in this information
        private CurrentUser user;



        // TODO pass in any needed information here
        PullUserInfoTask() {}

        // alternatively, we can pass in information during execute in which we will get the info here
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: on success do this
            // We will need to change User object to implement Parcelable so that we can pass both Current user and
            // MatchedUser to different activites/fragments
//        Intent intent = new Intent(getApplicationContext(), MatchingActivity.class);
//        intent.putExtra("UserObject", user);
//        startActivity(intent);
            // TODO to retrieve it later, use
            // CurrentUser user = getIntent().getParcelableExtra("UserObject");

            return false;
        }

    }

}

