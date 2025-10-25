/**
 * Notification.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * Data model class representing notification entity in patient information and records system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare.models;

public class Notification {
    private String id;
    private String userId;
    private String title;
    private String message;
    private String time;
    private long timestamp;
    private int iconResId;
    private boolean isRead;

    private String type; // "appointment_approved", "appointment_rejected", "appointment_reminder", "message", "general"
    private String relatedId; // appointmentId, messageId, etc.
    private String doctorId;
    private String doctorName;
    private String doctorImage;

    public Notification() {
        // Required empty constructor for Firebase
    }

    public Notification(String title, String message, String time, int iconResId, boolean isRead, String type) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.iconResId = iconResId;
        this.isRead = isRead;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public Notification(String id, String userId, String title, String message, long timestamp, 
                       boolean isRead, String type, String relatedId) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.type = type;
        this.relatedId = relatedId;
    }

    public Notification(String id, String userId, String title, String message, long timestamp,
                       boolean isRead, String type, String relatedId, String doctorId,
                       String doctorName, String doctorImage) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.type = type;
        this.relatedId = relatedId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorImage = doctorImage;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
    

    public String getTime() {
        return time;
    }

    public long getTimestamp() {
        return timestamp;
    
    }

    public int getIconResId() {
        return iconResId;
    }

    public boolean isRead() {
    
        return isRead;
    }

    public String getType() {
        return type;
    }

    
    public String getRelatedId() {
        return relatedId;
    }

    public String getDoctorId() {
        return doctorId;
    }
    

    public String getDoctorName() {
        return doctorName;
    }

    public String getDoctorImage() {
        return doctorImage;
    
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
    
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    
    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }
    

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setType(String type) {
    
        this.type = type;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    

    public void setDoctorImage(String doctorImage) {
        this.doctorImage = doctorImage;
    }
}

    
    
    
    
    
    
    
    




