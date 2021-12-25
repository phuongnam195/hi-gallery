package com.team2.higallery.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtils {
    //Credit: https://stackoverflow.com/questions/8204680/java-regex-email
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public static String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder();
        String alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rng = new Random();
        for (int i = 0; i < length; i++) {
            int r = rng.nextInt(alphanum.length());
            builder.append(alphanum.charAt(r));
        }
        return builder.toString();
    }
}