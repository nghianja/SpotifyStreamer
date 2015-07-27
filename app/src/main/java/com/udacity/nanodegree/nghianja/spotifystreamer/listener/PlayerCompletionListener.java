package com.udacity.nanodegree.nghianja.spotifystreamer.listener;

import android.media.MediaPlayer;

import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.PlayerCompletionEvent;

/**
 * Listener for MediaPlayer.setOnCompletionListener
 *
 * References:
 * [1] http://stackoverflow.com/questions/15635746/how-to-detect-song-playing-is-completed
 */
public class PlayerCompletionListener implements MediaPlayer.OnCompletionListener {
    @Override
    public void onCompletion(MediaPlayer mp) {
        SpotifyStreamerApp.bus.post(new PlayerCompletionEvent(mp.isLooping()));
    }
}
