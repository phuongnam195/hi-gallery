package com.team2.higallery.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.team2.higallery.Configuration;
import com.team2.higallery.R;
import com.team2.higallery.fragments.GridPhotosFragment;
import com.team2.higallery.models.Album;
import com.team2.higallery.models.TrashManager;
import com.team2.higallery.utils.DataUtils;

import java.util.ArrayList;
import java.util.Collections;

public class AlbumActivity extends AppCompatActivity implements GridPhotosFragment.ActivityCallbacks {
    int albumIndex;
    Album album;
    String albumName;
    ArrayList<String> imagePaths;
    ArrayList<Integer> selectedPhotoIndices = new ArrayList<>();;
    boolean needUpdate = false;

    ActionBar appbar;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_album);

        Intent intent = getIntent();
        albumIndex = intent.getIntExtra("albumIndex", -1);
        album = DataUtils.allAlbums.get(albumIndex);
        albumName = album.getName();
        imagePaths = album.getImages();

        setupAppBar();
        setupBody();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needUpdate) {
            ((GridPhotosFragment)fragment).sendFromActivityToFragment("album", "update_album_images", albumIndex);
        } else {
            needUpdate = true;
        }
    }

    private void setupAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_album);
        setSupportActionBar(toolbar);
       appbar = getSupportActionBar();

        // add back arrow to appbar
        appbar.setDisplayHomeAsUpEnabled(true);
        appbar.setDisplayShowHomeEnabled(true);

        appbar.setTitle(albumName);
    }

    private void setupBody() {
        fragment = new GridPhotosFragment(imagePaths, "album");
        getSupportFragmentManager().beginTransaction().add(R.id.body_album, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!selectedPhotoIndices.isEmpty()) {
            getMenuInflater().inflate(R.menu.menu_main_select_mode, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.deselect_all_menu_main:
                onDeselect();
                return true;
            case R.id.select_all_menu_main:
                onSelectAll();
                return true;
            case R.id.delete_selected_menu_main:
                onDeleteSelectedPhoto();
                return true;
            case R.id.vault_selected_menu_main:
                onVaultSelectedPhoto();
                return true;
        }
        return false;
    }

    private void onDeselect() {
        ((GridPhotosFragment)fragment).sendFromActivityToFragment("album", "deselect_all", 0);
        selectedPhotoIndices.clear();
        invalidateOptionsMenu();
        appbar.setTitle(albumName);
        invalidateOptionsMenu();
    }

    private void onSelectAll() {
        ((GridPhotosFragment)fragment).sendFromActivityToFragment("album", "select_all", 0);
        selectedPhotoIndices.clear();
        for (int i = 0; i < imagePaths.size(); i++) {
            selectedPhotoIndices.add(i);
        }
        int allCount = imagePaths.size();
        appbar.setTitle(allCount + "/" + allCount);
    }

    private void onDeleteSelectedPhoto() {
        TrashManager trashManager = TrashManager.getInstance(this);
        for (int i : selectedPhotoIndices) {
            trashManager.delete(imagePaths.get(i));
        }
        Collections.sort(selectedPhotoIndices);
        for (int i = selectedPhotoIndices.size() - 1; i >= 0; i--) {
            String imagePath = imagePaths.get(selectedPhotoIndices.get(i));
            imagePaths.remove(selectedPhotoIndices.get(i).intValue());
            DataUtils.allImages.remove(imagePath);
        }
        album.setImages(imagePaths);
        DataUtils.allAlbums.set(albumIndex, album);

        selectedPhotoIndices.clear();
        ((GridPhotosFragment)fragment).sendFromActivityToFragment("album", "remove", 0);
        appbar.setTitle(albumName);
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
                    if (selectedCount == 0) {
                        appbar.setTitle(albumName);
                        invalidateOptionsMenu();
                    } else {
                        int allCount = imagePaths.size();
                        appbar.setTitle(selectedCount + "/" + allCount);
                    }
                    break;
            }
        }
    }
}