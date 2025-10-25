package com.internship.healthcare.utils;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * SessionManager.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.utils
 * Manager class handling session business logic and coordination in patient information and records.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class SessionManager {
    private static final String PREF_NAME = "HealthcareSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    
    public void createLoginSession(String userId, String email, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    
    public String getUserId() {
        return preferences.getString(KEY_USER_ID, null);
    }

    
    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, null);
    }

    
    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, null);
    }

    
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
