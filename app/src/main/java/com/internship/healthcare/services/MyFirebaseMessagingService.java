package com.internship.healthcare.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.internship.healthcare.ChatActivity;
import com.internship.healthcare.R;
import com.internship.healthcare.utils.SessionManager;
/**
 * MyFirebaseMessagingService.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.services
 * A background Service for handling long-running operations.
 *
 * <p>Extends: {@link FirebaseMessagingService}</p>
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


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "healthcare_messages";
    private static final String CHANNEL_NAME = "Messages";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        if (remoteMessage.getData().size() > 0) {
            String messageType = remoteMessage.getData().get("type");
            
            if ("chat_message".equals(messageType)) {
                handleChatMessage(remoteMessage);
            }
        }

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            sendNotification(title, body, null, null, null, null, null);
        }
    }

    private void handleChatMessage(RemoteMessage remoteMessage) {
        String senderName = remoteMessage.getData().get("senderName");
        String message = remoteMessage.getData().get("message");
        String chatId = remoteMessage.getData().get("chatId");
    
        String senderId = remoteMessage.getData().get("senderId");
        String senderImage = remoteMessage.getData().get("senderImage");
        String senderRole = remoteMessage.getData().get("senderRole");
        String messageType = remoteMessage.getData().get("messageType");

        String notificationBody = message;
        if ("image".equals(messageType)) {
            notificationBody = "📷 Photo";
        }

        sendNotification(senderName, notificationBody, chatId, senderId, senderName, senderImage, senderRole);
    }

    private void sendNotification(String title, String messageBody, String chatId, 
                                  String senderId, String senderName, String senderImage, String senderRole) {
        Intent intent;
        
        if (chatId != null && senderId != null) {
            intent = new Intent(this, ChatActivity.class);
            intent.putExtra("chatId", chatId);
            intent.putExtra("otherUserId", senderId);
            intent.putExtra("otherUserName", senderName);
    
            intent.putExtra("otherUserImage", senderImage);
            intent.putExtra("otherUserRole", senderRole);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            intent = new Intent(this, com.internship.healthcare.MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 
            0, 
            intent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        String channelId = CHANNEL_ID;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
    
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Healthcare message notifications");
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        
        // You can save this to Firebase Realtime Database under users/{userId}/fcmToken
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        // This allows you to send targeted notifications to specific users
        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getUserId();

        if (userId != null && !userId.isEmpty()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId);
            userRef.child("fcmToken").setValue(token);
        }

    }
}

    
    