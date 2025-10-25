package com.internship.healthcare;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.models.Doctor;
import com.internship.healthcare.models.DaySchedule;
import com.internship.healthcare.models.UserAppointment;
import com.internship.healthcare.utils.MessagingUtils;
import com.internship.healthcare.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
/**
 * DoctorDetailsActivity.java
 * A comprehensive healthcare management Android application
 * Activity displaying comprehensive doctor profile and booking interface. Integrates with Firebase Authentication, Realtime Database.
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


public class DoctorDetailsActivity extends AppCompatActivity {

    private ImageButton backButton, favoriteButton, slideButton;
    private MaterialCardView chatButton, callButton;
    private TextView doctorAvatar, doctorName, doctorSpecialty, doctorRating;
    private TextView doctorExperience, readMoreButton, selectedDateText, slideText;
    private TextView doctorDescription, doctorDegree, doctorUniversity;
    private TextView doctorFee, patientsCount;
    private ImageView doctorAvatarImage;

    private LinearLayout datesContainer;
    private ChipGroup morningTimeSlots, afternoonTimeSlots, eveningTimeSlots, nightTimeSlots;
    private FrameLayout slideButtonContainer;

    private Doctor doctor;
    private String selectedDate = "";
    private String selectedTime = "";
    private Calendar selectedCalendar;

    String name;
    String speciality;
    String image;
    double consultationFee;
    String phone;
    double rating;
    int experience;
    String about;
    String degree;
    String university;
    
    private boolean isFavorite = false;
    private boolean isDescriptionExpanded = false;
    private float slideButtonInitialX = 0f;
    private float maxSlideDistance = 0f;
    private boolean isSliding = false;
    private boolean appointmentBooked = false;

    private FirebaseAuth auth;
    private DatabaseReference appointmentsRef;
    private DatabaseReference favoritesRef;
    private DatabaseReference doctorProfilesRef;
    private DatabaseReference usersRef;
    private DatabaseReference doctorSchedulesRef;
    
    private String doctorId;
    private int appointmentDuration = 30; // Default 30 minutes
    private java.util.Map<String, DaySchedule> weekSchedule;

    // Array of avatar background colors
    private final String[] avatarColors = {
        "#64B5F6", "#F06292", "#FFB74D", "#BA68C8",
        "#4DB6AC", "#81C784", "#FFD54F", "#FF8A65"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        auth = FirebaseAuth.getInstance();
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        doctorProfilesRef = FirebaseDatabase.getInstance().getReference("doctorProfiles");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        doctorSchedulesRef = FirebaseDatabase.getInstance().getReference("doctorSchedules");
        
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            favoritesRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("favoriteDoctors");
        }

        initializeViews();

        doctorId = getIntent().getStringExtra("doctorId");
        
    
        if (doctorId != null && !doctorId.isEmpty()) {
            loadDoctorFromFirebase(doctorId);
        } else {
            // Fallback to intent data if no doctorId
            getDoctorDataFromIntent();
            setupDoctorInfo();
        }

        setupClickListeners();
        setupDateSelector();
        setupTimeSlots();
        setupSlideButton();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        favoriteButton = findViewById(R.id.favorite_button);
        doctorAvatar = findViewById(R.id.doctor_avatar);
        doctorAvatarImage = findViewById(R.id.doctor_avatar_image);
        doctorName = findViewById(R.id.doctor_name);
        doctorSpecialty = findViewById(R.id.doctor_specialty);
        doctorRating = findViewById(R.id.doctor_rating);
        doctorExperience = findViewById(R.id.doctor_experience);
        doctorDescription = findViewById(R.id.doctor_description);
        doctorDegree = findViewById(R.id.doctor_degree);
        doctorUniversity = findViewById(R.id.doctor_university);
        doctorFee = findViewById(R.id.doctor_fee);
        patientsCount = findViewById(R.id.patients_count);
        chatButton = findViewById(R.id.chat_button);
        callButton = findViewById(R.id.call_button);
        readMoreButton = findViewById(R.id.read_more_button);
        selectedDateText = findViewById(R.id.selected_date_text);
        datesContainer = findViewById(R.id.dates_container);
        morningTimeSlots = findViewById(R.id.morning_time_slots);
        afternoonTimeSlots = findViewById(R.id.afternoon_time_slots);
        eveningTimeSlots = findViewById(R.id.evening_time_slots);
        nightTimeSlots = findViewById(R.id.night_time_slots);
        slideButtonContainer = findViewById(R.id.slide_button_container);
        slideButton = findViewById(R.id.slide_button);
        slideText = findViewById(R.id.slide_text);
    
    }

    private void getDoctorDataFromIntent() {
        String doctorId = getIntent().getStringExtra("doctorId");
        name = getIntent().getStringExtra("doctorName");
        speciality = getIntent().getStringExtra("doctorSpeciality");
        image = getIntent().getStringExtra("doctorImage");
        consultationFee = getIntent().getDoubleExtra("consultationFee", 850.0);
        phone = getIntent().getStringExtra("doctorPhone");
        rating = getIntent().getDoubleExtra("doctorRating", 4.9);
        experience = getIntent().getIntExtra("doctorExperience", 7);
        about = getIntent().getStringExtra("doctorAbout");
        degree = getIntent().getStringExtra("doctorDegree");
        university = getIntent().getStringExtra("doctorUniversity");

        doctor = new Doctor(
            doctorId != null ? doctorId : "d1", 
            name != null ? name : "Dr. Farhana Akter",
            speciality != null ? speciality : "Gynecologist",
            image != null ? image : "",
            consultationFee,
            phone != null ? phone : "+1234567890",
            rating,
            experience,
            about != null ? about : "Experienced healthcare professional specializing in " + 
                (speciality != null ? speciality.toLowerCase() : "medical care") + 
                " with comprehensive patient care and treatment.",
            degree != null ? degree : "MBBS, MD - " + (speciality != null ? speciality : "Medicine"),
            university != null ? university : ""
        );
    
    }

    private void loadDoctorFromFirebase(String doctorId) {
        
        // First, load doctor profile data
        doctorProfilesRef.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot profileSnapshot) {
                if (profileSnapshot.exists()) {
                    String specialty = profileSnapshot.child("specialty").getValue(String.class);
                    String degree = profileSnapshot.child("degree").getValue(String.class);
                    String university = profileSnapshot.child("university").getValue(String.class);
                    Integer experienceYears = profileSnapshot.child("experienceYears").getValue(Integer.class);
                    Integer experience = profileSnapshot.child("experience").getValue(Integer.class);
                    Double consultationFee = profileSnapshot.child("consultationFee").getValue(Double.class);
                    String bio = profileSnapshot.child("bio").getValue(String.class);
                    String about = profileSnapshot.child("about").getValue(String.class);
                    String clinicAddress = profileSnapshot.child("clinicAddress").getValue(String.class);
                    String contactNumber = profileSnapshot.child("contactNumber").getValue(String.class);
                    String profileImageUrl = profileSnapshot.child("profileImageUrl").getValue(String.class);
                    Double rating = profileSnapshot.child("rating").getValue(Double.class);
                    Integer totalPatients = profileSnapshot.child("totalPatients").getValue(Integer.class);
                    
                    final int patientCount = totalPatients != null ? totalPatients : 0;
                    
                    final String clinic = clinicAddress;
                    
                    // Now load user data (name, phone) from users node
                    usersRef.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            String name = userSnapshot.child("name").getValue(String.class);
    
                            String phone = userSnapshot.child("phone").getValue(String.class);
                            
                            doctor = new Doctor(
                                doctorId,
                                name != null ? name : "Dr. Unknown",
                                specialty != null ? specialty : "General Physician",
                                profileImageUrl != null ? profileImageUrl : "",
                                consultationFee != null ? consultationFee : 500.0,
                                contactNumber != null ? contactNumber : (phone != null ? phone : ""),
    
                                rating != null ? rating : 4.5,
                                experienceYears != null ? experienceYears : (experience != null ? experience : 5),
                                bio != null && !bio.isEmpty() ? bio : (about != null ? about : ""),
                                degree != null ? degree : "MBBS",
                                university != null ? university : "Medical College",
                                clinic != null && !clinic.isEmpty() ? clinic : ""
                            );
                            
                            setupDoctorInfo();
                            
                            updatePatientCount(patientCount);
                            
                            loadDoctorSchedule(doctorId);
                        }
                        
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(DoctorDetailsActivity.this, 
                                "Failed to load doctor info: " + error.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                            // Try to use intent data as fallback
                            getDoctorDataFromIntent();
                            setupDoctorInfo();
                        }
                    });
                } else {
                    Toast.makeText(DoctorDetailsActivity.this, 
    
                        "Doctor profile not found", 
                        Toast.LENGTH_SHORT).show();
                    // Try to use intent data as fallback
                    getDoctorDataFromIntent();
                    setupDoctorInfo();
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorDetailsActivity.this, 
                    "Failed to load doctor data: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                // Try to use intent data as fallback
                getDoctorDataFromIntent();
                setupDoctorInfo();
            }
        });
    }

    private void setupDoctorInfo() {
        if (doctor == null) {
            Toast.makeText(this, "Doctor information not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        String imageUrl = doctor.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            doctorAvatarImage.setVisibility(View.VISIBLE);
            doctorAvatar.setVisibility(View.GONE);
    
            
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile) // Optional placeholder
                .error(R.drawable.ic_profile) // Optional error image
                .circleCrop() // Make it circular
                .into(doctorAvatarImage);
        } else {
            doctorAvatarImage.setVisibility(View.GONE);
            doctorAvatar.setVisibility(View.VISIBLE);
            
            String initials = getInitials(doctor.getName());
            doctorAvatar.setText(initials);
            int colorIndex = Math.abs(doctor.getName().hashCode()) % avatarColors.length;
            doctorAvatar.setBackgroundColor(Color.parseColor(avatarColors[colorIndex]));
        }

        doctorName.setText(doctor.getName());
        
        String specialtyText = doctor.getSpeciality();
        String clinicAddress = doctor.getClinicAddress();
        if (clinicAddress != null && !clinicAddress.isEmpty()) {
            doctorSpecialty.setText(specialtyText + " • " + clinicAddress);
        } else {
    
            // Fallback to default hospital name if no clinic address
            doctorSpecialty.setText(specialtyText + " • ");
        }
        
        doctorRating.setText(String.format(Locale.getDefault(), "%.1f", doctor.getRating()));
        doctorExperience.setText(doctor.getExperience() + " Years");
        
        doctorFee.setText("₹" + String.format(Locale.getDefault(), "%.0f", doctor.getConsultationFee()));
        
        patientsCount.setText("0");
        
        String about = doctor.getAbout();
        if (about != null && !about.isEmpty()) {
            doctorDescription.setText(about);
        } else {
    
            doctorDescription.setText("Specialist in " + doctor.getSpeciality().toLowerCase() + 
                " with " + doctor.getExperience() + " years of trusted experience in providing " +
                "quality healthcare services to patients.");
        }
        
        String degree = doctor.getDegree();
        String university = doctor.getUniversity();
        if (degree != null && !degree.isEmpty()) {
            doctorDegree.setText(degree);
        } else {
            doctorDegree.setText("MBBS, MD - " + doctor.getSpeciality());
        }
        
        if (university != null && !university.isEmpty()) {
            doctorUniversity.setText(university);
        } else {
            doctorUniversity.setText("Dhaka Medical College");
        }
        
        checkFavoriteStatus();
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "D";
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    private void updatePatientCount(int count) {
        if (count >= 100) {
            patientsCount.setText(String.format(Locale.getDefault(), "%.1fK+", count / 1000.0));
        } else if (count > 0) {
            patientsCount.setText(count + "+");
        } else {
            patientsCount.setText("New");
        }
    }

    private void loadDoctorSchedule(String doctorId) {
        doctorSchedulesRef.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer duration = snapshot.child("appointmentDuration").getValue(Integer.class);
                    if (duration != null) {
                        appointmentDuration = duration;
                    }
                    
                    weekSchedule = new HashMap<>();
                    DataSnapshot scheduleSnapshot = snapshot.child("weekSchedule");
                    for (DataSnapshot daySnapshot : scheduleSnapshot.getChildren()) {
                        String day = daySnapshot.getKey();
                        DaySchedule daySchedule = daySnapshot.getValue(DaySchedule.class);
                        if (day != null && daySchedule != null) {
                            weekSchedule.put(day.toLowerCase(), daySchedule);
                        }
                    }
                    
                    // Refresh date selector and time slots with schedule data
                    refreshDateSelectorWithSchedule();
                    updateTimeSlotsForSelectedDate();
                } else {
                    // No schedule found, keep default slots
                    Toast.makeText(DoctorDetailsActivity.this, 
                        "Doctor schedule not available", 
                        Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
    
                Toast.makeText(DoctorDetailsActivity.this, 
                    "Failed to load schedule: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshDateSelectorWithSchedule() {
        // Keep existing date cards but update their availability
        // The visual update will happen when they are clicked
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    

        favoriteButton.setOnClickListener(v -> toggleFavorite());

        readMoreButton.setOnClickListener(v -> expandDescription());

        selectedDateText.setOnClickListener(v -> showDatePicker());
        
        // Chat button click listener
        chatButton.setOnClickListener(v -> openChat());
        
        callButton.setOnClickListener(v -> makeCall());
    }

    private void setupDateSelector() {
    
        selectedCalendar = Calendar.getInstance();
        
        for (int i = 0; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, i);
            
            MaterialCardView dateCard = createDateCard(calendar, i == 0);
    
            datesContainer.addView(dateCard);
        }

        updateSelectedDate(Calendar.getInstance());
    }

    private MaterialCardView createDateCard(Calendar calendar, boolean isSelected) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            (int) (70 * getResources().getDisplayMetrics().density),
            (int) (90 * getResources().getDisplayMetrics().density)
        );
        params.setMargins(0, 0, (int) (12 * getResources().getDisplayMetrics().density), 0);
        card.setLayoutParams(params);
        card.setRadius(1000);
        card.setCardElevation(0);
        card.setStrokeWidth(0);
        card.setStrokeColor(Color.parseColor("#FFFFFF00"));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(android.view.Gravity.CENTER);
        content.setPadding(
            (int) (8 * getResources().getDisplayMetrics().density),
            (int) (12 * getResources().getDisplayMetrics().density),
            (int) (8 * getResources().getDisplayMetrics().density),
            (int) (12 * getResources().getDisplayMetrics().density)
        );

        // Day of week
        TextView dayText = new TextView(this);
        dayText.setText(new SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.getTime()).substring(0, 3));
        dayText.setTextSize(12);
    
        dayText.setGravity(android.view.Gravity.CENTER);
        
        // Date number
        TextView dateText = new TextView(this);
        dateText.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        dateText.setTextSize(20);
        dateText.setTypeface(null, android.graphics.Typeface.BOLD);
        dateText.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
    
        dateParams.setMargins(0, (int) (4 * getResources().getDisplayMetrics().density), 0, 0);
        dateText.setLayoutParams(dateParams);

        content.addView(dayText);
        content.addView(dateText);
        card.addView(content);

    
        String dayName = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime()).toLowerCase();
        boolean isDoctorAvailable = isDoctorAvailableOnDay(dayName);

        if (!isDoctorAvailable) {
            // Grey out unavailable days
            card.setCardBackgroundColor(Color.parseColor("#E0E0E0"));
            card.setStrokeColor(Color.parseColor("#BDBDBD"));
            dayText.setTextColor(Color.parseColor("#9E9E9E"));
            dateText.setTextColor(Color.parseColor("#9E9E9E"));
        } else if (isSelected) {
            card.setCardBackgroundColor(Color.parseColor("#4772F5"));
            card.setStrokeColor(Color.parseColor("#ffffff00"));
            dayText.setTextColor(Color.WHITE);
            dateText.setTextColor(Color.WHITE);
            selectedCalendar = (Calendar) calendar.clone();
            updateSelectedDate(calendar);
        } else {
    
            card.setCardBackgroundColor(Color.parseColor("#f4f8fb"));
            card.setStrokeColor(Color.parseColor("#2196F300"));
            dayText.setTextColor(Color.parseColor("#C4000000"));
            dateText.setTextColor(Color.parseColor("#C4000000"));
        }

        card.setOnClickListener(v -> {
            if (!isDoctorAvailable) {
                Toast.makeText(this, "Doctor is not available on " + 
                    new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime()), 
                    Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Deselect all cards
            for (int i = 0; i < datesContainer.getChildCount(); i++) {
    
                MaterialCardView otherCard = (MaterialCardView) datesContainer.getChildAt(i);
                LinearLayout otherContent = (LinearLayout) otherCard.getChildAt(0);
                TextView otherDayText = (TextView) otherContent.getChildAt(0);
                TextView otherDateText = (TextView) otherContent.getChildAt(1);
                
                Calendar otherCal = Calendar.getInstance();
                otherCal.add(Calendar.DAY_OF_YEAR, i);
                String otherDayName = new SimpleDateFormat("EEEE", Locale.getDefault())
                    .format(otherCal.getTime()).toLowerCase();
                boolean isOtherAvailable = isDoctorAvailableOnDay(otherDayName);
                
                if (!isOtherAvailable) {
                    otherCard.setCardBackgroundColor(Color.parseColor("#E0E0E0"));
                    otherCard.setStrokeColor(Color.parseColor("#BDBDBD"));
                    otherDayText.setTextColor(Color.parseColor("#9E9E9E"));
                    otherDateText.setTextColor(Color.parseColor("#9E9E9E"));
                } else {
                    otherCard.setCardBackgroundColor(Color.parseColor("#f4f8fb"));
                    otherCard.setStrokeColor(Color.parseColor("#2196F300"));
                    otherDayText.setTextColor(Color.parseColor("#757575"));
                    otherDateText.setTextColor(Color.parseColor("#212121"));
                }
            }

            // Select this card
            card.setCardBackgroundColor(Color.parseColor("#4772F5"));
            card.setStrokeColor(Color.parseColor("#2196F300"));
            dayText.setTextColor(Color.WHITE);
            dateText.setTextColor(Color.WHITE);
            
            selectedCalendar = (Calendar) calendar.clone();
            updateSelectedDate(calendar);
            
            updateTimeSlotsForSelectedDate();
        });

        return card;
    }

    private boolean isDoctorAvailableOnDay(String dayName) {
        if (weekSchedule == null || !weekSchedule.containsKey(dayName)) {
            return true; // Default to available if schedule not loaded
        }
        DaySchedule schedule = weekSchedule.get(dayName);
        return schedule != null && schedule.isAvailable();
    }

    private void updateSelectedDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        selectedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(calendar.getTime());
        selectedDateText.setText(dateFormat.format(calendar.getTime()));
    }

    private void showDatePicker() {
        Calendar calendar = selectedCalendar != null ? selectedCalendar : Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar newCalendar = Calendar.getInstance();
                newCalendar.set(year, month, dayOfMonth);
                
                selectedCalendar = newCalendar;
                updateSelectedDate(newCalendar);
                
                // Refresh date selector
                datesContainer.removeAllViews();
                for (int i = 0; i < 7; i++) {
                    Calendar cal = (Calendar) newCalendar.clone();
                    cal.add(Calendar.DAY_OF_YEAR, i - 3);
                    MaterialCardView dateCard = createDateCard(cal, i == 3);
                    datesContainer.addView(dateCard);
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setupTimeSlots() {
        // Initial setup with default slots
        // Real slots will be loaded when schedule is fetched
        String[] morningSlots = {"09:00 AM", "10:00 AM", "11:00 AM"};
        for (String time : morningSlots) {
            Chip chip = createTimeChip(time, morningTimeSlots, false);
            morningTimeSlots.addView(chip);
        }

        String[] afternoonSlots = {"12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM"};
        for (String time : afternoonSlots) {
            Chip chip = createTimeChip(time, afternoonTimeSlots, false);
            afternoonTimeSlots.addView(chip);
        }

        String[] eveningSlots = {"05:00 PM", "06:00 PM", "07:00 PM"};
        for (String time : eveningSlots) {
            Chip chip = createTimeChip(time, eveningTimeSlots, false);
            eveningTimeSlots.addView(chip);
        }

        String[] nightSlots = {"08:00 PM", "09:00 PM", "10:00 PM"};
        for (String time : nightSlots) {
            Chip chip = createTimeChip(time, nightTimeSlots, false);
            nightTimeSlots.addView(chip);
        }

        if (morningTimeSlots.getChildCount() > 0) {
            Chip firstChip = (Chip) morningTimeSlots.getChildAt(0);
            firstChip.setChecked(true);
            selectedTime = firstChip.getText().toString();
        }
    }

    private void updateTimeSlotsForSelectedDate() {
        if (selectedCalendar == null || weekSchedule == null) {
            return;
        }
        
        morningTimeSlots.removeAllViews();
        afternoonTimeSlots.removeAllViews();
    
        eveningTimeSlots.removeAllViews();
        nightTimeSlots.removeAllViews();
        
        String dayName = new SimpleDateFormat("EEEE", Locale.getDefault())
            .format(selectedCalendar.getTime()).toLowerCase();
        
        DaySchedule daySchedule = weekSchedule.get(dayName);
        if (daySchedule == null || !daySchedule.isAvailable()) {
            // No slots available
            return;
        }
        
        List<String> allSlots = generateTimeSlots(
    
            daySchedule.getStartTime(), 
            daySchedule.getEndTime(), 
            appointmentDuration
        );
        
        List<String> bookedSlots = getDummyBookedSlots(allSlots);
        
        // Separate into morning, afternoon, evening, and night
        for (String slot : allSlots) {
            boolean isBooked = bookedSlots.contains(slot);
    
            
            String timeOfDay = getTimeOfDay(slot);
            switch (timeOfDay) {
                case "morning":
                    Chip morningChip = createTimeChip(slot, morningTimeSlots, isBooked);
                    morningTimeSlots.addView(morningChip);
                    break;
                case "afternoon":
                    Chip afternoonChip = createTimeChip(slot, afternoonTimeSlots, isBooked);
                    afternoonTimeSlots.addView(afternoonChip);
                    break;
                case "evening":
                    Chip eveningChip = createTimeChip(slot, eveningTimeSlots, isBooked);
                    eveningTimeSlots.addView(eveningChip);
                    break;
                case "night":
                    Chip nightChip = createTimeChip(slot, nightTimeSlots, isBooked);
                    nightTimeSlots.addView(nightChip);
                    break;
            }
        }
        
        // Select first available slot
        selectFirstAvailableSlot();
    }

    private List<String> generateTimeSlots(String startTime, String endTime, int durationMinutes) {
        List<String> slots = new ArrayList<>();
        
        try {
            SimpleDateFormat format24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat format12 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    
            
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            
            start.setTime(format24.parse(startTime));
            end.setTime(format24.parse(endTime));
            
            while (start.before(end)) {
                slots.add(format12.format(start.getTime()));
                start.add(Calendar.MINUTE, durationMinutes);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return slots;
    }

    private List<String> getDummyBookedSlots(List<String> allSlots) {
        List<String> booked = new ArrayList<>();
        for (int i = 0; i < allSlots.size(); i++) {
            if (i % 3 == 1) { // Book every 3rd slot as an example
                booked.add(allSlots.get(i));
            }
        }
        return booked;
    }

    private String getTimeOfDay(String timeSlot) {
        // Categorize time slots:
        // Morning: 12:00 AM - 11:59 AM
        // Afternoon: 12:00 PM - 4:59 PM
        // Evening: 5:00 PM - 7:59 PM
        // Night: 8:00 PM - 11:59 PM
        try {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    
            Calendar time = Calendar.getInstance();
            time.setTime(format.parse(timeSlot));
            
            int hour = time.get(Calendar.HOUR_OF_DAY);
            
            if (hour >= 0 && hour < 12) {
                return "morning";
            } else if (hour >= 12 && hour < 17) {
                return "afternoon";
            } else if (hour >= 17 && hour < 20) {
                return "evening";
            } else {
                return "night";
            }
        } catch (ParseException e) {
            return "morning"; // default
        }
    }

    private void selectFirstAvailableSlot() {
        selectedTime = "";
        
        // Try morning slots first
        for (int i = 0; i < morningTimeSlots.getChildCount(); i++) {
            Chip chip = (Chip) morningTimeSlots.getChildAt(i);
            if (chip.isEnabled()) {
                chip.setChecked(true);
                selectedTime = chip.getText().toString();
                return;
            }
        }
        
        // Then try afternoon slots
        for (int i = 0; i < afternoonTimeSlots.getChildCount(); i++) {
            Chip chip = (Chip) afternoonTimeSlots.getChildAt(i);
            if (chip.isEnabled()) {
                chip.setChecked(true);
                selectedTime = chip.getText().toString();
                return;
            }
        }
        
        // Then try evening slots
        for (int i = 0; i < eveningTimeSlots.getChildCount(); i++) {
            Chip chip = (Chip) eveningTimeSlots.getChildAt(i);
            if (chip.isEnabled()) {
                chip.setChecked(true);
                selectedTime = chip.getText().toString();
                return;
            }
        }
        
        // Finally try night slots
        for (int i = 0; i < nightTimeSlots.getChildCount(); i++) {
            Chip chip = (Chip) nightTimeSlots.getChildAt(i);
            if (chip.isEnabled()) {
                chip.setChecked(true);
                selectedTime = chip.getText().toString();
                return;
            }
        }
    }

    private Chip createTimeChip(String time, ChipGroup group, boolean isBooked) {
        Chip chip = new Chip(this);
        chip.setText(time);
        chip.setCheckable(!isBooked); // Can't check if booked
        chip.setEnabled(!isBooked); // Disable if booked
        
        if (isBooked) {
            // Grey out booked slots
            chip.setChipBackgroundColor(ColorStateList.valueOf((Color.parseColor("#E0E0E0"))));
            chip.setTextColor(Color.parseColor("#9E9E9E"));
        } else {
            chip.setChipBackgroundColor(ColorStateList.valueOf((Color.parseColor("#f4f8fb"))));
            chip.setTextColor(Color.parseColor("#212121"));
        }
        
        chip.setChipStrokeColor(getColorStateList(android.R.color.transparent));
        chip.setChipStrokeWidth(0);
        chip.setCheckedIconVisible(false);

        if (!isBooked) {
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedTime = time;
                    chip.setChipBackgroundColor(ColorStateList.valueOf((Color.parseColor("#4772F5"))));
                    chip.setTextColor(Color.WHITE);
                    // Uncheck other group
                    if (group == morningTimeSlots) {
                        afternoonTimeSlots.clearCheck();
                    } else {
    
                        morningTimeSlots.clearCheck();
                    }
                } else {
                    chip.setChipBackgroundColor(ColorStateList.valueOf((Color.parseColor("#f4f8fb"))));
                    chip.setTextColor(Color.parseColor("#212121"));
                }
            });
        }

        return chip;
    }

    private void setupSlideButton() {
        slideButton.post(() -> {
            slideButtonInitialX = slideButton.getX();
            maxSlideDistance = slideButtonContainer.getWidth() - slideButton.getWidth() - 4;
        });

        slideButton.setOnTouchListener((v, event) -> {
            if (appointmentBooked) return true;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isSliding = true;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float newX = event.getRawX() - slideButton.getWidth() / 2f;
                    float containerX = slideButtonContainer.getX();
                    
                    // Calculate relative position
                    float relativeX = newX - containerX;
    
                    
                    // Constrain within bounds
                    if (relativeX >= slideButtonInitialX && relativeX <= slideButtonInitialX + maxSlideDistance) {
                        slideButton.setX(relativeX);
                        
                        float progress = (relativeX - slideButtonInitialX) / maxSlideDistance;
                        slideText.setAlpha(1 - progress);
                        
                        if (progress >= 0.95f) {
                            completeBooking();
                            return true;
                        }
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isSliding && !appointmentBooked) {
                        // Animate back to start
                        ObjectAnimator animator = ObjectAnimator.ofFloat(
                            slideButton, "x", slideButton.getX(), slideButtonInitialX
                        );
                        animator.setDuration(200);
                        animator.start();
                        
                        slideText.setAlpha(1f);
                        isSliding = false;
                    }
                    return true;
            }
            return false;
        });
    }

    private void completeBooking() {
        if (appointmentBooked) return;
        
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            return;
        }
    

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to book appointment", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Security check: Prevent doctors from booking appointments with themselves
        String currentUserId = auth.getCurrentUser().getUid();
        if (currentUserId.equals(doctorId)) {
            Toast.makeText(this, "You cannot book an appointment with yourself", Toast.LENGTH_SHORT).show();
            return;
        }

        showAppointmentSummaryBottomSheet();
    }

    private void showAppointmentSummaryBottomSheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_appointment_summary, null);
        bottomSheet.setContentView(view);

        // Find views
        ImageButton btnClose = view.findViewById(R.id.btn_close);
        TextView bsDoctorAvatar = view.findViewById(R.id.bs_doctor_avatar);
        ImageView bsDoctorAvatarImage = view.findViewById(R.id.bs_doctor_avatar_image);
        TextView bsDoctorName = view.findViewById(R.id.bs_doctor_name);
        TextView bsDoctorSpecialty = view.findViewById(R.id.bs_doctor_specialty);
        TextView bsAppointmentDate = view.findViewById(R.id.bs_appointment_date);
        TextView bsAppointmentTime = view.findViewById(R.id.bs_appointment_time);
        TextView bsConsultationFee = view.findViewById(R.id.bs_consultation_fee);
        com.google.android.material.button.MaterialButton btnPayNow = view.findViewById(R.id.btn_pay_now);

        bsDoctorName.setText(doctor.getName());
        
        bsDoctorSpecialty.setText(doctor.getSpeciality());
        
        String imageUrl = doctor.getImage();
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("default_url")) {
            bsDoctorAvatarImage.setVisibility(View.VISIBLE);
            bsDoctorAvatar.setVisibility(View.GONE);
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(bsDoctorAvatarImage);
        } else {
    
            bsDoctorAvatarImage.setVisibility(View.GONE);
            bsDoctorAvatar.setVisibility(View.VISIBLE);
            String initials = getInitials(doctor.getName());
            bsDoctorAvatar.setText(initials);
            int colorIndex = Math.abs(doctor.getName().hashCode()) % avatarColors.length;
            bsDoctorAvatar.setBackgroundColor(Color.parseColor(avatarColors[colorIndex]));
        }

        // Format and set appointment date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = outputFormat.format(inputFormat.parse(selectedDate));
            bsAppointmentDate.setText(formattedDate);
        } catch (ParseException e) {
            bsAppointmentDate.setText(selectedDate);
        }

        bsAppointmentTime.setText(selectedTime);

        bsConsultationFee.setText(String.format(Locale.getDefault(), "₹%.0f", doctor.getConsultationFee()));

        com.google.android.material.textfield.TextInputEditText etReason = view.findViewById(R.id.et_appointment_reason);

        btnClose.setOnClickListener(v -> bottomSheet.dismiss());

        // Request Appointment button
        btnPayNow.setOnClickListener(v -> {
            String reason = etReason.getText() != null ? etReason.getText().toString().trim() : "";
            
            if (reason.isEmpty()) {
                etReason.setError("Please provide a reason for appointment");
                etReason.requestFocus();
                return;
            }

            if (reason.length() < 10) {
                etReason.setError("Please provide more details (at least 10 characters)");
                etReason.requestFocus();
                return;
            }

            bottomSheet.dismiss();
            
            appointmentBooked = true;
            
            // Keep slide button at the end
            slideButton.setX(slideButtonInitialX + maxSlideDistance);
            
            slideText.setText("Processing...");
            slideText.setAlpha(1f);
            slideButton.setEnabled(false);
            
            showPaymentProcessingDialog(reason);
        });

    
        bottomSheet.show();
    }

    private void showPaymentProcessingDialog(String reason) {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Processing your request...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Simulate payment processing for 2 seconds
        new android.os.Handler().postDelayed(() -> {
            progressDialog.dismiss();
            
            Toast.makeText(this, "Sending appointment request...", Toast.LENGTH_SHORT).show();
            
            saveAppointmentToFirebase(reason);
        }, 2000);
    }

    private void saveAppointmentToFirebase(String reason) {
        String userId = auth.getCurrentUser().getUid();
        String appointmentId = appointmentsRef.push().getKey();

        if (appointmentId == null) {
            Toast.makeText(this, "Failed to create appointment request", Toast.LENGTH_SHORT).show();
            resetSlideButton();
            return;
        }

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
    
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String patientName = snapshot.child("name").getValue(String.class);
                String patientPhone = snapshot.child("phone").getValue(String.class);

                // - userId (patientId) = current logged-in user
                // - patientName/patientPhone = from current user's profile
                // - doctorId = doctor.getId() = the doctor being booked
                // - doctorName = doctor.getName() = the doctor's actual name
                // - doctorPhone = doctor's contact number
                UserAppointment appointment = new UserAppointment(
                    appointmentId,
                    userId,                           // PATIENT ID (current user)
                    doctor.getId(),                   // DOCTOR ID (doctor being booked)
                    doctor.getName(),                 // DOCTOR NAME (actual doctor's name)
                    doctor.getSpeciality(),          // DOCTOR SPECIALTY
                    doctor.getImage(),               // DOCTOR IMAGE
                    selectedDate,
                    selectedTime,
                    doctor.getConsultationFee(),
                    "pending", // Status is pending until doctor approves
                    System.currentTimeMillis(),
                    "", // notes
                    reason, // patient's reason for appointment
                    patientName != null ? patientName : "Patient",  // PATIENT NAME
                    patientPhone != null ? patientPhone : "",       // PATIENT PHONE
                    doctor.getMobile() != null ? doctor.getMobile() : "" // DOCTOR PHONE
                );

                DatabaseReference userAppointmentsRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("appointments")
                    .child(appointmentId);

                userAppointmentsRef.setValue(appointment)
                    .addOnSuccessListener(aVoid -> {
                        // Also save to global appointments collection
                        appointmentsRef.child(appointmentId).setValue(appointment)
                            .addOnSuccessListener(aVoid2 -> {
                                // Also save to doctor's appointment requests
                                DatabaseReference doctorAppointmentsRef = FirebaseDatabase.getInstance()
                                    .getReference("doctorAppointments")
                                    .child(doctor.getId())
                                    .child(appointmentId);
                                
                                doctorAppointmentsRef.setValue(appointment)
                                    .addOnSuccessListener(aVoid3 -> {
                                        slideText.setText("Request Sent!");
                                        slideButton.setImageResource(R.drawable.ic_heart);
                                        slideButtonContainer.setBackgroundColor(Color.parseColor("#4CAF50"));
                                        slideText.setTextColor(Color.WHITE);
                                        
                                        Toast.makeText(DoctorDetailsActivity.this, "Appointment request sent! Waiting for doctor's approval.", Toast.LENGTH_SHORT).show();
                                        
                                        slideButton.postDelayed(() -> {
                                            setResult(RESULT_OK);
                                            finish();
                                        }, 2000);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(DoctorDetailsActivity.this, "Failed to notify doctor: " + e.getMessage(),
                                                     Toast.LENGTH_SHORT).show();
                                        resetSlideButton();
                                    });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(DoctorDetailsActivity.this, "Failed to save appointment: " + e.getMessage(),
                                             Toast.LENGTH_SHORT).show();
                                resetSlideButton();
                            });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DoctorDetailsActivity.this, "Failed to create appointment request: " + e.getMessage(),
                                     Toast.LENGTH_SHORT).show();
                        resetSlideButton();
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorDetailsActivity.this, 
                    "Failed to fetch patient details: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                resetSlideButton();
            }
        });
    }

    private void resetSlideButton() {
        appointmentBooked = false;
        slideButton.setEnabled(true);
        
    
        ObjectAnimator animator = ObjectAnimator.ofFloat(
            slideButton, "x", slideButton.getX(), slideButtonInitialX
        );
        animator.setDuration(200);
        animator.start();
        
        slideText.setText("Slide to Book Appointment");
        slideText.setAlpha(1f);
    }
    
    private void checkFavoriteStatus() {
        if (auth.getCurrentUser() == null || favoritesRef == null) {
            return;
        }
        
        favoritesRef.child(doctor.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFavorite = snapshot.exists();
                updateFavoriteIcon();
    
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    
    private void toggleFavorite() {
        if (auth.getCurrentUser() == null || favoritesRef == null) {
            Toast.makeText(this, "Please login to add favorites", Toast.LENGTH_SHORT).show();
            return;
        }
        
        isFavorite = !isFavorite;
        updateFavoriteIcon();
    
        
        if (isFavorite) {
            favoritesRef.child(doctor.getId()).setValue(doctor)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Revert on failure
                    isFavorite = false;
                    updateFavoriteIcon();
                    Toast.makeText(this, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                });
        } else {
            favoritesRef.child(doctor.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Revert on failure
                    isFavorite = true;
                    updateFavoriteIcon();
                    Toast.makeText(this, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                });
        }
    }
    
    private void updateFavoriteIcon() {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_heart);
            favoriteButton.setColorFilter(Color.parseColor("#F44336"));
        } else {
            favoriteButton.setImageResource(R.drawable.ic_heart_outline);
            favoriteButton.clearColorFilter();
        }
    }
    
    private void expandDescription() {
        isDescriptionExpanded = !isDescriptionExpanded;
        
        if (isDescriptionExpanded) {
            // Expand the description
            doctorDescription.setMaxLines(Integer.MAX_VALUE);
            readMoreButton.setText("Read Less");
        } else {
            // Collapse the description
            doctorDescription.setMaxLines(3);
            readMoreButton.setText("Read More");
        }
    }
    
    
    private void openChat() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to chat with doctor", Toast.LENGTH_SHORT).show();
            return;
        }
        
        SessionManager sessionManager = new SessionManager(this);
        String currentUserId = sessionManager.getUserId();
        
        String chatId = MessagingUtils.generateChatId(currentUserId, doctorId);
        
        String doctorName = doctor.getName();
        String doctorImage = doctor.getImage();
        String doctorRole = "Doctor";
        
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("otherUserId", doctorId);
        intent.putExtra("otherUserName", doctorName);
        intent.putExtra("otherUserImage", doctorImage);
        intent.putExtra("otherUserRole", doctorRole);
        startActivity(intent);
    }
    
    
    private void makeCall() {
    
        if (doctor.getMobile() == null || doctor.getMobile().isEmpty()) {
            Toast.makeText(this, "Doctor's phone number not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + doctor.getMobile()));
            startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to make call: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
}

    
    
    
    
    
    