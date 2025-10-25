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
import com.internship.healthcare.models.AppointmentDetail;

import java.util.List;
/**
 * AppointmentDetailAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class AppointmentDetailAdapter extends RecyclerView.Adapter<AppointmentDetailAdapter.AppointmentViewHolder> {

    private List<AppointmentDetail> appointments;
    private OnAppointmentClickListener listener;
    
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
    
    public interface OnAppointmentClickListener {
        void onAppointmentClick(AppointmentDetail appointment);
        void onCallClick(AppointmentDetail appointment);
    }
    
    public AppointmentDetailAdapter(List<AppointmentDetail> appointments, OnAppointmentClickListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment_detail, parent, false);
        return new AppointmentViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentDetail appointment = appointments.get(position);
        
        holder.doctorName.setText(appointment.getDoctorName());
        
        holder.doctorCategory.setText(appointment.getDoctorCategory());
        
        holder.appointmentDateTime.setText(appointment.getFormattedDateTime());

        String color = avatarColors[position % avatarColors.length];
        holder.avatarBackground.setCardBackgroundColor(Color.parseColor(color));
        
        if (appointment.getDoctorImage() != null && !appointment.getDoctorImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(appointment.getDoctorImage())
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(holder.doctorAvatar);
        } else {
            // Use default icon with padding and tint
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_profile)
                    .circleCrop()
                    .into(holder.doctorAvatar);
            holder.doctorAvatar.setColorFilter(holder.itemView.getContext().getResources().getColor(android.R.color.white));
            int padding = (int) (24 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
            holder.doctorAvatar.setPadding(padding, padding, padding, padding);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAppointmentClick(appointment);
            }
        });

        holder.callButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCallClick(appointment);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return appointments.size();
    }
    
    public void updateData(List<AppointmentDetail> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }
    
    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView avatarBackground;
        ImageView doctorAvatar;
        TextView doctorName;
        TextView doctorCategory;
        TextView appointmentDateTime;
        ImageButton callButton;
        
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarBackground = itemView.findViewById(R.id.avatar_background);
            doctorAvatar = itemView.findViewById(R.id.doctor_avatar);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorCategory = itemView.findViewById(R.id.doctor_category);
            appointmentDateTime = itemView.findViewById(R.id.appointment_date_time);
            callButton = itemView.findViewById(R.id.call_button);
        }
    }
}

