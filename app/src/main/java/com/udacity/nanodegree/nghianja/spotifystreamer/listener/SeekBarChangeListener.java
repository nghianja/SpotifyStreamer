package com.udacity.nanodegree.nghianja.spotifystreamer.listener;

import android.widget.SeekBar;

import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.SeekBarChangeEvent;

/**
 * Listener for SeekBar.OnSeekBarChangeListener.
 *
 * References:
 * [1] http://stackoverflow.com/questions/8956218/android-seekbar-setonseekbarchangelistener
 */
public class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            SpotifyStreamerApp.bus.post(new SeekBarChangeEvent(SeekBarChangeEvent.Callback.PROGRESS_CHANGED, progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        SpotifyStreamerApp.bus.post(new SeekBarChangeEvent(SeekBarChangeEvent.Callback.START_TRACKING_TOUCH));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        SpotifyStreamerApp.bus.post(new SeekBarChangeEvent(SeekBarChangeEvent.Callback.STOP_TRACKING_TOUCH));
    }
}
