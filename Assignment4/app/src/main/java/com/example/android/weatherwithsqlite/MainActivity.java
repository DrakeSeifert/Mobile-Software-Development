package com.example.android.weatherwithsqlite;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

import com.example.android.weatherwithsqlite.utils.OpenWeatherMapUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ForecastAdapter.OnForecastItemClickListener,
        LoaderManager.LoaderCallbacks<String>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        SavedLocationsAdapter.OnLocationItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FORECAST_URL_KEY = "forecastURL";
    private static final int FORECAST_LOADER_ID = 0;

    private TextView mForecastLocationTV;
    private RecyclerView mForecastItemsRV;
    private ProgressBar mLoadingIndicatorPB;
    private TextView mLoadingErrorMessageTV;
    private ForecastAdapter mForecastAdapter;

    private RecyclerView mSavedLocationsRV;
    private SavedLocationsAdapter mSavedLocationsAdapter;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private SQLiteDatabase mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Remove shadow under action bar.
        getSupportActionBar().setElevation(0);

        mForecastLocationTV = findViewById(R.id.tv_forecast_location);

        mLoadingIndicatorPB = findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessageTV = findViewById(R.id.tv_loading_error_message);
        mForecastItemsRV = findViewById(R.id.rv_forecast_items);

        mForecastAdapter = new ForecastAdapter(this, this);
        mForecastItemsRV.setAdapter(mForecastAdapter);
        mForecastItemsRV.setLayoutManager(new LinearLayoutManager(this));
        mForecastItemsRV.setHasFixedSize(true);

        mSavedLocationsRV = findViewById(R.id.rv_saved_locations);
        mSavedLocationsAdapter = new SavedLocationsAdapter(this);
        mSavedLocationsAdapter.initLocations(getAllSavedLocations());
        mSavedLocationsRV.setAdapter(mSavedLocationsAdapter);
        mSavedLocationsRV.setLayoutManager(new LinearLayoutManager(this));
        mSavedLocationsRV.setHasFixedSize(true);

        //Drawer
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        //SQLite Database
        //WeatherDBHelper dbHelper = new WeatherDBHelper(this);
        //mDB = dbHelper.getWritableDatabase();
        //mDB = dbHelper.getReadableDatabase();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        loadForecast(sharedPreferences, true);
    }

    /*private long addSearchResultToDB() {
        if (mSearchResult != null) {

        } else {
            return -1;
        }
    }*/

    /*
    public void createTable() {
        CREATE TABLE weatherTable (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                location TEXT NOT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    }

    public void insertTable() {
        INSERT INTO weatherTable (location) VALUES (
            'Portland, OR, US'
        );
    }

    public void readTable() {
        SELECT * FROM weatherTable;
        SELECT * FROM weatherTable WHERE location == 'Corvallis, OR, US';
        SELECT * FROM weatherTable WHERE location == '%, OR, US'; //Selects all from Oregon
        SELECT * FROM weatherTable ORDER BY timestamp DESC;
    }

    public void updateTable() {
        UPDATE weatherTable SET location == 'Bend, OR, US' WHERE location == 'Portland, OR, US';
    }

    public void deleteRowFromTable() {
        DELETE FROM weatherTable WHERE location == 'Bend, OR, US';
    }
    */

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        mDB.close();
        super.onDestroy();
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

        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_location:
                showForecastLocationInMap();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void loadForecast(SharedPreferences sharedPreferences, boolean initialLoad) {
        String forecastLocation = sharedPreferences.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default_value)
        );
        String temperatureUnits = sharedPreferences.getString(
                getString(R.string.pref_units_key),
                getString(R.string.pref_units_default_value)
        );

        mForecastLocationTV.setText(forecastLocation);
        mLoadingIndicatorPB.setVisibility(View.VISIBLE);

        String forecastURL = OpenWeatherMapUtils.buildForecastURL(forecastLocation, temperatureUnits);
        Bundle loaderArgs = new Bundle();
        loaderArgs.putString(FORECAST_URL_KEY, forecastURL);
        LoaderManager loaderManager = getSupportLoaderManager();
        if (initialLoad) {
            loaderManager.initLoader(FORECAST_LOADER_ID, loaderArgs, this);
        } else {
           loaderManager.restartLoader(FORECAST_LOADER_ID, loaderArgs, this);
         }
    }

    public void showForecastLocationInMap() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String forecastLocation = sharedPreferences.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default_value)
        );
        Uri geoUri = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", forecastLocation)
                .build();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        String forecastURL = null;
        if (args != null) {
            forecastURL = args.getString(FORECAST_URL_KEY);
        }
        return new ForecastLoader(this, forecastURL);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(TAG, "got forecast from loader");
        mLoadingIndicatorPB.setVisibility(View.INVISIBLE);
        if (data != null) {
            mLoadingErrorMessageTV.setVisibility(View.INVISIBLE);
            mForecastItemsRV.setVisibility(View.VISIBLE);
            ArrayList<OpenWeatherMapUtils.ForecastItem> forecastItems = OpenWeatherMapUtils.parseForecastJSON(data);
            mForecastAdapter.updateForecastItems(forecastItems);
        } else {
            mForecastItemsRV.setVisibility(View.INVISIBLE);
            mLoadingErrorMessageTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // Nothing ...
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String forecastLocation = sharedPreferences.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default_value)
        );

        if(sharedPreferences != null) {
            addSearchResultToDB(forecastLocation);
            //mSavedLocationsAdapter.updateLocations(forecastLocation);
            mSavedLocationsAdapter.initLocations(getAllSavedLocations());
        }

        boolean test = checkIsLocationInTable(forecastLocation);
        Log.d(TAG, "LOG: " + forecastLocation + " is in table after insert: " + test);

        loadForecast(sharedPreferences, false);
    }

    private long addSearchResultToDB(String forecastLocation) {

        WeatherDBHelper dbHelper = new WeatherDBHelper(this);
        mDB = dbHelper.getWritableDatabase();

        boolean test = checkIsLocationInTable(forecastLocation);
        Log.d(TAG, "LOG: " + forecastLocation + " is in table before insert: " + test);

        if(!checkIsLocationInTable(forecastLocation)) {
            ContentValues values = new ContentValues();
            values.put(WeatherContract.WeatherTable.COLUMN_LOCATION, forecastLocation);
            return mDB.insert(WeatherContract.WeatherTable.TABLE_NAME, null, values);
        } else {
            return -1;
        }
    }

    private boolean checkIsLocationInTable(String forecastLocation) {
        String sqlSelection = WeatherContract.WeatherTable.COLUMN_LOCATION + " = ?";
        String[] sqlSelectionArgs = { forecastLocation }; //Protect against SQL injection
        Cursor cursor = mDB.query(
                    WeatherContract.WeatherTable.TABLE_NAME,
                    null,
                    sqlSelection,
                    sqlSelectionArgs,
                    null,
                    null,
                    null
                );
        boolean isInTable = cursor.getCount() > 0;
        cursor.close();
        return isInTable;
    }

    private void deleteLocationFromDB(String forecastLocation) {
        String sqlSelection = WeatherContract.WeatherTable.COLUMN_LOCATION + " = ?";
        String[] sqlSelectionArgs = { forecastLocation }; //Protect against SQL injection
        mDB.delete(WeatherContract.WeatherTable.TABLE_NAME, sqlSelection, sqlSelectionArgs);
    }

    private void deleteAllLocationsFromDB() {
        mDB.delete(WeatherContract.WeatherTable.TABLE_NAME, null, null);
    }

    @Override
    public void onLocationItemClick(String location) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pref_location_key), location);
        editor.apply();
        mDrawerLayout.closeDrawers();
    }

    private ArrayList<String> getAllSavedLocations() {
        WeatherDBHelper dbHelper = new WeatherDBHelper(this);
        mDB = dbHelper.getReadableDatabase();

        Cursor cursor = mDB.query(
                WeatherContract.WeatherTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WeatherContract.WeatherTable.COLUMN_TIMESTAMP + " DESC"
        );

        ArrayList<String> savedLocations = new ArrayList<>();
        while(cursor.moveToNext()) {
            String location;
            location = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherTable.COLUMN_LOCATION));
            //Log.d(TAG, "LOG: Adding location: " + location + " to Nav");
            savedLocations.add(location);
        }
        cursor.close();
        return savedLocations;
    }
}
