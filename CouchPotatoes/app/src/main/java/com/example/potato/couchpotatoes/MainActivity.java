package com.example.potato.couchpotatoes;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import org.w3c.dom.Text;

/**
 * The home page which shows potential matches.
 */
public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
    private android.widget.Button logout;
    private android.widget.ImageButton rejectBtn;
    private android.widget.ImageButton acceptBtn;

    // For the user cards
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Sets up the activity layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DBHelper();
        ActionBar actionBar = getActionBar();

        // Find the id's of the buttons
        acceptBtn = (android.widget.ImageButton) findViewById(R.id.acceptBtn);
        rejectBtn = (android.widget.ImageButton) findViewById(R.id.rejectBtn);
        logout = findViewById(R.id.logout);

        // Set up how many cards are displayed as a stack
        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        mContext = getApplicationContext();
        mSwipeView.getBuilder().setDisplayViewCount(3).setSwipeDecor(new SwipeDecor()
                .setPaddingTop(20).setRelativeScale(0.01f));

        // Adds fake users for testing purposes
        User user_test1 = new MatchedUser(null, null, "Bob", null, "Smith",
                null, null, "Los Angleles", null, null, null,
                0, 0, false, false);
        User user_test2 = new MatchedUser(null, null, "Gary", null, "Gillespie",
                null, null, "La Jolla", null, null, null,
                0, 0, false, false);
        User user_test3 = new MatchedUser(null, null, "Amy", null, "Blah",
                null, null, "New York City", null, null, null,
                0, 0, false, false);
        User user_test4 = new MatchedUser(null, null, "Thomas", null, "Anderson",
                null, null, "New York City", null, null, null,
                0, 0, false, false);
        User user_test5 = new MatchedUser(null, null, "Anita", null, "Bath",
                null, null, "New York City", null, null, null,
                0, 0, false, false);
        mSwipeView.addView(new UserCard(mContext, user_test1, mSwipeView));
        mSwipeView.addView(new UserCard(mContext, user_test2, mSwipeView));
        mSwipeView.addView(new UserCard(mContext, user_test3, mSwipeView));
        mSwipeView.addView(new UserCard(mContext, user_test4, mSwipeView));
        mSwipeView.addView(new UserCard(mContext, user_test5, mSwipeView));

        // Simulate a swipe right or left by pressing the bottom buttons
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeView.doSwipe(true);
            }
        });
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeView.doSwipe(false);
            }
        });

        // Log out and display the log in screen
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.auth.signOut();
                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
                finish();
            }
        });
    }

    /**
     * Creates a dialog containing the info of a user and the similar interests they share.
     */
    public static class UserInfoDialogFragment extends DialogFragment {
        /**
         * Creates a dialog fragment using a user object
         * @param user - user to get info from
         * @return dialog containing user info
         */
        public static UserInfoDialogFragment newInstance(User user) {
            UserInfoDialogFragment frag = new UserInfoDialogFragment();
            Bundle args = new Bundle();

            // Pass the user object into the dialog
            args.putSerializable("UserInfo", user);
            frag.setArguments(args);
            return frag;
        }

        // When the dialog is created, show the info
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Sets up the dialog layout
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View content = inflater.inflate(R.layout.user_dialog, null);
            User mUser = (User) getArguments().getSerializable("UserInfo");

            // Creates a close button to dismiss the dialog
            builder.setView(content).setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User closed the dialog
                    UserInfoDialogFragment.this.getDialog().cancel();
                }
            });

            // Populates the user info text views
            TextView nameAgeTxt = (TextView) content.findViewById(R.id.nameAgeTxt);
            TextView locationTxt = (TextView) content.findViewById(R.id.locationTxt);
            nameAgeTxt.setText(mUser.getFirstName());
            locationTxt.setText(mUser.getCity());

            return builder.create();
        }
    }

    /**
     * Grabs a user's info and displays it in a swipe-able card.
     *
     * Source: https://blog.mindorks.com/android-tinder-swipe-view-example-3eca9b0d4794
     */
    @Layout(R.layout.card_view)
    public class UserCard {
        @com.mindorks.placeholderview.annotations.View(R.id.profileImageView)
        private ImageView profileImageView;

        @com.mindorks.placeholderview.annotations.View(R.id.nameAgeTxt)
        private TextView nameAgeTxt;

        @com.mindorks.placeholderview.annotations.View(R.id.locationNameTxt)
        private TextView locationNameTxt;

        private User mUser;
        private Context mContext;
        private SwipePlaceHolderView mSwipeView;
        private String image;

        /**
         * Create a card which displays a user's info.
         *
         * @param context - current activity
         * @param user - the user to display info for
         * @param swipeView - space used to detect a swipe
         */
        public UserCard(Context context, User user, SwipePlaceHolderView swipeView) {
            mContext = context;
            mUser = user;
            mSwipeView = swipeView;
        }

        /**
         * When the card is clicked, open a page displaying more user info.
         */
        @Click(R.id.cardText)
        private void onClick() {
            UserInfoDialogFragment userFrag = UserInfoDialogFragment.newInstance(mUser);
            userFrag.show(getFragmentManager(), "info");
        }

        /**
         * Populate the image and text fields of the card.
         */
        @Resolve
        private void onResolved() {
            // Loads the profile image
            image = "http://www.aft.com/components/com_easyblog/themes/wireframe/images/placeholder-image.png";
            Glide.with(mContext).load(image).into(profileImageView);
            nameAgeTxt.setText(mUser.getFirstName());
            locationNameTxt.setText(mUser.getCity());

            /**
             * When the profile image is clicked, open the user gallery activity
             */
            profileImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, UserGalleryActivity.class));
                }
            });
        }

        /**
         * When the card is swiped left, log the event
         */
        @SwipeOut
        private void onSwipedOut() {
            Log.d("EVENT", "onSwipedOut");
            mSwipeView.addView(this);
        }

        /**
         * When the card is released but wasn't swiped, reset to its original position
         */
        @SwipeCancelState
        private void onSwipeCancelState() {
            Log.d("EVENT", "onSwipeCancelState");
        }

        /**
         * When the card is swiped right, log the event
         */
        @SwipeIn
        private void onSwipeIn() {
            Log.d("EVENT", "onSwipedIn");
        }

        /**
         * Detects when the card is moved enough to the right to count as an accept
         */
        @SwipeInState
        private void onSwipeInState() {
            Log.d("EVENT", "onSwipeInState");
        }

        /**
         * Detects when the card is moved enough to the left to count as a reject
         */
        @SwipeOutState
        private void onSwipeOutState() {
            Log.d("EVENT", "onSwipeOutState");
        }
    }
}
