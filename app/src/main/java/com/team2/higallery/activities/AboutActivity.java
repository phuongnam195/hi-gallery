package com.team2.higallery.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.team2.higallery.Configuration;
import com.team2.higallery.R;

public class AboutActivity extends AppCompatActivity {
    private ActionBar appbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_about);
        setupAppBar();
    }

    private void setupAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_about);
        setSupportActionBar(toolbar);
        appbar = getSupportActionBar();

        // add back arrow to appbar
        appbar.setDisplayHomeAsUpEnabled(true);
        appbar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}
