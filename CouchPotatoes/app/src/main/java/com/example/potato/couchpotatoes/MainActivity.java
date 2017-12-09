package com.example.potato.couchpotatoes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import org.w3c.dom.Text;
import java.lang.Double;
import java.lang.Number.*;

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
                //intent.putExtra( "userName", userName.getText() );
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
                    // Log.d( "TEST", field.toString() );
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
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }

}

