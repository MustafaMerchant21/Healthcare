package com.internship.healthcare.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.R;
import com.internship.healthcare.models.DoctorRating;

import java.util.HashMap;
import java.util.Map;
/**
 * RatingDialogHelper.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.utils
 * A Dialog displaying modal UI for user interaction.
 *
 *
 * <h3>Firebase Integration:</h3>
 * <ul>
 *   <li>Realtime Database</li>
 * </ul>
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class RatingDialogHelper {

    public interface OnRatingSubmittedListener {
        void onRatingSubmitted(float rating);
    }

    public static void showRatingDialog(Context context, String appointmentId, String doctorId, 
                                       String doctorName, String userId, String userName,
                                       OnRatingSubmittedListener listener) {

        
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rate_doctor, null);
        dialog.setContentView(view);
        
        // Make dialog background transparent for rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        
        TextView titleText = view.findViewById(R.id.dialog_title);
        TextView doctorNameText = view.findViewById(R.id.doctor_name);
        RatingBar ratingBar = view.findViewById(R.id.rating_bar);
        EditText reviewText = view.findViewById(R.id.review_text);
    
        MaterialCardView submitButton = view.findViewById(R.id.submit_button);
        MaterialCardView skipButton = view.findViewById(R.id.skip_button);
        
        titleText.setText("Rate Your Experience");
        doctorNameText.setText("How was your appointment with " + doctorName + "?");
        
        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            
            if (rating == 0) {
                Toast.makeText(context, "Please select a rating", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String review = reviewText.getText().toString().trim();
            
            // Submit rating
            submitRating(context, appointmentId, doctorId, userId, userName, rating, review, 
                new OnRatingSubmittedListener() {
                    @Override
                    public void onRatingSubmitted(float submittedRating) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onRatingSubmitted(submittedRating);
                        }
                    }
                });
        });
        
        skipButton.setOnClickListener(v -> {
            // Mark as rating given (skipped) so dialog doesn't show again
            markRatingAsGiven(appointmentId);
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private static void submitRating(Context context, String appointmentId, String doctorId, 
                                     String userId, String userName, float rating, String review,
                                     OnRatingSubmittedListener listener) {
        
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("doctorRatings");
        DatabaseReference doctorProfileRef = FirebaseDatabase.getInstance()
                .getReference("doctorProfiles").child(doctorId);
        DatabaseReference appointmentRef = FirebaseDatabase.getInstance()
                .getReference("appointments").child(appointmentId);
        
        String ratingId = ratingsRef.push().getKey();
        if (ratingId == null) {
    
            Toast.makeText(context, "Failed to submit rating", Toast.LENGTH_SHORT).show();
            return;
        }
        
        DoctorRating doctorRating = new DoctorRating(
            ratingId, doctorId, userId, userName, appointmentId, 
            rating, review, System.currentTimeMillis()
        );
        
        ratingsRef.child(doctorId).child(ratingId).setValue(doctorRating)
            .addOnSuccessListener(aVoid -> {
                updateDoctorRating(doctorId, rating);
                
                // Mark appointment as rated
                appointmentRef.child("ratingGiven").setValue(true);
                
                Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                
                if (listener != null) {
                    listener.onRatingSubmitted(rating);
                }
            })
    
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to submit rating: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            });
    }
    
    private static void updateDoctorRating(String doctorId, float newRating) {
        DatabaseReference doctorRef = FirebaseDatabase.getInstance()
                .getReference("doctorProfiles").child(doctorId);
        
        doctorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double currentRating = snapshot.child("rating").getValue(Double.class);
                    Integer totalRatings = snapshot.child("totalRatings").getValue(Integer.class);
                    
                    if (currentRating == null) currentRating = 0.0;
                    if (totalRatings == null) totalRatings = 0;
                    
                    // Calculate new average
                    double totalScore = currentRating * totalRatings;
                    totalScore += newRating;
                    totalRatings += 1;
                    double newAverage = totalScore / totalRatings;
                    
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("rating", newAverage);
                    updates.put("totalRatings", totalRatings);
                    
                    doctorRef.updateChildren(updates);
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    
    private static void markRatingAsGiven(String appointmentId) {
        DatabaseReference appointmentRef = FirebaseDatabase.getInstance()
                .getReference("appointments").child(appointmentId);
        appointmentRef.child("ratingGiven").setValue(true);
    }
}

    
    
    
    