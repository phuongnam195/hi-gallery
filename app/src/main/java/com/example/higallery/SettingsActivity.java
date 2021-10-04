package com.example.higallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import androidx.annotation.Nullable;

public class SettingsActivity extends Activity {
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        Toolbar appbar = (Toolbar) findViewById(R.id.appbar_settings);
//        appbar.setNavigationIcon(R.attr.actionModeCloseDrawable);
//        appbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
    }

    public void goBack(View view) {
        finish();
    }
}
