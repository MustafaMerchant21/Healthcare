package com.internship.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.internship.healthcare.adapters.DoctorAdapter;
import com.internship.healthcare.models.Doctor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
/**
 * DoctorListActivity.java
 * A comprehensive healthcare management Android application
 * Activity handling doctor list screen and user interactions.
 *
 * <p>Extends: {@link AppCompatActivity}</p>
 *
 * <h3>Firebase Integration:</h3>
 * <ul>
 *   <li>Realtime Database</li>
 * </ul>
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class DoctorListActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView categoryTitle;
    private EditText searchInput;
    private RecyclerView doctorsRecycler;
    private LinearLayout emptyState;
    
    private DoctorAdapter adapter;

    private List<Doctor> allDoctors;
    private List<Doctor> filteredDoctors;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        
        categoryName = getIntent().getStringExtra("categoryName");
        if (categoryName == null) {
            categoryName = "Doctors";
        }
        
        initializeViews();
        
        categoryTitle.setText(categoryName);
        
        allDoctors = new ArrayList<>();
        filteredDoctors = new ArrayList<>();
        loadDoctors();
        
        doctorsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        
        adapter = new DoctorAdapter(filteredDoctors, doctor -> {
            Intent intent = new Intent(this, DoctorDetailsActivity.class);
    
            intent.putExtra("doctorId", doctor.getId());
            intent.putExtra("doctorName", doctor.getName());
            intent.putExtra("doctorSpeciality", doctor.getSpeciality());
            intent.putExtra("doctorImage", doctor.getImage());
            intent.putExtra("consultationFee", doctor.getConsultationFee());
            intent.putExtra("doctorPhone", doctor.getMobile());
            intent.putExtra("doctorRating", doctor.getRating());
            intent.putExtra("doctorExperience", doctor.getExperience());
            intent.putExtra("doctorAbout", doctor.getAbout());
            intent.putExtra("doctorDegree", doctor.getDegree());
            intent.putExtra("doctorUniversity", doctor.getUniversity());
            startActivity(intent);
        });
        doctorsRecycler.setAdapter(adapter);
        
        setupListeners();
    }
    
    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        categoryTitle = findViewById(R.id.category_title);
        searchInput = findViewById(R.id.search_input);
        doctorsRecycler = findViewById(R.id.doctors_recycler);
        emptyState = findViewById(R.id.empty_state);
    }
    
    private void loadDoctors() {
        DatabaseReference doctorsRef = FirebaseDatabase.getInstance().getReference("doctorProfiles");
        
        doctorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allDoctors.clear();
                filteredDoctors.clear();
                
                for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                    String doctorId = doctorSnapshot.getKey();
                    String specialty = doctorSnapshot.child("specialty").getValue(String.class);
                    
                    // Filter by category name - case insensitive match
                    if (specialty != null && specialty.equalsIgnoreCase(categoryName)) {
                        String about = doctorSnapshot.child("about").getValue(String.class);
                        String degree = doctorSnapshot.child("degree").getValue(String.class);
    
                        Integer experienceYears = doctorSnapshot.child("experienceYears").getValue(Integer.class);
                        String profileImageUrl = doctorSnapshot.child("profileImageUrl").getValue(String.class);
                        Double rating = doctorSnapshot.child("rating").getValue(Double.class);
                        String university = doctorSnapshot.child("university").getValue(String.class);
                        Integer consultationFee = doctorSnapshot.child("consultationFee").getValue(Integer.class);
                        String contactNumber = doctorSnapshot.child("contactNumber").getValue(String.class);
                        
                        String userId = doctorSnapshot.child("userId").getValue(String.class);
                        if (userId != null) {
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
    
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot userSnapshot) {
                                    String name = userSnapshot.child("name").getValue(String.class);
                                    String phone = contactNumber != null ? contactNumber : userSnapshot.child("phone").getValue(String.class);
                                    
                                    Doctor doctor = new Doctor();
    
                                    doctor.setId(doctorId);
                                    doctor.setName(name != null ? name : "Dr. Unknown");
                                    doctor.setSpeciality(specialty);
                                    doctor.setImage(profileImageUrl != null ? profileImageUrl : "");
                                    doctor.setConsultationFee(consultationFee != null ? consultationFee : 0);
                                    doctor.setMobile(phone != null ? phone : "");
                                    doctor.setRating(rating != null ? rating : 0.0);
                                    doctor.setExperience(experienceYears != null ? experienceYears : 0);
                                    doctor.setAbout(about != null ? about : "");
                                    doctor.setDegree(degree != null ? degree : "");
                                    doctor.setUniversity(university != null ? university : "");
                                    
                                    allDoctors.add(doctor);
                                    filteredDoctors.add(doctor);
                                    updateUI();
                                }
                                
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                }
                
                updateUI();
            }
            
    
            @Override
            public void onCancelled(DatabaseError databaseError) {
                updateUI();
            }
        });
    }
    
    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());
        
        // Search functionality
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDoctors(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void filterDoctors(String query) {
    
        filteredDoctors.clear();
        
        if (query.isEmpty()) {
            filteredDoctors.addAll(allDoctors);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Doctor doctor : allDoctors) {
                if (doctor.getName().toLowerCase().contains(lowerCaseQuery) ||
                    doctor.getSpeciality().toLowerCase().contains(lowerCaseQuery)) {
                    filteredDoctors.add(doctor);
                }
            }
        }
        
        updateUI();
    
    }
    
    private void updateUI() {
        if (filteredDoctors.isEmpty()) {
            doctorsRecycler.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            doctorsRecycler.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    
        
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}

    
    
    
    
    