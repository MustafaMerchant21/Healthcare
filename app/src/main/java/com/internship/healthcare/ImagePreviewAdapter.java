package com.internship.healthcare;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
/**
 * ImagePreviewAdapter.java
 * A comprehensive healthcare management Android application
 * RecyclerView adapter managing image preview data binding and view recycling.
 *
 * <p>Extends: {@link RecyclerView.Adapter}</p>
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ImagePreviewViewHolder> {

    private List<Uri> imageUris;
    private OnImageDeleteListener deleteListener;

    public interface OnImageDeleteListener {
        void onImageDelete(int position);
    }


    public ImagePreviewAdapter(List<Uri> imageUris, OnImageDeleteListener deleteListener) {
        this.imageUris = imageUris;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ImagePreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_preview, parent, false);
        return new ImagePreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePreviewViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        holder.bind(imageUri, position);
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }
    

    class ImagePreviewViewHolder extends RecyclerView.ViewHolder {
        private final ImageView previewImage;
        private final ImageButton deleteButton;

        public ImagePreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            previewImage = itemView.findViewById(R.id.preview_image);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(Uri imageUri, int position) {
            previewImage.setImageURI(imageUri);
    
            
            deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onImageDelete(position);
                }
            });
        }
    }

    public void removeImage(int position) {
        imageUris.remove(position);
    
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, imageUris.size());
    }
}

    
    