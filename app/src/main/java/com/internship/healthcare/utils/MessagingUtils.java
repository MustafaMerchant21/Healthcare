package com.internship.healthcare.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.internship.healthcare.models.ChatMessage;

import java.util.HashMap;
import java.util.Map;
/**
 * MessagingUtils.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.utils
 * Utility class for managing chat messaging functionality.
 * Handles sending messages, updating chat metadata, and managing chat lists.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */



public class MessagingUtils {

    private static final DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
    private static final DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");
    private static final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

    
    public static String generateChatId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 
                ? userId1 + "_" + userId2 
                : userId2 + "_" + userId1;
    }

    
    public static void sendMessage(String chatId, String senderId, String receiverId, 
                                   String message, String senderName, 
                                   OnMessageSentListener listener) {
        String messageId = messagesRef.child(chatId).push().getKey();
        if (messageId == null) {
            if (listener != null) listener.onFailure("Failed to generate message ID");
            return;
        }

        ChatMessage chatMessage = new ChatMessage(
                messageId,
                senderId,
                receiverId,
                message,
                System.currentTimeMillis(),
                false,
                "text",
                null,
                senderName
        );

        messagesRef.child(chatId).child(messageId).setValue(chatMessage)
                .addOnSuccessListener(aVoid -> {
                    updateChatMetadata(senderId, receiverId, message, chatId, senderName);
                    
                    FCMNotificationSender.sendChatNotification(receiverId, senderName, 
                            message, chatId, senderId, null, null, "text");
                    
                    if (listener != null) listener.onSuccess(messageId);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    
    public static void sendImageMessage(String chatId, String senderId, String receiverId,
                                       String imageUrl, String senderName,
                                       OnMessageSentListener listener) {
        String messageId = messagesRef.child(chatId).push().getKey();
        if (messageId == null) {
            if (listener != null) listener.onFailure("Failed to generate message ID");
            return;
        }

        ChatMessage chatMessage = new ChatMessage(
                messageId,
                senderId,
                receiverId,
                "📷 Photo",
                System.currentTimeMillis(),
                false,
                "image",
                imageUrl,
                senderName
        );

        messagesRef.child(chatId).child(messageId).setValue(chatMessage)
                .addOnSuccessListener(aVoid -> {
                    updateChatMetadata(senderId, receiverId, "📷 Photo", chatId, senderName);
                    
                    FCMNotificationSender.sendChatNotification(receiverId, senderName, 
                            "📷 Photo", chatId, senderId, null, null, "image");
                    
                    if (listener != null) listener.onSuccess(messageId);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    
    public static void updateChatMetadata(String senderId, String receiverId, 
                                         String lastMessage, String chatId, 
                                         String senderName) {
        long timestamp = System.currentTimeMillis();

        usersRef.child(senderId).child("profileImageUrl").get().addOnSuccessListener(imageSnapshot -> {
            String senderImage = imageSnapshot.exists() ? imageSnapshot.getValue(String.class) : null;
            
            // If profileImageUrl doesn't exist, try "image" field (for doctors)
            if (senderImage == null) {
                usersRef.child(senderId).child("image").get().addOnSuccessListener(doctorImageSnapshot -> {
                    String doctorImage = doctorImageSnapshot.exists() ? doctorImageSnapshot.getValue(String.class) : null;
                    updateChatMetadataWithImage(senderId, receiverId, lastMessage, chatId, senderName, doctorImage, timestamp);
                });
            } else {
                updateChatMetadataWithImage(senderId, receiverId, lastMessage, chatId, senderName, senderImage, timestamp);
            }
        });
    }
    
    
    private static void updateChatMetadataWithImage(String senderId, String receiverId, 
                                                    String lastMessage, String chatId, 
                                                    String senderName, String senderImage,
                                                    long timestamp) {
        Map<String, Object> senderChatData = new HashMap<>();
        senderChatData.put("chatId", chatId);
        senderChatData.put("lastMessage", lastMessage);
        senderChatData.put("lastMessageTime", timestamp);
        senderChatData.put("otherUserId", receiverId);

        chatsRef.child(senderId).child(chatId).updateChildren(senderChatData);

        chatsRef.child(receiverId).child(chatId).get().addOnSuccessListener(snapshot -> {
            int currentUnreadCount = 0;
            if (snapshot.exists() && snapshot.child("unreadCount").exists()) {
                currentUnreadCount = snapshot.child("unreadCount").getValue(Integer.class);
            }

            Map<String, Object> receiverChatData = new HashMap<>();
            receiverChatData.put("chatId", chatId);
            receiverChatData.put("lastMessage", lastMessage);
            receiverChatData.put("lastMessageTime", timestamp);
            receiverChatData.put("otherUserId", senderId);
            receiverChatData.put("otherUserName", senderName);
            receiverChatData.put("otherUserImage", senderImage);
            receiverChatData.put("unreadCount", currentUnreadCount + 1);

            chatsRef.child(receiverId).child(chatId).updateChildren(receiverChatData);
        });
    }

    
    public static void markMessagesAsRead(String chatId, String currentUserId) {
        messagesRef.child(chatId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                for (com.google.firebase.database.DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                    if (message != null && message.getReceiverId().equals(currentUserId) && !message.isRead()) {
                        messagesRef.child(chatId).child(message.getId()).child("isRead").setValue(true);
                    }
                }
            }
        });

        chatsRef.child(currentUserId).child(chatId).child("unreadCount").setValue(0);
    }

    
    public static void initializeChatMetadata(String userId, String otherUserId, 
                                             String otherUserName, String otherUserImage,
                                             String otherUserRole, String chatId) {
        chatsRef.child(userId).child(chatId).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                // Chat doesn't exist, create new one
                Map<String, Object> chatData = new HashMap<>();
                chatData.put("chatId", chatId);
                chatData.put("otherUserId", otherUserId);
                chatData.put("otherUserName", otherUserName);
                chatData.put("otherUserImage", otherUserImage);
                chatData.put("otherUserRole", otherUserRole);
                chatData.put("lastMessage", "");
                chatData.put("lastMessageTime", 0); // Set to 0 for new chats with no messages
                chatData.put("unreadCount", 0);
                chatData.put("isOnline", false);

                chatsRef.child(userId).child(chatId).setValue(chatData);
            } else {
                // Chat exists, only update user info fields without touching lastMessageTime
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("otherUserId", otherUserId);
                updateData.put("otherUserName", otherUserName);
                updateData.put("otherUserImage", otherUserImage);
                updateData.put("otherUserRole", otherUserRole);
                
                chatsRef.child(userId).child(chatId).updateChildren(updateData);
            }
        });
    }

    
    public static void updateOnlineStatus(String userId, boolean isOnline) {
        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference("presence").child(userId);
        Map<String, Object> presenceData = new HashMap<>();
        presenceData.put("online", isOnline);
        presenceData.put("lastSeen", System.currentTimeMillis());
        presenceRef.updateChildren(presenceData);
    }

    public interface OnMessageSentListener {
        void onSuccess(String messageId);
        void onFailure(String error);
    }
}
