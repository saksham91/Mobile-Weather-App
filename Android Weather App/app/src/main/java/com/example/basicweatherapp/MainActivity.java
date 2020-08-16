package com.example.basicweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText cityName;
    private EditText zipcode;
    private Button buttonSubmit;
    private TextView dataTV;
    private WeatherAPI weatherAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataTV = findViewById(R.id.weatherData);
        weatherAPI = WeatherAPI.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonSubmit = findViewById(R.id.submitButton);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sb;
                Map<String, Integer> data = weatherAPI.getWeatherData(getApplicationContext());
                if (data != null && !data.isEmpty()) {
                    sb = "Temperature: " + data.get("currentTemp") + "\n" +
                            "Min Temperature: " + data.get("minTemp") + "\n" +
                            "Max temperature: " + data.get("maxTemp");
                } else {
                    sb = "Failed to fetch data...";
                }
                dataTV.setText(sb);
            }
        });
    }
}
