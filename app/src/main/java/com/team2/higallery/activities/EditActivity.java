package com.example.higallery.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.net.Uri;
import android.app.Dialog;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.higallery.MainActivity;
import com.example.higallery.R;

public class EditActivity extends AppCompatActivity{

    Button selectImage;
    Button takePhoto;
    Button saveImage;
    Button backButton;

    //Button up and down
    Button upButton;
    Button downButton;
    Button backUpDown;

    //DS cac filter
    Button brightness; // Do sang
    Button blackAndWhite; // Trang den
    Button negative; // Am ban
    Button warm; // Do am



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        selectImage = (Button)findViewById(R.id.selectImageButton) ;
        takePhoto = (Button)findViewById(R.id.takePhotoButton);
        blackAndWhite = (Button)findViewById(R.id.blackAndWhite);
        saveImage = (Button)findViewById(R.id.saveImage);
        backButton = (Button) findViewById(R.id.backButton);

        //Button up and down
        upButton =(Button)findViewById(R.id.up) ;
        downButton=(Button)findViewById(R.id.down);
        backUpDown = (Button)findViewById(R.id.backToFilter);
        //Do sang
        brightness = (Button)findViewById(R.id.brightness);
        // Am ban
        negative = (Button)findViewById(R.id.negative);
        //Do am
        warm = (Button)findViewById(R.id.warm);
        init();
    }

    // Tạo biến để kiểm tra quyền
    private static final int REQUEST_PERMISSION = 1234;
    private static final String[] PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_COUNT = 2;

    // HÀM check xem đã có đủ hết các quyền cần thiết chưa
    private boolean notPermissions(){
        for(int i = 0; i < PERMISSION_COUNT; i++){
            if(checkSelfPermission(PERMISSION[i]) != PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }

    static {
        System.loadLibrary("photoEditor");
    }
    private static native void blackAndWhite(int[] pixels, int width, int height);
    private static native void BrightnessUp(int[] pixels, int width, int height);
    private static native void BrightnessDown(int[] pixels, int width, int height);
    private static native void negative(int[] pixels, int width, int height);
    private static native void WarmDown(int[] pixels, int width, int height);
    private static native void WarmUp(int[] pixels, int width, int height);

    @Override
    public void onResume(){
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notPermissions()){
            requestPermissions(PERMISSION, REQUEST_PERMISSION);
        }
    }

//    @Override
//    public void onRequestPermissionResult(int requestCode, String[] permission, int[] grantResults){
//        super.onRequestPermissionsResult(requestCode, permission, grantResults);
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION && grantResults.length > 0){
            if(notPermissions()){
                // Nếu người dùng không cấp quyền thì đóng rồi mở lại
                ((ActivityManager) this.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
                recreate();
            }
        }
    }

    private static final int REQUEST_PICK_IMAGE = 12345;
    private ImageView imageView;


    //Nut back thuong
    public  void onBackPressed(){
        if (editMode){
            findViewById(R.id.editScreen).setVisibility(View.GONE);
            findViewById(R.id.main_screen).setVisibility(View.VISIBLE);
            editMode = false;
        }
        else{
            super.onBackPressed();
        }
    }

    private void init(){

        // Kiểm tra version
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        imageView = findViewById(R.id.imageView);

        // Kiểm tra xem thiết bị có camera ko, k có thì ẩn nút chụp
        if(!EditActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            findViewById(R.id.takePhotoButton).setVisibility(View.GONE);
        }


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Tạo intent để chọn ảnh từ thiết bị
                final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                final Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                final Intent chooserIntent = Intent.createChooser(intent, "Select Image");
                startActivityForResult(chooserIntent,
                        REQUEST_PICK_IMAGE);
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePhotoIntent.resolveActivity(getPackageManager()) != null){
                    //Tạo file cho hình ảnh vừa chụp
                    final File photoFile = createImageFile();
                    imageUri = Uri.fromFile(photoFile);

                    //Lưu URI đề phòng trường hợp bị mất
                    final SharedPreferences myPrefs = getSharedPreferences(appID, 0);
                    myPrefs.edit().putString("path", photoFile.getAbsolutePath()).apply();
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
                }else{
                    Toast.makeText(EditActivity.this, "Camera của bạn đang bị lỗi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE){
                            final File outFile = createImageFile();
                            try(FileOutputStream out = new FileOutputStream(outFile)){
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                imageUri = Uri.parse("file://"+outFile.getAbsolutePath());
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
                                Toast.makeText(EditActivity.this, "Đã lưu!", Toast.LENGTH_SHORT).show();
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                };
                builder.setMessage("Lưu ảnh hiện tại vào thư viện?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.editScreen).setVisibility(View.GONE);
                findViewById(R.id.main_screen).setVisibility(View.VISIBLE);
                editMode = false;
            }
        });

        brightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.filtersGroup).setVisibility(View.GONE);
                findViewById(R.id.UpDown).setVisibility(View.VISIBLE);
                editBrightness = true;
            }
        });

        blackAndWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    public void run(){
                        blackAndWhite(pixels, width, height);
                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                }.start();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    public void run(){
                        negative(pixels, width, height);
                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                }.start();
            }
        });

        warm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.filtersGroup).setVisibility(View.GONE);
                findViewById(R.id.UpDown).setVisibility(View.VISIBLE);
                editWarm = true;
            }
        });

        backUpDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBrightness = false;
                editWarm = false;
                findViewById(R.id.UpDown).setVisibility(View.GONE);
                findViewById(R.id.filtersGroup).setVisibility(View.VISIBLE);
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    public void run(){
                        //Do here
                        if(editBrightness){
                            BrightnessDown(pixels, width, height);
                        }
                        if(editWarm){
                            WarmDown(pixels, width, height);
                        }

                        //end
                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                }.start();
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    public void run(){
                        if(editBrightness){
                            BrightnessUp(pixels, width, height);
                        }
                        if(editWarm){
                            WarmUp(pixels, width, height);
                        }
                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                }.start();
            }
        });
    }

    private static final int REQUEST_IMAGE_CAPTURE = 1012; //Giá trị để check trong hàm onActivityResult

    private static final String appID = "HiGallery";

    private  Uri imageUri;

    private File createImageFile(){
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = "/JPEG_" + timeStamp + ".jpg";
        final File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(storageDir + imageFileName);
    }

    private boolean editMode = false;
    private boolean editBrightness = false;
    private boolean editWarm = false;
    private Bitmap bitmap;
    private int width = 0;
    private int height = 0;
    private static final int MAX_PIXEL_COUNT = 2048;

    private int[] pixels; // Lưu pixel của hình ảnh
    private int pixelCount = 0; // đếm số pixel của hình ảnh

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(imageUri == null){
                //lấy đường dẫn đã lưu trong trường hợp lạc mất image URI
                final SharedPreferences pref = getSharedPreferences(appID, 0);
                final String path = pref.getString("path", "");

                // Nếu không có đường dẫn thì sẽ out ra ngoài (lỗi đường dẫn)
                if (path.length() < 1){
                    recreate();
                    return;
                }
                imageUri = Uri.parse("file://" + path);
            }

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
        }
        else if(data == null){
            recreate();
            return;
        }
        else if(requestCode == REQUEST_PICK_IMAGE){
            imageUri = data.getData();
        }
        final ProgressDialog dialog = ProgressDialog.show(EditActivity.this, "Loading", "Please Wait", true);

        editMode = true;
        findViewById(R.id.main_screen).setVisibility(View.GONE);
        findViewById(R.id.editScreen).setVisibility(View.VISIBLE);
        findViewById(R.id.UpDown).setVisibility(View.GONE);


        // Tạo luồng (thread) để hiện ảnh vừa load) -- Load bitmap
        new Thread(){
            public void run(){
                bitmap = null;
                final BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
                bmpOptions.inBitmap = bitmap;
                bmpOptions.inJustDecodeBounds = true; // Vua voi man hinh
                try(InputStream input = getContentResolver().openInputStream(imageUri)){
                    bitmap = BitmapFactory.decodeStream(input, null, bmpOptions);
                }catch (IOException e){
                    e.printStackTrace();
                }
                bmpOptions.inJustDecodeBounds = false;

                width = bmpOptions.outWidth;
                height = bmpOptions.outHeight;

                //Nếu resizeScale > 1 thì bộ giải mã sẽ hiển thị hình ảnh nhỏ hơn để vừa với màn hình (độ phân giải nhỏ hơn) -- gán vào inSampleSize
                int resizeScale = 1;
                if(width > MAX_PIXEL_COUNT){
                    resizeScale = width / MAX_PIXEL_COUNT;
                }else if(height > MAX_PIXEL_COUNT){
                    resizeScale = height / MAX_PIXEL_COUNT;
                }
                if(width/resizeScale > MAX_PIXEL_COUNT || height/resizeScale > MAX_PIXEL_COUNT){
                    resizeScale++;
                }
                bmpOptions.inSampleSize = resizeScale;
                InputStream input = null;
                try{
                    input = getContentResolver().openInputStream(imageUri);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                    recreate();
                    return;
                }
                bitmap = BitmapFactory.decodeStream(input, null, bmpOptions);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap); // Hiển thị ảnh lên View
                        dialog.cancel();//Load xong thi dong dialog
                    }
                });
                width = bitmap.getWidth();
                height = bitmap.getHeight();
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                pixelCount = width*height;
                pixels = new int[pixelCount];

                //Copy tung pixel vao mang
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            }
        }.start(); // Chạy thread vừa code

    }
}
