package com.example.basicweatherapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.basicweatherapp.R;
import com.example.basicweatherapp.models.List;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ForecastDataAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<List> mListItems;
    private boolean isExpanded = false;

    public ForecastDataAdapter(Context context, ArrayList<List> listItems) {
        this.mContext = context;
        this.mListItems = listItems;
    }

    @Override
    public int getGroupCount() {
        return (mListItems != null ? 5 : 0);
    }

    @Override
    public int getChildrenCount(int i) {
        return 4;
    }

    @Override
    public List getGroup(int groupPosition) {
        if(mListItems == null) {
            return null;
        }
        return mListItems.get(groupPosition * 4);
    }

    @Override
    public List getChild(int groupPosition, int childPosition) {
        return mListItems.get(childPosition);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean b, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return null;
        }

        if (view == null) {
            view = inflater.inflate(R.layout.forecast_collapsed_data, null);
            updateArrows(view, isExpanded);
            TextView dateHeader = view.findViewById(R.id.date);
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            DateTime dateTime = formatter.parseDateTime(mListItems.get(listPosition * 4).dtTxt);
            Log.d("ForecastDateAdapter", "date: " + dateTime);
            dateHeader.setText(getFormattedDate(dateTime));
        }

        return view;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition, boolean b, View view, ViewGroup viewGroup) {
        List item = getChild(listPosition, expandedListPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null) return null;
            view = layoutInflater.inflate(R.layout.forecast_row_data, null);
        }
        fillChildWithView(view, item);
        return view;
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
            String url = "https://openweathermap.org/img/wn/" + item.weather.get(0).icon + "@2x.png";
            forecastTime.setText(item.dtTxt.split(" ")[1]);
            temperature.setText(item.main.temp);
            wind.setText(item.wind.speed);
            if (item.rain != null) {
                rain.setText(item.rain.h3 + "%");
            } else {
                rain.setText("0%");
            }
            humidity.setText(item.main.humidity + "%");
            Picasso.get().load(url).into(image);
        }
    }

    private String getFormattedDate(DateTime dateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, y", Locale.getDefault());
        return formatter.format(dateTime.toDate());
    }
}
