package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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
    private String currUserID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        helper = new DBHelper();

        submitBtn = (Button) findViewById(R.id.settingsSubmitBtn);

        genderList = (ListView) findViewById(R.id.gender_list);
        genderList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, genders));
        genderList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        sexualPreference = (ListView) findViewById(R.id.sexual_preference);
        ArrayAdapter sexualPreferenceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, genders);
        sexualPreference.setAdapter( sexualPreferenceAdapter );
        sexualPreference.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        sexualPreference.getChoiceMode();

        currUserID = helper.getAuth().getUid();

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
            }
        });
    }

    private void displaySexualPreference() {

    }
}
