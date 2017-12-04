package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DBHelper helper = new DBHelper();
    private ArrayList<String> listItems = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;
    private ListView listView;
    private TextView userName;
    private Map<String,String> chats = new HashMap<>();
    private String userID = helper.getAuth().getUid();
    private String displayName = helper.getAuthUserDisplayName();
    private String userNames = ""; // The list of users in a chat exl. displayName

    // For the side navigation bar
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private android.widget.TextView sidebarUserName;
    private android.widget.TextView sidebarUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        // places toolbar into the screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        // Display the user's display name
       // userName = (TextView) findViewById(R.id.userName);
        //userName.setText( displayName );


        // enables toggle button on toolbar to open the sidebar
        mDrawer = (DrawerLayout) findViewById(R.id.chatroom_drawer_layout);

        // adds the toggle button
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // set up side navigation bar layout
        navView = (NavigationView) findViewById(R.id.chatroom_nav_view);
        navView.setNavigationItemSelectedListener(this);

        // Use a ListView to display the list of chats
        listView = (ListView) findViewById(R.id.chatList);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter( listAdapter );

        // Fetch and display all chats the current user belongs to.
        // The names of all chat members that belong to a chat are displayed and identify each chat.
        helper.getDb().getReference( helper.getUserChatPath() + userID ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> elems = dataSnapshot.getChildren().iterator();

                // Make sure not to display already existing chatIDs more than once
                listItems.clear();

                // Get the next chat
                while ( elems.hasNext() ) {
                    String chatID = elems.next().getKey();

                    // Fetch the names of all users that belong to the selected chat
                    helper.getDb().getReference( helper.getChatUserPath() + chatID ).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> users = dataSnapshot.getChildren().iterator();

                            userNames = "";

                            // Concatenate the names of all users that belong to the current chat, delimited by a comma.
                            while ( users.hasNext() ) {
                                String currUser = (String) users.next().getValue();
                                if (currUser.equals(displayName))
                                    continue;
                                if ( !userNames.equals( "" ) ) {
                                    userNames += ", ";
                                }
                                userNames += currUser;

                            }

                            // Keep track of the chatID corresponding to the list of user names
                            chats.put( userNames, dataSnapshot.getKey() );

                            // Add the list of user names identifying the current chat to the ListView
                            listItems.add( userNames );

                            // Notify ListAdapter of changes
                            listAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d( "TEST", databaseError.toString() );
                        }
                    });
                }

                //listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.toString() );
            }
        });

        // Add an event handler to begin the MessageActivity corresponding to the clicked chatID
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                String chatID = chats.get( String.valueOf( parent.getItemAtPosition( position ) ) );

                // Create new Intent, keeping track of the selected chatID
                Intent intent = new Intent( getApplicationContext(), MessageActivity.class );
                intent.putExtra( "chatID", chatID );
                intent.putExtra("otherUsers", String.valueOf(parent.getItemAtPosition(position)));

                // Begin the messaging activity corresponding to the selected chat
                startActivity( intent );
                //finish();
            }
        });
    }

    // Handles pressing back button in bottom navigation bar when sidebar is on the screen
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.chatroom_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Handles action in the sidebar menu
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent( getApplicationContext(), PreferencesActivity.class );
            startActivity( intent );

        } else if (id == R.id.nav_matches) {
            Intent intent = new Intent( getApplicationContext(), MainActivity.class );
            startActivity( intent );
            finish();

        } else if (id == R.id.nav_chats) {
            // redirects user to ChatRoomActivity.xml
            /*Intent intent = new Intent( getApplicationContext(), ChatRoomActivity.class );
            startActivity( intent );
            finish();*/


        } else if (id == R.id.nav_settings) {
            // TODO: go to SettingsActivity

        }
        else if (id == R.id.nav_info) {
            Intent intent = new Intent( getApplicationContext(), AboutUsActivity.class );
            startActivity( intent );

        } else if (id == R.id.nav_logout) {
            // logs out and redirects user to LoginActivity.xml
            helper.getAuth().signOut();
            startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.chatroom_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}