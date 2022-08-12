package com.lesscode.pinokio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class splash_screen extends AppCompatActivity {
    TextView desc_title; // declaration desc_title
    Handler handler = new Handler(); // create a new Handler class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // initialization desc_title
        desc_title = findViewById(R.id.desc_title);

        // delay 1 second
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_anim);
                desc_title.startAnimation(animation);
                moveInterval();
            }
        }, 1000);
    }

    // move activity
    public void moveInterval() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splash_screen.this, login_screen.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        }, 2000);
    }
}