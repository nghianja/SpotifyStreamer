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
import com.udacity.nanodegree.nghianja.spotifystreamer.event.PlayerCompletionEvent;
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
 * [8] http://www.binpress.com/tutorial/using-android-media-style-notifications-with-media-session-controls/165
 */
public class PlayerService extends Service {

    private static final String TAG = "PlayerService";

    // Binder given to clients
    private final IBinder binder = new PlayerBinder();

    private boolean isPrepared = false;
    private String dataSource;
    private MediaPlayer mediaPlayer;
//    private MediaSessionManager mediaSessionManager;
//    private MediaSession mediaSession;
//    private MediaController mediaController;
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
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            initMediaSessions();
            createNotificationBuilder(android.R.drawable.ic_media_play, "Play", SpotifyStreamerConstants.ACTION.PLAY_ACTION);
            setForeground();
        }
        handleIntent(intent);
        return START_NOT_STICKY;
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
        isPrepared = true;
        mediaPlayer.start();
        createNotificationBuilder(android.R.drawable.ic_media_pause, "Pause", SpotifyStreamerConstants.ACTION.PAUSE_ACTION);
        notificationManager.notify(SpotifyStreamerConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notificationBuilder.build());
    }

    @Subscribe
    public void onCompletion(PlayerCompletionEvent event) {
        createNotificationBuilder(android.R.drawable.ic_media_play, "Play", SpotifyStreamerConstants.ACTION.PLAY_ACTION);
        notificationManager.notify(SpotifyStreamerConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notificationBuilder.build());
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
            startForeground(SpotifyStreamerConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notificationBuilder.build());
        } else {
            stopForeground(true);
        }
    }

    private void initMediaSessions() {
        mediaPlayer.setOnPreparedListener(new PlayerPreparedListener());
        mediaPlayer.setOnCompletionListener(new PlayerCompletionListener());
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

//        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
//        mediaSession = new MediaSession(getApplicationContext(), "simple player session");
//        mediaController = new MediaController(getApplicationContext(), mediaSession.getSessionToken());
    }

    private void createNotificationBuilder(int icon, String title, String intentAction) {
        Intent notificationIntent = new Intent(this, ArtistListActivity.class);
        notificationIntent.setAction(SpotifyStreamerConstants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_spotifier);

        notificationBuilder = new Notification.Builder(this)
                .setLargeIcon(Bitmap.createScaledBitmap(bitmap, 128, 128, false))
                .setSmallIcon(R.drawable.ic_music_note_white)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous, "Previous", getPendingIntent(SpotifyStreamerConstants.ACTION.PREV_ACTION))
                .addAction(icon, title, getPendingIntent(intentAction))
                .addAction(android.R.drawable.ic_media_next, "Next", getPendingIntent(SpotifyStreamerConstants.ACTION.NEXT_ACTION));
        updateNotificationBuilder();
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

    private PendingIntent getPendingIntent(String intentAction) {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(intentAction);
        return PendingIntent.getService(this, 0, intent, 0);
    }

    private void setLargeIcon(Bitmap icon) {
        notificationBuilder.setLargeIcon(icon);
        notificationManager.notify(SpotifyStreamerConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notificationBuilder.build());
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null || SpotifyStreamerApp.tracks == null)
            return;

        String action = intent.getAction();

        if (action.equals(SpotifyStreamerConstants.ACTION.PREV_ACTION)) {
            if (SpotifyStreamerApp.index > 0) {
                SpotifyStreamerApp.index = SpotifyStreamerApp.index - 1;
            } else {
                SpotifyStreamerApp.index = SpotifyStreamerApp.tracks.size() - 1;
            }
            try {
                preparePlayer(SpotifyStreamerApp.tracks.get(SpotifyStreamerApp.index).getPreviewUrl());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                SpotifyStreamerApp.tracks = null;
                SpotifyStreamerApp.index = 0;
            }
        } else if (action.equals(SpotifyStreamerConstants.ACTION.PAUSE_ACTION)) {
            if (isPrepared && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                createNotificationBuilder(android.R.drawable.ic_media_play, "Play", SpotifyStreamerConstants.ACTION.PLAY_ACTION);
                notificationManager.notify(SpotifyStreamerConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notificationBuilder.build());
            }
        } else if (action.equals(SpotifyStreamerConstants.ACTION.PLAY_ACTION)) {
            if (isPrepared) {
                mediaPlayer.start();
                createNotificationBuilder(android.R.drawable.ic_media_pause, "Pause", SpotifyStreamerConstants.ACTION.PAUSE_ACTION);
                notificationManager.notify(SpotifyStreamerConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notificationBuilder.build());
            }
        } else if (action.equals(SpotifyStreamerConstants.ACTION.NEXT_ACTION)) {
            if (SpotifyStreamerApp.index < SpotifyStreamerApp.tracks.size() - 1) {
                SpotifyStreamerApp.index = SpotifyStreamerApp.index + 1;
            } else {
                SpotifyStreamerApp.index = 0;
            }
            try {
                preparePlayer(SpotifyStreamerApp.tracks.get(SpotifyStreamerApp.index).getPreviewUrl());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                SpotifyStreamerApp.tracks = null;
                SpotifyStreamerApp.index = 0;
            }
        }
    }

}
