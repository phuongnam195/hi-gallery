package com.team2.higallery.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.team2.higallery.Configuration;
import com.team2.higallery.R;
import com.team2.higallery.models.TrashManager;

public class TrashActivity extends AppCompatActivity {

    TrashManager trashManager;
    TextView trashCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_trash);

        setupAppBar();

        trashManager = TrashManager.getInstance(this);

        trashCount = (TextView) findViewById(R.id.trash_count);
        trashCount.setText(String.valueOf(trashManager.count()));
    }

    private void setupAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_trash);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    public void onDeleteAll(View view) {
        if (!trashManager.deletePermanentlyAll()) {
            // Lỗi
        }
        trashCount.setText(String.valueOf(trashManager.count()));
    }

    public void onRestoreAll(View view) {
        if (!trashManager.restoreAll()) {
            // Lỗi
        }
        trashCount.setText(String.valueOf(trashManager.count()));
    }
}