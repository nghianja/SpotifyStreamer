package com.udacity.nanodegree.nghianja.spotifystreamer.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.PlayerCompletionEvent;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.PlayerPreparedEvent;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.SeekBarChangeEvent;
import com.udacity.nanodegree.nghianja.spotifystreamer.listener.PlayerCompletionListener;
import com.udacity.nanodegree.nghianja.spotifystreamer.listener.PlayerPreparedListener;
import com.udacity.nanodegree.nghianja.spotifystreamer.listener.SeekBarChangeListener;
import com.udacity.nanodegree.nghianja.spotifystreamer.parcelable.TrackParcelable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of DialogFragment for playing the track preview stream of a currently selected track.
 * <p/>
 * References
 * [1] http://mrbool.com/how-to-play-audio-files-in-android-with-a-seekbar-feature-and-mediaplayer-class/28243
 * [2] http://code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778
 * [3] http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
 * [4] http://stackoverflow.com/questions/10307131/android-mediaplayer-prepareasync-method
 * [5] http://stackoverflow.com/questions/15635746/how-to-detect-song-playing-is-completed
 * [6] http://stackoverflow.com/questions/12112061/handle-button-clicks-in-a-dialogfragment
 * [7] http://stackoverflow.com/questions/8956218/android-seekbar-setonseekbarchangelistener
 * [8] http://stackoverflow.com/questions/17439252/how-to-pause-handler-postdelayed-timer-on-android
 */
public class PlayerFragment extends DialogFragment {

    private static final String TAG = "PlayerFragment";
    private long durationMM;
    private long durationSS;
    private TextView playArtist;
    private TextView playAlbum;
    private ImageView playArtwork;
    private TextView playTrack;
    private SeekBar playSeeker;
    private TextView playStart;
    private TextView playEnd;
    private ImageButton playPrevious;
    private ImageButton playPause;
    private ImageButton playNext;
    private MediaPlayer mediaPlayer;
    private Handler seekHandler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
        }
    };
    private boolean isPrepared = false;

    public static PlayerFragment newInstance(int index, ArrayList<TrackParcelable> tracks) {
        PlayerFragment f = new PlayerFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putParcelableArrayList("tracks", tracks);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    public ArrayList<TrackParcelable> getTracks() {
        return getArguments().getParcelableArrayList("tracks");
    }

    public TrackParcelable getTrack() {
        return getTracks().get(getShownIndex());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpotifyStreamerApp.bus.register(this);
    }

    @Override
    public void onDestroy() {
        SpotifyStreamerApp.bus.unregister(this);
        super.onDestroy();
    }

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player, container, false);

        playArtist = (TextView) v.findViewById(R.id.play_artist);
        playAlbum = (TextView) v.findViewById(R.id.play_album);
        playArtwork = (ImageView) v.findViewById(R.id.play_artwork);
        playTrack = (TextView) v.findViewById(R.id.play_track);
        playSeeker = (SeekBar) v.findViewById(R.id.play_seeker);
        playStart = (TextView) v.findViewById(R.id.play_start);
        playEnd = (TextView) v.findViewById(R.id.play_end);
        playPrevious = (ImageButton) v.findViewById(R.id.play_previous);
        playPause = (ImageButton) v.findViewById(R.id.play_pause);
        playNext = (ImageButton) v.findViewById(R.id.play_next);

        playSeeker.setOnSeekBarChangeListener(new SeekBarChangeListener());
        playPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = getShownIndex();
                ArrayList<TrackParcelable> tracks = getTracks();
                if (index > 0) {
                    getArguments().putInt("index", index - 1);
                } else {
                    getArguments().putInt("index", tracks.size() - 1);
                }
                changeTrack();
            }
        });
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPrepared) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playPause.setImageResource(android.R.drawable.ic_media_play);
                    } else {
                        mediaPlayer.start();
                        playPause.setImageResource(android.R.drawable.ic_media_pause);
                    }
                } else {
                    Toast.makeText(getActivity(), "preview not loaded yet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        playNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = getShownIndex();
                ArrayList<TrackParcelable> tracks = getTracks();
                if (index < tracks.size() - 1) {
                    getArguments().putInt("index", index + 1);
                } else {
                    getArguments().putInt("index", 0);
                }
                changeTrack();
            }
        });
        updateViews();

        return v;
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new PlayerPreparedListener());
        mediaPlayer.setOnCompletionListener(new PlayerCompletionListener());
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        preparePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (seekHandler == null) {
            seekHandler = new Handler();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        seekHandler.removeCallbacks(runnable);
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void updateViews() {
        TrackParcelable track = getTrack();

        playArtist.setText(track.getArtistName());
        playAlbum.setText(track.getAlbumName());
        if (track.getImageLarge() != null) {
            Picasso.with(getActivity()).load(Uri.parse(track.getImageLarge())).into(playArtwork);
        }
        playTrack.setText(track.getTrackName());
        playSeeker.setMax(0);
        playStart.setText("0:00");
        playEnd.setText("0:00");
        playPause.setImageResource(android.R.drawable.ic_media_play);
    }

    public void updateSeekBar() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        long currentMM = TimeUnit.MILLISECONDS.toMinutes(currentPosition);
        long currentSS = TimeUnit.MILLISECONDS.toSeconds(currentPosition) % TimeUnit.MINUTES.toSeconds(1);
        playSeeker.setProgress(currentPosition);
        playStart.setText(String.format("%01d:%02d", currentMM, currentSS));
        playEnd.setText(String.format("%01d:%02d", durationMM - currentMM, durationSS - currentSS));
        seekHandler.postDelayed(runnable, 100);
    }

    public void preparePlayer() {
        try {
            isPrepared = false;
            Log.d(TAG, "previewUrl=" + getTrack().getPreviewUrl());
            mediaPlayer.setDataSource(getTrack().getPreviewUrl());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            Toast.makeText(getActivity(), getResources().getString(R.string.no_preview), Toast.LENGTH_SHORT).show();
        }
    }

    public void changeTrack() {
        seekHandler.removeCallbacks(runnable);
        mediaPlayer.reset();
        updateViews();
        preparePlayer();
    }

    @Subscribe
    public void onPrepared(PlayerPreparedEvent event) {
        isPrepared = true;
        playSeeker.setMax(event.getDuration());
        playEnd.setText(event.getEndText());
        Toast.makeText(getActivity(), event.getEndText() + " preview loaded", Toast.LENGTH_SHORT).show();
        durationMM = TimeUnit.MILLISECONDS.toMinutes(event.getDuration());
        durationSS = TimeUnit.MILLISECONDS.toSeconds(event.getDuration()) % TimeUnit.MINUTES.toSeconds(1);
        updateSeekBar();
    }

    @Subscribe
    public void onCompletion(PlayerCompletionEvent event) {
        if (!event.isLooping()) {
            playPause.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Subscribe
    public void onSeekBarChanged(SeekBarChangeEvent event) {
        switch (event.getCallback()) {
            case PROGRESS_CHANGED:
                mediaPlayer.seekTo(event.getProgress());
                break;
            case START_TRACKING_TOUCH:
                seekHandler.removeCallbacks(runnable);
                break;
            case STOP_TRACKING_TOUCH:
                seekHandler.postDelayed(runnable, 100);
                break;
        }
    }

}
