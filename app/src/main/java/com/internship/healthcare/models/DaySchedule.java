package com.internship.healthcare.models;
/**
 * DaySchedule.java
 * A comprehensive healthcare management Android application
 *
 * Package: com.internship.healthcare.models
 *
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */
public class DaySchedule {
    private boolean available;
    private String startTime;
    private String endTime;

    public DaySchedule() {
    }

    public DaySchedule(boolean available, String startTime, String endTime) {

        this.available = available;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
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
}




