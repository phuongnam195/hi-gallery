package com.team2.higallery.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.team2.higallery.MainActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent comingIntent = getIntent();
        String action = comingIntent.getAction();
        String type = comingIntent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String url = comingIntent.getStringExtra(Intent.EXTRA_TEXT);
                Intent intent = new Intent(SplashActivity.this, GetInstaActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
                finish();
            }
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }
}