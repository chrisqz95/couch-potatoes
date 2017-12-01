package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cristianrecinos on 11/26/17.
 */

public class SettingsActivity extends AppCompatActivity {
    private DBHelper helper;
    private static String[] genders = new String[] {"male", "female", "non-binary"};
    private ListView genderList;
    private ListView sexualPreference;
    private Button submitBtn;
    private Button cancelBtn;
    private String currUserID;
    private LinearLayout settingsBtnLayout;

    private String prevGender;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        helper = new DBHelper();

        settingsBtnLayout = (LinearLayout) findViewById(R.id.settingsBtnLayout);
        submitBtn = (Button) findViewById(R.id.settingsSubmitBtn);
        cancelBtn = (Button) findViewById(R.id.settingsCancelBtn);

        settingsBtnLayout.setVisibility(View.GONE);

        genderList = (ListView) findViewById(R.id.gender_list);
        genderList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, genders));
        genderList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        sexualPreference = (ListView) findViewById(R.id.sexual_preference);
        sexualPreference.setAdapter( new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, genders) );
        sexualPreference.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        currUserID = helper.getAuth().getUid();

        displayGender();
        displaySexualPreference();

        genderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( settingsBtnLayout.getVisibility() == View.GONE ) {
                    settingsBtnLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // Add click handler to submit changes to Firebase
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Source: https://stackoverflow.com/questions/4831918/how-to-get-all-checked-items-from-a-listview
                SparseBooleanArray genderChecked = genderList.getCheckedItemPositions();
                SparseBooleanArray sexualPrefChecked = sexualPreference.getCheckedItemPositions();

                //Map<String,Object> genderListMap = new HashMap<>();

                // Get all checked items
                for (int i = 0; i < genderList.getAdapter().getCount(); i++) {
                    if (genderChecked.get(i)) {
                        //Log.d( "TEST", genders[ i ] + " gender CHECKED" );
                        // Submit gender changes to Firebase
                        // TODO add DBHelper method to update gender only and replace below
                        helper.getDb().getReference( helper.getUserPath() ).child( currUserID ).child( "gender" ).setValue( genders[i] );
                        prevGender = genders[i];
                    }
                }

                Map<String, Object> sexualPreferenceMap = new HashMap<>();

                // Get all checked items
                for (int i = 0; i < sexualPreference.getAdapter().getCount(); i++) {
                    if (sexualPrefChecked.get(i)) {
                        //Log.d( "TEST", genders[ i ] + " preference CHECKED" );
                        sexualPreferenceMap.put( genders[i], true );
                    }
                }

                // Submit partner preference changes to Firebase
                // TODO add DBHelper method to update gender only and replace below
                helper.getDb().getReference( helper.getPartnerPreferencePath() ).child( currUserID ).child( "gender" ).setValue( sexualPreferenceMap );

                settingsBtnLayout.setVisibility(View.GONE);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsBtnLayout.setVisibility(View.GONE);

                SparseBooleanArray genderChecked = genderList.getCheckedItemPositions();

                for (int i = 0; i < genderList.getAdapter().getCount(); i++) {
                    if (genderChecked.get(i)) {
                        genderList.setItemChecked( Arrays.asList( genders ).indexOf( prevGender ), true );
                    }
                    else {
                        genderList.setItemChecked( Arrays.asList( genders ).indexOf( prevGender ), false );
                    }
                }
            }
        });
    }

    // Fetch preferences from Firebase
    private void displaySexualPreference() {
        helper.getDb().getReference( helper.getPartnerPreferencePath() ).child( currUserID ).child( "gender" ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d( "TEST", dataSnapshot.toString() );
                for ( DataSnapshot currGender : dataSnapshot.getChildren() ) {
                    //Log.d( "TEST", currGender.toString() );
                    String genderPref = currGender.getKey();
                    //Log.d( "TEST", genderPref );
                    sexualPreference.setItemChecked( Arrays.asList( genders ).indexOf( genderPref ), true );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }

    // Fetch gender from Firebase
    private void displayGender() {
        helper.getDb().getReference( helper.getUserPath() ).child( currUserID ).child( "gender" ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gender = (String) dataSnapshot.getValue();

                genderList.setItemChecked( Arrays.asList( genders ).indexOf( gender ), true );
                prevGender = gender;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }
}
