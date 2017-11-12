package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class RegistrationActivity extends AppCompatActivity {
    DBHelper helper = new DBHelper();
    EditText mFirstName;
    EditText mMiddleName;
    EditText mLastName;
    EditText mBirthDate;
    EditText mGender;
    Button submit;
    CurrentUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d( "TEST", helper.auth.getUid() );

        mFirstName = (EditText) findViewById(R.id.firstName);
        mMiddleName = (EditText) findViewById(R.id.middleName);
        mLastName = (EditText) findViewById(R.id.lastName);
        mBirthDate = (EditText) findViewById(R.id.birthDate);
        mGender = (EditText) findViewById(R.id.gender);
        submit = (Button) findViewById(R.id.Submit);

        // TODO Delete user account if registration process is aborted or reopen registration process if
        // user closes app during registration

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                if ( attemptRegistration() ) {
                  startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                  finish();
                }
                else {
                    // TODO Could not create User
                }
                */
                attemptRegistration();
            }
        });

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    protected void attemptRegistration() {
        String email = (String) getIntent().getExtras().get( "email" );
        String password = (String) getIntent().getExtras().get( "password" );

        helper.auth.createUserWithEmailAndPassword( email, password ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

        String email = helper.auth.getCurrentUser().getEmail();
        String userID = helper.auth.getUid();
        String firstName = mFirstName.getText().toString();
        String middleName = mMiddleName.getText().toString();
        String lastName = mLastName.getText().toString();
        String birthDate = mBirthDate.getText().toString();
        String gender = mGender.getText().toString();
        String city = "";
        String state = "";
        String country = "";
        String bio = "";
        String displayName = helper.getFullName( firstName, middleName, lastName );
        Double latitude = 0.0;
        Double longitude = 0.0;
        boolean locked = false;
        boolean suspended = false;

        user = new CurrentUser(
                email, userID, firstName, middleName, lastName, birthDate, gender, city, state, country, bio,
                latitude, longitude, locked, suspended );

        helper.updateAuthUserDisplayName( displayName );

        String password = (String) getIntent().getExtras().get( "password" );

        helper.auth.signInWithEmailAndPassword( email, password ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                getIntent().putExtra( "password", "" );

                helper.addNewUser( user );

                startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                finish();
            }
        });

        //helper.addNewUser( user );

        //Log.d( "TEST", displayName );

                //startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                //finish();

            }
        });
        // TODO return helper.addNewUser( user );
        //return false;
    }
}
