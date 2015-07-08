package com.udacity.nanodegree.nghianja.spotifystreamer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.udacity.nanodegree.nghianja.spotifystreamer.fragment.SettingsFragment;

/**
 * Class for global variables.
 *
 * References:
 * [1] http://www.vogella.com/tutorials/JavaLibrary-EventBusOtto/article.html
 */
public class SpotifyStreamerApp extends Application {

    public static Bus bus = new Bus();

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getCountryCode(Activity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        String country = sharedPref.getString(SettingsFragment.KEY_PREF_COUNTRY, "");
        if (country.equals("")) {
            country = activity.getResources().getConfiguration().locale.getCountry();
        }
        return country;
    }

}
