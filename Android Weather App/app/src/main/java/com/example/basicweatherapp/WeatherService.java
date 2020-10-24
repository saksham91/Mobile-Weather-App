package com.example.basicweatherapp;

import com.example.basicweatherapp.models.FiveDayData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather?")
    Call<WeatherData> getWeatherDataByName(@Query("name") String cityName, @Query("appid") String appId);

    @GET("data/2.5/forecast?")
    Call<WeatherResponse> getWeatherForecastByName(@Query("name") String cityName, @Query("appid") String appId);

    @GET("data/2.5/weather?")
    Call<WeatherData> getWeatherDataByZipCode(@Query("zip") String zipCode, @Query("appid") String appId);

    @GET("data/2.5/forecast?")
    Call<FiveDayData> getWeatherForecastByZipCode(@Query("zip") String zipCode, @Query("appid") String appId, @Query("units") String units);

    @GET("data/2.5/weather?")
    Call<WeatherData> getWeatherDataCurrentLocation(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String appId);

    @GET("data/2.5/forecast?")
    Call<WeatherResponse> getWeatherForecastCurrentLocation(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String appId);
}
