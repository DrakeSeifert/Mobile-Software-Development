package com.example.android.lifecycleweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.lifecycleweather.utils.NetworkUtils;

import java.io.IOException;

/**
 * Created by Drake on 2/25/2018.
 */

public class WeatherSearchLoader extends AsyncTaskLoader<String> {

    private final static String TAG = WeatherSearchLoader.class.getSimpleName();

    String mSearchResultsJSON;
    private String mWeatherSearchURL;

    public WeatherSearchLoader(Context context, String url) {
        super(context);
        mWeatherSearchURL = url;
    }

    @Override
    protected void onStartLoading() {
        if(mWeatherSearchURL != null) {
            if(mSearchResultsJSON != null) {
                Log.d(TAG, "LOG: returning cached results");
                deliverResult(mSearchResultsJSON);
            } else {
                forceLoad();
            }
        }
    }

    @Override
    public String loadInBackground() {
        if(mWeatherSearchURL != null) {
            Log.d(TAG, "LOG: loading results from API with URL: " + mWeatherSearchURL);
            String searchResults = null;
            try {
                searchResults = NetworkUtils.doHTTPGet(mWeatherSearchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        } else {
            return null;
        }
        /*if(mWeatherSearchURL != null) {
            String openWeatherMapURL = params[0];
            String forecastJSON = null;
            try {
                forecastJSON = NetworkUtils.doHTTPGet(openWeatherMapURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return forecastJSON
        } else {
            return null;
        }*/
    }

    @Override
    public void deliverResult(String data) {
        mSearchResultsJSON = data;
        super.deliverResult(data);
    }
}
