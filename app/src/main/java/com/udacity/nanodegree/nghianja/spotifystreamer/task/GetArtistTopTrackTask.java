package com.udacity.nanodegree.nghianja.spotifystreamer.task;

import android.os.AsyncTask;
import android.util.Log;

import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.event.GetArtistTopTrackEvent;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * AsyncTask that does some work, then posts the result to the event bus.
 *
 * References:
 * [1] http://simonvt.net/2014/04/17/asynctask-is-bad-and-you-should-feel-bad/
 */
public class GetArtistTopTrackTask extends AsyncTask<String, Void, Tracks> {

    private static final String TAG = "GetArtistTopTrackTask";

    /**
     * Get Spotify catalog information about an artist’s top tracks by country.
     *
     * parameters:
     * [0] The Spotify ID for the artist.
     * [1] The country: an ISO 3166-1 alpha-2 country code.
     */
    @Override
    protected Tracks doInBackground(String... parameters) {
        Tracks results;

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        Map<String, Object> options = new HashMap<>();
        options.put("country", parameters[1]);

        try {
            results = spotify.getArtistTopTrack(parameters[0], options);
        } catch (RetrofitError ex) {
            Log.e(TAG, ex.toString());
            results = new Tracks();
        }

        return results;
    }

    @Override
    protected void onPostExecute(Tracks results) {
        SpotifyStreamerApp.bus.post(new GetArtistTopTrackEvent(results));
    }

}
