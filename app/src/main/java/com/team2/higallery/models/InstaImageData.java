package com.team2.higallery.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.team2.higallery.activities.GetInstaActivity;
import com.team2.higallery.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class InstaImageData {
    // Một hình ảnh có 3 kích thước (nhỏ - trung bình - lớn)
    ArrayList<String> sources;      // URL
    ArrayList<Integer> widths;
    ArrayList<Integer> heights;
    ArrayList<Boolean> downloadFlags;

    public InstaImageData() {
        sources = new ArrayList<>();
        widths = new ArrayList<>();
        heights = new ArrayList<>();
        downloadFlags = new ArrayList<>();
    }

    public String source(InstaSize size) {
        return sources.get(size.ordinal());
    }

    public String resolution(InstaSize size) {
        int i = size.ordinal();
        return widths.get(i) + " x " + heights.get(i);
    }

    public String download(InstaSize size, Context context) {
        if (downloadFlags.get(size.ordinal())) {
            return "Downloaded";
        }
        String url = source(size);
        try {
            Bitmap downloadedBitmap = Glide.with(context).asBitmap().load(url).submit().get();
            String fileName = BitmapUtils.save(downloadedBitmap, context);
            if (fileName != null) {
                downloadFlags.set(size.ordinal(), true);
            }
            return fileName;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean parse(String json) {
        int typenameIdx = json.indexOf("\"__typename\"");
        int preQMIdx = json.indexOf('\"', typenameIdx + "\"__typename\"".length() + 1);
        int sufQMIdx = json.indexOf('\"', preQMIdx + 1);
        String typename = json.substring(preQMIdx + 1, sufQMIdx);

        if (!typename.equals("GraphImage")) {
            return false;
        }

        int drIdx = json.indexOf("\"display_resources\"");          // "display_resources"
        int preSBIdx = json.indexOf('[', drIdx);                // [
        int sufSBIdx = json.indexOf(']', preSBIdx);             // ]
        json = json.substring(preSBIdx, sufSBIdx + 1);

        int startIdx = 0;
        while (true) {
            int srcIdx = json.indexOf("\"src\"", startIdx);     // "src"
            if (srcIdx == -1) {
                break;
            }
            preQMIdx = json.indexOf('\"', srcIdx + "\"src\"".length() + 1);     // "
            sufQMIdx = json.indexOf('\"', preQMIdx + 1);                        // "
            String source = json.substring(preQMIdx + 1, sufQMIdx);
            sources.add(source);

            int widthIdx = json.indexOf("\"config_width\"", sufQMIdx);
            int colonIdx = json.indexOf(':', widthIdx);
            int width = 0;
            for (int i = colonIdx + 1; '0' <= json.charAt(i) && json.charAt(i) <= '9'; i++) {
                width = width * 10 + json.charAt(i) - '0';
            }
            widths.add(width);

            int heightIdx = json.indexOf("\"config_height\"", sufQMIdx);
            colonIdx = json.indexOf(':', heightIdx);
            int height = 0;
            for (int i = colonIdx + 1; '0' <= json.charAt(i) && json.charAt(i) <= '9'; i++) {
                height = height * 10 + json.charAt(i) - '0';
            }
            heights.add(height);

            downloadFlags.add(false);

            startIdx = colonIdx + 1;
        }

        return true;
    }
}
