package com.team2.higallery.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.team2.higallery.Configuration;
import com.team2.higallery.R;
import com.team2.higallery.providers.ImagesProvider;
import com.team2.higallery.utils.DataUtils;

public class SettingsActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    TextView txtSubtitleAutoClean;
    TextView txtSubtitleGridType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_settings);

        setupAppBar();
        handleDarkThemeSwitch();

        txtSubtitleAutoClean = (TextView) findViewById(R.id.subtitle_settings_auto_clean);
        txtSubtitleAutoClean.setText(Configuration.getAutoCleanString());

        txtSubtitleGridType = (TextView) findViewById(R.id.subtitle_settings_grid_type);
        txtSubtitleGridType.setText(Configuration.getGridType());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Configuration.save(this);
    }

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

    public void onAutoClean(View view) {
        PopupMenu popup = new PopupMenu(this, view, Gravity.END, R.attr.actionOverflowMenuStyle, 0);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_auto_clean, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    public void onGridType(View view) {
        PopupMenu popup = new PopupMenu(this, view, Gravity.END, R.attr.actionOverflowMenuStyle, 0);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_grid_type, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    public void onDeleteDuplicates(View view) {
        Dialog waitingDialog = new Dialog(this);
        waitingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitingDialog.setContentView(R.layout.dialog_waiting);
        waitingDialog.show();

        new Thread() {
            @Override
            public void run() {
                int count = ImagesProvider.deleteDuplicateImages(SettingsActivity.this);
                String result = getResources().getString(R.string.settings_delete_duplicates_result) + count;
                SettingsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        waitingDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, result, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    private void onBack() {
        finish();
    }

    public void openAbout(View view) {
        Toast.makeText(this, "Team 2", Toast.LENGTH_SHORT).show();
    }

    private void refreshActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_0_auto_clean:
                Configuration.autoCleanTime = Configuration.AUTO_CLEAN_OFF;
                txtSubtitleAutoClean.setText(item.getTitle());
                return true;
            case R.id.menu_1_auto_clean:
                Configuration.autoCleanTime = Configuration.AUTO_CLEAN_TIME_1;
                txtSubtitleAutoClean.setText(item.getTitle());
                return true;
            case R.id.menu_2_auto_clean:
                Configuration.autoCleanTime = Configuration.AUTO_CLEAN_TIME_2;
                txtSubtitleAutoClean.setText(item.getTitle());
                return true;
            case R.id.menu_3_auto_clean:
                Configuration.autoCleanTime = Configuration.AUTO_CLEAN_TIME_3;
                txtSubtitleAutoClean.setText(item.getTitle());
                return true;
            case R.id.menu_0_grid_type:
                Configuration.gridTypeColumns = Configuration.GRID_TYPE_0;
                txtSubtitleGridType.setText(item.getTitle());
                return true;
            case R.id.menu_1_grid_type:
                Configuration.gridTypeColumns = Configuration.GRID_TYPE_1;
                txtSubtitleGridType.setText(item.getTitle());
                return true;
            case R.id.menu_2_grid_type:
                Configuration.gridTypeColumns = Configuration.GRID_TYPE_2;
                txtSubtitleGridType.setText(item.getTitle());
                return true;
        }

        return false;
    }
}