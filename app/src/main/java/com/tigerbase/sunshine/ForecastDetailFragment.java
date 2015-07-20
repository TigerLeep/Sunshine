package com.tigerbase.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastDetailFragment extends Fragment
{

    private final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private final String FORECAST_SHARE_HASTAG = " #SunshineApp";

    private TextView _textView = null;
    private ShareActionProvider _shareActionProvider = null;
    private String _forecast = "";

    public ForecastDetailFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        _textView = (TextView)rootView.findViewById(R.id.detail_text);

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT))
        {
            _forecast = intent.getStringExtra(Intent.EXTRA_TEXT);
            _textView.setText(_forecast);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_forecastdetailfragment, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        _shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (_shareActionProvider != null)
        {
            _shareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else
        {
            Log.e(LOG_TAG, "ShareActionProvider is null");
        }
    }

    private Intent createShareForecastIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, _forecast + FORECAST_SHARE_HASTAG);
        return intent;
    }

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
            Log.d(LOG_TAG, "Forecast: " + _forecast);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
