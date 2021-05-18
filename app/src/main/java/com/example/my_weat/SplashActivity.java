package com.example.my_weat;




import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class SplashActivity extends AppCompatActivity {


    // This is the time it will take for the splash screen to be displayed
    private static int SPLASH_TIME_OUT = 3000;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        YoYo.with(Techniques.Bounce)
                .duration(7000) // Time it for logo takes to bounce up and down
                .playOn(findViewById(R.id.logo));



        YoYo.with(Techniques.FadeInUp)
                .duration(5000) // Time it for app name to fade in up
                .playOn(findViewById(R.id.appname));

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
