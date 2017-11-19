package com.example.potato.couchpotatoes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
    private android.widget.Button logout;
    private android.widget.ImageButton rejectBtn;
    private android.widget.ImageButton acceptBtn;
    private android.widget.ImageButton profileBtn;
    private android.widget.Button datingBtn;
    private android.widget.Button friendsBtn;
    private android.widget.ImageButton messengerBtn;

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DBHelper();

        profileBtn = (android.widget.ImageButton) findViewById(R.id.profileBtn);
        datingBtn = (android.widget.Button) findViewById(R.id.datingBtn);
        friendsBtn = (android.widget.Button) findViewById(R.id.friendsBtn);
        messengerBtn = (android.widget.ImageButton) findViewById(R.id.messengerBtn);

        acceptBtn = (android.widget.ImageButton) findViewById(R.id.acceptBtn);
        rejectBtn = (android.widget.ImageButton) findViewById(R.id.rejectBtn);
        logout = findViewById(R.id.logout);

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        mContext = getApplicationContext();
        mSwipeView.getBuilder().setDisplayViewCount(3).setSwipeDecor(new SwipeDecor()
                .setPaddingTop(20).setRelativeScale(0.01f));

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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.auth.signOut();
                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
                finish();
            }
        });
    }

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

        public UserCard(Context context, User user, SwipePlaceHolderView swipeView) {
            mContext = context;
            mUser = user;
            mSwipeView = swipeView;
        }

        @Click(R.id.cardView)
        private void onClick() {
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            intent.putExtra("UserInfo", mUser);
            startActivity(intent);
        }

        @Resolve
        private void onResolved() {
            image = "http://www.aft.com/components/com_easyblog/themes/wireframe/images/placeholder-image.png";
            Glide.with(mContext).load(image).into(profileImageView);
            nameAgeTxt.setText(mUser.getFirstName());
            locationNameTxt.setText(mUser.getCity());
        }

        @SwipeOut
        private void onSwipedOut() {
            Log.d("EVENT", "onSwipedOut");
            mSwipeView.addView(this);
        }

        @SwipeCancelState
        private void onSwipeCancelState() {
            Log.d("EVENT", "onSwipeCancelState");
        }

        @SwipeIn
        private void onSwipeIn() {
            Log.d("EVENT", "onSwipedIn");
        }

        @SwipeInState
        private void onSwipeInState() {
            Log.d("EVENT", "onSwipeInState");
        }

        @SwipeOutState
        private void onSwipeOutState() {
            Log.d("EVENT", "onSwipeOutState");
        }
    }
}
