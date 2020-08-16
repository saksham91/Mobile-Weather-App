package com.example.basicweatherapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.basicweatherapp.models.Main;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherAPI {

    private static final String TAG = "API";
    private static WeatherAPI mWeatherAPI;
    private Gson mGson;
    private final String baseURL = "http://api.openweathermap.org/";
    private final String appId = "APIKEY";
    private final String zipCode = "02128";
    private Retrofit mRetrofit;
    private Map<String, Integer> values = new HashMap<>();

    private WeatherAPI() {
        mGson = new Gson();
    }

    public static WeatherAPI getInstance() {
        if (mWeatherAPI == null) {
            mWeatherAPI = new WeatherAPI();
        }
        return mWeatherAPI;
    }

    public Map<String, Integer> getWeatherData(final Context context) {
        if (mRetrofit == null) { initRetrofit(); }
        WeatherService weatherService = mRetrofit.create(WeatherService.class);
        Call<WeatherData> call = weatherService.getWeatherDataByZipCode(zipCode, appId);
        call.enqueue(new Callback<WeatherData>() {

            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.code() == 200) {
                    WeatherData weatherData = response.body();
                    assert weatherData != null;
                    handleCurrentWeatherData(weatherData);
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
        return values;
    }

    private void initRetrofit() {
            mRetrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

    private Map<String, Integer> handleCurrentWeatherData(@NonNull WeatherData weatherData) {
        Main tempData = weatherData.main;
        values.put("currentTemp", convertToTempScale(tempData.temp));
        values.put("minTemp", convertToTempScale(tempData.temp_min));
        values.put("maxTemp", convertToTempScale(tempData.temp_max));
        return values;
    }

    private int convertToTempScale(String temp) {
        return (int) Math.round(Double.valueOf(temp) - 273.15);
    }

    private void handleWeatherData(ArrayList<ForecastData> dataList) {
        for (ForecastData data : dataList) {

        }
    }

}
