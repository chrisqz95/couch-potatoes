package com.example.potato.couchpotatoes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Picture;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.JsonWriter;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private DBHelper helper;
    private static String[] moviePrefList = new String[] {"Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", "Documentary", "Drama", "Family", "Fantasy", "Film-Noir", "History", "Horror", "Music", "Musical", "Mystery", "Romance", "Sci-Fi", "Sport", "Thriller", "War", "Western"};
    private static String[] sportsPrefList = new String[] {"The", "Thing", "Go", "Skrraaaa"};

    private ArrayList<String> interestList;

    private Button settingsTab;
    private Button photosTab;
    private Button movieTab;
    private Button sportsTab;
    private EditText userBio;
    private TextView moviesSelection;
    private TextView sportsSelection;
    private LinearLayout interestsLayout;
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
        SharedPreferences prefs = this.getSharedPreferences("com.example.potato.couchpotatoes", Context.MODE_PRIVATE);

        profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
        userTitle = (TextView) findViewById(R.id.user_title);
        prefHorizBtns = (LinearLayout) findViewById(R.id.preferencesHorizBtns);
        bioTitle = (TextView) findViewById(R.id.biography_title);
        userBio = (EditText) findViewById(R.id.user_bio);
        bioBtnLayout = (LinearLayout) findViewById(R.id.bioBtnLayout);
        bioSubmitBtn = (Button) findViewById(R.id.profileBioSubmitBtn);
        bioSubmitCancelBtn = (Button) findViewById(R.id.profileBioSubmitCancelBtn);
        interestsTitle = (TextView) findViewById(R.id.interests_title);
        imgView = (ImageView) findViewById(R.id.preferencesProfilePic);
        spinner = (ProgressBar)findViewById(R.id.preferencesSpinner);

        userTitle.setVisibility(View.GONE);
        prefHorizBtns.setVisibility(View.GONE);
        bioTitle.setVisibility(View.GONE);
        userBio.setVisibility(View.GONE);
        //bioSubmitBtn.setVisibility(View.GONE);
        bioBtnLayout.setVisibility(View.GONE);
        interestsTitle.setVisibility(View.GONE);
        imgView.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);

        helper = new DBHelper();

        currUserID = helper.getAuth().getUid();

        interestList = new ArrayList<>();
        interestAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, interestList );

        interestListView = (ListView) findViewById(R.id.interestListView);
        interestListView.setAdapter( interestAdapter );

        //interestsLayout = (LinearLayout) findViewById(R.id.interestsLayout);

        // places toolbar on top of the screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        // enables toggle button on toolbar to open the sidebar
        mDrawer = (DrawerLayout) findViewById(R.id.profile_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // set up side navigation bar layout
        navView = (NavigationView) findViewById(R.id.profile_nav_view);
        navView.setNavigationItemSelectedListener(this);

        // Want to display icons in original color scheme
        navView.setItemIconTintList(null);

        // initialize textViews on the sidebar header
        sidebarUserName = (android.widget.TextView) navView.getHeaderView(0)
                .findViewById(R.id.sidebar_username);
        sidebarUserEmail = (android.widget.TextView) navView.getHeaderView(0)
                .findViewById(R.id.sidebar_user_email);

        // displays user's name and email on the sidebar header
        sidebarUserName.setText( helper.getAuthUserDisplayName() );
        sidebarUserEmail.setText( helper.getUser().getEmail() );

        settingsTab = (Button) findViewById(R.id.settingsTab);
        settingsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        photosTab = (Button) findViewById(R.id.photosTab);
        photosTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PictureGridActivity.class);
                DBHelper dbHelper = new DBHelper();
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
                // TODO
                //Toast.makeText(getApplicationContext(), "Click to change profile picture", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PictureGridActivity.class);
                DBHelper dbHelper = new DBHelper();
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
                //Log.d( "TEST", interestList.get( position ) + " CLICKED" );
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
                //Log.d( "TEST", "SUBMIT CHANGES" );
                // Hide bio buttons
                // TODO MOVE TO new helper method
                if ( bioBtnLayout.getVisibility() == View.VISIBLE ) {
                    bioBtnLayout.setVisibility(View.GONE);
                }

                // Remove bio edit text focus
                // Workaround: Remove focus by requesting focus elsewhere
                // TODO May want to find a better way of doing this later
                profileLayout.requestFocus();

                String bioChanges = userBio.getText().toString();
                
                // Save new bio state
                bioTextPrev = bioChanges;

                // Submit bio to Firebase
                // TODO Add method to DBHelper to only change bio
                helper.getDb().getReference( helper.getUserPath() ).child( currUserID ).child( "bio" ).setValue( bioChanges );
                //Log.d( "TEST", bioChanges );
            }
        });

        bioSubmitCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide bio buttons
                // TODO MOVE TO new helper method
                if ( bioBtnLayout.getVisibility() == View.VISIBLE ) {
                    bioBtnLayout.setVisibility(View.GONE);
                }

                // Remove bio edit text focus
                // Workaround: Remove focus by requesting focus elsewhere
                // TODO May want to find a better way of doing this later
                profileLayout.requestFocus();

                // Restore bio to previous state
                userBio.setText( bioTextPrev );
            }
        });

        /*
        TextView htext =new TextView(this);
        htext.setText("Test");
        //htext.setId(5);
        htext.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        interestsLayout.addView(htext);
        */


        /*
        movieTab = (Button) findViewById(R.id.moviePrefTab);
        movieTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PreferenceChart.class);
                Bundle b = new Bundle();
                b.putInt("type", 0);
                intent.putExtras(b);
                startActivity(intent);
            }
        });


        sportsTab = (Button) findViewById(R.id.sportsPrefTab);
        sportsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PreferenceChart.class);
                Bundle b = new Bundle();
                b.putInt("type", 1);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        userBio = (EditText) findViewById(R.id.user_bio);
        userBio.setText(prefs.getString("user_bio", ""), TextView.BufferType.EDITABLE);
        cachePhotoURIs();
        */
     
    }

    /*

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = this.getSharedPreferences("com.example.potato.couchpotatoes", Context.MODE_PRIVATE);
        moviesSelection = (TextView) findViewById(R.id.movies_selection);
        String prefsList = prefs.getString("testList0", "");
        if (prefsList != "") {
            String[] selectedItems = prefsList.split(",");
            ArrayList<String> prefSelection = new ArrayList<String>();

            for (int i = 0; i < selectedItems.length; i++) {
                prefSelection.add(moviePrefList[Integer.parseInt(selectedItems[i])]);
            }

            StringBuilder selection = new StringBuilder();
            for (int i = 0; i < prefSelection.size(); i++) {
                selection.append(prefSelection.get(i));
                if (i != prefSelection.size() - 1) {
                    selection.append(", ");
                }
            }

            moviesSelection.setText(selection.toString());
        }
        else {
            moviesSelection.setText("");
        }

        // update the list of selected items for the the sports interests section
        sportsSelection = (TextView) findViewById(R.id.sports_selection);
        prefsList = prefs.getString("testList1", "");
        if (prefsList != "") {
            String[] selectedItems = prefsList.split(",");
            ArrayList<String> prefSelection = new ArrayList<String>();

            for (int i = 0; i < selectedItems.length; i++) {
                prefSelection.add(sportsPrefList[Integer.parseInt(selectedItems[i])]);
            }

            StringBuilder selection = new StringBuilder();
            for (int i = 0; i < prefSelection.size(); i++) {
                selection.append(prefSelection.get(i));
                if (i != prefSelection.size() - 1) {
                    selection.append(", ");
                }
            }

            sportsSelection.setText(selection.toString());
        }
        else {
            sportsSelection.setText("");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = this.getSharedPreferences("com.example.potato.couchpotatoes", Context.MODE_PRIVATE);
        prefs.edit().putString("user_bio", userBio.getText().toString()).apply();
    }

    private void cachePhotoURIs(){
        final ArrayList<String> uriList = new ArrayList<String>();
        final DBHelper dbHelper = new DBHelper();
        dbHelper.fetchCurrentUser();

        //final DatabaseReference listRef = dbHelper.getDb().getReference().child("User_Photo").child(dbHelper.getUser().getUid());
        //final DatabaseReference imgRef = dbHelper.getDb().getReference().child("Photo");

        //final DatabaseReference listRef = dbHelper.getDb().getReference().child("Photo");
        final DatabaseReference listRef = dbHelper.getDb().getReference();

        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.child("User_Photo").child(dbHelper.getUser().getUid()).getChildren()) {
                    try {
                        uriList.add(snapshot.child("Photo").child(dataSnapshot.getKey()).child("uri").getValue().toString());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                JSONArray jsArray = new JSONArray(uriList);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("PhotoList",jsArray.toString()).apply();
    */
  

    private void displayInterests() {
        helper.getDb().getReference( helper.getInterestPath() ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                TextView htext =new TextView(this);
                htext.setText("Test");
                //htext.setId(5);
                htext.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                interestsLayout.addView(htext);
                */
                //Log.d( "TEST", dataSnapshot.toString() );
                for ( final DataSnapshot interest : dataSnapshot.getChildren() ) {
                    //Log.d( "TEST", interest.toString() );
                    String currInterest = (String) interest.getValue();
                    interestList.add( currInterest );
                    //Log.d( "TEST", currInterest );
                    /*
                    TextView newTextView = new TextView(getApplicationContext());
                    newTextView.setText( currInterest );
                    newTextView.setTextColor( Color.BLACK );
                    newTextView.setTextSize( 18 );
                    newTextView.setId( interestList.indexOf( currInterest ) );
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins( 200, 100, 200, 0 );
                    newTextView.setLayoutParams( layoutParams );
                    newTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Log.d( "TEST", interestList.get( v.getId() ) + " CLICKED" );
                            Intent intent = new Intent(getApplicationContext(), PreferenceChart.class);
                            intent.putExtra( "interest", interestList.get( v.getId() ) );
                            startActivity(intent);
                        }
                    });
                    interestsLayout.addView( newTextView );
                    */
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
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }

    private void displayUserInfo() {
        helper.getDb().getReference( helper.getUserPath() ).child( currUserID ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = "";
                String middleName = "";
                String lastName = "";
                String bio = "";

                for ( DataSnapshot field : dataSnapshot.getChildren() ) {
                    //Log.d( "TEST", field.toString() );
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
                String name = helper.getFullName( firstName, middleName, lastName );
                userTitle.setText( name );
                userBio.setText( bio );
                bioTextPrev = bio;
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
                //System.out.println("The read failed: " + databaseError.getMessage());
                Log.d("TEST", databaseError.getMessage());
            }
        });
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
            // TODO: go to the settings page
            //Intent intent = new Intent( getApplicationContext(), SettingsActivity.class );
            //startActivity( intent );
            startActivity( new Intent( getApplicationContext(), AppSettingsActivity.class ) );
        }
        else if (id == R.id.nav_info) {
            // TODO: go to the "about us" page
            Intent intent = new Intent( getApplicationContext(), AboutUsActivity.class );
            startActivity( intent );
        } else if (id == R.id.nav_logout) {
            // logs out and redirects user to LoginActivity.xml
            helper.getAuth().signOut();
            startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.profile_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}