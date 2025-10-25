package com.internship.healthcare;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.internship.healthcare.adapters.PatientsAdapter;
import com.internship.healthcare.models.PatientInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.databinding.ActivityDoctorPatientsBinding;

import java.util.ArrayList;
import java.util.List;
/**
 * DoctorPatientsActivity.java
 * A comprehensive healthcare management Android application
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


public class DoctorPatientsActivity extends AppCompatActivity {

    private ActivityDoctorPatientsBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference doctorPatientsRef;
    private DatabaseReference usersRef;
    private PatientsAdapter adapter;
    private List<PatientInfo> patientList;
    private String doctorId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorPatientsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        doctorId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (doctorId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        doctorPatientsRef = FirebaseDatabase.getInstance().getReference("doctorPatients").child(doctorId);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        patientList = new ArrayList<>();

        setupToolbar();
        setupRecyclerView();
        setupSearchBar();
    
        loadPatients();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new PatientsAdapter(patient -> {
            Toast.makeText(this, "Patient: " + patient.getPatientName(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to PatientDetailsActivity
        });

        binding.recyclerViewPatients.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewPatients.setAdapter(adapter);
    }

    private void setupSearchBar() {
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
    
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadPatients() {
    
        binding.progressIndicator.setVisibility(View.VISIBLE);

        doctorPatientsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                patientList.clear();

                if (snapshot.exists()) {
                    int totalPatients = 0;
                    int activeThisMonth = 0;
                    long currentTime = System.currentTimeMillis();
                    long thirtyDaysAgo = currentTime - (30L * 24 * 60 * 60 * 1000);
    

                    for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                        String patientId = patientSnapshot.getKey();
                        
                        PatientInfo patientInfo = patientSnapshot.getValue(PatientInfo.class);
    
                        if (patientInfo != null) {
                            patientInfo.setPatientId(patientId);
                            patientList.add(patientInfo);
                            totalPatients++;

                            // Count active patients this month
                            if (patientInfo.getLastVisitDate() > thirtyDaysAgo) {
                                activeThisMonth++;
                            }
                        }
                    }
    

                    binding.tvTotalPatients.setText(String.valueOf(totalPatients));
                    binding.tvActivePatients.setText(String.valueOf(activeThisMonth));

                    adapter.setPatients(patientList);
                    binding.layoutEmptyState.setVisibility(patientList.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.recyclerViewPatients.setVisibility(patientList.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    // No patients
                    binding.tvTotalPatients.setText("0");
                    binding.tvActivePatients.setText("0");
                    binding.layoutEmptyState.setVisibility(View.VISIBLE);
    
                    binding.recyclerViewPatients.setVisibility(View.GONE);
                }

                binding.progressIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                binding.progressIndicator.setVisibility(View.GONE);
    
                Toast.makeText(DoctorPatientsActivity.this,
                        "Error loading patients: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

    
    