package com.example.higallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.activity_view_image, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String image = imagesPath.get(position);
        Glide.with(context).load(image).into(holder.image);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoClickListener.onPhotoClick(image);
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

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageView);
        }
    }

    public interface PhotoClickListener {
        void onPhotoClick(String path);
    }
}
