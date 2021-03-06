package com.team2.higallery.activities;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import com.team2.higallery.R;
import com.team2.higallery.utils.FileUtils;

public class EditActivity extends AppCompatActivity{


    private ActionBar appBar;

    //Rotate screen
    SeekBar seekbarCustomRotate;
    TextView degreeCustomRotate;
    EditText text;
    EditText textSize;
    EditText textSize2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        seekbarCustomRotate = (SeekBar)findViewById(R.id.seekbar_custom_rotate);
        text = (EditText)findViewById(R.id.text) ;
        textSize = (EditText)findViewById(R.id.size);

        textSize2 = (EditText)findViewById(R.id.sizedraw);





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

//    // T???o bi???n ????? ki???m tra quy???n
//    private static final int REQUEST_PERMISSION = 1234;
//    private static final String[] PERMISSION = {
////            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
//    private static final int PERMISSION_COUNT = 1;

    // H??M check xem ???? c?? ????? h???t c??c quy???n c???n thi???t ch??a
//    private boolean notPermissions(){
//        for(int i = 0; i < PERMISSION_COUNT; i++){
//            if(checkSelfPermission(PERMISSION[i]) != PackageManager.PERMISSION_GRANTED){
//                return true;
//            }
//        }
//        return false;
//    }

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
    }

    //    private static final int REQUEST_PICK_IMAGE = 12345;
    private ImageView imageView;


    //Nut back thuong
    public  void onBackPressed(){
        if (editMode){
//            findViewById(R.id.editScreen).setVisibility(View.GONE);
            //findViewById(R.id.main_screen).setVisibility(View.VISIBLE);

            if(editing) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE){
                            saveIMG();
                            editMode = false;

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
                builder.setMessage(getResources().getString(R.string.edit_back_message_dialog))
                        .setPositiveButton(R.string.edit_yes_dialog, dialogClickListener)
                        .setNegativeButton(R.string.edit_no_dialog, dialogClickListener)
                        .setNeutralButton(R.string.edit_cancel_action_dialog, dialogClickListener)
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

        // Ki???m tra version
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        imageView = findViewById(R.id.image_view);


        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                str = text.getText().toString();
                if (addText){
                    Draw();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tmp = textSize.getText().toString();
                if(!tmp.equals("")){
                    try{
                        size = Integer.parseInt(tmp);
                    }catch (Exception err){
                        size = 1;
                    }
                }
                if (addText){
                    Draw();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textSize2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tmp = textSize2.getText().toString();
                if(!tmp.equals("")){
                    try{
                        size2 = Integer.parseInt(tmp);
                    }catch (Exception err){
                        size2 = 1;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//        degree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        seekbarCustomRotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int prog = progress - 180;

                bitmap_rotate = RotateBitmap(bitmap, prog);
                currentDeg = prog;
                imageView.setImageBitmap(bitmap_rotate);
                degreeCustomRotate.setText(String.format("%d??", currentDeg));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    int locaX = 0;
    int locaY = 0;
    int size = 30;
    int size2 = 2;
    public Bitmap bitmap_text;
    int color = 0xFF000000;
    int colorDraw = 0xFF000000;
    String str = "";
    boolean addText = false;
    boolean drawing = false;
    int currX = 0;
    int currY = 0;
    public Bitmap drawTemp;

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if(addText){
                // save the X,Y coordinates
//                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
//                    locaX = Math.round(event.getX());
//                    locaY = Math.round(event.getY());
//                    Draw();
                if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                    locaX = Math.round(event.getX());
                    locaY = Math.round(event.getY());
                    Draw();
                }
//                Draw();
            }

            // let the touch event pass on to whoever needs it
            return true;
        }
    };

    View.OnTouchListener touchListener2 = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(drawing){
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        locaX = Math.round(event.getX());
                        locaY = Math.round(event.getY());
                        DrawLine();
                        break;
                    case MotionEvent.ACTION_UP:
                        currX = 0;
                        currY = 0;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
            }

            // let the touch event pass on to whoever needs it
            return true;
        }
    };




    private void Draw(){
        int widthBM1 = imageView.getDrawable().getIntrinsicWidth(); // K??ch th?????c bitmap theo px
        int heightBM1 = imageView.getDrawable().getIntrinsicHeight();

        int widthIMGV = imageView.getWidth(); //k??ch th?????c imageview theo px
        int heightIMGV = imageView.getHeight();

        float ratio = (float)widthBM1 / (float)heightBM1;

        int widthBM2, heightBM2; // K??ch th?????c bitmap h??? quy chi???u ImageView :D

        if ((float)heightIMGV * ratio <= (float)widthIMGV){
            heightBM2 = Math.round((float)heightIMGV);
            widthBM2 = Math.round((float)heightIMGV * ratio);
        }else{
            widthBM2 = Math.round((float)widthIMGV);
            heightBM2 = Math.round((float)widthIMGV / ratio);
        }

        int xx, yy; // T???a ????? th???t c???a bitmap
        if (widthIMGV == widthBM2 ){
            xx = locaX;
        }else{
            if (locaX < (widthIMGV - widthBM2 )/2) {
                xx = 0;
            }else{
                xx = locaX - (widthIMGV - widthBM2 )/2;
            }
        }

        if (heightIMGV == heightBM2 ){
            yy = locaY;
        }else{
            if (locaY < (heightIMGV - heightBM2 )/2) {
                yy = 0;
            }else{
                yy = locaY - (heightIMGV - heightBM2 )/2;
            }
        }

        int x, y;
        x = Math.round(((float)xx / (float)widthBM2) * (float)widthBM1);
        y = Math.round(((float)yy / (float)heightBM2) * (float)heightBM1);

        if (y == 0){
            y += size;
        }
        if (y > heightBM1){
            y = heightBM1;
        }

//            int startBMX = Math.round((widthIMGV - widthBM) / 2); // t???a ????? c???a bitmap t??nh t??? imageview
//            int startBMY = Math.round((heightIMGV - heightBM) / 2);

        bitmap_text = drawStringonBitmap(bitmap, str, x, y, color, 1, size, false);
        imageView.setImageBitmap(bitmap_text);
    }




    private void DrawLine(){
        int widthBM1 = imageView.getDrawable().getIntrinsicWidth(); // K??ch th?????c bitmap theo px
        int heightBM1 = imageView.getDrawable().getIntrinsicHeight();

        int widthIMGV = imageView.getWidth(); //k??ch th?????c imageview theo px
        int heightIMGV = imageView.getHeight();

        float ratio = (float)widthBM1 / (float)heightBM1;

        int widthBM2, heightBM2; // K??ch th?????c bitmap h??? quy chi???u ImageView :D

        if ((float)heightIMGV * ratio <= (float)widthIMGV){
            heightBM2 = Math.round((float)heightIMGV);
            widthBM2 = Math.round((float)heightIMGV * ratio);
        }else{
            widthBM2 = Math.round((float)widthIMGV);
            heightBM2 = Math.round((float)widthIMGV / ratio);
        }

        int xx, yy; // T???a ????? th???t c???a bitmap
        if (widthIMGV == widthBM2 ){
            xx = locaX;
        }else{
            if (locaX < (widthIMGV - widthBM2 )/2) {
                xx = 0;
            }else{
                xx = locaX - (widthIMGV - widthBM2 )/2;
            }
        }

        if (heightIMGV == heightBM2 ){
            yy = locaY;
        }else{
            if (locaY < (heightIMGV - heightBM2 )/2) {
                yy = 0;
            }else{
                yy = locaY - (heightIMGV - heightBM2 )/2;
            }
        }

        int x, y;
        x = Math.round(((float)xx / (float)widthBM2) * (float)widthBM1);
        y = Math.round(((float)yy / (float)heightBM2) * (float)heightBM1);

        if (y == 0){
            y += size;
        }
        if (y > heightBM1){
            y = heightBM1;
        }

//            int startBMX = Math.round((widthIMGV - widthBM) / 2); // t???a ????? c???a bitmap t??nh t??? imageview
//            int startBMY = Math.round((heightIMGV - heightBM) / 2);
        if(drawTemp != null){
            drawTemp = drawLineonBitmap(drawTemp, x, y, colorDraw, size2);
        }
        else{
            drawTemp = drawLineonBitmap(bitmap, x, y,colorDraw, size2);
        }
        imageView.setImageBitmap(drawTemp);
    }

    public Bitmap drawLineonBitmap(Bitmap src, int x, int y, int color, int size) {

        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();

        paint.setColor(color);
        if (size <1){
            paint.setStrokeWidth(1);

        }else{
            paint.setStrokeWidth(size);
        }
        canvas.drawBitmap(src, 0, 0, null);

        if(currX != 0 && currY != 0){
            canvas.drawLine(currX, currY, x, y, paint);
        }
        currX = x;
        currY = y;
        return result;
    }



    private static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        Bitmap newBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//        newBitmap.setHasAlpha(true);
        return newBitmap;
    }

    private static final int REQUEST_IMAGE_CAPTURE = 1012; //Gi?? tr??? ????? check trong h??m onActivityResult

    private static final String appID = "HiGallery";

    private  Uri imageUri;

    private File createImageFile(){
        File folder = new File(FileUtils.HIGALERRY_FOLDER_PATH);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            //Toast.makeText(MainActivity.this, "Directory Created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Create folder HiGallery fail!", Toast.LENGTH_SHORT).show();
        }

        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = "/JPEG_" + timeStamp + ".jpg";
        return new File(FileUtils.HIGALERRY_FOLDER_PATH + imageFileName);
    }

    private File createImageFile2() {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = "/JPEG_" + timeStamp + ".jpg";
        String path = pathList.get(currentIndex);
        return new File(path.substring(0, path.lastIndexOf("/")) + imageFileName);
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

    private int[] pixels; // L??u pixel c???a h??nh ???nh
    private int pixelCount = 0; // ?????m s??? pixel c???a h??nh ???nh

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

        final ProgressDialog dialog = ProgressDialog.show(EditActivity.this, "Loading", "Please Wait", true);
        currentDeg = 0;
        editMode = true;

        // T???o lu???ng (thread) ????? hi???n ???nh v???a load) -- Load bitmap
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

                //N???u resizeScale > 1 th?? b??? gi???i m?? s??? hi???n th??? h??nh ???nh nh??? h??n ????? v???a v???i m??n h??nh (????? ph??n gi???i nh??? h??n) -- g??n v??o inSampleSize
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
                        imageView.setImageBitmap(bitmap); // Hi???n th??? ???nh l??n View
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
        }.start(); // Ch???y thread v???a code

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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    editing = false;
                    final File outFile = createImageFile();
                    try(FileOutputStream out = new FileOutputStream(outFile)){
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        imageUri = Uri.parse("file://"+outFile.getAbsolutePath());
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
                        Toast.makeText(EditActivity.this, "???? l??u!", Toast.LENGTH_SHORT).show();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    editing = false;
                    final File outFile = createImageFile2();
                    try(FileOutputStream out = new FileOutputStream(outFile)){
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        imageUri = Uri.parse("file://"+outFile.getAbsolutePath());
                        File deleteIMG= new File(pathList.get(currentIndex));
                        FileUtils.removeImageMedia(EditActivity.this, deleteIMG);

                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
                        Toast.makeText(EditActivity.this, "???? l??u!", Toast.LENGTH_SHORT).show();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
                else{
                        editMode = true;
                        editing = true;
                }
            }
        };
        builder.setMessage(R.string.edit_save_message_dialog)
                .setPositiveButton(R.string.edit_save_new_action_dialog, dialogClickListener)
                .setNegativeButton(R.string.edit_save_action_dialog, dialogClickListener)
                .setNeutralButton(R.string.edit_cancel_action_dialog, dialogClickListener)
                .show();
    }

    public void setVerticalFlip(){
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] cpy = new int[pixelCount];
        for(int i = 0; i<pixelCount; i++){
            int opposite = pixelCount - 1 - i;
            cpy[opposite - (opposite%width - i % width)] = pixels[i];
        }
        pixels = cpy;
    }

    public void setHorizontalFlip(){
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] cpy = new int[pixelCount];
        for(int i = 0; i<pixelCount; i++){
            int opposite = pixelCount - 1 - i;
            cpy[opposite - (opposite/width - i /width)*width] = pixels[i];
        }
        pixels = cpy;
    }

    public void onFilter(View v) {
        findViewById(R.id.main_group).setVisibility(View.GONE);
        findViewById(R.id.filters_group).setVisibility(View.VISIBLE);
    }

    public void onRotate(View v) {
        findViewById(R.id.rotate_group).setVisibility(View.VISIBLE);
        findViewById(R.id.main_group).setVisibility(View.GONE);
    }

    public void onFlip(View v) {
        findViewById(R.id.flip_group).setVisibility(View.VISIBLE);
        findViewById(R.id.main_group).setVisibility(View.GONE);
    }

    public void onBrightness(View v) {
        findViewById(R.id.filters_group).setVisibility(View.GONE);
        findViewById(R.id.inc_dec_group).setVisibility(View.VISIBLE);
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
                getPixelOfBitmap(bitmap);
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
        findViewById(R.id.filters_group).setVisibility(View.GONE);
        findViewById(R.id.inc_dec_group).setVisibility(View.VISIBLE);
        editWarm = true;
    }

    public void onSaveImageFilter(View v){
        saveIMG();
    }

    public void onBackFilter(View v) {
        findViewById(R.id.filters_group).setVisibility(View.GONE);
        findViewById(R.id.main_group).setVisibility(View.VISIBLE);
    }

    public void onBackUpDown(View v) {
        editBrightness = false;
        editWarm = false;
        findViewById(R.id.inc_dec_group).setVisibility(View.GONE);
        findViewById(R.id.filters_group).setVisibility(View.VISIBLE);
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

    public static Bitmap drawStringonBitmap(Bitmap src, String string, int x, int y, int color, int alpha, int size, boolean underline) {

        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(color);
//        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.drawText(string, x, y, paint);

        return result;
    }

    public void onBackRotate(View v) {
        findViewById(R.id.rotate_group).setVisibility(View.GONE);
        findViewById(R.id.main_group).setVisibility(View.VISIBLE);
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
        currentDeg = 0;
        editing = true;
        findViewById(R.id.rotate_group).setVisibility(View.GONE);
        findViewById(R.id.custom_rotate_group).setVisibility(View.VISIBLE);
        SeekBar t;
        t = (SeekBar) findViewById(R.id.seekbar_custom_rotate);
        t.setProgress(currentDeg + 180);
        imageView.setImageBitmap(RotateBitmap(bitmap, currentDeg));
        degreeCustomRotate = (TextView) findViewById(R.id.degree_custom_rotate);
    }

    public void onBackSeekBar(View v) {
        findViewById(R.id.custom_rotate_group).setVisibility(View.GONE);
        findViewById(R.id.rotate_group).setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);
        currentDeg = 0;
    }

    public void onOkSeekBar(View v) {
        editing = true;
        findViewById(R.id.custom_rotate_group).setVisibility(View.GONE);
        findViewById(R.id.rotate_group).setVisibility(View.VISIBLE);
        if(bitmap_rotate != null){
            bitmap = bitmap_rotate;
        }

        imageView.setImageBitmap(bitmap);
        getPixelOfBitmap(bitmap);

    }

    public void onBackFlip(View v) {
        findViewById(R.id.flip_group).setVisibility(View.GONE);
        findViewById(R.id.main_group).setVisibility(View.VISIBLE);
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

    public void onAddText(View v){
        imageView.setOnTouchListener(touchListener);
        addText = true;
        locaX = 0;
        locaY = 0;
        size = 30;
        text.setText("");
        str = text.getText().toString();
        textSize.setText(Integer.toString(size));
        color = 0xFF000000;

        findViewById(R.id.addText_group).setVisibility(View.VISIBLE);
        findViewById(R.id.main_group).setVisibility(View.GONE);
    }

    public void onBackAddText(View v){
        addText = false;
        findViewById(R.id.addText_group).setVisibility(View.GONE);
        findViewById(R.id.main_group).setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);
    }

    public void onOkAddText(View v){
        editing = true;
        bitmap = bitmap_text;
        addText = false;
        findViewById(R.id.addText_group).setVisibility(View.GONE);
        findViewById(R.id.main_group).setVisibility(View.VISIBLE);
    }

    public void onColorAddText(View v){
        int i = color;
        int[] rgb = new int[]{(i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF};

        final ColorPicker cp = new ColorPicker(EditActivity.this, rgb[0], rgb[1], rgb[2]);        /* On Click listener for the dialog, when the user select the color */
        Button okColor = (Button)cp.findViewById(R.id.okColorButton);
        cp.show();
        cp.enableAutoClose(); // Enable auto-dismiss for the dialog

        /* Set a new Listener called when user click "select" */
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color2) {
                // Do whatever you want

                color = color2;
                Draw();

                // If the auto-dismiss option is not enable (disabled as default) you have to manually dimiss the dialog
                // cp.dismiss();
            }
        });

    }

    public void onDrawing(View v){
        imageView.setOnTouchListener(touchListener2);
        drawing = true;
        locaX = 0;
        locaY = 0;
        currX = 0;
        currY = 0;
        size2 = 2;
        colorDraw = 0xFF000000;
        textSize2.setText(Integer.toString(size2));

        findViewById(R.id.Draw_group).setVisibility(View.VISIBLE);
        findViewById(R.id.main_group).setVisibility(View.GONE);
    }

    public void onBackDraw(View v){
        imageView.setImageBitmap(bitmap);
        drawTemp = null;
        drawing = false;
        findViewById(R.id.Draw_group).setVisibility(View.GONE);
        findViewById(R.id.main_group).setVisibility(View.VISIBLE);
    }

    public void onOKDraw(View v){
        editing = true;
        bitmap = drawTemp;
        drawTemp = null;
        drawing = false;
        findViewById(R.id.Draw_group).setVisibility(View.GONE);
        findViewById(R.id.main_group).setVisibility(View.VISIBLE);
    }

    public void onColorDraw(View v){
        int i = colorDraw;
        int[] rgb = new int[]{(i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF};

        final ColorPicker cp = new ColorPicker(EditActivity.this, rgb[0], rgb[1], rgb[2]);


        /* On Click listener for the dialog, when the user select the color */
        Button okColor = (Button)cp.findViewById(R.id.okColorButton);
        cp.show();
        cp.enableAutoClose(); // Enable auto-dismiss for the dialog

        /* Set a new Listener called when user click "select" */
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color2) {
                // Do whatever you want

                colorDraw = color2;

                // If the auto-dismiss option is not enable (disabled as default) you have to manually dimiss the dialog
                // cp.dismiss();
            }
        });

    }

}

