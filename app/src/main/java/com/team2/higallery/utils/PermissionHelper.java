package com.team2.higallery.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {
    // Credit: https://stackoverflow.com/questions/37672338/java-lang-securityexception-permission-denial-reading-com-android-providers-me/37672627
    public static final int REQUEST_CODE = 376;

    private static final String[] EXTERNAL_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static boolean isGranted(Context context) {
        for (int i = 0; i < EXTERNAL_STORAGE.length; i++) {
            if (ContextCompat.checkSelfPermission(context,
                    EXTERNAL_STORAGE[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void request(Context context) {
        ActivityCompat
                .requestPermissions(
                        (Activity) context,
                        EXTERNAL_STORAGE,
                        REQUEST_CODE);
    }
}
