
package com.internship.healthcare.models;

/**
 * Doctor.java
 * A comprehensive healthcare management Android application
 *
 * Package: com.internship.healthcare.models
 *
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

public class Doctor {
    private String id;
    private String name;
    private String speciality;
    private String image;
    private double consultationFee;
    private String mobile;
    private double rating;
    private int experience; // in years
    private String qualification;
    private String about;
    private String degree;
    private String university;
    private String clinicAddress;
    private long timestamp;


    public Doctor() {
    }

    public Doctor(String id, String name, String speciality, String image, 
                 double consultationFee, String mobile, double rating, int experience) {
        this.id = id;
        this.name = name;
        this.speciality = speciality;
        this.image = image;
        this.consultationFee = consultationFee;
        this.mobile = mobile;
        this.rating = rating;
        this.experience = experience;
    }

    public Doctor(String id, String name, String speciality, String image, 
                 double consultationFee, String mobile, double rating, int experience,
                 String about, String degree, String university) {
        this.id = id;
        this.name = name;
        this.speciality = speciality;
        this.image = image;
        this.consultationFee = consultationFee;
        this.mobile = mobile;
        this.rating = rating;
        this.experience = experience;
        this.about = about;
        this.degree = degree;
        this.university = university;
        this.clinicAddress = "";
    }

    public Doctor(String id, String name, String speciality, String image, 
                 double consultationFee, String mobile, double rating, int experience,
                 String about, String degree, String university, String clinicAddress) {
        this.id = id;
        this.name = name;
        this.speciality = speciality;
        this.image = image;
        this.consultationFee = consultationFee;
        this.mobile = mobile;
        this.rating = rating;
        this.experience = experience;
        this.about = about;
        this.degree = degree;
        this.university = university;
        this.clinicAddress = clinicAddress;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpeciality() {

        return speciality;
    }

    public String getImage() {
        return image;
    }


    public double getConsultationFee() {
        return consultationFee;
    }

    public String getMobile() {
        return mobile;
    }


    public double getRating() {
        return rating;
    }

    public int getExperience() {
        return experience;

    }

    public String getQualification() {
        return qualification;
    }

    public String getAbout() {

        return about;
    }

    public long getTimestamp() {
        return timestamp;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public void setImage(String image) {
        this.image = image;

    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public void setMobile(String mobile) {

        this.mobile = mobile;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }


    public void setAbout(String about) {
        this.about = about;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;

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

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;

    }

    // Utility methods
    public String getFormattedFee() {
        return "₹" + (int)consultationFee;
    }

    public String getFormattedRating() {

        return String.format("%.1f", rating);
    }
}














