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
import android.widget.Toast;

import com.team2.higallery.MainActivity;
import com.team2.higallery.R;
import com.team2.higallery.adapters.InstaListViewAdapter;
import com.team2.higallery.models.InstaImageData;
import com.team2.higallery.models.InstaSize;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetInstaActivity extends AppCompatActivity {
    final String TYPENAME_GRAPH_IMAGE = "\"__typename\":\"GraphImage\"";

    private ActionBar appBar;
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
        appBar = getSupportActionBar();

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
                if (json == null) {
                    return;
                }
                imageList = getImageUrls(json);
                InstaListViewAdapter adapter = new InstaListViewAdapter(GetInstaActivity.this, R.layout.insta_card, imageList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
        int questIdx = rawUrl.indexOf('?');
        String jsonUrl = rawUrl.substring(0, questIdx + 1) + "__a=1";

//        TEST MODE: Dùng trong trường hợp Instagram không cho tải JSON
//        jsonUrl = "https://firebasestorage.googleapis.com/v0/b/higallery-vault.appspot.com/o/insta.json?alt=media&token=101848bb-0e79-47db-b695-bc2d364d35a2";

        String json = null;
        try {
            URL url = new URL(jsonUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                json = bufferedReader.readLine();
                bufferedReader.close();
                int startIdx = json.indexOf(TYPENAME_GRAPH_IMAGE);
                if (startIdx == -1) {
                    Log.d("JSON ERROR", json);
                    return null;
                }
                json = json.substring(startIdx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    ArrayList<InstaImageData> getImageUrls(String json) {
        ArrayList<InstaImageData> imageList = new ArrayList<>();

        int nodeIdx = json.indexOf(TYPENAME_GRAPH_IMAGE);
        while (true) {
            int endNodeIdx = json.indexOf(TYPENAME_GRAPH_IMAGE, nodeIdx + TYPENAME_GRAPH_IMAGE.length() + 1);
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