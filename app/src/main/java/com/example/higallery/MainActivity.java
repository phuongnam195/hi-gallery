package com.example.higallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends Activity {
    private FrameLayout frameBody;
    private View currentBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.loadSelectedLanguage(this);
        setContentView(R.layout.activity_main);

        handleBottomNavigation();
        frameBody = (FrameLayout) findViewById(R.id.body_main);
        currentBody = getLayoutInflater().inflate(R.layout.frame_all_images, null);
        frameBody.addView(currentBody);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1 && resultCode == RESULT_OK) {
                boolean languageSwitched = data.getBooleanExtra("languageSwitched", false);
                if (languageSwitched) {
                    refreshActivity();
                }
            }
        } catch (Exception ignore) {
        }
    }

    public void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        popup.getMenuInflater()
                .inflate(R.menu.popup_menu_main, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_vault:
                        openVault(view);
                        break;
                    case R.id.menu_item_settings:
                        openSettings(view);
                        break;
                }
                return true;
            }
        });

        popup.show();
    }

    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        startActivity(intent);
    }

    public void openVault(View view) {
        // Lợi code - UI-login
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    private void handleBottomNavigation() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_bar_main);
        navigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                frameBody.removeView(currentBody);
                switch (item.getItemId()) {
                    case R.id.navigation_photos:
                        currentBody = getLayoutInflater().inflate(R.layout.frame_all_images, null);
                        break;
                    case R.id.navigation_album:
                        currentBody = getLayoutInflater().inflate(R.layout.frame_album, null);
                        break;
                    case R.id.navigation_favorite:
                        currentBody = getLayoutInflater().inflate(R.layout.frame_favorite, null);
                        break;
                }
                frameBody.addView(currentBody);
                return false;
            }
        });
    }

    private void refreshActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}