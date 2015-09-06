package com.udacity.nanodegree.nghianja.spotifystreamer;

import android.app.Activity;
import android.app.Application;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Bus;
import com.udacity.nanodegree.nghianja.spotifystreamer.fragment.PlayerFragment;
import com.udacity.nanodegree.nghianja.spotifystreamer.parcelable.TrackParcelable;
import com.udacity.nanodegree.nghianja.spotifystreamer.service.PlayerService;

import java.util.ArrayList;

/**
 * Class for global variables.
 *
 * References:
 * [1] http://www.vogella.com/tutorials/JavaLibrary-EventBusOtto/article.html
 */
public class SpotifyStreamerApp extends Application {

    public static Bus bus = new Bus(/* ThreadEnforcer.ANY */);
    public static ArrayList<TrackParcelable> tracks = null;
    public static int index = 0;
    public static boolean nowPlaying = false;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getCountryCode(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_key_country), context.getResources().getConfiguration().locale.getCountry());
    }

    public static boolean checkNotificationControls(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(context.getString(R.string.pref_key_notification), false);
    }

    public static Intent getShareIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        if (tracks == null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        } else {
            TrackParcelable track = tracks.get(index);
            shareIntent.putExtra(Intent.EXTRA_TEXT, track.getUri());
        }

        return shareIntent;
    }

    public static void addNowPlaying(Menu menu) {
        if (menu != null) {
            MenuItem item = menu.add(Menu.NONE, R.id.now_playing, 10, R.string.now_playing);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            item.setIcon(android.R.drawable.ic_media_play);
        }
    }

    public static void removeNowPlaying(Menu menu) {
        if (menu != null) {
            menu.removeItem(R.id.now_playing);
        }
    }

    public static void showPlayer(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        DialogFragment newFragment = PlayerFragment.newInstance(index, tracks);

        if (activity.getResources().getBoolean(R.bool.has_two_panes)) {
            newFragment.show(fragmentManager, "dialog");
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, PlayerService.class);
        startService(intent);
    }

}
