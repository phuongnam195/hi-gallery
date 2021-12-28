package com.team2.higallery.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.team2.higallery.MainActivity;
import com.team2.higallery.R;
import com.team2.higallery.adapters.InstaListViewAdapter;
import com.team2.higallery.providers.InstaAPI;
import com.team2.higallery.models.InstaImageData;
import com.team2.higallery.models.InstaSize;

import java.util.ArrayList;

public class GetInstaActivity extends AppCompatActivity {
    Button btnAllSmall;
    Button btnAllMedium;
    Button btnAllLarge;

    ArrayList<InstaImageData> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_insta);

        setupAppBar();
        setupBody();
    }

    @SuppressLint("RestrictedApi")
    private void setupAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_get_insta);
        setSupportActionBar(toolbar);
        ActionBar appBar = getSupportActionBar();

        // add back arrow to appbar
        appBar.setDisplayHomeAsUpEnabled(true);
        appBar.setDisplayShowHomeEnabled(true);
    }

    private void setupBody() {
        Intent intent = getIntent();
        String rawUrl = intent.getStringExtra("url");

        ListView listView = (ListView) findViewById(R.id.body_get_insta);
        btnAllSmall = (Button) findViewById(R.id.get_all_small_get_insta);
        btnAllMedium = (Button) findViewById(R.id.get_all_medium_get_insta);
        btnAllLarge = (Button) findViewById(R.id.get_all_large_get_insta);

        new Thread() {
            @Override
            public void run() {
                String json = getJSON(rawUrl);
                imageList = getImageUrls(json);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (imageList.size() == 0) {
                            TextView tvEmpty = (TextView) findViewById(R.id.empty_get_insta);
                            tvEmpty.setVisibility(View.VISIBLE);
                            return;
                        }

                        InstaListViewAdapter adapter = new InstaListViewAdapter(GetInstaActivity.this, R.layout.insta_card, imageList);
                        listView.setAdapter(adapter);
                        if (imageList.size() > 1) {
                            LinearLayout getAllLayout = (LinearLayout) findViewById(R.id.get_all_get_insta);
                            getAllLayout.setVisibility(View.VISIBLE);
                        }
                        btnAllSmall.setEnabled(true);
                        btnAllMedium.setEnabled(true);
                        btnAllLarge.setEnabled(true);
                    }
                });
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_get_insta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.home_get_insta:
                startActivity(new Intent(GetInstaActivity.this, MainActivity.class));
                finish();
                return true;
        }
        return false;
    }

    public void onDownloadAllSmall(View view) {
        btnAllSmall.setEnabled(false);
        downloadAll(InstaSize.SMALL, btnAllSmall);
    }

    public void onDownloadAllMedium(View view) {
        btnAllMedium.setEnabled(false);
        downloadAll(InstaSize.MEDIUM, btnAllMedium);
    }

    public void onDownloadAllLarge(View view) {
        btnAllLarge.setEnabled(false);
        downloadAll(InstaSize.LARGE, btnAllLarge);
    }

    private void downloadAll(InstaSize size, Button button) {
        Toast.makeText(GetInstaActivity.this, R.string.get_insta_toast_downloading, Toast.LENGTH_LONG).show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                int countSuccess = 0;
                for (int i = 0; i < imageList.size(); i++) {
                    if (imageList.get(i).download(size, GetInstaActivity.this) != null) {
                        countSuccess++;
                    }
                }
                String result = getResources().getString(R.string.get_insta_toast_saved) + " " + countSuccess + "/" + imageList.size();
                int finalCountSuccess = countSuccess;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GetInstaActivity.this, result, Toast.LENGTH_SHORT).show();
                        if (finalCountSuccess != imageList.size()) {
                            button.setEnabled(true);
                        }
                    }
                });
            }
        }.start();
    }

    String getJSON(String rawUrl) {
//        TEST MODE: JSON sample
//        jsonUrl = "https://firebasestorage.googleapis.com/v0/b/higallery-vault.appspot.com/o/insta.json?alt=media&token=101848bb-0e79-47db-b695-bc2d364d35a2";


        int prePostIDIdx = rawUrl.indexOf("/p/");
        int sufPostIDIdx = rawUrl.indexOf('/', prePostIDIdx + "/p/".length() + 1);
        String postId = rawUrl.substring(prePostIDIdx + "/p/".length(), sufPostIDIdx);
        Log.d("JSON URL", rawUrl);
        Log.d("POST ID", postId);

        InstaAPI instaAPI = InstaAPI.getInstance();
        for (int i = 0; i < instaAPI.size(); i++) {
            String json = instaAPI.fetchJSON(postId, i);
            if (json != null) {
                return json;
            }
        }

        return null;
    }

    ArrayList<InstaImageData> getImageUrls(String json) {
        ArrayList<InstaImageData> imageList = new ArrayList<>();

        if (json == null) {
            return imageList;
        }

        int nodeIdx = json.indexOf(InstaAPI.TYPENAME_GRAPH_IMAGE);
        if (nodeIdx == -1) {
            return imageList;
        }
        while (true) {
            int endNodeIdx = json.indexOf(InstaAPI.TYPENAME_GRAPH_IMAGE, nodeIdx + InstaAPI.TYPENAME_GRAPH_IMAGE.length() + 1);
            if (endNodeIdx == -1) {
                endNodeIdx = json.length();
            }
            String subJson = json.substring(nodeIdx, endNodeIdx);
            InstaImageData imageData = new InstaImageData();
            if (imageData.parse(subJson)) {
                imageList.add(imageData);
            }
            if (endNodeIdx == json.length()) {
                break;
            }
            nodeIdx = endNodeIdx;
        }

        return imageList;
    }
}