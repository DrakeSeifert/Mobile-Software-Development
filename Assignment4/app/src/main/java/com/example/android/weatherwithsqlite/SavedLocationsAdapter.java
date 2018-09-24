package com.example.android.weatherwithsqlite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.weatherwithsqlite.utils.OpenWeatherMapUtils;

import java.util.ArrayList;

/**
 * Created by Drake on 3/13/2018.
 */

public class SavedLocationsAdapter
        extends RecyclerView.Adapter<SavedLocationsAdapter.LocationViewHolder> {

    private ArrayList<String> mSavedLocationsList;
    OnLocationItemClickListener mLocationItemClickListener;

    public SavedLocationsAdapter(OnLocationItemClickListener locationItemClickListener) {
        mLocationItemClickListener = locationItemClickListener;
    }

    public interface OnLocationItemClickListener {
        void onLocationItemClick(String location);
    }

    public void initLocations(ArrayList<String> locationsList) {
        mSavedLocationsList = locationsList;
        notifyDataSetChanged();
    }

    public void updateLocations(String location) {
        mSavedLocationsList.add(location);
        //notifyDataSetChanged();
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() {
        if(mSavedLocationsList != null) {
            return mSavedLocationsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        holder.bind(mSavedLocationsList.get(position));
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.saved_locations, parent, false);
        return new LocationViewHolder(view);
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        private TextView mLocationTV;

        public LocationViewHolder(View itemView) {
            super(itemView);
            mLocationTV = (TextView)itemView.findViewById(R.id.tv_saved_forecast_location);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String location = mSavedLocationsList.get(getAdapterPosition());
                    mLocationItemClickListener.onLocationItemClick(location);
                }
            });
        }

        /*@Override
        public void onClick(View v) {
            //OpenWeatherMapUtils.ForecastItem forecastItem = mForecastItems.get(getAdapterPosition());
            //mForecastItemClickListener.onForecastItemClick(forecastItem);
            String location = mSavedLocationsList.get(getAdapterPosition());
            mLocationItemClickListener.onLocationItemClick(location);
        }*/

        public void bind(String location) {
            mLocationTV.setText(location);
        }
    }
}
