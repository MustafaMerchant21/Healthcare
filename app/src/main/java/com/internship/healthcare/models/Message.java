/**
 * Message.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * Data model class representing message entity in patient information and records system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare.models;

public class Message {
    private String doctorName;
    private String lastMessage;
    private String timestamp;
    private int doctorImageResId;
    private boolean isOnline;
    private boolean isOlderThan24Hours;
    private int unreadCount;


    public Message(String doctorName, String lastMessage, String timestamp,
                   int doctorImageResId, boolean isOnline, boolean isOlderThan24Hours) {
        this.doctorName = doctorName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.doctorImageResId = doctorImageResId;
        this.isOnline = isOnline;
        this.isOlderThan24Hours = isOlderThan24Hours;
        this.unreadCount = 0;
    }

    public Message(String doctorName, String lastMessage, String timestamp,
                   int doctorImageResId, boolean isOnline, boolean isOlderThan24Hours, int unreadCount) {
        this.doctorName = doctorName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.doctorImageResId = doctorImageResId;
        this.isOnline = isOnline;
        this.isOlderThan24Hours = isOlderThan24Hours;
        this.unreadCount = unreadCount;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;

    }

    public int getDoctorImageResId() {
        return doctorImageResId;
    }

    public void setDoctorImageResId(int doctorImageResId) {
        this.doctorImageResId = doctorImageResId;

    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {

        isOnline = online;
    }

    public boolean isOlderThan24Hours() {
        return isOlderThan24Hours;
    }

    public void setOlderThan24Hours(boolean olderThan24Hours) {

        isOlderThan24Hours = olderThan24Hours;
    }

    public int getUnreadCount() {
        return unreadCount;
    }


    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}








