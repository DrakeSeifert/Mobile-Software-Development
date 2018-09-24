package com.example.android.connectedweather;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SearchResultDetailActivity extends AppCompatActivity {

    private TextView mTVSearchResultName;
    private TextView mTVSearchResultHighTemp;
    private TextView mTVSearchResultLowTemp;

    private WeatherUtils.SearchResult mSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_detail);

        mTVSearchResultName = (TextView)findViewById(R.id.tv_search_result_name);
        mTVSearchResultHighTemp = (TextView)findViewById(R.id.tv_search_result_humidity);
        mTVSearchResultLowTemp = (TextView)findViewById(R.id.tv_search_result_wind_speed);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(WeatherUtils.EXTRA_SEARCH_RESULT)) {
            mSearchResult = (WeatherUtils.SearchResult) intent.getSerializableExtra(WeatherUtils.EXTRA_SEARCH_RESULT);

            mTVSearchResultName.setText(mSearchResult.dateTime);

            String humidityStr = "Humidity: " + mSearchResult.humidity + "%";
            mTVSearchResultHighTemp.setText(humidityStr);

            String windSpeedStr = "Wind Speed: " + mSearchResult.windSpeed + " mph";
            mTVSearchResultLowTemp.setText(windSpeedStr);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_detail, menu);
        return true;
    }

    public void shareWeather() {
        if(mSearchResult != null) {
            String shareText = getString(R.string.share_weather_prefix) + " " +
                    mSearchResult.dateTime + " - " +
                    mSearchResult.description + " - " +
                    mSearchResult.temperature + "F";

            ShareCompat.IntentBuilder.from(this)
                    .setChooserTitle(R.string.share_weather)
                    .setType("text/plain")
                    .setText(shareText)
                    .startChooser();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareWeather();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
