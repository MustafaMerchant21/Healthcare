/**
 * Appointment.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * Model class representing an appointment in the healthcare system.
 * Contains appointment details including doctor information, date, and time.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare.models;


public class Appointment {
    private String doctorName;
    private String doctorSpecialty;
    private String appointmentDate;
    private String appointmentTime;
    private int doctorImageResId;

    public Appointment(String doctorName, String doctorSpecialty, String appointmentDate, 
                       String appointmentTime, int doctorImageResId) {
        this.doctorName = doctorName;
        this.doctorSpecialty = doctorSpecialty;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.doctorImageResId = doctorImageResId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialty() {

        return doctorSpecialty;
    }

    public void setDoctorSpecialty(String doctorSpecialty) {
        this.doctorSpecialty = doctorSpecialty;
    }


    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }


    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }


    public int getDoctorImageResId() {
        return doctorImageResId;
    }

    public void setDoctorImageResId(int doctorImageResId) {
        this.doctorImageResId = doctorImageResId;
    }

}





