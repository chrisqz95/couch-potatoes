package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class PreferencesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DBHelper dbHelper;

    private ArrayList<String> interestList;

    private Button settingsTab;
    private Button photosTab;
    private EditText userBio;
    private ListView interestListView;
    private ArrayAdapter<String> interestAdapter;
    private TextView userTitle;
    private ImageView imgView;
    private ProgressBar spinner;
    private TextView bioTitle;
    private TextView interestsTitle;
    private LinearLayout prefHorizBtns;
    private Button bioSubmitBtn;
    private Button bioSubmitCancelBtn;
    private String bioTextPrev;
    private LinearLayout profileLayout;
    private LinearLayout bioBtnLayout;

    private String currUserID;

    // For the side navigation bar
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private android.widget.TextView sidebarUserName;
    private android.widget.TextView sidebarUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileLayout = findViewById(R.id.profileLayout);
        userTitle = findViewById(R.id.user_title);
        prefHorizBtns = findViewById(R.id.preferencesHorizBtns);
        bioTitle = findViewById(R.id.biography_title);
        userBio = findViewById(R.id.user_bio);
        bioBtnLayout = findViewById(R.id.bioBtnLayout);
        bioSubmitBtn = findViewById(R.id.profileBioSubmitBtn);
        bioSubmitCancelBtn = findViewById(R.id.profileBioSubmitCancelBtn);
        interestsTitle = findViewById(R.id.interests_title);
        imgView = findViewById(R.id.preferencesProfilePic);
        spinner = findViewById(R.id.preferencesSpinner);

        userTitle.setVisibility(View.GONE);
        prefHorizBtns.setVisibility(View.GONE);
        bioTitle.setVisibility(View.GONE);
        userBio.setVisibility(View.GONE);
        bioBtnLayout.setVisibility(View.GONE);
        interestsTitle.setVisibility(View.GONE);
        imgView.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);

        dbHelper = DBHelper.getInstance();

        currUserID = dbHelper.getAuth().getUid();

        interestList = new ArrayList<>();
        interestAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, interestList );

        interestListView = findViewById(R.id.interestListView);
        interestListView.setAdapter( interestAdapter );

        // places toolbar on top of the screen
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        // set up the side navigation bar on the left side of screen
        mDrawer = (DrawerLayout) findViewById(R.id.profile_drawer_layout);
        navView = (NavigationView) findViewById(R.id.profile_nav_view);
        setSideBarDrawer( mDrawer, navView, toolbar , dbHelper);

        settingsTab = findViewById(R.id.settingsTab);
        settingsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        photosTab = findViewById(R.id.photosTab);
        photosTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PictureGridActivity.class);
                DBHelper dbHelper = DBHelper.getInstance();
                dbHelper.fetchCurrentUser();
                intent.putExtra("uid", dbHelper.getUser().getUid());
                intent.putExtra("isCurrentUser", true);
                intent.putExtra("changeProfilePic", false);
                startActivity(intent);
            }
        });

        imgView.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                return false;
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PictureGridActivity.class);
                DBHelper dbHelper = DBHelper.getInstance();
                dbHelper.fetchCurrentUser();
                intent.putExtra("uid", dbHelper.getUser().getUid());
                intent.putExtra("isCurrentUser", true);
                intent.putExtra("changeProfilePic", true);
                startActivity(intent);
            }
        });

        displayProfilePic();
        displayUserInfo();
        displayInterests();

        interestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PreferenceChart.class);
                intent.putExtra( "interest", interestList.get( position ) );
                startActivity(intent);
            }
        });

        userBio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( hasFocus ) {
                    bioBtnLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        bioSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide bio buttons
                if ( bioBtnLayout.getVisibility() == View.VISIBLE ) {
                    bioBtnLayout.setVisibility(View.GONE);
                }

                // Remove bio edit text focus
                // Workaround: Remove focus by requesting focus elsewhere
                profileLayout.requestFocus();

                String bioChanges = userBio.getText().toString();
                
                // Save new bio state
                bioTextPrev = bioChanges;

                // Submit bio to Firebase
                dbHelper.getDb().getReference( dbHelper.getUserPath() ).child( currUserID ).child( "bio" ).setValue( bioChanges );
            }
        });

        bioSubmitCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide bio buttons
                if ( bioBtnLayout.getVisibility() == View.VISIBLE ) {
                    bioBtnLayout.setVisibility(View.GONE);
                }

                // Remove bio edit text focus
                // Workaround: Remove focus by requesting focus elsewhere
                profileLayout.requestFocus();

                // Restore bio to previous state
                userBio.setText( bioTextPrev );
            }
        });
    }

    // Make sure the navView highlight the correct location
    @Override
    public void onResume() {
        super.onResume();
        // highlight the current location
        navView.setCheckedItem(R.id.nav_profile);
    }

    private void displayInterests() {
        dbHelper.getDb().getReference( dbHelper.getInterestPath() ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( final DataSnapshot interest : dataSnapshot.getChildren() ) {
                    String currInterest = (String) interest.getValue();
                    interestList.add( currInterest );
                }

                interestAdapter.notifyDataSetChanged();

                // Hide spinner and display page elements
                userTitle.setVisibility(View.VISIBLE);
                prefHorizBtns.setVisibility(View.VISIBLE);
                bioTitle.setVisibility(View.VISIBLE);
                userBio.setVisibility(View.VISIBLE);
                interestsTitle.setVisibility(View.VISIBLE);
                imgView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void displayUserInfo() {
        dbHelper.getDb().getReference( dbHelper.getUserPath() ).child( currUserID ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = "";
                String middleName = "";
                String lastName = "";
                String bio = "";

                for ( DataSnapshot field : dataSnapshot.getChildren() ) {
                    switch ( field.getKey() ) {
                        case "firstName":
                            firstName = (String) field.getValue();
                            break;
                        case "middleName":
                            middleName = (String) field.getValue();
                            break;
                        case "lastName":
                            lastName = (String) field.getValue();
                            break;
                        case "bio":
                            bio = (String) field.getValue();
                            break;
                        default:
                            break;
                    }
                }
                String name = dbHelper.getFullName( firstName, middleName, lastName );
                userTitle.setText( name );
                userBio.setText( bio );
                bioTextPrev = bio;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void displayProfilePic() {
        dbHelper.getDb().getReference(dbHelper.getUserPath()).child( currUserID ).child("profile_pic").addValueEventListener(new ValueEventListener() {
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
                    // TODO Add method to DBHelper to get this
                    //url = "gs://couch-potatoes-47758.appspot.com/Default/ProfilePic/potato_1_profile_pic.png";
                    String uri = "@drawable/profile";

                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res = getResources().getDrawable(imageResource);
                    imgView.setImageDrawable(res);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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
        sidebarUserEmail = (android.widget.TextView) navView.getHeaderView(0)
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.chatroom_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Handles action in the sidebar menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // user is already on the page; do nothing
        } else if (id == R.id.nav_matches) {
            // redirect user to the "Find Matches" screen
            finish();

        } else if (id == R.id.nav_chats) {
            // redirects user to ChatRoomActivity.xml
            Intent intent = new Intent( getApplicationContext(), ChatRoomActivity.class );
            startActivity( intent );
            finish();
        }
        else if (id == R.id.nav_settings) {
            startActivity( new Intent( getApplicationContext(), AppSettingsActivity.class ) );
        }
        else if (id == R.id.nav_info) {
            Intent intent = new Intent( getApplicationContext(), AboutUsActivity.class );
            startActivity( intent );
        } else if (id == R.id.nav_logout) {
            // logs out and redirects user to LoginActivity.xml
            dbHelper.getAuth().signOut();
            startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.profile_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}