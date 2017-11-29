package com.example.potato.couchpotatoes;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
// for the side bar activity
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MatchingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
	private DBHelper helper;
    private final String[] tabTitles = new String[] { "Date", "Friend" };

    // For the side navigation bar
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private android.widget.TextView sidebarUserName;
    private android.widget.TextView sidebarUserEmail;

    // list of matches for dating and friending
//    private List<MatchedUser> matchedDateList = new ArrayList<>();
//    private List<MatchedUser> matchedFriendList = new ArrayList<>();
    private ArrayList<String> matchedDateList = new ArrayList<>();
    private ArrayList<String> matchedFriendList = new ArrayList<>();

    private MatchFragmentPagerAdapter adapter;
    private MatchViewPager viewPager;
    private MatchPageFragment fFragment;
    private MatchPageFragment dFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        helper = new DBHelper();

        // TODO CHANGE THIS THIS IS TEMPORARY
        //matchedDateList.add("Della");
        //matchedFriendList.add("Nestor");

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.matching_tabs);
        viewPager = (MatchViewPager) findViewById(R.id.matching_viewpager);

        adapter = new MatchFragmentPagerAdapter(getSupportFragmentManager());
        // add fragments to the view pager
        fFragment = MatchPageFragment.newInstance(matchedFriendList);
        dFragment = MatchPageFragment.newInstance(matchedDateList);
        //adapter.addFragment(MatchPageFragment.newInstance(matchedDateList), tabTitles[0]);
        //adapter.addFragment(MatchPageFragment.newInstance(matchedFriendList), tabTitles[1]);
        //adapter.addFragment(MatchPageFragment.newInstance(matchedFriendList), tabTitles[1]);
        adapter.addFragment(dFragment, tabTitles[0]);
        adapter.addFragment(fFragment, tabTitles[1]);

        // line of code below causes app to crash; commenting out for app functionality -Mervin
        viewPager.setAdapter(adapter);

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
        setSupportActionBar(toolbar);

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

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent( getApplicationContext(), SettingsActivity.class );
            startActivity( intent );
        } else if (id == R.id.nav_info) {
            // TODO: go to Page with device information

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

    public void fetchPotentFriendsFromFirebase () {
        helper.getDb().getReference( helper.getPotentFriendPath() + helper.getAuth().getUid() ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> potentDates = dataSnapshot.getChildren().iterator();

                matchedFriendList.clear();

                while ( potentDates.hasNext() ) {
                    matchedFriendList.add( (String) potentDates.next().getValue() );
                }

                Log.d( "TEST", matchedFriendList.toString() );

                adapter.notifyDataSetChanged();
                viewPager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }

    public void fetchPotentDatesFromFirebase () {
        helper.getDb().getReference( helper.getPotentDatePath() + helper.getAuth().getUid() ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> potentDates = dataSnapshot.getChildren().iterator();

                matchedDateList.clear();

                while ( potentDates.hasNext() ) {
                    matchedDateList.add( (String) potentDates.next().getValue() );
                }

                Log.d( "TEST", matchedDateList.toString() );

                adapter.notifyDataSetChanged();
                viewPager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }
}
