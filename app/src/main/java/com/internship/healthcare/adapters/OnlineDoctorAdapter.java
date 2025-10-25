package com.internship.healthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.internship.healthcare.R;

import java.util.List;
/**
 * OnlineDoctorAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class OnlineDoctorAdapter extends RecyclerView.Adapter<OnlineDoctorAdapter.OnlineDoctorViewHolder> {

    private final List<Integer> doctorImages;
    private final OnDoctorClickListener clickListener;

    public interface OnDoctorClickListener {
        void onDoctorClick(int doctorImageResId);
    }
    public OnlineDoctorAdapter(List<Integer> doctorImages, OnDoctorClickListener clickListener) {
        this.doctorImages = doctorImages;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public OnlineDoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_online_doctor, parent, false);
        return new OnlineDoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineDoctorViewHolder holder, int position) {
        int doctorImageResId = doctorImages.get(position);
        holder.bind(doctorImageResId, clickListener);
    }

    @Override
    public int getItemCount() {
        return doctorImages.size();
    }

    public static class OnlineDoctorViewHolder extends RecyclerView.ViewHolder {
        private final ImageView doctorAvatar;

        public OnlineDoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorAvatar = itemView.findViewById(R.id.online_doctor_avatar);
        }

        public void bind(int doctorImageResId, OnDoctorClickListener clickListener) {
            doctorAvatar.setImageResource(doctorImageResId);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onDoctorClick(doctorImageResId);
                }
            });
        }
    }
}