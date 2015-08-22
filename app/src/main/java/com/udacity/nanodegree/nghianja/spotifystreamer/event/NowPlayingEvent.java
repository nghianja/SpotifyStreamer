package com.udacity.nanodegree.nghianja.spotifystreamer.event;

/**
 * The event to publishing and for the response to subscribing to the event bus.
 *
 * References:
 * [1] http://square.github.io/otto/
 * [2] http://simonvt.net/2014/04/17/asynctask-is-bad-and-you-should-feel-bad/
 */
public class NowPlayingEvent {

    public enum Action {
        ADD, REMOVE
    }

    private Action action;

    public NowPlayingEvent(Action action) {
        this.action = action;
    }

    public Action getAction() { return action; }

}
