package com.example.drake.basicweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements WeatherAdapter.OnWeatherClickedListener {

    private RecyclerView mWeatherListRecyclerView;
    private WeatherAdapter mAdapter;

    private String [] weatherOptions = {
                    "Sat Jan 27 - 51/46F",
                    "Sun Jan 28 - 60/43F",
                    "Mon Jan 29 - 59/39F",
                    "Tue Jan 30 - 49/36F",
                    "Wed Jan 31 - 50/34F",
                    "Thu Feb  1 - 51/37F",
                    "Fri Feb  2 - 52/42F",
                    "Sat Feb  3 - 51/35F",
                    "Sun Feb  4 - 50/34F",
                    "Mon Feb  5 - 49/35F",
                    "Tue Feb  6 - 50/35F",
                    "Wed Feb  7 - 50/35F",
                    "Thu Feb  8 - 54822/35F",
                    "Fri Feb  9 - 54/37"
                    };

    private String [] weatherDetails = {
                    "Showers, Precipitation: 50%, Wind: S 17 Mph, Humidity: 91%",
                    "Mostly Cloudy, Precipitation: 10%, Wind: S 7 Mph, Humidity: 87%",
                    "PM Rain, Precipitation: 80%, Wind: S 9 Mph, Humidity: 71%",
                    "AM Showers, Precipitation: 40%, Wind: SW 8 Mph, Humidity: 83%",
                    "Mostly Cloudy, Precipitation: 20%, Wind: SSW 5 Mph, Humidity: 80%",
                    "Partly Cloudy, Precipitation: 10%, Wind: NE 6 Mph, Humidity: 79%",
                    "Partly Cloudy, Precipitation: 20%, Wind: S 6 Mph, Humidity: 84%",
                    "Cloudy, Precipitation: 20%, Wind: SW 6 Mph, Humidity: 87%",
                    "Partly Cloudy, Precipitation: 10%, Wind: SSW 6 Mph, Humidity: 93%",
                    "AM Showers, Precipitation: 40%, Wind: SSW 6 Mph, Humidity: 88%",
                    "Partly Cloudy, Precipitation: 20%, Wind: SSW 6 Mph, Humidity: 80%",
                    "Partly Cloudy, Precipitation: 20%, Wind: S 6 Mph, Humidity: 78%",
                    "A Raining Inferno Will Crash From the Sky and Fire Tornadoes Will Rupture the Earth's Crust, Precipitation: 0%, Wind: All 14602 Mph, Humidity: 111%",
                    "Partly Cloudy, Precipitation: 20%, Wind: SSE 7 Mph, Humidity: 74%",
                    };

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToast = null;

        mWeatherListRecyclerView = (RecyclerView)findViewById(R.id.rv_weather_list);

        //Set layout manager for recycler view
        mWeatherListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWeatherListRecyclerView.setHasFixedSize(true);

        //Create weather adapter object
        mAdapter = new WeatherAdapter(this);
        mWeatherListRecyclerView.setAdapter(mAdapter);

        //Print weather data
        for(int i = 0; i < weatherOptions.length; i++) {
            mAdapter.addWeather(weatherOptions[i]);
        }
    }

    @Override
    public void onWeatherClick(int weatherIdx) {
        if (mToast != null) {
            mToast.cancel();
        }
        String toastText = weatherDetails[weatherIdx];
        mToast = Toast.makeText(this, toastText, Toast.LENGTH_LONG);
        mToast.show();
    }
}
