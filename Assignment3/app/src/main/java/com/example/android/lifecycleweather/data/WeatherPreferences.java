package com.example.android.lifecycleweather.data;

import android.content.SharedPreferences;

/**
 * Created by hessro on 5/10/17.
 */

public class WeatherPreferences {

    private static final String DEFAULT_FORECAST_LOCATION = "Corvallis, OR, US";
    private static final String DEFAULT_TEMPERATURE_UNITS = "imperial";
    private static final String DEFAULT_TEMPERATURE_UNITS_ABBR = "F";
    private static final String DEFAULT_DATE_FORMAT = "EEE, MMM d, h:mm a";

    static SharedPreferences sharedPreferences;

    static String Units;
    static String Location;

    public static void setSharedPreferences(SharedPreferences SP) {
        sharedPreferences = SP;
    }

    public static void setUnits(String units) {
        Units = units;
    }

    public static void setLocation(String location) {
        Location = location;
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static String getUnits() {
        return Units;
    }

    public static String getLocation() {
        return Location;
    }

    public static String getDefaultForecastLocation() {
        return DEFAULT_FORECAST_LOCATION;
    }

    public static String getAbbreviation() {
        String units;
        switch (Units) {
            case "imperial":
                units = "F";
                break;
            case "metric":
                units = "C";
                break;
            case "kelvin":
                units = "K";
                break;
            default:
                units = " error";
                break;
        }
        return units;
    }

    public static String getDefaultTemperatureUnits() {
        return DEFAULT_TEMPERATURE_UNITS;
    }

    public static String getDefaultTemperatureUnitsAbbr() {
        return DEFAULT_TEMPERATURE_UNITS_ABBR;
    }

    public static String getDefaultDateFormat() {
        return DEFAULT_DATE_FORMAT;
    }
}
