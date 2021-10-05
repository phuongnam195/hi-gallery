package com.example.higallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;

import java.util.Locale;

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

    public void switchLanguage(View view) {
        String currentLanguage = LocaleHelper.getLocale(getBaseContext()).getLanguage();
        if (currentLanguage.equals(new Locale("en").getLanguage())) {
            LocaleHelper.setLocale(this, "vi");
        }

        if (currentLanguage.equals(new Locale("vi").getLanguage())) {
            LocaleHelper.setLocale(this, "en");
        }

        Intent intent = getIntent();
        intent.putExtra("languageSwitched", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void openAbout(View view) {
        Toast.makeText(this, "Team 2", Toast.LENGTH_SHORT).show();
    }
}
