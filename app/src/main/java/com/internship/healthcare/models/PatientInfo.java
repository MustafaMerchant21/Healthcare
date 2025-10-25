/**
 * PatientInfo.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * Data model class representing patient info entity in patient information and records system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare.models;

public class PatientInfo {
    private String patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String patientImageUrl;
    private int totalAppointments;
    private long lastVisitDate;
    private long firstVisitDate;

    private String lastAppointmentStatus; // "completed", "cancelled", "upcoming"

    public PatientInfo() {
        // Required empty constructor for Firebase
    }

    public PatientInfo(String patientId, String patientName, String patientEmail, String patientPhone) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientPhone = patientPhone;
        this.totalAppointments = 0;
        this.lastVisitDate = 0;
        this.firstVisitDate = System.currentTimeMillis();
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }


    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;

    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;

    }

    public String getPatientImageUrl() {
        return patientImageUrl;
    }

    public void setPatientImageUrl(String patientImageUrl) {

        this.patientImageUrl = patientImageUrl;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {

        this.totalAppointments = totalAppointments;
    }

    public long getLastVisitDate() {
        return lastVisitDate;
    }


    public void setLastVisitDate(long lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public long getFirstVisitDate() {
        return firstVisitDate;
    }


    public void setFirstVisitDate(long firstVisitDate) {
        this.firstVisitDate = firstVisitDate;
    }

    public String getLastAppointmentStatus() {
        return lastAppointmentStatus;
    }


    public void setLastAppointmentStatus(String lastAppointmentStatus) {
        this.lastAppointmentStatus = lastAppointmentStatus;
    }

    // Helper methods
    public boolean hasVisited() {
        return totalAppointments > 0;

    }

    public String getFormattedTotalAppointments() {
        if (totalAppointments == 1) {
            return "1 appointment";
        } else {
            return totalAppointments + " appointments";

        }
    }
}










