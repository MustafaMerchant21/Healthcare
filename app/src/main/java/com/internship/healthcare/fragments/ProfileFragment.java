package com.internship.healthcare.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.AccountInfoActivity;
import com.internship.healthcare.AdminVerificationActivity;
import com.internship.healthcare.DoctorScheduleActivity;
import com.internship.healthcare.DoctorVerificationActivity;
import com.internship.healthcare.EditDoctorProfileActivity;
import com.internship.healthcare.LocationActivity;
import com.internship.healthcare.PaymentMethodsActivity;
import com.internship.healthcare.R;
import com.internship.healthcare.SignInScreen;
import com.internship.healthcare.utils.SessionManager;
/**
 * ProfileFragment.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.fragments
 * Fragment displaying user profile and account settings.
 *
 * <p>Extends: {@link Fragment}</p>
 *
 * <h3>Firebase Integration:</h3>
 * <ul>
 *   <li>Authentication</li>
 *   <li>Realtime Database</li>
 * </ul>
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class ProfileFragment extends Fragment {

    private View skeletonLoading;
    private View profileContent;
    private ImageView profileImage;
    private TextView profileInitials;
    private MaterialCardView profileImageCard;
    private TextView userName;
    private TextView userEmail;
    private MaterialCardView accountInfoCard;
    private MaterialCardView paymentMethodsCard;
    private MaterialCardView locationCard;
    private MaterialCardView supportCard;
    private MaterialCardView logoutCard;
    
    // Doctor-specific views
    private MaterialCardView becomeDoctorCard;
    private MaterialCardView verificationStatusCard;
    private MaterialCardView doctorStatsCard;
    private MaterialCardView editPublicProfileCard;
    private MaterialCardView manageScheduleCard;
    private MaterialCardView adminVerificationCard;
    private TextView totalPatientsText;
    private TextView ratingText;
    
    private SessionManager sessionManager;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private DatabaseReference doctorProfilesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        skeletonLoading = view.findViewById(R.id.skeleton_loading);
        profileContent = view.findViewById(R.id.profile_content);
        
        showSkeleton();
        
        sessionManager = new SessionManager(requireContext());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        doctorProfilesRef = FirebaseDatabase.getInstance().getReference("doctorProfiles");
        
        profileImage = view.findViewById(R.id.profile_image);
        profileInitials = view.findViewById(R.id.profile_initials);
        profileImageCard = view.findViewById(R.id.profile_image_card);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        accountInfoCard = view.findViewById(R.id.account_info_card);
        paymentMethodsCard = view.findViewById(R.id.payment_methods_card);
        locationCard = view.findViewById(R.id.location_card);
        supportCard = view.findViewById(R.id.support_card);
        logoutCard = view.findViewById(R.id.logout_card);
        adminVerificationCard = view.findViewById(R.id.admin_verification);
        
        becomeDoctorCard = view.findViewById(R.id.become_doctor_card);
        verificationStatusCard = view.findViewById(R.id.verification_status_card);
        doctorStatsCard = view.findViewById(R.id.doctor_stats_card);
        editPublicProfileCard = view.findViewById(R.id.edit_public_profile_card);
        manageScheduleCard = view.findViewById(R.id.manage_schedule_card);
        totalPatientsText = view.findViewById(R.id.total_patients_text);
        ratingText = view.findViewById(R.id.rating_text);
        
        loadUserData();
        checkUserRole();
        
        setupClickListeners();
    }
    
    private void loadUserData() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            String email = auth.getCurrentUser().getEmail();
            
            if (email != null) {
                userEmail.setText(email);
            }
            
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isAdded() || getActivity() == null) {
                        return;
                    }
                    
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        if (name != null && !name.isEmpty()) {
                            userName.setText(name);
                        } else {
                            userName.setText("User");
                            name = "User";
                        }
                        
                        String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                        String role = snapshot.child("role").getValue(String.class);
                        
                        // If doctor, try to get image from doctor profile
                        if ("doctor".equals(role)) {
                            loadDoctorProfileImage(userId, name);
                        } else {
                            loadProfileImage(profileImageUrl, name);
                        }
                        
                        hideSkeleton();
                    } else {
                        userName.setText("User");
                        showInitials("User");
                        hideSkeleton();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                    userName.setText("User");
                    showInitials("User");
                    hideSkeleton();
                }
            });
        }
    }
    
    private void showSkeleton() {
        if (skeletonLoading != null && profileContent != null) {
            skeletonLoading.setVisibility(View.VISIBLE);
            profileContent.setVisibility(View.GONE);
        }
    }
    
    private void hideSkeleton() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded() && skeletonLoading != null && profileContent != null) {
                skeletonLoading.setVisibility(View.GONE);
                profileContent.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }
    
    private void loadDoctorProfileImage(String userId, String name) {
        doctorProfilesRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || getActivity() == null) {
                    return;
                }
                
                if (snapshot.exists()) {
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    loadProfileImage(profileImageUrl, name);
                } else {
                    // No doctor profile, check user profile
                    databaseReference.child(userId).child("profileImageUrl")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    if (!isAdded() || getActivity() == null) {
                                        return;
                                    }
                                    String profileImageUrl = userSnapshot.getValue(String.class);
                                    loadProfileImage(profileImageUrl, name);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    if (isAdded() && getActivity() != null) {
                                        showInitials(name);
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded() && getActivity() != null) {
                    showInitials(name);
                }
            }
        });
    }
    
    private void loadProfileImage(String imageUrl, String name) {
        if (!isAdded() || getActivity() == null || getContext() == null) {
            return;
        }
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("null")) {
            try {
                Glide.with(requireContext())
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .error(R.drawable.ic_profile)
                        .into(profileImage);
                
                profileImage.setVisibility(View.VISIBLE);
                profileInitials.setVisibility(View.GONE);
            } catch (Exception e) {
                // Fallback to initials if Glide fails
                showInitials(name);
            }
        } else {
            showInitials(name);
        }
    }
    
    private void showInitials(String name) {
        if (!isAdded() || getActivity() == null || profileImage == null || profileInitials == null) {
            return;
        }
        profileImage.setVisibility(View.GONE);
        profileInitials.setVisibility(View.VISIBLE);
        
        String initials = getInitials(name);
        profileInitials.setText(initials);
        
        int color = generateColorFromName(name);
        profileImageCard.setCardBackgroundColor(color);
    }
    
    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "U";
        }
        
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        } else if (parts.length == 1 && parts[0].length() > 0) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return "U";
    }
    
    private int generateColorFromName(String name) {
        int hash = name.hashCode();
        String[] colors = {
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#FFA07A", 
            "#98D8C8", "#6C5CE7", "#A29BFE", "#FD79A8",
            "#FDCB6E", "#00B894", "#00CEC9", "#0984E3"
        };
        int index = Math.abs(hash) % colors.length;
        return Color.parseColor(colors[index]);
    }
    
    private void checkUserRole() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String role = snapshot.child("role").getValue(String.class);
                        Boolean isVerified = snapshot.child("isVerified").getValue(Boolean.class);
                        String verificationStatus = snapshot.child("doctorVerificationStatus").getValue(String.class);
                        
                        if (role == null) role = "patient";
                        if (isVerified == null) isVerified = false;
                        if (verificationStatus == null) verificationStatus = "none";
                        
                        // Show admin verification card only for admins
                        if ("admin".equals(role)) {
                            adminVerificationCard.setVisibility(View.VISIBLE);
                        } else {
                            adminVerificationCard.setVisibility(View.GONE);
                        }
                        
                        if ("doctor".equals(role) && isVerified) {
                            // Verified doctor - show stats and edit profile
                            setupVerifiedDoctorView();
                            loadDoctorStats();
                        } else if ("doctor".equals(role) && !isVerified) {
                            // Doctor with pending verification
                            setupPendingDoctorView(verificationStatus);
                        } else {
                            // Patient - show "Become a Doctor" option
                            setupPatientView();
                        }
                    } else {
                        // Default to patient view
                        adminVerificationCard.setVisibility(View.GONE);
                        setupPatientView();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to check user role", Toast.LENGTH_SHORT).show();
                    setupPatientView(); // Default to patient view on error
                }
            });
        }
    }
    
    private void setupPatientView() {
        becomeDoctorCard.setVisibility(View.VISIBLE);
        verificationStatusCard.setVisibility(View.GONE);
        doctorStatsCard.setVisibility(View.GONE);
        editPublicProfileCard.setVisibility(View.GONE);
        manageScheduleCard.setVisibility(View.GONE);
    }
    
    private void setupPendingDoctorView(String verificationStatus) {
        becomeDoctorCard.setVisibility(View.GONE);
        doctorStatsCard.setVisibility(View.GONE);
        editPublicProfileCard.setVisibility(View.GONE);
        manageScheduleCard.setVisibility(View.GONE);
        
        if ("pending".equals(verificationStatus) || "rejected".equals(verificationStatus)) {
            verificationStatusCard.setVisibility(View.VISIBLE);
        } else {
            verificationStatusCard.setVisibility(View.GONE);
        }
    }
    
    private void setupVerifiedDoctorView() {
        becomeDoctorCard.setVisibility(View.GONE);
        verificationStatusCard.setVisibility(View.GONE);
        doctorStatsCard.setVisibility(View.VISIBLE);
        editPublicProfileCard.setVisibility(View.VISIBLE);
        manageScheduleCard.setVisibility(View.VISIBLE);
    }
    
    private void loadDoctorStats() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            
            doctorProfilesRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Integer totalPatientsInt = snapshot.child("totalPatients").getValue(Integer.class);
                        Long totalPatientsLong = snapshot.child("totalPatients").getValue(Long.class);
                        
                        if (totalPatientsInt != null) {
                            totalPatientsText.setText(String.valueOf(totalPatientsInt));
                        } else if (totalPatientsLong != null) {
                            totalPatientsText.setText(String.valueOf(totalPatientsLong));
                        } else {
                            totalPatientsText.setText("0");
                        }
                        
                        Double rating = snapshot.child("rating").getValue(Double.class);
                        if (rating != null && rating > 0) {
                            ratingText.setText(String.format("%.1f", rating));
                        } else {
                            ratingText.setText("0.0");
                        }
                    } else {
                        // Profile doesn't exist yet, show default values
                        totalPatientsText.setText("0");
                        ratingText.setText("0.0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load doctor stats", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void setupClickListeners() {
        // Account Info
        accountInfoCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AccountInfoActivity.class);
            startActivity(intent);
        });
        
        // Payment Methods
        paymentMethodsCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PaymentMethodsActivity.class);
            startActivity(intent);
        });
        
        locationCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), LocationActivity.class);
            startActivity(intent);
        });
        
        // Become a Doctor
        becomeDoctorCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), DoctorVerificationActivity.class);
            startActivity(intent);
        });
        
        // Edit Public Profile (for doctors)
        editPublicProfileCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditDoctorProfileActivity.class);
            startActivity(intent);
        });
        manageScheduleCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), DoctorScheduleActivity.class);
            startActivity(intent);
        });
        
        // Support
        supportCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Support - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigation to Support Activity
            // Intent intent = new Intent(requireActivity(), SupportActivity.class);
            // startActivity(intent);
        });

        adminVerificationCard.setOnClickListener(v ->{
            startActivity(new Intent(requireActivity(), AdminVerificationActivity.class));
        });
        
        // Logout
        logoutCard.setOnClickListener(v -> {
            performLogout();
        });
    }
    
    private void performLogout() {
        sessionManager.logout();
        // Sign out from Firebase
        auth.signOut();
        
        Intent intent = new Intent(requireActivity(), SignInScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}