package com.internship.healthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.internship.healthcare.R;
import com.internship.healthcare.models.VerificationRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * VerificationRequestAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class VerificationRequestAdapter extends RecyclerView.Adapter<VerificationRequestAdapter.ViewHolder> {

    private List<VerificationRequest> requests;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onReview(VerificationRequest request, String userId);
        void onReject(VerificationRequest request, String userId);
    }

    public VerificationRequestAdapter(List<VerificationRequest> requests, OnRequestActionListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_verification_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VerificationRequest request = requests.get(position);
        
        holder.doctorName.setText("Loading...");
        
        holder.specialty.setText(request.getSpecialty());
        
        holder.degree.setText(request.getDegree());
        holder.university.setText(request.getUniversity());
        
        holder.experience.setText(request.getExperienceYears() + " years");
        
        holder.consultationFee.setText("₹" + String.format(Locale.getDefault(), "%.0f", request.getConsultationFee()));
        
        int documentCount = request.getDocumentUrls() != null ? request.getDocumentUrls().size() : 0;
        holder.documentsCount.setText(documentCount + " document" + (documentCount != 1 ? "s" : "") + " uploaded");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String submittedDate = dateFormat.format(new Date(request.getSubmittedAt()));
        holder.submittedDate.setText("Submitted on " + submittedDate);
        // Review button click
        holder.reviewButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReview(request, request.getUserId());
            }
        });
        
        // Reject button click
        holder.rejectButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReject(request, request.getUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void updateRequests(List<VerificationRequest> newRequests) {
        this.requests = newRequests;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName, specialty, degree, university, experience;
        TextView consultationFee, documentsCount, submittedDate;
        MaterialButton reviewButton, rejectButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            specialty = itemView.findViewById(R.id.specialty);
            degree = itemView.findViewById(R.id.degree);
            university = itemView.findViewById(R.id.university);
            experience = itemView.findViewById(R.id.experience);
            consultationFee = itemView.findViewById(R.id.consultation_fee);
            documentsCount = itemView.findViewById(R.id.documents_count);
            submittedDate = itemView.findViewById(R.id.submitted_date);
            reviewButton = itemView.findViewById(R.id.review_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
}