package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.DataSnapshot;

/**
 * The home page which shows potential matches.
 */
public class MainActivity extends AppCompatActivity {
    private DBHelper helper;

    // AsyncTasks
    private FetchCurrentUserInfoTask mFetchCurrUserInfoTask;

    protected void onCreate(Bundle savedInstanceState) {
        // Sets up the activity layout
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new DBHelper();

        android.widget.Button chat = (android.widget.Button) findViewById(R.id.viewChats);

        // Display user's name if logged in
        if ( helper.isUserLoggedIn() ) {

            // reads all the user info on the current user
            attemptFetchCurUserInfo();

            startActivity(new Intent(getApplicationContext(), MatchingActivity.class));
            finish();

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

    private class FetchCurrentUserInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            helper.fetchCurrentUserInfo(getApplicationContext(), new SimpleCallback<DataSnapshot>() {
                @Override
                public void callback(DataSnapshot data) {
                    final CurrentUser currentUser = CurrentUser.getInstance();
                    currentUser.initializeFromDataSnapshot(data);
                }
            });

            return null;
        }
    }

    // Grabs the user info of the current user from firebase and sets CurrentUser from that data
    private void attemptFetchCurUserInfo() {
        if (mFetchCurrUserInfoTask != null) {
            return;
        }

        mFetchCurrUserInfoTask = new FetchCurrentUserInfoTask();
        mFetchCurrUserInfoTask.execute((Void) null);
    }
}

