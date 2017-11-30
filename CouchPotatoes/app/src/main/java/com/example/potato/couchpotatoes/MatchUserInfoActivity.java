package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MatchUserInfoActivity extends AppCompatActivity {
    private DBHelper helper;
    private TextView matchUserInfoGeneralHeader;
    private TextView matchUserInfoGeneralText;
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
        matchUserInfoInterestHeader = (TextView) findViewById(R.id.matchUserInfoInterestHeader);
        matchUserInfoInterestText = (TextView) findViewById(R.id.matchUserInfoInterestText);

        currMatchID = getIntent().getExtras().getString( "currMatchID" );

        if ( currMatchID == null || currMatchID.equals( "" ) ) {
            // TODO HANDLE IF NULL
            currMatchID = "MATCH USER ID COULD NOT BE READ";
        }

        //matchUserInfoGeneralHeader.setText( currMatchID );

        helper.getDb().getReference( helper.getUserPath() + currMatchID ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> res = new HashMap<>();

                for ( DataSnapshot children : dataSnapshot.getChildren() ) {
                    res.put( children.getKey(), children.getValue() );
                }

                String generalInfoHeader = "General Info";

                //genInfoHeader.setText( generalInfoHeader );
                matchUserInfoGeneralHeader.setText( generalInfoHeader );

                String firstName = (String) res.get( "firstName" );
                String middleName = (String) res.get( "middleName" );
                String lastName = (String) res.get( "lastName" );
                String gender = (String) res.get( "gender" );
                String birth_date = (String) res.get( "birth_date" );
                String bio = (String) res.get( "bio" );

                //MatchedUser match = new MatchedUser( currMatchID, firstName, middleName, lastName, birth_date, gender, "", "", "", bio, 0, 0, false, false );

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

                // TODO Maybe fetch and display profile pic here also
                //userInfo += "General Info:\n\n";
                //userInfo += paddSpace( "First Name:", "", 19 );
                //userInfo += firstName + "\n\n";
                //userInfo += paddSpace( "Middle Name:", "", 17 );
                //userInfo += middleName + "\n\n";
                //userInfo += paddSpace( "Last Name:", "", 18 );
                //userInfo += lastName + "\n\n";
                //userInfo += paddSpace( "Gender:", "", 19 );
                //userInfo += gender + "\n\n";
                //userInfo += paddSpace( "Birth Day:", "", 20 );
                //userInfo += birth_date + "\n";

                //userInfo += paddSpace( "Bio:", "", 19 );
                //userInfo += bio + "\n\n";


                //userInfo += helper.getFullName( firstName, middleName, lastName );

                // Omitt middle name here
                //userInfo += helper.getFullName( firstName, "", lastName );
                //userInfo += "\t\t\t\t\t";

                String genderAbbrev = "";

                // Abbreviate gender
                // If non-binary, do not mention gender
                if ( gender.equals( "male" ) ) {
                    genderAbbrev= "M";
                }
                else if ( gender.equals( "female" ) ) {
                    genderAbbrev = "F";
                }

                int numSpaces = 30;

                userInfo += paddSpace( helper.getFullName( firstName, "", lastName ), genderAbbrev, numSpaces );

                //String format = "%s%30s";
                //userInfo += String.format( format, helper.getFullName( firstName, "", lastName ), genderAbbrev );

                // Omitt birthday here
                //userInfo += "\t\t\t\t\t";
                //userInfo += birth_date;

                //textView.setText( userInfo );

                matchUserInfoGeneralText.setText( userInfo );

                String bioHeaderStr = "Bio";

                //bioHeader.setText( bioHeaderStr );

                //bioText.setText( bio );

                //gUserInfo = userInfo;

                //gUserInfo += "\nInterests:";

                String interestsHeaderStr = "Interests";

                matchUserInfoInterestHeader.setText( interestsHeaderStr );

                // Fetch and display User's Interests
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

                                //Log.d( "TEST", "Interest: " + interest );
                                //Log.d( "TEST", "Subcategory: " + subcategory );
                                //Log.d( "TEST", "Preference: " + preference );

                                    /*
                                    if ( preference.equals( "like" ) || preference.equals( "like to try" ) ) {
                                        interests += "◆  ";
                                    }
                                    else {
                                        interests += "◇  ";
                                    }
                                    */
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

                        //textView.setText( gUserInfo + "\n\n" + interests );
                        //textView.setText( userInfo );
                        //userInfo = "";
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
