package com.team2.higallery.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.team2.higallery.Configuration;
import com.team2.higallery.R;

import java.io.IOException;

public class SetWallActivity extends AppCompatActivity {
    ActionBar appBar;
    LinearLayout bottomBar;
    ImageView imageWall;
    Bitmap bitmap;
    Dialog waitingDialog;

    WallpaperManager wallpaperManager;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_set_wall);
        imageWall = (ImageView) findViewById(R.id.image_set_wall);

        Intent intent = getIntent();
        imagePath = intent.getStringExtra("currentImagePath");
        wallpaperManager = WallpaperManager.getInstance(this);
        bitmap = BitmapFactory.decodeFile(imagePath, new BitmapFactory.Options());

        setupAppBar();
        setupBottomBar();

        imageWall.setImageBitmap(bitmap);
        imageWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBarVisibility();
            }
        });

        waitingDialog = new Dialog(this);
        waitingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitingDialog.setContentView(R.layout.dialog_wait_set_wall);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    @SuppressLint("RestrictedApi")
    private void setupAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_set_wall);
        setSupportActionBar(toolbar);
        appBar = getSupportActionBar();

        // add back arrow to appbar
        appBar.setDisplayHomeAsUpEnabled(true);
        appBar.setDisplayShowHomeEnabled(true);

        // disable show/hide animation
        appBar.setShowHideAnimationEnabled(false);

        appBar.setTitle(R.string.set_wall_title);
    }

    private void setupBottomBar() {
        bottomBar = (LinearLayout) findViewById(R.id.bottombar_set_wall);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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

    public void onLockScreen(View v) {
        try {
            waitingDialog.show();
            Bitmap bitmap = createImageFitScreen(this.bitmap);
            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    public void onHomeScreen(View v) {
        try {
            waitingDialog.show();
            Bitmap bitmap = createImageFitScreen(this.bitmap);
            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    public void onBothScreens(View v) {
        try {
            waitingDialog.show();
            Bitmap bitmap = createImageFitScreen(this.bitmap);
            wallpaperManager.setBitmap(bitmap, null, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    private Bitmap createImageFitScreen(Bitmap tempBitmap) {
        DisplayMetrics metrics = new DisplayMetrics();
        // lấy chiều dài rộng màn hình
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > height)
            height = realHeight;

        double rate = (float) height / (float) width;

        Bitmap bitmap;
        int newWidth, newHeight;
        if (tempBitmap.getHeight() < tempBitmap.getWidth() * rate) {
            newWidth = (int) ((float) tempBitmap.getHeight() / rate);
            newHeight = tempBitmap.getHeight();
            bitmap = Bitmap.createBitmap(tempBitmap, tempBitmap.getWidth() / 2 - newWidth / 2, 0, newWidth, newHeight);
        } else {
            newHeight = (int) (tempBitmap.getWidth() * rate);
            newWidth = tempBitmap.getWidth();
            bitmap = Bitmap.createBitmap(tempBitmap, 0, tempBitmap.getHeight() / 2 - newHeight / 2, newWidth, newHeight);
        }

        return bitmap;
    }
}


