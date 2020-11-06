package com.example.basicweatherapp.util;

import static com.example.basicweatherapp.MainActivity.kphToMph;

public class UnitsFormatter {

    public static String getFormattedTime(String time, boolean isTwelveHourFormat) {
        switch (time) {
            case "03:00:00":
                return "3 AM";
            case "09:00:00":
                return "9 AM";
            case "15:00:00":
                return "3 PM";
            case "21:00:00":
                return "9 PM";
            default:
                return time;
        }
    }

    public static int convertToImperial(String metricValue, boolean isTemp) {
        if (!isTemp) {
            return (int) Math.round(Double.parseDouble(metricValue) * kphToMph);
        }
        double temperatureInCelsius = Double.parseDouble(metricValue);
        return (int) Math.round(temperatureInCelsius * 1.8 + 32);
    }

    public static String convertToImperialTemp(int metricValue) {
        return String.valueOf(Math.round(metricValue * 1.8 + 32));
    }

    public static String extractCityName(final String fullName) {
        String[] name = fullName.split(", ");
        if (name.length > 1) return name[1];
        return fullName;
    }
}
