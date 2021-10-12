package com.example.higallery;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.higallery.activities.LoginVaultActivity;
import com.example.higallery.activities.SettingsActivity;
import com.example.higallery.fragments.AlbumFragment;
import com.example.higallery.fragments.AllPhotosFragment;
import com.example.higallery.fragments.FavoriteFragment;
import com.example.higallery.utils.LocaleHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends FragmentActivity {
    private final Fragment fragment1 = new AllPhotosFragment();
    private final Fragment fragment2 = new AlbumFragment();
    private final Fragment fragment3 = new FavoriteFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment currentFragment;
    private int currentNavID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.loadSelectedLanguage(this);
        setContentView(R.layout.activity_main);

        fm.beginTransaction().add(R.id.body_main, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.body_main, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.body_main, fragment1, "1").commit();
        currentFragment = fragment1;
        currentNavID = R.id.navigation_photos;
        handleBottomNavigation();
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
        Intent intent = new Intent(this, LoginVaultActivity.class);
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
                if (item.getItemId() == currentNavID) {
                    return false;
                }
                currentNavID = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.navigation_photos:
                        fm.beginTransaction().hide(currentFragment).show(fragment1).commit();
                        currentFragment = fragment1;
                        return true;

                    case R.id.navigation_album:
                        fm.beginTransaction().hide(currentFragment).show(fragment2).commit();
                        currentFragment = fragment2;
                        return true;

                    case R.id.navigation_favorite:
                        fm.beginTransaction().hide(currentFragment).show(fragment3).commit();
                        currentFragment = fragment3;
                        return true;
                }
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