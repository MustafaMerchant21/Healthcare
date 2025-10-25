package com.internship.healthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.internship.healthcare.models.PatientInfo;
import com.google.android.material.chip.Chip;
import com.internship.healthcare.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * PatientsAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.PatientViewHolder> {

    private List<PatientInfo> patientList;
    private List<PatientInfo> patientListFull; // For search functionality
    private OnPatientClickListener listener;

    public interface OnPatientClickListener {
        void onPatientClick(PatientInfo patient);
    }

    public PatientsAdapter(OnPatientClickListener listener) {
        this.patientList = new ArrayList<>();
        this.patientListFull = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        PatientInfo patient = patientList.get(position);
        holder.bind(patient);
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public void setPatients(List<PatientInfo> patients) {
        this.patientList = patients;
        this.patientListFull = new ArrayList<>(patients);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        patientList.clear();
        
        if (query == null || query.trim().isEmpty()) {
            patientList.addAll(patientListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (PatientInfo patient : patientListFull) {
                if (patient.getPatientName().toLowerCase().contains(lowerCaseQuery) ||
                    patient.getPatientPhone().toLowerCase().contains(lowerCaseQuery) ||
                    (patient.getPatientEmail() != null && 
                     patient.getPatientEmail().toLowerCase().contains(lowerCaseQuery))) {
                    patientList.add(patient);
                }
            }
        }
        notifyDataSetChanged();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPatientImage;
        private TextView tvPatientName;
        private TextView tvPatientPhone;
        private TextView tvTotalAppointments;
        private TextView tvLastVisit;
        private Chip chipStatus;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPatientImage = itemView.findViewById(R.id.ivPatientImage);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvPatientPhone = itemView.findViewById(R.id.tvPatientPhone);
            tvTotalAppointments = itemView.findViewById(R.id.tvTotalAppointments);
            tvLastVisit = itemView.findViewById(R.id.tvLastVisit);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }

        public void bind(PatientInfo patient) {
            tvPatientName.setText(patient.getPatientName());

            tvPatientPhone.setText(patient.getPatientPhone());

            tvTotalAppointments.setText(patient.getFormattedTotalAppointments());

            if (patient.getLastVisitDate() > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(new Date(patient.getLastVisitDate()));
                tvLastVisit.setText("Last: " + formattedDate);
            } else {
                tvLastVisit.setText("Last visit: Never");
            }

            long currentTime = System.currentTimeMillis();
            long thirtyDaysAgo = currentTime - (30L * 24 * 60 * 60 * 1000); // 30 days in milliseconds

            if (patient.getLastVisitDate() > thirtyDaysAgo) {
                chipStatus.setText("Active");
                chipStatus.setChipBackgroundColorResource(R.color.green_light);
            } else if (patient.getTotalAppointments() > 0) {
                chipStatus.setText("Inactive");
                chipStatus.setChipBackgroundColorResource(R.color.orange_light);
            } else {
                chipStatus.setText("New");
                chipStatus.setChipBackgroundColorResource(R.color.blue_light);
            }

            if (patient.getPatientImageUrl() != null && !patient.getPatientImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(patient.getPatientImageUrl())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .into(ivPatientImage);
            } else {
                ivPatientImage.setImageResource(R.drawable.ic_profile);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPatientClick(patient);
                }
            });
        }
    }
}
