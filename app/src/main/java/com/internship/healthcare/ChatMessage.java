/**
 * ChatMessage.java
 * A comprehensive healthcare management Android application
 * Data model class representing chat message entity in patient information and records system.
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare;

public class ChatMessage {
    public static final int TYPE_DATE = 0;
    public static final int TYPE_USER = 1;
    public static final int TYPE_DOCTOR = 2;

    private int type;
    private String message;
    private String timestamp;
    private String dateLabel;

    private String imageUrl;
    private int userAvatarResId;
    private int doctorAvatarResId;

    public ChatMessage(String dateLabel) {
        this.type = TYPE_DATE;
        this.dateLabel = dateLabel;
    }

    public ChatMessage(int type, String message, String timestamp, int avatarResId) {
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        if (type == TYPE_USER) {
            this.userAvatarResId = avatarResId;
        } else {
            this.doctorAvatarResId = avatarResId;
        }
    }

    public ChatMessage(int type, String message, String timestamp, String imageUrl, int avatarResId) {
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        if (type == TYPE_USER) {
            this.userAvatarResId = avatarResId;
        } else {
            this.doctorAvatarResId = avatarResId;
        }
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDateLabel() {
        return dateLabel;
    }
    

    public String getImageUrl() {
        return imageUrl;
    }

    public int getUserAvatarResId() {
        return userAvatarResId;
    
    }

    public int getDoctorAvatarResId() {
        return doctorAvatarResId;
    }

    public boolean hasImage() {
    
        return imageUrl != null && !imageUrl.isEmpty();
    }

    public void setType(int type) {
        this.type = type;
    }

    
    public void setMessage(String message) {
        this.message = message;
    
    }

    public void setTimestamp(String timestamp) {
    
        this.timestamp = timestamp;
    }
    
    

    public void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    
    }

    public void setUserAvatarResId(int userAvatarResId) {
        this.userAvatarResId = userAvatarResId;
    }

    public void setDoctorAvatarResId(int doctorAvatarResId) {
    
        this.doctorAvatarResId = doctorAvatarResId;
    }
}

    
    
    
    
    
    
    
    