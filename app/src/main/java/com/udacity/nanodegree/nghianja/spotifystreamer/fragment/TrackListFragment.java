package com.udacity.nanodegree.nghianja.spotifystreamer.fragment;

import android.app.Activity;
import android.app.ListFragment;
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
import com.udacity.nanodegree.nghianja.spotifystreamer.adapter.TrackArrayAdapter;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.GetArtistTopTrackEvent;
import com.udacity.nanodegree.nghianja.spotifystreamer.parcelable.TrackParcelable;
import com.udacity.nanodegree.nghianja.spotifystreamer.task.GetArtistTopTrackTask;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a list view, representing a list of Items.
 * <p/>
 * References:
 * [1] http://www.vogella.com/tutorials/AndroidListView/article.html
 * [2] http://stackoverflow.com/questions/15392261/android-pass-dataextras-to-a-fragment
 * [3] http://stackoverflow.com/questions/10463560/retaining-list-in-list-fragment-on-orientation-change
 * [4] http://stackoverflow.com/questions/14835828/keep-list-fragment-selected-item-position-on-orientation-change
 */
public class TrackListFragment extends ListFragment {

    private static final String TAG = "TrackListFragment";
    private ArrayList<TrackParcelable> tracks;
    private TrackArrayAdapter adapter;

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static TrackListFragment newInstance(int index, String artistId) {
        TrackListFragment f = new TrackListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putString("SpotifyId", artistId);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    public String getArtistId() {
        return getArguments().getString("SpotifyId", "");
    }

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
        return inflater.inflate(R.layout.fragment_toptracks, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey("tracks")) {
            tracks = new ArrayList<>();
            TrackParcelable track = new TrackParcelable("Waiting to load top tracks...", "", null, null, null);
            tracks.add(track);
        } else {
            tracks = savedInstanceState.getParcelableArrayList("tracks");
        }

        adapter = new TrackArrayAdapter(getActivity(), tracks);
        setListAdapter(adapter);
        getArtistTopTrack(getActivity(), getArtistId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("tracks", tracks);
    }

    public void getArtistTopTrack(Activity activity, String artistId) {
        if (artistId != null && !artistId.equals("")) {
            if (SpotifyStreamerApp.isNetworkAvailable(activity)) {
                activity.setProgressBarIndeterminateVisibility(true);
                GetArtistTopTrackTask task = new GetArtistTopTrackTask();
                task.execute(artistId, SpotifyStreamerApp.getCountryCode(activity));
            } else {
                Toast.makeText(activity, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "position=" + position);
//        showDetails(position);
    }

    public void updateAdapter(List<Track> items) {
        ArrayList<TrackParcelable> newTracks = new ArrayList<>();
        for (Track item : items) {
            Log.d(TAG, "track name=" + item.name);
            Log.d(TAG, "album name=" + item.album.name);
            if (item.album.images != null && !item.album.images.isEmpty()) {
                Image large = null;
                Image small = null;
                Image base = null;
                for (Image image : item.album.images) {
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
                if (large == null) {
                    if (small != null && small.width > base.width) {
                        large = small;
                    } else {
                        large = base;
                    }
                }
                if (small == null) {
                    small = base;
                }
                newTracks.add(new TrackParcelable(item.name, item.album.name, large.url, small.url, item.preview_url));
            } else {
                newTracks.add(new TrackParcelable(item.name, item.album.name, null, null, item.preview_url));
            }
        }
        adapter.clear();
        adapter.addAll(newTracks);
        adapter.notifyDataSetChanged();
        tracks = newTracks;
    }

    @Subscribe
    public void onAsyncTaskExecute(GetArtistTopTrackEvent event) {
        Tracks results = event.getResults();
        List<Track> items = results.tracks;
        if (items == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        } else {
            updateAdapter(items);
        }
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

}
