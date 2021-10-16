package com.example.higallery.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.higallery.R;
import com.example.higallery.utils.EncryptAndDecryptImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class VaultAlbumActivity extends Activity {
    Button btnSelectImage;
    Button btnEncrypt, btnDecrypt;
    Button btnEncryptAndSaveFile, btnDecryptAndOpenFile;
    ImageView ivImageView;
    TextView tvStringOfImage;

    private static final String FILE_NAME = "image";

    //Input bitmap to convert to byte array, (file bytes array here)
    byte[] bytesOfImage = null;
    //Input byte array to convert to bitmap, then show image to screen()
    Bitmap bitmapOfImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_album);

        //Assign variable
        btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
        btnEncrypt = (Button) findViewById(R.id.btnEncrypt);
        btnDecrypt = (Button) findViewById(R.id.btnDecrypt);

        ivImageView = (ImageView) findViewById(R.id.ivImageView);
        tvStringOfImage = (TextView) findViewById(R.id.tvStringOfImage);

        btnEncryptAndSaveFile = (Button) findViewById(R.id.btnEncryptAndSaveFile);
        btnDecryptAndOpenFile = (Button) findViewById(R.id.btnDecryptAndOpenFile);

        //Event for Seclect Image button
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check permission read external store
                if (ContextCompat.checkSelfPermission(
                        VaultAlbumActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED
                ) {
                    //When permission is not granted, then request grand
                    ActivityCompat.requestPermissions(
                            VaultAlbumActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            100
                    );
                } else {
                    //When permission is granted, then choose image
                    selectImage();
                }
            }
        });

        //Event for encrypto button
        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmapOfImage != null) {
                    try {
                        //Encrypto
                        bytesOfImage = EncryptAndDecryptImage.encryptImage(bitmapOfImage);

                        //Delete bitmap of image
                        ivImageView.setImageBitmap(null);

                        //Show
                        tvStringOfImage.setText("Encrypto Success!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //Event for decrypto button
        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get code of image to decrypto, to show
                if (bytesOfImage != null) {
                    try {
                        //Decrypt
                        bitmapOfImage = EncryptAndDecryptImage.decryptImage(bytesOfImage);

                        //Delete code of image
                        tvStringOfImage.setText("Decrypt Success!");

                        //Show image
                        ivImageView.setImageBitmap(bitmapOfImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //Event for Encrypt file and Save file
        btnEncryptAndSaveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VaultAlbumActivity.this, "Dang ma hoa va luu", Toast.LENGTH_SHORT).show();
                if (bitmapOfImage != null) {
                    //Encrypt
                    try {
                        bytesOfImage = EncryptAndDecryptImage.encryptImage(bitmapOfImage);
                        saveFile(bytesOfImage);

                        bytesOfImage = null;

                        //Delete bitmap of image
                        ivImageView.setImageBitmap(null);

                        //Show
                        tvStringOfImage.setText("Encrypto and Save Success!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //Event for Decrypt file and Open file
        btnDecryptAndOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bytesOfImage == null) {
                    bytesOfImage = readFile();

                    if (bytesOfImage != null) {
                        try {
                            //Decrypt
                            bitmapOfImage = EncryptAndDecryptImage.decryptImage(bytesOfImage);

                            bytesOfImage = null;

                            //Delete code of image
                            tvStringOfImage.setText("Opene And Decrypt Success!");

                            //Show image
                            ivImageView.setImageBitmap(bitmapOfImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void selectImage() {
        //Remove data previous
        tvStringOfImage.setText("");
        ivImageView.setImageBitmap(null);
        bytesOfImage = null;
        bitmapOfImage = null;

        //Transfer to dialog choice
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 100);
    }


    private void saveFile(byte[] bytes) {
        Context context = getApplicationContext();
        try {
            File dirFile = new File(context.getFilesDir(), FILE_NAME);
            FileOutputStream fos = new FileOutputStream(dirFile);

            fos.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(VaultAlbumActivity.this, "Da luu du lieu", Toast.LENGTH_SHORT).show();
    }

    private byte[] readFile() {
        Context context = getApplicationContext();
        byte[] bytes = null;

        try {
            //Read file, then convert to bytes array
            File file = new File(context.getFilesDir(), FILE_NAME);
            FileInputStream fis = new FileInputStream(file);
            bytes = new byte[fis.available()];

            fis.read(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    // Credit 2 hàm bên dưới: https://www.youtube.com/watch?v=R_OSC0-nSIg
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            Toast.makeText(VaultAlbumActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    //When selected image, This function will call to encrypt
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //After choose image, if image existed
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                //Get bitmap from image that received
                bitmapOfImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                //Show image to screen
                ivImageView.setImageBitmap(bitmapOfImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(VaultAlbumActivity.this, "Input failure!", Toast.LENGTH_SHORT).show();
        }
    }
}