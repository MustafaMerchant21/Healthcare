package com.internship.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.adapters.NotificationAdapter;
import com.internship.healthcare.models.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * NotificationActivity.java
 * A comprehensive healthcare management Android application
 * Activity displaying system notifications. Integrates with Firebase Authentication, Realtime Database.
 *
 * <p>Extends: {@link AppCompatActivity}</p>
 * <p>Implements: {@link NotificationAdapter.OnNotificationClickListener}</p>
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


public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {

    private ImageButton backButton;
    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter notificationAdapter;
    private LinearLayout emptyState;
    private ProgressBar loadingIndicator;
    private TextView clearAllButton;
    

    private FirebaseAuth auth;
    private DatabaseReference notificationsRef;
    private ValueEventListener notificationsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        auth = FirebaseAuth.getInstance();

        backButton = findViewById(R.id.back_button);
        notificationsRecyclerView = findViewById(R.id.notifications_recycler_view);
        emptyState = findViewById(R.id.empty_state);
        loadingIndicator = findViewById(R.id.loading_indicator);
        clearAllButton = findViewById(R.id.clear_all_button);

        backButton.setOnClickListener(v -> finish());

        clearAllButton.setOnClickListener(v -> showClearAllDialog());

        setupNotifications();
        
        loadNotifications();
    }

    private void setupNotifications() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    
        notificationsRecyclerView.setLayoutManager(layoutManager);

        notificationAdapter = new NotificationAdapter(this);
        notificationsRecyclerView.setAdapter(notificationAdapter);
    }

    private void loadNotifications() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to view notifications", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        notificationsRef = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(userId);

        loadingIndicator.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        notificationsRecyclerView.setVisibility(View.GONE);
        clearAllButton.setVisibility(View.GONE);

        notificationsListener = notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
    
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingIndicator.setVisibility(View.GONE);
                
                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Notification notification = child.getValue(Notification.class);
                    if (notification != null) {
                        if (notification.getId() == null || notification.getId().isEmpty()) {
                            notification.setId(child.getKey());
                        }
    
                        notifications.add(notification);
                    }
                }

                if (notifications.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    notificationsRecyclerView.setVisibility(View.GONE);
                    clearAllButton.setVisibility(View.GONE);
                } else {
                    // Sort by timestamp (newest first)
                    Collections.sort(notifications, new Comparator<Notification>() {
                        @Override
                        public int compare(Notification n1, Notification n2) {
                            return Long.compare(n2.getTimestamp(), n1.getTimestamp());
                        }
                    });
                    
                    emptyState.setVisibility(View.GONE);
                    notificationsRecyclerView.setVisibility(View.VISIBLE);
                    clearAllButton.setVisibility(View.VISIBLE);
                    notificationAdapter.setNotifications(notifications);
    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingIndicator.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
                Toast.makeText(NotificationActivity.this, 
                        "Failed to load notifications: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNotificationClick(Notification notification) {
        // Mark as read
        markAsRead(notification);
        
        if (notification.getType() != null && notification.getRelatedId() != null) {
            if (notification.getType().startsWith("appointment_")) {
                Intent intent = new Intent(this, AppointmentDetailActivity.class);
                intent.putExtra("appointmentId", notification.getRelatedId());
                startActivity(intent);
            }
        }
    
    }

    @Override
    public void onDeleteClick(Notification notification) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notification")
                .setMessage("Are you sure you want to delete this notification?")
                .setPositiveButton("Delete", (dialog, which) -> deleteNotification(notification))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void markAsRead(Notification notification) {
        if (notification.isRead() || auth.getCurrentUser() == null) return;
        
        if (notification.getId() == null || notification.getId().isEmpty()) {
            Toast.makeText(this, "Invalid notification", Toast.LENGTH_SHORT).show();
            return;
        }
    

        String userId = auth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(userId)
                .child(notification.getId())
                .child("read")
                .setValue(true);
    }

    private void deleteNotification(Notification notification) {
        if (auth.getCurrentUser() == null) return;
        
        if (notification.getId() == null || notification.getId().isEmpty()) {
            Toast.makeText(this, "Invalid notification", Toast.LENGTH_SHORT).show();
    
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(userId)
                .child(notification.getId())
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Notification deleted", Toast.LENGTH_SHORT).show();
                    notificationAdapter.removeNotification(notification);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete notification", Toast.LENGTH_SHORT).show();
                });
    }

    
    private void showClearAllDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Clear All Notifications")
                .setMessage("Are you sure you want to clear all notifications? This action cannot be undone.")
                .setPositiveButton("Clear All", (dialog, which) -> clearAllNotifications())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearAllNotifications() {
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
    
        FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(userId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "All notifications cleared", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to clear notifications", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationsRef != null && notificationsListener != null) {
            notificationsRef.removeEventListener(notificationsListener);
        }
    }
}

    
    
    
    