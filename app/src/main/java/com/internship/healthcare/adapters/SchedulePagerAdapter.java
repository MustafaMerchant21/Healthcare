package com.internship.healthcare.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.internship.healthcare.fragments.ScheduledAppointmentsFragment;
import com.internship.healthcare.fragments.CompletedAppointmentsFragment;
import com.internship.healthcare.fragments.CancelledAppointmentsFragment;
/**
 * SchedulePagerAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class SchedulePagerAdapter extends FragmentStateAdapter {

    public SchedulePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public SchedulePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ScheduledAppointmentsFragment(); // Scheduled/Upcoming appointments
            case 1:
                return new CompletedAppointmentsFragment(); // Completed appointments
            case 2:
                return new CancelledAppointmentsFragment(); // Cancelled/Rejected appointments
            default:
                return new ScheduledAppointmentsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Three tabs
    }
}