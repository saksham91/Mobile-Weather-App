package com.example.basicweatherapp;

import com.example.basicweatherapp.models.Clouds;
import com.example.basicweatherapp.models.Coord;
import com.example.basicweatherapp.models.Main;
import com.example.basicweatherapp.models.SysWeather;
import com.example.basicweatherapp.models.Weather;
import com.example.basicweatherapp.models.Wind;

import java.util.ArrayList;

public class WeatherData {

    public Coord coord;
    public ArrayList<Weather> weather;
    public String base;
    public Main main;
    public String visibility;
    public Wind wind;
    public Clouds clouds;
    public String dt;
    public SysWeather sys;
    public String id;
    public String name;
    public String cod;

}
