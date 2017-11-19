package com.example.potato.couchpotatoes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by casey on 11/18/17.
 */

public class UserActivity extends AppCompatActivity {
    private ImageView profileImageView;
    private TextView nameAgeTxt;
    private TextView locationNameTxt;
    private ImageButton backBtn;
    private User mUser;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        nameAgeTxt = (TextView) findViewById(R.id.nameAgeTxt);
        locationNameTxt = (TextView) findViewById(R.id.locationNameTxt);
        backBtn = (ImageButton) findViewById(R.id.backBtn);

        mUser = (User) getIntent().getSerializableExtra("UserInfo");
        mContext = getApplicationContext();

        String image = "http://www.aft.com/components/com_easyblog/themes/wireframe/images/placeholder-image.png";
        Glide.with(mContext).load(image).into(profileImageView);
        nameAgeTxt.setText(mUser.getFirstName());
        locationNameTxt.setText(mUser.getCity());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
