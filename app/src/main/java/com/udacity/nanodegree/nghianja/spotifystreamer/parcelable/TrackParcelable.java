package com.udacity.nanodegree.nghianja.spotifystreamer.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to store artist details for screen orientation change.
 *
 * References:
 * [1] http://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-be-parcelable
 * [2] http://blog.danielebottillo.com/2013/04/android-parcel-data-inside-and-between.html
 */
public class TrackParcelable implements Parcelable {

    private String trackName;
    private String albumName;
    private String imageLarge;
    private String imageSmall;
    private String previewUrl;

    // Constructor
    public TrackParcelable(String trackName, String albumName, String imageLarge, String imageSmall, String previewUrl) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.imageLarge = imageLarge;
        this.imageSmall = imageSmall;
        this.previewUrl = previewUrl;
    }

    // Getters and Setters
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageLarge() {
        return imageLarge;
    }

    public void setImageLarge(String imageLarge) {
        this.imageLarge = imageLarge;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(trackName);
        out.writeString(albumName);
        out.writeString(imageLarge);
        out.writeString(imageSmall);
        out.writeString(previewUrl);
    }

    private TrackParcelable(Parcel in) {
        this.trackName = in.readString();
        this.albumName = in.readString();
        this.imageLarge = in.readString();
        this.imageSmall = in.readString();
        this.previewUrl = in.readString();
    }

    public static final Parcelable.Creator<TrackParcelable> CREATOR
            = new Parcelable.Creator<TrackParcelable>() {
        public TrackParcelable createFromParcel(Parcel in) {
            return new TrackParcelable(in);
        }

        public TrackParcelable[] newArray(int size) {
            return new TrackParcelable[size];
        }
    };

}
