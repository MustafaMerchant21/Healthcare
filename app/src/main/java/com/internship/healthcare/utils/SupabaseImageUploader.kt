package com.internship.healthcare.utils

import android.content.Context
import android.net.Uri
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Utility class for uploading images to Supabase Storage.
 * Handles asynchronous image uploads with callback-based result handling.
 *
 * @param context Android context for accessing content resolver
 */
class SupabaseImageUploader(private val context: Context) {

    private val storage = SupabaseConfig.client.storage

    interface UploadCallback {
        fun onSuccess(publicUrl: String)
        fun onFailure(error: String)
    }

    
    fun uploadImage(
        imageUri: Uri,
        bucketName: String,
        fileName: String,
        callback: UploadCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Read file bytes
                val fileBytes = readBytesFromUri(imageUri)
                if (fileBytes == null) {
                    withContext(Dispatchers.Main) {
                        callback.onFailure("Failed to read image file")
                    }
                    return@launch
                }

                // Upload to Supabase
                val bucket = storage.from(bucketName)
                bucket.upload(fileName, fileBytes) {
                    upsert = false // Don't overwrite existing files
                }

                val publicUrl = bucket.publicUrl(fileName)

                withContext(Dispatchers.Main) {
                    callback.onSuccess(publicUrl)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure("Upload failed: ${e.message}")
                }
            }
        }
    }

    
    fun uploadImageWithUpsert(
        imageUri: Uri,
        bucketName: String,
        fileName: String,
        callback: UploadCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fileBytes = readBytesFromUri(imageUri)
                if (fileBytes == null) {
                    withContext(Dispatchers.Main) {
                        callback.onFailure("Failed to read image file")
                    }
                    return@launch
                }

                val bucket = storage.from(bucketName)
                bucket.upload(fileName, fileBytes) {
                    upsert = true // Allow overwriting existing files
                }

                val publicUrl = bucket.publicUrl(fileName)

                withContext(Dispatchers.Main) {
                    callback.onSuccess(publicUrl)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure("Upload failed: ${e.message}")
                }
            }
        }
    }

    private suspend fun readBytesFromUri(uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val byteBuffer = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var len: Int
                while (inputStream.read(buffer).also { len = it } != -1) {
                    byteBuffer.write(buffer, 0, len)
                }
                byteBuffer.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        // Common bucket names
        const val BUCKET_DOCTOR_PROFILES = "doctor-profiles"
        const val BUCKET_USER_PROFILES = "user-profiles"
        const val BUCKET_DOCTOR_CERTIFICATES = "doctor-certificates"
    }
}