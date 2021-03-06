package com.team2.higallery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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

public class GridPhotosAdapter extends RecyclerView.Adapter<GridPhotosAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<String> imagePaths;
    private ArrayList<Bitmap> imageBitmaps;
    protected ClickListener clickListener;
    private ArrayList<Integer> selectedIndices;
    boolean isBitmap;


    public GridPhotosAdapter(Context context, Object images, ArrayList<Integer> selectedIndices, ClickListener onClick) {
        this.context = context;
        ArrayList<?> list = (ArrayList) images;
        if (!list.isEmpty()) {
            if (list.get(0) instanceof String) {
                isBitmap = false;
                this.imagePaths = new ArrayList<>();
                for (Object obj : list) {
                    this.imagePaths.add((String) obj);
                }
            } else {
                isBitmap = true;
                this.imageBitmaps = new ArrayList<>();
                for (Object obj : list) {
                    this.imageBitmaps.add((Bitmap) obj);
                }
            }
        }
        this.selectedIndices = selectedIndices;
        this.clickListener = onClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final int defaultPadding = 2;
        final int selectedPadding = 15;

        if (isBitmap) {
            Bitmap bitmap = imageBitmaps.get(position);
            Glide.with(context).load(bitmap).override(300, 300).centerCrop().transition(DrawableTransitionOptions.withCrossFade(500)).into(holder.imageView);
        } else {
            String imagePath = imagePaths.get(position);
            Glide.with(context).load(imagePath).override(300, 300).centerCrop().transition(DrawableTransitionOptions.withCrossFade(500)).into(holder.imageView);
        }

        // N???u ???nh n??y n???m ??ang ???????c select th?? ?????i giao di???n
        if (selectedIndices.contains(position)) {
            holder.imageView.setPadding(selectedPadding,selectedPadding,selectedPadding,selectedPadding);
            holder.imageView.setColorFilter(Color.argb(150, 89, 82, 96));
        } else {
            holder.imageView.setPadding(defaultPadding,defaultPadding,defaultPadding,defaultPadding);
            holder.imageView.setColorFilter(Color.TRANSPARENT);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(position);
            }
        });

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickListener.onLongClick(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (isBitmap) {
            if (imageBitmaps != null) {
                return imageBitmaps.size();
            }
        } else {
            if (imagePaths != null) {
                return imagePaths.size();
            }
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

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
        notifyDataSetChanged();
    }

    public void setImageBitmaps(ArrayList<Bitmap> bitmaps) {
        this.imageBitmaps = bitmaps;
        notifyDataSetChanged();
    }

    public void deselectAll() {
        selectedIndices.clear();
        notifyDataSetChanged();
    }

    public void selectAll() {
        selectedIndices.clear();
        for (int i = 0; i < getItemCount(); i++) {
            selectedIndices.add(i);
        }
        notifyDataSetChanged();
    }

    public void deselect(int index) {
        selectedIndices.remove(Integer.valueOf(index));
        notifyItemChanged(index);
    }

    public void select(int index) {
        selectedIndices.add(index);
        notifyItemChanged(index);
    }

    public interface ClickListener {
        void onClick(int index);
        void onLongClick(int index);
    }
}
