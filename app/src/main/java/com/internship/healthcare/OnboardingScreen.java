package com.internship.healthcare;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.internship.healthcare.databinding.ActivityOnboardingScreenBinding;

import java.util.ArrayList;
import java.util.List;
/**
 * OnboardingScreen.java
 * A comprehensive healthcare management Android application
 * An Android Activity representing a single screen in the healthcare application.
 *
 * <p>Extends: {@link AppCompatActivity}</p>
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */



public class OnboardingScreen extends AppCompatActivity {
    private ActivityOnboardingScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingScreenBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView carouselRecyclerView = findViewById(R.id.recyclerView);
        carouselRecyclerView.setHasFixedSize(true);

        List<Integer> imageResources = new ArrayList<>();
    
        imageResources.add(R.drawable.on_1);
        imageResources.add(R.drawable.on_2);
        imageResources.add(R.drawable.on_3);
        imageResources.add(R.drawable.on_4);

        CarouselAdapter adapter = new CarouselAdapter(imageResources);
        carouselRecyclerView.setAdapter(adapter);

        CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager();
        carouselRecyclerView.setLayoutManager(carouselLayoutManager);

        CarouselSnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(carouselRecyclerView);

        MaterialButton signin, signup;
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);

        Intent toSignin = new Intent(this, SignInScreen.class);
        Intent toSignup = new Intent(this, SignUpScreen.class);

        signin.setOnClickListener(v -> {
            startActivity(toSignin);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        signup.setOnClickListener(v -> {
            startActivity(toSignup);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }
}