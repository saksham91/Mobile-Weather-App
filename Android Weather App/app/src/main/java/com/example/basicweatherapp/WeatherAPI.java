package com.example.basicweatherapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.example.basicweatherapp.models.Main;
import com.example.basicweatherapp.models.Weather;
import com.example.basicweatherapp.models.Wind;
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
    private final String baseURL = "https://api.openweathermap.org/";
    private final String appId = "API";
    private final String zipCode = "10001";
    private Retrofit mRetrofit;
    private WeatherResponseListener mListener;
    private Map<String, String> values = new HashMap<>();

    private WeatherAPI() {
        mGson = new Gson();
        mListener = null;
    }

    public static WeatherAPI getInstance() {
        if (mWeatherAPI == null) {
            mWeatherAPI = new WeatherAPI();
        }
        return mWeatherAPI;
    }

    public Map<String, String> getWeatherData(final Context context, String zip) {
        if (mRetrofit == null) { initRetrofit(); }
        WeatherService weatherService = mRetrofit.create(WeatherService.class);
        Call<WeatherData> call = weatherService.getWeatherDataByZipCode(zip, appId);
        call.enqueue(new Callback<WeatherData>() {

            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.code() == 200) {
                    WeatherData weatherData = response.body();
                    assert weatherData != null;
                    handleCurrentWeatherData(weatherData);
                    notifySubscriber();
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

    private Map<String, String> handleCurrentWeatherData(@NonNull WeatherData weatherData) {
        String icon = null;
        Main tempData = weatherData.main;
        Wind windData = weatherData.wind;
        ArrayList<Weather> weathers = weatherData.weather;
        if (!weathers.isEmpty()) {
            icon = weathers.get(0).icon;
        }
        values.put("cityName", weatherData.name);
        values.put("currentTemp", String.valueOf(convertToTempScale(tempData.temp)));
        values.put("minTemp", String.valueOf(convertToTempScale(tempData.temp_min)));
        values.put("maxTemp", String.valueOf(convertToTempScale(tempData.temp_max)));
        values.put("humidity", tempData.humidity);
        values.put("windSpeed", windData.speed);
        values.put("icon", icon);
        return values;
    }

    private int convertToTempScale(String temp) {
        return (int) Math.round(Double.parseDouble(temp) - 273.15);
    }

    public void subscribeToWeatherResponseData(WeatherResponseListener listener) {
        mListener = listener;
    }

    private void notifySubscriber() {
        if (mListener == null) return;

        mListener.onResponseSuccess();
    }

    private void handleWeatherData(ArrayList<ForecastData> dataList) {
        for (ForecastData data : dataList) {

        }
    }

}
