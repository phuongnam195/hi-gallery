package com.example.higallery.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.example.higallery.R;

public class PhotoDetailActivity extends Activity {
    private boolean isShowBar = true;
    private Toolbar appbar;
    private Toolbar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        appbar = (Toolbar) findViewById(R.id.appbar_photo_detail);
        bottomBar = (Toolbar) findViewById(R.id.bottom_bar_photo_detail);
    }

    public void toggleBarVisibility(View view) {
        Log.d("df", "asd");
        if (isShowBar) {
            appbar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);

        } else {
            appbar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
        }
        isShowBar = !isShowBar;
    }
}