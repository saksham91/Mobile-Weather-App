package com.example.basicweatherapp;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements WeatherResponseListener {

    private EditText name;
    private EditText zipcode;
    private Button buttonSubmit;
    private TextView dataTV;
    private WeatherAPI weatherAPI;
    private ProgressBar progressBar;
    private LinearLayout weatherView;
    private boolean isMetric = true;
    private Map<String, String> data;
    private TextView cityName;
    private TextView maxTemp;
    private TextView minTemp;
    private TextView humidity;
    private TextView windSpeed;
    private Switch switchUnit;
    private static final Double kphToMph = 0.621371;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        weatherAPI = WeatherAPI.getInstance();
        weatherAPI.subscribeToWeatherResponseData(this);
    }

    private void setupViews() {
        cityName = findViewById(R.id.city_name);
        maxTemp = findViewById(R.id.max_temperature_value);
        minTemp = findViewById(R.id.min_temp_value);
        humidity = findViewById(R.id.humidity_value);
        windSpeed = findViewById(R.id.wind_value);
        dataTV = findViewById(R.id.weatherData);
        progressBar = findViewById(R.id.progress_bar);
        zipcode = findViewById(R.id.zipCodeText);
        weatherView = findViewById(R.id.weather_view);
        weatherView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonSubmit = findViewById(R.id.submitButton);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateZipCode()) {
                    data = weatherAPI.getWeatherData(getApplicationContext(), zipcode.getText().toString());
                } else {
                    displayError();
                    weatherView.setVisibility(View.GONE);
                    return;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setActionView(R.layout.toolbar_switch);

        switchUnit = item.getActionView().findViewById(R.id.unit_switch);
        switchUnit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isMetric = isChecked;
                switchUnit.setText(isMetric ? R.string.unit_metric : R.string.unit_imperial);
                if (data != null) {
                    configureViews();
                }
            }
        });
        return true;
    }

    private boolean validateZipCode() {
        return zipcode.getText().toString().length() == 5;
    }

    private void displayError() {
        Toast.makeText(this, "Invalid ZipCode", Toast.LENGTH_LONG).show();
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
        ImageView imageView = findViewById(R.id.weather_logo);
        String url = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
        Picasso.get().load(url).into(imageView);
    }

    @Override
    public void onResponseSuccess() {
        configureViews();
    }
}
