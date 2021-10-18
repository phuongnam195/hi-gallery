package com.example.higallery.fragments;

import android.Manifest;
import android.content.Context;
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

import com.example.higallery.GalleryAdapter;
import com.example.higallery.ImagesGallery;
import com.example.higallery.MainActivity;
import com.example.higallery.R;

import java.util.List;

public class AllImagesFragment extends Fragment {
    private final int PORTRAIT_COLUMNS = 4;
    private final int LANDSCAPE_COLUMNS = 6;

    private static final int MY_READ_PERMISSION_CODE = 101;

    GalleryAdapter galleryAdapter;
    List<String> images;
    Context context;


    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AllImagesFragment() {
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
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.fragment_all_images, null);

        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.photos_recycler_view);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        } else {
//            recyclerView.setHasFixedSize(true);
            if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                recyclerView.setLayoutManager(new GridLayoutManager(context.getApplicationContext(), PORTRAIT_COLUMNS));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context.getApplicationContext(), LANDSCAPE_COLUMNS));
            }

            images = ImagesGallery.listOfImages(context);
            galleryAdapter = new GalleryAdapter(context, images, new GalleryAdapter.PhotoClickListener() {
                @Override
                public void onPhotoClick(String path) {

                }
            });
            recyclerView.setAdapter(galleryAdapter);
        }

        return layout;
    }
}