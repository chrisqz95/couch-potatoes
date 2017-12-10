package com.example.potato.couchpotatoes;

import java.util.ArrayList;

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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.Iterator;
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

import android.widget.TextView;
import android.widget.Toast;


public class MatchingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DBHelper dbHelper;
    private final String[] tabTitles = new String[] { "Date", "Friend" };

    private final int VIEW_PAGER_DATE_TAB_POSITION = 0;
    private final int VIEW_PAGER_FRIEND_TAB_POSITION = 1;

    // For the side navigation bar
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private android.widget.TextView sidebarUserName;
    private android.widget.TextView sidebarUserEmail;

    // List of matches for dating and friending
    private ArrayList<String> matchedDateList = new ArrayList<>();
    private ArrayList<String> matchedFriendList = new ArrayList<>();

    private MatchFragmentPagerAdapter adapter;
    private MatchViewPager viewPager;
    private MatchPageFragment datingPage;
    private MatchPageFragment friendPage;

    private ProgressBar spinner;

    private LinearLayout likeAndDislikeLayout;
    private FloatingActionButton likeButton;
    private FloatingActionButton dislikeButton;

    private ImageView imgView;
    private CircleImageView circleProfilePic;
    private ImageView profilePic;

    private int currTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        dbHelper = DBHelper.getInstance();

        currUserID = dbHelper.getAuth().getUid();

        final TabLayout tabLayout = findViewById(R.id.matching_tabs);
        likeButton = findViewById(R.id.fab_match);
        dislikeButton = findViewById(R.id.fab_unmatch);
        imgView = findViewById(R.id.imageView2);
        imgView.setVisibility(View.GONE);

        viewPager = findViewById(R.id.matching_viewpager);
        viewPager.setVisibility(View.GONE);
        likeAndDislikeLayout =  findViewById(R.id.likeAndDislikeLayout);
        likeAndDislikeLayout.setVisibility(View.GONE);
        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        adapter = new MatchFragmentPagerAdapter(getSupportFragmentManager());

        // Add fragments to the view pager
        datingPage = MatchPageFragment.newInstance(matchedDateList, true);
        friendPage = MatchPageFragment.newInstance(matchedFriendList, false);
        adapter.addFragment(datingPage, tabTitles[0]);
        adapter.addFragment(friendPage, tabTitles[1]);
        viewPager.setAdapter(adapter);

        // Change behavior of like and dislike buttons based on currently selected tab
        addPageChangeListener();

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        // Fetch list of potential friends from Firebase
        fetchPotentFriendsFromFirebase();

        // Fetch list of potential dates from Firebase
        fetchPotentDatesFromFirebase();

        // places toolbar on top of the screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        // set up the side navigation bar on the left side of screen
        mDrawer = (DrawerLayout) findViewById(R.id.match_drawer_layout);
        navView = (NavigationView) findViewById(R.id.match_nav_view);
        setSideBarDrawer( mDrawer, navView, toolbar , dbHelper);
    }

    // Make sure the navView highlight the correct location on the sidebar
    @Override
    public void onResume() {
        super.onResume();
        // highlight the current location
        navView.setCheckedItem(R.id.nav_matches);
    }

    /*
     * The method sets up the navigation drawer (a.k.a. the sidebar) on the
     * left side of the screen.
     */
    private void setSideBarDrawer( DrawerLayout mDrawer, NavigationView navView,
                                   Toolbar toolbar, DBHelper helper) {
        // enables toggle button on toolbar to open the sidebar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // set up side navigation bar layout
        navView.setNavigationItemSelectedListener(this);

        // Want to display icons in original color scheme
        navView.setItemIconTintList(null);

        // highlight the current location
        navView.setCheckedItem(R.id.nav_matches);

        // sets up TextViews in sidebar to display the user's name and email
        sidebarUserName = (android.widget.TextView) navView.getHeaderView(0)
                .findViewById(R.id.sidebar_username);
        sidebarUserEmail = navView.getHeaderView(0)
                .findViewById(R.id.sidebar_user_email);
        setSideBarText( sidebarUserName, sidebarUserEmail, helper );
    }

    /*
     * This method sets the text of the TextViews in the sidebar to display the
     * user's name and email.
     */
    private void setSideBarText( TextView nameView, TextView emailView, DBHelper helper ) {
        // fetches user's name and email
        String displayName = helper.getAuthUserDisplayName();
        String displayEmail = helper.getUser().getEmail();

        nameView.setText( displayName );
        emailView.setText( displayEmail );
    }

    // Handles pressing back button in bottom navigation bar when sidebar is on the screen
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.match_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Handles action in the sidebar menu
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent( getApplicationContext(), PreferencesActivity.class );
            startActivity( intent );
        } else if (id == R.id.nav_matches) {
        } else if (id == R.id.nav_chats) {
            // redirects user to ChatRoomActivity.xml
            Intent intent = new Intent( getApplicationContext(), ChatRoomActivity.class );
            startActivity( intent );

        }
        else if (id == R.id.nav_settings) {
            startActivity( new Intent( getApplicationContext(), AppSettingsActivity.class ) );
        } else if (id == R.id.nav_info) {
            Intent intent = new Intent( getApplicationContext(), AboutUsActivity.class );
            startActivity( intent );
        } else if (id == R.id.nav_logout) {
            // logs out and redirects user to LoginActivity.xml
            dbHelper.getAuth().signOut();
            startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.match_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // Populates the machtedFriendList with the list of potential friends and notifies the adapter of changes
    public void fetchPotentFriendsFromFirebase () {
        dbHelper.getDb().getReference( dbHelper.getPotentFriendPath() + dbHelper.getAuth().getUid() ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> potentDates = dataSnapshot.getChildren().iterator();

                matchedFriendList.clear();

                while ( potentDates.hasNext() ) {
                    matchedFriendList.add( (String) potentDates.next().getValue() );
                }

                if ( matchedFriendList.isEmpty() ) {
                    resetMatchingView();
                }

                adapter.notifyDataSetChanged();
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem( currTab );

                // Hides the progress bar
                hideProgressBar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // Populates the machtedDateList with the list of potential dates and notifies the adapter of changes
    public void fetchPotentDatesFromFirebase () {
        dbHelper.getDb().getReference( dbHelper.getPotentDatePath() + dbHelper.getAuth().getUid() ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> potentDates = dataSnapshot.getChildren().iterator();

                matchedDateList.clear();

                while ( potentDates.hasNext() ) {
                    matchedDateList.add( (String) potentDates.next().getValue() );
                }

                adapter.notifyDataSetChanged();
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem( currTab );

                // NOTE: Temporary workaround for now: ( Want functionality before Layout tabs are pressed )
                // Try to fetch profile pic from Firebase and update ImageView
                // If profile pic is null, display default profile pic instead
                if ( !matchedDateList.isEmpty() ) {
                    displayPotentMatchProfilePic( matchedDateList.get(0) );
                    addLikeDislikeListeners();
                }

                // Done fetching potent matches from Firebase
                // Hide spinner and display Matching Activity Views
                hideProgressBar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * Displays the progress bar while hiding everything else
     */
    private void showProgressBar() {
        spinner.setVisibility(View.VISIBLE);
        imgView.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        likeAndDislikeLayout.setVisibility(View.GONE);
    }

    /**
     * Hides the progress bar while making everything else visible
     */
    private void hideProgressBar() {
        spinner.setVisibility(View.GONE);
        imgView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        likeAndDislikeLayout.setVisibility(View.VISIBLE);
    }

    private void resetMatchingView() {
        // Set default image
        resetImageView();

        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem( currTab );
    }

    private void resetImageView() {
        // Set default image
        String uri = "@drawable/profile";

        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        imgView.setImageDrawable(res);
    }

    private void likeUser( int tabPos ) {
        String currUserID = dbHelper.getAuth().getUid();
        String potentMatchID = "";
        String timestamp = dbHelper.getNewTimestamp();
        showProgressBar();

        if ( tabPos == VIEW_PAGER_FRIEND_TAB_POSITION ) {
            potentMatchID = matchedFriendList.get(0);
            dbHelper.addToBefriend(currUserID, potentMatchID, timestamp);
        }
        else {
            potentMatchID = matchedDateList.get(0);
            dbHelper.addToDate(currUserID, potentMatchID, timestamp);
        }

        Toast.makeText(MatchingActivity.this, "Liked!", Toast.LENGTH_SHORT).show();
        dbHelper.addToLike(currUserID, potentMatchID, timestamp);
    }

    private void dislikeUser( int tabPos ) {
        String currUserID = dbHelper.getAuth().getUid();
        String potentMatchID = "";
        String timestamp = dbHelper.getNewTimestamp();
        showProgressBar();

        if ( tabPos == VIEW_PAGER_FRIEND_TAB_POSITION ) {
            potentMatchID = matchedFriendList.get(0);
        }
        else {
            potentMatchID = matchedDateList.get(0);
        }

        Toast.makeText(MatchingActivity.this, "Disliked!", Toast.LENGTH_SHORT).show();
        dbHelper.addToDislike(currUserID, potentMatchID, timestamp);
    }

    private void displayPotentMatchProfilePic( String matchUserID ) {
        // Try to fetch profile pic from Firebase and update ImageView
        // If profile pic is null, display default profile pic instead
        dbHelper.getDb().getReference(dbHelper.getUserPath()).child( matchUserID ).child("profile_pic").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = "";

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    url = (String) dataSnapshot.getValue();
                    if (imgView != null) {
                        StorageReference uriRef = dbHelper.getStorage().getReferenceFromUrl(url);

                        // Set ImageView to contain photo
                        Glide.with(getApplicationContext())
                                .using(new FirebaseImageLoader())
                                .load(uriRef)
                                .into(imgView);
                    }
                } else {
                    // Default Profile Pic
                    resetImageView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TEST", databaseError.getMessage());
            }
        });
    }

    private void addLikeDislikeListeners() {

        imgView.setOnTouchListener(new OnSwipeTouchListener(MatchingActivity.this) {

            /**
             * When the picture is swiped left, dislike the user
             */
            @Override
            public void onSwipeLeft() {
                if ( !matchedDateList.isEmpty() ) {
                    dislikeUser( currTab );
                } else {
                    resetMatchingView();
                }
            }

            /**
             * When the picture is swiped right, like the user
             */
            @Override
            public void onSwipeRight() {
                if ( !matchedDateList.isEmpty() ) {
                    likeUser( currTab );
                } else {
                    resetMatchingView();
                }
            }
        });

        // NOTE: Temporary workaround for now: Set default action button listeners ( before Layout tabs are pressed )
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !matchedDateList.isEmpty() ) {
                    likeUser( currTab );
                } else {
                    resetMatchingView();
                }
            }
        });

        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !matchedDateList.isEmpty() ) {
                    dislikeUser( currTab );
                } else {
                    resetMatchingView();
                }
            }
        });
    }

    private void addPageChangeListener() {
        // Change behavior of like and dislike buttons based on currently selected tab
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {

                // If Date tab selected, have like button add to Date object on Firebase
                if ( position == VIEW_PAGER_DATE_TAB_POSITION ) {
                    currTab = VIEW_PAGER_DATE_TAB_POSITION;

                    // Try to fetch profile pic from Firebase and update ImageView
                    // If profile pic is null, display default profile pic instead
                    if ( !matchedDateList.isEmpty() ) {
                        displayPotentMatchProfilePic( matchedDateList.get(0) );

                        addLikeDislikeListeners();
                    }
                    else {
                        resetImageView();
                    }
                }
                // If Friend tab selected, have like button add to Befriend object on Firebase
                else if ( position == VIEW_PAGER_FRIEND_TAB_POSITION  ){
                    currTab = VIEW_PAGER_FRIEND_TAB_POSITION;

                    // Try to fetch profile pic from Firebase and update ImageView
                    // If profile pic is null, display default profile pic instead
                    if ( !matchedFriendList.isEmpty() ) {
                        displayPotentMatchProfilePic( matchedFriendList.get(0) );
                        addLikeDislikeListeners();
                    }
                    else {
                        resetImageView();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
}