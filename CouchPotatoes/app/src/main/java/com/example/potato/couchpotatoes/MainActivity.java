package com.example.potato.couchpotatoes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
//    private android.widget.TextView userID;
//    private android.widget.TextView userName;
//    private android.widget.TextView userDescription;
    private android.widget.Button logout;
    private android.widget.ImageButton rejectBtn;
    private android.widget.ImageButton acceptBtn;

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new DBHelper();

//        userID = (android.widget.TextView) findViewById(R.id.userID);
//        userName = (android.widget.TextView) findViewById(R.id.name);
//        userDescription = (android.widget.TextView) findViewById(R.id.description);
        acceptBtn = (android.widget.ImageButton) findViewById(R.id.acceptBtn);
        rejectBtn = (android.widget.ImageButton) findViewById(R.id.rejectBtn);
        logout = findViewById(R.id.logout);

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        mContext = getApplicationContext();
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f));

//        if ( helper.isUserLoggedIn() ) {
//            userID.setText( helper.user.getEmail() );
//        }
//        else {
//            userID.setText("Not logged in ...");
//        }

//        userName.setText("Insert Name Here");
//        userDescription.setText("Insert Description Here");

        User user_test1 = new MatchedUser(null, null, "Bob", null, "Smith",
                null, null, "Los Angleles", null, null, null,
                0, 0, false, false);
        User user_test2 = new MatchedUser(null, null, "Gary", null, "Gillespie",
                null, null, "La Jolla", null, null, null,
                0, 0, false, false);
        User user_test3 = new MatchedUser(null, null, "Amy", null, "Blah",
                null, null, "New York City", null, null, null,
                0, 0, false, false);
        User user_test4 = new MatchedUser(null, null, "Thomas", null, "Anderson",
                null, null, "New York City", null, null, null,
                0, 0, false, false);
        User user_test5 = new MatchedUser(null, null, "Anita", null, "Bath",
                null, null, "New York City", null, null, null,
                0, 0, false, false);
        mSwipeView.addView(new UserCard(mContext, user_test1, mSwipeView));
        mSwipeView.addView(new UserCard(mContext, user_test2, mSwipeView));
        mSwipeView.addView(new UserCard(mContext, user_test3, mSwipeView));
        mSwipeView.addView(new UserCard(mContext, user_test4, mSwipeView));
        mSwipeView.addView(new UserCard(mContext, user_test5, mSwipeView));

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeView.doSwipe(true);
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeView.doSwipe(false);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.auth.signOut();
                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
                finish();
            }
        });
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }
}
