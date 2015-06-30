package com.udacity.nanodegree.nghianja.spotifystreamer.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.activity.TrackListActivity;
import com.udacity.nanodegree.nghianja.spotifystreamer.adapter.ArtistArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;


/**
 * A placeholder fragment containing a list view, representing a list of Items.
 *
 * References:
 * [1] http://www.vogella.com/tutorials/AndroidListView/article.html
 * [2] http://stackoverflow.com/questions/13305861/fool-proof-way-to-handle-fragment-on-orientation-change
 * [3] http://stackoverflow.com/questions/15313598/once-for-all-how-to-correctly-save-instance-state-of-fragments-in-back-stack
 * [4] http://inthecheesefactory.com/blog/fragment-state-saving-best-practices/en
 * [5] http://stackoverflow.com/questions/10463560/retaining-list-in-list-fragment-on-orientation-change
 * [6] http://stackoverflow.com/questions/12503836/how-to-save-custom-arraylist-on-android-screen-rotate
 */
public class MainActivityFragment extends ListFragment {

    private static final String TAG = "MainActivityFragment";
    private ArtistArrayAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_searchable, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // setRetainInstance(true);

        if (savedInstanceState == null) {
            List<Artist> artists = new ArrayList<>();
            Artist artist = new Artist();
            artist.name = "Search for artists in action bar";
            artists.add(artist);
            adapter = new ArtistArrayAdapter(getActivity(), artists);
        }

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Log.d(TAG, "position=" + position);
        Artist artist = adapter.getItem(position);

        // launch activity to display an artist's top tracks
        Intent intent = new Intent();
        intent.setClass(getActivity(), TrackListActivity.class);
        intent.putExtra("SpotifyId", artist.id);

        startActivity(intent);
    }

}
