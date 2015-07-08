package com.udacity.nanodegree.nghianja.spotifystreamer.event;

import kaaes.spotify.webapi.android.models.Tracks;

/**
 * The event to publishing and for the response to subscribing to the event bus.
 *
 * References:
 * [1] http://square.github.io/otto/
 * [2] http://simonvt.net/2014/04/17/asynctask-is-bad-and-you-should-feel-bad/
 */
public class GetArtistTopTrackEvent {

    private Tracks results;

    public GetArtistTopTrackEvent(Tracks results) {
        this.results = results;
    }

    public Tracks getResults() {
        return results;
    }

}
