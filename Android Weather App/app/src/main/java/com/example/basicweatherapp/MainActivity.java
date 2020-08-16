package com.example.basicweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText cityName;
    private EditText zipcode;
    private Button buttonSubmit;
    private TextView dataTV;
    private WeatherAPI weatherAPI;
    private ProgressBar progressBar;
    private LinearLayout weatherView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataTV = findViewById(R.id.weatherData);
        progressBar = findViewById(R.id.progress_bar);
        zipcode = findViewById(R.id.zipCodeText);
        weatherView = findViewById(R.id.weather_view);
        weatherView.setVisibility(View.GONE);
        weatherAPI = WeatherAPI.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonSubmit = findViewById(R.id.submitButton);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> data;
                if (validateZipCode()) {
                    data = weatherAPI.getWeatherData(getApplicationContext(), zipcode.getText().toString());
                } else {
                    displayError();
                    weatherView.setVisibility(View.GONE);
                    return;
                }
                if (data != null && !data.isEmpty()) {
                    dataTV.setVisibility(View.GONE);
                    configureViews(data);
                } else {
                    String s = "Failed to fetch data...";
                    dataTV.setVisibility(View.VISIBLE);
                    dataTV.setText(s);
                }
            }
        });
    }

    private boolean validateZipCode() {
        return zipcode.getText().toString().length() == 5;
    }

    private void displayError() {
        Toast.makeText(this, "Invalid ZipCode..", Toast.LENGTH_LONG).show();
    }

    private void configureViews(final Map<String, String> data) {
        TextView cityName = findViewById(R.id.city_name);
        TextView maxTemp = findViewById(R.id.max_temperature_value);
        TextView minTemp = findViewById(R.id.min_temp_value);
        TextView humidity = findViewById(R.id.humidity_value);
        TextView windSpeed = findViewById(R.id.wind_value);
        String icon = data.get("icon");
        weatherView.setVisibility(View.VISIBLE);

        cityName.setText(data.get("cityName"));
        maxTemp.setText(data.get("maxTemp"));
        minTemp.setText(data.get("minTemp"));
        humidity.setText(data.get("humidity"));
        windSpeed.setText(data.get("windSpeed"));
        getImageView(icon);
    }

    private void getImageView(String icon) {
        ImageView imageView = findViewById(R.id.weather_logo);
        String url = "http://openweathermap.org/img/wn/" + icon + "@2x.png";
        Picasso.get().load(url).into(imageView);
    }
}
