package com.example.potato.couchpotatoes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PreferenceChart extends AppCompatActivity{
    private static String[] moviePrefList = new String[] {"Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", "Documentary", "Drama", "Family", "Fantasy", "Film-Noir", "History", "Horror", "Music", "Musical", "Mystery", "Romance", "Sci-Fi", "Sport", "Thriller", "War", "Western"};
    private static String[] sportsPrefList = new String[] {"The", "Thing", "Go", "Skrraaaa"};
    private int chartType;

    private ListView moviePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_chart);
        Bundle b = getIntent().getExtras();
        chartType = -1;
        if (b != null) {
            chartType = b.getInt("type");
        }
        final SharedPreferences prefs = this.getSharedPreferences("com.example.potato.couchpotatoes", Context.MODE_PRIVATE);

        moviePreferences = (ListView) findViewById(R.id.chartArray);


        String[] chartValues = moviePrefList;

        switch (chartType) {
            case 0: chartValues = moviePrefList; break;
            case 1: chartValues = sportsPrefList; break;
        }

        moviePreferences.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, chartValues));

        moviePreferences.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        moviePreferences.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                SparseBooleanArray checkedItems = moviePreferences.getCheckedItemPositions();

                StringBuilder prefsList = new StringBuilder();
                for (int i = 0; i < 100; i++) {
                    if (checkedItems.get(i)) {
                        prefsList.append(i + ",");
                    }
                }
                Log.v("PreferenceChart", "lordyyy please");

                StringBuilder listKey = new StringBuilder();
                listKey.append("testList" + chartType);
                prefs.edit().putString(listKey.toString(), prefsList.toString()).apply();
            }
        });

        StringBuilder listKey = new StringBuilder();
        listKey.append("testList" + chartType);
        String prefsList = prefs.getString(listKey.toString(), "");

        if (prefsList != "") {
            String[] selectedItems = prefsList.split(",");
            for (int i = 0; i < selectedItems.length; i++) {
                moviePreferences.setItemChecked(Integer.parseInt(selectedItems[i]), true);
            }
        }

        SparseBooleanArray checkedItems = moviePreferences.getCheckedItemPositions();

        StringBuilder prefsListTEST = new StringBuilder();
        for (int i = 0; i < checkedItems.size(); i++) {
            if (checkedItems.get(i)) {
                prefsListTEST.append(i + ",");
            }
        }
        Log.v("PreferenceChart", prefsListTEST.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
