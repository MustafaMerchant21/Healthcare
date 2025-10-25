/**
 * Service.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.models
 * A background Service for handling long-running operations.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */

package com.internship.healthcare.models;

public class Service {
    private String name;
    private int iconResId;

    public Service(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

}



