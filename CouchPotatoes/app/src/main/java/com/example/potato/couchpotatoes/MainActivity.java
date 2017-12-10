package com.example.potato.couchpotatoes;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.mindorks.placeholderview.SwipePlaceHolderView;

/**
 * The home page which shows potential matches.
 */
public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
    private android.widget.TextView userName;
    private android.widget.Button logout;
    private FloatingActionButton acceptBtn;
    private FloatingActionButton rejectBtn;

    // For the user cards
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private android.widget.Button chat;

    private String currUserID;

    // AsyncTasks
    private FetchCurrentUserInfoTask mFetchCurrUserInfoTask;

    protected void onCreate(Bundle savedInstanceState) {
        // Sets up the activity layout
        super.onCreate(savedInstanceState);

        //mMainActivityView = findViewById(R.id.main_activity_screen);
        //mProgressView = findViewById(R.id.main_activity_progressbar);
        //mProgressView.setVisibility();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new DBHelper();
        ActionBar actionBar = getActionBar();

        userName = (android.widget.TextView) findViewById(R.id.userName);
        chat = (android.widget.Button) findViewById(R.id.viewChats);
        currUserID = helper.getAuth().getUid();

        // Display user's name if logged in
        if ( helper.isUserLoggedIn() ) {

            // reads all the user info on the current user
//            pullCurrentUserInfo();
            attemptFetchCurUserInfo();

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
                //intent.putExtra( "userName", userName.getText() );
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

