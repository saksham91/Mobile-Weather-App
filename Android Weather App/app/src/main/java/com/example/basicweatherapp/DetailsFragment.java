package com.example.basicweatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.basicweatherapp.models.FiveDayData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment implements WeatherResponseListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private WeatherAPI weatherAPI;
    private SharedPreferences mSharedPrefs;
    private FiveDayData fiveDayData;
    private boolean isMetric = true;

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
        return inflater.inflate(R.layout.fragment_details, container, false);
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
        if (fiveDayData != null) {
            Log.d("DetailsFragment", "configureViews: " + fiveDayData.city.name);
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
}