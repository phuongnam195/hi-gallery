package com.team2.higallery.providers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team2.higallery.models.Account;
import com.team2.higallery.models.EncryptedImage;
import com.team2.higallery.utils.DataUtils;
import com.team2.higallery.utils.DatabaseHelper;
import com.team2.higallery.utils.EncryptUtils;
import com.team2.higallery.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class VaultManager {
    private static VaultManager instance;

    public static VaultManager getInstance(Context context) {
        if (instance == null) {
            instance = new VaultManager(context);
        }
        return instance;
    }

    private VaultManager(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
        encryptUtils = EncryptUtils.getInstance();
        encryptedImages = dbHelper.getAllEncryptedImages();
    }

    Context context;
    DatabaseHelper dbHelper;
    EncryptUtils encryptUtils;
    ArrayList<EncryptedImage> encryptedImages;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();

    public ArrayList<Bitmap> getAllDecryptedBitmaps() {
        if (bitmaps.isEmpty()) {
            try {
                for (EncryptedImage image : encryptedImages) {
                    File file = new File(context.getFilesDir().getAbsolutePath(), image.getFileName());
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    Bitmap decryptedBitmap = encryptUtils.decryptImage(bytes);
                    bitmaps.add(decryptedBitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmaps;
    }

    public void clearBitmaps() {
        bitmaps.clear();
    }

    public void moveImageToVault(String imagePath) {
        String encryptedFileName = DataUtils.generateRandomString(20) + ".hgv";   // HiGallery Vault

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        byte[] data = encryptUtils.encryptImage(bitmap);
        try {
            FileOutputStream out = context.openFileOutput(encryptedFileName, Context.MODE_PRIVATE);
            out.write(data);
            out.close();
        } catch (Exception e) {
        }

        FileUtils.removeImageFile(context, new File(imagePath));

        EncryptedImage encryptedImage = new EncryptedImage(encryptedFileName, imagePath);
        long id = dbHelper.insertEncryptedImage(encryptedImage);
        encryptedImage.setId(id);
        encryptedImages.add(encryptedImage);
        int index = encryptedImages.size() - 1;

        //Credit: https://firebase.google.com/docs
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference ref = storageRef.child("encrypted_files/" + encryptedFileName);
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                encryptedImage.setSynced(false);
                encryptedImages.set(index, encryptedImage);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference docRef = firestore.collection("users").document(Account.uid).collection("encrypted_images").document();
                docRef.set(encryptedImage.toMap());
            }
        });
    }

    public void revertToGallery(int index) {
        EncryptedImage encryptedImage = encryptedImages.get(index);
        String encryptedPath = encryptedImage.getFileName();
        String oldPath = encryptedImage.getOldPath();

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(context.getFilesDir().getAbsolutePath() + "/" + encryptedPath));
            Bitmap bitmap = encryptUtils.decryptImage(bytes);

            File imageFile = new File(oldPath);
            OutputStream fOut = new FileOutputStream(imageFile);
            bitmap.compress(FileUtils.getCompressFormat(oldPath), 100, fOut);
            fOut.flush();
            fOut.close();

            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        long id = encryptedImage.getId();
        dbHelper.removeEncryptedImage(id);
        encryptedImages.remove(index);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference ref = storageRef.child("encrypted_files/" + encryptedImage.getFileName());
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                Query query = firestore.collection("users").document(Account.uid).collection("encrypted_images").whereEqualTo("fileName", encryptedImage.getFileName());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        DocumentReference docRef = task.getResult().getDocuments().get(0).getReference();
                        docRef.delete();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    public void addEncryptedImage(EncryptedImage encryptedImage) {
        long id = dbHelper.insertEncryptedImage(encryptedImage);
        encryptedImage.setId(id);
        encryptedImages.add(encryptedImage);
    }

    public void synchronize() {
        for (int i = 0; i < encryptedImages.size(); i++) {
            EncryptedImage encryptedImage = encryptedImages.get(i);
            if (encryptedImage.isSynced()) {
                break;
            }

            final int index = i;
            Uri file = Uri.fromFile(new File(context.getFilesDir().getAbsolutePath(), encryptedImage.getFileName()));

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference ref = storageRef.child("encrypted_files/" + encryptedImage.getFileName());
            UploadTask uploadTask = ref.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    DocumentReference docRef = firestore.collection("users").document(Account.uid).collection("encrypted_images").document();
                    docRef.set(encryptedImage.toMap());
                    encryptedImage.setSynced(true);
                    encryptedImages.set(index, encryptedImage);
                }
            });
        }
    }
}
