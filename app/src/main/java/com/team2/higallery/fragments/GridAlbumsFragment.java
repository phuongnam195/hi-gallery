package com.team2.higallery.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2.higallery.activities.AlbumActivity;
import com.team2.higallery.R;
import com.team2.higallery.adapters.GridAlbumsAdapter;
import com.team2.higallery.interfaces.FragmentCallbacks;
import com.team2.higallery.models.Album;
import com.team2.higallery.utils.DataUtils;

import java.util.ArrayList;

public class GridAlbumsFragment extends Fragment implements FragmentCallbacks {
    private final int PORTRAIT_COLUMNS = 3;

    Context context;
    GridAlbumsAdapter gridAlbumsAdapter;
    ArrayList<Album> albums;

    public GridAlbumsFragment() {
        DataUtils.divideAllImagesToAlbums();
        this.albums = DataUtils.allAlbums;
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
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.fragment_grid_albums, null);

        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.albums_recycler_view);
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new GridLayoutManager(context.getApplicationContext(), PORTRAIT_COLUMNS));

        gridAlbumsAdapter = new GridAlbumsAdapter(context, albums, new GridAlbumsAdapter.ClickListener() {
            @Override
            public void onClick(int index) {
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("albumIndex", index);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(gridAlbumsAdapter);

        return layout;
    }

    @Override
    public void sendFromActivityToFragment(String sender, String header, int value) {
        switch (header) {
            case "update":
                this.albums = DataUtils.allAlbums;
                gridAlbumsAdapter.notifyDataSetChanged();
                break;
        }
    }
}