package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import java.util.Random;

public class SpinToChooseActivity  extends AppCompatActivity {

    private Button button;
    private ImageView ic_spinner;
    private ImageView ic_potato;
    private Random r;
    private int degree = 0, degree_old = 0;
    private int spinner_key;
    private ImageView iv_bottle;

    private String[] activityOptions = {"COUCHPOTATOES SUGGESTS:\n GO TO THE BEACH",
            "COUCHPOTATOES SUGGESTS:\n GO TO A MOVIE",
            "COUCHPOTATOES SUGGESTS: PLAY VIDEOGAMES",
            "COUCHPOTATOES SUGGESTS:\n GO GET COFFEE",
            "COUCHPOTATOES SUGGESTS:\n GO ON A HIKE",
            "COUCHPOTATOES SUGGESTS:\n GO FOR A WALK"
    };
    private String[] foodOptions = {"COUCHPOTATOES SUGGESTS:\n PIZZA FOR DINNER",
            "COUCHPOTATOES SUGGESTS:\n BURGERS FOR DINNER",
            "COUCHPOTATOES SUGGESTS:\n ITALIAN FOOD FOR DINNER",
            "COUCHPOTATOES SUGGESTS:\n MEXICAN FOOD FOR DINNER",
            "COUCHPOTATOES SUGGESTS:\n JAPANESE FOOD FOR DINNER",
            "COUCHPOTATOES SUGGESTS: CHINESE FOOD FOR DINNER"};
    private String [] niceOptions = {"COUCHPOTATOES SUGGESTS:\n SAY GOODBYE WITH A HUG ;) ",
            "COUCHPOTATOES SUGGESTS:\n SAY GOODBYE WITH A HANDSHAKE ;) ",
            "COUCHPOTATOES SUGGESTS:\n SAY GOODBYE WITH A KISS ON THE CHEEK ;) ",
            "COUCHPOTATOES SUGGESTS:\n SAY GOODBYE WITH A PECK ON THE LIPS ;) ",
            "COUCHPOTATOES SUGGESTS:\n SAY GOODBYE WITH A WAVE ;) ",
            "COUCHPOTATOES SUGGESTS:\n SAY GOODBYE WITH A HIGH FIVE ;) " };
    private String [] naughtyOptions = {"COUCHPOTATOES SUGGESTS:\n SAY GOODBYE WITH A MAKEOUT SESSION ;) ",
            "COUCHPOTATOES SUGGESTS:\n SAY GOODBYE WITH AN ADVANCED HUG ;) ",
            "COUCHPOTATOES SUGGESTS:\n SAY GOODBYE WITH SOME SEXUAL ACTIVITIES ;) ",
            "COUCHPOTATOES SUGGESTS:\n SAY GOODBYE WITH A FRENCH KISS ;) ",
            "COUCHPOTATOES SUGGESTS:\n GO HOME ",
            "COUCHPOTATOES SUGGESTS:\n SAY GOODBYE WITH A SLAP ON THE BUTT ;)" };

    private int foodImages [] = { R.drawable.potato_pizza,
            R.drawable.potato_hamburger,
            R.drawable.potato_italian,
            R.drawable.potato_mexican,
            R.drawable.potato_japanese,
            R.drawable.potato_chinese
    };

    private int activityImages [] = { R.drawable.potato_pizza,
            R.drawable.potato_movie,
            R.drawable.potato_videogames,
            R.drawable.potato_coffee,
            R.drawable.potato_hike,
            R.drawable.potato_walk
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set correct view for chosen spinner
        spinner_key = getIntent().getIntExtra("key", 0);
        if(spinner_key == 0 || (spinner_key == 1)){
            setContentView(R.layout.activity_spin_to_choose);
            button = findViewById(R.id.button);
            ic_spinner = findViewById(R.id.ic_spinner);
            ic_potato = findViewById(R.id.ic_potato);

            if(spinner_key == 0) {
                ic_spinner.setImageResource(R.mipmap.spinner_food);
            } else{
                ic_spinner.setImageResource(R.mipmap.spinner_activity);
            }
        } else {
            setContentView(R.layout.activity_spin_bottle);
            iv_bottle = findViewById(R.id.iv_bottle);
            button = findViewById(R.id.button);
            ic_spinner = findViewById(R.id.ic_spinner);
            if(spinner_key == 2){
                ic_spinner.setImageResource(R.mipmap.spinner_nice);
            } else{
                ic_spinner.setImageResource(R.mipmap.spinner_naughty);
            }
        }

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
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        String message = currentNumber(360 - (degree % 360), spinner_key);

                        // return to main screen
                        final String message2 = message;
                        // return to main screen
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                intent.putExtra("message", message2);
                                intent.putExtra( "chatID", getIntent().getStringExtra("chatID") );
                                intent.putExtra("otherUsers", getIntent().getStringExtra("otherUsers"));
                                finish();
                                startActivity(intent);
                            }
                        }, 1500);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                // Start appropriate spinner
                if(spinner_key == 1 || spinner_key == 0){
                    ic_spinner.startAnimation(rotate);
                }
                else {
                    iv_bottle.startAnimation(rotate);
                }

            }
        });
    }

    private String currentNumber(int degrees, int spinner_key) {
        int images[] = foodImages;
        String options[] = foodOptions;
        int chosen_image;
        String chosen_option;

        // grab correct spinner info
        if(spinner_key == 1){
            options = activityOptions;
            images = activityImages;
        } else if(spinner_key == 2){
            options = niceOptions;
        } else if(spinner_key == 3){
            options = naughtyOptions;
        }

        switch ((degrees / (360 / 6)) % 6) {
            case 0:
                chosen_image = images[0];
                chosen_option = options[0];
                break;
            case 1:
                chosen_image = images[1];
                chosen_option =  options[1];
                break;
            case 2:
                chosen_image = images[2];
                chosen_option = options[2];
                break;
            case 3:
                chosen_image = images[3];
                chosen_option = options[3];
                break;
            case 4:
                chosen_image = images[4];
                chosen_option = options[4];
                break;
            case 5:
                chosen_image = images[5];
                chosen_option = options[5];
                break;
            default:
                chosen_image = images[0];
                chosen_option = "";
                break;
        }

        // update image if needed
        if((spinner_key == 0) || (spinner_key == 1)){
            System.out.println(chosen_image);
            ic_potato.setImageResource(chosen_image);
        }

        return chosen_option;
    }
}
