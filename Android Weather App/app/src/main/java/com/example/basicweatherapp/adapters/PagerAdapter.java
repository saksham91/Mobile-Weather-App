package com.example.basicweatherapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.basicweatherapp.DetailsFragment;
import com.example.basicweatherapp.HomeFragment;
import com.example.basicweatherapp.SettingsFragment;

public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new DetailsFragment();
            case 2:
                return  new SettingsFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
