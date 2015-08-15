package com.udacity.nanodegree.nghianja.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.udacity.nanodegree.nghianja.spotifystreamer.listener.PlayerCompletionListener;
import com.udacity.nanodegree.nghianja.spotifystreamer.listener.PlayerPreparedListener;

/**
 * Implementation of Service for playing media asynchronously.
 *
 * References:
 * [1] http://www.vogella.com/tutorials/AndroidServices/article.html
 * [2] http://stackoverflow.com/questions/22485298/stopself-vs-stopselfint-vs-stopserviceintent
 * [3] http://stackoverflow.com/questions/15431768/how-to-send-event-from-service-to-activity-with-otto-event-bus
 */
public class PlayerService extends Service {

    private static final String TAG = "PlayerService";

    // Binder given to clients
    private final IBinder binder = new PlayerBinder();

    private String dataSource = "";
    private MediaPlayer mediaPlayer;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class PlayerBinder extends Binder {
        public PlayerService getService() {
            // Return this instance of PlayerService so clients can call public methods
            return PlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.setOnPreparedListener(new PlayerPreparedListener());
        mediaPlayer.setOnCompletionListener(new PlayerCompletionListener());
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        // SpotifyStreamerApp.bus.register(this);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onDestroy() {
        // SpotifyStreamerApp.bus.unregister(this);
        if (mediaPlayer != null) mediaPlayer.release();
    }

    /*
    @Subscribe
    public void onPrepared(PlayerPreparedEvent event) {}

    @Subscribe
    public void onCompletion(PlayerCompletionEvent event) {}
    */

    /** methods for clients */
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public String getDataSource() { return dataSource; }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

}