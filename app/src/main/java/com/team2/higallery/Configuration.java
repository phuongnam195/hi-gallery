package com.team2.higallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.team2.higallery.utils.LocaleHelper;

public class Configuration {
    private static final String SELECTED_LANGUAGE = "language";
    private static final String SELECTED_THEME = "dark_theme";

    public static String language;
    public static boolean isDarkTheme = false;

    public static void load(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        language = preferences.getString(SELECTED_LANGUAGE, "");
        if (language == null || language.isEmpty()) {
            language = LocaleHelper.getLocale(context).getLanguage();
        }

        isDarkTheme = preferences.getBoolean(SELECTED_THEME, false);
    }

    public static void save(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.putBoolean(SELECTED_THEME, isDarkTheme);
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
}