package com.internship.healthcare.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.AppointmentDetailActivity;
import com.internship.healthcare.R;
import com.internship.healthcare.adapters.AppointmentDetailAdapter;
import com.internship.healthcare.models.AppointmentDetail;
import com.internship.healthcare.models.UserAppointment;

import java.util.ArrayList;
import java.util.List;
/**
 * CompletedAppointmentsFragment.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.fragments
 *
 * Fragment showing completed appointments
 *
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */
public class CompletedAppointmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private ProgressBar loadingIndicator;
    private TextView emptyMessage;
    
    private AppointmentDetailAdapter adapter;
    private List<AppointmentDetail> appointments;
    private DatabaseReference appointmentsRef;
    private ValueEventListener appointmentsListener;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.status_recycler_view);
        emptyState = view.findViewById(R.id.status_empty_state);
        loadingIndicator = view.findViewById(R.id.status_loading_indicator);
        emptyMessage = view.findViewById(R.id.status_empty_message);

        emptyMessage.setText("No completed appointments");

        auth = FirebaseAuth.getInstance();
        appointments = new ArrayList<>();

        setupRecyclerView();

        loadCompletedAppointments();
    }

    private void setupRecyclerView() {
        adapter = new AppointmentDetailAdapter(appointments, new AppointmentDetailAdapter.OnAppointmentClickListener() {
            @Override
            public void onAppointmentClick(AppointmentDetail appointment) {
                openAppointmentDetail(appointment);
            }

            @Override
            public void onCallClick(AppointmentDetail appointment) {
                makePhoneCall(appointment.getDoctorPhone());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadCompletedAppointments() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please login to view appointments", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        appointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointments");
        loadingIndicator.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        appointmentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingIndicator.setVisibility(View.GONE);
                appointments.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    UserAppointment userAppointment = child.getValue(UserAppointment.class);
                    if (userAppointment != null) {
                        // Filter ONLY for appointments where current user is the PATIENT (not doctor)
                        String appointmentUserId = userAppointment.getUserId();
                        
                        if (userId.equals(appointmentUserId)) {
                            // Filter for completed appointments
                            if ("completed".equals(userAppointment.getStatus())) {
                                AppointmentDetail appointment = convertToAppointmentDetail(userAppointment);
                                appointments.add(appointment);
                            }
                        }
                    }
                }

                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingIndicator.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Failed to load appointments: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        appointmentsRef.addValueEventListener(appointmentsListener);
    }

    private void updateUI() {
        if (appointments.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            adapter.updateData(appointments);
        }
    }

    private void openAppointmentDetail(AppointmentDetail appointment) {
        Intent intent = new Intent(getActivity(), AppointmentDetailActivity.class);
        intent.putExtra("appointmentId", appointment.getId());
        startActivity(intent);
    }

    private void makePhoneCall(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Phone number not available", Toast.LENGTH_SHORT).show();
        }
    }
    
    private AppointmentDetail convertToAppointmentDetail(UserAppointment userAppointment) {
        String doctorPhone = userAppointment.getDoctorPhone();
        
        // If phone is null or empty, try to fetch from doctor profile
        if ((doctorPhone == null || doctorPhone.isEmpty()) && userAppointment.getDoctorId() != null) {
            FirebaseDatabase.getInstance()
                    .getReference("doctorProfiles")
                    .child(userAppointment.getDoctorId())
                    .child("contactNumber")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists() && snapshot.getValue() != null) {
                            String phone = snapshot.getValue(String.class);
                            if (phone != null && !phone.isEmpty()) {
                                userAppointment.setDoctorPhone(phone);
                            }
                        } else {
                            // Try users node as fallback
                            FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child(userAppointment.getDoctorId())
                                    .child("phone")
                                    .get()
                                    .addOnSuccessListener(phoneSnapshot -> {
                                        if (phoneSnapshot.exists() && phoneSnapshot.getValue() != null) {
                                            String phone = phoneSnapshot.getValue(String.class);
                                            if (phone != null && !phone.isEmpty()) {
                                                userAppointment.setDoctorPhone(phone);
                                            }
                                        }
                                    });
                        }
                    });
        }
        
        return new AppointmentDetail(
                userAppointment.getId(),
                userAppointment.getDoctorId(),
                userAppointment.getDoctorName(),
                userAppointment.getDoctorSpeciality(),
                userAppointment.getDoctorImage(),
                userAppointment.getAppointmentDate(),
                userAppointment.getAppointmentTime(),
                doctorPhone != null ? doctorPhone : "", // doctor phone
                userAppointment.getStatus(),
                userAppointment.getTimestamp()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (appointmentsRef != null && appointmentsListener != null) {
            appointmentsRef.removeEventListener(appointmentsListener);
        }
    }
}