package com.udacity.nanodegree.nghianja.spotifystreamer.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.model.TrackViewHolder;

import java.util.List;

import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * A custom adapter for track list view.
 * <p/>
 * References:
 * [1] http://www.vogella.com/tutorials/AndroidListView/article.html
 * [2] https://www.airpair.com/android/list-fragment-android-studio
 * [3] https://github.com/square/picasso/issues/609
 */
public class TrackArrayAdapter extends ArrayAdapter<Track> {

    private static final String TAG = "TrackArrayAdapter";
    private final Context context;

    public TrackArrayAdapter(Context context, List<Track> tracks) {
        super(context, -1, tracks);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        TrackViewHolder holder;
        Track track = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.list_item_track, parent, false);
            holder = new TrackViewHolder();
            holder.thumbnailView = (ImageView) rowView.findViewById(R.id.thumbnail);
            holder.songView = (TextView) rowView.findViewById(R.id.song);
            holder.albumView = (TextView) rowView.findViewById(R.id.album);
            rowView.setTag(holder);
        } else {
            rowView = convertView;
            holder = (TrackViewHolder) rowView.getTag();
        }

        AlbumSimple album = track.album;
        if (album != null) {
            if (album.images != null && !album.images.isEmpty()) {
                Image image = album.images.get(0);
                try {
                    Picasso.with(context).load(Uri.parse(image.url)).into(holder.thumbnailView);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
            holder.albumView.setText(album.name);
        }

        holder.songView.setText(track.name);
        return rowView;
    }

}