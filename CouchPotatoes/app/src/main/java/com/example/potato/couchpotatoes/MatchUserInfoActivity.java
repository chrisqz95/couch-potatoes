package com.example.potato.couchpotatoes;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.potato.couchpotatoes.StringUtilities.*;

public class MatchUserInfoActivity extends AppCompatActivity {
    private DBHelper helper;
    private TextView matchUserInfoGeneralHeader;
    private TextView matchUserInfoGeneralText;
    private TextView matchUserInfoBioHeader;
    private TextView matchUserInfoBioText;
    private TextView matchUserInfoInterestHeader;
    private TextView matchUserInfoInterestText;
    private String currMatchID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        helper = new DBHelper();

        matchUserInfoGeneralHeader = (TextView) findViewById(R.id.matchUserInfoGeneralHeader);
        matchUserInfoGeneralText = (TextView) findViewById(R.id.matchUserInfoGeneralText);
        matchUserInfoBioHeader = (TextView) findViewById(R.id.matchUserInfoBioHeader);
        matchUserInfoBioText = (TextView) findViewById(R.id.matchUserInfoBioText);
        matchUserInfoInterestHeader = (TextView) findViewById(R.id.matchUserInfoInterestHeader);
        matchUserInfoInterestText = (TextView) findViewById(R.id.matchUserInfoInterestText);

        currMatchID = getIntent().getExtras().getString( "currMatchID" );

        if ( currMatchID == null || currMatchID.equals( "" ) ) {
            currMatchID = "MATCH USER ID COULD NOT BE READ";
        }

        // Fetch and display potential match's info
        helper.getDb().getReference( helper.getUserPath() + currMatchID ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> res = new HashMap<>();

                for ( DataSnapshot children : dataSnapshot.getChildren() ) {
                    res.put( children.getKey(), children.getValue() );
                }

                String firstName = (String) res.get( "firstName" );
                String lastName = (String) res.get( "lastName" );
                String gender = (String) res.get( "gender" );
                String birth_date = (String) res.get( "birth_date" );
                String bio = (String) res.get( "bio" );
                String city = (String) res.get( "city" );
                String state = (String) res.get( "state" );
                String country = (String) res.get( "country" );

                String userInfo = "";
                String genderAbbrev = "";

                // Abbreviate gender. If non-binary, do not mention gender
                if ( gender.equals( "male" ) )
                    genderAbbrev= "M";
                else if ( gender.equals( "female" ) )
                    genderAbbrev = "F";

                // Omit middle name here - Personal preference - can change later
                String potentMatchName = helper.getFullName( firstName, "", lastName );
                matchUserInfoGeneralHeader.setText( potentMatchName );

                userInfo += paddSpaceln( "Gender: ", genderAbbrev, 38 );
                userInfo += "\n";

                // Calculate and display age instead of birthday
                userInfo += paddSpaceln( "Birthday: ", birth_date, 34 );
                userInfo += "\n";
                userInfo += paddSpaceln( "City: ", city, 38 );
                userInfo += "\n";
                userInfo += paddSpaceln( "State: ", state, 37 );
                userInfo += "\n";
                userInfo += paddSpace( "Country: ", country, 36 );

                matchUserInfoGeneralText.setText( userInfo );

                String bioHeaderStr = "About Me";
                matchUserInfoBioHeader.setText( bioHeaderStr );
                matchUserInfoBioText.setText( bio );

                String interestsHeaderStr = "My Interests";
                matchUserInfoInterestHeader.setText( interestsHeaderStr );

                // Fetch and display User's Interests
                helper.getDb().getReference( helper.getUserInterestPath() ).child( currMatchID ).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {                                                
                        InterestStringBuilder builder = new InterestStringBuilder();
                        String interests = builder.getInterestString(dataSnapshot) ;
                        matchUserInfoInterestText.setText( interests );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
