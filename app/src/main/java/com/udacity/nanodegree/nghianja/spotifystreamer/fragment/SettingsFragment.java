package com.udacity.nanodegree.nghianja.spotifystreamer.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.udacity.nanodegree.nghianja.spotifystreamer.R;

/**
 * Implementation of PreferenceFragment for specifying the country code setting.
 *
 * References:
 * [1] http://stackoverflow.com/questions/13596250/how-to-listen-for-preference-changes-within-a-preferencefragment
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsFragment";
    private OnSettingsChangedListener listener;

    // Container Activity must implement this interface
    public interface OnSettingsChangedListener {
        public void onSettingsChanged(String key);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnSettingsChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSettingsChangedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_country))) {
            Preference connectionPref = findPreference(key);
            String country = sharedPreferences.getString(key, "");
            if (country.equals("")) {
                connectionPref.setSummary("System Default");
            } else {
                connectionPref.setSummary(country);
            }
        }
        listener.onSettingsChanged(key);
    }

}
