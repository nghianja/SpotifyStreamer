package com.udacity.nanodegree.nghianja.spotifystreamer.task;

import android.os.AsyncTask;
import android.util.Log;

import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.SearchArtistEvent;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * AsyncTask that does some work, then posts the result to the event bus.
 *
 * References:
 * [1] http://simonvt.net/2014/04/17/asynctask-is-bad-and-you-should-feel-bad/
 */
public class SearchArtistTask extends AsyncTask<String, Void, ArtistsPager> {

    private static final String TAG = "SearchArtistTask";

    @Override
    protected ArtistsPager doInBackground(String... parameters) {
        ArtistsPager results;

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        try {
            results = spotify.searchArtists(parameters[0]);
        } catch (RetrofitError ex) {
            Log.e(TAG, ex.toString());
            results = new ArtistsPager();
        }

        return results;
    }

    @Override
    protected void onPostExecute(ArtistsPager results) {
        SpotifyStreamerApp.bus.post(new SearchArtistEvent(results));
    }

}
