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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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
    private ArrayList<List> finalList = new ArrayList<>();
    private boolean isMetric = true;
    private java.util.List candidateTimes = Arrays.asList("03:00:00", "09:00:00", "15:00:00", "21:00:00");

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
        mSharedPrefs = getActivity().getSharedPreferences(SettingsFragment.PREFERENCE_FILE, Context.MODE_PRIVATE);
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
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
        isMetric = mSharedPrefs.getBoolean(getString(R.string.unit_preference_key), true);
        String cityZipCode = mSharedPrefs.getString(getString(R.string.city_zip_code_key), "02128");
        callAPI(cityZipCode);
    }

    private void callAPI(String zipCode) {
        fiveDayData = weatherAPI.getFiveDaysForecast(getContext(), zipCode);
    }

    private void configureViews() {
        mExpandableListView = mFragmentView.findViewById(R.id.forecast_list);
        if (fiveDayData != null && weatherAPI != null) {
            TextView cityNameHeader = mFragmentView.findViewById(R.id.cityNameHeader);
            cityNameHeader.setText(weatherAPI.getCityName());
            java.util.List<String> dayNames = weatherAPI.filterForecastDays();
            Map<String, java.util.List<List>> timeBasedWeatherData = weatherAPI.getTimeBasedWeatherInfo();
            mForecastDataAdapter = new ForecastDataAdapter(getContext(), dayNames, timeBasedWeatherData);
            mExpandableListView.setOnGroupClickListener(this);
            mExpandableListView.setAdapter(mForecastDataAdapter);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.city_zip_code_key))) {
            String zipCode = sharedPreferences.getString(s, "02128");
            callAPI(zipCode);
        } else if (s.equals(getString(R.string.unit_preference_key))) {
            isMetric = sharedPreferences.getBoolean(s, false);
            configureViews();
        }
    }

    @Override
    public void onResponseSuccess() {
        configureViews();
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
        final Object group = mForecastDataAdapter.getGroup(groupPosition);
        return false;
    }
}