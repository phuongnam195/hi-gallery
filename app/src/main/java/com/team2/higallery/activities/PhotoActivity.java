package com.team2.higallery.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.team2.higallery.Configuration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.team2.higallery.R;

import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {
    private ActionBar appBar;
    private LinearLayout bottomBar;
    private FloatingActionButton favoriteButton;

    int currentIndex;
    ArrayList<String> imagePaths;

    private boolean dummyFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_photo);

        Intent intent = getIntent();
        imagePaths = intent.getStringArrayListExtra("imagePaths");
        currentIndex = intent.getIntExtra("currentIndex", 0);

        setupAppBar();
        setupBottomBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ImageView imageDummy = (ImageView) findViewById(R.id.imageDummy);
        imageDummy.setImageResource(R.drawable.ic_launcher_background);
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
        favoriteButton = (FloatingActionButton) findViewById(R.id.favorite_action_photo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBack();
                return true;
            case R.id.edit_action_photo:
                onEdit();
                return true;
            case R.id.new_album_action_photo:
                onNewAlbum();
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

    public void toggleBarVisibility(View view) {
        if (appBar.isShowing()) {
            appBar.hide();
            bottomBar.setVisibility(View.GONE);

        } else {
            appBar.show();
            bottomBar.setVisibility(View.VISIBLE);
        }
    }

    public void onBack() {
        finish();
    }

    public void onEdit() {
        Toast.makeText(this, "Sửa...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    public void onNewAlbum() {
        Toast.makeText(this, "Thêm vào album mới (tạo album)...", Toast.LENGTH_SHORT).show();
    }

    public void onSecure() {
        Toast.makeText(this, "Thêm vào thư mục bí mật...", Toast.LENGTH_SHORT).show();
    }

    public void onSetAs() {
        Toast.makeText(this, "Đặt làm hình nền, hình khóa...", Toast.LENGTH_SHORT).show();
    }

    public void onDetails() {
        Toast.makeText(this, "Xem metadata...", Toast.LENGTH_SHORT).show();
    }

    public void onShare(View view) {
        Toast.makeText(this, "Chia sẻ...", Toast.LENGTH_SHORT).show();
    }

    public void toggleFavorite(View view) {
        Resources resources = getResources();
        if (dummyFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border);
            favoriteButton.setTooltipText(resources.getString(R.string.photo_favorite));
        } else {
            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite);
            favoriteButton.setTooltipText(resources.getString(R.string.photo_unfavorite));
        }
        dummyFavorite = !dummyFavorite;

        Toast.makeText(this, "Thích/Bỏ thích...", Toast.LENGTH_SHORT).show();
    }

    public void onDelete(View view) {
        Toast.makeText(this, "Xóa vào thùng rác", Toast.LENGTH_SHORT).show();
    }
}