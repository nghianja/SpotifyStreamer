package com.udacity.nanodegree.nghianja.spotifystreamer.listener;

import android.media.MediaPlayer;

import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.PlayerPreparedEvent;

/**
 * Listener for MediaPlayer.setOnPreparedListener
 *
 * References:
 * [1] http://stackoverflow.com/questions/10307131/android-mediaplayer-prepareasync-method
 */
public class PlayerPreparedListener implements MediaPlayer.OnPreparedListener {
    @Override
    public void onPrepared(MediaPlayer mp) {
        SpotifyStreamerApp.bus.post(new PlayerPreparedEvent(mp.getDuration()));
    }
}
