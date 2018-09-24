package com.example.drake.basicweather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Drake on 1/25/18.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private ArrayList<String> mWeatherList;

    private OnWeatherClickedListener mClickedListener;

    public WeatherAdapter(OnWeatherClickedListener clickedListener) {
        mWeatherList = new ArrayList<String>();
        mClickedListener = clickedListener;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.weather_list_item, parent, false);
        WeatherViewHolder viewHolder = new WeatherViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        String weather = mWeatherList.get(position);
        holder.bind(weather);
    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    public void addWeather(String weather) {
        mWeatherList.add(weather);
        notifyDataSetChanged();
    }

    public interface OnWeatherClickedListener {
        void onWeatherClick(int weatherIdx);
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder {

        private TextView mWeatherText;

        public WeatherViewHolder(final View itemView) {
            super(itemView);
            mWeatherText = (TextView)itemView.findViewById(R.id.tv_weather_text);
            mWeatherText.setClickable(true);

            mWeatherText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mClickedListener.onWeatherClick(getAdapterPosition());
                }
            });
        }

        public void bind(String weather) {
            mWeatherText.setText(weather);
        }
    }

}
