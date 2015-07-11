package com.udacity.nanodegree.nghianja.spotifystreamer.fragment;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.activity.TrackListActivity;
import com.udacity.nanodegree.nghianja.spotifystreamer.adapter.ArtistArrayAdapter;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.SearchArtistEvent;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a list view, representing a list of Items.
 * <p/>
 * References:
 * [1] http://www.vogella.com/tutorials/AndroidListView/article.html
 * [2] http://stackoverflow.com/questions/13305861/fool-proof-way-to-handle-fragment-on-orientation-change
 * [3] http://stackoverflow.com/questions/15313598/once-for-all-how-to-correctly-save-instance-state-of-fragments-in-back-stack
 * [4] http://inthecheesefactory.com/blog/fragment-state-saving-best-practices/en
 * [5] http://stackoverflow.com/questions/10463560/retaining-list-in-list-fragment-on-orientation-change
 * [6] http://stackoverflow.com/questions/12503836/how-to-save-custom-arraylist-on-android-screen-rotate
 */
public class ArtistListFragment extends ListFragment {

    private static final String TAG = "ArtistListFragment";
    private ArtistArrayAdapter adapter;
    private boolean dualPane;
    private int currentPosition = 0;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setRetainInstance(true);
        SpotifyStreamerApp.bus.register(this);
    }

    @Override
    public void onDestroy() {
        SpotifyStreamerApp.bus.unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_searchable, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            List<Artist> artists = new ArrayList<>();
            Artist artist = new Artist();
            artist.name = "Search for artists in action bar";
            artists.add(artist);
            adapter = new ArtistArrayAdapter(getActivity(), artists);
        } else {
            // Restore last state for checked position.
            currentPosition = savedInstanceState.getInt("choicePosition", 0);
        }

        setListAdapter(adapter);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View tracksFrame = getActivity().findViewById(R.id.tracks);
        dualPane = tracksFrame != null && tracksFrame.getVisibility() == View.VISIBLE;

        if (dualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(currentPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("choicePosition", currentPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "position=" + position);
        showDetails(position);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        currentPosition = index;
        Artist artist = adapter.getItem(index);

        if (dualPane) {
            if (artist.id != null && !artist.id.equals("")) {
                // We can display everything in-place with fragments, so update
                // the list to highlight the selected item and show the data.
                getListView().setItemChecked(index, true);
            }

            // Check what fragment is currently shown, replace if needed.
            TrackListFragment tracksFragment = (TrackListFragment)
                    getFragmentManager().findFragmentById(R.id.tracks);
            if (tracksFragment == null) {
                // Make new fragment to show this selection.
                tracksFragment = TrackListFragment.newInstance(index, artist.id);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.tracks, tracksFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            } else if (tracksFragment.getShownIndex() != index || index == 0) {
                tracksFragment.getArtistTopTrack(getActivity(), artist.id);
            }
        } else {
            if (artist.id != null && !artist.id.equals("")) {
                // launch activity to display an artist's top tracks
                Intent intent = new Intent();
                intent.setClass(getActivity(), TrackListActivity.class);
                intent.putExtra("index", index);
                intent.putExtra("SpotifyId", artist.id);
                startActivity(intent);
            }
        }
    }

    public void updateAdapter(List<Artist> items) {
        adapter.clear();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onAsyncTaskExecute(SearchArtistEvent event) {
        ArtistsPager results = event.getResults();
        List<Artist> items = results.artists.items;
        if (items == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        } else {
            updateAdapter(items);
        }
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

}
