package com.internship.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.internship.healthcare.models.UserAppointment;
import com.internship.healthcare.models.DoctorProfile;
import com.internship.healthcare.utils.RatingDialogHelper;
import com.internship.healthcare.utils.MessagingUtils;
import com.internship.healthcare.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * AppointmentDetailActivity.java
 * A comprehensive healthcare management Android application
 * Activity showing detailed appointment information and actions. Integrates with Firebase Authentication, Realtime Database.
 *
 * <p>Extends: {@link AppCompatActivity}</p>
 *
 * <h3>Firebase Integration:</h3>
 * <ul>
 *   <li>Authentication</li>
 *   <li>Realtime Database</li>
 * </ul>
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class AppointmentDetailActivity extends AppCompatActivity {

    private ImageButton backButton;
    private MaterialCardView avatarBackground;
    private ImageView doctorAvatar;
    private TextView doctorName;
    private TextView doctorCategory;
    private TextView doctorExperience;
    private TextView doctorRating;

    private TextView appointmentDate;
    private MaterialButton changeDateButton;
    private MaterialButton messageDoctorButton;
    private TextView totalCostAmount;
    private TextView toPayAmount;
    private TextView paymentStatus;
    private TextView problemDescription;
    private TextView statusMessage;
    private ProgressBar loadingIndicator;
    
    // Firebase
    private DatabaseReference appointmentRef;
    private DatabaseReference doctorProfileRef;
    private String appointmentId;
    private UserAppointment appointment;
    private DoctorProfile doctorProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);
        
        Intent intent = getIntent();
        appointmentId = intent.getStringExtra("appointmentId");
        
        if (appointmentId == null || appointmentId.isEmpty()) {
            Toast.makeText(this, "Invalid appointment", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initializeViews();
        
        loadAppointmentFromFirebase();
        
        setupListeners();
    }
    
    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
    
        avatarBackground = findViewById(R.id.avatar_background);
        doctorAvatar = findViewById(R.id.doctor_avatar);
        doctorName = findViewById(R.id.doctor_name);
        doctorCategory = findViewById(R.id.doctor_category);
        doctorExperience = findViewById(R.id.doctor_experience);
        doctorRating = findViewById(R.id.doctor_rating);
        appointmentDate = findViewById(R.id.appointment_date);
        changeDateButton = findViewById(R.id.change_date_button);
        messageDoctorButton = findViewById(R.id.message_doctor_button);
        totalCostAmount = findViewById(R.id.total_cost_amount);
        toPayAmount = findViewById(R.id.to_pay_amount);
        paymentStatus = findViewById(R.id.payment_status);
        problemDescription = findViewById(R.id.problem_description);
        statusMessage = findViewById(R.id.status_message);
        loadingIndicator = findViewById(R.id.loading_indicator);
    }
    
    private void loadAppointmentFromFirebase() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }
        
        appointmentRef = FirebaseDatabase.getInstance()
                .getReference("appointments")
    
                .child(appointmentId);
        
        appointmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    appointment = snapshot.getValue(UserAppointment.class);
                    if (appointment != null) {
                        checkAndUpdateAppointmentStatus();
                        
                        loadDoctorProfile();
                    } else {
                        if (loadingIndicator != null) {
                            loadingIndicator.setVisibility(View.GONE);
                        }
                        Toast.makeText(AppointmentDetailActivity.this, 
                                "Failed to load appointment details", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    finish();
    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                Toast.makeText(AppointmentDetailActivity.this, 
                        "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    
    }
    
    private void loadDoctorProfile() {
        if (appointment == null || appointment.getDoctorId() == null) {
            displayAppointmentData();
            return;
        }
        
        doctorProfileRef = FirebaseDatabase.getInstance()
                .getReference("doctorProfiles")
                .child(appointment.getDoctorId());
        
        doctorProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                
                if (snapshot.exists()) {
                    doctorProfile = snapshot.getValue(DoctorProfile.class);
                }
                
                displayAppointmentData();
            }
    

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                displayAppointmentData();
            }
        });
    }
    
    private void checkAndUpdateAppointmentStatus() {
        if (appointment == null || appointment.getStatus() == null) return;
        
        // Only update approved appointments (not pending ones)
    
        String currentStatus = appointment.getStatus();
        if (!"approved".equals(currentStatus)) {
            return;
        }
        
        if (isAppointmentDatePassed()) {
            appointmentRef.child("patientCounted").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean patientCounted = snapshot.getValue(Boolean.class);
                    
                    if (patientCounted == null || !patientCounted) {
                        appointmentRef.child("status").setValue("completed");
                        appointmentRef.child("patientCounted").setValue(true);
    
                        
                        // Increment doctor's patient count
                        if (appointment.getDoctorId() != null) {
                            incrementDoctorPatientCount(appointment.getDoctorId());
                        }
                        
                        appointment.setStatus("completed");
                        displayAppointmentData();
                        
                        showRatingDialogIfNeeded();
                        
                        Toast.makeText(AppointmentDetailActivity.this, 
                                "Appointment marked as completed", Toast.LENGTH_SHORT).show();
                    } else {
                        // Just update status if already counted
                        appointmentRef.child("status").setValue("completed")
                                .addOnSuccessListener(aVoid -> {
    
                                    appointment.setStatus("completed");
                                    displayAppointmentData();
                                    
                                    showRatingDialogIfNeeded();
                                });
                    }
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AppointmentDetailActivity.this, 
                            "Failed to check appointment status: " + error.getMessage(), 
                            Toast.LENGTH_SHORT).show();
    
                }
            });
        }
    }
    
    private void incrementDoctorPatientCount(String doctorId) {
        DatabaseReference doctorRef = FirebaseDatabase.getInstance()
                .getReference("doctorProfiles")
                .child(doctorId)
                .child("totalPatients");

        doctorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
    
                Integer currentCount = snapshot.getValue(Integer.class);
                if (currentCount == null) {
                    currentCount = 0;
                }
                doctorRef.setValue(currentCount + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    
    private void showRatingDialogIfNeeded() {
        if (appointment == null || appointment.isRatingGiven()) {
            return; // Already rated
        }
        
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            return;
        }
        
        String currentUserId = auth.getCurrentUser().getUid();
        if (!currentUserId.equals(appointment.getUserId())) {
            return; // Current user is not the patient
        }
        
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users").child(currentUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child("name").getValue(String.class);
                if (userName == null) userName = "Anonymous";
                
    
                RatingDialogHelper.showRatingDialog(
                    AppointmentDetailActivity.this,
                    appointmentId,
                    appointment.getDoctorId(),
                    appointment.getDoctorName(),
                    currentUserId,
                    userName,
                    rating -> {
                        // Rating submitted successfully
                        appointment.setRatingGiven(true);
                    }
                );
            }
    
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    
    private boolean isAppointmentDatePassed() {
        if (appointment == null) return false;
        
        try {
            String dateStr = appointment.getAppointmentDate();
    
            String timeStr = appointment.getAppointmentTime();
            
            if (dateStr == null || dateStr.isEmpty()) {
                return false;
            }
            
            // Combine date and time
            String dateTimeStr = dateStr;
            if (timeStr != null && !timeStr.isEmpty()) {
                dateTimeStr += " " + timeStr;
            }
            
            // Try different date formats - PRIORITIZE formats with year
    
            SimpleDateFormat[] formats = {
                new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.ENGLISH),
                new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH),
                new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH),
                new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH),
                new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            };
            
            Date appointmentDateTime = null;
    
            for (SimpleDateFormat format : formats) {
                try {
                    appointmentDateTime = format.parse(dateTimeStr);
                    if (appointmentDateTime != null) break;
                } catch (ParseException e) {
                    // Try next format
                }
            }
            
            if (appointmentDateTime != null) {
                Date now = new Date();
                return now.after(appointmentDateTime);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    
    private void displayAppointmentData() {
        if (appointment == null) return;
        
        // 1. Set doctor name (from appointment, not current user)
        if (appointment.getDoctorName() != null && !appointment.getDoctorName().isEmpty()) {
            doctorName.setText(appointment.getDoctorName());
        } else {
            doctorName.setText("Doctor");
        }
        
        if (appointment.getDoctorImage() != null && !appointment.getDoctorImage().isEmpty()) {
            Glide.with(this)
                    .load(appointment.getDoctorImage())
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(doctorAvatar);
        } else {
            // Use default icon with padding and tint
            Glide.with(this)
                    .load(R.drawable.ic_profile)
                    .circleCrop()
                    .into(doctorAvatar);
    
            doctorAvatar.setColorFilter(getResources().getColor(android.R.color.white));
            int padding = (int) (24 * getResources().getDisplayMetrics().density);
            doctorAvatar.setPadding(padding, padding, padding, padding);
        }
        
        // 2. Set doctor specialty
        if (appointment.getDoctorSpeciality() != null && !appointment.getDoctorSpeciality().isEmpty()) {
            doctorCategory.setText(appointment.getDoctorSpeciality());
        } else if (doctorProfile != null && doctorProfile.getSpecialty() != null) {
    
            doctorCategory.setText(doctorProfile.getSpecialty());
        } else {
            doctorCategory.setText("General Physician");
        }
        
        // 3. Set doctor experience from doctor profile
        if (doctorProfile != null && doctorProfile.getExperienceYears() > 0) {
            doctorExperience.setText(doctorProfile.getExperienceYears() + " Years");
        } else {
            doctorExperience.setText("N/A");
        }
        
        if (doctorProfile != null && doctorProfile.getRating() > 0) {
            doctorRating.setText(String.format("%.1f", doctorProfile.getRating()));
        } else {
            doctorRating.setText("N/A");
        }
        
        // 4. Set appointment date and time
        String dateStr = appointment.getAppointmentDate() != null ? appointment.getAppointmentDate() : "";
        String timeStr = appointment.getAppointmentTime() != null ? appointment.getAppointmentTime() : "";
        
        if (!dateStr.isEmpty() && !timeStr.isEmpty()) {
            appointmentDate.setText(dateStr + ", " + timeStr);
        } else if (!dateStr.isEmpty()) {
            appointmentDate.setText(dateStr);
        } else {
            appointmentDate.setText("Not specified");
        }
        
        // 5. Set appointment fees (consultation fee from appointment or doctor profile)
        double fee = appointment.getConsultationFee();
        if (fee <= 0 && doctorProfile != null) {
            fee = doctorProfile.getConsultationFee();
        }
        if (fee <= 0) {
            fee = 90.0; // Default fallback
        }
        
        totalCostAmount.setText("₹" + (int)fee);
        toPayAmount.setText("₹" + (int)fee);
        
        // 6. Payment status always shows "Paid Successfully"
        paymentStatus.setText("Paid Successfully");
        paymentStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        
        String status = appointment.getStatus();
        boolean isCompleted = "completed".equals(status);
        boolean isCancelled = "cancelled".equals(status) || "rejected".equals(status);
        
        if (isCompleted || isCancelled) {
    
            changeDateButton.setVisibility(View.GONE);
        } else {
            changeDateButton.setVisibility(View.VISIBLE);
        }
        
        // 7. Set detailed description (reason for appointment from patient)
        String reason = appointment.getReason();
        if (reason != null && !reason.isEmpty()) {
            problemDescription.setText(reason);
        } else {
            problemDescription.setText("No specific reason provided by the patient.");
        }
        
        // 8. Set status message below description
        String statusText = getStatusMessage(status);
        if (statusMessage != null) {
            statusMessage.setText(statusText);
            statusMessage.setVisibility(View.VISIBLE);
        }
        
        // 9. Show rating dialog if appointment is completed and not yet rated
        if ("completed".equals(status) && !appointment.isRatingGiven()) {
            // Delay slightly to show dialog after UI updates
            new android.os.Handler().postDelayed(this::showRatingDialogIfNeeded, 500);
        }
    }
    
    private String getStatusMessage(String status) {
        if (status == null) return "";
        
        switch (status.toLowerCase()) {
            case "completed":
                return "✓ This appointment has been completed. Thank you for visiting!";
            case "cancelled":
            case "rejected":
                return "✗ This appointment has been cancelled.";
            case "pending":
                return "⏳ This appointment is pending approval from the doctor.";
            case "approved":
            case "scheduled":
            case "upcoming":
                return "✓ This appointment is confirmed. Please arrive 10 minutes before your scheduled time.";
            default:
                return "Status: " + status;
        }
    }
    
    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());
        
        changeDateButton.setOnClickListener(v -> {
            Toast.makeText(this, "Change date functionality coming soon", Toast.LENGTH_SHORT).show();
            // TODO: Show date picker dialog
        });
        
        // Message Doctor button
        messageDoctorButton.setOnClickListener(v -> {
            if (appointment != null && appointment.getDoctorId() != null && doctorProfile != null) {
                openChatWithDoctor();
            } else {
                Toast.makeText(this, "Doctor information not available", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Doctor card click - Navigate to doctor profile
        findViewById(R.id.doctor_card).setOnClickListener(v -> {
            if (appointment != null && appointment.getDoctorId() != null) {
                Intent intent = new Intent(AppointmentDetailActivity.this, DoctorDetailsActivity.class);
                intent.putExtra("doctorId", appointment.getDoctorId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Doctor information not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void openChatWithDoctor() {
        SessionManager sessionManager = new SessionManager(this);
        String currentUserId = sessionManager.getUserId();
        String doctorId = appointment.getDoctorId();
        
        String chatId = MessagingUtils.generateChatId(currentUserId, doctorId);
        
        String doctorName = appointment.getDoctorName();
        String doctorImage = doctorProfile.getProfileImageUrl();
        String doctorRole = "Doctor";
        
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("otherUserId", doctorId);
        intent.putExtra("otherUserName", doctorName);
        intent.putExtra("otherUserImage", doctorImage);
        intent.putExtra("otherUserRole", doctorRole);
        startActivity(intent);
    }
}

    
    
    