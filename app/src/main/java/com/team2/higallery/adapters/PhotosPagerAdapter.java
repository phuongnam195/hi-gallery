package com.team2.higallery.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.github.chrisbanes.photoview.PhotoView;
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

    @Override
    public View instantiateItem(ViewGroup container, int position) {
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
