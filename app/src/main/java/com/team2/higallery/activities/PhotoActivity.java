package com.team2.higallery.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.internal.InternalTokenProvider;
import com.team2.higallery.Configuration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.team2.higallery.R;
import com.team2.higallery.adapters.PhotosPagerAdapter;
import com.team2.higallery.models.FavoriteImages;
import com.team2.higallery.models.TrashManager;
import com.team2.higallery.utils.DataUtils;
import com.team2.higallery.utils.EncryptAndDecryptImage;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {
    private ActionBar appBar;
    private LinearLayout bottomBar;
    private FloatingActionButton favoriteButton;

    int currentIndex;
    ArrayList<String> imagePaths;

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

        setupAppBar();
        setupBottomBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                setFavorite(FavoriteImages.check(imagePaths.get(position)));
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        setFavorite(FavoriteImages.check(imagePaths.get(currentIndex)));
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
                finish();
                return true;
            case R.id.edit_action_photo:
                onEdit();
                return true;
            case R.id.new_album_action_photo:
                onNewAlbum();
                return true;
            case R.id.secure_action_photo:
                try {
                    onSecure();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public void onSecure() throws IOException {
        if(false){
            //Not account
            Intent intent = new Intent(this, SignUpVaultActivity.class);
            startActivity(intent);

            return;
        }

        String imagePath = imagePaths.get(viewPager.getCurrentItem())   ;
        Toast.makeText(this, "Lấy đường dẫn" + imagePath, Toast.LENGTH_SHORT).show();

        Bitmap bitmap = EncryptAndDecryptImage.getBitmapFromImagePath(imagePath);
        Toast.makeText(this, "Tạo bitmap của ảnh từ đường dẫn", Toast.LENGTH_SHORT).show();

        byte[] bytes = EncryptAndDecryptImage.encryptImage(bitmap);
        Toast.makeText(this, "Mã hóa file từ bitmap", Toast.LENGTH_SHORT).show();

        Context context = getApplicationContext();
        String fileName = DataUtils.getNamePhoto(imagePaths.get(viewPager.getCurrentItem()));
        EncryptAndDecryptImage.saveFile(context, bytes, fileName);
        Toast.makeText(this, "Lưu file vào bộ nhớ app", Toast.LENGTH_SHORT).show();

        Toast.makeText(this, "Xoá ảnh và hoàn thành", Toast.LENGTH_SHORT).show();
    }

    public void onSetAs() {
        Bundle myData = new Bundle();
        myData.putString("currentImagePath", imagePaths.get(viewPager.getCurrentItem()));
        Intent intent = new Intent(this, ConstrainLayout.class);
        intent.putExtras(myData);
        startActivity(intent);
        Toast.makeText(this, "Đặt làm hình nền, hình khóa...", Toast.LENGTH_SHORT).show();
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
            Path path = Paths.get(URI.create("file://"+imagePaths.get(viewPager.getCurrentItem())));
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
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
        Toast.makeText(this, "Chia sẻ...", Toast.LENGTH_SHORT).show();
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