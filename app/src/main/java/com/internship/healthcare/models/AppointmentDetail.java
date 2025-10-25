package com.internship.healthcare.models;

/**
 * AppointmentDetail.java
 * A comprehensive healthcare management Android application
 *
 * Package: com.internship.healthcare.models
 * Data model class representing appointment detail entity in appointment scheduling and management system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */
public class AppointmentDetail {
    private String id;
    private String doctorId;
    private String doctorName;
    private String doctorCategory;
    private String doctorImage;
    private String appointmentDate;
    private String appointmentTime;
    private String doctorPhone;

    private String status; // upcoming, completed, cancelled
    private long timestamp;
    
    public AppointmentDetail() {
        // Required empty constructor for Firebase
    }
    
    public AppointmentDetail(String id, String doctorId, String doctorName, String doctorCategory,
                           String doctorImage, String appointmentDate, String appointmentTime,
                           String doctorPhone, String status, long timestamp) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorCategory = doctorCategory;
        this.doctorImage = doctorImage;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.doctorPhone = doctorPhone;
        this.status = status;
        this.timestamp = timestamp;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public String getDoctorCategory() {
        return doctorCategory;
    }
    
    
    public String getDoctorImage() {
        return doctorImage;
    }
    
    public String getAppointmentDate() {
        return appointmentDate;
    
    }
    
    public String getAppointmentTime() {
        return appointmentTime;
    }
    
    public String getDoctorPhone() {
    
        return doctorPhone;
    }
    
    public String getStatus() {
        return status;
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
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    
    }
    
    public void setDoctorCategory(String doctorCategory) {
        this.doctorCategory = doctorCategory;
    }
    
    public void setDoctorImage(String doctorImage) {
    
        this.doctorImage = doctorImage;
    }
    
    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    
    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }
    
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    
    }
    
    // Utility method
    public String getFormattedDateTime() {
        return appointmentDate + ", " + appointmentTime;
    }
}
    

    
    
    
    
    
    
    
    
    
    