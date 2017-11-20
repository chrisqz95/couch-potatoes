package com.example.potato.couchpotatoes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * A page that displays more info about a potential match
 */
public class UserActivity extends AppCompatActivity {
    private ImageView profileImageView;
    private TextView nameAgeTxt;
    private TextView locationNameTxt;
    private User mUser;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Creates a back button to return to the main page
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Finds the image and text field id's
        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        nameAgeTxt = (TextView) findViewById(R.id.nameAgeTxt);
        locationNameTxt = (TextView) findViewById(R.id.locationNameTxt);

        // Gets the user info that was passed from the main page
        mUser = (User) getIntent().getSerializableExtra("UserInfo");
        mContext = getApplicationContext();

        // Populates the image and text fields
        String image = "http://www.aft.com/components/com_easyblog/themes/wireframe/images/placeholder-image.png";
        Glide.with(mContext).load(image).into(profileImageView);
        nameAgeTxt.setText(mUser.getFirstName());
        locationNameTxt.setText(mUser.getCity());
    }

    /**
     * When the back button is pressed, return to the main page.
     *
     * @param item - the back button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }
}
