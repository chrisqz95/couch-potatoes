package com.example.potato.couchpotatoes;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AlertDialogLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PictureGridActivity extends AppCompatActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    // Lists to hold data for pictures
    private ArrayList<String> urlList;
    private ArrayList<String> hashList;

    // DBHelper to fetch references
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout to loading screen until FireBase returns data
        setContentView(R.layout.activity_picture_grid_loading);

        Bundle extras = getIntent().getExtras();
        dbHelper = new DBHelper();

        // Load data from FireBase
        // TODO: Replace hardcoded strings
        try {
            loadData(extras.getString("uid"), extras.getBoolean("isCurrentUser"));
        } catch (NullPointerException e) {
            Log.e(PictureGridActivity.class.toString(), e.getMessage());
            finish();
        }
    }

    private void loadData(final String uid, final boolean isCurrentUser) {
        dbHelper.fetchCurrentUser();

        // Create new lists
        urlList = new ArrayList<>();
        hashList = new ArrayList<>();

        // Fetch photo URL list from FireBase
        final DatabaseReference ref = dbHelper.getDb().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                urlList.clear();
                hashList.clear();
                // TODO: Replace hardcoded strings
                for (DataSnapshot dataSnapshot : snapshot.child("User_Photo").child(uid).getChildren()) {
                    try {
                        // Add each photo's URL and hash to the list
                        urlList.add(snapshot.child("Photo").child(dataSnapshot.getKey()).child("uri").getValue().toString());
                        hashList.add(dataSnapshot.getKey().toString());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                // Finished loading URLs, set grid layout to display photos.
                setContentView(R.layout.activity_picture_grid);
                setGrid();

                // Check if user has permission to edit photos, edit layout accordingly
                if (isCurrentUser) {
                    enableEditing(uid);
                } else {
                    disableEditing();
                }
                // Remove listener after data has been loaded
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log error and remove listener
                Log.e(PictureGridActivity.class.toString(), "The read failed: " + databaseError.getMessage());
                System.out.println("The read failed: " + databaseError.getMessage());
                ref.removeEventListener(this);
            }
        });
    }

    public void setGrid(){
        // Set the GridVew and GridAdapter variables
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(PictureGridActivity.this, R.layout.fragment_picture_grid_item, urlList);
        gridView.setAdapter(gridAdapter);

        // Add click listener to start PictureGridTabViewActivity
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PictureGridActivity.this, PictureGridTabViewActivity.class);
                intent.putExtra("itemCount", urlList.size());
                intent.putExtra("urlList", urlList);
                intent.putExtra("startingItem", position);
                startActivity(intent);
            }
        });
    }

    public void updateGrid(){
        gridAdapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    public void enableEditing(final String uid){
        // Add delete functionality via hold on photo
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(PictureGridActivity.this)
                        // TODO: Replace hardcoded strings
                        .setTitle("Are you sure you want to delete this photo?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Storage reference to photo and delete it
                                StorageReference storageRef = dbHelper.getStorage().getReference("Photo/" + uid + "/" + hashList.get(position));
                                storageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // Remove database entries
                                        final DatabaseReference dbRef = dbHelper.getDb().getReference();
                                        dbRef.child("Photo").child(hashList.get(position)).removeValue();
                                        dbRef.child("User_Photo").child(uid).child(hashList.get(position)).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        // Remove from local lists and update view
                                                        urlList.remove(position);
                                                        hashList.remove(position);
                                                        updateGrid();
                                                        // TODO: Replace hardcoded strings
                                                        Toast.makeText(getApplicationContext(), "Photo Successfully Deleted", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }
        });
    }

    public void disableEditing(){
        // Disable button from being visible
        ConstraintLayout fab = findViewById(R.id.btnUploadImage);
        fab.setVisibility(View.GONE);
    }
}
