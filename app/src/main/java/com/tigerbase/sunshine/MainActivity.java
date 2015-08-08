package com.tigerbase.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String FORECASTFRAGMENT_TAG = "FORECASTFRAGMENT";

    private String _locationSetting = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        _locationSetting = Utility.getPreferredLocation(this);
        if (savedInstanceState == null) {
            Log.v(LOG_TAG, "Creating new ForecastFragment");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.forecast_container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
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
                            .findFragmentByTag(FORECASTFRAGMENT_TAG);
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

}
