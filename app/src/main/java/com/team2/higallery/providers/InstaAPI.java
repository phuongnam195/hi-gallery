package com.team2.higallery.providers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class InstaAPI {
    private ArrayList<String> urls;
    private ArrayList<HashMap<String, String>> headers;

    final public static String TYPENAME_GRAPH_IMAGE = "\"__typename\":\"GraphImage\"";

    private static InstaAPI instance;
    public static InstaAPI getInstance() {
        if (instance == null) {
            instance = new InstaAPI();
        }
        return instance;
    }
    private InstaAPI() {
        urls = new ArrayList<>();
        headers = new ArrayList<>();

        /*
         * Public API của Instagram
         * Lấy JSON chứa thông tin bài đăng (ảnh)
         * Bị giới hạn
         */
        urls.add("https://www.instagram.com/p/{POST_ID}/?__a=1");
        headers.add(null);


        // Các API dự phòng

        /*
         * Instagram Scraper API của RapidAPI
         * 25 request/day
         */
        urls.add("https://instagram-scraper2.p.rapidapi.com/media_info?short_code={POST_ID}");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("x-rapidapi-host", "instagram-scraper2.p.rapidapi.com");
        properties.put("x-rapidapi-key", "8c04bd6534msh8887ec1004cede8p18537bjsnd4bccb63712f");
        headers.add(properties);

        urls.add("https://instagram-scraper2.p.rapidapi.com/media_info?short_code={POST_ID}");
        properties = new HashMap<>();
        properties.put("x-rapidapi-host", "instagram-scraper2.p.rapidapi.com");
        properties.put("x-rapidapi-key", "3a55b0bd14msh488b4d682014163p13b262jsna03690cb8080");
        headers.add(properties);
    }

    public int size() {
        return urls.size();
    }

    public String fetchJSON(String postId, int index) {
        String replacedUrl = urls.get(index).replace("{POST_ID}", postId);
        try {
            URL url = new URL(replacedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (!headers.get(index).isEmpty()) {
                headers.get(index).forEach(connection::setRequestProperty);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String json = bufferedReader.readLine();
                bufferedReader.close();
                int startIdx = json.indexOf(TYPENAME_GRAPH_IMAGE);
                if (startIdx != -1) {
                    return json.substring(startIdx);
                } else {
                    Log.d("FETCH JSON FAILED", "API no." + index + "\n" + json);
                }
            }
        } catch (Exception e) {
            Log.d("InstaAPI-fetchJSON", e.getMessage());
        }
        return null;
    }
}
