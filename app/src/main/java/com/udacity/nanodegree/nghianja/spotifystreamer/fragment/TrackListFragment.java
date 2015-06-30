package com.udacity.nanodegree.nghianja.spotifystreamer.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.activity.TrackListActivity;
import com.udacity.nanodegree.nghianja.spotifystreamer.adapter.TrackArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


/**
 * A placeholder fragment containing a list view, representing a list of Items.
 *
 * References:
 * [1] http://www.vogella.com/tutorials/AndroidListView/article.html
 * [2] http://stackoverflow.com/questions/15392261/android-pass-dataextras-to-a-fragment
 */
public class TrackListFragment extends ListFragment {

    private static final String TAG = "TrackListFragment";
    private TrackArrayAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrackListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_toptracks, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // setRetainInstance(true);

        if (savedInstanceState == null) {
            List<Track> tracks = new ArrayList<>();
            Track track = new Track();
            track.name = "Loading top tracks...";
            tracks.add(track);
            adapter = new TrackArrayAdapter(getActivity(), tracks);
        }

        setListAdapter(adapter);
        ((TrackListActivity) getActivity()).getArtistTopTrack();
    }

}
