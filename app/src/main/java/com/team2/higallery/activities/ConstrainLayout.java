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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.team2.higallery.Configuration;
import com.team2.higallery.R;

import java.io.IOException;

public class ConstrainLayout extends AppCompatActivity {
    private ActionBar appBar;
    private LinearLayout bottomBar;
    private ImageView imageWall;
    private Button lockScreen;
    private Button homeScreen;
    private Button allScreen;


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
        // chuyển ảnh sang bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap tempBitMap = BitmapFactory.decodeFile(imagePath,bmOptions);

        setupAppBar();
        setupBottomBar();

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait_set_wall);

        imageWall.setImageBitmap(tempBitMap);

        homeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.show();
                    Bitmap bitmap = createImageFitScreen(tempBitMap);
                    wallpaperManager.setBitmap( bitmap,null,true,WallpaperManager.FLAG_SYSTEM);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                dialog.cancel();
                finish();
            }

        });
        lockScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.show();
                    Bitmap bitmap = createImageFitScreen(tempBitMap);
                    wallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
        allScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.show();
                    Bitmap bitmap = createImageFitScreen(tempBitMap);
                    wallpaperManager.setBitmap(bitmap,null,true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
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
        bottomBar = (LinearLayout) findViewById(R.id.bottombar_photo);
        lockScreen = (Button) findViewById(R.id.set_wall_button_lock_screen);
        homeScreen = (Button) findViewById(R.id.set_wall_button_home_screen);
        allScreen = (Button) findViewById(R.id.set_wall_button_all_screen);
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

    private Bitmap createImageFitScreen(Bitmap tempBitmap) {
        DisplayMetrics metrics = new DisplayMetrics();
        // lấy chiều dài rộng màn hình
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > height)
            height =  realHeight;

        double rate = (float) height / (float) width;

        Bitmap bitmap;
        int newWidth, newHeight;
        if (tempBitmap.getHeight() < tempBitmap.getWidth() * rate) {
            newWidth = (int) ( (float) tempBitmap.getHeight() / rate);
            newHeight = tempBitmap.getHeight();
            bitmap = Bitmap.createBitmap(tempBitmap, tempBitmap.getWidth()/2 - newWidth/2, 0 , newWidth , newHeight);
        }
        else {
            newHeight = (int) (tempBitmap.getWidth() * rate);
            newWidth = tempBitmap.getWidth();
            bitmap = Bitmap.createBitmap(tempBitmap, 0,tempBitmap.getHeight()/2 - newHeight/2, newWidth , newHeight);
        }

        return bitmap;
    }
}


