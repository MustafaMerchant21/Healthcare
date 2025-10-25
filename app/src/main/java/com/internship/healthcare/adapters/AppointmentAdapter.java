package com.internship.healthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.internship.healthcare.R;
import com.internship.healthcare.models.Appointment;

import java.util.List;
/**
 * AppointmentAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private final List<Appointment> appointments;
    private final OnAppointmentClickListener clickListener;

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

/**
 * RecyclerView adapter managing appointment data binding and view recycling.
 *
 * <p>Extends: {@link RecyclerView.Adapter}</p>
 * 
 * @author Mustafa Merchant
 * @version 1.0
 */
    public AppointmentAdapter(List<Appointment> appointments, OnAppointmentClickListener clickListener) {
        this.appointments = appointments;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment_card, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.bind(appointment, clickListener);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private final ImageView doctorImage;
        private final TextView doctorName;
        private final TextView doctorSpecialty;
        private final TextView appointmentDate;
        private final TextView appointmentTime;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorImage = itemView.findViewById(R.id.doctor_image);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialty = itemView.findViewById(R.id.doctor_specialty);
            appointmentDate = itemView.findViewById(R.id.appointment_date);
            appointmentTime = itemView.findViewById(R.id.appointment_time);
        }

        public void bind(Appointment appointment, OnAppointmentClickListener clickListener) {
            doctorName.setText(appointment.getDoctorName());
            doctorSpecialty.setText(appointment.getDoctorSpecialty());
            appointmentDate.setText(appointment.getAppointmentDate());
            appointmentTime.setText(appointment.getAppointmentTime());
            doctorImage.setImageResource(appointment.getDoctorImageResId());
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onAppointmentClick(appointment);
                }
            });
        }
    }
}

