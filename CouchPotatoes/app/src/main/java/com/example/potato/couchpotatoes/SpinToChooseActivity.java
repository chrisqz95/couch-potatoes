package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import java.util.Random;

// https://www.youtube.com/watch?v=TPKugVdd6E4
public class SpinToChooseActivity  extends AppCompatActivity {

    Button button;
    ImageView ic_spinner;
    ImageView ic_potato;
    Random r;
    int degree = 0, degree_old = 0;
    int food;
    // because there are 37 selectors 9.72 degrees each
    private static final float FACTOR = 30f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_to_choose);

        button = findViewById(R.id.button);
        ic_spinner = findViewById(R.id.ic_spinner);
        food = getIntent().getIntExtra("key", 0);
        if(food == 0){
            ic_spinner.setImageResource(R.mipmap.spinner_activity);
        }
        ic_potato = findViewById(R.id.ic_potato);
        r = new Random();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                degree_old = degree % 360;
                degree = r.nextInt(3600) + 720;
                RotateAnimation rotate = new RotateAnimation(degree_old, degree,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(3600);
                rotate.setFillAfter(true);
                rotate.setInterpolator(new DecelerateInterpolator());
                rotate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        currentNumber(360 - (degree % 360), food);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        currentNumber(360 - (degree % 360), food);
                    }
                });

                ic_spinner.startAnimation(rotate);
            }
        });
    }

    private void currentNumber(int degrees, int food){
        if(food == 1) {
            switch ((degrees / (360 / 6)) % 6) {
                case 0:
                    ic_potato.setImageResource(R.drawable.potato_pizza);
                    break;
                case 1:
                    ic_potato.setImageResource(R.drawable.potato_hamburger);
                    break;
                case 2:
                    ic_potato.setImageResource(R.drawable.potato_italian);
                    break;
                case 3:
                    ic_potato.setImageResource(R.drawable.potato_mexican);
                    break;
                case 4:
                    ic_potato.setImageResource(R.drawable.potato_japanese);
                    break;
                case 5:
                    ic_potato.setImageResource(R.drawable.potato_chinese);
                    break;
            }
        } else {
            switch((degrees/(360/6))%6) {
                case 0:
                    ic_potato.setImageResource(R.drawable.potato_beach);
                    break;
                case 1:
                    ic_potato.setImageResource(R.drawable.potato_movie);
                    break;
                case 2:
                    ic_potato.setImageResource(R.drawable.potato_videogames);
                    break;
                case 3:
                    ic_potato.setImageResource(R.drawable.potato_coffee);
                    break;
                case 4:
                    ic_potato.setImageResource(R.drawable.potato_hike);
                    break;
                case 5:
                    ic_potato.setImageResource(R.drawable.potato_walk);
                    break;
            }
        }
    }
}
