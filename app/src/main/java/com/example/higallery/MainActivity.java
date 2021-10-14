package com.example.higallery;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.higallery.activities.LoginVaultActivity;
import com.example.higallery.activities.SettingsActivity;
import com.example.higallery.fragments.AlbumFragment;
import com.example.higallery.fragments.AllPhotosFragment;
import com.example.higallery.fragments.FavoriteFragment;
import com.example.higallery.utils.LocaleHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private final Fragment fragment1 = new AllPhotosFragment();
    private final Fragment fragment2 = new AlbumFragment();
    private final Fragment fragment3 = new FavoriteFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment currentFragment;
    private int currentNavID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setConfiguration();
        setContentView(R.layout.activity_main);

        setupAppBar();
        setupBody();
        setupBottomBar();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Configuration.alreadyChanged()) {
            refreshActivity();
            Configuration.appliedChanges();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Configuration.save(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_menu_item_main:
                openCamera();
                return true;
            case R.id.vault_menu_item_main:
                openVault();
                return true;
            case R.id.settings_menu_item_main:
                openSettings();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK) {
                boolean isChanged = data.getBooleanExtra("isChanged", false);
                if (isChanged) {
                    refreshActivity();
                }
            }
        } catch (Exception ignore) {
        }
    }

    // Áp dụng các cài đặt
    private void setConfiguration() {
        Configuration.load(this);
        Configuration.set(this);
    }

    private void setupAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_main);
        setSupportActionBar(toolbar);
    }

    private void setupBody() {
        fm.beginTransaction().add(R.id.body_main, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.body_main, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.body_main, fragment1, "1").commit();
        currentFragment = fragment1;
        currentNavID = R.id.navigation_photos;
    }

    private void setupBottomBar() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottombar_main);
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

    public void openCamera() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        startActivity(intent);
    }

    public void openVault() {
        // Lợi code - UI-login
        Intent intent = new Intent(this, LoginVaultActivity.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void refreshActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}