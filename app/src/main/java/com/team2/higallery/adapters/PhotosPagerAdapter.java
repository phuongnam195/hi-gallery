package com.team2.higallery.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.team2.higallery.R;
import com.team2.higallery.utils.FileUtils;

import java.util.ArrayList;

public class PhotosPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> imageList;
    private ClickListener clickListener;

    public PhotosPagerAdapter(Context context, ArrayList<String> imageList, ClickListener clickListener) {
        this.context = context;
        this.imageList = imageList;
        this.clickListener = clickListener;
    }

    @Override
    public int getCount() {
        if (imageList == null)
            return 0;
        return imageList.size();
    }

    private LayoutInflater layoutInflater;

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final String imagePath = imageList.get(position);

        // Đối với ảnh động (GIF)
        if (FileUtils.getExtension(imagePath).equalsIgnoreCase("gif")) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_view_fullscreen);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick();
                }
            });
            Glide.with(context).load(imagePath).fitCenter().into(imageView);
            container.addView(view);
            return view;
        }

        PhotoView photoView = new PhotoView(container.getContext());
        Uri myUri = Uri.parse(imageList.get(position));
        photoView.setImageURI(myUri);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick();
            }
        });
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // Credit: https://stackoverflow.com/questions/45666326/android-viewpager-delete-current-page-and-move-to-next
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void removeItem(int position) {
        imageList.remove(position);
        notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick();
    }
}
