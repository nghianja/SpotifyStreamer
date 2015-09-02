package com.udacity.nanodegree.nghianja.spotifystreamer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerConstants;
import com.udacity.nanodegree.nghianja.spotifystreamer.activity.ArtistListActivity;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.ChangeSettingsEvent;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.PlayerPreparedEvent;
import com.udacity.nanodegree.nghianja.spotifystreamer.listener.PlayerCompletionListener;
import com.udacity.nanodegree.nghianja.spotifystreamer.listener.PlayerPreparedListener;
import com.udacity.nanodegree.nghianja.spotifystreamer.parcelable.TrackParcelable;

import java.io.IOException;

/**
 * Implementation of Service for playing media asynchronously.
 * <p/>
 * References:
 * [1] http://www.vogella.com/tutorials/AndroidServices/article.html
 * [2] http://stackoverflow.com/questions/22485298/stopself-vs-stopselfint-vs-stopserviceintent
 * [3] http://stackoverflow.com/questions/15431768/how-to-send-event-from-service-to-activity-with-otto-event-bus
 * [4] http://stackoverflow.com/questions/14709462/home-button-pressed
 * [5] http://www.truiton.com/2014/10/android-foreground-service-example/
 * [6] http://stackoverflow.com/questions/5528288/how-do-i-update-the-notification-text-for-a-foreground-service-in-android
 * [7] http://stackoverflow.com/questions/26888247/easiest-way-to-use-picasso-in-notification-icon
 */
public class PlayerService extends Service {

    private static final String TAG = "PlayerService";

    // Binder given to clients
    private final IBinder binder = new PlayerBinder();

    private boolean isPrepared = false;
    private String dataSource;
    private MediaPlayer mediaPlayer;
    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;

    /**
     * Class used to load bitmap image via Picasso
     */
    public class IconTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setLargeIcon(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_spotifier));
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

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
        if (intent.getAction().equals(SpotifyStreamerConstants.ACTION.START_ACTION)) {
            mediaPlayer.setOnPreparedListener(new PlayerPreparedListener());
            mediaPlayer.setOnCompletionListener(new PlayerCompletionListener());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            setForeground();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        SpotifyStreamerApp.bus.register(this);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onDestroy() {
        SpotifyStreamerApp.bus.unregister(this);
        if (mediaPlayer != null) mediaPlayer.release();
    }

    @Subscribe
    public void onPrepared(PlayerPreparedEvent event) {
        updateNotificationBuilder();
        notificationManager.notify(SpotifyStreamerConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notificationBuilder.build());
        isPrepared = true;
        mediaPlayer.start();
    }

    @Subscribe
    public void onSettingsChanged(ChangeSettingsEvent event) {
        if (event.getKey().equals(getString(R.string.pref_key_notification))) {
            setForeground();
        }
    }

    /**
     * methods for clients
     */
    public void preparePlayer(String dataSource) throws IOException {
        isPrepared = false;
        mediaPlayer.reset();
        mediaPlayer.setDataSource(dataSource);
        mediaPlayer.prepareAsync();
        this.dataSource = dataSource;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public String getDataSource() {
        if (isPrepared) {
            return dataSource;
        } else {
            return "";
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setForeground() {
        if (SpotifyStreamerApp.checkNotificationControls(this)) {
            Intent notificationIntent = new Intent(this, ArtistListActivity.class);
            notificationIntent.setAction(SpotifyStreamerConstants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Intent previousIntent = new Intent(this, PlayerService.class);
            previousIntent.setAction(SpotifyStreamerConstants.ACTION.PREV_ACTION);
            PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

            Intent playIntent = new Intent(this, PlayerService.class);
            playIntent.setAction(SpotifyStreamerConstants.ACTION.PLAY_ACTION);
            PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

            Intent nextIntent = new Intent(this, PlayerService.class);
            nextIntent.setAction(SpotifyStreamerConstants.ACTION.NEXT_ACTION);
            PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_spotifier);

            notificationBuilder = new Notification.Builder(this)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setSmallIcon(R.drawable.ic_music_note_white)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_previous, "Previous", ppreviousIntent)
                    .addAction(android.R.drawable.ic_media_play, "Play", pplayIntent)
                    .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent);
            updateNotificationBuilder();

            startForeground(SpotifyStreamerConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notificationBuilder.build());
        } else {
            stopForeground(true);
        }
    }

    private void updateNotificationBuilder() {
        String trackName;
        String artistName;
        if (SpotifyStreamerApp.tracks == null) {
            trackName = "Track Name";
            artistName = "Artist Name";
        } else {
            TrackParcelable track = SpotifyStreamerApp.tracks.get(SpotifyStreamerApp.index);
            Log.d(TAG, track.getImageSmall());
            Picasso.with(this).load(track.getImageSmall()).into(new IconTarget());
            trackName = track.getTrackName();
            artistName = track.getArtistName();
        }

        notificationBuilder
                .setTicker(trackName)
                .setContentTitle(trackName)
                .setContentText(artistName);
    }

    private void setLargeIcon(Bitmap icon) {
        notificationBuilder.setLargeIcon(icon);
        notificationManager.notify(SpotifyStreamerConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notificationBuilder.build());
    }

}
