package com.team2.higallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.team2.higallery.utils.LocaleHelper;

public class Configuration {
    private static final String SELECTED_LANGUAGE = "language";
    private static final String SELECTED_THEME = "dark_theme";
    private static final String SELECTED_AUTO_CLEAN_TIME = "auto_clean_time";

    public static final long AUTO_CLEAN_OFF = -1;           // tắt tự động xóa
    public static final long AUTO_CLEAN_TIME_1 = 2592000000L;  // 30 ngày
    public static final long AUTO_CLEAN_TIME_2 = 7776000000L;  // 90 ngày
    public static final long AUTO_CLEAN_TIME_3 = 60000;        // 1 phút

    public static String language;
    public static boolean isDarkTheme = false;
    public static long autoCleanTime;

    public static void load(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        language = preferences.getString(SELECTED_LANGUAGE, "");
        if (language == null || language.isEmpty()) {
            language = LocaleHelper.getLocale(context).getLanguage();
        }

        isDarkTheme = preferences.getBoolean(SELECTED_THEME, false);

        autoCleanTime = preferences.getLong(SELECTED_AUTO_CLEAN_TIME, AUTO_CLEAN_OFF);
    }

    public static void save(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.putBoolean(SELECTED_THEME, isDarkTheme);
        editor.putLong(SELECTED_AUTO_CLEAN_TIME, autoCleanTime);
        editor.apply();
    }

    public static void set(Context context) {
        // set language
        LocaleHelper.setLocale(context, Configuration.language);

        // set theme
        if (isDarkTheme) {
            context.setTheme(R.style.AppThemeDark);
        } else {
            context.setTheme(R.style.AppTheme);
        }


    }

    public static boolean languageChanged = false;
    public static boolean themeChanged = false;

    // True nếu vừa có sự thay đổi config và chưa được áp dụng
    public static boolean alreadyChanged() {
        return languageChanged || themeChanged;
    }

    // Sau khi đã áp dụng các thay đổi thì set false
    public static void appliedChanges() {
        languageChanged = false;
        themeChanged = false;
    }

    public static void changeLanguage() {
        if (language.equals("en")) {
            language = "vi";
        } else {
            language = "en";
        }
        languageChanged = !languageChanged;
    }

    public static void changeTheme() {
        isDarkTheme = !isDarkTheme;
        themeChanged = !themeChanged;
    }

    public static int getAutoCleanString() {
        if (autoCleanTime == AUTO_CLEAN_OFF) {
            return R.string.settings_auto_clean_menu_0;
        }
        if (autoCleanTime == AUTO_CLEAN_TIME_1) {
            return R.string.settings_auto_clean_menu_1;
        }
        if (autoCleanTime == AUTO_CLEAN_TIME_2) {
            return R.string.settings_auto_clean_menu_2;
        }

        return R.string.settings_auto_clean_menu_3;
    }
}