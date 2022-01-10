package com.team2.higallery.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.team2.higallery.Configuration;
import com.team2.higallery.R;
import com.team2.higallery.fragments.GridPhotosFragment;
import com.team2.higallery.providers.TrashManager;

import java.util.ArrayList;
import java.util.Collections;

public class TrashActivity extends AppCompatActivity implements GridPhotosFragment.ActivityCallbacks {

    TrashManager trashManager;
    Fragment fragmentBody;
    ArrayList<Integer> selectedIndices = new ArrayList<>();

    Toolbar appbar;
    Button buttonDelete;
    Button buttonRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_trash);

        setupAppBar();

        trashManager = TrashManager.getInstance(this);
        trashManager.autoClean();

        setupBody();
        setupBottomBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((GridPhotosFragment) fragmentBody).sendFromActivityToFragment("trash", "update", -1);
    }

    private void setupAppBar() {
        appbar = (Toolbar) findViewById(R.id.appbar_trash);
        setSupportActionBar(appbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        // add back arrow to appbar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setupBody() {
        fragmentBody = new GridPhotosFragment(trashManager.getAllPaths(), "trash");
        getSupportFragmentManager().beginTransaction().add(R.id.body_trash, fragmentBody).commit();
    }

    private void setupBottomBar() {
        buttonDelete = (Button) findViewById(R.id.delete_trash);
        buttonRestore = (Button) findViewById(R.id.restore_trash);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (selectedIndices.isEmpty()) {
                    finish();
                } else {
                    disableSelectionMode();
                }
                return true;
        }
        return false;
    }

    public void onDelete(View view) {
        if (selectedIndices.isEmpty()) {
            trashManager.deletePermanentlyAll();
        } else {
            Collections.sort(selectedIndices);
            Collections.reverse(selectedIndices);
            for (int index : selectedIndices) {
                trashManager.deletePermanently(index);
            }
        }
        ((GridPhotosFragment) fragmentBody).sendFromActivityToFragment("trash", "update", -1);
        disableSelectionMode();
    }

    public void onRestore(View view) {
        if (selectedIndices.isEmpty()) {
            trashManager.restoreAll();
        } else {
            Collections.sort(selectedIndices);
            Collections.reverse(selectedIndices);
            for (int index : selectedIndices) {
                trashManager.restore(index);
            }
        }
        ((GridPhotosFragment) fragmentBody).sendFromActivityToFragment("trash", "update", -1);
        disableSelectionMode();
    }

    private void enableSelectionMode() {
        appbar.setNavigationIcon(R.drawable.ic_baseline_close);
        buttonDelete.setText(R.string.trash_delete);
        buttonRestore.setText(R.string.trash_restore);
    }

    private void disableSelectionMode() {
        appbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        appbar.setTitle(R.string.trash_title);
        buttonDelete.setText(R.string.trash_delete_all);
        buttonRestore.setText(R.string.trash_restore_all);
        selectedIndices.clear();
        ((GridPhotosFragment) fragmentBody).sendFromActivityToFragment("trash", "deselect_all", -1);
    }

    @Override
    public void sendFromFragmentToActivity(String sender, String header, int value) {
        if (sender.equals("grid_photos")) {
            switch (header) {
                case "select":
                    if (selectedIndices.isEmpty()) {
                        enableSelectionMode();
                    }
                case "deselect":
                    if (header.equals("select")) {
                        if (!selectedIndices.contains(value)) {
                            selectedIndices.add(value);
                        }
                    } else {
                        selectedIndices.remove(Integer.valueOf(value));
                    }
                    int selectedCount = selectedIndices.size();
                    int allCount = trashManager.count();
                    if (selectedCount == 0) {
                        disableSelectionMode();
                    } else {
                        appbar.setTitle(selectedCount + "/" + allCount);
                    }
                    break;
            }
        }
    }
}