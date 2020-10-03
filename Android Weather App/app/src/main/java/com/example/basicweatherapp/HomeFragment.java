package com.example.basicweatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.basicweatherapp.MainActivity.isMetric;
import static com.example.basicweatherapp.MainActivity.kphToMph;

public class HomeFragment extends Fragment implements WeatherResponseListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private EditText name;
    private EditText zipcode;
    private Button buttonSubmit;
    private TextView dataTV;
    private WeatherAPI weatherAPI;
    private LinearLayout weatherView;
    private Map<String, String> data;
    private TextView cityName;
    private TextView maxTemp;
    private TextView minTemp;
    private TextView humidity;
    private TextView windSpeed;
    private View mFragmentView;
    private SharedPreferences mSharedPrefs;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        mFragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        setupViews();
        return mFragmentView;
    }

    private void setupViews() {
        cityName = mFragmentView.findViewById(R.id.city_name);
        maxTemp = mFragmentView.findViewById(R.id.max_temperature_value);
        minTemp = mFragmentView.findViewById(R.id.min_temp_value);
        humidity = mFragmentView.findViewById(R.id.humidity_value);
        windSpeed = mFragmentView.findViewById(R.id.wind_value);
        dataTV = mFragmentView.findViewById(R.id.weatherData);
        zipcode = mFragmentView.findViewById(R.id.zipCodeText);
        weatherView = mFragmentView.findViewById(R.id.weather_view);
        weatherView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        buttonSubmit = mFragmentView.findViewById(R.id.submitButton);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                if (validateZipCode()) {
                    data = weatherAPI.getWeatherData(getContext(), zipcode.getText().toString());
                } else {
                    displayError();
                    weatherView.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean validateZipCode() {
        return zipcode.getText().toString().length() == 5;
    }

    private void displayError() {
        Toast.makeText(getContext(), "Invalid ZipCode", Toast.LENGTH_LONG).show();
    }


    @SuppressLint("SetTextI18n")
    private void configureViews() {
        if (data != null && !data.isEmpty()) {
            dataTV.setVisibility(View.GONE);
        } else {
            String s = "Failed to fetch data...";
            dataTV.setVisibility(View.VISIBLE);
            dataTV.setText(s);
            return;
        }

        String icon = data.get("icon");
        weatherView.setVisibility(View.VISIBLE);
        cityName.setText(data.get("cityName"));
        humidity.setText(data.get("humidity") + " %");
        if (isMetric) {
            maxTemp.setText(String.format(getResources().getString(R.string.temp_in_celsius), data.get("maxTemp")));
            minTemp.setText(String.format(getResources().getString(R.string.temp_in_celsius), data.get("minTemp")));
            windSpeed.setText(data.get("windSpeed") + " kph");
        } else {
            maxTemp.setText(String.format(getResources().getString(R.string.temp_in_fahrenheit), String.valueOf(convertToImperial(data.get("maxTemp"), true))));
            minTemp.setText(String.format(getResources().getString(R.string.temp_in_fahrenheit), String.valueOf(convertToImperial(data.get("minTemp"), true))));
            windSpeed.setText(convertToImperial(data.get("windSpeed"), false) + " mph");
        }
        getImageView(icon);
    }


    private int convertToImperial(String metricValue, boolean isTemp) {
        if (!isTemp) {
            return (int) Math.round(Double.parseDouble(metricValue) * kphToMph);
        }
        double temperatureInCelsius = Double.parseDouble(metricValue);
        return (int) Math.round(temperatureInCelsius * 1.8 + 32);
    }

    private void getImageView(String icon) {
        ImageView imageView = mFragmentView.findViewById(R.id.weather_logo);
        String url = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
        Picasso.get().load(url).into(imageView);
    }

    @Override
    public void onResponseSuccess() {
        configureViews();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.unit_preference_key))) {
            isMetric = sharedPreferences.getBoolean(s, false);
            configureViews();
        } else if (s.equals(getString(R.string.time_preference_key))) {

        }
    }
}