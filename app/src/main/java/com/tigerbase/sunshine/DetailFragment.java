package com.tigerbase.sunshine;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tigerbase.sunshine.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>
{

    private final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private final int LOADER_ID = 100;

    private static final String[] DETAILS_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
    };

    // These indices are tied to DETAILS_COLUMNS.  If DETAILS_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_WIND_SPEED = 6;
    static final int COL_WEATHER_DEGREES = 7;
    static final int COL_WEATHER_PRESSURE = 8;

    private TextView _dayTextView = null;
    private TextView _dateTextView = null;
    private TextView _highTextView = null;
    private TextView _lowTextView = null;
    private TextView _humidityTextView = null;
    private TextView _windTextView = null;
    private TextView _pressureTextView = null;
    private ImageView _iconImageView = null;
    private TextView _forecastTextView = null;

    private ShareActionProvider _shareActionProvider = null;
    private String _weatherLocationWithDateUrl = "";

    public DetailFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        _dayTextView = (TextView)rootView.findViewById(R.id.detail_day_textview);
        _dateTextView = (TextView)rootView.findViewById(R.id.detail_date_textview);
        _highTextView = (TextView)rootView.findViewById(R.id.detail_high_textview);
        _lowTextView = (TextView)rootView.findViewById(R.id.detail_low_textview);
        _humidityTextView = (TextView)rootView.findViewById(R.id.detail_humidity_textview);
        _windTextView = (TextView)rootView.findViewById(R.id.detail_wind_textview);
        _pressureTextView = (TextView)rootView.findViewById(R.id.detail_pressure_textview);
        _iconImageView = (ImageView)rootView.findViewById(R.id.detail_icon);
        _forecastTextView = (TextView)rootView.findViewById(R.id.detail_forecast_textview);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_forecastdetailfragment, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        _shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //if (_shareActionProvider != null && _forecast != null)
        //{
        //    _shareActionProvider.setShareIntent(createShareForecastIntent());
        //}
    }

    //private Intent createShareForecastIntent()
    //{
    //    Intent intent = new Intent(Intent.ACTION_SEND);
    //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    //    intent.setType("text/plain");
    //    intent.putExtra(Intent.EXTRA_TEXT, _forecast + FORECAST_SHARE_HASHTAG);
    //    return intent;
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_map)
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
            intent.setData(geoUri);
            PackageManager packageManager = getActivity().getPackageManager();
            if (intent.resolveActivity(packageManager) != null)
            {
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getActivity(), R.string.no_map_app, Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.action_share)
        {
            //Log.d(LOG_TAG, "Forecast: " + _forecast);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(getActivity());
        return Utility.formatTemperature(getActivity(), high, isMetric) + "/"
               + Utility.formatTemperature(getActivity(), low, isMetric);
    }

    // LoaderManager.LoaderCallbacks interface methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null)
        {
            return null;
        }
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                DETAILS_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst())
        {
            long date = data.getLong(DetailFragment.COL_WEATHER_DATE);
            float high = data.getFloat(DetailFragment.COL_WEATHER_MAX_TEMP);
            float low = data.getFloat(DetailFragment.COL_WEATHER_MIN_TEMP);
            float humidity = data.getFloat(DetailFragment.COL_WEATHER_HUMIDITY);
            float windSpeed = data.getFloat(DetailFragment.COL_WEATHER_WIND_SPEED);
            float degrees = data.getFloat(DetailFragment.COL_WEATHER_DEGREES);
            float pressure = data.getFloat(DetailFragment.COL_WEATHER_PRESSURE);
            float iconId = data.getInt(DetailFragment.COL_WEATHER_ID);
            String description = data.getString(DetailFragment.COL_WEATHER_DESC);

            Activity context = getActivity();
            boolean isMetric = Utility.isMetric(context);
            _dayTextView.setText(Utility.getDayName(context, date));
            _dateTextView.setText(Utility.getFormattedMonthDay(context, date));
            _highTextView.setText(Utility.formatTemperature(context, high, isMetric));
            _lowTextView.setText(Utility.formatTemperature(context, low, isMetric));
            _humidityTextView.setText(String.format(context.getString(R.string.format_humidity), humidity));
            _windTextView.setText(Utility.getFormattedWind(context, windSpeed, degrees));
            _pressureTextView.setText(String.format(context.getString(R.string.format_pressure), pressure));
            _iconImageView.setImageResource(R.mipmap.ic_launcher);
            _forecastTextView.setText(description);

            //if (_shareActionProvider != null)
            //{
            //    _shareActionProvider.setShareIntent(createShareForecastIntent());
            //}
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
