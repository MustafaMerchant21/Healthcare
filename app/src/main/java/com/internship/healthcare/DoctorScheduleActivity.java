package com.internship.healthcare;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.databinding.ActivityDoctorScheduleBinding;
import com.internship.healthcare.databinding.ItemDayScheduleBinding;
import com.internship.healthcare.models.DoctorSchedule;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
/**
 * DoctorScheduleActivity.java
 * A comprehensive healthcare management Android application
 * Activity managing doctor's working hours and availability. Integrates with Firebase Authentication, Realtime Database.
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


public class DoctorScheduleActivity extends AppCompatActivity {

    private ActivityDoctorScheduleBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference scheduleRef;
    private DoctorSchedule doctorSchedule;

    private Map<String, DayViewHolder> dayViewHolders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        scheduleRef = FirebaseDatabase.getInstance().getReference("doctorSchedules").child(userId);

        doctorSchedule = new DoctorSchedule(userId);
        dayViewHolders = new HashMap<>();

        setupToolbar();
        setupDurationSlider();
        setupDayViews();
        loadSchedule();
    
        setupSaveButton();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDurationSlider() {
        binding.sliderDuration.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                int minutes = (int) value;
                binding.tvDurationValue.setText(minutes + " minutes");
                doctorSchedule.setAppointmentDuration(minutes);
            }
        });
    }

    private void setupDayViews() {
        setupDayView("monday", "Monday", binding.scheduleMonday);
        setupDayView("tuesday", "Tuesday", binding.scheduleTuesday);
        setupDayView("wednesday", "Wednesday", binding.scheduleWednesday);
        setupDayView("thursday", "Thursday", binding.scheduleThursday);
        setupDayView("friday", "Friday", binding.scheduleFriday);
        setupDayView("saturday", "Saturday", binding.scheduleSaturday);
        setupDayView("sunday", "Sunday", binding.scheduleSunday);
    }

    private void setupDayView(String dayKey, String dayName, ItemDayScheduleBinding dayBinding) {
    
        DayViewHolder holder = new DayViewHolder(dayBinding, dayKey, dayName);
        dayViewHolders.put(dayKey, holder);
    }

    private void loadSchedule() {
        binding.progressIndicator.setVisibility(View.VISIBLE);
    

        scheduleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                binding.progressIndicator.setVisibility(View.GONE);
    

                if (snapshot.exists()) {
                    doctorSchedule = snapshot.getValue(DoctorSchedule.class);
                    if (doctorSchedule != null) {
                        updateUI();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                binding.progressIndicator.setVisibility(View.GONE);
                Toast.makeText(DoctorScheduleActivity.this,
                        "Error loading schedule: " + error.getMessage(),
    
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        binding.sliderDuration.setValue(doctorSchedule.getAppointmentDuration());
        binding.tvDurationValue.setText(doctorSchedule.getAppointmentDuration() + " minutes");

        for (Map.Entry<String, DayViewHolder> entry : dayViewHolders.entrySet()) {
            String dayKey = entry.getKey();
            DayViewHolder holder = entry.getValue();
    
            DoctorSchedule.DaySchedule daySchedule = doctorSchedule.getDaySchedule(dayKey);

            if (daySchedule != null) {
                holder.updateWithSchedule(daySchedule);
            }
        }
    }

    private void setupSaveButton() {
        binding.btnSaveSchedule.setOnClickListener(v -> saveSchedule());
    }
    

    private void saveSchedule() {
        binding.progressIndicator.setVisibility(View.VISIBLE);
        binding.btnSaveSchedule.setEnabled(false);

        // Collect all day schedules
        for (Map.Entry<String, DayViewHolder> entry : dayViewHolders.entrySet()) {
    
            String dayKey = entry.getKey();
            DayViewHolder holder = entry.getValue();
            DoctorSchedule.DaySchedule daySchedule = holder.getSchedule();
            doctorSchedule.setDaySchedule(dayKey, daySchedule);
        }

        doctorSchedule.setLastUpdated(System.currentTimeMillis());

        scheduleRef.setValue(doctorSchedule)
                .addOnSuccessListener(aVoid -> {
                    binding.progressIndicator.setVisibility(View.GONE);
                    binding.btnSaveSchedule.setEnabled(true);
                    Snackbar.make(binding.getRoot(), "Schedule saved successfully!",
                            Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
    
                    binding.progressIndicator.setVisibility(View.GONE);
                    binding.btnSaveSchedule.setEnabled(true);
                    Toast.makeText(DoctorScheduleActivity.this,
                            "Error saving schedule: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    // ViewHolder class for each day
    private class DayViewHolder {
        private final String dayKey;
        private final String dayName;

    
        private final SwitchMaterial switchAvailable;
        private final View layoutTimeDetails;
        private final TextInputEditText etStartTime;
        private final TextInputEditText etEndTime;

        public DayViewHolder(ItemDayScheduleBinding dayBinding, String dayKey, String dayName) {
            this.dayKey = dayKey;
            this.dayName = dayName;

            // Use binding directly
            switchAvailable = dayBinding.switchAvailable;
            layoutTimeDetails = dayBinding.layoutTimeDetails;
            etStartTime = dayBinding.etStartTime;
            etEndTime = dayBinding.etEndTime;

            dayBinding.tvDayName.setText(dayName);

    
            setupListeners();
        }

        private void setupListeners() {
            // Available switch
            switchAvailable.setOnCheckedChangeListener((buttonView, isChecked) -> {
    
                layoutTimeDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                switchAvailable.setText(isChecked ? "Available" : "Unavailable");
            });

            // Time pickers
            etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
            etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));
        }

        private void showTimePicker(TextInputEditText editText) {
            String currentTime = editText.getText().toString();
            String[] parts = currentTime.split(":");
            int hour = parts.length > 0 ? Integer.parseInt(parts[0]) : 9;
            int minute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    DoctorScheduleActivity.this,
                    (view, selectedHour, selectedMinute) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d",
                                selectedHour, selectedMinute);
                        editText.setText(time);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        }

        public void updateWithSchedule(DoctorSchedule.DaySchedule schedule) {
            switchAvailable.setChecked(schedule.isAvailable());
            layoutTimeDetails.setVisibility(schedule.isAvailable() ? View.VISIBLE : View.GONE);
            
            switchAvailable.setText(schedule.isAvailable() ? "Available" : "Unavailable");

            etStartTime.setText(schedule.getStartTime());
            etEndTime.setText(schedule.getEndTime());
        }

        public DoctorSchedule.DaySchedule getSchedule() {
            DoctorSchedule.DaySchedule schedule = new DoctorSchedule.DaySchedule();
            schedule.setAvailable(switchAvailable.isChecked());

            if (switchAvailable.isChecked()) {
                schedule.setStartTime(etStartTime.getText().toString());
                schedule.setEndTime(etEndTime.getText().toString());
            }

            return schedule;
        }
    }
}

    
    
    