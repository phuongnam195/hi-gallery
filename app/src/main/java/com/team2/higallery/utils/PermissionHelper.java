package com.team2.higallery.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {
    // Credit: https://stackoverflow.com/questions/37672338/java-lang-securityexception-permission-denial-reading-com-android-providers-me/37672627
    public static final int REQUEST_CODE = 376;

    private static final String[] NEEDED_PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static boolean check(Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        // Nếu dưới Android M thì không cần xin quyền
        if (currentAPIVersion < android.os.Build.VERSION_CODES.M) {
            return true;
        }

        for (int i = 0; i < NEEDED_PERMISSIONS.length; i++) {
            if (ContextCompat.checkSelfPermission(context,
                    NEEDED_PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat
                        .requestPermissions(
                                (Activity) context,
                                NEEDED_PERMISSIONS,
                                REQUEST_CODE);
                return false;
            }
        }
        return true;
    }
}
