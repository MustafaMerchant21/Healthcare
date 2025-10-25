package com.internship.healthcare.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * AppointmentStatusUpdater.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.utils
 * Utility class to automatically update appointment statuses
 * based on their dates
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */



public class AppointmentStatusUpdater {

    private static final String TAG = "AppointmentStatusUpdater";

    
    public static void updateExpiredAppointments() {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointments");

        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    String appointmentId = appointmentSnapshot.getKey();
                    String status = appointmentSnapshot.child("status").getValue(String.class);
                    String dateStr = appointmentSnapshot.child("appointmentDate").getValue(String.class);
                    String timeStr = appointmentSnapshot.child("appointmentTime").getValue(String.class);
                    String doctorId = appointmentSnapshot.child("doctorId").getValue(String.class);
                    Boolean patientCounted = appointmentSnapshot.child("patientCounted").getValue(Boolean.class);

    
                    // Only update approved appointments (scheduled/upcoming/pending means still waiting)
                    // Approved means doctor accepted the appointment
                    if (status != null && dateStr != null && doctorId != null &&
                            status.equals("approved")) {

                        if (isDateTimePassed(dateStr, timeStr)) {
                            if (patientCounted == null || !patientCounted) {
                                appointmentSnapshot.getRef().child("status").setValue("completed");
                                appointmentSnapshot.getRef().child("patientCounted").setValue(true);
                                
                                // Increment doctor's patient count
                                incrementDoctorPatientCount(doctorId);
                            } else if (!"completed".equals(status)) {
                                // Just update status if already counted
                                appointmentSnapshot.getRef().child("status").setValue("completed");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    
    private static void incrementDoctorPatientCount(String doctorId) {
        DatabaseReference doctorRef = FirebaseDatabase.getInstance()
                .getReference("doctorProfiles")
                .child(doctorId)
                .child("totalPatients");
    

        doctorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer currentCount = snapshot.getValue(Integer.class);
                if (currentCount == null) {
                    currentCount = 0;
                }
                doctorRef.setValue(currentCount + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    
    
    private static boolean isDateTimePassed(String dateStr, String timeStr) {
        try {
            // Combine date and time
            String dateTimeStr = dateStr;
            if (timeStr != null && !timeStr.isEmpty()) {
                dateTimeStr += " " + timeStr;
            }

            // Try different date formats - PRIORITIZE formats with year
            SimpleDateFormat[] formats = {
                    new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.ENGLISH),
                    new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH),
                    new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH),
    
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH),
                    new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            };

            Date appointmentDateTime = null;
            for (SimpleDateFormat format : formats) {
                try {
                    appointmentDateTime = format.parse(dateTimeStr);
                    if (appointmentDateTime != null) break;
                } catch (ParseException e) {
                    // Try next format
                }
            }

            if (appointmentDateTime != null) {
                Date now = new Date();
                return now.after(appointmentDateTime);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
