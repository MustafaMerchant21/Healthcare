/**
 * DoctorRating.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * Data model class representing doctor rating entity in doctor profiles and management system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare.models;

public class DoctorRating {
    private String id;
    private String doctorId;
    private String userId;
    private String userName;
    private String appointmentId;
    private float rating; // 1-5 stars
    private String review; // Optional review text
    private long timestamp;


    public DoctorRating() {
        // Required empty constructor for Firebase
    }

    public DoctorRating(String id, String doctorId, String userId, String userName, 
                       String appointmentId, float rating, String review, long timestamp) {
        this.id = id;
        this.doctorId = doctorId;
        this.userId = userId;
        this.userName = userName;
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.review = review;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }


    public String getAppointmentId() {
        return appointmentId;
    }

    public float getRating() {
        return rating;

    }

    public String getReview() {
        return review;
    }

    public long getTimestamp() {

        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;

    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setReview(String review) {

        this.review = review;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}









