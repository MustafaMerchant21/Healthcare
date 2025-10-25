package com.internship.healthcare;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.internship.healthcare.utils.FCMNotificationSender;
import com.internship.healthcare.utils.SessionManager;
/**
 * SignUpScreen.java
 * A comprehensive healthcare management Android application
 * Activity managing new user registration. Integrates with Firebase Authentication, Realtime Database, Cloud Messaging.
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


public class SignUpScreen extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private SessionManager sessionManager;
    private TextInputEditText name, username, mobile, email, password, confirmpassword;
    private MaterialButton signup_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");
        sessionManager = new SessionManager(this);
        
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmPassword);
        signup_button = findViewById(R.id.signup_button);
    
        TextView signin = findViewById(R.id.login);
        Intent toSignin = new Intent(this, SignInScreen.class);

        signin.setOnClickListener(v -> {
            startActivity(toSignin);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        signup_button.setOnClickListener(v -> {
            String nameText = name.getText().toString().trim();
            String usernameText = username.getText().toString().trim();
            String mobileText = mobile.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String confirmPasswordText = confirmpassword.getText().toString().trim();

            boolean isValid = true;
            
            if (nameText.isEmpty()){
                name.setError("Please enter your name");
                isValid = false;
            }
            if (usernameText.isEmpty()){
                username.setError("Please enter a username");
                isValid = false;
            }
            if(mobileText.isEmpty()){
                mobile.setError("Please enter your mobile number");
                isValid = false;
            }
            if (emailText.isEmpty()){
                email.setError("Please enter your email");
                isValid = false;
            }
            if (passwordText.isEmpty()){
                password.setError("Please enter a password");
                isValid = false;
            } else if (passwordText.length() < 6) {
                password.setError("Password must be at least 6 characters");
                isValid = false;
            }
            if (confirmPasswordText.isEmpty()){
                confirmpassword.setError("Please confirm your password");
                isValid = false;
            }
            
            // If validation fails, stop here
            if (!isValid) {
                return;
            }
            
            if (!passwordText.equals(confirmPasswordText)){
                confirmpassword.setError("Passwords do not match");
                return;
            }
            
            // All validations passed, create user
            auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    String userId = user.getUid();
                    
                    AuthHelper helper = new AuthHelper(nameText, usernameText, emailText, mobileText);
                    
                    reference.child(userId).setValue(helper).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            saveFCMToken(userId);
                            
                            sendVerificationEmail(user, emailText);
                        } else {
                            // Database write failed - delete the auth user to maintain consistency
                            String dbError = dbTask.getException() != null ? dbTask.getException().getMessage() : "Unknown database error";
                            
                            if (user != null) {
                                user.delete().addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        Toast.makeText(SignUpScreen.this, "Registration failed: " + dbError, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(SignUpScreen.this, "Critical error: User created but data not saved. Please contact support.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(SignUpScreen.this, "Failed to save user data: " + dbError, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(SignUpScreen.this, "Registration Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void sendVerificationEmail(FirebaseUser user, String email) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showEmailVerificationDialog(email);
                } else {
                    Toast.makeText(SignUpScreen.this, 
                        "Failed to send verification email: " + 
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"), 
                        Toast.LENGTH_LONG).show();
                    
                    // Sign out the user since they haven't verified
                    auth.signOut();
                    
                    // Redirect to sign in
                    startActivity(new Intent(SignUpScreen.this, SignInScreen.class));
                    finish();
                }
            });
        }
    }

    private void showEmailVerificationDialog(String email) {
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
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                user.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpScreen.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpScreen.this, "Failed to resend email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        okButton.setOnClickListener(v -> {
            // Sign out and go to sign in screen
            auth.signOut();
            dialog.dismiss();
            startActivity(new Intent(SignUpScreen.this, SignInScreen.class));
            finish();
    
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