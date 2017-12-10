package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
public class PreferenceChart extends AppCompatActivity {
    private DBHelper dbHelper;

    private ListView subcategoryLayout;

    private String interest;

    private ArrayList<String> subcategoryList;
    private ArrayAdapter<String> subcategoryAdapter;
    private Button submitBtn;
    private Button cancelBtn;
    private String currUserID;
    private LinearLayout subcategoryBtnLayout;

    private SparseBooleanArray prevGenderChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_chart);

        subcategoryLayout = findViewById(R.id.chartArray);
        subcategoryBtnLayout = findViewById(R.id.subcategoryBtnLayout);
        submitBtn = findViewById(R.id.subcategorySubmitBtn);
        cancelBtn = findViewById(R.id.subcategoryCancelBtn);

        subcategoryBtnLayout.setVisibility(View.GONE);

        prevGenderChecked = new SparseBooleanArray();

        dbHelper = DBHelper.getInstance();

        currUserID = dbHelper.getAuth().getUid();

        subcategoryList = new ArrayList<>();

        subcategoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, subcategoryList );
        subcategoryLayout.setAdapter( subcategoryAdapter );
        subcategoryLayout.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        interest = getIntent().getExtras().getString( "interest" );

        for ( int i = 0; i < subcategoryList.size(); i++ ) {
            prevGenderChecked.put( i, false );
        }

        displaySubcategories();
        displayLikedSubcategories();

        subcategoryLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( subcategoryBtnLayout.getVisibility() == View.GONE ) {
                    subcategoryBtnLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // Add click handler to submit changes to Firebase
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subcategoryBtnLayout.setVisibility(View.GONE);

                // Source: https://stackoverflow.com/questions/4831918/how-to-get-all-checked-items-from-a-listview
                SparseBooleanArray genderChecked = subcategoryLayout.getCheckedItemPositions();

                // Get all checked items
                for (int i = 0; i < subcategoryLayout.getAdapter().getCount(); i++) {
                    if (genderChecked.get(i)) {
                        // Submit gender changes to Firebase
                        dbHelper.addToUserInterest( currUserID, interest, subcategoryList.get( i ), "like" );
                    }
                    else {
                        dbHelper.removeFromUserInterest( currUserID, interest, subcategoryList.get( i ) );
                    }
                }

                // Record changes
                prevGenderChecked = genderChecked;
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subcategoryBtnLayout.setVisibility(View.GONE);

                for ( int i = 0; i < subcategoryAdapter.getCount(); i++ ) {
                    subcategoryLayout.setItemChecked( i, prevGenderChecked.get( i ) );
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void displaySubcategories() {
        dbHelper.getDb().getReference( dbHelper.getInterestSubcategoryPath() ).child( interest ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               for ( DataSnapshot subcategory : dataSnapshot.getChildren() ) {
                   String currSubcategory = (String) subcategory.getValue();
                   subcategoryList.add( currSubcategory );
               }

               subcategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void displayLikedSubcategories() {
        dbHelper.getDb().getReference( dbHelper.getUserInterestPath() ).child( currUserID ).child( interest ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot subcat : dataSnapshot.getChildren() ) {
                    String subcategory = subcat.getKey();

                    subcategoryLayout.setItemChecked( subcategoryList.indexOf( subcategory ), true );
                    prevGenderChecked.put( subcategoryList.indexOf( subcategory ), true );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}
