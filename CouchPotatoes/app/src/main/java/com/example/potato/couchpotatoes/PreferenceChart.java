package com.example.potato.couchpotatoes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PreferenceChart extends AppCompatActivity {
    private DBHelper helper;
    private static String[] moviePrefList = new String[] {"Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", "Documentary", "Drama", "Family", "Fantasy", "Film-Noir", "History", "Horror", "Music", "Musical", "Mystery", "Romance", "Sci-Fi", "Sport", "Thriller", "War", "Western"};
    private static String[] sportsPrefList = new String[] {"The", "Thing", "Go", "Skrraaaa"};
    private int chartType;

    private ListView moviePreferences;

    private ListView subcategoryLayout;

    private String interest;

    private ArrayList<String> subcategoryList;
    private ArrayAdapter<String> subcategoryAdapter;
    private Button submitBtn;
    private String currUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_chart);

        subcategoryLayout = (ListView) findViewById(R.id.chartArray);
        submitBtn = (Button) findViewById(R.id.subcategorySubmitButton);

        helper = new DBHelper();

        currUserID = helper.getAuth().getUid();

        subcategoryList = new ArrayList<>();

        subcategoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, subcategoryList );
        subcategoryLayout.setAdapter( subcategoryAdapter );
        subcategoryLayout.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        interest = getIntent().getExtras().getString( "interest" );

        displaySubcategories();
        displayLikedSubcategories();

        // Add click handler to submit changes to Firebase
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Source: https://stackoverflow.com/questions/4831918/how-to-get-all-checked-items-from-a-listview
                SparseBooleanArray genderChecked = subcategoryLayout.getCheckedItemPositions();

                //Map<String,Object> genderListMap = new HashMap<>();

                // Get all checked items
                for (int i = 0; i < subcategoryLayout.getAdapter().getCount(); i++) {
                    if (genderChecked.get(i)) {
                        //Log.d( "TEST", subcategoryList.get( i ) + " LIKE" );
                        // Submit gender changes to Firebase
                        helper.addToUserInterest( currUserID, interest, subcategoryList.get( i ), "like" );
                    }
                    else {
                        helper.removeFromUserInterest( currUserID, interest, subcategoryList.get( i ) );
                    }
                }
            }
        });


        /*
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
        */
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void displaySubcategories() {
        helper.getDb().getReference( helper.getInterestSubcategoryPath() ).child( interest ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               for ( DataSnapshot subcategory : dataSnapshot.getChildren() ) {
                   //String currSubcategory = (String) subcategory.getValue() + "✅";
                   String currSubcategory = (String) subcategory.getValue();
                   subcategoryList.add( currSubcategory );
                   //Log.d( "TEST", currSubcategory );
                   /*
                   TextView newTextView = new TextView(getApplicationContext());
                   newTextView.setText( currSubcategory );
                   newTextView.setTextColor( Color.BLACK );
                   newTextView.setTextSize( 18 );
                   newTextView.setId( subcategoryList.indexOf( currSubcategory ) );
                   LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                   //layoutParams.setMargins( 200, 100, 200, 0 );
                   newTextView.setLayoutParams( layoutParams );
                   newTextView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Log.d( "TEST", subcategoryList.get( v.getId() ) + " CLICKED" );
                       }
                   });
                   subcategoryLayout.addView( newTextView );
                   subcategoryLayout.a
                   */
               }

               subcategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }

    private void displayLikedSubcategories() {
        helper.getDb().getReference( helper.getUserInterestPath() ).child( currUserID ).child( interest ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot subcat : dataSnapshot.getChildren() ) {
                    //Log.d( "TEST", subcat.toString() );
                    String subcategory = subcat.getKey();
                    String preference = (String) subcat.getValue();

                    subcategoryLayout.setItemChecked( subcategoryList.indexOf( subcategory ), true );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }

}
