package com.tigerbase.sunshine;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;

import com.tigerbase.sunshine.data.WeatherContract;
import com.tigerbase.sunshine.data.WeatherProvider;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    private final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private final int LOADER_ID = 100;
    private ForecastAdapter _adapter = null;

    public ForecastFragment() {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        //if (id == R.id.action_refresh)
        //{
        //    UpdateWeather();
        //    //String[] forecasts = task.get();
        //    //for (String forecast : forecasts) {
        //    //    Log.v(LOG_TAG, "Forecast: " + forecast);
        //    //}
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    private void UpdateWeather() {
        FetchWeatherTask task = new FetchWeatherTask(getActivity());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String locationKey = getString(R.string.pref_location_key);
        String locationDefault = getString(R.string.pref_location_default);
        String location = preferences.getString(locationKey, locationDefault);

        String unitsKey = getString(R.string.pref_units_key);
        String unitsDefault = getString(R.string.pref_units_default);
        String units = preferences.getString(unitsKey, unitsDefault);
        task.execute(location, units);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        _adapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(_adapter);
        //listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        //{
        //    @Override
        //    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //        String forecast = _adapter.getItem(position);
        //        Intent intent = new Intent(ForecastFragment.this.getActivity(), DetailActivity.class);
        //        intent.putExtra(Intent.EXTRA_TEXT, forecast);
        //        ForecastFragment.this.startActivity(intent);
        //        //Toast toast = Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT);
        //        //toast.show();
        //    }
        //});

        return rootView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        UpdateWeather();
    }

    // LoaderManager.LoaderCallbacks interface methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        _adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        _adapter.swapCursor(null);
    }
}
