package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

    private ArrayList<String> matchedUserList;
    private FloatingActionButton matchButton;
    private FloatingActionButton unmatchButton;
    private DBHelper helper;

    private String currMatchID;
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
    public static MatchPageFragment newInstance(ArrayList<String> matchedUserList) {
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
            switch (v.getId()) {
                case R.id.fab_match:
                    //TODO
                    break;

                case R.id.fab_unmatch:
                    //TODO
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
            currMatchID = matchedUserList.get( 0 );

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
}
