package com.example.higallery.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper {
    public static Locale getLocale(Context context) {
        return context.getResources().getConfiguration().getLocales().get(0);
    }

    @SuppressWarnings("deprecation")
    public static void setLocale(Context context, String language) {
        if (getLocale(context).getLanguage().equals(language)) {
            return;
        }
        Resources activityRes = context.getResources();
        Configuration activityConf = activityRes.getConfiguration();
        Locale newLocale = new Locale(language);
        activityConf.setLocale(newLocale);
        activityRes.updateConfiguration(activityConf, activityRes.getDisplayMetrics());
    }
}