package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PictureGridActivity extends AppCompatActivity  {
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_grid);

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.fragment_picture_grid_item, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);

                /*
                // Convert image to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                item.getImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                */

//                //Create intent
//                Intent intent = new Intent(PictureGridActivity.this, DetailsActivity.class);
//                intent.putExtra("title", item.getTitle());
//                //intent.putExtra("image", byteArray);
//                intent.putExtra("uri", item.getUri());
//
//                //Start details activity
//                startActivity(intent);
                Intent intent = new Intent(PictureGridActivity.this, PictureGridTabViewActivity.class);
                intent.putExtra("itemCount", getStringData().size());
                intent.putExtra("urlList", getStringData());
                intent.putExtra("startingItem", position);
                startActivity(intent);
            }
        });

        Picasso picasso = new Picasso.Builder(getApplicationContext()).memoryCache(new LruCache(2400000)).build();
        picasso.setIndicatorsEnabled(true);
        Picasso.setSingletonInstance(picasso);

    }

    /**
     * Prepare some dummy data for gridview
     */
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        JSONArray jsArray = null;
        try {
             jsArray = new JSONArray(PreferenceManager.getDefaultSharedPreferences(this).getString("PhotoList",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsArray != null) {
            for (int x = 0; x < jsArray.length(); x++){
                try {
                    imageItems.add(new ImageItem(jsArray.getString(x), "potato"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return imageItems;
    }

    private ArrayList<String> getStringData(){
        final ArrayList<String> urlList = new ArrayList<>();
        JSONArray jsArray = null;
        try {
            jsArray = new JSONArray(PreferenceManager.getDefaultSharedPreferences(this).getString("PhotoList",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsArray != null) {
            for (int x = 0; x < jsArray.length(); x++){
                try {
                    urlList.add(jsArray.getString(x));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return urlList;
    }



    private void loadData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        final DBHelper dbHelper = new DBHelper();
        dbHelper.fetchCurrentUser();

        final DatabaseReference ref = dbHelper.getDb().getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.child("User_Photo").child(dbHelper.getUser().getUid()).getChildren()) {
                    try {
                        imageItems.add(new ImageItem(snapshot.child("Photo").child(dataSnapshot.getKey()).child("uri").getValue().toString(), "potato"));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                gridView = (GridView) findViewById(R.id.gridView);
                gridAdapter = new GridViewAdapter(PictureGridActivity.this, R.layout.fragment_picture_grid_item, imageItems);
                gridView.setAdapter(gridAdapter);

                gridView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        ImageItem item = (ImageItem) parent.getItemAtPosition(position);

                        //Create intent
                        Intent intent = new Intent(PictureGridActivity.this, DetailsActivity.class);
                        intent.putExtra("title", item.getTitle());
                        //intent.putExtra("image", byteArray);
                        intent.putExtra("uri", item.getUri());

                        //Start details activity
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }
}