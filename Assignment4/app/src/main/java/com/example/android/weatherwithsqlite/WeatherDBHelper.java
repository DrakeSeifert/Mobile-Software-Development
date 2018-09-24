package com.example.android.weatherwithsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Drake on 3/13/2018.
 */

public class WeatherDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weatherTable.db";
    private static final int DATABASE_VERSION = 1;

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //onCreate is called when DB needs to be created
        //This happens under the hood by SQLiteOpenHelper

        final String SQL_CREATE_WEATHER_TABLE =
                "CREATE TABLE " + WeatherContract.WeatherTable.TABLE_NAME + " (" +
                    WeatherContract.WeatherTable._ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WeatherContract.WeatherTable.COLUMN_LOCATION +
                        " TEXT NOT NULL, " +
                    WeatherContract.WeatherTable.COLUMN_TIMESTAMP +
                        " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Typically this method would use an ALTER TABLE statement,
        //however we will simply delete it and create a new one

        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherTable.TABLE_NAME);
        onCreate(db);
    }
}
