package com.tigerbase.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tigerbase.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter
{
    private final String LOG_TAG = ForecastAdapter.class.getSimpleName();
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;

    private boolean _useSpecialTodayLayout = true;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setUseSpecialTodayLayout(boolean useSpecialTodayLayout)
    {
        _useSpecialTodayLayout = useSpecialTodayLayout;
    }

    @Override
    public int getItemViewType(int position)
    {
        return (position == 0 && _useSpecialTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch(viewType)
        {
            case VIEW_TYPE_TODAY:
                layoutId = R.layout.list_item_forecast_today;
                break;
            case VIEW_TYPE_FUTURE_DAY:
                layoutId = R.layout.list_item_forecast;
                break;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ForecastListItemViewHolder viewHolder = new ForecastListItemViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        ForecastListItemViewHolder viewHolder = (ForecastListItemViewHolder)view.getTag();

        // TODO Read weather forecast from cursor
        String forecast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(forecast);

        // Read weather icon ID from cursor
        int conditionCode = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int iconResourceId = -1;
        int viewType = getItemViewType(cursor.getPosition());
        switch(viewType)
        {
            case VIEW_TYPE_TODAY:
                iconResourceId = Utility.getArtResourceForWeatherCondition(conditionCode);
                break;
            case VIEW_TYPE_FUTURE_DAY:
                iconResourceId = Utility.getIconResourceForWeatherCondition(conditionCode);
                break;
        }

        if (iconResourceId == -1)
        {
            iconResourceId = R.mipmap.ic_launcher;
        }
        viewHolder.iconView.setImageResource(iconResourceId);
        viewHolder.iconView.setContentDescription(forecast + " icon");

        // TODO Read date from cursor
        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(mContext, date));

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));
        viewHolder.highTempView.setContentDescription("High temperature " + viewHolder.highTempView.getText());

        // TODO Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
        viewHolder.lowTempView.setContentDescription("Low temperature " + viewHolder.lowTempView.getText());
    }
}