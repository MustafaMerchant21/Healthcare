package com.internship.healthcare.models;

import java.util.List;
/**
 * VerificationRequest.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * Data model class representing verification request entity in patient information and records system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class VerificationRequest {
    private String requestId;
    private String userId;
    private String userName;
    private String userEmail;
    private String specialty;
    private String degree;
    private String university;
    private int experienceYears;

    private double consultationFee;
    private String about;
    private String hospitalAffiliation;
    private String contactNumber;
    private List<String> documentUrls; // Uploaded certificates/documents
    private String status; // "pending", "approved", "rejected"
    private long submittedAt;
    private long reviewedAt;
    private String reviewedBy; // Admin ID
    private String rejectionReason;

    public VerificationRequest() {
        this.status = "pending";
        this.submittedAt = System.currentTimeMillis();
    }

    public VerificationRequest(String requestId, String userId, String userName, String userEmail,
                               String specialty, String degree, String university, int experienceYears,
                               double consultationFee, String about, String hospitalAffiliation,
                               String contactNumber, List<String> documentUrls) {
        this.requestId = requestId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.specialty = specialty;
        this.degree = degree;
        this.university = university;
        this.experienceYears = experienceYears;
        this.consultationFee = consultationFee;
        this.about = about;
        this.hospitalAffiliation = hospitalAffiliation;
        this.contactNumber = contactNumber;
        this.documentUrls = documentUrls;
        this.status = "pending";
        this.submittedAt = System.currentTimeMillis();
    }

    public VerificationRequest(String userId, String specialty, String degree, String university, int experience, double consultationFee, List<String> documentUrls) {
        this.userId = userId;
        this.specialty = specialty;
        this.degree = degree;
        this.university = university;
        this.experienceYears = experience;
        this.consultationFee = consultationFee;
        this.documentUrls = documentUrls;
        this.status = "pending";
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    
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

    public List<String> getDocumentUrls() {
    
        return documentUrls;
    }

    public void setDocumentUrls(List<String> documentUrls) {
        this.documentUrls = documentUrls;
    }

    public String getStatus() {
    
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    
    public long getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(long submittedAt) {
        this.submittedAt = submittedAt;
    }

    
    public long getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(long reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    
    }
}

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    