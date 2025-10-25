package com.internship.healthcare.models;
/**
 * DoctorCategory.java
 * A comprehensive healthcare management Android application
 *
 * Package: com.internship.healthcare.models
 * Data model class representing doctor category entity in doctor profiles and management system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */
public class DoctorCategory {
    private String id;
    private String categoryName;
    private int doctorCount;
    private double averagePrice;
    private String iconUrl;
    private boolean isPopular;
    
    public DoctorCategory() {

        // Required empty constructor for Firebase
    }
    
    public DoctorCategory(String id, String categoryName, int doctorCount, double averagePrice, 
                         String iconUrl, boolean isPopular) {
        this.id = id;
        this.categoryName = categoryName;
        this.doctorCount = doctorCount;
        this.averagePrice = averagePrice;
        this.iconUrl = iconUrl;
        this.isPopular = isPopular;
    }
    
    public String getId() {
        return id;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public int getDoctorCount() {
        return doctorCount;
    }
    
    public double getAveragePrice() {
        return averagePrice;
    }

    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public boolean isPopular() {
        return isPopular;

    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setCategoryName(String categoryName) {

        this.categoryName = categoryName;
    }
    
    public void setDoctorCount(int doctorCount) {
        this.doctorCount = doctorCount;
    }
    

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    
    public void setPopular(boolean popular) {
        isPopular = popular;
    }
    
    // Utility methods
    public String getFormattedPrice() {

        if (averagePrice <= 0) {
            return "NA";
        }
        return "~₹" + (int) averagePrice;
    }
    
    public String getDoctorCountText() {

        return doctorCount + " Doctor" + (doctorCount != 1 ? "s" : "") + " Available";
    }
}







