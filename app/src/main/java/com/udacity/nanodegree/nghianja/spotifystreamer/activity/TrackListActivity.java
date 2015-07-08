package com.udacity.nanodegree.nghianja.spotifystreamer.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.adapter.TrackArrayAdapter;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.GetArtistTopTrackEvent;
import com.udacity.nanodegree.nghianja.spotifystreamer.fragment.SettingsFragment;
import com.udacity.nanodegree.nghianja.spotifystreamer.fragment.TrackListFragment;
import com.udacity.nanodegree.nghianja.spotifystreamer.task.GetArtistTopTrackTask;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * References:
 * [1] http://stackoverflow.com/questions/15392261/android-pass-dataextras-to-a-fragment
 */
public class TrackListActivity extends Activity {

    private static final String TAG = "TrackListActivity";
    private TrackListFragment trackFragment;
    private String artistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpotifyStreamerApp.bus.register(this);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_track_list);

        FragmentManager manager = getFragmentManager();
        trackFragment = (TrackListFragment) manager.findFragmentById(R.id.track_fragment);

        // Get the Spotify ID from the intent
        Intent intent = getIntent();
        artistId = intent.getStringExtra("SpotifyId");
        Log.d(TAG, "SpotifyId=" + artistId);
    }

    @Override
    protected void onDestroy() {
        SpotifyStreamerApp.bus.unregister(this);
        super.onDestroy();
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
        if (SpotifyStreamerApp.isNetworkAvailable(this)) {
            setProgressBarIndeterminateVisibility(true);
            GetArtistTopTrackTask task = new GetArtistTopTrackTask();
            task.execute(artistId, SpotifyStreamerApp.getCountryCode(this));
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateAdapter(List<Track> items) {
        TrackArrayAdapter adapter = (TrackArrayAdapter) trackFragment.getListAdapter();

        if (adapter == null) {
            Log.w(TAG, "adapter should not be null");
        } else {
            adapter.clear();
            adapter.addAll(items);
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onAsyncTaskExecute(GetArtistTopTrackEvent event) {
        Tracks results = event.getResults();
        List<Track> items = results.tracks;
        if (items == null) {
            Toast.makeText(this, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        } else {
            updateAdapter(items);
        }
        setProgressBarIndeterminateVisibility(false);
    }

}
