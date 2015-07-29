package com.udacity.nanodegree.nghianja.spotifystreamer.event;

/**
 * The event to publishing and for the response to subscribing to the event bus.
 *
 * References:
 * [1] http://square.github.io/otto/
 * [2] http://simonvt.net/2014/04/17/asynctask-is-bad-and-you-should-feel-bad/
 */
public class SeekBarChangeEvent {

    public enum Callback {
        PROGRESS_CHANGED, START_TRACKING_TOUCH, STOP_TRACKING_TOUCH
    }

    private Callback callback;
    private int progress;

    public SeekBarChangeEvent(Callback callback) {
        this.callback = callback;
    }

    public SeekBarChangeEvent(Callback callback, int progress) {
        this.callback = callback;
        this.progress = progress;
    }

    public Callback getCallback() {
        return callback;
    }

    public int getProgress() {
        return progress;
    }

}
