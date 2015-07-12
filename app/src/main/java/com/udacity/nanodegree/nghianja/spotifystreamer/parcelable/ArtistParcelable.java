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
public class ArtistParcelable implements Parcelable {

    private String id;
    private String name;
    private String imageUrl;

    // Constructor
    public ArtistParcelable(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(imageUrl);
    }

    private ArtistParcelable(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<ArtistParcelable> CREATOR
            = new Parcelable.Creator<ArtistParcelable>() {
        public ArtistParcelable createFromParcel(Parcel in) {
            return new ArtistParcelable(in);
        }
        public ArtistParcelable[] newArray(int size) {
            return new ArtistParcelable[size];
        }
    };

}
