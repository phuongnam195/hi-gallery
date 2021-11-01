package com.team2.higallery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.team2.higallery.R;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<String> imagePaths;
    protected PhotoClickListener photoClickListener;

    public GalleryAdapter(Context context, ArrayList<String> imagePaths, PhotoClickListener photoClickListener) {
        this.context = context;
        this.imagePaths = imagePaths;
        this.photoClickListener = photoClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String imagePath = imagePaths.get(position);
        Glide.with(context).load(imagePath).override(300, 300).centerCrop().transition(DrawableTransitionOptions.withCrossFade(500)).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoClickListener.onPhotoClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (imagePaths != null) {
            return imagePaths.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }

    public interface PhotoClickListener {
        void onPhotoClick(int index);
    }
}
