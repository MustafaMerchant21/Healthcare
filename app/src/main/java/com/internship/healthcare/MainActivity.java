package com.internship.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.fragments.AppointmentsFragment;
import com.internship.healthcare.fragments.CategoryFragment;
import com.internship.healthcare.fragments.HomeFragment;
import com.internship.healthcare.fragments.ProfileFragment;
/**
 * MainActivity.java
 * A comprehensive healthcare management Android application
 * Main activity serving as the primary navigation hub of the healthcare application.
 * Manages bottom navigation, user greetings, notifications, and fragment transactions.
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */



public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabMessages;
    private TextView greetingText, subtitleText, notificationBadge;
    private ImageButton notificationButton;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private LinearLayout greetingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "════════════════════════════════════════");
        Log.d("MainActivity", "onCreate called");
        Log.d("MainActivity", "savedInstanceState: " + (savedInstanceState != null ? "NOT NULL (recreated)" : "NULL (fresh start)"));
        Log.d("MainActivity", "TaskId: " + getTaskId());
        Log.d("MainActivity", "Intent: " + getIntent());
        Log.d("MainActivity", "Intent Flags: " + getIntent().getFlags());
        Log.d("MainActivity", "════════════════════════════════════════");
        
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        greetingText = findViewById(R.id.greeting_text);
        subtitleText = findViewById(R.id.subtitle_text);
        notificationButton = findViewById(R.id.notification_button);
        notificationBadge = findViewById(R.id.notification_badge);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabMessages = findViewById(R.id.fab_messages);

        com.internship.healthcare.utils.AppointmentStatusUpdater.updateExpiredAppointments();

        loadUserData();
        checkUserRoleAndConfigureNav();
        updateNotificationBadge();


        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                updateAppBar(true, null, null);
            } else if (itemId == R.id.nav_category) {
                selectedFragment = new CategoryFragment();
                updateAppBar(false, "Categories", null);
            } else if (itemId == R.id.nav_schedule) {
                selectedFragment = new AppointmentsFragment();
                updateAppBar(false, "Schedule", null);
            } else if (itemId == R.id.nav_patients) {
                Intent intent = new Intent(MainActivity.this, AppointmentRequestsActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                updateAppBar(false, "Profile", null);
            }

            return loadFragment(selectedFragment);
        });

        fabMessages.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
            startActivity(intent);
        });

        notificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume called");
        updateNotificationBadge();
        checkUserRoleAndConfigureNav();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("MainActivity", "onRestart called");
    }

    
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    
    private void loadUserData() {
        
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userName = snapshot.child("name").getValue(String.class);
                        if (userName != null && !userName.isEmpty()) {
                            greetingText.setText("Hello " + userName);
                        } else {
                            greetingText.setText("Hello There!");
                        }
                    } else {
                        greetingText.setText("Hello There!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    greetingText.setText("Hello There!");
                }
            });
        } else {
            greetingText.setText("Hello There!");
        }
    }

    
    private void checkUserRoleAndConfigureNav() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String role = snapshot.child("role").getValue(String.class);
                        Boolean isVerified = snapshot.child("isVerified").getValue(Boolean.class);
                        

                        if (role == null) role = "patient";
                        if (isVerified == null) isVerified = false;

                        boolean showPatientsTab = "doctor".equals(role) && isVerified;
                        updatePatientsTabVisibility(showPatientsTab);
                    } else {
                        updatePatientsTabVisibility(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Failed to check user role", Toast.LENGTH_SHORT).show();
                    updatePatientsTabVisibility(false);
                }
            });
        } else {
            updatePatientsTabVisibility(false);
        }
    }

    
    private void updatePatientsTabVisibility(boolean visible) {
        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().findItem(R.id.nav_patients).setVisible(visible);
        }
    }

    
    private void updateAppBar(boolean isHome, String title, String subtitle) {
        if (isHome) {
            greetingText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            subtitleText.setVisibility(View.VISIBLE);
            loadUserData();
        } else {
            
            if (title != null) {
                greetingText.setText(title);
                greetingText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                subtitleText.setVisibility(View.GONE);
            }
            subtitleText.setVisibility(View.GONE);
        }
    }

    
    private void updateNotificationBadge() {
        if (auth.getCurrentUser() == null) {
            notificationBadge.setVisibility(View.GONE);
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(userId);

        notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unreadCount = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Boolean isRead = child.child("read").getValue(Boolean.class);
                    if (isRead != null && !isRead) {
                        unreadCount++;
                    }
                }

                if (unreadCount > 0) {
                    String badgeText = unreadCount > 99 ? "99+" : String.valueOf(unreadCount);
                    notificationBadge.setText(badgeText);
                    notificationBadge.setVisibility(View.VISIBLE);
                } else {
                    notificationBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notificationBadge.setVisibility(View.GONE);
            }
        });
    }
}



     