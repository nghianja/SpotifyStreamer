package com.udacity.nanodegree.nghianja.spotifystreamer.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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

import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.parcelable.TrackParcelable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of DialogFragment for playing the track preview stream of a currently selected track.
 *
 * References
 * [1] http://mrbool.com/how-to-play-audio-files-in-android-with-a-seekbar-feature-and-mediaplayer-class/28243
 * [2] http://code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778
 * [3] http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
 * [4] http://stackoverflow.com/questions/10307131/android-mediaplayer-prepareasync-method
 * [5] http://stackoverflow.com/questions/12112061/handle-button-clicks-in-a-dialogfragment
 */
public class PlayerFragment extends DialogFragment {

    private static final String TAG = "PlayerFragment";
    private SeekBar playSeeker;
    private TextView playEnd;
    private ImageButton playPause;
    private MediaPlayer mediaPlayer;
    private Thread playThread;

    public static PlayerFragment newInstance(int index, TrackParcelable track) {
        PlayerFragment f = new PlayerFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putParcelable("track", track);
        f.setArguments(args);

        return f;
    }

    public void setArguments(int index, TrackParcelable track) {
        getArguments().putInt("index", index);
        getArguments().putParcelable("track", track);
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    public TrackParcelable getTrack() {
        return getArguments().getParcelable("track");
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player, container, false);

        playSeeker = (SeekBar) v.findViewById(R.id.play_seeker);
        playEnd = (TextView) v.findViewById(R.id.play_end);
        playPause = (ImageButton) v.findViewById(R.id.play_pause);

        TrackParcelable track = getTrack();
        TextView playArtist = (TextView) v.findViewById(R.id.play_artist);
        TextView playAlbum = (TextView) v.findViewById(R.id.play_album);
        TextView playTrack = (TextView) v.findViewById(R.id.play_track);

        playArtist.setText(track.getArtistName());
        playAlbum.setText(track.getAlbumName());
        playTrack.setText(track.getTrackName());
        if (track.getImageLarge() != null) {
            Picasso.with(getActivity()).load(Uri.parse(track.getImageLarge())).into((ImageView) v.findViewById(R.id.play_artwork));
        }

        return v;
    }

    /** The system calls this only when creating the layout in a dialog. */
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
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int duration = mp.getDuration();
                String endText = String.format("%01d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
                Toast.makeText(getActivity(), endText + " preview loaded", Toast.LENGTH_SHORT).show();

                playSeeker.setMax(duration);
                playEnd.setText(endText);
                playPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.start();
                    }
                });
            }
        });
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Log.d(TAG, "previewUrl=" + getTrack().getPreviewUrl());
            mediaPlayer.setDataSource(getTrack().getPreviewUrl());
            Log.i(TAG, "preparing MediaPlayer...");
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            Toast.makeText(getActivity(), getResources().getString(R.string.no_preview), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

}
