package com.example.potato.couchpotatoes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AlertDialogLayout;
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

    //    private String uid;
//    private boolean uploadButton;
    private boolean isCurrentUser = false;
    private ArrayList<String> urlList;
    private ArrayList<String> hashList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_picture_grid_loading);
        Bundle extras = getIntent().getExtras();
        loadData(extras.getString("uid"), extras.getBoolean("isCurrentUser"));


//        Picasso picasso = new Picasso.Builder(getApplicationContext()).memoryCache(new LruCache(2400000)).build();
//        picasso.setIndicatorsEnabled(true);
//        Picasso.setSingletonInstance(picasso);

    }

    private void loadData(final String uid, final boolean isCurrentUser) {
        final DBHelper dbHelper = new DBHelper();
        dbHelper.fetchCurrentUser();

        urlList = new ArrayList<>();
        hashList = new ArrayList<>();

        final DatabaseReference ref = dbHelper.getDb().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                urlList.clear();
                hashList.clear();
                for (DataSnapshot dataSnapshot : snapshot.child("User_Photo").child(uid).getChildren()) {
                    try {
                        urlList.add(snapshot.child("Photo").child(dataSnapshot.getKey()).child("uri").getValue().toString());
                        hashList.add(dataSnapshot.getKey().toString());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                setContentView(R.layout.activity_picture_grid);
                gridView = (GridView) findViewById(R.id.gridView);
                gridAdapter = new GridViewAdapter(PictureGridActivity.this, R.layout.fragment_picture_grid_item, urlList);
                gridView.setAdapter(gridAdapter);
                gridView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(PictureGridActivity.this, PictureGridTabViewActivity.class);
                        intent.putExtra("itemCount", urlList.size());
                        intent.putExtra("urlList", urlList);
                        intent.putExtra("startingItem", position);
                        startActivity(intent);
                    }
                });

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                if (isCurrentUser) {
                    gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            new AlertDialog.Builder(PictureGridActivity.this)
                                    .setTitle("Are you sure you want to delete this image?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //StorageReference storageRef = dbHelper.getStorage().getReference(); //("Photo/pPqKDpd6TXaO5Utj7s3Te6OTaLT2/Tobedeleted.PNG");
                                            //storageRef.child("Photo/").child(uid + "/").child(hashList.get(position));
                                            StorageReference storageRef = dbHelper.getStorage().getReference("Photo/" + uid + "/" + hashList.get(position));
                                            storageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    final DatabaseReference dbRef = dbHelper.getDb().getReference();
                                                    dbRef.child("Photo").child(hashList.get(position)).removeValue();
                                                    dbRef.child("User_Photo").child(uid).child(hashList.get(position)).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    loadData(uid, isCurrentUser);
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                            Toast.makeText(getApplicationContext(), "TEST", Toast.LENGTH_LONG).show();
                            return true;
                        }
                    });

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //startActivity(new Intent(PictureGridActivity.this, UploadImageFragment.class));
                            Snackbar.make(view, "TODO: UploadImageFragment", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

//                        FragmentManager fragmentManager = getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                        UploadImageFragment fragment = new UploadImageFragment();
//                        fragmentTransaction.add(R.id.viewer, fragment);
//                        fragmentTransaction.commit();
                        }
                    });
                } else {
                    fab.setVisibility(View.GONE);
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }
}
