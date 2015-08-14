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
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private final int LOADER_ID = 100;

    private final static String ARGUMENT_LOCATIONWITHDATEURI_KEY = "LocationWithDateUri";
    private static final String[] DETAILS_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
    };

    // These indices are tied to DETAILS_COLUMNS.  If DETAILS_COLUMNS changes, these
    // must change.
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_CONDITION_ID = 5;
    private static final int COL_WEATHER_HUMIDITY = 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_PRESSURE = 9;

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
    private Uri _locationWithDateUri = null;

    public DetailFragment()
    {
    }

    public static DetailFragment CreateInstance(Uri locationWithDateUri)
    {
        DetailFragment detailFragment = new DetailFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_LOCATIONWITHDATEURI_KEY, locationWithDateUri);
        detailFragment.setArguments(arguments);

        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.v(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.v(LOG_TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        Uri locationWithDateUri = null;
        Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();

        if (intent != null && intent.getData() != null)
        {
            // Our Activity was created with an Intent (single-pane mode)
            locationWithDateUri = intent.getData();
        }
        else if (arguments!= null && arguments.containsKey(ARGUMENT_LOCATIONWITHDATEURI_KEY))
        {
            // Our Activity was created with Bundle arguments (two-pane mode)
            locationWithDateUri = arguments.getParcelable(ARGUMENT_LOCATIONWITHDATEURI_KEY);
        }

        // Save the Uri to be used with the loader.
        _locationWithDateUri = locationWithDateUri;

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.v(LOG_TAG, "onCreateView");
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
        Log.v(LOG_TAG, "onCreateOptionsMenu");
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
        Log.v(LOG_TAG, "onOptionsItemSelected");
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
        Log.v(LOG_TAG, "onCreateLoader");
        return new CursorLoader(
                getActivity(),
                _locationWithDateUri,
                DETAILS_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished");
        if(data != null && data.moveToFirst())
        {
            long date = data.getLong(DetailFragment.COL_WEATHER_DATE);
            float high = data.getFloat(DetailFragment.COL_WEATHER_MAX_TEMP);
            float low = data.getFloat(DetailFragment.COL_WEATHER_MIN_TEMP);
            int conditionCode = data.getInt(DetailFragment.COL_WEATHER_CONDITION_ID);
            int iconResourceId = Utility.getArtResourceForWeatherCondition(conditionCode);
            float humidity = data.getFloat(DetailFragment.COL_WEATHER_HUMIDITY);
            float windSpeed = data.getFloat(DetailFragment.COL_WEATHER_WIND_SPEED);
            float degrees = data.getFloat(DetailFragment.COL_WEATHER_DEGREES);
            float pressure = data.getFloat(DetailFragment.COL_WEATHER_PRESSURE);
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
            _iconImageView.setImageResource(iconResourceId);
            _forecastTextView.setText(description);

            //if (_shareActionProvider != null)
            //{
            //    _shareActionProvider.setShareIntent(createShareForecastIntent());
            //}
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset");
    }

    public void onLocationChange(String location)
    {
        Log.v(LOG_TAG, "onLocationChange");
        Uri locationWithDateUri = _locationWithDateUri;
        if(locationWithDateUri != null)
        {
            long date = WeatherContract.WeatherEntry
                    .getDateFromUri(locationWithDateUri);
            Uri newLocationWithDateUri = WeatherContract.WeatherEntry
                    .buildWeatherLocationWithDate(location, date);
            _locationWithDateUri = newLocationWithDateUri;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    public void onDateChange(long date)
    {
        Log.v(LOG_TAG, "onDateChange");
        Uri locationWithDateUri = _locationWithDateUri;
        if(locationWithDateUri != null)
        {
            String location = WeatherContract.WeatherEntry
                    .getLocationSettingFromUri(locationWithDateUri);
            Uri newLocationWithDateUri = WeatherContract.WeatherEntry
                    .buildWeatherLocationWithDate(location, date);
            _locationWithDateUri = newLocationWithDateUri;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    public void onUriChange(Uri locationWithDateUri)
    {
        Log.v(LOG_TAG, "onUriChange");
        if(locationWithDateUri != null)
        {
            _locationWithDateUri = locationWithDateUri;
            if(getLoaderManager() == null)
            {
                Log.v(LOG_TAG, "onUriChange: null");
            }
            else
            {
                Log.v(LOG_TAG, "onUriChange: !null");
                getLoaderManager().restartLoader(LOADER_ID, null, this);
            }
        }
    }

}
