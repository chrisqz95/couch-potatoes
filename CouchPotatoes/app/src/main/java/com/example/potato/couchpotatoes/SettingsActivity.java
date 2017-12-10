package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private DBHelper helper;
    private static String[] genders = new String[] {"male", "female", "non-binary"};
    private ListView sexualPreference;
    private Button submitBtn;
    private Button cancelBtn;
    private String currUserID;
    private LinearLayout settingsBtnLayout;
    private SparseBooleanArray prevSexualPrefChecked;
    private EditText cityEditText;
    private EditText stateEditText;
    private EditText countryEditText;
    private LinearLayout settingsLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        helper = new DBHelper();

        prevSexualPrefChecked = new SparseBooleanArray();

        for ( int i = 0; i < genders.length; i++ ) {
            prevSexualPrefChecked.put( i, false );
        }

        settingsLayout = findViewById(R.id.settingsLayout);
        settingsBtnLayout = findViewById(R.id.settingsBtnLayout);
        submitBtn = findViewById(R.id.settingsSubmitBtn);
        cancelBtn = findViewById(R.id.settingsCancelBtn);

        cityEditText = findViewById(R.id.cityEditText);
        stateEditText = findViewById(R.id.stateEditText);
        countryEditText = findViewById(R.id.countryEditText);

        settingsBtnLayout.setVisibility(View.GONE);

        sexualPreference = findViewById(R.id.sexual_preference);
        sexualPreference.setAdapter( new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, genders) );
        sexualPreference.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        currUserID = helper.getAuth().getUid();

        //displayGender();
        displaySexualPreference();
        displayCityStateCountry();

        cityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( hasFocus ) {
                    if ( settingsBtnLayout.getVisibility() == View.GONE ) {
                        settingsBtnLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        stateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( hasFocus ) {
                    if ( settingsBtnLayout.getVisibility() == View.GONE ) {
                        settingsBtnLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        countryEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( hasFocus ) {
                    if ( settingsBtnLayout.getVisibility() == View.GONE ) {
                        settingsBtnLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        sexualPreference.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                //SparseBooleanArray genderChecked = genderList.getCheckedItemPositions();
                SparseBooleanArray sexualPrefChecked = sexualPreference.getCheckedItemPositions();

                String city = cityEditText.getText().toString();
                String state = stateEditText.getText().toString();
                String country = countryEditText.getText().toString();

                helper.getDb().getReference( helper.getUserPath() ).child( currUserID ).child( "city" ).setValue( city );
                helper.getDb().getReference( helper.getUserPath() ).child( currUserID ).child( "state" ).setValue( state );
                helper.getDb().getReference( helper.getUserPath() ).child( currUserID ).child( "country" ).setValue( country );

                Map<String, Object> sexualPreferenceMap = new HashMap<>();

                // Get all checked items
                for (int i = 0; i < sexualPreference.getAdapter().getCount(); i++) {
                    if (sexualPrefChecked.get(i)) {
                        sexualPreferenceMap.put( genders[i], true );
                    }
                }

                // Record changes
                prevSexualPrefChecked = sexualPrefChecked;

                // Submit partner preference changes to Firebase
                helper.getDb().getReference( helper.getPartnerPreferencePath() ).child( currUserID ).child( "gender" ).setValue( sexualPreferenceMap );

                settingsBtnLayout.setVisibility(View.GONE);

                // Remove bio edit text focus
                // Workaround: Remove focus by requesting focus elsewhere
                settingsLayout.requestFocus();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsBtnLayout.setVisibility(View.GONE);

                for (int i = 0; i < sexualPreference.getAdapter().getCount(); i++) {
                    sexualPreference.setItemChecked( i, prevSexualPrefChecked.get( i ) );
                }

                // Remove bio edit text focus
                // Workaround: Remove focus by requesting focus elsewhere
                settingsLayout.requestFocus();
            }
        });
    }

    // Fetch preferences from Firebase
    private void displaySexualPreference() {
        helper.getDb().getReference( helper.getPartnerPreferencePath() ).child( currUserID ).child( "gender" ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot currGender : dataSnapshot.getChildren() ) {
                    String genderPref = currGender.getKey();
                    sexualPreference.setItemChecked( Arrays.asList( genders ).indexOf( genderPref ), true );
                    prevSexualPrefChecked.put( Arrays.asList( genders).indexOf( genderPref ), true );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }

    private void displayCityStateCountry() {
        helper.getDb().getReference( helper.getUserPath() ).child( currUserID ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot field : dataSnapshot.getChildren() ) {
                    switch ( field.getKey() ) {
                        case "city":
                            cityEditText.setText( ( field.getValue() != null ) ? field.getValue().toString() : "" );
                            break;
                        case "state":
                            stateEditText.setText( ( field.getValue() != null ) ? field.getValue().toString() : "" );
                            break;
                        case "country":
                            countryEditText.setText( ( field.getValue() != null ) ? field.getValue().toString() : "" );
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
