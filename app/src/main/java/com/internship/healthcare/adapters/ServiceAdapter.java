package com.internship.healthcare.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.internship.healthcare.R;
import com.internship.healthcare.models.Service;

import java.util.List;
/**
 * ServiceAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;
    private OnServiceClickListener listener;
    
    // Color pairs: background color and icon tint color
    private final String[][] colorPairs = {
        {"#E3F2FD", "#2196F3"}, // Light Blue
        {"#F3E5F5", "#9C27B0"}, // Light Purple
        {"#FFF3E0", "#FF9800"}, // Light Orange
        {"#E8F5E9", "#4CAF50"}, // Light Green
        {"#FCE4EC", "#E91E63"}, // Light Pink
        {"#FFF9C4", "#FBC02D"}  // Light Yellow
    };

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public ServiceAdapter(List<Service> serviceList, OnServiceClickListener listener) {
        this.serviceList = serviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getName());
        holder.serviceIcon.setImageResource(service.getIconResId());
        
        String[] colors = colorPairs[position % colorPairs.length];
        holder.iconBackground.setCardBackgroundColor(Color.parseColor(colors[0]));
        holder.serviceIcon.setColorFilter(Color.parseColor(colors[1]));
        
    /**
     * Callback invoked when create view holder in patient information and records
     *
     * @param parent parent
     * @param viewType integer value
     * @return the ServiceViewHolder result of the operation
     */
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceClick(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView iconBackground;
        ImageView serviceIcon;
        TextView serviceName;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            iconBackground = itemView.findViewById(R.id.icon_background);
            serviceIcon = itemView.findViewById(R.id.service_icon);
            serviceName = itemView.findViewById(R.id.service_name);
        }
    }
}