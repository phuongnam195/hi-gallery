package com.team2.higallery.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.team2.higallery.models.Account;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils {
    private static final byte[] FIXED_SALT = "PTPMCTBDD-Nhom02".getBytes();

    private static EncryptUtils instance = null;
    public static EncryptUtils getInstance() {
        if (instance == null) {
            instance = new EncryptUtils();
        }
        return instance;
    }
    private EncryptUtils() { }

    private Cipher _cipher;
    private SecretKeySpec secretKey;

    private Cipher getCipher() {
        if (_cipher == null) {
            try {
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                String hashedPIN = Account.hashedPIN;
                byte[] key = Account.hashedPIN.getBytes(StandardCharsets.UTF_8);
                key = sha.digest(key);
                key = Arrays.copyOf(key, 16);
                secretKey = new SecretKeySpec(key, "AES");
                _cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return _cipher;
    }

    public byte[] encryptImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();

        try {
            Cipher cipher = getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(bytes);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Bitmap decryptImage(byte[] encryptedBytes) {
        try {
            Cipher cipher = getCipher();
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[]  decryptedBytes = cipher.doFinal(encryptedBytes);
            return BitmapFactory.decodeByteArray(decryptedBytes,  0, decryptedBytes.length);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String hash(String input) {
        try {
            KeySpec spec = new PBEKeySpec(input.toCharArray(), FIXED_SALT, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            String output = new String(hash, "UTF-8");
            return output;
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return null;
    }
}