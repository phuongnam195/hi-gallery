package com.team2.higallery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.team2.higallery.R;
import com.team2.higallery.models.Album;

import java.util.ArrayList;

public class GridAlbumsAdapter extends RecyclerView.Adapter<GridAlbumsAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<Album> albums;
    protected ClickListener clickListener;

    public GridAlbumsAdapter(Context context, ArrayList<Album> albums, ClickListener onClick) {
        this.context = context;
        this.albums = albums;
        this.clickListener = onClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,  @SuppressLint("RecyclerView") int position) {
        Album album = albums.get(position);

        String avatar = album.getAvatar();
        Glide.with(context).load(avatar).override(500, 500).centerCrop().transition(DrawableTransitionOptions.withCrossFade(500)).into(holder.albumAvatar);

        String title = album.getTitle();
        holder.albumTitle.setText(title);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView albumAvatar;
        TextView albumTitle;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumAvatar = itemView.findViewById(R.id.album_avatar);
            albumTitle = itemView.findViewById(R.id.album_title);
            container = (LinearLayout) itemView;
        }
    }

    public interface ClickListener {
        void onClick(int index);
    }
}
