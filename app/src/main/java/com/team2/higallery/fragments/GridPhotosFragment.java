package com.team2.higallery.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2.higallery.activities.PhotoActivity;
import com.team2.higallery.adapters.GridPhotosAdapter;
import com.team2.higallery.R;
import com.team2.higallery.interfaces.FragmentCallbacks;
import com.team2.higallery.models.FavoriteImages;
import com.team2.higallery.models.TrashManager;
import com.team2.higallery.utils.DataUtils;

import java.util.ArrayList;

public class GridPhotosFragment extends Fragment implements FragmentCallbacks {
    private final int PORTRAIT_COLUMNS = 4;
    private final int LANDSCAPE_COLUMNS = 6;        // Hiện tại đang bị lỗi xoay màn hình

    Context context;
    GridPhotosAdapter gridPhotosAdapter;

    ArrayList<String> imagePaths;
    ArrayList<Integer> selectedIndices = new ArrayList<>();     // Chứa các index của ảnh trong imagePaths đang được select
    ActivityCallbacks callbacks;                                // Truyền thông tin từ fragment về activity

    public GridPhotosFragment(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
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

        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.photos_recycler_view);
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(context.getApplicationContext(), PORTRAIT_COLUMNS));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context.getApplicationContext(), LANDSCAPE_COLUMNS));
        }

        gridPhotosAdapter = new GridPhotosAdapter(context, imagePaths, selectedIndices, new GridPhotosAdapter.ClickListener() {
            @Override
            public void onClick(int index) {
                // Nếu đang không ở chế độ Selection (có ảnh đang được chọn)
                // thì mở ảnh đó
                if (selectedIndices.isEmpty()) {
                    Intent intent = new Intent(context, PhotoActivity.class);
                    intent.putStringArrayListExtra("imagePaths", imagePaths);
                    intent.putExtra("currentIndex", index);
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
                gridPhotosAdapter.setImagePaths(DataUtils.allImages);
                imagePaths = DataUtils.allImages;
                break;
            case "update_all_photos":       // Cập nhật lại danh sách tất cả ảnh
                imagePaths = DataUtils.allImages;
                gridPhotosAdapter.setImagePaths(imagePaths);
                break;
            case "update_favorite_images":  // Cập nhật lại danh sách ảnh được yêu thích
                FavoriteImages.load(context);
                imagePaths = FavoriteImages.get();
                gridPhotosAdapter.setImagePaths(imagePaths);
                break;
            case "update_deleted_images":   // Cập nhật lại danh sách ảnh bị xóa
                imagePaths = TrashManager.getInstance(context).getAllPaths();
                gridPhotosAdapter.setImagePaths(imagePaths);
                break;
            case "update_album_images":     // Cập nhật lại danh sách ảnh của album có index là value
                imagePaths = DataUtils.allAlbums.get(value).getImages();
                gridPhotosAdapter.setImagePaths(imagePaths);
                break;
        }
    }

    public interface ActivityCallbacks {
        public void sendFromFragmentToActivity(String sender, String header, int value);
    }
}