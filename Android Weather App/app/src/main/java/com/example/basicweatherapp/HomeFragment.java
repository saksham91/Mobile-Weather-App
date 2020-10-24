package com.example.basicweatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.basicweatherapp.util.UnitsFormatter;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment implements WeatherResponseListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView dataTV;
    private WeatherAPI weatherAPI;
    private LinearLayout weatherView;
    private Map<String, String> data;
    private TextView currentTemp;
    private TextView currentTime;
    private TextView todayDate;
    private TextView cityName;
    private TextView feelsLike;
    private TextView weatherCondition;
    private TextView humidity;
    private TextView windSpeed;
    private View mFragmentView;
    public static boolean isMetric = true;
    private SharedPreferences mSharedPrefs;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherAPI = WeatherAPI.getInstance();
        weatherAPI.subscribeToWeatherResponseData(this);
        mSharedPrefs = Objects.requireNonNull(getActivity()).getSharedPreferences(SettingsFragment.PREFERENCE_FILE, Context.MODE_PRIVATE);
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
        isMetric = mSharedPrefs.getBoolean(getString(R.string.unit_preference_key), true);
        String cityZipCode = mSharedPrefs.getString(getString(R.string.city_zip_code_key), "02128");
        callAPI(cityZipCode);
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
        currentTemp = mFragmentView.findViewById(R.id.current_temp);
        currentTime = mFragmentView.findViewById(R.id.current_time);
        todayDate = mFragmentView.findViewById(R.id.current_date);
        feelsLike = mFragmentView.findViewById(R.id.feels_like_value);
        humidity = mFragmentView.findViewById(R.id.humidity_value);
        windSpeed = mFragmentView.findViewById(R.id.wind_value);
        weatherCondition = mFragmentView.findViewById(R.id.condition_value);
        dataTV = mFragmentView.findViewById(R.id.weatherData);
        weatherView = mFragmentView.findViewById(R.id.weather_view);
        weatherView.setVisibility(View.GONE);
    }

    private void callAPI(String zipCode) {
        data = weatherAPI.getWeatherData(getContext(), zipCode);
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

        DateTime dt = new DateTime();
        todayDate.setText(getFormattedDate(dt));
        currentTime.setText(getFormattedTime(dt));

        String icon = data.get("icon");
        weatherView.setVisibility(View.VISIBLE);
        cityName.setText(data.get("cityName"));
        weatherCondition.setText(capitalizeFirstAlphabet(data.get("condition")));
        humidity.setText(data.get("humidity") + " %");

        if (isMetric) {
            feelsLike.setText(String.format(getResources().getString(R.string.temp_in_celsius), data.get("feelsLike")));
            currentTemp.setText(String.format(getResources().getString(R.string.temp_in_celsius),data.get("currentTemp")));
            //minTemp.setText(String.format(getResources().getString(R.string.temp_in_celsius), data.get("minTemp")));
            windSpeed.setText(data.get("windSpeed") + " kph");
        } else {
            feelsLike.setText(String.format(getResources().getString(R.string.temp_in_fahrenheit), String.valueOf(UnitsFormatter.convertToImperial(data.get("feelsLike"), true))));
            currentTemp.setText(String.format(getResources().getString(R.string.temp_in_fahrenheit),String.valueOf(UnitsFormatter.convertToImperial(data.get("currentTemp"), true))));
            //minTemp.setText(String.format(getResources().getString(R.string.temp_in_fahrenheit), String.valueOf(UnitsFormatter.convertToImperial(data.get("minTemp"), true))));
            windSpeed.setText(UnitsFormatter.convertToImperial(data.get("windSpeed"), false) + " mph");
        }
        getImageView(icon);
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

        } else if (s.equals(getString(R.string.city_zip_code_key))) {
            String zipCode = sharedPreferences.getString(s, "02128");
            callAPI(zipCode);
        }
    }

    private String getFormattedDate(DateTime dateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        return formatter.format(dateTime.toDate());
    }

    private String getFormattedTime(DateTime dateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return formatter.format(dateTime.toDate());
    }

    private String capitalizeFirstAlphabet(String str) {
        if (str == null || str.length() == 0) return "";

        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}