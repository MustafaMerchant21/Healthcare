package com.internship.healthcare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.DoctorDetailsActivity;
import com.internship.healthcare.DoctorListActivity;
import com.internship.healthcare.R;
import com.internship.healthcare.adapters.CategoryAdapter;
import com.internship.healthcare.adapters.DoctorAdapter;
import com.internship.healthcare.models.Doctor;
import com.internship.healthcare.models.DoctorCategory;
import com.internship.healthcare.models.DoctorProfile;
import com.internship.healthcare.models.User;

import java.util.ArrayList;
import java.util.List;
/**
 * CategoryFragment.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.fragments
 * Fragment showing doctor categories and specialty browsing.
 *
 * <p>Extends: {@link Fragment}</p>
 *
 * <h3>Firebase Integration:</h3>
 * <ul>
 *   <li>Realtime Database</li>
 * </ul>
 *
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class CategoryFragment extends Fragment {

    private static final String TAG = "CategoryFragment";

    private View skeletonLoading;
    private View categoryContent;
    private EditText searchInput;
    private ChipGroup filterChips;
    private Chip chipCategories, chipDoctors, chipMbbs, chipMs, chipMd;
    private RecyclerView categoriesRecycler;
    private RecyclerView doctorsRecycler;
    private LinearLayout emptyState;
    private TextView categoriesHeader;
    private TextView doctorsHeader;
    private View contentScrollView;
    private View loadingIndicator;

    private CategoryAdapter categoryAdapter;
    private DoctorAdapter doctorAdapter;
    private List<DoctorCategory> allCategories;
    private List<DoctorCategory> filteredCategories;
    private List<Doctor> allDoctors;
    private List<Doctor> filteredDoctors;

    private DatabaseReference databaseReference;
    private boolean isDoctorsLoaded = false;
    private int pendingDoctorLoads = 0;

    // Filter state
    private enum FilterType {
        CATEGORIES, DOCTORS, MBBS, MS, MD
    }

    private FilterType currentFilter = FilterType.CATEGORIES;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        skeletonLoading = view.findViewById(R.id.skeleton_loading);
        categoryContent = view.findViewById(R.id.category_content);

        showSkeleton();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        searchInput = view.findViewById(R.id.search_input);
        filterChips = view.findViewById(R.id.filter_chips);
        chipCategories = view.findViewById(R.id.chip_categories);
        chipDoctors = view.findViewById(R.id.chip_doctors);
        chipMbbs = view.findViewById(R.id.chip_mbbs);
        chipMs = view.findViewById(R.id.chip_ms);
        chipMd = view.findViewById(R.id.chip_md);
        categoriesRecycler = view.findViewById(R.id.categories_recycler);
        doctorsRecycler = view.findViewById(R.id.doctors_recycler);
        emptyState = view.findViewById(R.id.empty_state);
        categoriesHeader = view.findViewById(R.id.categories_header);
        doctorsHeader = view.findViewById(R.id.doctors_header);
        contentScrollView = view.findViewById(R.id.content_scroll_view);
        loadingIndicator = view.findViewById(R.id.loading_indicator);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        categoriesRecycler.setLayoutManager(gridLayoutManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        doctorsRecycler.setLayoutManager(linearLayoutManager);

        allCategories = new ArrayList<>();
        filteredCategories = new ArrayList<>();
        allDoctors = new ArrayList<>();
        filteredDoctors = new ArrayList<>();

        showLoading(true);

        loadCategories();
        loadDoctorsFromFirebase();

        categoryAdapter = new CategoryAdapter(filteredCategories, category -> {
            openDoctorsList(category.getCategoryName());
        });
        categoriesRecycler.setAdapter(categoryAdapter);

        doctorAdapter = new DoctorAdapter(filteredDoctors, doctor -> {
            openDoctorDetails(doctor);
        });
        doctorsRecycler.setAdapter(doctorAdapter);

        setupSearch();

        setupFilterChips();
    }

    private void loadCategories() {
        String[] categoryNames = {
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

        allCategories.clear();
        for (int i = 0; i < categoryNames.length; i++) {
            allCategories.add(new DoctorCategory(
                    String.valueOf(i + 1),
                    categoryNames[i],
                    0, // doctorCount - will be updated
                    0, // avgPrice - will be updated
                    "",
                    false
            ));
        }

        // Initially show all categories
        filteredCategories.addAll(allCategories);
        Log.d(TAG, "Initialized " + allCategories.size() + " categories");
    }

    private void loadDoctorsFromFirebase() {
        if (isDoctorsLoaded) {
            Log.d(TAG, "Doctors already loaded, skipping");
            return;
        }

        Log.d(TAG, "Starting to load doctors from Firebase");

        databaseReference.child("doctorProfiles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allDoctors.clear();
                pendingDoctorLoads = 0;

                // Count total doctors to load
                int totalDoctors = (int) dataSnapshot.getChildrenCount();
                Log.d(TAG, "Found " + totalDoctors + " doctor profiles in Firebase");

                if (totalDoctors == 0) {
                    // No doctors found
                    isDoctorsLoaded = true;
                    showLoading(false);
                    updateInitialUI();
                    Toast.makeText(getContext(), "No doctors found", Toast.LENGTH_SHORT).show();
                    return;
                }

                pendingDoctorLoads = totalDoctors;

                for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                    String userId = doctorSnapshot.getKey();
                    DoctorProfile doctorProfile = doctorSnapshot.getValue(DoctorProfile.class);

                    if (doctorProfile != null) {
                        Log.d(TAG, "Loading doctor profile for userId: " + userId);
                        loadUserDetails(userId, doctorProfile, totalDoctors);
                    } else {
                        // Decrement if profile is null
                        pendingDoctorLoads--;
                        Log.w(TAG, "Doctor profile is null for userId: " + userId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load doctors: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Failed to load doctors: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                isDoctorsLoaded = true;
                showLoading(false);
                updateInitialUI();
                hideSkeleton();
            }
        });
    }

    private void loadUserDetails(String userId, DoctorProfile doctorProfile, int totalDoctors) {
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        Log.d(TAG, "=== User Details Debug ===");
                        Log.d(TAG, "UserId: " + userId);
                        Log.d(TAG, "User exists: " + (user != null));

                        if (user != null) {
                            Log.d(TAG, "User name: " + user.getName());
                            Log.d(TAG, "User role: " + user.getRole());
                            Log.d(TAG, "User isVerified: " + user.isVerified());
                            Log.d(TAG, "Role equals 'doctor': " + "doctor".equals(user.getRole()));

                            boolean isDoctor = "doctor".equals(user.getRole());
                            boolean isVerified = user.isVerified();

                            Log.d(TAG, "Passes doctor check: " + isDoctor);
                            Log.d(TAG, "Passes verified check: " + isVerified);

                            if (isDoctor && isVerified) {
                                Doctor doctor = new Doctor();
                                doctor.setId(userId);
                                doctor.setName(user.getName());
                                doctor.setSpeciality(doctorProfile.getSpecialty());
                                doctor.setConsultationFee(doctorProfile.getConsultationFee());
                                doctor.setRating(doctorProfile.getRating());
                                doctor.setExperience(doctorProfile.getExperienceYears());
                                doctor.setDegree(doctorProfile.getDegree());
                                doctor.setUniversity(doctorProfile.getUniversity());

                                // Use getMobile() instead of getPhone() since that's what's in Firebase
                                doctor.setMobile(user.getMobile());

                                allDoctors.add(doctor);
                                Log.d(TAG, "✓ Successfully added doctor: " + doctor.getName() + " (Specialty: " + doctor.getSpeciality() + ")");
                                Log.d(TAG, "✓ Total doctors in list now: " + allDoctors.size());
                            } else {
                                Log.w(TAG, "✗ User failed verification: isDoctor=" + isDoctor + ", isVerified=" + isVerified);
                            }
                        } else {
                            Log.e(TAG, "✗ User object is NULL for userId: " + userId);
                        }

                        Log.d(TAG, "======================");

                        // Decrement pending counter
                        pendingDoctorLoads--;
                        Log.d(TAG, "Pending doctor loads remaining: " + pendingDoctorLoads);

                        if (pendingDoctorLoads == 0) {
                            isDoctorsLoaded = true;
                            Log.d(TAG, "========================================");
                            Log.d(TAG, "ALL DOCTORS LOADED! Total: " + allDoctors.size());
                            Log.d(TAG, "========================================");

                            updateCategoryStatistics();

                            showLoading(false);
                            updateInitialUI();

                            hideSkeleton();

                            if (getContext() != null) {
                                Toast.makeText(getContext(),
                                        allDoctors.size() + " verified doctors loaded",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to load user details for userId: " + userId + ", error: " + error.getMessage());

                        // Decrement pending counter
                        pendingDoctorLoads--;

                        if (pendingDoctorLoads == 0) {
                            isDoctorsLoaded = true;
                            Log.d(TAG, "All doctors loaded (with errors)! Total: " + allDoctors.size());
                            showLoading(false);
                            updateInitialUI();
                        }
                    }
                });
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Only allow search if doctors are loaded
                if (isDoctorsLoaded) {
                    filterResults(s.toString());
                } else {
                    Log.d(TAG, "Search attempted but doctors not loaded yet");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updateCategoryStatistics() {
        Log.d(TAG, "Updating category statistics...");

        // For each category, count doctors and calculate average fee
        for (DoctorCategory category : allCategories) {
            String categoryName = category.getCategoryName();
            int doctorCount = 0;
            double totalFee = 0;

            // Count doctors in this category and sum their fees
            for (Doctor doctor : allDoctors) {
                String doctorSpecialty = doctor.getSpeciality();
                if (doctorSpecialty != null && doctorSpecialty.equalsIgnoreCase(categoryName)) {
                    doctorCount++;
                    totalFee += doctor.getConsultationFee();
                }
            }

            category.setDoctorCount(doctorCount);
            if (doctorCount > 0) {
                int avgFee = (int) (totalFee / doctorCount);
                category.setAveragePrice(avgFee);
            } else {
                category.setAveragePrice(0);
            }

            Log.d(TAG, "Category: " + categoryName + ", Doctors: " + doctorCount + ", Avg Fee: " + category.getAveragePrice());
        }

        filteredCategories.clear();
        filteredCategories.addAll(allCategories);
    }

    private void setupFilterChips() {
        filterChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                // If nothing is checked, default to Categories
                chipCategories.setChecked(true);
                return;
            }

            int checkedId = checkedIds.get(0);

            if (checkedId == R.id.chip_categories) {
                currentFilter = FilterType.CATEGORIES;
            } else if (checkedId == R.id.chip_doctors) {
                currentFilter = FilterType.DOCTORS;
            } else if (checkedId == R.id.chip_mbbs) {
                currentFilter = FilterType.MBBS;
            } else if (checkedId == R.id.chip_ms) {
                currentFilter = FilterType.MS;
            } else if (checkedId == R.id.chip_md) {
                currentFilter = FilterType.MD;
            }

            // Reapply current search with new filter
            filterResults(searchInput.getText().toString());
        });

        chipCategories.setChecked(true);
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null && contentScrollView != null) {
            loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
            contentScrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void updateInitialUI() {
        // Initially show all categories and doctors
        filteredCategories.clear();
        filteredCategories.addAll(allCategories);
        filteredDoctors.clear();
        filteredDoctors.addAll(allDoctors);

        Log.d(TAG, "updateInitialUI: Categories=" + filteredCategories.size() + ", Doctors=" + filteredDoctors.size());

        updateUI();
    }

    private void filterResults(String query) {
        filteredCategories.clear();
        filteredDoctors.clear();

        Log.d(TAG, "Filtering with query: '" + query + "', filter: " + currentFilter + ", allDoctors.size=" + allDoctors.size());

        String lowerCaseQuery = query.toLowerCase();

        switch (currentFilter) {
            case CATEGORIES:
                for (DoctorCategory category : allCategories) {
                    if (query.isEmpty() || category.getCategoryName().toLowerCase().contains(lowerCaseQuery)) {
                        filteredCategories.add(category);
                        Log.d(TAG, "Matched category: " + category.getCategoryName());
                    }
                }

                // Only show doctors if user is actively searching
                if (!query.isEmpty()) {
                    filterDoctorsByNameOrSpecialty(lowerCaseQuery);
                }
                break;

            case DOCTORS:
                if (query.isEmpty()) {
                    filteredDoctors.addAll(allDoctors);
                } else {
                    filterDoctorsByNameOrSpecialty(lowerCaseQuery);
                }
                break;

            case MBBS:
                for (Doctor doctor : allDoctors) {
                    String degree = doctor.getDegree() != null ? doctor.getDegree().toUpperCase() : "";
                    if (degree.contains("MBBS")) {
                        if (query.isEmpty() || matchesDoctor(doctor, lowerCaseQuery)) {
                            filteredDoctors.add(doctor);
                            Log.d(TAG, "Matched MBBS doctor: " + doctor.getName());
                        }
                    }
                }
                break;

            case MS:
                for (Doctor doctor : allDoctors) {
                    String degree = doctor.getDegree() != null ? doctor.getDegree().toUpperCase() : "";
                    if (degree.contains("MS")) {
                        if (query.isEmpty() || matchesDoctor(doctor, lowerCaseQuery)) {
                            filteredDoctors.add(doctor);
                            Log.d(TAG, "Matched MS doctor: " + doctor.getName());
                        }
                    }
                }
                break;

            case MD:
                for (Doctor doctor : allDoctors) {
                    String degree = doctor.getDegree() != null ? doctor.getDegree().toUpperCase() : "";
                    if (degree.contains("MD")) {
                        if (query.isEmpty() || matchesDoctor(doctor, lowerCaseQuery)) {
                            filteredDoctors.add(doctor);
                            Log.d(TAG, "Matched MD doctor: " + doctor.getName());
                        }
                    }
                }
                break;
        }

        Log.d(TAG, "Filter complete: " + filteredCategories.size() + " categories, " + filteredDoctors.size() + " doctors");
        updateUI();
    }

    private void filterDoctorsByNameOrSpecialty(String lowerCaseQuery) {
        for (Doctor doctor : allDoctors) {
            if (matchesDoctor(doctor, lowerCaseQuery)) {
                filteredDoctors.add(doctor);
                Log.d(TAG, "Matched doctor: " + doctor.getName());
            }
        }
    }

    private boolean matchesDoctor(Doctor doctor, String lowerCaseQuery) {
        String doctorName = doctor.getName() != null ? doctor.getName().toLowerCase() : "";
        String doctorSpecialty = doctor.getSpeciality() != null ? doctor.getSpeciality().toLowerCase() : "";
        String doctorDegree = doctor.getDegree() != null ? doctor.getDegree().toLowerCase() : "";

        return doctorName.contains(lowerCaseQuery) ||
                doctorSpecialty.contains(lowerCaseQuery) ||
                doctorDegree.contains(lowerCaseQuery);
    }

    private void updateUI() {
        boolean hasCategories = !filteredCategories.isEmpty();
        boolean hasDoctors = !filteredDoctors.isEmpty();
        boolean hasAnyResults = hasCategories || hasDoctors;

        Log.d(TAG, "updateUI: hasCategories=" + hasCategories + ", hasDoctors=" + hasDoctors);

        // Show/hide empty state
        if (!hasAnyResults) {
            categoriesRecycler.setVisibility(View.GONE);
            doctorsRecycler.setVisibility(View.GONE);
            categoriesHeader.setVisibility(View.GONE);
            doctorsHeader.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.GONE);

            // Show/hide categories section
            if (hasCategories) {
                categoriesHeader.setVisibility(View.VISIBLE);
                categoriesRecycler.setVisibility(View.VISIBLE);
            } else {
                categoriesHeader.setVisibility(View.GONE);
                categoriesRecycler.setVisibility(View.GONE);
            }

            // Show/hide doctors section
            if (hasDoctors) {
                doctorsHeader.setVisibility(View.VISIBLE);
                doctorsRecycler.setVisibility(View.VISIBLE);
            } else {
                doctorsHeader.setVisibility(View.GONE);
                doctorsRecycler.setVisibility(View.GONE);
            }
        }

        if (categoryAdapter != null) {
            categoryAdapter.updateData(filteredCategories);
        }

        if (doctorAdapter != null) {
            doctorAdapter.updateData(filteredDoctors);
        }
    }

    private void openDoctorsList(String categoryName) {
        Intent intent = new Intent(getActivity(), DoctorListActivity.class);
        intent.putExtra("categoryName", categoryName);
        startActivity(intent);
    }

    private void openDoctorDetails(Doctor doctor) {
        Intent intent = new Intent(getActivity(), DoctorDetailsActivity.class);
        intent.putExtra("doctorId", doctor.getId());
        intent.putExtra("doctorName", doctor.getName());
        intent.putExtra("doctorSpeciality", doctor.getSpeciality());
        intent.putExtra("doctorFee", doctor.getConsultationFee());
        intent.putExtra("doctorRating", doctor.getRating());
        intent.putExtra("doctorExperience", doctor.getExperience());
        startActivity(intent);
    }

    private void showSkeleton() {
        if (skeletonLoading != null && categoryContent != null) {
            skeletonLoading.setVisibility(android.view.View.VISIBLE);
            categoryContent.setVisibility(android.view.View.GONE);
        }
    }

    private void hideSkeleton() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded() && skeletonLoading != null && categoryContent != null) {
                skeletonLoading.setVisibility(android.view.View.GONE);
                categoryContent.setVisibility(android.view.View.VISIBLE);
                /**
                 * Filters doctors by name or specialty based on criteria in patient information and records
                 *
                 * @param lowerCaseQuery string value
                 */
            }
        }, 1000);
    }
}