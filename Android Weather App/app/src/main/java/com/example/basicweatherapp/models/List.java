package com.example.basicweatherapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class List {
    @SerializedName("dt")
    @Expose
    public Integer dt;
    @SerializedName("main")
    @Expose
    public Main main;
    @SerializedName("weather")
    @Expose
    public java.util.List<Weather> weather = null;
    @SerializedName("clouds")
    @Expose
    public Clouds clouds;
    @SerializedName("wind")
    @Expose
    public Wind wind;
    @SerializedName("visibility")
    @Expose
    public Integer visibility;
    @SerializedName("pop")
    @Expose
    public Double pop;
    @SerializedName("rain")
    @Expose
    public Rain rain;
    @SerializedName("sys")
    @Expose
    public Sys sys;
    @SerializedName("dt_txt")
    @Expose
    public String dtTxt;

    @Override
    public String toString() {
        return "List{" +
                "dt=" + dt +
                ", main=" + main +
                ", weather=" + weather +
                ", clouds=" + clouds +
                ", wind=" + wind +
                ", visibility=" + visibility +
                ", pop=" + pop +
                ", rain=" + rain +
                ", sys=" + sys +
                ", dtTxt='" + dtTxt + '\'' +
                '}';
    }
}
