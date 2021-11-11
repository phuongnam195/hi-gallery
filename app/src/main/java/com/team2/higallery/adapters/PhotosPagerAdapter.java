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
    private ArrayList<String> imageList;
    private Context context;

    public PhotosPagerAdapter(ArrayList<String> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
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
}
