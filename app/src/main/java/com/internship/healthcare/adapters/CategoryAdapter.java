package com.internship.healthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.internship.healthcare.R;
import com.internship.healthcare.models.DoctorCategory;

import java.util.List;
/**
 * CategoryAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<DoctorCategory> categories;
    private OnCategoryClickListener listener;
    
    // Array of colors for cards
    private final String[] cardColors = {
        "#E3F2FD", // Light Blue
        "#F3E5F5", // Light Purple
        "#E8F5E9", // Light Green
        "#FFF3E0", // Light Orange
        "#FCE4EC", // Light Pink
        "#F1F8E9", // Light Lime
        "#E0F2F1", // Light Teal
        "#FFF9C4"  // Light Yellow
    };
    
    public interface OnCategoryClickListener {
        void onCategoryClick(DoctorCategory category);
    }
    
    public CategoryAdapter(List<DoctorCategory> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_card, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        DoctorCategory category = categories.get(position);
        
        holder.priceText.setText(category.getFormattedPrice());
        
        holder.categoryName.setText(category.getCategoryName());
        
        holder.doctorCount.setText(category.getDoctorCountText());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
        
        holder.arrowButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }
    
    /**
     * Callback invoked when bind view holder in patient information and records
     *
     * @param holder holder
     * @param position integer value
     */
    @Override
    public int getItemCount() {
        return categories.size();
    }
    
    public void updateData(List<DoctorCategory> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }
    
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView categoryCard;
        TextView priceText;
        TextView perVisitText;
        TextView categoryName;
        TextView doctorCount;
        ImageButton arrowButton;
        
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryCard = itemView.findViewById(R.id.category_card);
            priceText = itemView.findViewById(R.id.price_text);
            perVisitText = itemView.findViewById(R.id.per_visit_text);
            categoryName = itemView.findViewById(R.id.category_name);
            doctorCount = itemView.findViewById(R.id.doctor_count);
            arrowButton = itemView.findViewById(R.id.arrow_button);
        }
    }
}