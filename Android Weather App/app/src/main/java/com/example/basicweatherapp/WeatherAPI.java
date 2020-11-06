package com.example.basicweatherapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.basicweatherapp.models.FiveDayData;
import com.example.basicweatherapp.models.ForecastData;
import com.example.basicweatherapp.models.Main;
import com.example.basicweatherapp.models.Weather;
import com.example.basicweatherapp.models.Wind;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private String appId;
    private final String zipCode = "10001";
    private Retrofit mRetrofit;
    private ArrayList<WeatherResponseListener> mListener;
    private List<String> dayNames;
    private Map<String, List<com.example.basicweatherapp.models.List>> timeBasedWeather;
    private Map<String, String> currentWeatherData = new HashMap<>();
    private FiveDayData fiveDayData;
    private List<String> candidateTimes = Arrays.asList("03:00:00", "09:00:00", "15:00:00", "21:00:00");

    private WeatherAPI() {
        mGson = new Gson();
        mListener = new ArrayList<>();
        dayNames = new ArrayList<>();
        timeBasedWeather = new HashMap<>();
        appId = BuildConfig.ApiKey;
    }

    public static WeatherAPI getInstance() {
        if (mWeatherAPI == null) {
            mWeatherAPI = new WeatherAPI();
        }
        return mWeatherAPI;
    }

    public Map<String, String> getWeatherData(final Context context, String lat, String lon) {
        if (mRetrofit == null) { initRetrofit(); }
        WeatherService weatherService = mRetrofit.create(WeatherService.class);
        Call<WeatherData> call = weatherService.getWeatherDataCurrentLocation(lat, lon, appId);
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

    public FiveDayData getFiveDaysForecast(final Context context, String lat, String lon, String units) {
        if (mRetrofit == null) { initRetrofit(); }
        WeatherService weatherService = mRetrofit.create(WeatherService.class);
        Call<FiveDayData> call = weatherService.getWeatherForecastCurrentLocation(lat, lon, appId, units);
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
        String condition = null;
        Main tempData = weatherData.main;
        Wind windData = weatherData.wind;
        ArrayList<Weather> weathers = weatherData.weather;
        if (!weathers.isEmpty()) {
            icon = weathers.get(0).icon;
            condition = weathers.get(0).description;
        }
        currentWeatherData.put("cityName", weatherData.name);
        currentWeatherData.put("currentTemp", String.valueOf(convertToTempScale(tempData.temp)));
        currentWeatherData.put("minTemp", String.valueOf(convertToTempScale(tempData.temp_min)));
        currentWeatherData.put("maxTemp", String.valueOf(convertToTempScale(tempData.temp_max)));
        currentWeatherData.put("feelsLike", String.valueOf(convertToTempScale(tempData.feels_like)));
        currentWeatherData.put("dateAsString", weatherData.dt);
        currentWeatherData.put("condition", condition);
        currentWeatherData.put("humidity", tempData.humidity);
        currentWeatherData.put("windSpeed", windData.speed);
        currentWeatherData.put("icon", icon);
    }


    public FiveDayData getFiveDayData() {
        return fiveDayData;
    }

    public String getCityName() {
        if (fiveDayData != null && dayNames != null) {
            return fiveDayData.city.name;
        }
        return "";
    }

    @Nullable
    public List<String> filterForecastDays() {
        if (fiveDayData != null) {
            for (com.example.basicweatherapp.models.List times : fiveDayData.list) {
                String[] time = times.dtTxt.split(" ");
                if (!dayNames.contains(time[0])) {
                    dayNames.add(time[0]);
                }
            }
        }
        return dayNames;
    }

    @Nullable
    public Map<String, List<com.example.basicweatherapp.models.List>> getTimeBasedWeatherInfo() {
        if (fiveDayData == null || dayNames == null || dayNames.size() == 0) return null;

        // list of List items filtered according to required time stamps (03:00, 09:00....)
        List<com.example.basicweatherapp.models.List> weatherList = new ArrayList<>();

        for (String dayName : dayNames) {
            List<com.example.basicweatherapp.models.List> tempList = new ArrayList<>();
            for (com.example.basicweatherapp.models.List listItem : fiveDayData.list) {
                String[] day = listItem.dtTxt.split(" ");
                if (candidateTimes.contains(day[1]) && dayName.equals(day[0])) {
                    tempList.add(listItem);
                }
            }

            if (tempList.size() >= 1) {
                timeBasedWeather.put(dayName, tempList);
            }
        }

        return timeBasedWeather;
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
}
