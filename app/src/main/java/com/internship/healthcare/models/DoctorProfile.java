package com.internship.healthcare.models;

import java.util.List;
/**
 * DoctorProfile.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * Data model class representing doctor profile entity in doctor profiles and management system.
 *
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class DoctorProfile {
    private String userId; // Links to User
    private String specialty;
    private String degree;
    private String university;
    private int experienceYears;
    private double consultationFee;
    private String about;
    private String bio; // Short bio for profile

    private String clinicAddress; // Clinic/practice address
    private String hospitalAffiliation;
    private String contactNumber;
    private List<String> certificateUrls; // URLs to uploaded certificates in Firebase Storage
    private int totalPatients;
    private double rating;
    private int totalRatings;
    private String profileImageUrl;
    private long verifiedAt; // Timestamp when approved
    private String verifiedBy; // Admin ID who approved

    public DoctorProfile() {
        this.totalPatients = 0;
        this.rating = 0.0;
        this.totalRatings = 0;
    }

    public DoctorProfile(String userId, String specialty, String degree, String university,
                         int experienceYears, double consultationFee, String about,
                         String hospitalAffiliation, String contactNumber) {
        this.userId = userId;
        this.specialty = specialty;
        this.degree = degree;
        this.university = university;
        this.experienceYears = experienceYears;
        this.consultationFee = consultationFee;
        this.about = about;
        this.hospitalAffiliation = hospitalAffiliation;
        this.contactNumber = contactNumber;
        this.totalPatients = 0;
        this.rating = 0.0;
        this.totalRatings = 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }


    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;

    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;

    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {

        this.experienceYears = experienceYears;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {

        this.consultationFee = consultationFee;
    }

    public String getAbout() {
        return about;
    }


    public void setAbout(String about) {
        this.about = about;
    }

    public String getBio() {
        return bio;
    }


    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }


    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getHospitalAffiliation() {
        return hospitalAffiliation;
    }


    public void setHospitalAffiliation(String hospitalAffiliation) {
        this.hospitalAffiliation = hospitalAffiliation;
    }

    public String getContactNumber() {
        return contactNumber;

    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public List<String> getCertificateUrls() {
        return certificateUrls;

    }

    public void setCertificateUrls(List<String> certificateUrls) {
        this.certificateUrls = certificateUrls;
    }

    public int getTotalPatients() {

        return totalPatients;
    }

    public void setTotalPatients(int totalPatients) {
        this.totalPatients = totalPatients;
    }

    public double getRating() {

        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }


    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


    public long getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(long verifiedAt) {
        this.verifiedAt = verifiedAt;
    }


    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;

    }

    // Helper methods
    public void incrementPatientCount() {
        this.totalPatients++;
    }

    public void updateRating(double newRating) {

        double totalScore = this.rating * this.totalRatings;
        this.totalRatings++;
        this.rating = (totalScore + newRating) / this.totalRatings;
    }
}


















