/**
 * AuthHelper.java
 * A comprehensive healthcare management Android application
 * Helper class providing utility methods for auth in patient information and records.
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare;

public class AuthHelper {
    String name, username, email, mobile;

    public AuthHelper(String name, String username, String email, String mobile) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.mobile = mobile;
    }


    public AuthHelper() {
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setMobile(String mobile) {
        this.mobile = mobile;
    
    }

}

    
    
    
    
    