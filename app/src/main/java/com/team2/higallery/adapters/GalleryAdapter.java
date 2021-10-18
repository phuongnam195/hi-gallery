package com.team2.higallery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.team2.higallery.R;

import java.io.File;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    private List<String> imagesPath;
    protected PhotoClickListener photoClickListener;

    public GalleryAdapter(Context context, List<String> imagesPath, PhotoClickListener photoClickListener) {
        this.context = context;
        this.imagesPath = imagesPath;
        this.photoClickListener = photoClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.thumbnail_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imagesPath.get(position);
        Glide.with(holder.imageView.getContext()).load(new File(imagePath)).override(300, 300).centerCrop().into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoClickListener.onPhotoClick(imagePath);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (imagesPath != null) {
            return imagesPath.size();
        }

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public interface PhotoClickListener {
        void onPhotoClick(String path);
    }
}
