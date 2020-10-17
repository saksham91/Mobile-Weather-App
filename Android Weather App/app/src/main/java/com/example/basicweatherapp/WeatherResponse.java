package com.example.basicweatherapp;

import androidx.annotation.NonNull;

import com.example.basicweatherapp.models.ForecastData;

import java.util.ArrayList;
import java.util.Arrays;

public class WeatherResponse {

    public String cod;
    public String message;
    public String cnt;
    public ArrayList<ForecastData> list;

    @NonNull
    @Override
    public String toString() {
        return String.format("{cod='%s', message='%s', cnt='%s', list=['%s']}", cod, message, cnt, Arrays.toString(list.toArray()));
    }
}
