package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

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
            // TODO HANDLE IF NULL
            currMatchID = "MATCH USER ID COULD NOT BE READ";
        }

        // Fetch and display potential match's info
        // TODO Create method to do this
        helper.getDb().getReference( helper.getUserPath() + currMatchID ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> res = new HashMap<>();

                for ( DataSnapshot children : dataSnapshot.getChildren() ) {
                    res.put( children.getKey(), children.getValue() );
                }

                String generalInfoHeader = "General Info";

                matchUserInfoGeneralHeader.setText( generalInfoHeader );

                String firstName = (String) res.get( "firstName" );
                String middleName = (String) res.get( "middleName" );
                String lastName = (String) res.get( "lastName" );
                String gender = (String) res.get( "gender" );
                String birth_date = (String) res.get( "birth_date" );
                String bio = (String) res.get( "bio" );
                String city = (String) res.get( "city" );
                String state = (String) res.get( "state" );
                String country = (String) res.get( "country" );

                String userInfo = "";

                // TODO Need a better way to format text
                    /*
                    String format = "%30s%30s\n";
                    userInfo += String.format( format, "First Name:", firstName );
                    userInfo += String.format( format, "Middle Name:", middleName );
                    userInfo += String.format( format, "Last Name:", lastName );
                    userInfo += String.format( format, "Gender:", gender );
                    userInfo += String.format( format, "Birth Day:", birth_date );
                    userInfo += String.format( format, "Bio:", bio );
                    */

                String genderAbbrev = "";

                // Abbreviate gender
                // If non-binary, do not mention gender
                // TODO Create method to do this
                if ( gender.equals( "male" ) ) {
                    genderAbbrev= "M";
                }
                else if ( gender.equals( "female" ) ) {
                    genderAbbrev = "F";
                }

                // Omitt middle name here - Personal preference - can change later
                userInfo += paddSpaceln( "Name: ", helper.getFullName( firstName, "", lastName ), 30 );
                userInfo += "\n";
                userInfo += paddSpaceln( "Gender: ", genderAbbrev, 38 );
                userInfo += "\n";
                // TODO
                // Calculate and display age instead of birthday
                userInfo += paddSpaceln( "Birthday: ", birth_date, 34 );
                userInfo += "\n";
                userInfo += paddSpaceln( "City: ", city, 38 );
                userInfo += "\n";
                userInfo += paddSpaceln( "State: ", state, 37 );
                userInfo += "\n";
                userInfo += paddSpaceln( "Country: ", country, 36 );

                matchUserInfoGeneralText.setText( userInfo );

                String bioHeaderStr = "About Me";

                matchUserInfoBioHeader.setText( bioHeaderStr );

                matchUserInfoBioText.setText( bio );

                String interestsHeaderStr = "Interests";

                matchUserInfoInterestHeader.setText( interestsHeaderStr );

                // Fetch and display User's Interests
                // TODO Create method to do this
                helper.getDb().getReference( helper.getUserInterestPath() ).child( currMatchID ).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String interests = "";

                        for ( DataSnapshot child : dataSnapshot.getChildren() ) {
                            String interest = child.getKey();
                            interests += interest;
                            interests += "\n\n";

                            for ( DataSnapshot subchild : child.getChildren() ) {
                                String subcategory = subchild.getKey();
                                String preference = (String) subchild.getValue();


                                int newLinePos = 22;
                                //interests += "◇  ";
                                interests += "    ";
                                interests += addStrAtPos( subcategory, "\n     ", newLinePos );
                                interests += "  -  ";
                                interests += addStrAtPos( preference, "\n     ", newLinePos );
                                interests += "\n";
                            }
                            interests += "\n";
                        }

                        matchUserInfoInterestText.setText( interests );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d( "TEST", databaseError.getMessage() );
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.getMessage() );
            }
        });
    }

    //TODO MOVE THESE METHODS TO A NEW CLASS
    private String paddSpace( String title, String value, int desiredLength ) {
        String str = "";

        str += title;

        int numSpaces = desiredLength - title.length() - value.length();

        for ( int i = 0; i < numSpaces; i++ ) {
            str += "\t";
        }

        str += value;

        return str;
    }

    private String paddSpaceln( String title, String value, int desiredLength ) {
        return paddSpace( title, value + "\n", desiredLength );
    }

    private String paddSpaceEnd( String title, String value, int desiredLength ) {
        String str = "";

        str += title;

        str += value;

        int numSpaces = desiredLength - title.length() - value.length();

        for ( int i = 0; i < numSpaces; i++ ) {
            str += "\t";
        }

        str += "|";

        return str;
    }

    private String paddSpaceEndln( String title, String value, int desiredLength ) {
        return paddSpaceEnd( title, value, desiredLength ) + "\n";
    }

    private String addStrAtPos( String str, String addition, int position ) {
        String ret = "";

        for ( int i = 0; i < str.length(); i++ ) {
            if ( i == position ) {
                ret += addition;
            }
            ret += str.charAt( i );
        }

        return ret;
    }

}
