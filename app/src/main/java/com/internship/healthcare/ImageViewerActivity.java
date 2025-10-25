package com.internship.healthcare;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
/**
 * ImageViewerActivity.java
 * A comprehensive healthcare management Android application
 * Activity handling image viewer screen and user interactions.
 *
 * <p>Extends: {@link AppCompatActivity}</p>
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class ImageViewerActivity extends AppCompatActivity {

    private PhotoView photoView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);


        photoView = findViewById(R.id.photo_view);
        backButton = findViewById(R.id.back_button);

        String imageUrl = getIntent().getStringExtra("imageUrl");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(photoView);
        } else {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            finish();
    
        }

        backButton.setOnClickListener(v -> finish());
    }
}
