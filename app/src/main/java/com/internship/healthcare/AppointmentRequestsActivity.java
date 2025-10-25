package com.internship.healthcare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.internship.healthcare.fragments.AppointmentRequestsFragment;
/**
 * AppointmentRequestsActivity.java
 * A comprehensive healthcare management Android application
 * Activity handling appointment requests screen and user interactions.
 *
 * <p>Extends: {@link AppCompatActivity}</p>
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class AppointmentRequestsActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appointment_requests);

        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        AppointmentPagerAdapter pagerAdapter = new AppointmentPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Pending");
                    break;
                case 1:
                    tab.setText("Approved");
                    break;
                case 2:
                    tab.setText("Rejected");
                    break;
            }
        }).attach();
    }

    
    private static class AppointmentPagerAdapter extends FragmentStateAdapter {

        public AppointmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return AppointmentRequestsFragment.newInstance("pending");
                case 1:
                    return AppointmentRequestsFragment.newInstance("approved");
                case 2:
                    return AppointmentRequestsFragment.newInstance("rejected");
                default:
                    return AppointmentRequestsFragment.newInstance("pending");
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Three tabs: Pending, Approved, Rejected
        }
    }
}

    
    