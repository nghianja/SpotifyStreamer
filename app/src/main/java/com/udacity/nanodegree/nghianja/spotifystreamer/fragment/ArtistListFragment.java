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
import com.udacity.nanodegree.nghianja.spotifystreamer.parcelable.ArtistParcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;


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
 * [7] http://stackoverflow.com/questions/5412746/android-fragment-onrestoreinstancestate
 * [8] http://stackoverflow.com/questions/16976431/change-background-color-of-selected-item-on-a-listview
 */
public class ArtistListFragment extends ListFragment {

    private static final String TAG = "ArtistListFragment";
    private ArrayList<ArtistParcelable> artists;
    private ArtistArrayAdapter adapter;
    TrackListFragment tracksFragment;
    private boolean dualPane;
    private int currentPosition = 0;

    // this method is only called once for this fragment
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_searchable, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey("artists")) {
            artists = new ArrayList<>();
            ArtistParcelable artist = new ArtistParcelable(null, "Search for artists in action bar", null);
            artists.add(artist);
        } else {
            artists = savedInstanceState.getParcelableArrayList("artists");
        }

        adapter = new ArtistArrayAdapter(getActivity(), artists);
        setListAdapter(adapter);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View tracksFrame = getActivity().findViewById(R.id.tracks);
        dualPane = tracksFrame != null && tracksFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            currentPosition = savedInstanceState.getInt("choicePosition", 0);
        }

        if (dualPane) {
            // Make sure our UI is in the correct state.
            showDetails(currentPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("choicePosition", currentPosition);
        outState.putParcelableArrayList("artists", artists);
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
    public void showDetails(int index) {
        currentPosition = index;
        ArtistParcelable artist = adapter.getItem(index);

        if (dualPane) {
            // Check what fragment is currently shown, replace if needed.
            tracksFragment = (TrackListFragment) getFragmentManager().findFragmentById(R.id.tracks);
            if (tracksFragment == null) {
                // Make new fragment to show this selection.
                tracksFragment = TrackListFragment.newInstance(index, artist);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.replace(R.id.tracks, tracksFragment);
                transaction.commit();
            } else {
                tracksFragment.setArguments(index, artist);
                tracksFragment.getArtistTopTrack(getActivity());
            }
            if (artist.getId() != null && !artist.getId().equals("")) {
                adapter.setArtistId(artist.getId());
                adapter.notifyDataSetChanged();
            }
        } else {
            if (artist.getId() != null && !artist.getId().equals("")) {
                adapter.setArtistId(artist.getId());
                adapter.notifyDataSetChanged();
                // launch activity to display an artist's top tracks
                Intent intent = new Intent();
                intent.setClass(getActivity(), TrackListActivity.class);
                intent.putExtra("index", index);
                intent.putExtra("artist", artist);
                startActivity(intent);
            }
        }
    }

    public void updateAdapter(List<Artist> items) {
        ArrayList<ArtistParcelable> newArtists = new ArrayList<>();
        for (Artist item : items) {
            if (item.images != null && !item.images.isEmpty()) {
                Image large = null;
                Image small = null;
                Image base = null;
                for (Image image : item.images) {
                    switch (image.width) {
                        case 640:
                            large = image;
                            break;
                        case 300:
                            small = image;
                            break;
                        default:
                            base = image;
                    }
                }
                String url = (small != null) ? small.url : base.url;
                newArtists.add(new ArtistParcelable(item.id, item.name, url));
            } else {
                newArtists.add(new ArtistParcelable(item.id, item.name, null));
            }
        }
        adapter.clear();
        adapter.addAll(newArtists);
        adapter.notifyDataSetChanged();
        artists = newArtists;
    }

    @Subscribe
    public void onAsyncTaskExecute(SearchArtistEvent event) {
        ArtistsPager results = event.getResults();
        List<Artist> items = results.artists.items;
        if (items == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        } else {
            updateAdapter(items);
            if (tracksFragment != null) {
                tracksFragment.resetAdapter();
            }
        }
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

}
