package com.example.higallery;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "LocaleHelper.selectedLanguage";

    public static Locale getLocale(Context context) {
        return context.getResources().getConfiguration().getLocales().get(0);
    }

    @SuppressWarnings("deprecation")
    public static void setLocale(Context context, String language) {
        Resources activityRes = context.getResources();
        Configuration activityConf = activityRes.getConfiguration();
        Locale newLocale = new Locale(language);
        activityConf.setLocale(newLocale);
        activityRes.updateConfiguration(activityConf, activityRes.getDisplayMetrics());

        saveSelectedLanguage(context, language);
    }

    public static void loadSelectedLanguage(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String language = preferences.getString(SELECTED_LANGUAGE, "");
        setLocale(context, language);
    }

    private static void saveSelectedLanguage(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }
}