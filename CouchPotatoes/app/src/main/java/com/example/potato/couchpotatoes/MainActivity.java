package com.example.potato.couchpotatoes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
    private android.widget.TextView userName;
    private android.widget.Button logout;
    private android.widget.Button chat;

    private PullUserInfoTask mPullTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mMainActivityView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new DBHelper();

        userName = (android.widget.TextView) findViewById(R.id.userName);
        logout = (android.widget.Button)  findViewById(R.id.logout);
        chat = (android.widget.Button) findViewById(R.id.viewChats);

        // Display user's name if logged in
        if ( helper.isUserLoggedIn() ) {

            // TODO: complete code in the method attemptPullUserInfo()

            // fake initialized variables for testing
            String testString = "Hello this message is from Main";
            int testInt = 27;
            CurrentUser user_test2 = new CurrentUser("user@test.com", "10101", "Mervin",
                    "", "Ng", "03/27/1997", "Male", "Chicago",
                    "Illinois", "USA", "Seize the day!", 0, 0,
                    false, false);

            // passes data from MainActivity to a dummy activity 'DummyActivity'
            Intent mIntent = new Intent(getApplicationContext(), DummyActivity.class);
            mIntent.putExtra("testString1", testString);
            mIntent.putExtra("testInt1", testInt);
            mIntent.putExtra("testCurrentUser", (Parcelable) user_test2);
            startActivity(mIntent);
            finish();

            // User is logged in, load up the user object with all the logged user's info!
            // This will also render MatchActivity.class upon success
            //attemptPullUserInfo();

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

        mMainActivityView = findViewById(R.id.main_activity_screen);
        mProgressView = findViewById(R.id.main_activity_progressbar);
    }


    /**
     * Shows the progress UI and hides the Main Activity layout until user object is loaded
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMainActivityView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainActivityView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainActivityView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMainActivityView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Attempt to pull the user's info from Firebase. Once the user info is retrieved inside a user
     * object, it will be passed into a bundle so we can pass that object into MatchingActivity
     */
    private void attemptPullUserInfo() {
//        if( mPullTask == null )
//            return;

        // TODO if there is any logic that needs to go before starting the task is put here
        // ^comment: this logic work could possibly be done through onPreExecute on the AsyncTask

        // TODO: retrieve the correct authorized user object or the correct info to fill in a user object
        // fake user being passed down to Matching Activity
        // (for testing purposes only)
        CurrentUser user_test1 = new CurrentUser("user@test.com", null, "Robin",
                "McLaurin", "Williams", null, null, "Chicago",
                "Illinois", "USA", "Seize the day!", 0, 0,
                false, false);

        // while system loads up the user object and pass it to MatchingActivity,
        // system shall display a progress bar on screen
        showProgress(true);

        mPullTask = new PullUserInfoTask(user_test1);
        // TODO:  we can pass in anything into the method below and will appear in doInBackground.
        // see documentation for more details
        mPullTask.execute((Void) null);
    }


    // If we need to pass some sort of information to the asyncTask, replace the first
    // Void with the object type we need
    public class PullUserInfoTask extends AsyncTask<Void, Void, Boolean> {

        // TODO use Firebase to fill the user object in this information
        private CurrentUser mUser;
        // ^problem: FirebaseUser is NOT the same object as CurrentUser or User class

        // TODO pass in any needed information here
        PullUserInfoTask(CurrentUser user) {
            mUser = user;
        }

        // alternatively, we can pass in information during execute in which we will get the info here
        @Override
        protected Boolean doInBackground(Void...params) {

            // TODO: implement myParcelable class that extends the "Parcelable" interface
            // How to send user object to another activity via the Parcelable interface
            // https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
            // MyParcelable mParcelableUser;

            showProgress(false);

            String testString = "This is a test string.";

            Intent mIntent = new Intent(getApplicationContext(), DummyActivity.class);
            mIntent.putExtra("TestString", testString); // stores String object in the Intent object
            mIntent.putExtra("UserObject", (Parcelable) mUser); // stores User object in the Intent object
            startActivity(mIntent);
            finish();

            // TODO: on success, insert user object into a bundle and put it inside the intent!
            // We will need to change User object to implement Parcelable so that we can pass both Current user and
            // MatchedUser to different activites/fragments
//            Intent mIntent = new Intent(getApplicationContext(), DummyActivity.class);
//            mIntent.putExtra("TestString", testString); // stores String object in the Intent object
//            mIntent.putExtra("UserObject", (Parcelable) mUser); // stores User object in the Intent object
//            startActivity(mIntent);
//            finish();

            // TODO:  to retrieve it later, use the line below!
            //CurrentUser user = getIntent().getParcelableExtra("UserObject");

            return false;
        }
    }
}

