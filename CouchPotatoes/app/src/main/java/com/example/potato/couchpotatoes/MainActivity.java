package com.example.potato.couchpotatoes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

// for the side bar activity
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The home page which shows potential matches.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DBHelper helper;
    private android.widget.Button logout;
    private android.widget.ImageButton rejectBtn;
    private android.widget.ImageButton acceptBtn;
    private android.widget.ImageButton profileBtn;
    private android.widget.Button datingBtn;
    private android.widget.Button friendsBtn;
    private android.widget.ImageButton messengerBtn;

    // For the user cards
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    // For the navigation side bar
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DBHelper();

        // places toolbar on top of the screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        // adds toggle button for the sidebar on the toolbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Find the id's of the buttons
        profileBtn = (android.widget.ImageButton) findViewById(R.id.profileBtn);
        datingBtn = (android.widget.Button) findViewById(R.id.datingBtn);
        friendsBtn = (android.widget.Button) findViewById(R.id.friendsBtn);
        messengerBtn = (android.widget.ImageButton) findViewById(R.id.messengerBtn);
        acceptBtn = (android.widget.ImageButton) findViewById(R.id.acceptBtn);
        rejectBtn = (android.widget.ImageButton) findViewById(R.id.rejectBtn);
        logout = findViewById(R.id.logout);


        // Set up how many cards are displayed as a stack
        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        mContext = getApplicationContext();
        mSwipeView.getBuilder().setDisplayViewCount(3).setSwipeDecor(new SwipeDecor()
                .setPaddingTop(20).setRelativeScale(0.01f));

        // Adds fake users for testing purposes
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

        // Simulate a swipe right or left by pressing the bottom buttons
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

        // Log out and display the log in screen
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.auth.signOut();
                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
                finish();
            }
        });


    }

    /**
     * Grabs a user's info and displays it in a swipe-able card.
     *
     *
     */
    @Layout(R.layout.card_view)
    public class UserCard {
        @com.mindorks.placeholderview.annotations.View(R.id.profileImageView)
        private ImageView profileImageView;

        @com.mindorks.placeholderview.annotations.View(R.id.nameAgeTxt)
        private TextView nameAgeTxt;

        @com.mindorks.placeholderview.annotations.View(R.id.locationNameTxt)
        private TextView locationNameTxt;

        private User mUser;
        private Context mContext;
        private SwipePlaceHolderView mSwipeView;
        private String image;

        /**
         * Create a card which displays a user's info.
         *
         * @param context - current activity
         * @param user - the user to display info for
         * @param swipeView - space used to detect a swipe
         */
        public UserCard(Context context, User user, SwipePlaceHolderView swipeView) {
            mContext = context;
            mUser = user;
            mSwipeView = swipeView;
        }

        /**
         * When the card is clicked, open a page displaying more user info.
         */
        @Click(R.id.cardView)
        private void onClick() {
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            intent.putExtra("UserInfo", mUser);
            startActivity(intent);
        }

        /**
         * Populate the image and text fields of the card.
         */
        @Resolve
        private void onResolved() {
            image = "http://www.aft.com/components/com_easyblog/themes/wireframe/images/placeholder-image.png";
            Glide.with(mContext).load(image).into(profileImageView);
            nameAgeTxt.setText(mUser.getFirstName());
            locationNameTxt.setText(mUser.getCity());
        }

        /**
         * When the card is swiped left, log the event
         */
        @SwipeOut
        private void onSwipedOut() {
            Log.d("EVENT", "onSwipedOut");
            mSwipeView.addView(this);
        }

        /**
         * When the card is released but wasn't swiped, reset to its original position
         */
        @SwipeCancelState
        private void onSwipeCancelState() {
            Log.d("EVENT", "onSwipeCancelState");
        }

        /**
         * When the card is swiped right, log the event
         */
        @SwipeIn
        private void onSwipeIn() {
            Log.d("EVENT", "onSwipedIn");
        }

        /**
         * Detects when the card is moved enough to the right to count as an accept
         */
        @SwipeInState
        private void onSwipeInState() {
            Log.d("EVENT", "onSwipeInState");
        }

        /**
         * Detects when the card is moved enough to the left to count as a reject
         */
        @SwipeOutState
        private void onSwipeOutState() {
            Log.d("EVENT", "onSwipeOutState");
        }
    }

    /*
     * Handles action in the sidebar menu
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // TODO: go to profile activity
            // Make substitute screen appear

        } else if (id == R.id.nav_chats) {
            // TODO: go to ChatActivity
            // Make substitute screen appear

        } else if (id == R.id.nav_find_matches) {
            // TODO: go to "Find Matches"
            // Make substitute screen appear

        } else if (id == R.id.nav_settings) {
            // TODO: go to SettingsActivity
            // Make substitute screen appear

        } else if (id == R.id.nav_info) {
            // TODO: go to Page with device information
            // Make substitute screen appear

        } else if (id == R.id.nav_logout) {
            // TODO: go to LoginActivity
            // Make substitute screen appear

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
