package com.team2.higallery.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.team2.higallery.models.Account;
import com.team2.higallery.models.FavoriteImages;
import com.team2.higallery.providers.ImagesProvider;
import com.team2.higallery.providers.TrashManager;
import com.team2.higallery.providers.VaultManager;
import com.team2.higallery.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {
    private final int REQUEST_CODE_SIGN_UP_VAULT = 542;

    private ActionBar appBar;
    private LinearLayout bottomBar;
    private FloatingActionButton favoriteButton;

    int currentIndex;
    ArrayList<String> imagePaths;
    String source;

    ViewPager viewPager;
    PhotosPagerAdapter pagerAdapter;

    // Tham  khảo thread trong slide tuần 11
    private boolean isSlideShow = false;
    Handler myHandler = new Handler();
    private final Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager.setCurrentItem(currentIndex, false);
        }
    };
    long time = 0;
    private final Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
            while (isSlideShow) {
                try {
                    time += 100;
                    Thread.sleep(100);
                    if (time == 1500) {
                        time = 0;
                        currentIndex++;
                        if (currentIndex == imagePaths.size())
                            currentIndex = 0;
                        myHandler.post(foregroundRunnable);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_photo);

        Intent intent = getIntent();
        currentIndex = intent.getIntExtra("currentIndex", 0);
        source = intent.getStringExtra("source");
        if (source.equals("all_photos")) {
            imagePaths = new ArrayList<>(ImagesProvider.allImages);
        } else {
            imagePaths = intent.getStringArrayListExtra("imagePaths");
        }

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
        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();
        }
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
                if (isSlideShow) {
                    isSlideShow = false;
                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_UP_VAULT:
                if (resultCode == RESULT_OK) {
                    VaultManager.getInstance(this).moveImageToVault(imagePaths.get(currentIndex));
                    pagerAdapter.removeItem(currentIndex);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
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
            case R.id.slideshow_action_photo:
                onSlideShow();
                return true;
            case R.id.secure_action_photo:
                onSecure();
                return true;
            case R.id.set_as_action_photo:
                onSetAs();
                return true;
            case R.id.details_action_photo:
                onDetails();
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
        if (FileUtils.getExtension(imagePaths.get(currentIndex)).equalsIgnoreCase("GIF")) {
            Toast.makeText(this, R.string.photo_toast_edit_gif, Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle myData = new Bundle();
        myData.putInt("currentIndex", currentIndex);
        myData.putStringArrayList("pathList", imagePaths);
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtras(myData);
        startActivityForResult(intent, 1234);
    }

    public void onSlideShow() {
        isSlideShow = true;
        toggleBarVisibility();
        Thread threadSlideShow = new Thread(backgroundTask);
        threadSlideShow.start();
    }

    public void onSecure() {
        if (!Account.isSigned()) {
            Intent intent = new Intent(this, SignUpVaultActivity.class);
            intent.putExtra("finishAfterSignUp", true);
            startActivityForResult(intent, REQUEST_CODE_SIGN_UP_VAULT);
        } else {
            VaultManager.getInstance(this).moveImageToVault(imagePaths.get(currentIndex));
            pagerAdapter.removeItem(currentIndex);
        }

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
            detailName.append(ImagesProvider.getNamePhoto(imagePaths.get(viewPager.getCurrentItem())));
            detailSize.append(ImagesProvider.getSizePhoto(attr.size()*1.0));
            detailPath.append(imagePaths.get(viewPager.getCurrentItem()));
            detailResolution.append(ImagesProvider.getResolutionPhoto(imagePaths.get(viewPager.getCurrentItem()), this));
            detailLastModified.append(ImagesProvider.convertDateTimeToString(attr.lastModifiedTime().toString(), this));

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