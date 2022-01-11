package com.team2.higallery.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.team2.higallery.MainActivity;
import com.team2.higallery.R;
import com.team2.higallery.providers.ImagesProvider;
import com.team2.higallery.utils.BitmapUtils;
import com.team2.higallery.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class FindDuplicatesService extends Service {
    final String CHANNEL_ID = "HiGalley_DeleteDuplicates";

    NotificationCompat.Builder notiBuilder;
    NotificationManager notiManager;

    boolean isRunning = false;

    public FindDuplicatesService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        CharSequence name = "HiGalley_DeleteDuplicates";
        String description = "Delete duplicate images";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        notiBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)               // icon
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.app_name))          // tiêu đề
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)       // độ ưu tiên
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)    // hiển thị trên màn hình khóa
                .setAutoCancel(true);                                   // tự động xóa noti sau khi click

        notiManager = getSystemService(NotificationManager.class);
        notiManager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            // Tạo bản sao để tránh xung đột khi multi-thread
            ArrayList<String> copyAllImages = new ArrayList<>(ImagesProvider.allImages);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    ArrayList<String> duplicateImagePaths = findDuplicateImages(copyAllImages);
                    int count = duplicateImagePaths.size();

                    if (count == 0) {
                        notiBuilder.setContentText(getString(R.string.find_duplicates_not_detected_content));
                    } else {
                        Intent backToHome = new Intent(FindDuplicatesService.this, MainActivity.class);
                        backToHome.putStringArrayListExtra("duplicateImagePaths", duplicateImagePaths);
                        backToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                FindDuplicatesService.this,
                                0,
                                backToHome,
                                0);

                        notiBuilder.setContentText(getString(R.string.find_duplicates_result_content, count))
                                .setContentIntent(pendingIntent);                      // mở activity khi click
                    }

                    notiManager.notify(1905, notiBuilder.build());
                    isRunning = false;
                    stopSelf();
                }
            }.start();
        }

        return START_STICKY;
    }

    // Loại bỏ ảnh trùng lặp, trả về số lượng ảnh bị loại bỏ
    public static ArrayList<String> findDuplicateImages(ArrayList<String> imagePaths) {
        ArrayList<String> resultList = new ArrayList<>();
        HashMap<Integer, ArrayList<String>> hashMap = new HashMap<>();

        // Duyệt tất cả các path ảnh
        for (String currPath : imagePaths) {
            if (FileUtils.getExtension(currPath).equalsIgnoreCase("gif")) {
                continue;
            }

            Bitmap currBitmap = BitmapFactory.decodeFile(currPath);

            // Lấy mã hash của bitmap
            int hashCode = BitmapUtils.getHashCode(currBitmap);

            // Nếu trong map đã có hashCode này
            if (hashMap.containsKey(hashCode)) {
                ArrayList<String> insertedImagePaths = hashMap.get(hashCode);
                boolean notFound = true;

                // Thì duyệt các bitmap có cùng hashCode
                for (String oldPath : insertedImagePaths) {
                    Bitmap oldBitmap = BitmapFactory.decodeFile(oldPath);
                    // Nếu đã tồn tại bitmap giống 100%
                    if (BitmapUtils.compare(oldBitmap, currBitmap)) {
                        notFound = false;
                        // Xóa ảnh cũ hơn (cũng chính là ảnh hiện tại của vòng lặp) và không cần kiểm tra thêm
                        resultList.add(currPath);
                        break;
                    }
                }
                // Nếu không có bitmap (cùng hashCode) nào giống currBitmap, thì thêm vào danh sách
                if (notFound) {
                    insertedImagePaths.add(currPath);
                    hashMap.put(hashCode, insertedImagePaths);
                }
            }
            // Nếu trong map chưa có hashCode này
            else {
                ArrayList<String> newValue = new ArrayList<>();
                newValue.add(currPath);
                hashMap.put(hashCode, newValue);
            }
        }

        return resultList;

//        for (String path : resultList) {
//            trashManager.delete(path);
//        }
//
//        return resultList.size();
    }
}