package com.internship.healthcare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.internship.healthcare.AppointmentRequestsActivity;
import com.internship.healthcare.R;
import com.internship.healthcare.adapters.SchedulePagerAdapter;
/**
 * AppointmentsFragment.java
 * A comprehensive healthcare management Android application
 * Fragment managing appointment views with tab navigation.
 *
 * <p>Extends: {@link Fragment}</p>
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */
public class AppointmentsFragment extends Fragment {

    private TabLayout scheduleTabs;
    private ViewPager2 scheduleViewPager;
    private FloatingActionButton fabHistory;
    private SchedulePagerAdapter pagerAdapter;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        scheduleTabs = view.findViewById(R.id.schedule_tabs);
        scheduleViewPager = view.findViewById(R.id.schedule_viewpager);
        fabHistory = view.findViewById(R.id.fab_history);
        
        setupTabLayout();
        
        setupFAB();
    }
    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState saved instance state
     * @return inflated view component
     */
    
    private void setupTabLayout() {
        pagerAdapter = new SchedulePagerAdapter(this);
        scheduleViewPager.setAdapter(pagerAdapter);
        
        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(scheduleTabs, scheduleViewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Scheduled");
                            break;
    /**
     * Called immediately after onCreateView has returned. Initialize views here.
     *
     * @param view view
     * @param savedInstanceState saved instance state
     */
                        case 1:
                            tab.setText("Completed");
                            break;
                        case 2:
                            tab.setText("Cancelled");
                            break;
                    }
                }
        ).attach();
    }
    
    private void setupFAB() {
        fabHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AppointmentRequestsActivity.class);
            startActivity(intent);
        });
    }
    /**
     * Configures and prepares tab layout in appointment scheduling and management
     */
}

    /**
     * Configures and prepares fab in appointment scheduling and management
     */