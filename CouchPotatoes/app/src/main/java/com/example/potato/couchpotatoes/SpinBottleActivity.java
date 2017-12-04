package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import java.util.Random;

public class SpinBottleActivity extends AppCompatActivity {

    ImageView iv_bottle;
    Button b_go;
    Random r;
    int angle;
    int nice;
    ImageView iv_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_bottle);

        r = new Random();

        iv_bottle = findViewById(R.id.iv_bottle);
        iv_spinner = findViewById(R.id.iv_spinner);
        b_go = findViewById(R.id.b_go);

        nice =getIntent().getIntExtra("key", 0);
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
                rotate_bottle.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        String message = currentNumber(360 - (angle % 360), nice);
                        // return to main screen
                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                        intent.putExtra("message", message);
                        intent.putExtra( "chatID", getIntent().getStringExtra("chatID") );
                        intent.putExtra("otherUsers", getIntent().getStringExtra("otherUsers"));
                        finish();
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        currentNumber(360 - (angle % 360), nice);
                    }
                });
                iv_bottle.startAnimation(rotate_bottle);
            }
        });
    }

    private String currentNumber(int degrees, int nice){
        if(nice == 1) {
            switch ((degrees / (360 / 6)) % 6) {
                case 0:
                    return "COUCHPOTATOES SUGGESTION:\n*** SAY GOODBYE WITH A HUG ;) ***";
                case 1:
                    return "COUCHPOTATOES SUGGESTION:\n*** SAY GOODBYE WITH A HANDSHAKE ;) ***";
                case 2:
                    return "COUCHPOTATOES SUGGESTION:\n*** SAY GOODBYE WITH A KISS ON THE CHEEK ;) ***";
                case 3:
                    return "COUCHPOTATOES SUGGESTION:\n*** SAY GOODBYE WITH A PECK ON THE LIPS ;) ***";
                case 4:
                    return "COUCHPOTATOES SUGGESTION:\n*** SAY GOODBYE WITH A WAVE ;) ***";
                case 5:
                    return "COUCHPOTATOES SUGGESTION:\n*** SAY GOODBYE WITH A HIGH FIVE ;) ***";
            }
        } else {
            switch((degrees / (360 / 6)) % 6) {
                case 0:
                    return "COUCHPOTATOES SUGGESTION:\n*** SAY GOODBYE WITH A MAKEOUT SESSION ;) ***";
                case 1:
                    return "COUCHPOTATOES SUGGESTION:\n*** SAY GOODBYE WITH AN ADVANCED HUG ;) ***";
                case 2:
                    return "COUCHPOTATOES SUGGESTION:\n*** SAY GOODBYE WITH SOME SEXUAL ACTIVITIES ;) ***";
                case 3:
                    return "COUCHPOTATOES SUGGESTION:\n*** SAY GOODBYE WITH A FRENCH KISS ;) ***";
                case 4:
                    return "COUCHPOTATOES SUGGESTION:\n*** GO HOME ***";
                case 5:
                    return "COUCHPOTATOES SUGGESTION:\n*** SAY GOODBYE WITH A SLAP ON THE BUTT ***";
            }
        }
        return "";
    }
}
