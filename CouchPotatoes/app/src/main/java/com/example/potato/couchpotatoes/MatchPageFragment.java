package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchPageFragment extends Fragment {
    public static final String ARG_LIST = "ARG_LIST";

    private final String DATE_MATCH_TYPE = "DATE";
    private final String FRIEND_MATCH_TYPE = "FRIEND";

    private ArrayList<String> matchedUserList;
    private FloatingActionButton matchButton;
    private FloatingActionButton unmatchButton;
    private DBHelper helper;

    private TabLayout tabLayout;

    private String currMatchID;
    private String currDateMatchID;
    private String currFriendMatchID;
    private String matchType;
    private TextView textView;

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
    public static MatchPageFragment newInstance(ArrayList<String> matchedUserList, String matchType ) {
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_LIST, matchedUserList);
        args.putString( "matchType", matchType );
        MatchPageFragment fragment = new MatchPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new DBHelper();

        matchedUserList = getArguments().getStringArrayList(ARG_LIST);
        matchType = getArguments().getString( "matchType" );

        matchButton = (FloatingActionButton) getActivity().findViewById(R.id.fab_match);
        unmatchButton = (FloatingActionButton) getActivity().findViewById(R.id.fab_unmatch);

        matchButton.setOnClickListener(onClickListener);
        unmatchButton.setOnClickListener(onClickListener);
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            String currUserID = helper.getAuth().getUid();
            String timestamp = "0000-00-00 00:00:00";

            Log.d( "TEST", matchType );

            switch (v.getId()) {
                case R.id.fab_match:
                    Log.d( "TEST", "LIKE" );
                    if ( matchType.equals( DATE_MATCH_TYPE ) ) {
                        helper.addToLike( currUserID, currDateMatchID, timestamp );
                    }
                    else if ( matchType.equals( FRIEND_MATCH_TYPE ) ) {
                        helper.addToLike( currUserID, currFriendMatchID, timestamp );
                    }
                    break;

                case R.id.fab_unmatch:
                    Log.d( "TEST", "DISLIKE" );
                    if ( matchType.equals( DATE_MATCH_TYPE ) ) {
                        helper.addToDislike( currUserID, currDateMatchID, timestamp );
                    }
                    else if ( matchType.equals( FRIEND_MATCH_TYPE ) ) {
                        helper.addToDislike( currUserID, currFriendMatchID, timestamp );
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_page, container, false);
        textView = (TextView) view.findViewById(R.id.match_fragment_text);

        //textView.setText("Name 0: " +  matchedUserList.get(0) + "\n kdjfkdjf\ndkjfkdjf\ndkjfkd\ndkjfd\nkdjfdf\ndfkjdkf\ndkjfd");

        if ( matchedUserList.isEmpty() ) {
            textView.setText( "No new matches. Try adding more interests!" );
        }
        else {
            Log.d( "TEST", matchType );
            //String tag = getFragmentManager().getBackStackEntryAt( getFragmentManager().getBackStackEntryCount() - 1 ).getName();
            //Log.d( "TEST", tag );
            Log.d( "TEST", Integer.toString( view.getId() ) );

            currMatchID = matchedUserList.get( 0 );

            if ( matchType.equals( DATE_MATCH_TYPE ) ) {
                currDateMatchID = currMatchID;
            }
            else if ( matchType.equals( FRIEND_MATCH_TYPE ) ) {
                currFriendMatchID = currMatchID;
            }

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

                    //MatchedUser match = new MatchedUser( currMatchID, firstName, middleName, lastName, birth_date, gender, "", "", "", bio, 0, 0, false, false );

                    String userInfo = "";

                    // TODO Need a better way to format text
                    /*
                    userInfo += "First Name:"; userInfo += getTabs( 5 ); userInfo += firstName; userInfo += "\n";
                    userInfo += "Middle Name:"; userInfo += getTabs( 4 ); userInfo += middleName; userInfo += "\n";
                    userInfo += "Last Name:"; userInfo += getTabs( 4 ); userInfo += lastName; userInfo += "\n";
                    userInfo += "Gender:"; userInfo += getTabs( 7 ); userInfo += gender; userInfo += "\n";
                    userInfo += "Birth Day:"; userInfo += getTabs( 10 ); userInfo += birth_date; userInfo += "\n";
                    userInfo += "bio:"; userInfo += getTabs( 12 ); userInfo += bio; userInfo += "\n";
                    */

                    // TODO Maybe fetch and display profile pic here also
                    userInfo += "First Name:"; userInfo += firstName; userInfo += "\n";
                    userInfo += "Middle Name:"; userInfo += middleName; userInfo += "\n";
                    userInfo += "Last Name:"; userInfo += lastName; userInfo += "\n";
                    userInfo += "Gender:"; userInfo += gender; userInfo += "\n";
                    userInfo += "Birth Day:"; userInfo += birth_date; userInfo += "\n";
                    userInfo += "bio:"; userInfo += bio; userInfo += "\n";

                    textView.setText( userInfo );
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d( "TEST", databaseError.getMessage() );
                }
            });
        }

        return view;
    }

    /*
    private String getTabs( int numTabs ) {
        String str = "";
        for ( int i = 0; i < numTabs; i++ ) {
            str += "\t";
        }
        return str;
    }
    */
    public void printMatchType () {
        Log.d( "TEST", matchType );
    }
}
