package com.example.potato.couchpotatoes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
public class PreferencesActivity extends AppCompatActivity {
    private static String[] moviePrefList = new String[] {"Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", "Documentary", "Drama", "Family", "Fantasy", "Film-Noir", "History", "Horror", "Music", "Musical", "Mystery", "Romance", "Sci-Fi", "Sport", "Thriller", "War", "Western"};
    private static String[] sportsPrefList = new String[] {"The", "Thing", "Go", "Skrraaaa"};

    private Button settingsTab;
    private Button movieTab;
    private Button sportsTab;
    private EditText userBio;
    private TextView moviesSelection;
    private TextView sportsSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences prefs = this.getSharedPreferences("com.example.potato.couchpotatoes", Context.MODE_PRIVATE);

        settingsTab = (Button) findViewById(R.id.settingsTab);
        settingsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

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
    }

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
}