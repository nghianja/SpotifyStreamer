package com.udacity.nanodegree.nghianja.spotifystreamer.event;

import com.udacity.nanodegree.nghianja.spotifystreamer.parcelable.TrackParcelable;

/**
 * The event to publishing and for the response to subscribing to the event bus.
 *
 * References:
 * [1] http://square.github.io/otto/
 * [2] http://simonvt.net/2014/04/17/asynctask-is-bad-and-you-should-feel-bad/
 */
public class ChangeTrackEvent {

    private int index;
    private TrackParcelable track;

    public ChangeTrackEvent (int index, TrackParcelable track) {
        this.index = index;
        this.track = track;
    }

    public int getIndex() {
        return index;
    }

    public TrackParcelable getTrack() {
        return track;
    }

}
