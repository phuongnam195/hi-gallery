package com.team2.higallery.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.team2.higallery.utils.DataUtils;

import java.util.ArrayList;

public class FavoriteImages {
    public static ArrayList<String> list = new ArrayList<>();

    // Kiếm tra một ảnh có được yêu thích hay không
    public static boolean check(String imagePath) {
        return list.contains(imagePath);
    }

    // Thêm/xóa ảnh trong danh sách ảnh được yêu thích
    public static boolean toggle(String imagePath) {
        if (list.contains(imagePath)) {
            list.remove(imagePath);
            return false;
        } else {
            list.add(0, imagePath);
            return true;
        }
    }

    public static ArrayList<String> get() {
        ArrayList<String> result = new ArrayList<>();
        for (String path : list) {
            if (DataUtils.allImages.contains(path)) {
                result.add(path);
            }
        }
        return result;
    }

    // Nạp danh sách đường dẫn ảnh được yêu thích
    public static void load(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("IMAGES_DATA", Context.MODE_PRIVATE);
        int n = preferences.getInt("favorite_images_size", 0);
        if (n > 0) {
            list.clear();
        }
        for (int i = 0; i < n; i++) {
            String imageUrl = preferences.getString("favorite_image_" + String.valueOf(i), "");
            if (imageUrl.isEmpty()) {
                list.clear();
                return;
            }
            list.add(imageUrl);
        }
    }

    // Lưu danh sách đường dẫn ảnh được yêu thích
    public static void save(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("IMAGES_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("favorite_images_size", list.size());
        for (int i = 0; i < list.size(); i++) {
            editor.putString("favorite_image_" + String.valueOf(i), list.get(i));
        }
        editor.apply();
    }
}
