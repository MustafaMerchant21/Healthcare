package com.internship.healthcare;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.utils.DoctorNameFormatter;
import com.internship.healthcare.utils.SupabaseImageUploader;

import java.util.HashMap;
import java.util.Map;
/**
 * EditDoctorProfileActivity.java
 * A comprehensive healthcare management Android application
 * Activity handling edit doctor profile screen and user interactions.
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


public class EditDoctorProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private ImageView profileImage;
    private MaterialButton changePhotoButton, cancelButton, saveButton;
    private TextInputEditText nameInput, specialtyInput, bioInput, consultationFeeInput;
    private TextInputEditText clinicAddressInput, experienceInput, phoneInput, degreeInput, universityInput;
    private TextInputLayout nameInputLayout, specialtyInputLayout, bioInputLayout;
    private TextInputLayout consultationFeeInputLayout, clinicAddressInputLayout;

    private TextInputLayout experienceInputLayout, phoneInputLayout, degreeInputLayout, universityInputLayout;
    private CircularProgressIndicator progressIndicator;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference doctorProfilesRef;

    // Supabase
    private SupabaseImageUploader imageUploader;

    // Data
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String currentProfileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor_profile);

        auth = FirebaseAuth.getInstance();
        doctorProfilesRef = FirebaseDatabase.getInstance().getReference("doctorProfiles");

        imageUploader = new SupabaseImageUploader(this);

        initializeViews();

        setupToolbar();

        setupImagePicker();

        loadDoctorProfile();

        setupClickListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
    
        profileImage = findViewById(R.id.profile_image);
        changePhotoButton = findViewById(R.id.change_photo_button);
        nameInput = findViewById(R.id.name_input);
        nameInputLayout = findViewById(R.id.name_input_layout);
        specialtyInput = findViewById(R.id.specialty_input);
        specialtyInputLayout = findViewById(R.id.specialty_input_layout);
        experienceInput = findViewById(R.id.experience_input);
        experienceInputLayout = findViewById(R.id.experience_input_layout);
        phoneInput = findViewById(R.id.phone_input);
        phoneInputLayout = findViewById(R.id.phone_input_layout);
        bioInput = findViewById(R.id.bio_input);
        bioInputLayout = findViewById(R.id.bio_input_layout);
        consultationFeeInput = findViewById(R.id.consultation_fee_input);
        consultationFeeInputLayout = findViewById(R.id.consultation_fee_input_layout);
        clinicAddressInput = findViewById(R.id.clinic_address_input);
        clinicAddressInputLayout = findViewById(R.id.clinic_address_input_layout);
        degreeInput = findViewById(R.id.degree_input);
        degreeInputLayout = findViewById(R.id.degree_input_layout);
        universityInput = findViewById(R.id.university_input);
        universityInputLayout = findViewById(R.id.university_input_layout);
        cancelButton = findViewById(R.id.cancel_button);
        saveButton = findViewById(R.id.save_button);
        progressIndicator = findViewById(R.id.progress_indicator);
    }
    

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            profileImage.setImageURI(selectedImageUri);
                        }
                    }
                }
        );
    }

    private void setupClickListeners() {
        changePhotoButton.setOnClickListener(v -> openImagePicker());
        cancelButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> validateAndSave());
    }

    
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadDoctorProfile() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
    
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        showProgress(true);

        // First load user data (name, phone)
        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    String name = userSnapshot.child("name").getValue(String.class);
                    String phone = userSnapshot.child("phone").getValue(String.class);
                    
                    if (name != null) {
    
                        nameInput.setText(DoctorNameFormatter.removeDoctorPrefix(name));
                    }
                    
                    if (phone != null) {
                        phoneInput.setText(phone);
                    }
                }

    
                // Then load doctor profile data
                loadDoctorProfileData(userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgress(false);
                Toast.makeText(EditDoctorProfileActivity.this, 
    
                        "Failed to load user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDoctorProfileData(String userId) {
        doctorProfilesRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String specialty = snapshot.child("specialty").getValue(String.class);
                    String bio = snapshot.child("bio").getValue(String.class);
                    Double consultationFee = snapshot.child("consultationFee").getValue(Double.class);
                    String clinicAddress = snapshot.child("clinicAddress").getValue(String.class);
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    Integer experience = snapshot.child("experience").getValue(Integer.class);
    

                    if (specialty != null) {
                        specialtyInput.setText(specialty);
                    }
                    
                    if (bio != null) {
                        bioInput.setText(bio);
                    }
                    
                    if (consultationFee != null) {
                        consultationFeeInput.setText(String.valueOf(consultationFee.intValue()));
                    }
                    
                    if (clinicAddress != null) {
                        clinicAddressInput.setText(clinicAddress);
                    }
                    
                    if (experience != null) {
                        experienceInput.setText(String.valueOf(experience));
                    }
                    
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        currentProfileImageUrl = profileImageUrl;
    
                        Glide.with(EditDoctorProfileActivity.this).load(profileImageUrl).into(profileImage);
                    }

                    loadEducationDetails(userId);
                } else {
                    showProgress(false);
                    Toast.makeText(EditDoctorProfileActivity.this, 
                            "Doctor profile not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

    
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgress(false);
                Toast.makeText(EditDoctorProfileActivity.this, 
                        "Failed to load profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    
    }

    private void loadEducationDetails(String userId) {
        FirebaseDatabase.getInstance().getReference("doctorProfiles").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgress(false);
                
                if (snapshot.exists()) {
                    String degree = snapshot.child("degree").getValue(String.class);
                    String university = snapshot.child("university").getValue(String.class);
                    
                    if (degree != null) {
                        degreeInput.setText(degree);
                    }
                    
                    if (university != null) {
                        universityInput.setText(university);
                    }
                }
                // No error message if verification data doesn't exist
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgress(false);
                // Silent failure for education details
            }
        });
    }

    private void validateAndSave() {
        nameInputLayout.setError(null);
        experienceInputLayout.setError(null);
        phoneInputLayout.setError(null);
        bioInputLayout.setError(null);
        consultationFeeInputLayout.setError(null);
        clinicAddressInputLayout.setError(null);

        String name = nameInput.getText().toString().trim();
        String experienceStr = experienceInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String bio = bioInput.getText().toString().trim();
        String consultationFeeStr = consultationFeeInput.getText().toString().trim();
        String clinicAddress = clinicAddressInput.getText().toString().trim();

        // Validation
    
        boolean isValid = true;

        if (name.isEmpty()) {
            nameInputLayout.setError("Name is required");
            isValid = false;
        }

        if (experienceStr.isEmpty()) {
            experienceInputLayout.setError("Experience is required");
            isValid = false;
        }

    
        if (phone.isEmpty()) {
            phoneInputLayout.setError("Contact number is required");
            isValid = false;
        } else if (phone.length() < 10) {
            phoneInputLayout.setError("Invalid phone number");
            isValid = false;
        }

    
        if (consultationFeeStr.isEmpty()) {
            consultationFeeInputLayout.setError("Consultation fee is required");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        int experience = Integer.parseInt(experienceStr);
        double consultationFee = Double.parseDouble(consultationFeeStr);

        String doctorName = DoctorNameFormatter.formatDoctorName(name);

        saveProfileChanges(doctorName, experience, phone, bio, consultationFee, clinicAddress);
    }

    private void saveProfileChanges(String doctorName, int experience, String phone, 
                                   String bio, double consultationFee, String clinicAddress) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
    

        String userId = auth.getCurrentUser().getUid();
        showProgress(true);

        // First update user name and phone in users node
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", doctorName);
        userUpdates.put("phone", phone);

        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .updateChildren(userUpdates)
    
                .addOnSuccessListener(aVoid -> {
                    // Then update doctor profile
                    if (selectedImageUri != null) {
                        uploadProfileImage(userId, imageUrl -> {
                            if (imageUrl != null) {
                                updateDoctorProfile(userId, experience, bio, consultationFee, clinicAddress, imageUrl);
                            } else {
                                showProgress(false);
                                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        updateDoctorProfile(userId, experience, bio, consultationFee, clinicAddress, currentProfileImageUrl);
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(this, "Failed to update user info: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadProfileImage(String userId, OnImageUploadedListener listener) {
        String fileName = "doctor_profile_" + userId + "_" + System.currentTimeMillis() + ".jpg";
        
        imageUploader.uploadImage(selectedImageUri, "doctor-profiles", fileName, 
                new SupabaseImageUploader.UploadCallback() {
            @Override
            public void onSuccess(String publicUrl) {
                listener.onImageUploaded(publicUrl);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(EditDoctorProfileActivity.this, 
                        "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                listener.onImageUploaded(null);
            }
        });
    }

    private void updateDoctorProfile(String userId, int experience, String bio, double consultationFee, 
                                    String clinicAddress, String profileImageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("experience", experience);
        updates.put("bio", bio);
        updates.put("consultationFee", consultationFee);
        updates.put("clinicAddress", clinicAddress);
        
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            updates.put("profileImageUrl", profileImageUrl);
        }

        doctorProfilesRef.child(userId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
    
                    showProgress(false);
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    
                    finish();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(this, "Failed to update profile: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        saveButton.setEnabled(!show);
        cancelButton.setEnabled(!show);
        changePhotoButton.setEnabled(!show);
    }

    interface OnImageUploadedListener {
        void onImageUploaded(String imageUrl);
    }
}

    
    
    
    
    