package com.team2.higallery.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {
    // Credit: https://stackoverflow.com/questions/37672338/java-lang-securityexception-permission-denial-reading-com-android-providers-me/37672627
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 234;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 235;

    public static boolean checkReadExternalStorage(Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        // Nếu dưới Android M thì mặc định đã có quyền Read
        if (currentAPIVersion < android.os.Build.VERSION_CODES.M) {
            return true;
        }

        // Nếu đã có quyền Read
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // Yêu cầu quyền Read
        ActivityCompat
                .requestPermissions(
                        (Activity) context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE);
        return false;
    }

    public static boolean checkWriteExternalStorage(Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        // Nếu dưới Android M thì mặc định đã có quyền Write
        if (currentAPIVersion < android.os.Build.VERSION_CODES.M) {
            return true;
        }

        // Nếu đã có quyền Write
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // Yêu cầu quyền Write
        ActivityCompat
                .requestPermissions(
                        (Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);
        return false;
    }
}
