package com.udacity.nanodegree.nghianja.spotifystreamer;

/**
 * Class for constants.
 *
 * References:
 * [1] http://www.truiton.com/2014/10/android-foreground-service-example/
 */
public class SpotifyStreamerConstants {
    public interface ACTION {
        public static String MAIN_ACTION = "spotifystreamer.action.main";
        public static String PREV_ACTION = "spotifystreamer.action.prev";
        public static String PAUSE_ACTION = "spotifystreamer.action.pause";
        public static String PLAY_ACTION = "spotifystreamer.action.play";
        public static String NEXT_ACTION = "spotifystreamer.action.next";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
