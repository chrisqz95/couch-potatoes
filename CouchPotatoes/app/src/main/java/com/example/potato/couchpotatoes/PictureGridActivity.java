package com.example.potato.couchpotatoes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class PictureGridActivity extends AppCompatActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<String> urlList;
    private ArrayList<String> hashList;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_picture_grid_loading);
        Bundle extras = getIntent().getExtras();
        loadData(extras.getString("uid"), extras.getBoolean("isCurrentUser"), extras.getBoolean( "changeProfilePic" ) );
    }

    private void loadData(final String uid, final boolean isCurrentUser, final boolean changeProfilePic ) {
        final DBHelper dbHelper = new DBHelper();
        dbHelper.fetchCurrentUser();

        urlList = new ArrayList<>();
        hashList = new ArrayList<>();

        final DatabaseReference ref = dbHelper.getDb().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                clearPicList();

                updatePicList( snapshot, uid );

                setContentView(R.layout.activity_picture_grid);
                gridView = findViewById(R.id.gridView);
                gridAdapter = new GridViewAdapter(PictureGridActivity.this, R.layout.fragment_picture_grid_item, urlList);
                gridView.setAdapter(gridAdapter);

                ConstraintLayout fab = findViewById(R.id.btnUploadImage);

                // If user wants to change profile pic, set appropriate listener.
                // Else, set tab view listener.
                if ( isCurrentUser && changeProfilePic ) {
                    fab.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Select new profile picture", Toast.LENGTH_LONG).show();
                    addChangeProfilePicListener( dbHelper, uid );
                }else {
                    addBeginTabViewListener();
                }


                if (isCurrentUser) {
                    addImageDeleteListener( dbHelper, uid );

                    reloadImages( fab, ref, uid );
                } else {
                    fab.setVisibility(View.GONE);
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ref.removeEventListener(this);
            }
        });
    }

    private void reloadImages( final ConstraintLayout fab, final DatabaseReference ref, final String uid ) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        clearPicList();

                        updatePicList( snapshot, uid );

                        gridAdapter.notifyDataSetChanged();
                        gridView.invalidateViews();
                        ref.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        ref.removeEventListener(this);
                    }
                });
            }
        });
    }

    private void addImageDeleteListener( final DBHelper dbHelper, final String uid ) {
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(PictureGridActivity.this)
                        .setTitle("Are you sure you want to delete this image?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
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
                                                        //loadData(uid, isCurrentUser);
                                                        urlList.remove(position);
                                                        hashList.remove(position);
                                                        gridAdapter.notifyDataSetChanged();
                                                        gridView.invalidateViews();
                                                        Toast.makeText(getApplicationContext(), "Photo deleted successfully", Toast.LENGTH_LONG).show();
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

    private void addChangeProfilePicListener( final DBHelper dbHelper, final String uid ) {
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;

                new AlertDialog.Builder(PictureGridActivity.this)
                        .setTitle("Use as profile picture?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                StorageReference storageRef = dbHelper.getStorage().getReference("Photo/" + uid + "/" + hashList.get(pos));
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUrl) {
                                        dbHelper.getDb().getReference(dbHelper.getUserPath()).child(uid).child("profile_pic").setValue(downloadUrl.toString());
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private void addBeginTabViewListener() {
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

    private void clearPicList() {
        urlList.clear();
        hashList.clear();
    }

    private void updatePicList( final DataSnapshot snapshot, final String uid ) {
        for (DataSnapshot dataSnapshot : snapshot.child("User_Photo").child(uid).getChildren()) {
            try {
                urlList.add(snapshot.child("Photo").child(dataSnapshot.getKey()).child("uri").getValue().toString());
                hashList.add(dataSnapshot.getKey().toString());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
