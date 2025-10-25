/**
 * UserLocation.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * Data model class representing user location entity in patient information and records system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare.models;

public class UserLocation {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private boolean isDefault;
    private long timestamp;

    
    public UserLocation() {
        // Required empty constructor for Firebase
    }
    
    public UserLocation(String addressLine1, String addressLine2, String city, String state, 
                       String pincode, String country, boolean isDefault, long timestamp) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.country = country;
        this.isDefault = isDefault;
        this.timestamp = timestamp;
    }
    
    public String getAddressLine1() {
        return addressLine1;
    }
    
    public String getAddressLine2() {
        return addressLine2;
    }
    
    public String getCity() {
        return city;
    }
    
    public String getState() {
        return state;
    }

    
    public String getPincode() {
        return pincode;
    }
    
    public String getCountry() {
        return country;

    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public long getTimestamp() {

        return timestamp;
    }
    
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    
    public void setCity(String city) {
        this.city = city;
    }

    
    public void setState(String state) {
        this.state = state;
    }
    
    public void setPincode(String pincode) {
        this.pincode = pincode;

    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public void setDefault(boolean aDefault) {

        isDefault = aDefault;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    

    // Utility method to get full address
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        
        if (addressLine1 != null && !addressLine1.isEmpty()) {
            address.append(addressLine1);
        }

        
        if (addressLine2 != null && !addressLine2.isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(addressLine2);
        }
        
        if (city != null && !city.isEmpty()) {
            if (address.length() > 0) address.append(", ");

            address.append(city);
        }
        
        if (state != null && !state.isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(state);
        }
        

        if (pincode != null && !pincode.isEmpty()) {
            if (address.length() > 0) address.append(" - ");
            address.append(pincode);
        }
        
        if (country != null && !country.isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(country);

        }
        
        return address.toString();
    }
}





