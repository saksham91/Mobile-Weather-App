package com.example.basicweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static boolean isMetric = true;
    public static final Double kphToMph = 0.621371;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
    }

    private void setupViews() {
        TabLayout tabLayout = findViewById(R.id.tab_bar);
        TabItem homeTab = findViewById(R.id.tab_home);
        TabItem detailsTab = findViewById(R.id.tab_detail);
        TabItem settingsTab = findViewById(R.id.tab_settings);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter(this));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setIcon(R.drawable.ic_baseline_home_24);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_calendar_24);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_baseline_settings_24);
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
