package com.internship.healthcare.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.internship.healthcare.R;
import com.internship.healthcare.models.Doctor;

import java.util.List;
/**
 * DoctorAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private List<Doctor> doctors;
    private OnDoctorClickListener listener;
    
    // Array of avatar background colors
    private final String[] avatarColors = {
        "#64B5F6", // Light Blue
        "#F06292", // Pink
        "#FFB74D", // Orange
        "#BA68C8", // Purple
        "#4DB6AC", // Teal
        "#81C784", // Green
        "#FFD54F", // Yellow
        "#FF8A65"  // Coral
    };
    
    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }
    
    public DoctorAdapter(List<Doctor> doctors, OnDoctorClickListener listener) {
        this.doctors = doctors;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor_card, parent, false);
        return new DoctorViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        
        holder.doctorName.setText(doctor.getName());
        
        holder.doctorSpeciality.setText(doctor.getSpeciality());
        
        holder.consultationFee.setText(doctor.getFormattedFee());
        if (holder.doctorRating != null) {
            holder.doctorRating.setText(doctor.getFormattedRating());
        }
        
        String imageUrl = doctor.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(holder.doctorAvatar);
            holder.avatarBackground.setCardBackgroundColor(Color.TRANSPARENT);
            holder.doctorAvatar.setPadding(0, 0, 0, 0);
        } else {
            String color = avatarColors[position % avatarColors.length];
            holder.avatarBackground.setCardBackgroundColor(Color.parseColor(color));
            holder.doctorAvatar.setImageResource(R.drawable.ic_profile);
            holder.doctorAvatar.setColorFilter(Color.WHITE);
            int padding = (int) (12 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
            holder.doctorAvatar.setPadding(padding, padding, padding, padding);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoctorClick(doctor);
            }
        });
        
        holder.arrowButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoctorClick(doctor);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return doctors.size();
    }
    
    public void updateData(List<Doctor> newDoctors) {
        this.doctors = newDoctors;
        notifyDataSetChanged();
    }
    
    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView avatarBackground;
        ImageView doctorAvatar;
        TextView doctorName;
        TextView doctorSpeciality;
        TextView consultationFee;
        TextView doctorRating;
        ImageButton arrowButton;
        
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarBackground = itemView.findViewById(R.id.avatar_background);
            doctorAvatar = itemView.findViewById(R.id.doctor_avatar);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpeciality = itemView.findViewById(R.id.doctor_speciality);
            consultationFee = itemView.findViewById(R.id.consultation_fee);
            doctorRating = itemView.findViewById(R.id.doctor_rating);
            arrowButton = itemView.findViewById(R.id.arrow_button);
        }
    }
}