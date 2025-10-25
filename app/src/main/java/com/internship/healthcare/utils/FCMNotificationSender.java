package com.internship.healthcare.utils;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * FCMNotificationSender.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.utils
 * Data model class representing fcm notification sender entity in patient information and records system.
 *
 *
 * <h3>Firebase Integration:</h3>
 * <ul>
 *   <li>Realtime Database</li>
 *   <li>Cloud Messaging</li>
 * </ul>
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class FCMNotificationSender {

    private static final String TAG = "FCMNotificationSender";
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    // Firebase Server Key
    private static final String SERVER_KEY = "BMLpzfmq5uQYuYKREWvKStvGQbPrHJnDQ_H1D3f6f2b92FPU7heNnYPOGdzdoy2IXoWGN13j5Z4SA8uP9eiuvcA";
    
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();


    
    public static void sendChatNotification(String receiverId, String senderName, 
                                           String message, String chatId, String senderId,
                                           String senderImage, String senderRole, String messageType) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(receiverId)
                .child("fcmToken");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fcmToken = snapshot.getValue(String.class);
                    if (fcmToken != null && !fcmToken.isEmpty()) {
                        sendNotificationToToken(fcmToken, senderName, message, chatId, 
                                senderId, senderName, senderImage, senderRole, messageType);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to get FCM token: " + error.getMessage());
            }
        });
    }

    
    private static void sendNotificationToToken(String fcmToken, String title, String body,
    
                                               String chatId, String senderId, String senderName,
                                               String senderImage, String senderRole, String messageType) {
        executor.execute(() -> {
            try {
                JSONObject notification = new JSONObject();
                notification.put("title", title);
                notification.put("body", body);
                notification.put("sound", "default");

                JSONObject data = new JSONObject();
                data.put("type", "chat_message");
                data.put("chatId", chatId);
                data.put("senderId", senderId);
                data.put("senderName", senderName);
                data.put("senderImage", senderImage != null ? senderImage : "");
    
                data.put("senderRole", senderRole);
                data.put("message", body);
                data.put("messageType", messageType);

                JSONObject message = new JSONObject();
                message.put("to", fcmToken);
                message.put("notification", notification);
                message.put("data", data);
                message.put("priority", "high");

                URL url = new URL(FCM_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);
                conn.setDoOutput(true);

                // Write payload
                OutputStream os = conn.getOutputStream();
                os.write(message.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Notification sent successfully");
                } else {
                    Log.e(TAG, "Failed to send notification. Response code: " + responseCode);
                }

                conn.disconnect();

            } catch (JSONException | IOException e) {
                Log.e(TAG, "Error sending notification: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    
    public static void saveFCMToken(String userId, String token) {
        if (userId != null && !userId.isEmpty() && token != null && !token.isEmpty()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId);
            userRef.child("fcmToken").setValue(token)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "FCM token saved successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to save FCM token: " + e.getMessage()));
        }
    }

    
    public static void removeFCMToken(String userId) {
        if (userId != null && !userId.isEmpty()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId);
            userRef.child("fcmToken").removeValue();
        }
    }
}
