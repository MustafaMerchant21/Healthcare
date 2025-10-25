package com.internship.healthcare.fragments;

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
import com.internship.healthcare.R;
import com.internship.healthcare.adapters.AppointmentRequestAdapter;
import com.internship.healthcare.models.UserAppointment;

import java.util.ArrayList;
import java.util.List;
/**
 * AppointmentRequestsFragment.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.fragments
 * Fragment for doctors to manage incoming appointment requests.
 *
 * <p>Extends: {@link Fragment}</p>
 * <p>Implements: {@link AppointmentRequestAdapter.OnRequestActionListener}</p>
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
public class AppointmentRequestsFragment extends Fragment implements AppointmentRequestAdapter.OnRequestActionListener {

    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private ProgressBar loadingIndicator;
    private TextView emptyMessage;
    
    private AppointmentRequestAdapter adapter;
    private DatabaseReference doctorAppointmentsRef;

    private ValueEventListener appointmentsListener;
    private FirebaseAuth auth;
    private String filterStatus; // "pending", "approved", "rejected"

    public static AppointmentRequestsFragment newInstance(String status) {
        AppointmentRequestsFragment fragment = new AppointmentRequestsFragment();
        Bundle args = new Bundle();
        args.putString("status", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterStatus = getArguments().getString("status", "pending");
        }
        auth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment_requests, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.requests_recycler_view);
        emptyState = view.findViewById(R.id.empty_state);
        loadingIndicator = view.findViewById(R.id.loading_indicator);
        emptyMessage = view.findViewById(R.id.empty_message);

        adapter = new AppointmentRequestAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        updateEmptyMessage();

        loadAppointments();
    }

    private void updateEmptyMessage() {
        switch (filterStatus) {
            case "pending":
                emptyMessage.setText("No pending requests");
                break;
            case "approved":
                emptyMessage.setText("No approved appointments");
                break;
            case "rejected":
                emptyMessage.setText("No rejected requests");
                break;
        }
    }

    private void loadAppointments() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please login to view appointments", Toast.LENGTH_SHORT).show();
            return;
        }

        String doctorId = auth.getCurrentUser().getUid();
        doctorAppointmentsRef = FirebaseDatabase.getInstance()
                .getReference("doctorAppointments")
                .child(doctorId);

        loadingIndicator.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        appointmentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingIndicator.setVisibility(View.GONE);
                
                List<UserAppointment> appointments = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    UserAppointment appointment = child.getValue(UserAppointment.class);
                    if (appointment != null) {
                        appointments.add(appointment);
                    }
                }

                if (appointments.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setAppointments(appointments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingIndicator.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Failed to load appointments: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };
        
        doctorAppointmentsRef.orderByChild("status")
                .equalTo(filterStatus)
                .addValueEventListener(appointmentsListener);
    }

    @Override
    public void onAccept(UserAppointment appointment) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Accept Appointment")
                .setMessage("Are you sure you want to accept this appointment with " + appointment.getPatientName() + "?")
                .setPositiveButton("Accept", (dialog, which) -> updateAppointmentStatus(appointment, "approved"))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onReject(UserAppointment appointment) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Reject Appointment")
                .setMessage("Are you sure you want to reject this appointment request?")
                .setPositiveButton("Reject", (dialog, which) -> updateAppointmentStatus(appointment, "rejected"))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateAppointmentStatus(UserAppointment appointment, String newStatus) {
        if (auth.getCurrentUser() == null) return;

        String doctorId = auth.getCurrentUser().getUid();
        
        Toast.makeText(getContext(), "Updating appointment...", Toast.LENGTH_SHORT).show();

        DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference("appointments").child(appointment.getId());
        DatabaseReference userAppointmentRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(appointment.getUserId())
                .child("appointments")
                .child(appointment.getId());
        DatabaseReference doctorAppointmentRef = FirebaseDatabase.getInstance()
                .getReference("doctorAppointments")
                .child(doctorId)
                .child(appointment.getId());

        appointmentRef.child("status").setValue(newStatus);
        userAppointmentRef.child("status").setValue(newStatus);
        doctorAppointmentRef.child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    String message = "approved".equals(newStatus) ? 
                            "Appointment approved! Patient will be notified." :
                            "Appointment rejected.";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    
                    sendNotificationToPatient(appointment, newStatus);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update appointment: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void sendNotificationToPatient(UserAppointment appointment, String status) {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(appointment.getUserId());
        
        String notificationId = notificationsRef.push().getKey();
        if (notificationId == null) return;

        String title = "approved".equals(status) ? "Appointment Confirmed!" : "Appointment Update";
        String message = "approved".equals(status) ?
                "Dr. " + appointment.getDoctorName() + " has approved your appointment for " + 
                appointment.getAppointmentDate() + " at " + appointment.getAppointmentTime() :
                "Dr. " + appointment.getDoctorName() + " couldn't accept your appointment request.";

        java.util.Map<String, Object> notification = new java.util.HashMap<>();
        notification.put("id", notificationId);
        notification.put("userId", appointment.getUserId());
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("type", "appointment_" + status);
        notification.put("relatedId", appointment.getId());
        notification.put("doctorId", appointment.getDoctorId());
        notification.put("doctorName", appointment.getDoctorName());
        notification.put("doctorImage", appointment.getDoctorImage());
        notification.put("read", false);

        notificationsRef.child(notificationId).setValue(notification);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (doctorAppointmentsRef != null && appointmentsListener != null) {
            doctorAppointmentsRef.orderByChild("status")
                    .equalTo(filterStatus)
                    .removeEventListener(appointmentsListener);
        }
    }
}