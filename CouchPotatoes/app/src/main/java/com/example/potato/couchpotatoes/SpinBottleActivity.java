package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import java.util.Random;

public class SpinBottleActivity extends AppCompatActivity {

    ImageView iv_bottle;
    Button b_go;
    Random r;
    int angle;
    Boolean nice = true;
    ImageView iv_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_bottle);

        r = new Random();

        iv_bottle = findViewById(R.id.iv_bottle);
        iv_spinner = findViewById(R.id.iv_spinner);
        b_go = findViewById(R.id.b_go);

        int nice =getIntent().getIntExtra("key", 0);
        if(nice == 0){
            iv_spinner.setImageResource(R.mipmap.spinner_naughty);
        }
        b_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle = r.nextInt(3600) + 360;

                RotateAnimation rotate_bottle = new RotateAnimation(0, angle,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                rotate_bottle.setFillAfter(true);
                rotate_bottle.setDuration(3600);
                rotate_bottle.setInterpolator(new AccelerateDecelerateInterpolator());
                iv_bottle.startAnimation(rotate_bottle);
            }
        });
    }
}
