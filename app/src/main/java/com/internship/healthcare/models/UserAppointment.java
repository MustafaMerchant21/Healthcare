/**
 * UserAppointment.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * Data model class representing user appointment entity in appointment scheduling and management system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare.models;

public class UserAppointment {
    private String id;
    private String userId;
    private String doctorId;
    private String doctorName;
    private String doctorSpeciality;
    private String doctorImage;
    private String appointmentDate;
    private String appointmentTime;

    private double consultationFee;
    private String status; // "pending", "approved", "rejected", "completed", "cancelled"
    private long timestamp;
    private String notes;
    private String reason; // Patient's reason for appointment
    private String patientName; // Patient's name for doctor's view
    private String patientPhone; // Patient's contact
    private String doctorPhone; // Doctor's contact number
    private boolean patientCounted; // Track if this patient has been counted in doctor's total
    private boolean ratingGiven; // Track if patient has rated this appointment

    public UserAppointment() {
        // Required empty constructor for Firebase
        this.patientCounted = false;
        this.ratingGiven = false;
    }

    public UserAppointment(String id, String userId, String doctorId, String doctorName,
                          String doctorSpeciality, String doctorImage, String appointmentDate,
                          String appointmentTime, double consultationFee, String status,
                          long timestamp, String notes) {
        this.id = id;
        this.userId = userId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSpeciality = doctorSpeciality;
        this.doctorImage = doctorImage;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.consultationFee = consultationFee;
        this.status = status;
        this.timestamp = timestamp;
        this.notes = notes;
        this.reason = "";
        this.patientName = "";
        this.patientPhone = "";
        this.doctorPhone = "";
    }

    // Extended constructor with reason and patient details
    public UserAppointment(String id, String userId, String doctorId, String doctorName,
                          String doctorSpeciality, String doctorImage, String appointmentDate,
                          String appointmentTime, double consultationFee, String status,
                          long timestamp, String notes, String reason, String patientName, 
                          String patientPhone, String doctorPhone) {
        this.id = id;
        this.userId = userId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSpeciality = doctorSpeciality;
        this.doctorImage = doctorImage;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.consultationFee = consultationFee;
        this.status = status;
        this.timestamp = timestamp;
        this.notes = notes;
        this.reason = reason;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.doctorPhone = doctorPhone;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }


    public String getDoctorSpeciality() {
        return doctorSpeciality;
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

    public double getConsultationFee() {
        return consultationFee;
    }


    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }


    public String getNotes() {
        return notes;
    }

    public void setId(String id) {
        this.id = id;

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDoctorId(String doctorId) {

        this.doctorId = doctorId;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }


    public void setDoctorSpeciality(String doctorSpeciality) {
        this.doctorSpeciality = doctorSpeciality;
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

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getReason() {
        return reason;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPatientName() {
        return patientName;
    }


    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }


    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }


    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public boolean isPatientCounted() {
        return patientCounted;
    }


    public void setPatientCounted(boolean patientCounted) {
        this.patientCounted = patientCounted;
    }

    public boolean isRatingGiven() {
        return ratingGiven;
    }


    public void setRatingGiven(boolean ratingGiven) {
        this.ratingGiven = ratingGiven;
    }
}


















