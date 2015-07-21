package com.tigerbase.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private ArrayAdapter<String> _adapter = null;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
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
        if (id == R.id.action_refresh)
        {
            UpdateWeather();
            //String[] forecasts = task.get();
            //for (String forecast : forecasts) {
            //    Log.v(LOG_TAG, "Forecast: " + forecast);
            //}
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UpdateWeather() {
        FetchWeatherTask task = new FetchWeatherTask(getActivity(), _adapter);
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        _adapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forcast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>());

        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = _adapter.getItem(position);
                Intent intent = new Intent(ForecastFragment.this.getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, forecast);
                ForecastFragment.this.startActivity(intent);
                //Toast toast = Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT);
                //toast.show();
            }
        });

        return rootView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        UpdateWeather();
    }
}
