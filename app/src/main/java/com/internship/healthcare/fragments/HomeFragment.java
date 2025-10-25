package com.internship.healthcare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.AppointmentDetailActivity;
import com.internship.healthcare.DoctorDetailsActivity;
import com.internship.healthcare.DoctorListActivity;
import com.internship.healthcare.R;
import com.internship.healthcare.adapters.CarouselAdapter;
import com.internship.healthcare.adapters.ServiceAdapter;
import com.internship.healthcare.adapters.TopDoctorAdapter;
import com.internship.healthcare.models.Doctor;
import com.internship.healthcare.models.Service;
import com.internship.healthcare.models.UserAppointment;

import java.util.ArrayList;
import java.util.List;
/**
 * HomeFragment.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.fragments
 *
 * Fragment displaying dashboard with quick actions and upcoming appointments.
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


public class HomeFragment extends Fragment {

    private View skeletonLoading;
    private View homeContent;
    private RecyclerView carouselRecyclerView;
    private RecyclerView servicesRecyclerView;
    private RecyclerView topDoctorsRecyclerView;
    private MaterialCardView appointmentCard;
    private ServiceAdapter serviceAdapter;
    private TopDoctorAdapter topDoctorAdapter;
    private TextView seeMoreServices;
    private TextView seeMoreDoctors;
    private TextView noAppointmentsMessage;
    
    // Appointment card views
    private ImageView doctorImage;
    private TextView doctorName;
    private TextView doctorSpecialty;
    private TextView appointmentDate;
    private TextView appointmentTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        skeletonLoading = view.findViewById(R.id.skeleton_loading);
        homeContent = view.findViewById(R.id.home_content);
        
        showSkeleton();
        
        carouselRecyclerView = view.findViewById(R.id.carousel_recycler_view);
        servicesRecyclerView = view.findViewById(R.id.services_recycler_view);
        topDoctorsRecyclerView = view.findViewById(R.id.top_doctors_recycler_view);
        appointmentCard = view.findViewById(R.id.appointment_card);
        seeMoreServices = view.findViewById(R.id.see_more_services);
        seeMoreDoctors = view.findViewById(R.id.see_more_doctors);
        noAppointmentsMessage = view.findViewById(R.id.no_appointments_message);
        if (appointmentCard != null) {
            doctorImage = appointmentCard.findViewById(R.id.doctor_image);
            doctorName = appointmentCard.findViewById(R.id.doctor_name);
            doctorSpecialty = appointmentCard.findViewById(R.id.doctor_specialty);
            appointmentDate = appointmentCard.findViewById(R.id.appointment_date);
            appointmentTime = appointmentCard.findViewById(R.id.appointment_time);
        }

        setupCarousel();
        
        setupServicesRecyclerView();
        setupTopDoctorsRecyclerView();
        
        setupNextAppointment();
        
        // See More Services click listener - Navigate to Category Fragment
        seeMoreServices.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new CategoryFragment())
                        .commit();
                
                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = 
                    getActivity().findViewById(R.id.bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_category);
                }
            }
        });
        
        // See More Doctors click listener - Navigate to Category Fragment
        seeMoreDoctors.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new CategoryFragment())
                        .commit();
                
                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = 
                    getActivity().findViewById(R.id.bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_category);
                }
            }
        });
    }
    
    private void showSkeleton() {
        if (skeletonLoading != null && homeContent != null) {
            skeletonLoading.setVisibility(View.VISIBLE);
            homeContent.setVisibility(View.GONE);
            
            if (skeletonLoading.getBackground() instanceof android.graphics.drawable.AnimationDrawable) {
                ((android.graphics.drawable.AnimationDrawable) skeletonLoading.getBackground()).start();
            }
        }
    }
    
    private void hideSkeleton() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded() && skeletonLoading != null && homeContent != null) {
                skeletonLoading.setVisibility(View.GONE);
                homeContent.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }
    
    private void setupCarousel() {
        carouselRecyclerView.setHasFixedSize(true);
        
        List<Integer> carouselImages = new ArrayList<>();
        carouselImages.add(R.drawable.img0);
        carouselImages.add(R.drawable.img1);
        carouselImages.add(R.drawable.img2);
        carouselImages.add(R.drawable.img3);
        carouselImages.add(R.drawable.img4);
        carouselImages.add(R.drawable.img5);
        carouselImages.add(R.drawable.img6);
        
        CarouselAdapter carouselAdapter = new CarouselAdapter(carouselImages);

        carouselRecyclerView.setAdapter(carouselAdapter);
        
        CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager();
        carouselRecyclerView.setLayoutManager(carouselLayoutManager);
        
        // Attach CarouselSnapHelper for snapping behavior
        CarouselSnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(carouselRecyclerView);
    }
    
    private void setupServicesRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
            getContext(),
            LinearLayoutManager.HORIZONTAL, 
            false
        );
        servicesRecyclerView.setLayoutManager(layoutManager);
        
        List<Service> services = getServicesList();
        
        serviceAdapter = new ServiceAdapter(services, service -> {
            Intent intent = new Intent(getActivity(), DoctorListActivity.class);
            intent.putExtra("categoryName", service.getName());
            startActivity(intent);
        });
        
        servicesRecyclerView.setAdapter(serviceAdapter);
    }
    
    private List<Service> getServicesList() {
        List<Service> services = new ArrayList<>();
        
        services.add(new Service("General Physician", R.drawable.ic_stethoscope));
        services.add(new Service("Cardiologist", R.drawable.cardio));
        services.add(new Service("Dermatologist", R.drawable.derma));
        services.add(new Service("Neurologist", R.drawable.neuro));
        services.add(new Service("Pediatrician", R.drawable.pedia));
        services.add(new Service("Orthopedic Surgeon", R.drawable.ortho));
        services.add(new Service("Gynecologist", R.drawable.immune));
        
        return services;
    }
    
    private void setupTopDoctorsRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
            getContext(), 
            LinearLayoutManager.HORIZONTAL, 
            false
        );
        topDoctorsRecyclerView.setLayoutManager(layoutManager);
        
        List<Doctor> topDoctors = new ArrayList<>();
        
        topDoctorAdapter = new TopDoctorAdapter(topDoctors, doctor -> {
            Intent intent = new Intent(getActivity(), DoctorDetailsActivity.class);
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
        
        topDoctorsRecyclerView.setAdapter(topDoctorAdapter);
        
        loadTopDoctorsFromFirebase();
    }
    
    private void loadTopDoctorsFromFirebase() {
        DatabaseReference doctorProfilesRef = FirebaseDatabase.getInstance().getReference("doctorProfiles");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        
        doctorProfilesRef.orderByChild("rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Doctor> topDoctorsList = new ArrayList<>();
                List<DataSnapshot> doctorSnapshots = new ArrayList<>();
                

                // Collect all doctor snapshots
                for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                    Double rating = doctorSnapshot.child("rating").getValue(Double.class);
                    if (rating != null && rating >= 3.5) {
                        doctorSnapshots.add(doctorSnapshot);
                    }
                }
                
                // Sort by rating in descending order
                doctorSnapshots.sort((a, b) -> {
                    Double ratingA = a.child("rating").getValue(Double.class);
                    Double ratingB = b.child("rating").getValue(Double.class);
                    if (ratingA == null) ratingA = 0.0;
                    if (ratingB == null) ratingB = 0.0;
                    return Double.compare(ratingB, ratingA);
                });
                
                // Take top 5
                int count = Math.min(5, doctorSnapshots.size());
                final int[] pendingLoads = {count};
                
                for (int i = 0; i < count; i++) {
                    DataSnapshot doctorSnapshot = doctorSnapshots.get(i);
                    String doctorId = doctorSnapshot.getKey();
                    String userId = doctorSnapshot.child("userId").getValue(String.class);
                    
                    if (userId != null) {
                        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                String name = userSnapshot.child("name").getValue(String.class);
                                String specialty = doctorSnapshot.child("specialty").getValue(String.class);
                                String profileImageUrl = doctorSnapshot.child("profileImageUrl").getValue(String.class);

                                Integer consultationFee = doctorSnapshot.child("consultationFee").getValue(Integer.class);
                                String contactNumber = doctorSnapshot.child("contactNumber").getValue(String.class);
                                Double rating = doctorSnapshot.child("rating").getValue(Double.class);
                                Integer experienceYears = doctorSnapshot.child("experienceYears").getValue(Integer.class);
                                String about = doctorSnapshot.child("about").getValue(String.class);
                                String degree = doctorSnapshot.child("degree").getValue(String.class);
                                String university = doctorSnapshot.child("university").getValue(String.class);
                                

                                Doctor doctor = new Doctor();
                                doctor.setId(doctorId);
                                doctor.setName(name != null ? name : "Dr. Unknown");
                                doctor.setSpeciality(specialty != null ? specialty : "General");
                                doctor.setImage(profileImageUrl != null ? profileImageUrl : "");
                                doctor.setConsultationFee(consultationFee != null ? consultationFee : 0);
                                doctor.setMobile(contactNumber != null ? contactNumber : "");
                                doctor.setRating(rating != null ? rating : 0.0);
                                doctor.setExperience(experienceYears != null ? experienceYears : 0);
                                doctor.setAbout(about != null ? about : "");
                                doctor.setDegree(degree != null ? degree : "");
                                doctor.setUniversity(university != null ? university : "");
                                
                                topDoctorsList.add(doctor);
                                
                                pendingLoads[0]--;
                                if (pendingLoads[0] == 0) {
                                    // All doctors loaded, update adapter
                                    if (topDoctorAdapter != null) {
                                        topDoctorAdapter.updateDoctors(topDoctorsList);
                                    }
                                    hideSkeleton();
                                }
                            }
                            
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                pendingLoads[0]--;
                            }
                        });
                    } else {
                        pendingLoads[0]--;
                    }
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideSkeleton();
            }
        });
    }
    
    private void setupNextAppointment() {
        loadUpcomingAppointment();
    }
    
    private void loadUpcomingAppointment() {
        com.internship.healthcare.utils.SessionManager sessionManager = 
            new com.internship.healthcare.utils.SessionManager(requireContext());
        String currentUserId = sessionManager.getUserId();
        
        if (currentUserId == null || currentUserId.isEmpty()) {
            showNoAppointments();
            return;
        }
        
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        
        // Query appointments for current user
        appointmentsRef.orderByChild("userId").equalTo(currentUserId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!isAdded() || getContext() == null) return;
                    
                    UserAppointment upcomingAppointment = null;
                    long latestBookingTime = 0;
                    
                    // Find the most recently booked approved or pending appointment
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserAppointment appointment = snapshot.getValue(UserAppointment.class);
                        
                        if (appointment != null) {
                            String status = appointment.getStatus();
                            
                            // Consider approved and pending appointments (not completed, cancelled, or rejected)
                            if ("approved".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)) {
                                long bookingTime = appointment.getTimestamp();
                                
                                if (bookingTime > latestBookingTime) {
                                    latestBookingTime = bookingTime;
                                    upcomingAppointment = appointment;
                                }
                            }
                        }
                    }
                    
                    if (upcomingAppointment != null) {
                        displayUpcomingAppointment(upcomingAppointment);
                    } else {
                        showNoAppointments();
                    }
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (!isAdded() || getContext() == null) return;
                    showNoAppointments();
                }
            });
    }
    
    private void displayUpcomingAppointment(UserAppointment appointment) {
        if (!isAdded() || getContext() == null) return;
        
        noAppointmentsMessage.setVisibility(View.GONE);
        appointmentCard.setVisibility(View.VISIBLE);
        
        // Populate appointment card
        doctorName.setText(appointment.getDoctorName());
        doctorSpecialty.setText(appointment.getDoctorSpeciality());
        appointmentDate.setText(appointment.getAppointmentDate());
        appointmentTime.setText(appointment.getAppointmentTime());
        
        if (appointment.getDoctorImage() != null && !appointment.getDoctorImage().isEmpty()) {
            com.bumptech.glide.Glide.with(requireContext())
                .load(appointment.getDoctorImage())
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(doctorImage);
        } else {
            doctorImage.setImageResource(R.drawable.ic_profile);
        }
        
        appointmentCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AppointmentDetailActivity.class);
            intent.putExtra("appointmentId", appointment.getId());
            startActivity(intent);
        });
    }
    
    private void showNoAppointments() {
        if (!isAdded() || getContext() == null) return;
        
        noAppointmentsMessage.setVisibility(View.VISIBLE);
        appointmentCard.setVisibility(View.GONE);
    }
}