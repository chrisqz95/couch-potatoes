package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MatchPageFragment extends Fragment {
    public static final String ARG_LIST = "ARG_LIST";

    private final int BIO_SUBSTRING_LENGTH = 60;

    private ArrayList<String> matchedUserList;
    private FloatingActionButton matchButton;
    private FloatingActionButton unmatchButton;
    private DBHelper helper;

    private String currMatchID;
    private TextView bioText;
    private TextView interestsHeader;
    private TextView interestsText;
    private TextView userInfoText;
    private LinearLayout matchingUserInfoLayout;

    private ImageView imgView;

    private String gUserInfo;

    /**
     * TODO: NOTE IF WE WANT TO PASS IN THE LIST DIRECTLY, WE NEED TO MAKE MATCHEDUSER EXTEND PARCELABLE
     * @param savedInstanceState
     */
//    public static MatchPageFragment newInstance(List<MatchedUser> matchedUserList) {
//        Bundle args = new Bundle();
//        args.putParcelableArrayList(ARG_LIST, matchedUserList);
//        MatchPageFragment fragment = new MatchPageFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }

    /**
     * give it a list of the strings of the matched users
     * @param matchedUserList
     * @return
     */
    public static MatchPageFragment newInstance(ArrayList<String> matchedUserList ) {
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_LIST, matchedUserList);
        MatchPageFragment fragment = new MatchPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new DBHelper();

        matchedUserList = getArguments().getStringArrayList(ARG_LIST);

        matchButton = (FloatingActionButton) getActivity().findViewById(R.id.fab_match);
        unmatchButton = (FloatingActionButton) getActivity().findViewById(R.id.fab_unmatch);

        matchButton.setOnClickListener(onClickListener);
        unmatchButton.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            //Log.d( "TEST", "FRAGMENT CLICKED" );
            // NOTE: MAY NOT NEED THIS
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_page, container, false);
        bioText = (TextView) view.findViewById(R.id.bioText);
        interestsHeader = (TextView) view.findViewById(R.id.interestsHeader);
        interestsText = (TextView) view.findViewById(R.id.interestsText);
        userInfoText = (TextView) view.findViewById(R.id.userInfoText);
        matchingUserInfoLayout = (LinearLayout) view.findViewById(R.id.matchingUserInfoLayout);

        if ( matchedUserList.isEmpty() ) {
            userInfoText.setText( "No new matches. Try adding more interests!" );
        }
        else {
            currMatchID = matchedUserList.get( 0 );

            matchingUserInfoLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
                @Override
                public void onClick() {
                    // Begin new activity to display more information about the potential match
                    Intent intent = new Intent( getActivity().getApplicationContext(), MatchUserInfoActivity.class );
                    intent.putExtra( "currMatchID", currMatchID );
                    startActivity( intent );
                }

                @Override
                public void onSwipeLeft() {
                    Toast.makeText(getActivity(), "Disliked!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSwipeRight() {
                    Toast.makeText(getActivity(), "Liked!", Toast.LENGTH_SHORT).show();
                }
            });

            // Fetch and display info about the potential match
            // TODO Create method to do this
            helper.getDb().getReference( helper.getUserPath() + currMatchID ).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> res = new HashMap<>();

                    for ( DataSnapshot children : dataSnapshot.getChildren() ) {
                        res.put( children.getKey(), children.getValue() );
                    }

                    String firstName = (String) res.get( "firstName" );
                    String middleName = (String) res.get( "middleName" );
                    String lastName = (String) res.get( "lastName" );
                    String gender = (String) res.get( "gender" );
                    String birth_date = (String) res.get( "birth_date" );
                    String bio = (String) res.get( "bio" );

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
                    // TODO Create helper method to do this
                    if ( gender.equals( "male" ) ) {
                        genderAbbrev= "M";
                    }
                    else if ( gender.equals( "female" ) ) {
                        genderAbbrev = "F";
                    }

                    // Get the potential match's full name
                    // Omitt the middle name - Personal preference - Can change later
                    String potentMatchName = helper.getFullName( firstName, "", lastName );

                    // Display potential match's full name and gender on the same line
                    int numSpaces = 30;
                    userInfo += paddSpace( potentMatchName, genderAbbrev, numSpaces );

                    userInfoText.setText( userInfo );

                    // Display substring of bio here and full bio in MatchUserInfoActivity
                    // TODO Create helper method to do this
                    if ( bio.length() <= BIO_SUBSTRING_LENGTH ) {
                        bioText.setText( bio );
                    }
                    else {
                        String bioSubString = bio.substring( 0, BIO_SUBSTRING_LENGTH ) + " ...";
                        bioText.setText( bioSubString );
                    }

                    String interestsHeaderStr = "Interests";

                    interestsHeader.setText( interestsHeaderStr );

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

                            interestsText.setText( interests );
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

        return view;
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
