package com.example.basicweatherapp;

import com.example.basicweatherapp.models.Clouds;
import com.example.basicweatherapp.models.Main;
import com.example.basicweatherapp.models.Rain;
import com.example.basicweatherapp.models.Snow;
import com.example.basicweatherapp.models.Sys;
import com.example.basicweatherapp.models.Weather;
import com.example.basicweatherapp.models.Wind;

import java.util.ArrayList;

public class ForecastData {

    public String dt;
    public Main main;
    public ArrayList<Weather> weather;
    public Clouds clouds;
    public Wind wind;
    public Rain rain;
    public Snow snow;
    public Sys sys;
    public String dt_txt;

}
