package com.internship.healthcare;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
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
import com.internship.healthcare.utils.SupabaseImageUploader;

import java.util.HashMap;
import java.util.Map;
/**
 * AccountInfoActivity.java
 * A comprehensive healthcare management Android application
 * Activity handling account info screen and user interactions.
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


public class AccountInfoActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextInputEditText nameInput;
    private TextInputEditText usernameInput;
    private TextInputEditText emailInput;
    private TextInputEditText mobileInput;
    private MaterialButton saveButton;


    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String userId;
    private Uri selectedImageUri;
    private SupabaseImageUploader imageUploader;
    private String currentProfileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        
        if (currentUser != null) {
            userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imageUploader = new SupabaseImageUploader(this);

        backButton = findViewById(R.id.back_button);
        nameInput = findViewById(R.id.name_input);
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
    
        mobileInput = findViewById(R.id.mobile_input);
        saveButton = findViewById(R.id.save_button);

        loadUserData();

        backButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> showConfirmationDialog());
    }

    private void showConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);
        MaterialButton confirmButton = dialog.findViewById(R.id.confirm_button);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        confirmButton.setOnClickListener(v -> {
            dialog.dismiss();
            saveUserData();
        });

        dialog.show();
    }

    private void loadUserData() {
        saveButton.setEnabled(false);
        saveButton.setText("Loading...");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
    
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String mobile = snapshot.child("mobile").getValue(String.class);
                    currentProfileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                    if (name != null) nameInput.setText(name);
                    if (username != null) usernameInput.setText(username);
                    if (email != null) emailInput.setText(email);
                    if (mobile != null) mobileInput.setText(mobile);
                }

                // Enable save button
                saveButton.setEnabled(true);
                saveButton.setText("Save Changes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountInfoActivity.this, 
    
                    "Failed to load data: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                saveButton.setEnabled(true);
                saveButton.setText("Save Changes");
            }
        });
    }

    
    private void saveUserData() {
        String name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String mobile = mobileInput.getText() != null ? mobileInput.getText().toString().trim() : "";

        if (name.isEmpty()) {
            nameInput.setError("Name is required");
            nameInput.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return;
        }

        if (mobile.isEmpty()) {
            mobileInput.setError("Mobile number is required");
            mobileInput.requestFocus();
            return;
        }

        if (mobile.length() < 10) {
    
            mobileInput.setError("Please enter a valid mobile number");
            mobileInput.requestFocus();
            return;
        }

        saveButton.setEnabled(false);
        saveButton.setText("Saving...");

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("username", username);
        updates.put("mobile", mobile);

        databaseReference.updateChildren(updates)
    
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(AccountInfoActivity.this, 
                    "Profile updated successfully", 
                    Toast.LENGTH_SHORT).show();
                saveButton.setEnabled(true);
                saveButton.setText("Save Changes");
                
                // Optionally finish activity to go back
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(AccountInfoActivity.this, 
                    "Failed to update: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                saveButton.setEnabled(true);
                saveButton.setText("Save Changes");
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

    