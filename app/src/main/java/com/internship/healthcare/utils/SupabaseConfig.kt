package com.internship.healthcare.utils

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

/**
 * Configuration object for Supabase client and storage.
 * Provides centralized access to Supabase services for file uploads and storage management.
 */
object SupabaseConfig {
    private const val SUPABASE_URL = "https://pelfshmkjrrmqjkdbvso.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBlbGZzaG1ranJybXFqa2RidnNvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA4MDEzMjksImV4cCI6MjA3NjM3NzMyOX0.5CU8T3DZSs4g_iUW6RdQd252tUzVWY06ZlrS-oVnNyk"

    const val DOCTOR_CERTIFICATES_BUCKET = "doctor-certificates"

    val client = createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
        install(Storage)
    }

    val storage: Storage
        get() = client.storage
}