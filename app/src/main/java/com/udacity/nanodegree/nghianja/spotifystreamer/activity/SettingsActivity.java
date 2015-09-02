package com.udacity.nanodegree.nghianja.spotifystreamer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.ChangeSettingsEvent;
import com.udacity.nanodegree.nghianja.spotifystreamer.fragment.SettingsFragment;

/**
 * References:
 * [1] http://stackoverflow.com/questions/13941276/i-made-confuse-about-navigation-between-navigateupto-and-using-intent
 * [2] http://stackoverflow.com/questions/19184154/dynamically-set-parent-activity
 */
public class SettingsActivity extends Activity implements SettingsFragment.OnSettingsChangedListener {

    private static final String TAG = "SettingsActivity";
    private String pref_key_country = null;
    private String pref_key_notification = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (pref_key_country != null) {
            SpotifyStreamerApp.bus.post(new ChangeSettingsEvent(pref_key_country));
        }
        if (pref_key_notification != null) {
            SpotifyStreamerApp.bus.post(new ChangeSettingsEvent(pref_key_notification));
        }
        finish();
    }

    @Override
    public void onSettingsChanged(String key) {
        if (key == getString(R.string.pref_key_country)) {
            pref_key_country = key;
        } else if (key == getString(R.string.pref_key_notification)) {
            pref_key_notification = key;
        }
    }

}
