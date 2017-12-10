package com.example.potato.couchpotatoes;

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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
	private DBHelper helper;
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
    private String currUserID;
    private View sideBarHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        helper = new DBHelper();

        currUserID = helper.getAuth().getUid();

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.matching_tabs);
        //profilePic = (CircleImageView) findViewById(R.id.profile_image);
        likeButton = (FloatingActionButton) findViewById(R.id.fab_match);
        dislikeButton = (FloatingActionButton) findViewById(R.id.fab_unmatch);
        imgView = (ImageView) findViewById(R.id.imageView2);
        imgView.setVisibility(View.GONE);

        viewPager = (MatchViewPager) findViewById(R.id.matching_viewpager);
        viewPager.setVisibility(View.GONE);
        likeAndDislikeLayout = (LinearLayout) findViewById(R.id.likeAndDislikeLayout);
        likeAndDislikeLayout.setVisibility(View.GONE);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        adapter = new MatchFragmentPagerAdapter(getSupportFragmentManager());

        // Add fragments to the view pager
        datingPage = MatchPageFragment.newInstance(matchedDateList, true);
        friendPage = MatchPageFragment.newInstance(matchedFriendList, false);
        adapter.addFragment(datingPage, tabTitles[0]);
        adapter.addFragment(friendPage, tabTitles[1]);
        viewPager.setAdapter(adapter);

        // Change behavior of like and dislike buttons based on currently selected tab
        // TODO Create methods to handle behaviour below and reduce code redundancy
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // If Date tab selected, have like button add to Date object on Firebase
                if ( position == VIEW_PAGER_DATE_TAB_POSITION ) {
                    currTab = VIEW_PAGER_DATE_TAB_POSITION;

                    // Try to fetch profile pic from Firebase and update ImageView
                    // If profile pic is null, display default profile pic instead
                    if ( !matchedDateList.isEmpty() ) {
                        helper.getDb().getReference(helper.getUserPath()).child(matchedDateList.get(0)).child("profile_pic").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String url = "";

                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                    url = (String) dataSnapshot.getValue();
                                    if (imgView != null) {
                                        StorageReference uriRef = helper.getStorage().getReferenceFromUrl(url);

                                        // Set ImageView to contain photo
                                        Glide.with(getApplicationContext())
                                                .using(new FirebaseImageLoader())
                                                .load(uriRef)
                                                .into(imgView);
                                    }
                                } else {
                                    // Default Profile Pic
                                    // TODO Add method to DBHelper to get this
                                    // url = "gs://couch-potatoes-47758.appspot.com/Default/ProfilePic/potato_1_profile_pic.png";
                                    String uri = "@drawable/profile";

                                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                                    Drawable res = getResources().getDrawable(imageResource);
                                    imgView.setImageDrawable(res);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("TEST", databaseError.getMessage());
                            }
                        });

                        // Sets the buttons and image to refer to the dating page
                        updateLikeDislikeButtons(true);
                    }
                    else {
                        // Default profile pic
                        String uri = "@drawable/profile";

                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        Drawable res = getResources().getDrawable(imageResource);
                        imgView.setImageDrawable(res);
                    }
                }
                // If Friend tab selected, have like button add to Befriend object on Firebase
                else if ( position == VIEW_PAGER_FRIEND_TAB_POSITION  ){
                    currTab = VIEW_PAGER_FRIEND_TAB_POSITION;

                    // Try to fetch profile pic from Firebase and update ImageView
                    // If profile pic is null, display default profile pic instead
                    if ( !matchedFriendList.isEmpty() ) {
                        helper.getDb().getReference(helper.getUserPath()).child(matchedFriendList.get(0)).child("profile_pic").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String url = "";

                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                    url = (String) dataSnapshot.getValue();
                                    if (imgView != null) {
                                        StorageReference uriRef = helper.getStorage().getReferenceFromUrl(url);

                                        // Set ImageView to contain photo
                                        Glide.with(getApplicationContext())
                                                .using(new FirebaseImageLoader())
                                                .load(uriRef)
                                                .into(imgView);
                                    }
                                } else {
                                    // Default Profile Pic
                                    // TODO Add method to DBHelper to get this
                                    // url = "gs://couch-potatoes-47758.appspot.com/Default/ProfilePic/potato_2_profile_pic.png";
                                    String uri = "@drawable/profile";

                                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                                    Drawable res = getResources().getDrawable(imageResource);
                                    imgView.setImageDrawable(res);
                                }

                                /*
                                if (imgView != null) {
                                    StorageReference uriRef = helper.getStorage().getReferenceFromUrl(url);

                                    // Set ImageView to contain photo
                                    Glide.with(getApplicationContext())
                                            .using(new FirebaseImageLoader())
                                            .load(uriRef)
                                            .into(imgView);
                                }
                                */
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("TEST", databaseError.getMessage());
                            }
                        });

                        // Sets the buttons and image to refer to the friend page
                        updateLikeDislikeButtons(false);
                    }
                    else {
                        // Default profile pic
                        String uri = "@drawable/profile";

                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        Drawable res = getResources().getDrawable(imageResource);
                        imgView.setImageDrawable(res);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


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

        /* NOT WORKING
        final LayoutInflater factory = getLayoutInflater();

        sideBarHeader = factory.inflate(R.layout.sidebar_header, null);

        profilePic = sideBarHeader.findViewById(R.id.sidebarProfilePic);
        circleProfilePic = (CircleImageView) sideBarHeader.findViewById(R.id.profile_image);

        circleProfilePic.setVisibility(View.VISIBLE);
        profilePic.setBackgroundColor( Color.WHITE );

        //displayProfilePic();
        */

        // enables toggle button on toolbar to open the sidebar
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // set up side navigation bar layout
        navView = (NavigationView) findViewById(R.id.match_nav_view);
        navView.setNavigationItemSelectedListener(this);

        // Want to display icons in original color scheme
        navView.setItemIconTintList(null);

        // highlight the current location
        navView.setCheckedItem(R.id.nav_matches);

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

    // Make sure the navView highlight the correct location
    @Override
    public void onResume() {
        super.onResume();
        // highlight the current location
        navView.setCheckedItem(R.id.nav_matches);
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
    
    // Handles action in the sidebar menu
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
        else if (id == R.id.nav_settings) {
            //Intent intent = new Intent( getApplicationContext(), SettingsActivity.class );
            //startActivity( intent );
            startActivity( new Intent( getApplicationContext(), AppSettingsActivity.class ) );
        } else if (id == R.id.nav_info) {
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
    }

    // Populates the machtedFriendList with the list of potential friends and notifies the adapter of changes
    public void fetchPotentFriendsFromFirebase () {
        helper.getDb().getReference( helper.getPotentFriendPath() + helper.getAuth().getUid() ).addValueEventListener(new ValueEventListener() {
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
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }

    // Populates the machtedDateList with the list of potential dates and notifies the adapter of changes
    public void fetchPotentDatesFromFirebase () {
        helper.getDb().getReference( helper.getPotentDatePath() + helper.getAuth().getUid() ).addValueEventListener(new ValueEventListener() {
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
                // TODO Create method to do this
                if ( !matchedDateList.isEmpty() ) {
                    helper.getDb().getReference(helper.getUserPath()).child(matchedDateList.get(0)).child("profile_pic").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String url = "";

                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                url = (String) dataSnapshot.getValue();
                                if (imgView != null) {
                                    StorageReference uriRef = helper.getStorage().getReferenceFromUrl(url);

                                    // Set ImageView to contain photo
                                    Glide.with(getApplicationContext())
                                            .using(new FirebaseImageLoader())
                                            .load(uriRef)
                                            .into(imgView);
                                }
                            } else {
                                // Default Profile Pic
                                // TODO Add method to DBHelper to get this
                                //url = "gs://couch-potatoes-47758.appspot.com/Default/ProfilePic/potato_1_profile_pic.png";
                                String uri = "@drawable/profile";

                                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                                Drawable res = getResources().getDrawable(imageResource);
                                imgView.setImageDrawable(res);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("TEST", databaseError.getMessage());
                        }
                    });

                    imgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d( "TEST", "IMG CLICKED" );
                            //TODO Go to new activity to view potential match's photos
                            //Intent intent = new Intent( getApplicationContext(), ( INSERT IMAGE GALLERY ACTIVITY CLASS ) );
                            //intent.putExtra( "targetUserID", matchedDateList.get(0) );
                            //startActivity( intent );
                        }
                    });

                    // Sets the buttons and image to refer to the dating page
                    updateLikeDislikeButtons(true);
                }

                // Done fetching potent matches from Firebase
                // Hide spinner and display Matching Activity Views
                hideProgressBar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }

    private void displayProfilePic() {
        helper.getDb().getReference(helper.getUserPath()).child( currUserID ).child("profile_pic").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = "";

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    url = (String) dataSnapshot.getValue();

                    if (profilePic != null) {
                        StorageReference uriRef = helper.getStorage().getReferenceFromUrl(url);

                        // Set ImageView to contain photo
                        Glide.with(sideBarHeader.getContext().getApplicationContext())
                                .using(new FirebaseImageLoader())
                                .load(uriRef)
                                .into(profilePic);
                    }
                    else {
                        Log.d( "TEST", "TARGET NULL" );
                    }
                } else {
                    // Default Profile Pic
                    // TODO Add method to DBHelper to get this
                    //url = "gs://couch-potatoes-47758.appspot.com/Default/ProfilePic/potato_1_profile_pic.png";
                    String uri = "@drawable/profile";

                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res = getResources().getDrawable(imageResource);
                    profilePic.setImageDrawable(res);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TEST", databaseError.getMessage());
            }
        });
    }

    private void updateLikeDislikeButtons(final boolean dating) {
        // Creates a gesture listener for the user image
        imgView.setOnTouchListener(new OnSwipeTouchListener(MatchingActivity.this) {
            /**
             * When the picture is swiped left, dislike the user
             */
            @Override
            public void onSwipeLeft() {
                if ( !matchedDateList.isEmpty() ) {
                    String currUserID = helper.getAuth().getUid();
                    String potentMatchID = matchedDateList.get(0);
                    String timestamp = helper.getNewTimestamp();
                    showProgressBar();

                    Toast.makeText(MatchingActivity.this, "Disliked!", Toast.LENGTH_SHORT).show();
                    helper.addToDislike(currUserID, potentMatchID, timestamp);
                }
                else {
                    resetMatchingView();
                }
            }

            /**
             * When the picture is swiped right, like the user
             */
            @Override
            public void onSwipeRight() {
                if ( !matchedDateList.isEmpty() ) {
                    String currUserID = helper.getAuth().getUid();
                    String potentMatchID = matchedDateList.get(0);
                    String timestamp = helper.getNewTimestamp();
                    showProgressBar();

                    Toast.makeText(MatchingActivity.this, "Liked!", Toast.LENGTH_SHORT).show();
                    helper.addToLike(currUserID, potentMatchID, timestamp);

                    if (dating)
                        helper.addToDate(currUserID, potentMatchID, timestamp);
                    else
                        helper.addToBefriend(currUserID, potentMatchID, timestamp);
                }
                else {
                    resetMatchingView();
                }
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !matchedDateList.isEmpty() ) {
                    String currUserID = helper.getAuth().getUid();
                    String potentMatchID = matchedDateList.get(0);
                    String timestamp = helper.getNewTimestamp();
                    showProgressBar();

                    Toast.makeText(MatchingActivity.this, "Liked!", Toast.LENGTH_SHORT).show();
                    helper.addToLike(currUserID, potentMatchID, timestamp);

                    if (dating)
                        helper.addToDate(currUserID, potentMatchID, timestamp);
                    else
                        helper.addToBefriend(currUserID, potentMatchID, timestamp);
                }
                else {
                    resetMatchingView();
                }
            }
        });

        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !matchedDateList.isEmpty() ) {
                    String currUserID = helper.getAuth().getUid();
                    String potentMatchID = matchedDateList.get(0);
                    String timestamp = helper.getNewTimestamp();
                    showProgressBar();

                    Toast.makeText(MatchingActivity.this, "Disliked!", Toast.LENGTH_SHORT).show();
                    helper.addToDislike(currUserID, potentMatchID, timestamp);
                }
                else {
                    resetMatchingView();
                }
            }
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
        String uri = "@drawable/profile";

        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        imgView.setImageDrawable(res);

        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem( currTab );
    }
}
