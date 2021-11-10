package com.team2.higallery.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.net.Uri;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.team2.higallery.Configuration;
import com.team2.higallery.R;
import com.team2.higallery.utils.DataUtils;

public class EditActivity extends AppCompatActivity{

//    Button selectImage;
//    Button takePhoto;

    private ActionBar appBar;
    //Button trong main edit (BACK, FILTER, ROTATE, SAVE)
//    Button saveImage;
//    Button backButton;
//    Button filterButton;
//    Button rotateButton;
//    Button flipButton;

    //Button up and down (FILTER)
//    Button upButton;
//    Button downButton;
//    Button backUpDown;

    //DS cac filter
//    Button brightness; // Do sang
//    Button blackAndWhite; // Trang den
//    Button negative; // Am ban
//    Button warm; // Do am
//    Button saveImageFilter;
//    Button backFilter;

    //Rotate screen
//    Button backRotate;
//    Button rotate90R;
//    Button rotate90L;
//    Button custom;
    SeekBar degree;
//    Button backSeekbar;
//    Button okSeekBar;

    //Flip screen
//    Button backFlip;
//    Button horizontalFlip;
//    Button verticalFlip;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        Configuration.set(this);
        setContentView(R.layout.activity_edit);

//        selectImage = (Button)findViewById(R.id.selectImageButton) ;
//        takePhoto = (Button)findViewById(R.id.takePhotoButton);


        //Button trong main edit (BACK, FILTER, ROTATE, SAVE)
//        saveImage = (Button)findViewById(R.id.saveImage);
//        backButton = (Button) findViewById(R.id.backButton);
//        filterButton = (Button)findViewById(R.id.filterButton);
//        rotateButton = (Button)findViewById(R.id.rotateButton);
//        flipButton = (Button)findViewById(R.id.flipButton);

        //Button up and down
//        upButton =(Button)findViewById(R.id.up) ;
//        downButton=(Button)findViewById(R.id.down);
//        backUpDown = (Button)findViewById(R.id.backToFilter);

        //DS cac filter
//        saveImageFilter = (Button)findViewById(R.id.saveImage_Filter);
//        backFilter = (Button)findViewById(R.id.back_Filter);
//
//        blackAndWhite = (Button)findViewById(R.id.blackAndWhite);
//        brightness = (Button)findViewById(R.id.brightness);
//        negative = (Button)findViewById(R.id.negative);
//        warm = (Button)findViewById(R.id.warm);

        //Rotate screen
//        backRotate = (Button)findViewById(R.id.backRotate);
//        rotate90L = (Button)findViewById(R.id.rotate90L);
//        rotate90R = (Button)findViewById(R.id.rotate90R);
//        custom = (Button)findViewById(R.id.custom) ;
        degree = (SeekBar)findViewById(R.id.angle);

//        backSeekbar = (Button)findViewById(R.id.backSeekbar);
//        okSeekBar = (Button)findViewById(R.id.okSeekbar);

        //Flip screen
//        backFlip = (Button)findViewById(R.id.backFlip);
//        horizontalFlip = (Button)findViewById(R.id.horizontalFlip);
//        verticalFlip = (Button)findViewById(R.id.verticalFlip);
        init();
        setupAppBar();
        ShowEditor();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.edit_save:
                saveIMG();
                return true;
        }
        return false;
    }

//    // Tạo biến để kiểm tra quyền
//    private static final int REQUEST_PERMISSION = 1234;
//    private static final String[] PERMISSION = {
////            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
//    private static final int PERMISSION_COUNT = 1;

    // HÀM check xem đã có đủ hết các quyền cần thiết chưa
//    private boolean notPermissions(){
//        for(int i = 0; i < PERMISSION_COUNT; i++){
//            if(checkSelfPermission(PERMISSION[i]) != PackageManager.PERMISSION_GRANTED){
//                return true;
//            }
//        }
//        return false;
//    }

    @SuppressLint("RestrictedApi")
    private void setupAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_edit);
        setSupportActionBar(toolbar);
        appBar = getSupportActionBar();

        // add back arrow to appbar
        appBar.setDisplayHomeAsUpEnabled(true);
        appBar.setDisplayShowHomeEnabled(true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

//    public void toggleBarVisibility(View view) {
//        if (appBar.isShowing()) {
//            appBar.hide();
//            bottomBar.setVisibility(View.GONE);
//
//        } else {
//            appBar.show();
//            bottomBar.setVisibility(View.VISIBLE);
//        }
//    }

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
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notPermissions()){
//            Toast.makeText(this, "request", Toast.LENGTH_SHORT).show();
//            requestPermissions(PERMISSION, REQUEST_PERMISSION);
//        }
    }

//    @Override
//    public void onRequestPermissionResult(int requestCode, String[] permission, int[] grantResults){
//        super.onRequestPermissionsResult(requestCode, permission, grantResults);
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == REQUEST_PERMISSION && grantResults.length > 0){
//            if(notPermissions()){
//                // Nếu người dùng không cấp quyền thì đóng rồi mở lại
////                ((ActivityManager) this.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
////                recreate();
//                Toast.makeText(this, "chua cap quyen", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    //    private static final int REQUEST_PICK_IMAGE = 12345;
    private ImageView imageView;


    //Nut back thuong
    public  void onBackPressed(){
        if (editMode){
//            findViewById(R.id.editScreen).setVisibility(View.GONE);
            //findViewById(R.id.main_screen).setVisibility(View.VISIBLE);

            if(editing) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE){
                            saveIMG();
                            editing =false;
                            editMode = false;
                            finish();
                        }
                        else if (which == DialogInterface.BUTTON_NEGATIVE){
                            editing = false;
                            editMode = false;
                            finish();
                        }
                        else{

                        }
                    }
                };
                builder.setMessage("Ảnh chưa được lưu, bạn muốn lưu ảnh lại không?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .setNeutralButton("cancel", dialogClickListener)
                        .show();
            }
            else{
                editMode = false;
                finish();
            }
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

        imageView = findViewById(R.id.image_view);

        // Kiểm tra xem thiết bị có camera ko, k có thì ẩn nút chụp
//        if(!EditActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
//            findViewById(R.id.takePhotoButton).setVisibility(View.GONE);
//        }

//        selectImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //Đặt lại góc cho phần rotate
//                currentDeg = 0;
//
//                // Tạo intent để chọn ảnh từ thiết bị
//                final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                final Intent pickIntent = new Intent(Intent.ACTION_PICK);
//                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                final Intent chooserIntent = Intent.createChooser(intent, "Select Image");
//                startActivityForResult(chooserIntent,
//                        REQUEST_PICK_IMAGE);
//            }
//        });

//        takePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePhotoIntent.resolveActivity(getPackageManager()) != null){
//                    //Tạo file cho hình ảnh vừa chụp
//                    final File photoFile = createImageFile();
//                    imageUri = Uri.fromFile(photoFile);
//
//                    //Lưu URI đề phòng trường hợp bị mất
//                    final SharedPreferences myPrefs = getSharedPreferences(appID, 0);
//                    myPrefs.edit().putString("path", photoFile.getAbsolutePath()).apply();
//                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                    startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
//                }else{
//                    Toast.makeText(EditActivity.this, "Camera của bạn đang bị lỗi", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        saveImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
//                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == DialogInterface.BUTTON_POSITIVE){
//                            final File outFile = createImageFile();
//                            try(FileOutputStream out = new FileOutputStream(outFile)){
//                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                                imageUri = Uri.parse("file://"+outFile.getAbsolutePath());
//                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
//                                Toast.makeText(EditActivity.this, "Đã lưu!", Toast.LENGTH_SHORT).show();
//                            }catch(IOException e){
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                };
//                builder.setMessage("Lưu ảnh hiện tại vào thư viện?")
//                        .setPositiveButton("Yes", dialogClickListener)
//                        .setNegativeButton("No", dialogClickListener).show();
//            }
//        });

//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                findViewById(R.id.editScreen).setVisibility(View.GONE);
//                //findViewById(R.id.main_screen).setVisibility(View.VISIBLE);
//
////                Intent intent = new Intent(this, PhotoActivity.class);
////                intent.putStringArrayListExtra("imagePaths", imagePaths);
////                intent.putExtra("currentIndex", index);
////                startActivity(intent);
//                finish();
//                editMode = false;
//            }
//        });

//        brightness.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findViewById(R.id.filtersGroup).setVisibility(View.GONE);
//                findViewById(R.id.UpDown).setVisibility(View.VISIBLE);
//                editBrightness = true;
//            }
//        });

//        blackAndWhite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                new Thread(){
////                    public void run(){
////                        blackAndWhite(pixels, width, height);
////                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
////
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                imageView.setImageBitmap(bitmap);
////                            }
////                        });
////                    }
////                }.start();
//                editing = true;
//                bitmap = toGrayscale(bitmap);
//                getPixelOfBitmap(bitmap);
//                imageView.setImageBitmap(bitmap);
//            }
//        });

//        negative.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editing = true;
//                new Thread(){
//                    public void run(){
//                        negative(pixels, width, height);
//                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                imageView.setImageBitmap(bitmap);
//                            }
//                        });
//                    }
//                }.start();
//            }
//        });

//        warm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findViewById(R.id.filtersGroup).setVisibility(View.GONE);
//                findViewById(R.id.UpDown).setVisibility(View.VISIBLE);
//                editWarm = true;
//            }
//        });

//        backUpDown.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editBrightness = false;
//                editWarm = false;
//                findViewById(R.id.UpDown).setVisibility(View.GONE);
//                findViewById(R.id.filtersGroup).setVisibility(View.VISIBLE);
//            }
//        });
//
//        downButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editing = true;
//                new Thread(){
//                    public void run(){
//                        //Do here
//                        if(editBrightness){
//                            BrightnessDown(pixels, width, height);
//                        }
//                        if(editWarm){
//                            WarmDown(pixels, width, height);
//                        }
//
//                        //end
//                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                imageView.setImageBitmap(bitmap);
//                            }
//                        });
//                    }
//                }.start();
//            }
//        });
//
//        upButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editing = true;
//                new Thread(){
//                    public void run(){
//                        if(editBrightness){
//                            BrightnessUp(pixels, width, height);
//                        }
//                        if(editWarm){
//                            WarmUp(pixels, width, height);
//                        }
//                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                imageView.setImageBitmap(bitmap);
//                            }
//                        });
//                    }
//                }.start();
//            }
//        });



//        saveImageFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editing = false;
//                final AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
//                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == DialogInterface.BUTTON_POSITIVE){
//                            final File outFile = createImageFile();
//                            try(FileOutputStream out = new FileOutputStream(outFile)){
//                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                                imageUri = Uri.parse("file://"+outFile.getAbsolutePath());
//                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
//                                Toast.makeText(EditActivity.this, "Đã lưu!", Toast.LENGTH_SHORT).show();
//                            }catch(IOException e){
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                };
//                builder.setMessage("Lưu ảnh hiện tại vào thư viện?")
//                        .setPositiveButton("Yes", dialogClickListener)
//                        .setNegativeButton("No", dialogClickListener).show();
//            }
//        });

//        backFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findViewById(R.id.filtersGroup).setVisibility(View.GONE);
//                findViewById(R.id.main_btn_group).setVisibility(View.VISIBLE);
//            }
//        });

//        backRotate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findViewById(R.id.rotateScreen).setVisibility(View.GONE);
//                findViewById(R.id.main_btn_group).setVisibility(View.VISIBLE);
//            }
//        });
//
//        rotate90L.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editing = true;
//                bitmap = RotateBitmap(bitmap, -90);
//                imageView.setImageBitmap(bitmap);
//                getPixelOfBitmap(bitmap);
//
//            }
//        });
//
//        rotate90R.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editing = true;
//                bitmap = RotateBitmap(bitmap, 90);
//                imageView.setImageBitmap(bitmap);
//                getPixelOfBitmap(bitmap);
//            }
//        });
//
//        custom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editing = true;
//                findViewById(R.id.rotateScreen).setVisibility(View.GONE);
//                findViewById(R.id.seekbarScreen).setVisibility(View.VISIBLE);
//                SeekBar t;
//                t = (SeekBar) findViewById(R.id.angle);
//                t.setProgress(currentDeg + 180);
//                imageView.setImageBitmap(RotateBitmap(bitmap, currentDeg));
//                TextView a =(TextView)findViewById(R.id.degree);
//                a.setText(Integer.toString(currentDeg));
//            }
//        });

        degree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int prog = progress - 180;
                TextView a =(TextView)findViewById(R.id.degree);

                bitmap_rotate = RotateBitmap(bitmap, prog);
//                a.setText(Integer.toString(prog - currentDeg) + " 🌡");

                currentDeg = prog;
                imageView.setImageBitmap(bitmap_rotate);
                a.setText(Integer.toString(currentDeg) + " 🌡");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//
//        backSeekbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findViewById(R.id.seekbarScreen).setVisibility(View.GONE);
//                findViewById(R.id.rotateScreen).setVisibility(View.VISIBLE);
//                imageView.setImageBitmap(bitmap);
//                currentDeg = 0;
//
//            }
//        });
//
//        okSeekBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editing = true;
//                findViewById(R.id.seekbarScreen).setVisibility(View.GONE);
//                findViewById(R.id.rotateScreen).setVisibility(View.VISIBLE);
//                bitmap = bitmap_rotate;
//                imageView.setImageBitmap(bitmap);
//                getPixelOfBitmap(bitmap);
//
//            }
//        });


//        backFlip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findViewById(R.id.flipScreen).setVisibility(View.GONE);
//                findViewById(R.id.main_btn_group).setVisibility(View.VISIBLE);
//            }
//        });
//
//        horizontalFlip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editing = true;
//                setHorizontalFlip();
//                bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//                imageView.setImageBitmap(bitmap);
//            }
//        });
//
//        verticalFlip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editing = true;
//                setVerticalFlip();
//                bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//                imageView.setImageBitmap(bitmap);
//
//            }
//        });
    }

    private static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        Bitmap newBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//        newBitmap.setHasAlpha(true);
        return newBitmap;
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

    private boolean editing = false;
    private boolean editMode = false;
    private boolean editBrightness = false;
    private boolean editWarm = false;
    private Bitmap bitmap;
    private Bitmap bitmap_filter;
    private Bitmap bitmap_rotate;
    private int width = 0;
    private int height = 0;
    private static final int MAX_PIXEL_COUNT = 2048;
    private int currentDeg = 0;

    private int[] pixels; // Lưu pixel của hình ảnh
    private int pixelCount = 0; // đếm số pixel của hình ảnh

    private ArrayList<String> pathList;
    private int currentIndex;

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

    void ShowEditor(){

        Intent intent1 = getIntent();
        Bundle dataIt = intent1.getExtras();

        pathList = dataIt.getStringArrayList("pathList");
        currentIndex = dataIt.getInt("currentIndex");

        imageUri = Uri.parse("file://" + pathList.get(currentIndex));

//        if(resultCode != RESULT_OK){
//            return;
//        }
//        if(requestCode == REQUEST_IMAGE_CAPTURE){
//            if(imageUri == null){
//                //lấy đường dẫn đã lưu trong trường hợp lạc mất image URI
//                final SharedPreferences pref = getSharedPreferences(appID, 0);
//                final String path = pref.getString("path", "");
//
//                // Nếu không có đường dẫn thì sẽ out ra ngoài (lỗi đường dẫn)
//                if (path.length() < 1){
//                    recreate();
//                    return;
//                }
//                imageUri = Uri.parse("file://" + path);
//            }
//
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
//        }
//        else if(data == null){
//            recreate();
//            return;
//        }
//        else if(requestCode == REQUEST_PICK_IMAGE){
//            imageUri = data.getData();
//        }
        final ProgressDialog dialog = ProgressDialog.show(EditActivity.this, "Loading", "Please Wait", true);
        currentDeg = 0;
        editMode = true;
        //findViewById(R.id.main_screen).setVisibility(View.GONE);
        findViewById(R.id.editScreen).setVisibility(View.VISIBLE);
        findViewById(R.id.UpDown).setVisibility(View.GONE);
        findViewById(R.id.filtersGroup).setVisibility(View.GONE);
        findViewById(R.id.rotateScreen).setVisibility(View.GONE);
        findViewById(R.id.seekbarScreen).setVisibility(View.GONE);
        findViewById(R.id.flipScreen).setVisibility(View.GONE);


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

    public void getPixelOfBitmap(Bitmap bm){
        width = bm.getWidth();
        height = bm.getHeight();
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        pixelCount = width*height;
        pixels = new int[pixelCount];
        bitmap.getPixels(pixels,0,width,0,0,width, height);
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    private void saveIMG(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE){
                    editing = false;
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

//    private Bitmap adjustedContrast(Bitmap src, double value)
//    {
//        // image size
//        int width = src.getWidth();
//        int height = src.getHeight();
//        // create output bitmap
//        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
//        // color information
//        int A, R, G, B;
//        int pixel;
//        // get contrast value
//        double contrast = Math.pow((100 + value) / 100, 2);
//
//        // scan through all pixels
//        for(int x = 0; x < width; ++x) {
//            for(int y = 0; y < height; ++y) {
//                // get pixel color
//                pixel = src.getPixel(x, y);
//                A = Color.alpha(pixel);
//                // apply filter contrast for every channel R, G, B
//                R = Color.red(pixel);
//                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//                if(R < 0) { R = 0; }
//                else if(R > 255) { R = 255; }
//
//                G = Color.green(pixel);
//                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//                if(G < 0) { G = 0; }
//                else if(G > 255) { G = 255; }
//
//                B = Color.blue(pixel);
//                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//                if(B < 0) { B = 0; }
//                else if(B > 255) { B = 255; }
//
//                // set new pixel color to output bitmap
//                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//            }
//        }
//
////        for (int i = 0; i < width*height; i++){
////            A = Color.alpha(pixels[i]);
////            // apply filter contrast for every channel R, G, B
////            R = Color.red(pixels[i]);
////            R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
////            if(R < 0) { R = 0; }
////            else if(R > 255) { R = 255; }
////
////            G = Color.green(pixels[i]);
////            G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
////            if(G < 0) { G = 0; }
////            else if(G > 255) { G = 255; }
////
////            B = Color.blue(pixels[i]);
////            B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
////            if(B < 0) { B = 0; }
////            else if(B > 255) { B = 255; }
////
////            // set new pixel color to output bitmap
////            bmOut.setPixel(pixels[i]%width, pixels[i]%height, Color.argb(A, R, G, B));
////        }
//
//        return bmOut;
//    }

    public void setVerticalFlip(){
        int[] cpy = new int[pixelCount];
        for(int i = 0; i<pixelCount; i++){
            int opposite = pixelCount - 1 - i;
            cpy[opposite - (opposite%width - i % width)] = pixels[i];
        }
        pixels = cpy;
    }

    public void setHorizontalFlip(){
        int[] cpy = new int[pixelCount];
        for(int i = 0; i<pixelCount; i++){
            int opposite = pixelCount - 1 - i;
            cpy[opposite - (opposite/width - i /width)*width] = pixels[i];
        }
        pixels = cpy;
    }

    public void onFilter(View v) {
        findViewById(R.id.main_btn_group).setVisibility(View.GONE);
        findViewById(R.id.filtersGroup).setVisibility(View.VISIBLE);
    }

    public void onRotate(View v) {
        findViewById(R.id.rotateScreen).setVisibility(View.VISIBLE);
        findViewById(R.id.main_btn_group).setVisibility(View.GONE);
    }

    public void onFlip(View v) {
        findViewById(R.id.flipScreen).setVisibility(View.VISIBLE);
        findViewById(R.id.main_btn_group).setVisibility(View.GONE);
    }

    public void onBrightness(View v) {
        findViewById(R.id.filtersGroup).setVisibility(View.GONE);
        findViewById(R.id.UpDown).setVisibility(View.VISIBLE);
        editBrightness = true;
    }

    public void onBlackAndWhite(View v) {
        editing = true;
        bitmap = toGrayscale(bitmap);
        getPixelOfBitmap(bitmap);
        imageView.setImageBitmap(bitmap);
    }

    public void onNegative(View v) {
        editing = true;
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

    public void onWarm(View v) {
        findViewById(R.id.filtersGroup).setVisibility(View.GONE);
        findViewById(R.id.UpDown).setVisibility(View.VISIBLE);
        editWarm = true;
    }

    public void onSaveImageFilter(View v){
        saveIMG();
    }

    public void onBackFilter(View v) {
        findViewById(R.id.filtersGroup).setVisibility(View.GONE);
        findViewById(R.id.main_btn_group).setVisibility(View.VISIBLE);
    }

    public void onBackUpDown(View v) {
        editBrightness = false;
        editWarm = false;
        findViewById(R.id.UpDown).setVisibility(View.GONE);
        findViewById(R.id.filtersGroup).setVisibility(View.VISIBLE);
    }



    public void onDown(View v) {
        editing = true;
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



    public void onUp(View v) {
        editing = true;
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


    public void onBackRotate(View v) {
        findViewById(R.id.rotateScreen).setVisibility(View.GONE);
        findViewById(R.id.main_btn_group).setVisibility(View.VISIBLE);
    }

    public void onRotateL(View v) {
        editing = true;
        bitmap = RotateBitmap(bitmap, -90);
        imageView.setImageBitmap(bitmap);
        getPixelOfBitmap(bitmap);

    }

    public void onRotateR(View v) {
        editing = true;
        bitmap = RotateBitmap(bitmap, 90);
        imageView.setImageBitmap(bitmap);
        getPixelOfBitmap(bitmap);
    }

    public void onCustom(View v) {
        editing = true;
        findViewById(R.id.rotateScreen).setVisibility(View.GONE);
        findViewById(R.id.seekbarScreen).setVisibility(View.VISIBLE);
        SeekBar t;
        t = (SeekBar) findViewById(R.id.angle);
        t.setProgress(currentDeg + 180);
        imageView.setImageBitmap(RotateBitmap(bitmap, currentDeg));
        TextView a =(TextView)findViewById(R.id.degree);
        a.setText(Integer.toString(currentDeg));
    }

    public void onBackSeekBar(View v) {
        findViewById(R.id.seekbarScreen).setVisibility(View.GONE);
        findViewById(R.id.rotateScreen).setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);
        currentDeg = 0;

    }

    public void onOkSeekBar(View v) {
        editing = true;
        findViewById(R.id.seekbarScreen).setVisibility(View.GONE);
        findViewById(R.id.rotateScreen).setVisibility(View.VISIBLE);
        bitmap = bitmap_rotate;
        imageView.setImageBitmap(bitmap);
        getPixelOfBitmap(bitmap);

    }

    public void onBackFlip(View v) {
        findViewById(R.id.flipScreen).setVisibility(View.GONE);
        findViewById(R.id.main_btn_group).setVisibility(View.VISIBLE);
    }

    public void onHorizFlip(View v) {
        editing = true;
        setHorizontalFlip();
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        imageView.setImageBitmap(bitmap);
    }

    public void onVerFlip(View v) {
        editing = true;
        setVerticalFlip();
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        imageView.setImageBitmap(bitmap);
    }


}