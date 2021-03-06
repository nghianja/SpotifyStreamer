package com.udacity.nanodegree.nghianja.spotifystreamer.event;

/**
 * The event to publishing and for the response to subscribing to the event bus.
 *
 * References:
 * [1] http://square.github.io/otto/
 * [2] http://simonvt.net/2014/04/17/asynctask-is-bad-and-you-should-feel-bad/
 */
public class PreviousNextEvent {

    public enum Control {
        PREVIOUS, NEXT
    }

    private Control control;
    private int index;

    public PreviousNextEvent(Control control, int index) {
        this.control = control;
        this.index = index;
    }

    public Control getControl() {
        return control;
    }

    public int getIndex() {
        return index;
    }

}
