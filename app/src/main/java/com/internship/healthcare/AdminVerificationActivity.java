package com.internship.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.adapters.VerificationRequestAdapter;
import com.internship.healthcare.models.VerificationRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * AdminVerificationActivity.java
 * A comprehensive healthcare management Android application
 * Activity handling admin verification screen and user interactions.
 *
 * <p>Extends: {@link AppCompatActivity}</p>
 * <p>Implements: {@link VerificationRequestAdapter.OnRequestActionListener}</p>
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


public class AdminVerificationActivity extends AppCompatActivity 
        implements VerificationRequestAdapter.OnRequestActionListener {

    private MaterialToolbar toolbar;
    private RecyclerView verificationRequestsRecycler;
    private LinearLayout emptyState;
    private CircularProgressIndicator progressIndicator;
    private com.google.android.material.button.MaterialButton verifyAllButton;

    private FirebaseAuth auth;

    private DatabaseReference verificationRequestsRef;
    private DatabaseReference usersRef;

    private VerificationRequestAdapter adapter;
    private List<VerificationRequest> verificationRequests;
    private Map<String, String> userIdToRequestIdMap; // Maps userId to requestId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_verification);

        auth = FirebaseAuth.getInstance();
        verificationRequestsRef = FirebaseDatabase.getInstance().getReference("verificationRequests");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        verificationRequests = new ArrayList<>();
        userIdToRequestIdMap = new HashMap<>();

        initializeViews();

        setupToolbar();

        setupRecyclerView();

        loadPendingRequests();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        verificationRequestsRecycler = findViewById(R.id.verification_requests_recycler);
    
        emptyState = findViewById(R.id.empty_state);
        progressIndicator = findViewById(R.id.progress_indicator);
        verifyAllButton = findViewById(R.id.verify_all_button);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new VerificationRequestAdapter(verificationRequests, this);
        verificationRequestsRecycler.setLayoutManager(new LinearLayoutManager(this));
        verificationRequestsRecycler.setAdapter(adapter);
        
        // Setup Verify All button
        verifyAllButton.setOnClickListener(v -> showVerifyAllConfirmation());
    }

    private void loadPendingRequests() {
        showProgress(true);

        verificationRequestsRef.orderByChild("status").equalTo("pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
    
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showProgress(false);
                        verificationRequests.clear();
                        userIdToRequestIdMap.clear();

                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            VerificationRequest request = requestSnapshot.getValue(VerificationRequest.class);
                            if (request != null) {
                                verificationRequests.add(request);
    
                                userIdToRequestIdMap.put(request.getUserId(), requestSnapshot.getKey());
                            }
                        }

                        adapter.updateRequests(verificationRequests);
                        updateEmptyState();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
    
                        showProgress(false);
                        Toast.makeText(AdminVerificationActivity.this,
                                "Failed to load requests: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    
    private void updateEmptyState() {
        if (verificationRequests.isEmpty()) {
            verificationRequestsRecycler.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            verifyAllButton.setVisibility(View.GONE);
        } else {
            verificationRequestsRecycler.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            verifyAllButton.setVisibility(View.VISIBLE);
        }
    
    }
    
    private void showVerifyAllConfirmation() {
        int requestCount = verificationRequests.size();
        
        new MaterialAlertDialogBuilder(this)
                .setTitle("Verify All Requests")
                .setMessage("Are you sure you want to verify all " + requestCount + " pending requests?\n\nThis action cannot be undone.")
                .setPositiveButton("Verify All", (dialog, which) -> verifyAllRequests())
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void verifyAllRequests() {
        if (auth.getCurrentUser() == null) return;
        if (verificationRequests.isEmpty()) {
            Toast.makeText(this, "No pending requests to verify", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showProgress(true);
        verifyAllButton.setEnabled(false);

        String adminId = auth.getCurrentUser().getUid();
        long currentTime = System.currentTimeMillis();

        int totalRequests = verificationRequests.size();
        final int[] successCount = {0};
        final int[] failureCount = {0};

        for (VerificationRequest request : verificationRequests) {
            String userId = request.getUserId();

            Map<String, Object> requestUpdates = new HashMap<>();
            requestUpdates.put("status", "approved");
            requestUpdates.put("reviewedBy", adminId);
            requestUpdates.put("reviewedAt", currentTime);

            verificationRequestsRef.child(userIdToRequestIdMap.get(userId))
                    .updateChildren(requestUpdates)
                    .addOnSuccessListener(aVoid -> {
                        Map<String, Object> userUpdates = new HashMap<>();
                        userUpdates.put("doctorVerificationStatus", "approved");
                        userUpdates.put("isVerified", true);

                        usersRef.child(userId).updateChildren(userUpdates)
                                .addOnSuccessListener(aVoid2 -> {
                                    successCount[0]++;
                                    checkVerifyAllCompletion(totalRequests, successCount[0], failureCount[0]);
                                })
                                .addOnFailureListener(e -> {
                                    failureCount[0]++;
                                    checkVerifyAllCompletion(totalRequests, successCount[0], failureCount[0]);
                                });
                    })
                    .addOnFailureListener(e -> {
                        failureCount[0]++;
                        checkVerifyAllCompletion(totalRequests, successCount[0], failureCount[0]);
                    });
        }
    }
    
    private void checkVerifyAllCompletion(int total, int success, int failure) {
        if (success + failure >= total) {
            showProgress(false);
            verifyAllButton.setEnabled(true);
            
            String message;
            if (failure == 0) {
                message = "Successfully verified all " + success + " requests!";
            } else {
                message = "Verified " + success + " requests. Failed: " + failure;
            }
            
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onReview(VerificationRequest request, String userId) {
        Intent intent = new Intent(this, VerificationReviewActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("requestId", userIdToRequestIdMap.get(userId));
        startActivity(intent);
    }

    @Override
    public void onReject(VerificationRequest request, String userId) {
        showRejectionDialog(userId);
    }

    private void showRejectionDialog(String userId) {
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
                        rejectVerification(userId, reason);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    

    private void rejectVerification(String userId, String rejectionReason) {
        if (auth.getCurrentUser() == null) return;

        String adminId = auth.getCurrentUser().getUid();
        long currentTime = System.currentTimeMillis();

        Map<String, Object> requestUpdates = new HashMap<>();
        requestUpdates.put("status", "rejected");
        requestUpdates.put("reviewedBy", adminId);
        requestUpdates.put("reviewedAt", currentTime);
        requestUpdates.put("rejectionReason", rejectionReason);

    
        verificationRequestsRef.child(userId).updateChildren(requestUpdates)
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("doctorVerificationStatus", "rejected");
                    userUpdates.put("isVerified", false);

                    usersRef.child(userId).updateChildren(userUpdates)
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(this, "Application rejected", 
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to update user status", 
    
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to reject application: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    
    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}

    
    