package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.util.Log;

public class DummyActivity extends AppCompatActivity {
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = DummyActivity.class.getSimpleName();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /********Code for testing out passing strings through activities****/

        textView1 = findViewById(R.id.testText1);
        textView2 = findViewById(R.id.testText2);
        textView3 = findViewById(R.id.testText3);
        textView4 = findViewById(R.id.testText4);

//        // TODO: retrieve bundle & retrieve information
        // String testString = getIntent().getStringExtra("testString1");
        Intent mIntent = getIntent();
        mBundle = mIntent.getExtras();
        String testString = mBundle.getString("testString1");
        int testInt = mBundle.getInt("testInt1");
        CurrentUser testCurrentUser = mBundle.getParcelable("testCurrentUser");

        textView1.setText("HELLO WORLD");

        if( mBundle != null) {
            if( testString != null)
                textView2.setText(testString);
            if( testInt != 0 )
                textView3.setText(Integer.toString(testInt));
            if (testCurrentUser != null ) {
                textView4.setText(testCurrentUser.getIntroduction());
            } else {
                textView4.setText("testCurrentUser is null!");
            }
        }

        /****************************************************************/

//        Intent mIntent = getIntent();
//        mBundle = mIntent.getExtras();
//        String testString = null;

//        if (mBundle != null) {
//            testString = getIntent().getStringExtra("TestString");
//        } else {
//            testString = "Bundle is null!";
//        }

//        testString = getIntent().getStringExtra("TestString");
//
//        if( testString == null )
//            textView1.setText("String is null!");
//        else
//            textView1.setText(testString);

//        // TODO: do some error checking before using the CurrentUser object
//        CurrentUser user_test = (CurrentUser) mIntent.getParcelableExtra("UserObject");
//        if( user_test == null )
//            textView2.setText("CurrentUser Object (1) is null!");
//        else
//            textView2.setText(user_test.getClass().getName());

//        CurrentUser user_test2 = (CurrentUser) mIntent.getParcelableExtra("UserObject2");
//        if( user_test2 == null )
//            textView3.setText("CurrentUser Object (2) is null!");
//        else
//            textView3.setText(user_test2.getClass().getName());

        //textView2.setText( user_test.getBio() );


    }

}
