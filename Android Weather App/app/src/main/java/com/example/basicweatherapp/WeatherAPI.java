package com.example.basicweatherapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.basicweatherapp.models.FiveDayData;
import com.example.basicweatherapp.models.ForecastData;
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
    private final String appId = "8e845eb9a26e423f0d730d3c4759a46d";
    private final String zipCode = "10001";
    private Retrofit mRetrofit;
    private ArrayList<WeatherResponseListener> mListener;
    private Map<String, String> currentWeatherData = new HashMap<>();
    private FiveDayData fiveDayData;

    private WeatherAPI() {
        mGson = new Gson();
        mListener = new ArrayList<>();
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
        return currentWeatherData;
    }

    public FiveDayData getFiveDaysForecast(final Context context, String zipCode) {
        if (mRetrofit == null) { initRetrofit(); }
        WeatherService weatherService = mRetrofit.create(WeatherService.class);
        Call<FiveDayData> call = weatherService.getWeatherForecastByZipCode(zipCode, appId);
        call.enqueue(new Callback<FiveDayData>() {
            @Override
            public void onResponse(Call<FiveDayData> call, Response<FiveDayData> response) {
                if (response.code() == 200) {
                    fiveDayData = response.body();
                    assert fiveDayData != null;
                    notifySubscriber();
                }
            }

            @Override
            public void onFailure(Call<FiveDayData> call, Throwable t) {
                Log.d(TAG, "getWeatherForecastByZipCode error: ");
            }
        });
        return fiveDayData;
    }

    private void initRetrofit() {
            mRetrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

    private void handleCurrentWeatherData(@NonNull WeatherData weatherData) {
        String icon = null;
        Main tempData = weatherData.main;
        Wind windData = weatherData.wind;
        ArrayList<Weather> weathers = weatherData.weather;
        if (!weathers.isEmpty()) {
            icon = weathers.get(0).icon;
        }
        currentWeatherData.put("cityName", weatherData.name);
        currentWeatherData.put("currentTemp", String.valueOf(convertToTempScale(tempData.temp)));
        currentWeatherData.put("minTemp", String.valueOf(convertToTempScale(tempData.temp_min)));
        currentWeatherData.put("maxTemp", String.valueOf(convertToTempScale(tempData.temp_max)));
        currentWeatherData.put("humidity", tempData.humidity);
        currentWeatherData.put("windSpeed", windData.speed);
        currentWeatherData.put("icon", icon);
    }

    private int convertToTempScale(String temp) {
        return (int) Math.round(Double.parseDouble(temp) - 273.15);
    }

    public void subscribeToWeatherResponseData(WeatherResponseListener listener) {
        mListener.add(listener);
    }

    private void notifySubscriber() {
        if (mListener == null) return;

        for(WeatherResponseListener listener: mListener) {
            listener.onResponseSuccess();
        }
    }

    private void handleWeatherData(ArrayList<ForecastData> dataList) {
        for (ForecastData data : dataList) {

        }
    }

}
