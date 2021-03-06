package com.team2.higallery.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.team2.higallery.utils.EncryptUtils;

public class Account {
    public static final int PIN_LENGTH = 6;

    private static final String UID = "uid";
    private static final String EMAIL = "email";
    private static final String HASHED_PIN = "hashed_pin";

    public static String uid = null;
    public static String email = null;
    public static String hashedPIN = null;

    public static boolean isSigned() {
        return (uid != null && email != null && hashedPIN != null);
    }

    public static void store(String uid, String email, String pin, Context context) {
        String hashedPIN = EncryptUtils.hash(pin);

        Account.uid = uid;
        Account.email = email;
        Account.hashedPIN = hashedPIN;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UID, uid);
        editor.putString(EMAIL, email);
        editor.putString(HASHED_PIN, hashedPIN);
        editor.apply();
    }

    public static void load(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        uid = preferences.getString(UID, null);
        email = preferences.getString(EMAIL, null);
        hashedPIN = preferences.getString(HASHED_PIN, null);
    }

    public static boolean checkPIN(String inputPIN) {
        String hashedInputPIN = EncryptUtils.hash(inputPIN);
        return hashedInputPIN.equals(hashedPIN);
    }

    public static boolean updatePIN(String newPIN, Context context) {
        String hashedNewPIN = EncryptUtils.hash(newPIN);
        if (hashedNewPIN.equals(hashedPIN)) {
            return false;
        }
        hashedPIN = hashedNewPIN;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(HASHED_PIN, hashedPIN);
        editor.apply();
        return true;
    }

}