//File 2: DoctorVerificationActivity.java
package com.internship.healthcare;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.internship.healthcare.adapters.DocumentAdapter;
import com.internship.healthcare.models.VerificationRequest;
import com.internship.healthcare.utils.DoctorNameFormatter;
import com.internship.healthcare.utils.SupabaseStorageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * DoctorVerificationActivity.java
 * A comprehensive healthcare management Android application
 * Activity for doctor credential verification submission. Integrates with Firebase Authentication, Realtime Database.
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


public class DoctorVerificationActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private AutoCompleteTextView specialtyDropdown;
    private TextInputEditText degreeInput, universityInput, experienceInput, consultationFeeInput;
    private TextInputEditText contactNumberInput, clinicAddressInput, aboutInput;
    private TextInputLayout specialtyInputLayout, degreeInputLayout, universityInputLayout;
    private TextInputLayout experienceInputLayout, consultationFeeInputLayout;
    private TextInputLayout contactNumberInputLayout, clinicAddressInputLayout, aboutInputLayout;

    private MaterialButton uploadDocumentsButton, submitButton;
    private RecyclerView documentsRecyclerView;
    private CircularProgressIndicator progressIndicator;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private DatabaseReference verificationRequestsRef;

    // Supabase Storage
    private SupabaseStorageHelper supabaseStorageHelper;

    // Data
    private List<Uri> selectedDocuments;
    private DocumentAdapter documentAdapter;
    private ActivityResultLauncher<Intent> documentPickerLauncher;

    // Specialties list
    private static final String[] SPECIALTIES = {
            "General Physician",
            "Cardiologist",
            "Dermatologist",
            "Neurologist",
            "Pediatrician",
            "Orthopedic Surgeon",
            "Gynecologist",
            "Psychiatrist",
            "Dentist",
            "Ophthalmologist",
            "ENT Specialist",
            "Radiologist",
            "Anesthesiologist",
            "Urologist",
            "Gastroenterologist"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_verification);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        verificationRequestsRef = FirebaseDatabase.getInstance().getReference("verificationRequests");

        supabaseStorageHelper = new SupabaseStorageHelper(this);

        selectedDocuments = new ArrayList<>();

        initializeViews();

        setupToolbar();

        setupSpecialtyDropdown();

        setupDocumentPicker();

        setupClickListeners();
    }
    

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        specialtyDropdown = findViewById(R.id.specialty_dropdown);
        specialtyInputLayout = findViewById(R.id.specialty_input_layout);
        degreeInput = findViewById(R.id.degree_input);
        degreeInputLayout = findViewById(R.id.degree_input_layout);
        universityInput = findViewById(R.id.university_input);
        universityInputLayout = findViewById(R.id.university_input_layout);
        experienceInput = findViewById(R.id.experience_input);
        experienceInputLayout = findViewById(R.id.experience_input_layout);
        consultationFeeInput = findViewById(R.id.consultation_fee_input);
        consultationFeeInputLayout = findViewById(R.id.consultation_fee_input_layout);
        contactNumberInput = findViewById(R.id.contact_number_input);
        contactNumberInputLayout = findViewById(R.id.contact_number_input_layout);
        clinicAddressInput = findViewById(R.id.clinic_address_input);
        clinicAddressInputLayout = findViewById(R.id.clinic_address_input_layout);
        aboutInput = findViewById(R.id.about_input);
        aboutInputLayout = findViewById(R.id.about_input_layout);
        uploadDocumentsButton = findViewById(R.id.upload_documents_button);
        submitButton = findViewById(R.id.submit_button);
        documentsRecyclerView = findViewById(R.id.documents_recycler_view);
        progressIndicator = findViewById(R.id.progress_indicator);

        documentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        documentAdapter = new DocumentAdapter(selectedDocuments, this::removeDocument);
        documentsRecyclerView.setAdapter(documentAdapter);
    
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSpecialtyDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                SPECIALTIES
        );
        specialtyDropdown.setAdapter(adapter);
    }

    private void setupDocumentPicker() {
        documentPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri documentUri = data.getClipData().getItemAt(i).getUri();
    
                                // Take persistable URI permission
                                try {
                                    getContentResolver().takePersistableUriPermission(
                                            documentUri,
                                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    );
                                } catch (SecurityException e) {
                                    // Permission not needed for this URI
                                }
                                selectedDocuments.add(documentUri);
    
                            }
                        } else if (data.getData() != null) {
                            Uri documentUri = data.getData();
                            // Take persistable URI permission
                            try {
                                getContentResolver().takePersistableUriPermission(
                                        documentUri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );
                            } catch (SecurityException e) {
                                // Permission not needed for this URI
    
                            }
                            selectedDocuments.add(documentUri);
                        }
                        
                        updateDocumentsList();
                    }
                }
        );
    }

    private void setupClickListeners() {
        uploadDocumentsButton.setOnClickListener(v -> openDocumentPicker());
        submitButton.setOnClickListener(v -> validateAndSubmit());
    }

    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        documentPickerLauncher.launch(intent);
    }

    private void updateDocumentsList() {
        if (selectedDocuments.isEmpty()) {
            documentsRecyclerView.setVisibility(View.GONE);
        } else {
            documentsRecyclerView.setVisibility(View.VISIBLE);
            documentAdapter.notifyDataSetChanged();
        }
    }

    private void removeDocument(int position) {
        if (position >= 0 && position < selectedDocuments.size()) {
            selectedDocuments.remove(position);
            documentAdapter.notifyItemRemoved(position);
            updateDocumentsList();
        }
    }

    private void validateAndSubmit() {
    
        specialtyInputLayout.setError(null);
        degreeInputLayout.setError(null);
        universityInputLayout.setError(null);
        experienceInputLayout.setError(null);
        consultationFeeInputLayout.setError(null);
        contactNumberInputLayout.setError(null);
        clinicAddressInputLayout.setError(null);
    
        aboutInputLayout.setError(null);

        String specialty = specialtyDropdown.getText().toString().trim();
        String degree = degreeInput.getText().toString().trim();
        String university = universityInput.getText().toString().trim();
        String experienceStr = experienceInput.getText().toString().trim();
        String consultationFeeStr = consultationFeeInput.getText().toString().trim();
        String contactNumber = contactNumberInput.getText().toString().trim();
        String clinicAddress = clinicAddressInput.getText().toString().trim();
        String about = aboutInput.getText().toString().trim();

        // Validation
        boolean isValid = true;
    

        if (specialty.isEmpty()) {
            specialtyInputLayout.setError("Please select a specialty");
            isValid = false;
        }

        if (degree.isEmpty()) {
            degreeInputLayout.setError("Please enter your degree");
            isValid = false;
        }

    
        if (university.isEmpty()) {
            universityInputLayout.setError("Please enter your university");
            isValid = false;
        }

        if (experienceStr.isEmpty()) {
            experienceInputLayout.setError("Please enter years of experience");
            isValid = false;
        }

        if (consultationFeeStr.isEmpty()) {
            consultationFeeInputLayout.setError("Please enter consultation fee");
    
            isValid = false;
        }

        if (contactNumber.isEmpty()) {
            contactNumberInputLayout.setError("Please enter contact number");
            isValid = false;
        } else if (contactNumber.length() < 10) {
            contactNumberInputLayout.setError("Invalid contact number");
            isValid = false;
        }

        if (clinicAddress.isEmpty()) {
            clinicAddressInputLayout.setError("Please enter clinic address");
            isValid = false;
        }

        if (about.isEmpty()) {
            aboutInputLayout.setError("Please enter your professional bio");
            isValid = false;
        }

        if (selectedDocuments.isEmpty()) {
            Toast.makeText(this, "Please upload at least one certificate", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        int experience = Integer.parseInt(experienceStr);
        double consultationFee = Double.parseDouble(consultationFeeStr);

        // Submit verification request
        submitVerificationRequest(specialty, degree, university, experience, consultationFee,
                contactNumber, clinicAddress, about);
    }

    private void submitVerificationRequest(String specialty, String degree, String university, 
                                          int experience, double consultationFee,
                                          String contactNumber, String clinicAddress, String about) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        
        showProgress(true);

        // Upload documents first, then create verification request
        uploadDocuments(userId, documentUrls -> {
            if (documentUrls != null) {
                VerificationRequest request = new VerificationRequest(
                        userId,
                        specialty,
                        degree,
                        university,
                        experience,
                        consultationFee,
                        documentUrls
                );
                
                request.setContactNumber(contactNumber);
                request.setHospitalAffiliation(clinicAddress); // Using clinic address as hospital affiliation
                request.setAbout(about);

                verificationRequestsRef.child(userId).setValue(request)
                        .addOnSuccessListener(aVoid -> {
                            updateUserVerificationStatus(userId);
                        })
                        .addOnFailureListener(e -> {
                            showProgress(false);
                            Toast.makeText(this, "Failed to submit request: " + e.getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                        });
            } else {
                showProgress(false);
                Toast.makeText(this, "Failed to upload documents", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadDocuments(String userId, OnDocumentsUploadedListener listener) {
    
        // Use Supabase Storage Helper
        supabaseStorageHelper.uploadDocuments(userId, selectedDocuments, 
                new SupabaseStorageHelper.UploadCallback() {
            @Override
            public void onProgress(int uploadedCount, int totalCount) {
                runOnUiThread(() -> {
                    Toast.makeText(DoctorVerificationActivity.this, 
                            "Uploading: " + uploadedCount + "/" + totalCount, 
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onSuccess(List<String> urls) {
                runOnUiThread(() -> {
                    listener.onDocumentsUploaded(urls);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(DoctorVerificationActivity.this, 
                            "Upload failed: " + error, 
                            Toast.LENGTH_SHORT).show();
                    listener.onDocumentsUploaded(null);
                });
            }
        });
    }

    private void updateUserVerificationStatus(String userId) {
        // First, get the user's current name
        databaseReference.child(userId).child("name").get()
                .addOnSuccessListener(snapshot -> {
                    String currentName = snapshot.getValue(String.class);
                    
                    String doctorName = currentName != null ? 
                            DoctorNameFormatter.formatDoctorName(currentName) : null;
                    
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("role", "doctor");
                    updates.put("isVerified", false);
                    updates.put("doctorVerificationStatus", "pending");
                    if (doctorName != null) {
                        updates.put("name", doctorName);
                    }

                    databaseReference.child(userId).updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                showProgress(false);
                                Toast.makeText(this, "Verification request submitted successfully! Your name has been updated to include 'Dr.' prefix.", 
                                        Toast.LENGTH_LONG).show();
                                
                                finish();
                            })
    
                            .addOnFailureListener(e -> {
                                showProgress(false);
                                Toast.makeText(this, "Failed to update user status: " + e.getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // If we can't get the name, still update the status
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("role", "doctor");
    
                    updates.put("isVerified", false);
                    updates.put("doctorVerificationStatus", "pending");

                    databaseReference.child(userId).updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                showProgress(false);
                                Toast.makeText(this, "Verification request submitted successfully!", 
                                        Toast.LENGTH_LONG).show();
                                finish();
                            })
                            .addOnFailureListener(ex -> {
                                showProgress(false);
                                Toast.makeText(this, "Failed to update user status: " + ex.getMessage(), 
                                        Toast.LENGTH_SHORT).show();
    
                            });
                });
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        submitButton.setEnabled(!show);
        uploadDocumentsButton.setEnabled(!show);
    }

    @Override
    
    protected void onDestroy() {
        super.onDestroy();
        // Clean up Supabase storage helper
        if (supabaseStorageHelper != null) {
            supabaseStorageHelper.shutdown();
        }
    }

    interface OnDocumentsUploadedListener {
        void onDocumentsUploaded(List<String> documentUrls);
    }
}

    
    
    