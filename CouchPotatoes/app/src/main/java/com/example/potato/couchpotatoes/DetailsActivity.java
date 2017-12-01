package com.example.potato.couchpotatoes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_grid_details);

        String title = getIntent().getStringExtra("title");
        //Bitmap bitmap = getIntent().getParcelableExtra("image");

        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        //byte[] byteArray = getIntent().getByteArrayExtra("image");
        //Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //imageView.setImageBitmap(bitmap);
        String uri = getIntent().getStringExtra("uri");
        Picasso.with(getApplicationContext()).load(uri).into(imageView);
    }
}
