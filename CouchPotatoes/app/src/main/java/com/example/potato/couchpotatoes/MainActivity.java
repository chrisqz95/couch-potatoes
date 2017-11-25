package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// for the side bar activity
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DBHelper helper;
    private android.widget.TextView userName;
    private android.widget.Button logout;
    private android.widget.Button chat;

    // For the side navigation bar
    private DrawerLayout mDrawer;
    private NavigationView navView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DBHelper();

        // places toolbar on top of the screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enables toggle button for the sidebar on the toolbar
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        userName = (android.widget.TextView) findViewById(R.id.userName);
        logout = (android.widget.Button) findViewById(R.id.logout);
        chat = (android.widget.Button) findViewById(R.id.viewChats);
        
		// set up side navigation bar
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        // Display user's name if logged in
        if ( helper.isUserLoggedIn() ) {
            String displayName = helper.getAuthUserDisplayName();

            userName.setText( displayName );
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
    }
// Handles pressing back button when sidebar is on the screen
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
            // TODO: go to profile activity
            // Make substitute screen appear

        } else if (id == R.id.nav_chats) {
            // TODO: go to ChatActivity
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
