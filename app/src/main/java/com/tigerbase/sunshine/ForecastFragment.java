package com.tigerbase.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;

import com.tigerbase.sunshine.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private final int LOADER_ID = 100;
    private final String STATE_SELECTED_POSITION_KEY = "SelectedPosition";

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private ForecastAdapter _adapter = null;
    private ListView _listView = null;
    private int _selectedPosition = 0;
    private boolean _useSpecialTodayLayout = true;

    public ForecastFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.v(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.v(LOG_TAG, "onCreateView");
        _adapter = new ForecastAdapter(getActivity(), null, 0);
        _adapter.setUseSpecialTodayLayout(_useSpecialTodayLayout);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SELECTED_POSITION_KEY))
        {
            _selectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION_KEY);
            Log.v(LOG_TAG, "onCreateView: Retrieved _selectedPosition == " + Integer.toString(_selectedPosition));
        }

        _listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        _listView.setAdapter(_adapter);
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Log.v(LOG_TAG, "onItemClick: _selectedPosition == " + Integer.toString(_selectedPosition));
                    _selectedPosition = position;
                    Log.v(LOG_TAG, "onItemClick: set _selectedPosition = " + Integer.toString(_selectedPosition));
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Uri locationWithDateUri = WeatherContract
                            .WeatherEntry
                            .buildWeatherLocationWithDate(
                                    locationSetting,
                                    cursor.getLong(COL_WEATHER_DATE));
                    Log.v(LOG_TAG, locationWithDateUri.toString());
                    if (getActivity() instanceof IForecastList) {
                        Log.v(LOG_TAG, "instanceof IForecastList");
                        ((IForecastList) getActivity()).onForecastSelected(locationWithDateUri);
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.v(LOG_TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    // LoaderManager.LoaderCallbacks interface methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Log.v(LOG_TAG, "onCreateLoader");
        String locationSetting = Utility.getPreferredLocation(getActivity());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri =
                WeatherContract
                        .WeatherEntry
                        .buildWeatherLocationWithStartDate(
                                locationSetting,
                                System.currentTimeMillis());
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onStart()
    {
        Log.v(LOG_TAG, "onStart");
        super.onStart();
        //UpdateWeather();
    }

    @Override
    public void onResume()
    {
        Log.v(LOG_TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.v(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (_selectedPosition != ListView.INVALID_POSITION)
        {
            outState.putInt(STATE_SELECTED_POSITION_KEY, _selectedPosition);
            Log.v(LOG_TAG, "onSaveInstanceState saved _selectedPosition == " + Integer.toString(_selectedPosition));
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        Log.v(LOG_TAG, "onLoadFinished");
        _adapter.swapCursor(data);
        if (_selectedPosition != ListView.INVALID_POSITION) {
            _listView.setItemChecked(_selectedPosition, true);
            _listView.smoothScrollToPosition(_selectedPosition);
            Log.v(LOG_TAG, "onLoadFinished: select position " + Integer.toString(_selectedPosition));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Log.v(LOG_TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.v(LOG_TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.action_refresh)
        {
            UpdateWeather();
            Log.v(LOG_TAG, "Refresh!");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UpdateWeather() {
        Log.v(LOG_TAG, "UpdateWeather!");

        FetchWeatherTask task = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());

        task.execute(location);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        Log.v(LOG_TAG, "onLoaderReset");
        _adapter.swapCursor(null);
    }

    public void onLocationChanged()
    {
        Log.v(LOG_TAG, "onLocationChanged");
        UpdateWeather();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public void setUseSpecialTodayLayout(boolean useSpecialTodayLayout)
    {
        _useSpecialTodayLayout = useSpecialTodayLayout;
        if (_adapter != null)
        {
            _adapter.setUseSpecialTodayLayout(_useSpecialTodayLayout);
        }
    }
}
