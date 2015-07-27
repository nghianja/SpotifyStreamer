package com.udacity.nanodegree.nghianja.spotifystreamer.event;

import java.util.concurrent.TimeUnit;

/**
 * The event to publishing and for the response to subscribing to the event bus.
 *
 * References:
 * [1] http://square.github.io/otto/
 * [2] http://simonvt.net/2014/04/17/asynctask-is-bad-and-you-should-feel-bad/
 */
public class PlayerPreparedEvent {

    private int duration;
    private String endText;

    public PlayerPreparedEvent(int duration) {
        this.duration = duration;
        this.endText = String.format("%01d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
    }

    public int getDuration() {
        return duration;
    }

    public String getEndText() {
        return endText;
    }

}
