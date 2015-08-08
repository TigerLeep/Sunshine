package com.tigerbase.sunshine;

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
import android.widget.TextView;
import android.widget.Toast;

import com.tigerbase.sunshine.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastDetailFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>
{

    private final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private final int LOADER_ID = 100;

    private static final String[] DETAILS_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
    };

    // These indices are tied to DETAILS_COLUMNS.  If DETAILS_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;

    private TextView _textView = null;
    private ShareActionProvider _shareActionProvider = null;
    private String _forecast = "";
    private String _weatherLocationWithDateUrl = "";

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
        _textView = (TextView)rootView.findViewById(R.id.detail_text);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_forecastdetailfragment, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        _shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (_shareActionProvider != null && _forecast != null)
        {
            _shareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, _forecast + FORECAST_SHARE_HASHTAG);
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

    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(getActivity());
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
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
            String highAndLow = formatHighLows(
                    data.getDouble(ForecastDetailFragment.COL_WEATHER_MAX_TEMP),
                    data.getDouble(ForecastDetailFragment.COL_WEATHER_MIN_TEMP));

            String forecastText = Utility.formatDate(data.getLong(ForecastDetailFragment.COL_WEATHER_DATE)) +
                    " - " + data.getString(ForecastDetailFragment.COL_WEATHER_DESC) +
                    " - " + highAndLow;
            _forecast = forecastText;
            _textView.setText(forecastText);

            if (_shareActionProvider != null)
            {
                _shareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
