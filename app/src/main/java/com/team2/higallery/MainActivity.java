package com.team2.higallery;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.team2.higallery.activities.LoginVaultActivity;
import com.team2.higallery.activities.SettingsActivity;
import com.team2.higallery.activities.SignUpVaultActivity;
import com.team2.higallery.activities.TrashActivity;
import com.team2.higallery.fragments.AlbumFragment;
import com.team2.higallery.fragments.GridPhotosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team2.higallery.models.Account;
import com.team2.higallery.models.FavoriteImages;
import com.team2.higallery.models.TrashManager;
import com.team2.higallery.utils.DataUtils;
import com.team2.higallery.utils.PermissionHelper;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements GridPhotosFragment.ActivityCallbacks {
    Fragment fragment1, fragment2, fragment3;
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment currentFragment;
    private int currentNavID;

    ArrayList<Integer> selectedPhotoIndices = new ArrayList<>();

    Toolbar appbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setConfiguration();
        setContentView(R.layout.activity_main);

        setupAppBar();
        setupBottomBar();

        if (PermissionHelper.checkReadExternalStorage(this)
                && PermissionHelper.checkWriteExternalStorage(this)) {
            setupBody();
        }

        Account.auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Configuration.alreadyChanged()) {
            refreshActivity();
            Configuration.appliedChanges();
        }

        if (currentNavID == R.id.navigation_photos) {
            if (DataUtils.updateAllImagesFromExternalStorage(this)) {
                ((GridPhotosFragment)fragment1).sendFromActivityToFragment("main", "update_all_photos", -1);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Configuration.save(this);

        // Lưu path của các ảnh được yêu thích
        FavoriteImages.save(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (selectedPhotoIndices.isEmpty()) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main_select_mode, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_menu_main:
                openCamera();
                return true;
            case R.id.vault_menu_main:
                openVault();
                return true;
            case R.id.trash_menu_main:
                openTrash();
                return true;
            case R.id.settings_menu_main:
                openSettings();
                return true;
            case R.id.deselect_menu_main:
                onDeselect();
                return true;
            case R.id.select_all_menu_main:
                onSelectAll();
                return true;
            case R.id.delete_selected_menu_main:
                onDeleteSelectedPhoto();
                return true;
            case R.id.vault_selected_trash_menu_main:
                onVaultSelectedPhoto();
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
        appbar = (Toolbar) findViewById(R.id.appbar_main);
        setSupportActionBar(appbar);
    }

    private void setupBody() {
        DataUtils.updateAllImagesFromExternalStorage(this);
        fragment1 = new GridPhotosFragment(DataUtils.allImages);

        fragment2 = new AlbumFragment();

        FavoriteImages.load(this);
        fragment3 = new GridPhotosFragment(FavoriteImages.list);


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

                if (item.getItemId() != R.id.navigation_album) {
                    selectedPhotoIndices.clear();
                    ((GridPhotosFragment)currentFragment).sendFromActivityToFragment("main", "remove", 0);
                    appbar.setTitle(getResources().getString(R.string.main_title));
                    invalidateOptionsMenu();
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
                        ((GridPhotosFragment)currentFragment).sendFromActivityToFragment("main", "update_favorite_images", -1);
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
        FirebaseUser currentUser = Account.auth.getCurrentUser();

        // Check if user is signed in (non-null) and update UI accordingly.
        if (currentUser != null) {
            Intent intent = new Intent(this, LoginVaultActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SignUpVaultActivity.class);
            startActivity(intent);
        }
    }

    public void openTrash() {
        Intent intent = new Intent(this, TrashActivity.class);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.REQUEST_READ_EXTERNAL_STORAGE:
            case PermissionHelper.REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refreshActivity();
                } else {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    private void onDeselect() {
        ((GridPhotosFragment)currentFragment).sendFromActivityToFragment("main", "deselect_all", 0);
        selectedPhotoIndices.clear();
        invalidateOptionsMenu();
        appbar.setTitle(getResources().getString(R.string.main_title));
    }

    private void onSelectAll() {
        ((GridPhotosFragment)currentFragment).sendFromActivityToFragment("main", "select_all", 0);
        selectedPhotoIndices.clear();
        for (int i = 0; i < DataUtils.allImages.size(); i++) {
            selectedPhotoIndices.add(i);
        }
        int allCount = DataUtils.allImages.size();
        appbar.setTitle(allCount + "/" + allCount);
    }

    private void onDeleteSelectedPhoto() {
        TrashManager trashManager = TrashManager.getInstance(this);
        for (int i : selectedPhotoIndices) {
            trashManager.delete(DataUtils.allImages.get(i));
        }
        Collections.sort(selectedPhotoIndices);
        for (int i = selectedPhotoIndices.size() - 1; i >= 0; i--) {
            DataUtils.allImages.remove(selectedPhotoIndices.get(i).intValue());
        }
        selectedPhotoIndices.clear();
        ((GridPhotosFragment)currentFragment).sendFromActivityToFragment("main", "remove", 0);
        appbar.setTitle(getResources().getString(R.string.main_title));
        invalidateOptionsMenu();
    }

    private  void onVaultSelectedPhoto() {

    }

    @Override
    public void sendFromFragmentToActivity(String sender, String header, int value) {
        if (sender.equals("grid_photos")) {
            switch (header) {
                case "select":
                case "deselect":
                    if (selectedPhotoIndices.isEmpty()) {
                        invalidateOptionsMenu();
                    }
                    if (header.equals("select")) {
                        if (!selectedPhotoIndices.contains(value)) {
                            selectedPhotoIndices.add(value);
                        }
                    } else {
                        selectedPhotoIndices.remove(Integer.valueOf(value));
                    }
                    int selectedCount = selectedPhotoIndices.size();
                    int allCount = 0;
                    if (currentNavID == R.id.navigation_photos) {
                        allCount = DataUtils.allImages.size();
                    } else if (currentNavID == R.id.navigation_favorite) {
                        allCount = FavoriteImages.list.size();
                    }
                    if (selectedCount == 0) {
                        appbar.setTitle(getResources().getString(R.string.main_title));
                        invalidateOptionsMenu();
                    } else {
                        appbar.setTitle(selectedCount + "/" + allCount);
                    }
                    break;
                case "should_reload":
                    if (currentNavID == R.id.navigation_favorite) {
                        ((GridPhotosFragment)currentFragment).sendFromActivityToFragment("main", "update_favorite_images", -1);
                    }
            }

        }
    }
}