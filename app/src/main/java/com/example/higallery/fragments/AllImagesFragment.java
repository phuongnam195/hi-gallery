package com.example.higallery.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.higallery.GalleryAdapter;
import com.example.higallery.ImagesGallery;
import com.example.higallery.MainActivity;
import com.example.higallery.R;

import java.util.List;

public class AllImagesFragment extends Fragment {
    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    List<String> images;
    Context context;

    private static final int MY_READ_PERMISSION_CODE = 101;

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AllImagesFragment() {
    }

    public static AllImagesFragment newInstance() {
        return new AllImagesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
        } catch (IllegalStateException e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_all_images, null);

        recyclerView = layout.findViewById(R.id.recyclerview_gallery_images);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        } else {
            loadImages();
        }

        return layout;
    }

    public void loadImages() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        images = ImagesGallery.listOfImages(context);
        galleryAdapter = new GalleryAdapter(context, images, new GalleryAdapter.PhotoClickListener() {
            @Override
            public void onPhotoClick(String path) {
                //do something with photo

            }
        });
        recyclerView.setAdapter(galleryAdapter);
    }
}