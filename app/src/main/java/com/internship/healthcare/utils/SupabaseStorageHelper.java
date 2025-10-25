package com.internship.healthcare.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.jan.supabase.storage.BucketApi;
import io.github.jan.supabase.storage.Storage;
import kotlin.Unit;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;
/**
 * SupabaseStorageHelper.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.utils
 * Helper class providing utility methods for supabase storage in patient information and records.
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class SupabaseStorageHelper {

    private final Context context;
    private final Storage storage;
    private final ExecutorService executorService;

    public SupabaseStorageHelper(Context context) {
        this.context = context;
        this.storage = SupabaseConfig.INSTANCE.getStorage();

        this.executorService = Executors.newFixedThreadPool(3);
    }

    public interface UploadCallback {
        void onProgress(int uploadedCount, int totalCount);
        void onSuccess(List<String> urls);
        void onFailure(String error);
    }

    
    public void uploadDocuments(String userId, List<Uri> documentUris, UploadCallback callback) {
        List<String> uploadedUrls = new ArrayList<>();
        int[] uploadCount = {0};
        int[] failureCount = {0};
        int totalDocuments = documentUris.size();

        for (int i = 0; i < documentUris.size(); i++) {
            final int index = i;
            Uri documentUri = documentUris.get(i);

            executorService.execute(() -> {
                try {
                    // Read file bytes
                    byte[] fileBytes = readBytesFromUri(documentUri);
                    if (fileBytes == null) {
                        handleFailure(uploadCount, failureCount, totalDocuments,
                                uploadedUrls, callback, "Failed to read file");
                        return;
                    }

                    String extension = getFileExtension(documentUri);
                    String fileName = userId + "/certificate_" + System.currentTimeMillis() + "_" + index + "." + extension;

                    // Upload to Supabase using coroutine
                    uploadToSupabase(fileName, fileBytes, new UploadResult() {
                        @Override
                        public void onSuccess(String publicUrl) {
                            synchronized (uploadedUrls) {
                                uploadedUrls.add(publicUrl);
                                uploadCount[0]++;
                                callback.onProgress(uploadCount[0], totalDocuments);

                                if (uploadCount[0] + failureCount[0] == totalDocuments) {
                                    if (uploadCount[0] > 0) {
                                        callback.onSuccess(uploadedUrls);
                                    } else {
                                        callback.onFailure("All uploads failed");
                                    }
                                }
                            }
                        }
    

                        @Override
                        public void onFailure(String error) {
                            handleFailure(uploadCount, failureCount, totalDocuments,
                                    uploadedUrls, callback, error);
                        }
                    });

                } catch (Exception e) {
                    handleFailure(uploadCount, failureCount, totalDocuments,
                            uploadedUrls, callback, e.getMessage());
                }
            });
        }
    }

    private interface UploadResult {
        void onSuccess(String publicUrl);
        void onFailure(String error);
    }

    
    private void uploadToSupabase(String fileName, byte[] fileBytes, UploadResult result) {
        try {
            BucketApi bucket = storage.from(SupabaseConfig.DOCTOR_CERTIFICATES_BUCKET);

            // Upload file using runBlocking with upload options
            BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE, (scope, continuation) ->
                    bucket.upload(
                            fileName,
                            fileBytes,
                            builder -> {
                                builder.setUpsert(false); // Don't overwrite existing files
                                return Unit.INSTANCE;
                            },
                            continuation
                    )
            );

            String publicUrl = bucket.publicUrl(fileName);

            result.onSuccess(publicUrl);

        } catch (Exception e) {
            result.onFailure("Upload failed: " + e.getMessage());
    
        }
    }

    private void handleFailure(int[] uploadCount, int[] failureCount, int totalDocuments,
                               List<String> uploadedUrls, UploadCallback callback, String error) {
        synchronized (uploadedUrls) {
            failureCount[0]++;

            if (uploadCount[0] + failureCount[0] == totalDocuments) {
                if (uploadCount[0] > 0) {
                    callback.onSuccess(uploadedUrls);
                } else {
                    callback.onFailure(error);
                }
            }
        }
    }

    private byte[] readBytesFromUri(Uri uri) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null) return null;

            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            inputStream.close();
            return byteBuffer.toByteArray();
    

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileExtension(Uri uri) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        if (extension == null || extension.isEmpty()) {
            // Try to get from MIME type
            ContentResolver contentResolver = context.getContentResolver();
            String mimeType = contentResolver.getType(uri);
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }
        return extension != null ? extension : "pdf";
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
    
    