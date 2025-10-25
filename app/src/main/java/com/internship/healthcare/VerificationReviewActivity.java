package com.internship.healthcare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.adapters.DocumentUrlAdapter;
import com.internship.healthcare.models.DoctorProfile;
import com.internship.healthcare.models.VerificationRequest;

import java.util.HashMap;
import java.util.Map;
/**
 * VerificationReviewActivity.java
 * A comprehensive healthcare management Android application
 * Activity for admin review of doctor verification requests. Integrates with Firebase Authentication, Realtime Database.
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


public class VerificationReviewActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView applicantName, applicantEmail;
    private TextView detailSpecialty, detailDegree, detailUniversity;
    private TextView detailExperience, detailConsultationFee;
    private RecyclerView documentsRecycler;
    private MaterialButton rejectButton, approveButton;
    private CircularProgressIndicator progressIndicator;
/**

 * 
 * @author Mustafa Merchant
 * @version 1.0
 */

    private FirebaseAuth auth;
    private DatabaseReference verificationRequestsRef;
    private DatabaseReference usersRef;
    private DatabaseReference doctorProfilesRef;

    private String userId;
    private String requestId;
    private VerificationRequest currentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_review);

        userId = getIntent().getStringExtra("userId");
        requestId = getIntent().getStringExtra("requestId");

        if (userId == null || requestId == null) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        auth = FirebaseAuth.getInstance();
        verificationRequestsRef = FirebaseDatabase.getInstance().getReference("verificationRequests");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        doctorProfilesRef = FirebaseDatabase.getInstance().getReference("doctorProfiles");

        initializeViews();

        setupToolbar();

    /**
     * Called when the activity is first created. Initializes the activity and sets up the UI.
     *
     * @param savedInstanceState bundle containing key-value pairs
     */
        loadVerificationRequest();
        loadUserInfo();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        applicantName = findViewById(R.id.applicant_name);
        applicantEmail = findViewById(R.id.applicant_email);
        detailSpecialty = findViewById(R.id.detail_specialty);
        detailDegree = findViewById(R.id.detail_degree);
        detailUniversity = findViewById(R.id.detail_university);
        detailExperience = findViewById(R.id.detail_experience);
        detailConsultationFee = findViewById(R.id.detail_consultation_fee);
        documentsRecycler = findViewById(R.id.documents_recycler);
        rejectButton = findViewById(R.id.reject_button);
        approveButton = findViewById(R.id.approve_button);
        progressIndicator = findViewById(R.id.progress_indicator);

        rejectButton.setOnClickListener(v -> showRejectionDialog());
        approveButton.setOnClickListener(v -> showApprovalConfirmation());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Initializes views in patient information and records
     */
    private void loadVerificationRequest() {
        showProgress(true);

        verificationRequestsRef.child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgress(false);

                if (snapshot.exists()) {
                    currentRequest = snapshot.getValue(VerificationRequest.class);
                    if (currentRequest != null) {
                        displayRequestDetails();
                    }
                } else {
                    Toast.makeText(VerificationReviewActivity.this,
                            "Request not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

    /**
     * Configures and prepares toolbar in patient information and records
     */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgress(false);
                Toast.makeText(VerificationReviewActivity.this,
                        "Failed to load request: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Loads verification request from data source in patient information and records from Firebase Realtime Database
     */
    private void loadUserInfo() {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
    /**
     * Callback invoked when data change in patient information and records
     *
     * @param snapshot snapshot
     */

                    if (name != null) {
                        applicantName.setText(name);
                    }
                    if (email != null) {
                        applicantEmail.setText(email);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Silently fail, not critical
            }
        });
    }

    private void displayRequestDetails() {
        detailSpecialty.setText(currentRequest.getSpecialty());
        detailDegree.setText(currentRequest.getDegree());
    /**
     * Callback invoked when cancelled in patient information and records
     *
     * @param error error
     */
        detailUniversity.setText(currentRequest.getUniversity());
        detailExperience.setText(currentRequest.getExperienceYears() + " years");
        detailConsultationFee.setText("₹" + String.valueOf((int) currentRequest.getConsultationFee()));

        if (currentRequest.getDocumentUrls() != null && !currentRequest.getDocumentUrls().isEmpty()) {
            DocumentUrlAdapter adapter = new DocumentUrlAdapter(
                    currentRequest.getDocumentUrls(),
                    this::openDocument
            );
            documentsRecycler.setLayoutManager(new LinearLayoutManager(this));
            documentsRecycler.setAdapter(adapter);
        }
    }
    /**
     * Loads user info from data source in patient information and records from Firebase Realtime Database
     */

    private void openDocument(String documentUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(documentUrl));
        startActivity(intent);
    /**
     * Callback invoked when data change in patient information and records
     *
     * @param snapshot snapshot
     */
    }

    private void showRejectionDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rejection_reason, null);
        TextInputEditText reasonInput = dialogView.findViewById(R.id.rejection_reason_input);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Reject Application")
                .setMessage("Please provide a reason for rejection:")
                .setView(dialogView)
                .setPositiveButton("Reject", (dialog, which) -> {
                    String reason = reasonInput.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(this, "Please enter a rejection reason",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        rejectApplication(reason);
                    }
                })
    /**
     * Callback invoked when cancelled in patient information and records
     *
     * @param error error
     */
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showApprovalConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Approve Application")
                .setMessage("Are you sure you want to approve this doctor verification request? This will grant the user doctor privileges.")
                .setPositiveButton("Approve", (dialog, which) -> approveApplication())
                .setNegativeButton("Cancel", null)
    /**
     * Performs display request details operation and updates UI accordingly.
     */
                .show();
    }

    private void approveApplication() {
        if (auth.getCurrentUser() == null) return;

        String adminId = auth.getCurrentUser().getUid();
        long currentTime = System.currentTimeMillis();

        showProgress(true);

        // 1. Update verification request
        Map<String, Object> requestUpdates = new HashMap<>();
        requestUpdates.put("status", "approved");
        requestUpdates.put("reviewedBy", adminId);
        requestUpdates.put("reviewedAt", currentTime);

        verificationRequestsRef.child(requestId).updateChildren(requestUpdates)
                .addOnSuccessListener(aVoid -> {
    /**
     * Performs open document operation and updates UI accordingly.
     *
     * @param documentUrl string value
     */
                    // 2. Update user status
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("role", "doctor");
                    userUpdates.put("isVerified", true);
                    userUpdates.put("doctorVerificationStatus", "approved");

                    usersRef.child(userId).updateChildren(userUpdates)
                            .addOnSuccessListener(aVoid2 -> {
                                // 3. Create doctor profile
                                createDoctorProfile(adminId, currentTime);
    /**
     * Displays rejection dialog to user in patient information and records
     */
                            })
                            .addOnFailureListener(e -> {
                                showProgress(false);
                                Toast.makeText(this, "Failed to update user: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(this, "Failed to update request: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void createDoctorProfile(String adminId, long verifiedAt) {
        DoctorProfile profile = new DoctorProfile();
        profile.setUserId(userId);
        profile.setSpecialty(currentRequest.getSpecialty());
        profile.setDegree(currentRequest.getDegree());
        profile.setUniversity(currentRequest.getUniversity());
        profile.setExperienceYears(currentRequest.getExperienceYears());
        profile.setConsultationFee(currentRequest.getConsultationFee());
        profile.setCertificateUrls(currentRequest.getDocumentUrls());
    /**
     * Displays approval confirmation to user in patient information and records
     */
        profile.setVerifiedBy(adminId);
        profile.setVerifiedAt(verifiedAt);
        profile.setTotalPatients(0);
        profile.setRating(0.0);
        profile.setTotalRatings(0);

        doctorProfilesRef.child(userId).setValue(profile)
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    Toast.makeText(this, "Application approved successfully!",
                            Toast.LENGTH_LONG).show();
    /**
     * Approves application request in patient information and records
     */
                    finish();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(this, "Failed to create doctor profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectApplication(String rejectionReason) {
        if (auth.getCurrentUser() == null) return;

        String adminId = auth.getCurrentUser().getUid();
        long currentTime = System.currentTimeMillis();

        showProgress(true);

        Map<String, Object> requestUpdates = new HashMap<>();
        requestUpdates.put("status", "rejected");
        requestUpdates.put("reviewedBy", adminId);
        requestUpdates.put("reviewedAt", currentTime);
        requestUpdates.put("rejectionReason", rejectionReason);

        verificationRequestsRef.child(requestId).updateChildren(requestUpdates)
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("doctorVerificationStatus", "rejected");
                    userUpdates.put("isVerified", false);

                    usersRef.child(userId).updateChildren(userUpdates)
                            .addOnSuccessListener(aVoid2 -> {
                                showProgress(false);
                                Toast.makeText(this, "Application rejected",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                showProgress(false);
                                Toast.makeText(this, "Failed to update user: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
    /**
     * Creates new doctor profile instance in patient information and records to Firebase Realtime Database
     *
     * @param adminId string value
     * @param verifiedAt long integer value
     */
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(this, "Failed to reject application: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        approveButton.setEnabled(!show);
        rejectButton.setEnabled(!show);
    }
}

    /**
     * Rejects application request in patient information and records
     *
     * @param rejectionReason string value
     */
    /**
     * Displays progress to user in patient information and records
     *
     * @param show boolean flag
     */