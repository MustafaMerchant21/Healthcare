package com.internship.healthcare.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.internship.healthcare.AppointmentDetailActivity;
import com.internship.healthcare.R;
import com.internship.healthcare.adapters.AppointmentDetailAdapter;
import com.internship.healthcare.models.AppointmentDetail;

import java.util.ArrayList;
import java.util.List;
/**
 * PatientAppointmentsFragment.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.fragments
* Fragment showing appointments booked by patients with this doctor
* (For Doctor view only)
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */
public class PatientAppointmentsFragment extends Fragment {

    private RecyclerView appointmentsRecycler;
    private LinearLayout emptyState;
    private AppointmentDetailAdapter adapter;
    private List<AppointmentDetail> appointments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_appointments, container, false);

        appointmentsRecycler = view.findViewById(R.id.tab_appointments_recycler);
        emptyState = view.findViewById(R.id.tab_empty_state);

        appointmentsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        loadPatientAppointments();

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
        appointmentsRecycler.setAdapter(adapter);

        updateEmptyState();

        return view;
    }
    private void loadPatientAppointments() {
        appointments = new ArrayList<>();
        // Sample data
        appointments.add(new AppointmentDetail(
                "p1", "currentUser", "John Doe", "General Checkup",
                "", "Oct 20", "Fri", "+1234567890", "upcoming", System.currentTimeMillis()
        ));
        appointments.add(new AppointmentDetail(
                "p2", "currentUser", "Jane Smith", "Consultation",

                "", "Oct 22", "Sun", "+1234567891", "upcoming", System.currentTimeMillis()
        ));
    }

    private void updateEmptyState() {
        if (appointments.isEmpty()) {
            appointmentsRecycler.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            appointmentsRecycler.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void openAppointmentDetail(AppointmentDetail appointment) {
        Intent intent = new Intent(getContext(), AppointmentDetailActivity.class);
        intent.putExtra("appointment_id", appointment.getId());
        startActivity(intent);
    }

    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}