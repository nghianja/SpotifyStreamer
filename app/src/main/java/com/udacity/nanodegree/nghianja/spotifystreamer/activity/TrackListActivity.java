package com.udacity.nanodegree.nghianja.spotifystreamer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.fragment.TrackListFragment;

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
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        if (getResources().getBoolean(R.bool.has_two_panes)) {
            finish();
            return;
        }

        // Get the Spotify ID from the intent
        Intent intent = getIntent();
        artistId = intent.getStringExtra("SpotifyId");
        Log.d(TAG, "SpotifyId=" + artistId);

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            trackFragment = new TrackListFragment();
            trackFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, trackFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track, menu);
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
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
