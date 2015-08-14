package com.tigerbase.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tigerbase.sunshine.data.WeatherContract;


public class MainActivity extends ActionBarActivity implements IForecastList {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DETAILFRAGMENTTAG";

    private String _locationSetting = "";
    private boolean _twoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        _locationSetting = Utility.getPreferredLocation(this);
        if (findViewById(R.id.weather_detail_container) != null)
        {
            _twoPane = true;
            if (savedInstanceState == null)
            {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.weather_detail_container,
                                new DetailFragment(),
                                DETAILFRAGMENT_TAG)
                        .commit();
            }
            else
            {
                _twoPane = false;
            }
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.v(LOG_TAG, "onStart");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.v(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v(LOG_TAG, "onResume");

        if (_locationSetting != Utility.getPreferredLocation(this))
        {
            Log.v(LOG_TAG, "Preferred Location changed");
            ForecastFragment forecastFragment =
                    (ForecastFragment)getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_forecast);
            forecastFragment.onLocationChanged();
            _locationSetting = Utility.getPreferredLocation(this);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.v(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        if(id == R.id.action_map)
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(location).toString());
            intent.setData(geoUri);
            if (intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, R.string.no_map_app, Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onForecastSelected(Uri locationWithDateUri) {
        Log.v(LOG_TAG, "onForecastSelected: " + locationWithDateUri.toString());
        if(!_twoPane)
        {
            Log.v(LOG_TAG, "onForecastSelected: !_twoPane");
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(locationWithDateUri);
            startActivity(intent);
        }
        else
        {
            Log.v(LOG_TAG, "onForecastSelected: _twoPane");
            DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager()
                    .findFragmentByTag(DETAILFRAGMENT_TAG);
            if(detailFragment != null)
            {
                detailFragment.onUriChange(locationWithDateUri);
            }
        }
    }

}
