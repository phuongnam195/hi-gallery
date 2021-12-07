package com.team2.higallery.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.team2.higallery.Configuration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.team2.higallery.R;
import com.team2.higallery.adapters.PhotosPagerAdapter;
import com.team2.higallery.models.FavoriteImages;
import com.team2.higallery.models.TrashManager;
import com.team2.higallery.models.VaultManager;
import com.team2.higallery.utils.DataUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {
    private ActionBar appBar;
    private LinearLayout bottomBar;
    private FloatingActionButton favoriteButton;

    int currentIndex;
    ArrayList<String> imagePaths;
    String source;

    ViewPager viewPager;
    PhotosPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_photo);

        Intent intent = getIntent();
        imagePaths = intent.getStringArrayListExtra("imagePaths");
        currentIndex = intent.getIntExtra("currentIndex", 0);
        source = intent.getStringExtra("source");

        setupAppBar();
        setupBottomBar();

        new Thread() {
            @Override
            public void run() {
                setupBody();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pagerAdapter.notifyDataSetChanged();
    }

    @SuppressLint("RestrictedApi")
    private void setupAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_photo);
        setSupportActionBar(toolbar);
        appBar = getSupportActionBar();

        // add back arrow to appbar
        appBar.setDisplayHomeAsUpEnabled(true);
        appBar.setDisplayShowHomeEnabled(true);

        // disable show/hide animation
        appBar.setShowHideAnimationEnabled(false);
    }

    private void setupBottomBar() {
        if (source.equals("trash")) {
            bottomBar = (LinearLayout) findViewById(R.id.bottombar_photo_trash);
        } else {
            bottomBar = (LinearLayout) findViewById(R.id.bottombar_photo);
            favoriteButton = (FloatingActionButton) findViewById(R.id.favorite_action_photo);
        }
        bottomBar.setVisibility(View.VISIBLE);
    }

    private void setupBody() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new PhotosPagerAdapter(this, imagePaths, new PhotosPagerAdapter.ClickListener() {
            @Override
            public void onClick() {
                toggleBarVisibility();
            }
        });
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentIndex);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                if (!source.equals("trash")) {
                    setFavorite(FavoriteImages.check(imagePaths.get(position)));
                }
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        if (!source.equals("trash")) {
            setFavorite(FavoriteImages.check(imagePaths.get(currentIndex)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!source.equals("trash")) {
            getMenuInflater().inflate(R.menu.menu_photo, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.edit_action_photo:
                onEdit();
                return true;
//            case R.id.new_album_action_photo:
//                onNewAlbum();
//                return true;
            case R.id.secure_action_photo:
                onSecure();
                return true;
            case R.id.set_as_action_photo:
                onSetAs();
                return true;
            case R.id.details_action_photo:
                onDetails();
                return true;
            case R.id.delete_permanently_action_photo:
                return true;
            case R.id.restore_action_photo:
                return true;
        }
        return false;
    }

    private void toggleBarVisibility() {
        if (appBar.isShowing()) {
            appBar.hide();
            bottomBar.setVisibility(View.GONE);

        } else {
            appBar.show();
            bottomBar.setVisibility(View.VISIBLE);
        }
    }

    public void onEdit() {
        Bundle myData = new Bundle();
        myData.putInt("currentIndex", currentIndex);
        myData.putStringArrayList("pathList", imagePaths);
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtras(myData);
        startActivityForResult(intent, 1234);
    }

    public void onNewAlbum() {
        Toast.makeText(this, "Thêm vào album mới (tạo album)...", Toast.LENGTH_SHORT).show();
    }

    public void onSecure() {
        VaultManager vaultManager = VaultManager.getInstance(this);
        vaultManager.moveImageToVault(imagePaths.get(currentIndex));
        pagerAdapter.removeItem(currentIndex);
    }

    public void onSetAs() {
        Bundle myData = new Bundle();
        myData.putString("currentImagePath", imagePaths.get(viewPager.getCurrentItem()));
        Intent intent = new Intent(this, SetWallActivity.class);
        intent.putExtras(myData);
        startActivity(intent);
    }

    public void onDetails() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_detail_photo);

        TextView detailName = (TextView) dialog.findViewById(R.id.photo_details_name);
        TextView detailSize = (TextView) dialog.findViewById(R.id.photo_details_size);
        TextView detailPath = (TextView) dialog.findViewById(R.id.photo_details_path);
        TextView detailResolution = (TextView) dialog.findViewById(R.id.photo_details_resolution);
        TextView detailLastModified = (TextView) dialog.findViewById(R.id.photo_details_last_modified);
        Button detailButtonClose = (Button) dialog.findViewById(R.id.photo_details_button_close);

        detailButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        try {
            File file = new File(imagePaths.get(currentIndex));
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            detailName.append(DataUtils.getNamePhoto(imagePaths.get(viewPager.getCurrentItem())));
            detailSize.append(DataUtils.getSizePhoto(attr.size()*1.0));
            detailPath.append(imagePaths.get(viewPager.getCurrentItem()));
            detailResolution.append(DataUtils.getResolutionPhoto(imagePaths.get(viewPager.getCurrentItem()), this));
            detailLastModified.append(DataUtils.convertDateTimeToString(attr.lastModifiedTime().toString(), this));

        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.show();
    }

    public void onShare(View view) {
        // Credit: https://guides.codepath.com/android/Sharing-Content-with-Intents
        // Credit: https://www.geeksforgeeks.org/how-to-share-image-of-your-app-with-another-app-in-android/
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        File imageFile = new File(imagePaths.get(currentIndex));
        Uri uri = FileProvider.getUriForFile(
                this,
                "com.team2.higallery.provider",
                imageFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name));
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.photo_share_title)));
    }

    public void toggleFavorite(View view) {
        final boolean isFavorite = FavoriteImages.toggle(imagePaths.get(currentIndex));
        setFavorite(isFavorite);
    }

    public void onDelete(View view) {
        TrashManager trashManager = TrashManager.getInstance(this);
        trashManager.delete(imagePaths.get(currentIndex));

        if (imagePaths.size() == 1) {
            finish();
        }

        pagerAdapter.removeItem(currentIndex);
    }

    public void onDeletePermanently(View view) {
        TrashManager trashManager = TrashManager.getInstance(this);
        trashManager.deletePermanently(currentIndex);
        if (imagePaths.size() == 1) {
            finish();
        }
        pagerAdapter.removeItem(currentIndex);
    }

    public void onRestore(View view) {
        TrashManager trashManager = TrashManager.getInstance(this);
        trashManager.restore(currentIndex);
        if (imagePaths.size() == 1) {
            finish();
        }
        pagerAdapter.removeItem(currentIndex);
    }

    private void setFavorite(boolean state) {
        if (state) {
            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite);
            favoriteButton.setTooltipText(getResources().getString(R.string.photo_unfavorite));
        } else {
            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border);
            favoriteButton.setTooltipText(getResources().getString(R.string.photo_favorite));
        }
    }
}