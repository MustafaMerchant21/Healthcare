/**
 * PaymentMethod.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * Data model class representing payment method entity in patient information and records system.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare.models;

public class PaymentMethod {
    
    public enum PaymentType {
        CARD,
        UPI
    }
    
    private String id;
    private PaymentType type;

    private String cardNumber;
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private String cardType; // Visa, Mastercard, etc.
    private String upiId;
    private boolean isDefault;
    private long timestamp;
    
    public PaymentMethod() {
        // Required empty constructor for Firebase
    }
    
    public PaymentMethod(String id, PaymentType type, String cardNumber, String cardHolderName, 
                        String expiryMonth, String expiryYear, String cardType, String upiId, 
                        boolean isDefault, long timestamp) {
        this.id = id;
        this.type = type;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cardType = cardType;
        this.upiId = upiId;
        this.isDefault = isDefault;
        this.timestamp = timestamp;
    }
    
    public String getId() {
        return id;
    }
    
    public PaymentType getType() {
        return type;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public String getCardHolderName() {
        return cardHolderName;
    }

    
    public String getExpiryMonth() {
        return expiryMonth;
    }
    
    public String getExpiryYear() {
        return expiryYear;

    }
    
    public String getCardType() {
        return cardType;
    }
    
    public String getUpiId() {

        return upiId;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    

    public long getTimestamp() {
        return timestamp;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    
    public void setType(PaymentType type) {
        this.type = type;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;

    }
    
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
    
    public void setExpiryMonth(String expiryMonth) {

        this.expiryMonth = expiryMonth;
    }
    
    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }
    

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
    
    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    
    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;

    }
    
    // Utility methods
    public String getMaskedCardNumber() {
        if (cardNumber != null && cardNumber.length() >= 4) {
            return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        }

        return "****";
    }
    
    public String getExpiryDate() {
        if (expiryMonth != null && expiryYear != null) {
            return expiryMonth + "/" + expiryYear;
        }
        return "";

    }
    
    // Helper method to detect card type from card number
    public static String detectCardType(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "Card";
        }
        

        String cleaned = cardNumber.replaceAll("\\s", "");
        
        if (cleaned.startsWith("4")) {
            return "Visa";
        } else if (cleaned.matches("^5[1-5].*")) {
            return "Mastercard";
        } else if (cleaned.matches("^3[47].*")) {
            return "Amex";

        } else if (cleaned.matches("^6(?:011|5).*")) {
            return "Discover";
        } else if (cleaned.matches("^(?:2131|1800|35).*")) {
            return "JCB";
        } else {
            return "Card";
        }
    }

}








