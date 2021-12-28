package com.team2.higallery.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.team2.higallery.R;
import com.team2.higallery.models.InstaImageData;
import com.team2.higallery.models.InstaSize;

import java.util.ArrayList;

public class InstaListViewAdapter extends ArrayAdapter<InstaImageData> {
    Context context;
    ArrayList<InstaImageData> imageList;
    int layoutResource;

    public InstaListViewAdapter(Context context, int resource, ArrayList<InstaImageData> imageList) {
        super(context, resource, imageList);
        this.context = context;
        this.layoutResource = resource;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(layoutResource, null);

        InstaImageData image = imageList.get(position);

        ImageView imageView = (ImageView) row.findViewById(R.id.image_insta);
        Glide.with(context).load(image.source(InstaSize.SMALL)).into(imageView);
        Button btnSmall = (Button) row.findViewById(R.id.small_button_insta);
        Button btnMedium = (Button) row.findViewById(R.id.medium_button_insta);
        Button btnLarge = (Button) row.findViewById(R.id.large_button_insta);

        btnSmall.setText(image.resolution(InstaSize.SMALL));
        btnMedium.setText(image.resolution(InstaSize.MEDIUM));
        btnLarge.setText(image.resolution(InstaSize.LARGE));

        btnSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSmall.setEnabled(false);
                downloadImage(image, InstaSize.SMALL, btnSmall);
            }
        });

        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMedium.setEnabled(false);
                downloadImage(image, InstaSize.MEDIUM, btnMedium);
            }
        });

        btnLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLarge.setEnabled(false);
                downloadImage(image, InstaSize.LARGE, btnLarge);
            }
        });

        return (row);
    }

    void downloadImage(InstaImageData image, InstaSize size, Button button) {
        Toast.makeText(context, R.string.get_insta_toast_downloading, Toast.LENGTH_LONG).show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                String fileName = image.download(size, context);
                String result = context.getResources().getString(R.string.get_insta_toast_failed);
                if (fileName != null) {
                    result = context.getResources().getString(R.string.get_insta_toast_saved)
                            + " " + fileName;
                }
                final String finalResult = result;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, finalResult, Toast.LENGTH_SHORT).show();
                        if (fileName == null) {
                            button.setEnabled(true);
                        }
                    }
                });
            }
        }.start();
    }
}
