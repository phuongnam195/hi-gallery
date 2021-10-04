package com.example.higallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        popup.getMenuInflater()
                .inflate(R.menu.popup_menu_main, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_settings:
                        openSettings(view);
                        break;
                    case R.id.menu_item_about:
                        Toast.makeText(MainActivity.this, "Team 2", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    public void openCamera(View view) {
        Toast.makeText(MainActivity.this, "Chưa code", Toast.LENGTH_SHORT).show();

//        Intent intent = new Intent("android.provider.MediaStore.ACTION_IMAGE_CAPTURE");
//        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private BottomNavigationView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_photos:
                    break;
                case R.id.navigation_album:
                    break;
                case R.id.navigation_favorite:
                    break;
            }
            return false;
        }
    };
}