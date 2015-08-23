package com.udacity.nanodegree.nghianja.spotifystreamer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ShareActionProvider;

import com.squareup.otto.Subscribe;
import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.NowPlayingEvent;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.PlayerPreparedEvent;
import com.udacity.nanodegree.nghianja.spotifystreamer.fragment.TrackListFragment;

/**
 * References:
 * [1] http://stackoverflow.com/questions/15392261/android-pass-dataextras-to-a-fragment
 * [2] http://stackoverflow.com/questions/21352571/android-how-do-i-check-if-dialogfragment-is-showing
 */
public class TrackListActivity extends Activity {

    private static final String TAG = "TrackListActivity";
    private TrackListFragment trackFragment;
    private String artistId;
    private Menu menu;
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpotifyStreamerApp.bus.register(this);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
    protected void onDestroy() {
        SpotifyStreamerApp.bus.unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track, menu);

        MenuItem item = menu.findItem(R.id.share);
        shareActionProvider = (ShareActionProvider) item.getActionProvider();
        shareActionProvider.setShareIntent(SpotifyStreamerApp.getShareIntent());

        if (SpotifyStreamerApp.nowPlaying) {
            SpotifyStreamerApp.addNowPlaying(menu);
        }

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.now_playing:
                SpotifyStreamerApp.showPlayer(this);
                SpotifyStreamerApp.removeNowPlaying(menu);
                SpotifyStreamerApp.nowPlaying = false;
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onPlayerFragmentDetach(NowPlayingEvent event) {
        Log.d(TAG, "event.getAction=" + event.getAction());
        if (event.getAction() == NowPlayingEvent.Action.ADD) {
            SpotifyStreamerApp.addNowPlaying(menu);
            SpotifyStreamerApp.nowPlaying = true;
        }
    }

    @Subscribe
    public void onPrepared(PlayerPreparedEvent event) {
        shareActionProvider.setShareIntent(SpotifyStreamerApp.getShareIntent());
    }

}
