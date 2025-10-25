package com.internship.healthcare.models;
/**
 * ChatMessage.java
 * A comprehensive healthcare management Android application
 *
 * Package: com.internship.healthcare.models
 *
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */
public class ChatMessage {
    private String id;
    private String senderId;
    private String receiverId;
    private String message;
    private long timestamp;
    private boolean isRead;
    private String messageType; // "text", "image"
    private String imageUrl;

    private String senderName;
    
    public ChatMessage() {
        // Required empty constructor for Firebase
    }
    
    public ChatMessage(String id, String senderId, String receiverId, String message, 
                   long timestamp, boolean isRead, String messageType, String imageUrl, String senderName) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.messageType = messageType;
        this.imageUrl = imageUrl;
        this.senderName = senderName;
    }
    
    public String getId() {
        return id;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public String getReceiverId() {
        return receiverId;
    }
    
    public String getMessage() {
        return message;
    }

    
    public long getTimestamp() {
        return timestamp;
    }
    
    public boolean isRead() {
        return isRead;

    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public String getImageUrl() {

        return imageUrl;
    }
    
    public String getSenderName() {
        return senderName;
    }
    

    public void setId(String id) {
        this.id = id;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    
    public void setMessage(String message) {
        this.message = message;

    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setRead(boolean read) {

        isRead = read;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

}









