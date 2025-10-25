package com.internship.healthcare;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.models.UserLocation;
/**
 * LocationActivity.java
 * A comprehensive healthcare management Android application
 * Activity handling user location selection. Integrates with Firebase Authentication, Realtime Database.
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


public class LocationActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextInputEditText addressLine1Input;
    private TextInputEditText addressLine2Input;
    private AutoCompleteTextView cityInput;
    private AutoCompleteTextView stateInput;
    private TextInputEditText pincodeInput;
    private AutoCompleteTextView countryInput;

    private MaterialButton saveButton;
    
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String userId;
    
    // Indian cities
    private static final String[] INDIAN_CITIES = {
            "Agra", "Ahmedabad", "Allahabad", "Amritsar", "Aurangabad", "Bangalore", "Bhopal",
            "Bhubaneswar", "Chandigarh", "Chennai", "Coimbatore", "Dehradun", "Delhi",
            "Dhanbad", "Faridabad", "Ghaziabad", "Guwahati", "Gurgaon", "Howrah", "Hyderabad",
            "Indore", "Jaipur", "Jabalpur", "Kanpur", "Kalyan-Dombivali", "Kochi", "Kolkata",
            "Lucknow", "Ludhiana", "Madurai", "Meerut", "Mangalore", "Mohali", "Mumbai",
            "Mysuru", "Nagpur", "Nashik", "Navi Mumbai", "Noida", "Patna", "Pimpri-Chinchwad",
            "Pune", "Raipur", "Rajkot", "Ranchi", "Surat", "Srinagar", "Thane", "Tiruchirappalli",
            "Tirupati", "Udaipur", "Vadodara", "Varanasi", "Vasai-Virar", "Vijayawada", "Visakhapatnam"
    };
    
    // Indian states
    private static final String[] INDIAN_STATES = {
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
        "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka",
        "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram",
        "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu",
        "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal",
        "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu",
        "Delhi", "Jammu and Kashmir", "Ladakh", "Lakshadweep", "Puducherry"
    };
    
    // Countries
    private static final String[] COUNTRIES = {
        "India", "United States", "United Kingdom", "Canada", "Australia",
        "Germany", "France", "Japan", "China", "Singapore"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        
        if (currentUser != null) {
            userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("location");
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        backButton = findViewById(R.id.back_button);
        addressLine1Input = findViewById(R.id.address_line1_input);
        addressLine2Input = findViewById(R.id.address_line2_input);
    
        cityInput = findViewById(R.id.city_input);
        stateInput = findViewById(R.id.state_input);
        pincodeInput = findViewById(R.id.pincode_input);
        countryInput = findViewById(R.id.country_input);
        saveButton = findViewById(R.id.save_button);
        
        setupDropdowns();
        
        loadLocationData();
        
        backButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveLocationData());
    }
    
    private void setupDropdowns() {
        // City dropdown
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, INDIAN_CITIES);
        cityInput.setAdapter(cityAdapter);
        
        // State dropdown
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, INDIAN_STATES);
        stateInput.setAdapter(stateAdapter);
        
        // Country dropdown
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        countryInput.setAdapter(countryAdapter);
        countryInput.setText("India", false); // Default to India
    }
    
    private void loadLocationData() {
        saveButton.setEnabled(false);
        saveButton.setText("Loading...");
        
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
    
                    UserLocation location = snapshot.getValue(UserLocation.class);
                    
                    if (location != null) {
                        if (location.getAddressLine1() != null) {
                            addressLine1Input.setText(location.getAddressLine1());
                        }
                        if (location.getAddressLine2() != null) {
                            addressLine2Input.setText(location.getAddressLine2());
                        }
                        if (location.getCity() != null) {
                            cityInput.setText(location.getCity(), false);
                        }
                        if (location.getState() != null) {
                            stateInput.setText(location.getState(), false);
                        }
                        if (location.getPincode() != null) {
                            pincodeInput.setText(location.getPincode());
                        }
                        if (location.getCountry() != null) {
                            countryInput.setText(location.getCountry(), false);
    
                        }
                    }
                }
                
                saveButton.setEnabled(true);
                saveButton.setText("Save Location");
            }
            
    
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LocationActivity.this,
                        "Failed to load location: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                saveButton.setEnabled(true);
                saveButton.setText("Save Location");
            }
        });
    }
    
    private void saveLocationData() {
        String addressLine1 = addressLine1Input.getText() != null ? 
                addressLine1Input.getText().toString().trim() : "";
        String addressLine2 = addressLine2Input.getText() != null ? 
                addressLine2Input.getText().toString().trim() : "";
        String city = cityInput.getText().toString().trim();
        String state = stateInput.getText().toString().trim();
        String pincode = pincodeInput.getText() != null ? 
                pincodeInput.getText().toString().trim() : "";
        String country = countryInput.getText().toString().trim();
        
        if (addressLine1.isEmpty()) {
            addressLine1Input.setError("Address is required");
            addressLine1Input.requestFocus();
            return;
        }
        
        if (city.isEmpty()) {
            cityInput.setError("City is required");
            cityInput.requestFocus();
            return;
        }
        
        if (state.isEmpty()) {
    
            stateInput.setError("State is required");
            stateInput.requestFocus();
            return;
        }
        
        if (pincode.isEmpty()) {
            pincodeInput.setError("Pincode is required");
            pincodeInput.requestFocus();
            return;
        }
        
        if (pincode.length() != 6) {
            pincodeInput.setError("Enter a valid 6-digit pincode");
            pincodeInput.requestFocus();
    
            return;
        }
        
        if (country.isEmpty()) {
            countryInput.setError("Country is required");
            countryInput.requestFocus();
            return;
        }
        
        saveButton.setEnabled(false);
        saveButton.setText("Saving...");
        
        UserLocation location = new UserLocation(
                addressLine1,
                addressLine2,
                city,
                state,
                pincode,
                country,
                true, // Default location
                System.currentTimeMillis()
        );
        
        databaseReference.setValue(location)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(LocationActivity.this,
                            "Location saved successfully",
                            Toast.LENGTH_SHORT).show();
                    saveButton.setEnabled(true);
                    saveButton.setText("Save Location");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LocationActivity.this,
                            "Failed to save location: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    saveButton.setEnabled(true);
                    saveButton.setText("Save Location");
                });
    }
}
