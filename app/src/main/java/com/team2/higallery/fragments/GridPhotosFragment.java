package com.team2.higallery.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2.higallery.Configuration;
import com.team2.higallery.activities.PhotoActivity;
import com.team2.higallery.adapters.GridPhotosAdapter;
import com.team2.higallery.R;
import com.team2.higallery.interfaces.FragmentCallbacks;
import com.team2.higallery.models.FavoriteImages;
import com.team2.higallery.providers.ImagesProvider;
import com.team2.higallery.providers.TrashManager;

import java.util.ArrayList;

public class GridPhotosFragment extends Fragment implements FragmentCallbacks {
    Context context;
    GridPhotosAdapter gridPhotosAdapter;

    ArrayList<String> imagePaths;
    ArrayList<Integer> selectedIndices = new ArrayList<>();     // Chứa các index của ảnh trong imagePaths đang được select
    ActivityCallbacks callbacks;                                // Truyền thông tin từ fragment về activity
    String source;
    int columns;

    RecyclerView recyclerView;

    public GridPhotosFragment() {
    }

    public GridPhotosFragment(ArrayList<String> imagePaths, String source) {
        this.imagePaths = imagePaths;
        this.source = source;
        this.columns = 4;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callbacks = (ActivityCallbacks) context;
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
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.fragment_grid_photos, null);

        recyclerView = (RecyclerView) layout.findViewById(R.id.photos_recycler_view);
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new GridLayoutManager(context.getApplicationContext(), columns));

        gridPhotosAdapter = new GridPhotosAdapter(context, imagePaths, selectedIndices, new GridPhotosAdapter.ClickListener() {
            @Override
            public void onClick(int index) {
                // Nếu đang không ở chế độ Selection (có ảnh đang được chọn)
                // thì mở ảnh đó
                if (selectedIndices.isEmpty()) {
                    Log.d("CLICK TIME", String.valueOf(System.currentTimeMillis()));
                    Intent intent = new Intent(context, PhotoActivity.class);
                    intent.putExtra("currentIndex", index);
                    intent.putExtra("source", source);
                    if (!source.equals("all_photos")) {
                        intent.putExtra("imagePaths", imagePaths);
                    }
                    startActivity(intent);
                } else {
                    // Ngược lại, nếu đang ở chế độ Selection,
                    // và ta click lại 1 ảnh đang được select, thì bỏ select cho ảnh đó
                    if (selectedIndices.contains(index)) {
                        callbacks.sendFromFragmentToActivity("grid_photos", "deselect", index);
                        gridPhotosAdapter.deselect(index);
                    } else {
                        // nếu click 1 ảnh mới, thì thêm ảnh đó vào danh sách ảnh được select
                        callbacks.sendFromFragmentToActivity("grid_photos", "select", index);
                        gridPhotosAdapter.select(index);
                    }
                }
            }

            @Override
            public void onLongClick(int index) {
                // Nếu long click một ảnh chưa có trong danh sách được select, thì thêm ảnh vào danh sách select
                if (!selectedIndices.contains(index)) {
                    callbacks.sendFromFragmentToActivity("grid_photos", "select", index);
                    gridPhotosAdapter.select(index);
                }
            }
        });
        recyclerView.setAdapter(gridPhotosAdapter);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Thông báo cho activity là fragment này vừa mới resume, cần reload, nhưng activity có reload không thì tùy
        callbacks.sendFromFragmentToActivity("grid_photos", "should_reload", -1);

        if (recyclerView != null && gridPhotosAdapter != null && columns != Configuration.gridTypeColumns) {
            columns = Configuration.gridTypeColumns;
            recyclerView.setLayoutManager(new GridLayoutManager(context.getApplicationContext(), columns));
            recyclerView.setAdapter(gridPhotosAdapter);
        }
    }

    @Override
    public void sendFromActivityToFragment(String sender, String header, int value) {
        switch (header) {
            case "deselect_all":    // Bỏ select tất cả (tắt chế độ Selection)
                gridPhotosAdapter.deselectAll();
                break;
            case "select_all":      // Select tất cả ảnh
                gridPhotosAdapter.selectAll();
                break;
            case "remove":          // Vừa có các ảnh (được select) đã bị xóa
                selectedIndices.clear();
                gridPhotosAdapter.setImagePaths(ImagesProvider.allImages);
                imagePaths = ImagesProvider.allImages;
                break;
            case "update_all_photos":       // Cập nhật lại danh sách tất cả ảnh
                imagePaths = ImagesProvider.allImages;
                gridPhotosAdapter.setImagePaths(imagePaths);
                break;
            case "update_favorite_images":  // Cập nhật lại danh sách ảnh được yêu thích
                imagePaths = FavoriteImages.list;
                gridPhotosAdapter.setImagePaths(imagePaths);
                break;
            case "update_deleted_images":   // Cập nhật lại danh sách ảnh bị xóa
                imagePaths = TrashManager.getInstance(context).getAllPaths();
                gridPhotosAdapter.setImagePaths(imagePaths);
                break;
            case "update_album_images":     // Cập nhật lại danh sách ảnh của album có index là value
                imagePaths = ImagesProvider.allAlbums.get(value).getImages();
                gridPhotosAdapter.setImagePaths(imagePaths);
                break;
        }
    }

    public interface ActivityCallbacks {
        public void sendFromFragmentToActivity(String sender, String header, int value);
    }
}