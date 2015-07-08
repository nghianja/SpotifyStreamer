package com.udacity.nanodegree.nghianja.spotifystreamer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.udacity.nanodegree.nghianja.spotifystreamer.fragment.SettingsFragment;

/**
 * References:
 * [1] http://stackoverflow.com/questions/13941276/i-made-confuse-about-navigation-between-navigateupto-and-using-intent
 * [2] http://stackoverflow.com/questions/19184154/dynamically-set-parent-activity
 */
public class SettingsActivity extends Activity {

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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
