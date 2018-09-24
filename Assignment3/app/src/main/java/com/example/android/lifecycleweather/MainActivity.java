package com.example.android.lifecycleweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.lifecycleweather.data.WeatherPreferences;
import com.example.android.lifecycleweather.utils.OpenWeatherMapUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ForecastAdapter.OnForecastItemClickListener,
        android.support.v4.app.LoaderManager.LoaderCallbacks<String>{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String SEARCH_URL_KEY = "weatherSearchURL";
    private static final int WEATHER_SEARCH_LOADER_ID = 0;


    private TextView mForecastLocationTV;
    private RecyclerView mForecastItemsRV;
    private ProgressBar mLoadingIndicatorPB;
    private TextView mLoadingErrorMessageTV;
    private ForecastAdapter mForecastAdapter;

    SharedPreferences sharedPreferences;

    String mUnits;
    String mLocation;

    SharedPreferences.OnSharedPreferenceChangeListener mPreferenceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        mUnits = sharedPreferences.getString(
                getString(R.string.pref_weather_units_key),
                WeatherPreferences.getDefaultTemperatureUnits());

        mLocation = sharedPreferences.getString(
                getString(R.string.pref_weather_location_key),
                WeatherPreferences.getDefaultForecastLocation());

        WeatherPreferences.setSharedPreferences(sharedPreferences);
        WeatherPreferences.setUnits(mUnits);
        WeatherPreferences.setLocation(mLocation);

        mPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                loadForecastReset(sharedPreferences);
            }
        };

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mPreferenceListener);

        setContentView(R.layout.activity_main);

        // Remove shadow under action bar.
        getSupportActionBar().setElevation(0);

        mForecastLocationTV = (TextView)findViewById(R.id.tv_forecast_location);
        mForecastLocationTV.setText(WeatherPreferences.getLocation());

        mLoadingIndicatorPB = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessageTV = (TextView)findViewById(R.id.tv_loading_error_message);
        mForecastItemsRV = (RecyclerView)findViewById(R.id.rv_forecast_items);

        mForecastAdapter = new ForecastAdapter(this);
        mForecastItemsRV.setAdapter(mForecastAdapter);
        mForecastItemsRV.setLayoutManager(new LinearLayoutManager(this));
        mForecastItemsRV.setHasFixedSize(true);

        loadForecast();
    }

    @Override
    public void onForecastItemClick(OpenWeatherMapUtils.ForecastItem forecastItem) {
        Intent intent = new Intent(this, ForecastItemDetailActivity.class);
        intent.putExtra(OpenWeatherMapUtils.ForecastItem.EXTRA_FORECAST_ITEM, forecastItem);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                showForecastLocation();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadForecast() {

        Log.d(TAG, "LOG: In loadForecast()");

        String openWeatherMapForecastURL = OpenWeatherMapUtils
                .buildForecastURL(WeatherPreferences.getLocation(),
                        WeatherPreferences.getUnits());

        Bundle loaderArgs = new Bundle();
        loaderArgs.putString(SEARCH_URL_KEY, openWeatherMapForecastURL);
        mLoadingIndicatorPB.setVisibility(View.VISIBLE);


        if(!getSupportLoaderManager().hasRunningLoaders()) {
                Log.d(TAG, "LOG: Calling initLoader");
                getSupportLoaderManager().initLoader(WEATHER_SEARCH_LOADER_ID, loaderArgs, this);
        } else { //Otherwise, restart loader
            Log.d(TAG, "LOG: Calling restartLoader");
            getSupportLoaderManager().restartLoader(WEATHER_SEARCH_LOADER_ID, loaderArgs, this);

        }
    }

    public void loadForecastReset(SharedPreferences sharedPreferences) {

        Log.d(TAG, "LOG: In loadForecastReset()");

        mLocation = sharedPreferences.getString(
                getString(R.string.pref_weather_location_key),
                WeatherPreferences.getDefaultForecastLocation());

        mUnits = sharedPreferences.getString(
                getString(R.string.pref_weather_units_key),
                WeatherPreferences.getDefaultTemperatureUnits());

        WeatherPreferences.setUnits(mUnits);
        WeatherPreferences.setLocation(mLocation);

        String openWeatherMapForecastURL = OpenWeatherMapUtils
                .buildForecastURL(WeatherPreferences.getLocation(),
                        WeatherPreferences.getUnits());

        Bundle loaderArgs = new Bundle();
        loaderArgs.putString(SEARCH_URL_KEY, openWeatherMapForecastURL);
        mLoadingIndicatorPB.setVisibility(View.VISIBLE);
        mForecastLocationTV.setText(WeatherPreferences.getLocation());
        Log.d(TAG, "LOG: Calling restartLoader");
        getSupportLoaderManager().restartLoader(WEATHER_SEARCH_LOADER_ID, loaderArgs, this);
    }

    public void showForecastLocation() {
        Uri geoUri = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", WeatherPreferences.getLocation())
                .build();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        String weatherSearchURL = null;
        if(args != null) {
            weatherSearchURL = args.getString(SEARCH_URL_KEY);
        }
        //Create Loader object, pass URL to constructor
        return new WeatherSearchLoader(this, weatherSearchURL);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String forecastJSON) {
        Log.d(TAG, "LOG: Grabbing cached results\n");
        mLoadingIndicatorPB.setVisibility(View.INVISIBLE);
        if (forecastJSON != null) {
            mLoadingErrorMessageTV.setVisibility(View.INVISIBLE);
            mForecastItemsRV.setVisibility(View.VISIBLE);
            ArrayList<OpenWeatherMapUtils.ForecastItem> forecastItems = OpenWeatherMapUtils.parseForecastJSON(forecastJSON);
            mForecastAdapter.updateForecastItems(forecastItems);
        } else {
            mForecastItemsRV.setVisibility(View.INVISIBLE);
            mLoadingErrorMessageTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        //Empty
    }
}
