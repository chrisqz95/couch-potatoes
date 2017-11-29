package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by cristianrecinos on 11/26/17.
 */

public class SettingsActivity extends AppCompatActivity {
    private static String[] genders = new String[] {"Male", "Female", "Non-binary"};
    private ListView genderList;
    private ListView sexualPreference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        genderList = (ListView) findViewById(R.id.gender_list);
        genderList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, genders));
        genderList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        sexualPreference = (ListView) findViewById(R.id.sexual_preference);
        sexualPreference.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, genders));
        sexualPreference.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
}
