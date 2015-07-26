package com.udacity.nanodegree.nghianja.spotifystreamer.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.parcelable.TrackParcelable;

/**
 * Implementation of DialogFragment for playing the track preview stream of a currently selected track.
 *
 * References
 * [1] http://mrbool.com/how-to-play-audio-files-in-android-with-a-seekbar-feature-and-mediaplayer-class/28243
 * [2] http://code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778
 */
public class PlayerFragment extends DialogFragment {

    private static final String TAG = "PlayerFragment";
    private MediaPlayer mediaPlayer;

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
        TrackParcelable track = getTrack();
        TextView play_artist = (TextView) v.findViewById(R.id.play_artist);
        TextView play_album = (TextView) v.findViewById(R.id.play_album);
        TextView play_track = (TextView) v.findViewById(R.id.play_track);
        play_artist.setText(track.getArtistName());
        play_album.setText(track.getAlbumName());
        play_track.setText(track.getTrackName());
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
    public void onResume() {
        super.onResume();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.release();
        mediaPlayer = null;
    }

}
