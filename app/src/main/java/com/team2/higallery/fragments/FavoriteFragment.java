package com.team2.higallery.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2.higallery.MainActivity;
import com.team2.higallery.R;
import com.team2.higallery.activities.PhotoActivity;
import com.team2.higallery.adapters.GalleryAdapter;
import com.team2.higallery.models.FavoriteImages;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {
    private final int PORTRAIT_COLUMNS = 4;
    private final int LANDSCAPE_COLUMNS = 6;

    private static final int MY_READ_PERMISSION_CODE = 101;

    GalleryAdapter galleryAdapter;
    Context context;

    public FavoriteFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
        } catch (IllegalStateException e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: Cần xác định có cần change hay không
        if (galleryAdapter != null) {
            galleryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.fragment_favorite, null);

        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.photos_recycler_view);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        } else {
            recyclerView.setHasFixedSize(true);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerView.setLayoutManager(new GridLayoutManager(context.getApplicationContext(), PORTRAIT_COLUMNS));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context.getApplicationContext(), LANDSCAPE_COLUMNS));
            }

            ArrayList<String> favoriteImagePaths = FavoriteImages.list;

            galleryAdapter = new GalleryAdapter(context, favoriteImagePaths, new GalleryAdapter.PhotoClickListener() {
                @Override
                public void onPhotoClick(int index) {
                    Intent intent = new Intent(context, PhotoActivity.class);
                    intent.putStringArrayListExtra("imagePaths", favoriteImagePaths);
                    intent.putExtra("currentIndex", index);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(galleryAdapter);
        }

        return layout;
    }
}