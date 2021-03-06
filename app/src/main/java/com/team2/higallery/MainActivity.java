package com.team2.higallery;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.team2.higallery.activities.LoginVaultActivity;
import com.team2.higallery.activities.SettingsActivity;
import com.team2.higallery.activities.SignUpVaultActivity;
import com.team2.higallery.activities.TrashActivity;
import com.team2.higallery.fragments.GridAlbumsFragment;
import com.team2.higallery.fragments.GridPhotosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team2.higallery.models.Account;
import com.team2.higallery.models.FavoriteImages;
import com.team2.higallery.providers.ImagesProvider;
import com.team2.higallery.providers.TrashManager;
import com.team2.higallery.providers.VaultManager;
import com.team2.higallery.utils.DataUtils;
import com.team2.higallery.utils.FileUtils;
import com.team2.higallery.utils.PermissionHelper;
import com.team2.higallery.utils.gif.AnimatedGifEncoder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements GridPhotosFragment.ActivityCallbacks {
    private final int REQUEST_CODE_SIGN_UP_VAULT = 342;
    private final int REQUEST_CODE_SETTINGS = 354;

    Fragment fragment1, fragment2, fragment3;
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment currentFragment;
    private int currentNavID;

    ArrayList<String> currentImages;
    ArrayList<Integer> selectedIndices = new ArrayList<>();

    Toolbar appbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!PermissionHelper.isGranted(this)) {
            PermissionHelper.request(this);
            return;
        }

        setConfiguration();
        setContentView(R.layout.activity_main);

        setupAppBar();
        setupBottomBar();
        setupBody();

        FavoriteImages.load(this);
        Account.load(this);
        VaultManager.getInstance(this).synchronize();

        File ourPictureFolder = new File(FileUtils.HIGALERRY_FOLDER_PATH);
        if (!ourPictureFolder.exists()) {
            ourPictureFolder.mkdirs();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ArrayList<String> duplicateImagePaths = intent.getStringArrayListExtra("duplicateImagePaths");
        if (duplicateImagePaths == null || duplicateImagePaths.isEmpty()) {
            return;
        }
        for (String path : duplicateImagePaths) {
            TrashManager.getInstance(this).delete(path);
        }
        Toast.makeText(this, R.string.delete_duplicates_done, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!PermissionHelper.isGranted(this)) {
            return;
        }

        if (Configuration.alreadyChanged()) {
            refreshActivity();
            Configuration.appliedChanges();
        }

        if (ImagesProvider.updateAllImagesFromExternalStorage(this)) {
            ((GridPhotosFragment) fragment1).sendFromActivityToFragment("main", "update", -1);
            if (fragment2 != null) {
                ((GridAlbumsFragment) fragment2).sendFromActivityToFragment("main", "update", -1);
            }
        }

        if (currentFragment == fragment1) {
            currentImages = ImagesProvider.allImages;
        } else if (currentFragment == fragment3) {
            currentImages = FavoriteImages.get();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!PermissionHelper.isGranted(this)) {
            return;
        }

        Configuration.save(this);

        // L??u path c???a c??c ???nh ???????c y??u th??ch
        FavoriteImages.save(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (selectedIndices.isEmpty()) {
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
            case R.id.deselect_all_menu_main:
                onDeselectAll();
                return true;
            case R.id.select_all_menu_main:
                onSelectAll();
                return true;
            case R.id.new_album_selected_menu_main:
                onNewAlbumSelectedPhoto();
                return true;
            case R.id.delete_selected_menu_main:
                onDeleteSelectedPhoto();
                return true;
            case R.id.vault_selected_menu_main:
                onVaultSelectedPhoto();
                return true;
            case R.id.generate_gif_selected_menu_main:
                onGenerateGifSelectedPhoto();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTINGS:
                if (resultCode == RESULT_OK) {
                    boolean isChanged = data.getBooleanExtra("isChanged", false);
                    if (isChanged) {
                        refreshActivity();
                    }
                }
                break;
            case REQUEST_CODE_SIGN_UP_VAULT:
                if (resultCode == RESULT_OK) {
                    moveSelectedImagesToVault();
                    Toast.makeText(this, R.string.main_toast_move_to_vault_done, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.main_toast_move_to_vault_failed, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // ??p d???ng c??c c??i ?????t
    private void setConfiguration() {
        Configuration.load(this);
        Configuration.set(this);
    }

    private void setupAppBar() {
        appbar = (Toolbar) findViewById(R.id.appbar_main);
        setSupportActionBar(appbar);
    }

    private void setupBody() {
        fragment1 = new GridPhotosFragment(ImagesProvider.allImages, "all_photos");
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

                if (!selectedIndices.isEmpty()) {
                    selectedIndices.clear();
                    ((GridPhotosFragment) currentFragment).sendFromActivityToFragment("main", "deselect_all", 0);
                    appbar.setTitle(getResources().getString(R.string.main_title));
                    invalidateOptionsMenu();
                }

                currentNavID = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.navigation_photos:
                        fm.beginTransaction().hide(currentFragment).show(fragment1).commit();
                        currentFragment = fragment1;
                        currentImages = ImagesProvider.allImages;
                        return true;

                    case R.id.navigation_album:
                        if (fragment2 == null) {
                            fragment2 = new GridAlbumsFragment();
                            fm.beginTransaction().hide(currentFragment).add(R.id.body_main, fragment2, "2").commit();
                        } else {
                            fm.beginTransaction().hide(currentFragment).show(fragment2).commit();
                            ((GridAlbumsFragment) fragment2).sendFromActivityToFragment("main", "update", -1);
                        }
                        currentFragment = fragment2;
                        currentImages = null;
                        return true;

                    case R.id.navigation_favorite:
                        if (fragment3 == null) {
                            fragment3 = new GridPhotosFragment(FavoriteImages.get(), "favorites");
                            fm.beginTransaction().hide(currentFragment).add(R.id.body_main, fragment3, "3").commit();
                        } else {
                            fm.beginTransaction().hide(currentFragment).show(fragment3).commit();
                            ((GridPhotosFragment) fragment3).sendFromActivityToFragment("main", "update", -1);
                        }
                        currentFragment = fragment3;
                        currentImages = FavoriteImages.get();
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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
    }

    private void refreshActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.REQUEST_CODE:
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

    private void onDeselectAll() {
        ((GridPhotosFragment) currentFragment).sendFromActivityToFragment("main", "deselect_all", 0);
        selectedIndices.clear();
        invalidateOptionsMenu();
        appbar.setTitle(getResources().getString(R.string.main_title));
    }

    private void onSelectAll() {
        ((GridPhotosFragment) currentFragment).sendFromActivityToFragment("main", "select_all", 0);
        selectedIndices.clear();
        for (int i = 0; i < currentImages.size(); i++) {
            selectedIndices.add(i);
        }
        int allCount = currentImages.size();
        appbar.setTitle(allCount + "/" + allCount);
    }

    private void onDeleteSelectedPhoto() {
        TrashManager trashManager = TrashManager.getInstance(this);
        for (int i : selectedIndices) {
            trashManager.delete(currentImages.get(i));
        }
        selectedIndices.sort(Collections.reverseOrder());
        for (Integer index : selectedIndices) {
            String path = currentImages.get(index);
            ImagesProvider.allImages.remove(path);
        }
        selectedIndices.clear();
        ((GridPhotosFragment) currentFragment).sendFromActivityToFragment("main", "remove", -1);
        if (currentFragment == fragment3) {
            ((GridPhotosFragment) fragment1).sendFromActivityToFragment("main", "remove", -1);
        }

        appbar.setTitle(getResources().getString(R.string.main_title));
        invalidateOptionsMenu();

        ImagesProvider.divideAllImagesToAlbums();
        if (fragment2 != null) {
            ((GridAlbumsFragment) fragment2).sendFromActivityToFragment("main", "update", -1);
        }
    }

    private void onVaultSelectedPhoto() {
        if (!Account.isSigned()) {
            Intent intent = new Intent(this, SignUpVaultActivity.class);
            intent.putExtra("finishAfterSignUp", true);
            startActivityForResult(intent, REQUEST_CODE_SIGN_UP_VAULT);
        } else {
            moveSelectedImagesToVault();
            Toast.makeText(this, R.string.main_toast_move_to_vault_done, Toast.LENGTH_SHORT).show();
        }
    }

    private void onGenerateGifSelectedPhoto() {
        if (selectedIndices.size() <= 1) {
            Toast.makeText(this, R.string.main_toast_generate_gif_invalid_number, Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        for (Integer index : selectedIndices) {
            String imagePath = currentImages.get(index);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            bitmaps.add(bitmap);
        }
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.set(bitmaps);
        encoder.encode();
        String fileName = "hi_animated_" + DataUtils.generateRandomString(10) + ".gif";
        String filePath = FileUtils.HIGALERRY_FOLDER_PATH + "/" + fileName;
        boolean ok = encoder.export(filePath);

        if (ok) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
            if (currentFragment == fragment1) {
                ImagesProvider.allImages.add(0, filePath);
            }
            Toast.makeText(this, R.string.main_toast_generate_gif_done, Toast.LENGTH_SHORT).show();

            ImagesProvider.divideAllImagesToAlbums();
            if (fragment2 != null) {
                ((GridAlbumsFragment) fragment2).sendFromActivityToFragment("main", "update", 0);
            }
        } else {
            Toast.makeText(this, R.string.main_toast_generate_gif_failed, Toast.LENGTH_SHORT).show();
        }

        selectedIndices.clear();
        appbar.setTitle(getResources().getString(R.string.main_title));
        invalidateOptionsMenu();
        ((GridPhotosFragment) currentFragment).sendFromActivityToFragment("main", "deselect_all", 0);
    }

    private void onNewAlbumSelectedPhoto() {
        if (currentFragment == fragment3) {
            Toast.makeText(this, "Sorry! Currently not supported.", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_album);

        EditText input = (EditText) dialog.findViewById(R.id.input_dialog_new_album);
        Button btnOK = (Button) dialog.findViewById(R.id.ok_dialog_new_album);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String albumName = input.getText().toString();
                File albumFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);

                ArrayList<String> newFileList = new ArrayList<>();

                for (Integer index : selectedIndices) {
                    String imagePath = ImagesProvider.allImages.get(index);
                    File newFile = FileUtils.moveFile(imagePath, albumFolder);
                    if (newFile != null) {
                        FileUtils.removeImageMedia(MainActivity.this, new File(imagePath));
                        newFileList.add(newFile.toString());
                        ImagesProvider.allImages.set(index, newFile.getPath());
                    }
                }

                if (newFileList.size() > 0) {
                    String[] newFiles = new String[newFileList.size()];
                    newFiles = newFileList.toArray(newFiles);
                    MediaScannerConnection.scanFile(MainActivity.this,
                            newFiles,
                            null, null);
                }

                selectedIndices.clear();
                appbar.setTitle(getResources().getString(R.string.main_title));
                invalidateOptionsMenu();
                ((GridPhotosFragment) currentFragment).sendFromActivityToFragment("main", "deselect_all", 0);

                ImagesProvider.divideAllImagesToAlbums();
                if (fragment2 != null) {
                    ((GridAlbumsFragment) fragment2).sendFromActivityToFragment("main", "update", 0);
                }

                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void moveSelectedImagesToVault() {
        VaultManager vaultManager = VaultManager.getInstance(this);
        for (int i : selectedIndices) {
            vaultManager.moveImageToVault(currentImages.get(i));
        }
        selectedIndices.sort(Collections.reverseOrder());
        for (Integer index : selectedIndices) {
            String path = currentImages.get(index);
            ImagesProvider.allImages.remove(path);
        }
        selectedIndices.clear();
        ((GridPhotosFragment) currentFragment).sendFromActivityToFragment("main", "remove", 0);
        appbar.setTitle(getResources().getString(R.string.main_title));
        invalidateOptionsMenu();

        ImagesProvider.divideAllImagesToAlbums();
        if (fragment2 != null) {
            ((GridAlbumsFragment) fragment2).sendFromActivityToFragment("main", "update", 0);
        }
    }

    @Override
    public void sendFromFragmentToActivity(String sender, String header, int value) {
        if (sender.equals("grid_photos")) {
            switch (header) {
                case "select":
                case "deselect":
                    if (selectedIndices.isEmpty()) {
                        invalidateOptionsMenu();
                    }
                    if (header.equals("select")) {
                        if (!selectedIndices.contains(value)) {
                            selectedIndices.add(value);
                        }
                    } else {
                        selectedIndices.remove(Integer.valueOf(value));
                    }
                    int selectedCount = selectedIndices.size();
                    if (selectedCount == 0) {
                        appbar.setTitle(getResources().getString(R.string.main_title));
                        invalidateOptionsMenu();
                    } else {
                        int allCount = currentImages.size();
                        appbar.setTitle(selectedCount + "/" + allCount);
                    }
                    break;
                case "should_reload":
                    if (currentNavID == R.id.navigation_favorite) {
                        ((GridPhotosFragment) currentFragment).sendFromActivityToFragment("main", "update", -1);
                    }
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (!selectedIndices.isEmpty()) {
            onDeselectAll();
        } else {
            super.onBackPressed();
        }
    }
}