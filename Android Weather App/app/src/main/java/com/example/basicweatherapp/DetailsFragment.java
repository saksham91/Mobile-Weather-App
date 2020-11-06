package com.example.basicweatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.basicweatherapp.adapters.ForecastDataAdapter;
import com.example.basicweatherapp.models.FiveDayData;
import com.example.basicweatherapp.models.List;
import com.example.basicweatherapp.util.UnitsFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment implements WeatherResponseListener, SharedPreferences.OnSharedPreferenceChangeListener, ExpandableListView.OnGroupClickListener {

    private WeatherAPI weatherAPI;
    private SharedPreferences mSharedPrefs;
    private FiveDayData fiveDayData;
    private View mFragmentView;
    private ExpandableListView mExpandableListView;
    private ForecastDataAdapter mForecastDataAdapter;
    private boolean isMetric = true;
    private String latitude;
    private String longitude;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherAPI = WeatherAPI.getInstance();
        weatherAPI.subscribeToWeatherResponseData(this);
        mSharedPrefs = Objects.requireNonNull(getActivity()).getSharedPreferences(SettingsFragment.PREFERENCE_FILE, Context.MODE_PRIVATE);
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
        isMetric = mSharedPrefs.getBoolean(getString(R.string.unit_preference_key), true);
        latitude = mSharedPrefs.getString(getString(R.string.city_latitude_key), "40.7128");
        longitude = mSharedPrefs.getString(getString(R.string.city_longitude_key), "74.0060");
        callAPILatLng(latitude, longitude, getUnits());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_details, container, false);
        return mFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        configureViews();
    }

    private void callAPILatLng(final String lat, final String lon, final String units) {
        fiveDayData = weatherAPI.getFiveDaysForecast(getContext(), lat, lon, units);
    }

    private void configureViews() {
        mExpandableListView = mFragmentView.findViewById(R.id.forecast_list);

        if (fiveDayData != null && weatherAPI != null) {
            TextView cityNameHeader = mFragmentView.findViewById(R.id.cityNameHeader);
            cityNameHeader.setText(UnitsFormatter.extractCityName(weatherAPI.getCityName()));
            java.util.List<String> dayNames = weatherAPI.filterForecastDays();
            Map<String, java.util.List<List>> timeBasedWeatherData = weatherAPI.getTimeBasedWeatherInfo();
            mForecastDataAdapter = new ForecastDataAdapter(getContext(), dayNames, timeBasedWeatherData, isMetric);
            mExpandableListView.setOnGroupClickListener(this);
            mExpandableListView.setAdapter(mForecastDataAdapter);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.unit_preference_key))) {
            isMetric = sharedPreferences.getBoolean(s, false);
            callAPILatLng(latitude, longitude, getUnits());
        } else if (s.equals(getString(R.string.city_latitude_key)) || s.equals(getString(R.string.city_longitude_key))) {
            latitude = sharedPreferences.getString(getString(R.string.city_latitude_key), "");
            longitude = sharedPreferences.getString(getString(R.string.city_longitude_key), "");
            callAPILatLng(latitude, longitude, getUnits());
        }
    }

    private String getUnits() {
        return isMetric ? "metric" : "imperial";
    }

    @Override
    public void onResponseSuccess() {
        fiveDayData = weatherAPI.getFiveDayData();
        configureViews();
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
        final Object group = mForecastDataAdapter.getGroup(groupPosition);
        return false;
    }
}