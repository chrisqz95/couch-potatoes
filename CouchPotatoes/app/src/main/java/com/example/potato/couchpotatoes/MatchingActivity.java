package com.example.potato.couchpotatoes;

import java.util.ArrayDeque;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

// for the side bar activity
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 *
 */
public class MatchingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String[] tabTitles = new String[] { "Date", "Friend" };

    private final int VIEW_PAGER_DATE_TAB_POSITION = 0;
    private final int VIEW_PAGER_FRIEND_TAB_POSITION = 1;

    private DBHelper helper;

    Toolbar toolbar;

    // For the side navigation bar
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private android.widget.TextView sidebarUserName;
    private android.widget.TextView sidebarUserEmail;

    // list of matches for dating and friending
    private ArrayList<String> matchedDateList = new ArrayList<>();
    private ArrayList<String> matchedFriendList = new ArrayList<>();
    private ArrayDeque<MatchedUser> matchedDateQueue;
    private ArrayDeque<MatchedUser> matchedFriendQueue;

    // for tabs
    private MatchFragmentPagerAdapter adapter;
    private MatchViewPager viewPager;

    // loading spinner
    private ProgressBar spinner;

    // main content layout
    private LinearLayout likeAndDislikeLayout;

    // profile image
    private ImageView imgView;

    private int currTab = 0;

    private String currUserID;

    private View sideBarHeader;

    private FloatingActionButton acceptBtn;
    private FloatingActionButton rejectBtn;

    // For the user cards
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        // TODO change this to singleton
        helper = new DBHelper();

        currUserID = helper.getAuth().getUid();

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.matching_tabs);
        imgView = (ImageView) findViewById(R.id.imageView2);
        viewPager = (MatchViewPager) findViewById(R.id.matching_viewpager);
        likeAndDislikeLayout = (LinearLayout) findViewById(R.id.likeAndDislikeLayout);
        spinner = (ProgressBar)findViewById(R.id.progressBar);

        displayLoading();

        adapter = new MatchFragmentPagerAdapter(getSupportFragmentManager());

        // add fragments to the view pager
//        adapter.addFragment(MatchPageFragment.newInstance(matchedDateList), tabTitles[0]);
//        adapter.addFragment(MatchPageFragment.newInstance(matchedFriendList), tabTitles[1]);
        adapter.addFragment(MatchPageFragment.newInstance(matchedDateQueue), tabTitles[0]);
        adapter.addFragment(MatchPageFragment.newInstance(matchedFriendQueue), tabTitles[1]);

        // Change behavior of like and dislike buttons based on currently selected tab
        // TODO Create methods to handle behaviour below and reduce code redundancy
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                FloatingActionButton likeButton = findViewById(R.id.fab_match);
                FloatingActionButton dislikeButton = findViewById(R.id.fab_unmatch);

                // If Date tab selected, have like button add to Date object on Firebase
                if ( position == VIEW_PAGER_DATE_TAB_POSITION ) {
                    currTab = VIEW_PAGER_DATE_TAB_POSITION;

                }
                // If Friend tab selected, have like button add to Befriend object on Firebase
                else if ( position == VIEW_PAGER_FRIEND_TAB_POSITION  ){
                    currTab = VIEW_PAGER_FRIEND_TAB_POSITION;

                }
            } // end of method onPageSelected

            @Override
            public void onPageScrollStateChanged(int state) {}
        }); // end of method  onPageScrollStateChanged()


        // finish tab setup
        viewPager.setAdapter(adapter);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

		// places toolbar on top of the screen
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        // Fetch list of potential friends from Firebase
        helper.fetchPotentialMatches(getApplicationContext(), DBHelper.MATCH_TYPE.FRIEND, new SimpleCallback<ArrayList<MatchedUser>>() {
            @Override
            public void callback(ArrayList<MatchedUser> data) {
                matchedFriendQueue = new ArrayDeque<>(data);
                if (matchedFriendQueue.isEmpty()) {
                    Log.d("MATCH CREATION", "failed to fill matched friend queue");
                }
            }
        });

        // Fetch list of potential dates from Firebase
        helper.fetchPotentialMatches(getApplicationContext(), DBHelper.MATCH_TYPE.DATE, new SimpleCallback<ArrayList<MatchedUser>>() {
            @Override
            public void callback(ArrayList<MatchedUser> data) {
                matchedDateQueue = new ArrayDeque<>(data);
                if (matchedDateQueue.isEmpty()) {
                    Log.d("MATCH CREATION", "failed to fill matched date queue");
                }

                // done loading information, display it
                displayContents();
            }
        });

        /*
         * TODO: button listeners, set the initial images, set background pulling new matched users
         * TODO: onClick listeners for like, dislike, image clicked
         */
    }

    // Handles pressing back button in bottom navigation bar when sidebar is on the screen
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void navBarSetup() {
        // enables toggle button on toolbar to open the sidebar
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // set up side navigation bar layout
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        sidebarUserName = (android.widget.TextView) navView.getHeaderView(0)
                .findViewById(R.id.sidebar_username);
        sidebarUserEmail = (android.widget.TextView) navView.getHeaderView(0)
                .findViewById(R.id.sidebar_user_email);

        // fetches user's name and email
        String displayName = helper.getAuthUserDisplayName();
        String displayEmail = helper.getUser().getEmail();

        // displays user's name and email on the sidebar header
        sidebarUserName.setText( displayName );
        sidebarUserEmail.setText( displayEmail );
    }

    // Handles action in the sidebar menu TODO change so that this isn't duplicated code
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

		if (id == R.id.nav_profile) {
            Intent intent = new Intent( getApplicationContext(), PreferencesActivity.class );
            startActivity( intent );
        } else if (id == R.id.nav_matches) {
            // TODO: if not already in page, redirect page to MainActivity

		} else if (id == R.id.nav_chats) {
            // redirects user to ChatRoomActivity.xml
            Intent intent = new Intent( getApplicationContext(), ChatRoomActivity.class );
            startActivity( intent );

        }
        // Remove for now. Uncomment later if needed.
        //else if (id == R.id.nav_settings) {
            //Intent intent = new Intent( getApplicationContext(), SettingsActivity.class );
            //startActivity( intent );
        //}
        else if (id == R.id.nav_info) {
            // TODO: go to Page with device information
            Intent intent = new Intent( getApplicationContext(), AboutUsActivity.class );
            startActivity( intent );
        } else if (id == R.id.nav_logout) {
            // logs out and redirects user to LoginActivity.xml
            helper.getAuth().signOut();
            startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    } // End of method onNavigationItemSelected

    /**
     * Change screen to be loading.
     */
    private void displayLoading() {
        spinner.setVisibility(View.VISIBLE);
        imgView.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        likeAndDislikeLayout.setVisibility(View.GONE);
    }

    /**
     * display normal contents to screen
     */
    private void displayContents() {
        spinner.setVisibility(View.GONE);
        imgView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        likeAndDislikeLayout.setVisibility(View.VISIBLE);
    }
}
