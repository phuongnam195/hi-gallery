package com.example.higallery.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.example.higallery.Configuration;
import com.example.higallery.utils.LocaleHelper;
import com.example.higallery.R;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_settings);

        setupAppBar();
        handleDarkThemeSwitch();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Configuration.save(this);
    }

    @SuppressLint("RestrictedApi")
    private void setupAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_settings);
        setSupportActionBar(toolbar);
        ActionBar appBar = getSupportActionBar();
        if (appBar == null) {
            return;
        }

        // add back arrow to appbar
        appBar.setDisplayHomeAsUpEnabled(true);
        appBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBack();
            return true;
        }
        return false;
    }

    private void handleDarkThemeSwitch() {
        SwitchCompat switchDarkTheme = (SwitchCompat) findViewById(R.id.switch_setting_darktheme);
        switchDarkTheme.setChecked(Configuration.isDarkTheme);
        switchDarkTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration.changeTheme();
                refreshActivity();
            }
        });
    }

    public void switchLanguage(View view) {
        Configuration.changeLanguage();
        refreshActivity();
    }

    private void onBack() {
        finish();
    }

    public void openAbout(View view) {
        Toast.makeText(this, "Team 2", Toast.LENGTH_SHORT).show();
    }

    public void function1(View view) {
        Toast.makeText(this, "Chức năng thứ nhất", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, PhotoActivity.class);
        startActivity(intent);
    }

    public void function3(View view) {
        Toast.makeText(this, "Chức năng thứ ba", Toast.LENGTH_LONG).show();
    }

    private void refreshActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}