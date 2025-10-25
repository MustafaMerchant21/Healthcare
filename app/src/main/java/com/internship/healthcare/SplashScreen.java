package com.internship.healthcare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.internship.healthcare.utils.SessionManager;

/**
 * SplashScreen.java
 * Package: com.internship.healthcare
 *
 * @author Mustafa Merchant
 * @version 1.1
 * @since 2025
 */

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private SessionManager sessionManager;
    private FirebaseAuth auth;
    private boolean isNavigating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SplashScreen", "═══════════════════════════════════");
        Log.d("SplashScreen", "onCreate called");
        Log.d("SplashScreen", "savedInstanceState: " + (savedInstanceState != null ? "NOT NULL" : "NULL"));
        Log.d("SplashScreen", "TaskId: " + getTaskId());
        Log.d("SplashScreen", "Intent: " + getIntent());
        Log.d("SplashScreen", "Intent Flags: " + getIntent().getFlags());
        Log.d("SplashScreen", "═══════════════════════════════════");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        auth = FirebaseAuth.getInstance();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isNavigating) {
                checkLoginStatus();
            }
        }, 1200);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("SplashScreen", "⚠️ onNewIntent called - preventing duplicate navigation");
        Log.d("SplashScreen", "New Intent: " + intent);
        finish();
    }

    private void checkLoginStatus() {
        if (isNavigating) {
            Log.d("SplashScreen", "⚠️ Already navigating, skipping checkLoginStatus");
            return;
        }

        isNavigating = true;

        boolean isLoggedIn = sessionManager.isLoggedIn();
        boolean hasCurrentUser = auth.getCurrentUser() != null;
        Log.d("SplashScreen", "checkLoginStatus - isLoggedIn: " + isLoggedIn + ", hasCurrentUser: " + hasCurrentUser);

        if (isLoggedIn && hasCurrentUser) {
            Log.d("SplashScreen", "✓ Starting MainActivity");
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            // These flags prevent duplicate activities
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Log.d("SplashScreen", "✓ MainActivity started, SplashScreen finishing");
        } else {
            // User is not logged in
            if (isLoggedIn) {
                sessionManager.logout();
            }

            Log.d("SplashScreen", "✓ Starting OnboardingScreen");
            Intent intent = new Intent(SplashScreen.this, OnboardingScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Log.d("SplashScreen", "✓ OnboardingScreen started, SplashScreen finishing");
        }
    }
}