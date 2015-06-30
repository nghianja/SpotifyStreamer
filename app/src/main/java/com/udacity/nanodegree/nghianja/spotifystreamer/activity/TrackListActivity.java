package com.udacity.nanodegree.nghianja.spotifystreamer.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.adapter.TrackArrayAdapter;
import com.udacity.nanodegree.nghianja.spotifystreamer.fragment.SettingsFragment;
import com.udacity.nanodegree.nghianja.spotifystreamer.fragment.TrackListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * References:
 * [1] http://stackoverflow.com/questions/15392261/android-pass-dataextras-to-a-fragment
 */
public class TrackListActivity extends Activity {

    private static final String TAG = "TrackListActivity";
    private TrackListFragment subFragment;
    private String artistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sub);

        FragmentManager manager = getFragmentManager();
        subFragment = (TrackListFragment) manager.findFragmentById(R.id.sub_fragment);

        // Get the Spotify ID from the intent
        Intent intent = getIntent();
        artistId = intent.getStringExtra("SpotifyId");
        Log.d(TAG, "SpotifyId=" + artistId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_refresh) {
            getArtistTopTrack();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getCountryCode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String country = sharedPref.getString(SettingsFragment.KEY_PREF_COUNTRY, "");
        if (country.equals("")) {
            country = this.getResources().getConfiguration().locale.getCountry();
        }
        Log.d(TAG, "country=" + country);
        return country;
    }

    public void getArtistTopTrack() {
        setProgressBarIndeterminateVisibility(true);
        GetArtistTopTrackTask task = new GetArtistTopTrackTask();
        task.execute(artistId);
    }

    public void updateAdapter(List<Track> items) {
        TrackArrayAdapter adapter = (TrackArrayAdapter) subFragment.getListAdapter();

        if (adapter == null) {
            Log.w(TAG, "adapter should not be null");
        } else {
            adapter.clear();
            adapter.addAll(items);
            adapter.notifyDataSetChanged();
        }

        setProgressBarIndeterminateVisibility(false);
    }

    public void toastNoNetwork() {
        Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
    }

    public void toastConnectionError() {
        Toast.makeText(this, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
    }

    private class GetArtistTopTrackTask extends AsyncTask<String, Void, Tracks> {
        @Override
        protected Tracks doInBackground(String... artistIds) {
            Tracks results = new Tracks();

            if (isNetworkAvailable()) {
                try {
                    String country = getCountryCode();
                    SpotifyApi api = new SpotifyApi();
                    SpotifyService spotify = api.getService();

                    Map<String, Object> options = new HashMap<>();
                    options.put("country", country);
                    results = spotify.getArtistTopTrack(artistIds[0], options);
                } catch (RetrofitError ex) {
                    toastConnectionError();
                }
            } else {
                results.tracks = new ArrayList<>();
                toastNoNetwork();
            }

            return results;
        }

        @Override
        protected void onPostExecute(Tracks results) {
            Log.i(TAG, "getArtistTopTrack() returned Tracks");
            updateAdapter(results.tracks);
        }
    }

}
