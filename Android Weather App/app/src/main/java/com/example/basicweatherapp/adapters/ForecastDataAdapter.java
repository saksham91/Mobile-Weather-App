package com.example.basicweatherapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.basicweatherapp.R;
import com.example.basicweatherapp.models.List;
import com.example.basicweatherapp.util.UnitsFormatter;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ForecastDataAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private java.util.List<String> mDayNames;
    private Map<String, java.util.List<List>> mTimeBasedWeather;
    private boolean isMetric;

    public ForecastDataAdapter(Context context, java.util.List<String> dayNames, Map<String, java.util.List<List>> timeBasedWeather, boolean isMetric) {
        this.mContext = context;
        this.mDayNames = dayNames;
        this.mTimeBasedWeather = timeBasedWeather;
        this.isMetric = isMetric;
    }

    @Override
    public int getGroupCount() {
        return 5;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mTimeBasedWeather != null ? mTimeBasedWeather.get(mDayNames.get(groupPosition)).size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (mDayNames == null) {
            return null;
        }
        return mDayNames.get(groupPosition);
    }

    @Override
    public List getChild(int groupPosition, int childPosition) {
        return mTimeBasedWeather.get(mDayNames.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return null;
        }

        if (view == null) {
            view = inflater.inflate(R.layout.forecast_collapsed_data, null);
        }
        updateArrows(view, isExpanded);
        TextView dateHeader = view.findViewById(R.id.date);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = formatter.parseDateTime(mDayNames.get(listPosition));
        dateHeader.setText(getFormattedDate(dateTime));

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        List item = getChild(groupPosition, childPosition);

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null) return null;
            view = layoutInflater.inflate(R.layout.forecast_expanded_data, null);
        }

        if (groupPosition > 0) {
            hideLabels(view);
        }
        fillChildWithView(view, item);
        return view;
    }

    private void hideLabels(View view) {
        TextView tv1 = view.findViewById(R.id.forecast_data_time_label);
        TextView tv2 = view.findViewById(R.id.temp_label);
        TextView tv3 = view.findViewById(R.id.wind_label);
        TextView tv4 = view.findViewById(R.id.rain_label);
        TextView tv5 = view.findViewById(R.id.humidity_label);
        TextView tv6 = view.findViewById(R.id.weather_icon_label);
        tv1.setVisibility(View.GONE);
        tv2.setVisibility(View.GONE);
        tv3.setVisibility(View.GONE);
        tv4.setVisibility(View.GONE);
        tv5.setVisibility(View.GONE);
        tv6.setVisibility(View.GONE);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private void updateArrows(View view, boolean isExpanded) {
        if (view == null) return;

        ImageView arrowImage = view.findViewById(R.id.arrow);
        if (arrowImage ==  null) return;

        if (isExpanded) {
            arrowImage.setImageDrawable(mContext.getDrawable(R.drawable.arrow_up));
        } else {
            arrowImage.setImageDrawable(mContext.getDrawable(R.drawable.arrow_down));
        }
    }

    @SuppressLint("SetTextI18n")
    private void fillChildWithView(View view, List item) {
        if (view == null) return;

        TextView forecastTime = view.findViewById(R.id.forecast_data_time);
        TextView temperature = view.findViewById(R.id.temp_value);
        TextView wind = view.findViewById(R.id.wind_value);
        TextView rain = view.findViewById(R.id.rain_value);
        TextView humidity = view.findViewById(R.id.humidity_value);
        ImageView image = view.findViewById(R.id.weather_icon);

        if (item != null) {
            int temp = (int) Double.parseDouble(item.main.temp);
            int windSpeed = (int) Double.parseDouble(item.wind.speed);
            String url = "https://openweathermap.org/img/wn/" + item.weather.get(0).icon + "@2x.png";
            forecastTime.setText(UnitsFormatter.getFormattedTime(item.dtTxt.split(" ")[1], false));
            temperature.setText(isMetric ? String.format(Locale.getDefault(), mContext.getString(R.string.temp_in_celsius), String.valueOf(temp))
                    : String.format(Locale.getDefault(), mContext.getString(R.string.temp_in_fahrenheit), String.valueOf(temp)));
            wind.setText(isMetric ? windSpeed + " kph" : windSpeed + " mph");
            if (item.rain != null) {
                rain.setText((int) Math.round(item.rain.h3) + " mm");
            } else {
                rain.setText("0 mm");
            }
            humidity.setText(item.main.humidity + "%");
            Picasso.get().load(url).into(image);
        }
    }

    private String getFormattedDate(DateTime dateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        return formatter.format(dateTime.toDate());
    }
}
