/**
 * DoctorNameFormatter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.utils
 * Formats a name to include "Dr." prefix
 * Removes any existing "Dr.", "Dr", "doctor", "Doctor" prefix and adds "Dr." at the start
 *
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare.utils;

public class DoctorNameFormatter {
    
    

    public static String formatDoctorName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return name;
        }
        
        String trimmedName = name.trim();
        
        if (trimmedName.toLowerCase().startsWith("dr.")) {
            trimmedName = trimmedName.substring(3).trim();
        } else if (trimmedName.toLowerCase().startsWith("dr ")) {
            trimmedName = trimmedName.substring(2).trim();
        }
        
        if (trimmedName.toLowerCase().startsWith("doctor ")) {
            trimmedName = trimmedName.substring(6).trim();
        }
        
        return "Dr. " + trimmedName;
    }
    
    
    public static String removeDoctorPrefix(String name) {
        if (name == null || name.trim().isEmpty()) {
            return name;
        }
        
        String trimmedName = name.trim();
        
        if (trimmedName.toLowerCase().startsWith("dr.")) {
            return trimmedName.substring(3).trim();
        } else if (trimmedName.toLowerCase().startsWith("dr ")) {
            return trimmedName.substring(2).trim();
        }
        
        if (trimmedName.toLowerCase().startsWith("doctor ")) {
            return trimmedName.substring(6).trim();
        }
        
        return trimmedName;
    }
}
