package com.team2.higallery.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.team2.higallery.utils.DataUtils;

public class Account {
    private static final String EMAIL = "email";
    private static final String HASHED_PIN = "hashed_pin";

    public static FirebaseAuth auth;
    public static String email = null;
    public static String hashedPIN = null;

    public static void store(String email, String pin, Context context) {
        String hashedPIN = DataUtils.hash(pin);

        Account.email = email;
        Account.hashedPIN = hashedPIN;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL, email);
        editor.putString(HASHED_PIN, hashedPIN);
        editor.apply();
    }

    public static void load(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        email = preferences.getString(EMAIL, null);
        hashedPIN = preferences.getString(HASHED_PIN, null);
    }

    public static boolean checkPIN(String inputPIN, Context context) {
        String hashedInputPIN = DataUtils.hash(inputPIN);

        if (hashedPIN == null) {
            load(context);
        }

        return hashedInputPIN.equals(hashedPIN);
    }
}