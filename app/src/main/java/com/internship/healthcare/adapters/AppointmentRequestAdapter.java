package com.internship.healthcare.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.internship.healthcare.R;
import com.internship.healthcare.models.UserAppointment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/**
 * AppointmentRequestAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class AppointmentRequestAdapter extends RecyclerView.Adapter<AppointmentRequestAdapter.ViewHolder> {

    private List<UserAppointment> appointments = new ArrayList<>();
    private OnRequestActionListener listener;
    private final String[] avatarColors = {
        "#64B5F6", "#F06292", "#FFB74D", "#BA68C8",
        "#4DB6AC", "#81C784", "#FFD54F", "#FF8A65"
    };
    public interface OnRequestActionListener {
        void onAccept(UserAppointment appointment);
        void onReject(UserAppointment appointment);
    }

    public AppointmentRequestAdapter(OnRequestActionListener listener) {
        this.listener = listener;
    }

    public void setAppointments(List<UserAppointment> appointments) {
        this.appointments = appointments != null ? appointments : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserAppointment appointment = appointments.get(position);
        holder.bind(appointment);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView patientAvatar, patientName, patientPhone, requestStatus;
        TextView appointmentDate, appointmentTime, appointmentReason, consultationFee;
        MaterialButton btnAccept, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientAvatar = itemView.findViewById(R.id.patient_avatar);
            patientName = itemView.findViewById(R.id.patient_name);
            patientPhone = itemView.findViewById(R.id.patient_phone);
            requestStatus = itemView.findViewById(R.id.request_status);
            appointmentDate = itemView.findViewById(R.id.appointment_date);
            appointmentTime = itemView.findViewById(R.id.appointment_time);
            appointmentReason = itemView.findViewById(R.id.appointment_reason);
            consultationFee = itemView.findViewById(R.id.consultation_fee);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }

        void bind(UserAppointment appointment) {
            String name = appointment.getPatientName() != null ? appointment.getPatientName() : "Patient";
            patientName.setText(name);
            
            String phone = appointment.getPatientPhone();
            if (phone != null && !phone.isEmpty()) {
                patientPhone.setText(phone);
                patientPhone.setVisibility(View.VISIBLE);
            } else {
                patientPhone.setVisibility(View.GONE);
            }

            String initials = getInitials(name);
            patientAvatar.setText(initials);
            int colorIndex = Math.abs(name.hashCode()) % avatarColors.length;
            patientAvatar.setBackgroundColor(Color.parseColor(avatarColors[colorIndex]));

            // Format and set date
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                appointmentDate.setText(appointment.getAppointmentDate());
            } catch (Exception e) {
                appointmentDate.setText(appointment.getAppointmentDate());
            }

            appointmentTime.setText(appointment.getAppointmentTime());

            String reason = appointment.getReason();
            if (reason != null && !reason.isEmpty()) {
                appointmentReason.setText(reason);
            } else {
                appointmentReason.setText("No reason provided");
            }

            consultationFee.setText(String.format(Locale.getDefault(), "₹%.0f", appointment.getConsultationFee()));

            String status = appointment.getStatus();
            updateStatusBadge(status);

            if ("pending".equals(status)) {
                btnAccept.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.VISIBLE);
                
                btnAccept.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onAccept(appointment);
                    }
                });

                btnReject.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onReject(appointment);
                    }
                });
            } else {
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
            }
        }

        private void updateStatusBadge(String status) {
            switch (status) {
                case "pending":
                    requestStatus.setText("Pending");
                    requestStatus.setTextColor(Color.parseColor("#FF9800"));
                    requestStatus.setBackgroundResource(R.drawable.badge_background);
                    break;
                case "approved":
                    requestStatus.setText("Approved");
                    requestStatus.setTextColor(Color.parseColor("#4CAF50"));
                    requestStatus.setBackgroundResource(R.drawable.badge_background);
                    break;
                case "rejected":
                    requestStatus.setText("Rejected");
                    requestStatus.setTextColor(Color.parseColor("#F44336"));
                    requestStatus.setBackgroundResource(R.drawable.badge_background);
                    break;
                default:
                    requestStatus.setText(status);
                    requestStatus.setTextColor(Color.parseColor("#757575"));
            }
        }

        private String getInitials(String name) {
            if (name == null || name.isEmpty()) return "P";
            String[] parts = name.split(" ");
            if (parts.length >= 2) {
                return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
            }
            return name.substring(0, Math.min(2, name.length())).toUpperCase();
        }
    }
}