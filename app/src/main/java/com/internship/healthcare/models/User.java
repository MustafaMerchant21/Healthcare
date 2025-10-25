/**
 * User.java
 * A comprehensive healthcare management Android application
 *
 * Package: com.internship.healthcare.models
 * Data model class representing user entity in patient information and records system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private String mobile;
    private String role; // "patient", "doctor", "admin"
    private boolean isVerified; // For doctors - whether their profile is verified
    private String doctorVerificationStatus; // "none", "pending", "approved", "rejected"


    public User() {
        this.role = "patient"; // Default role
        this.isVerified = false;
        this.doctorVerificationStatus = "none";
    }

    public User(String userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.mobile = phone;
        this.role = "patient"; // Default role
        this.isVerified = false;
        this.doctorVerificationStatus = "none";
    }

    // Full constructor
    public User(String userId, String name, String email, String phone, String role, 
                boolean isVerified, String doctorVerificationStatus) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.mobile = phone;
        this.role = role;
        this.isVerified = isVerified;
        this.doctorVerificationStatus = doctorVerificationStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;

    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String phone) {
        this.mobile = phone;

    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {

        this.role = role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public boolean getIsVerified() {

        return isVerified;
    }

    public void setIsVerified(boolean isVerified) { this.isVerified = isVerified; }
    public void setVerified(boolean verified) {
        isVerified = verified;
    }


    public String getDoctorVerificationStatus() {
        return doctorVerificationStatus;
    }

    public void setDoctorVerificationStatus(String doctorVerificationStatus) {
        this.doctorVerificationStatus = doctorVerificationStatus;
    }


    // Helper methods
    public boolean isDoctor() {
        return "doctor".equals(role);
    }

    public boolean isPatient() {

        return "patient".equals(role);
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public boolean isVerifiedDoctor() {

        return isDoctor() && isVerified && "approved".equals(doctorVerificationStatus);
    }
}











