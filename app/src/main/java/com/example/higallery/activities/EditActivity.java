package com.example.higallery.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.net.Uri;
import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.higallery.MainActivity;
import com.example.higallery.R;

public class EditActivity extends AppCompatActivity{

    Button selectImage;
    Button takePhoto;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        selectImage = (Button)findViewById(R.id.selectImageButton) ;
        takePhoto = (Button)findViewById(R.id.takePhotoButton);

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

    private void init(){
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

        dialog.cancel();//Load xong thi dong dialog
    }
}
