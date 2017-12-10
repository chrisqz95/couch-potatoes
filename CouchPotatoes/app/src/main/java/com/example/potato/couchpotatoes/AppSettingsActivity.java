package com.example.potato.couchpotatoes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AppSettingsActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private Button deleteAccountBtn;
    private DialogInterface.OnClickListener dialogClickListener;
    private ProgressBar spinner;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        // adds up navigation to the toolbar on top
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = findViewById(R.id.progressBar2);

        dbHelper = DBHelper.getInstance();

        deleteAccountBtn = findViewById(R.id.deleteAccountBtn);

        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == DialogInterface.BUTTON_POSITIVE){
                    deleteAccountBtn.setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);
                    deleteAccount();
                }
            }
        };

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage( "Delete your account and all account data?" )
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Confirm", dialogClickListener)
                        .show();
            }
        });

    }

    private void deleteAccount() {
        String currUserID = dbHelper.getAuth().getUid();

        dbHelper.removeUser( currUserID );

        dbHelper.getAuth().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                spinner.setVisibility(View.GONE);
                dbHelper.getAuth().signOut();
                Intent intent = new Intent( getApplicationContext(), LoginActivity.class );
                startActivity(intent);
                finish();
            }
        });
    }
}
