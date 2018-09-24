package com.example.android.connectedweather;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Drake on 2/10/2018.
 */

public class WeatherUtils {

    //example API call:
    //api.openweathermap.org/data/2.5/forecast?id=524901&APPID=1111111111

    final static String WEATHER_SEARCH_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";
    final static String WEATHER_SEARCH_ID_PARAM = "id";
    final static String WEATHER_SEARCH_ID = "5720727"; //Corvallis
    final static String WEATHER_SEARCH_APPID_PARAM = "APPID";
    final static String WEATHER_SEARCH_APPID = "9a99694a9decab956a5e4554b13f7d52";
    final static String WEATHER_SEARCH_UNITS_PARAM = "units";
    final static String WEATHER_SEARCH_UNITS = "imperial";

    public static final String EXTRA_SEARCH_RESULT = "WeatherUtils.SearchResult";

    public static class SearchResult implements Serializable {
        public String dateTime;
        public String temperature;
        public String description;

        //Detailed data
        public String humidity;
        public String windSpeed;
    }

    public static String buildWeatherSearchURL() {
        return Uri.parse(WEATHER_SEARCH_BASE_URL).buildUpon()
                .appendQueryParameter(WEATHER_SEARCH_ID_PARAM, WEATHER_SEARCH_ID)
                .appendQueryParameter(WEATHER_SEARCH_APPID_PARAM, WEATHER_SEARCH_APPID)
                .appendQueryParameter(WEATHER_SEARCH_UNITS_PARAM, WEATHER_SEARCH_UNITS)
                .build().toString();
    }

    public static ArrayList<SearchResult> parseSearchResultsJSON(String searchResultsJSON) {
        try {
            //Grab outer JSON element
            JSONObject json = new JSONObject(searchResultsJSON);
            JSONArray list = json.getJSONArray("list");
            int cnt = Integer.parseInt(json.getString("cnt"));

            System.out.println("*****cnt: " + cnt);

            //Create Array List to hold results
            ArrayList<SearchResult> searchResultsList = new ArrayList<SearchResult>();

            for(int i = 0; i < cnt; i++) {
                SearchResult result = new SearchResult();

                //Grab inner JSON elements
                JSONObject listIdx = list.getJSONObject(i);
                JSONObject main = listIdx.getJSONObject("main");

                JSONArray weather = listIdx.getJSONArray("weather");
                JSONObject weatherIdx = weather.getJSONObject(0);

                JSONObject wind = listIdx.getJSONObject("wind");

                //Add results to Array List
                result.dateTime = listIdx.getString("dt_txt");
                result.temperature = main.getString("temp");
                result.description = weatherIdx.getString("description");
                result.humidity = main.getString("humidity");
                result.windSpeed = wind.getString("speed");

                searchResultsList.add(result);
            }
            return searchResultsList;

        } catch (JSONException e) {
            return null;
        }
    }
}
