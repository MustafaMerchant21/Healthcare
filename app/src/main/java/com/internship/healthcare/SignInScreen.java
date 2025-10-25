package com.internship.healthcare;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.internship.healthcare.utils.SessionManager;
import com.internship.healthcare.utils.FCMNotificationSender;
/**
 * SignInScreen.java
 * A comprehensive healthcare management Android application
 * Activity handling user authentication and login. Integrates with Firebase Authentication, Realtime Database, Cloud Messaging.
 *
 * <p>Extends: {@link AppCompatActivity}</p>
 *
 * <h3>Firebase Integration:</h3>
 * <ul>
 *   <li>Authentication</li>
 *   <li>Realtime Database</li>
 *   <li>Cloud Messaging</li>
 * </ul>
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class SignInScreen extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private SessionManager sessionManager;
    private TextInputEditText username, password;
    private MaterialButton login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        sessionManager = new SessionManager(this);
        
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_button = findViewById(R.id.login_button);
        TextView signup = findViewById(R.id.signUp);
        Intent toSignup = new Intent(this, SignUpScreen.class);
        signup.setOnClickListener(v -> {
            startActivity(toSignup);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    
        });

        login_button.setOnClickListener(v -> {
            String emailText = username.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            boolean isValid = true;
            
            if (emailText.isEmpty()) {
                username.setError("Email cannot be empty");
                isValid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                username.setError("Please enter a valid email");
                isValid = false;
            }
            
            if (passwordText.isEmpty()) {
                password.setError("Password cannot be empty");
                isValid = false;
            }
            
            if (!isValid) {
                return;
            }
            
            // Sign in with Firebase Authentication
            auth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = auth.getCurrentUser();
                    if (currentUser != null && !currentUser.isEmailVerified()) {
                        // Email not verified, show dialog
                        showEmailVerificationDialog(emailText, currentUser);
                        return;
                    }
                    
                    String userId = currentUser.getUid();
                    
                    databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String userName = snapshot.child("name").getValue(String.class);
                                String userEmail = snapshot.child("email").getValue(String.class);
                                
                                sessionManager.createLoginSession(userId, 
                                    userEmail != null ? userEmail : emailText, 
                                    userName != null ? userName : "User");
                                
                                saveFCMToken(userId);
                                
                                Toast.makeText(SignInScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInScreen.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                // User data not found in database, but authenticated
                                sessionManager.createLoginSession(userId, emailText, "User");
                                Toast.makeText(SignInScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInScreen.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Database error, but authentication succeeded
                            sessionManager.createLoginSession(userId, emailText, "User");
    
                            Toast.makeText(SignInScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInScreen.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(SignInScreen.this, "Login Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void showEmailVerificationDialog(String email, FirebaseUser user) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_email_verification);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView emailAddressText = dialog.findViewById(R.id.email_address_text);
        MaterialButton resendButton = dialog.findViewById(R.id.resend_button);
        MaterialButton okButton = dialog.findViewById(R.id.ok_button);

        emailAddressText.setText(email);

        resendButton.setOnClickListener(v -> {
            if (user != null) {
    
                user.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignInScreen.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignInScreen.this, "Failed to resend email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        okButton.setOnClickListener(v -> {
            // Sign out and dismiss dialog
            auth.signOut();
            dialog.dismiss();
        });

        dialog.show();
    }
    private void saveFCMToken(String userId) {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String token = task.getResult();
                    FCMNotificationSender.saveFCMToken(userId, token);
                }
            });
    }
}