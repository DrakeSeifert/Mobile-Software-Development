package com.example.android.connectedweather;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.OnForecastItemClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mForecastListRV;
    private ForecastAdapter mForecastAdapter;
    private Toast mToast;

    private ProgressBar mLoadingProgressBar;
    private TextView mLoadingErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingProgressBar = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessage = (TextView)findViewById(R.id.tv_loading_error);

        mForecastListRV = (RecyclerView)findViewById(R.id.rv_forecast_list);

        mForecastListRV.setLayoutManager(new LinearLayoutManager(this));
        mForecastListRV.setHasFixedSize(true);

        mForecastAdapter = new ForecastAdapter(this);
        mForecastListRV.setAdapter(mForecastAdapter);

        doWeatherSearch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void viewMap() {
        /*{
            "id": 5720727,
                "name": "Corvallis",
                "country": "US",
                "coord": {
            "lon": -123.262039,
                    "lat": 44.564571
        }*/

        //Request format:
        //https://tile.openweathermap.org/map/{layer}/{z}/{x}/{y}.png?appid={api_key}

        //String mapStr = "https://tile.openweathermap.org/map/clouds_new/10/44/-123.png?appid=9a99694a9decab956a5e4554b13f7d52";
        String mapStr = "http://openweathermap.org/weathermap?basemap=map&cities=false&layer=clouds&lat=44.5645&lon=-123.2620&zoom=10";
        Uri map = Uri.parse(mapStr);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, map);

        if(mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            System.out.println("****Error with map intent****");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                viewMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doWeatherSearch() {
        String weatherSearchURL = WeatherUtils.buildWeatherSearchURL();
        Log.d(TAG, "querying search URL: " + weatherSearchURL);
        new WeatherSearchTask().execute(weatherSearchURL);
    }

    public class WeatherSearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            String weatherSearchURL = urls[0];

            String searchResults = null;
            try {
                searchResults = NetworkUtils.doHttpGet(weatherSearchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            if(s != null) {
                ArrayList<WeatherUtils.SearchResult> searchResultsList =
                        WeatherUtils.parseSearchResultsJSON(s);
                mForecastAdapter.updateForecastData(searchResultsList);
                mLoadingErrorMessage.setVisibility(View.INVISIBLE);
                mForecastListRV.setVisibility(View.VISIBLE);
            } else {
                mForecastListRV.setVisibility(View.INVISIBLE);
                mLoadingErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onForecastItemClick(WeatherUtils.SearchResult detailedForecast) {
        Intent searchResultDetailActivityIntent =
                new Intent(this, SearchResultDetailActivity.class);
        searchResultDetailActivityIntent.putExtra(WeatherUtils.EXTRA_SEARCH_RESULT, detailedForecast);
        startActivity(searchResultDetailActivityIntent);
    }
}
