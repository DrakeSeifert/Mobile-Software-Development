package com.example.android.weatherwithsqlite;

import android.provider.BaseColumns;

/**
 * Created by Drake on 3/13/2018.
 */

public class WeatherContract {

    //Constructor is private so object cannot be instantiated
    private WeatherContract() {}

    public static class WeatherTable implements BaseColumns {
        //Note: _id column is inherited from BaseColumns interface
        public static final String TABLE_NAME = "weatherTable";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
