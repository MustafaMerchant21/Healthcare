package com.internship.healthcare.models;

import java.util.HashMap;
import java.util.Map;
/**
 * DoctorSchedule.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * Data model class representing doctor schedule entity in appointment scheduling and management system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class DoctorSchedule {
    private String doctorId;
    private Map<String, DaySchedule> weekSchedule; // "monday", "tuesday", etc.
    private int appointmentDuration; // in minutes (e.g., 30, 45, 60)
    private long lastUpdated;

    public DoctorSchedule() {
        // Required empty constructor for Firebase
        this.weekSchedule = new HashMap<>();

        this.appointmentDuration = 30; // Default 30 minutes
    }

    public DoctorSchedule(String doctorId) {
        this.doctorId = doctorId;
        this.weekSchedule = new HashMap<>();
        this.appointmentDuration = 30;
        this.lastUpdated = System.currentTimeMillis();
        
        initializeDefaultSchedule();
    }

    private void initializeDefaultSchedule() {
        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        for (String day : days) {
            weekSchedule.put(day, new DaySchedule());
        }
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }


    public Map<String, DaySchedule> getWeekSchedule() {
        return weekSchedule;
    }

    public void setWeekSchedule(Map<String, DaySchedule> weekSchedule) {
        this.weekSchedule = weekSchedule;
    }

    public int getAppointmentDuration() {

        return appointmentDuration;
    }

    public void setAppointmentDuration(int appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
    }


    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Helper methods
    public DaySchedule getDaySchedule(String day) {
        return weekSchedule.get(day.toLowerCase());
    }


    public void setDaySchedule(String day, DaySchedule schedule) {
        weekSchedule.put(day.toLowerCase(), schedule);
        this.lastUpdated = System.currentTimeMillis();
    }

    // Inner class for daily schedule
    public static class DaySchedule {

        private boolean isAvailable;
        private String startTime; // Format: "HH:mm" (24-hour)
        private String endTime;   // Format: "HH:mm" (24-hour)
        private String breakStartTime; // Optional break
        private String breakEndTime;

        public DaySchedule() {

            // Default: Not available
            this.isAvailable = false;
            this.startTime = "09:00";
            this.endTime = "17:00";
            this.breakStartTime = "12:00";
            this.breakEndTime = "13:00";
        }


        public DaySchedule(boolean isAvailable, String startTime, String endTime) {
            this.isAvailable = isAvailable;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public DaySchedule(boolean isAvailable, String startTime, String endTime, 

                          String breakStartTime, String breakEndTime) {
            this.isAvailable = isAvailable;
            this.startTime = startTime;
            this.endTime = endTime;
            this.breakStartTime = breakStartTime;
            this.breakEndTime = breakEndTime;
        }

        public boolean isAvailable() {

            return isAvailable;
        }

        public void setAvailable(boolean available) {
            isAvailable = available;
        }

        public String getStartTime() {
            return startTime;

        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getBreakStartTime() {
            return breakStartTime;
        }

        public void setBreakStartTime(String breakStartTime) {
            this.breakStartTime = breakStartTime;
        }

        public String getBreakEndTime() {
            return breakEndTime;
        }

        public void setBreakEndTime(String breakEndTime) {
            this.breakEndTime = breakEndTime;
        }

        // Helper method to check if break is configured
        public boolean hasBreak() {
            return breakStartTime != null && !breakStartTime.isEmpty() &&
                   breakEndTime != null && !breakEndTime.isEmpty();
        }
    }
}











