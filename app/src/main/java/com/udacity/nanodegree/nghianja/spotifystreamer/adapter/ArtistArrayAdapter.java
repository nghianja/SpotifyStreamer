package com.udacity.nanodegree.nghianja.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.model.ArtistViewHolder;
import com.udacity.nanodegree.nghianja.spotifystreamer.parcelable.ArtistParcelable;

import java.util.List;

/**
 * A custom adapter for artist list view.
 * <p/>
 * References:
 * [1] http://www.vogella.com/tutorials/AndroidListView/article.html
 * [2] https://www.airpair.com/android/list-fragment-android-studio
 * [3] http://stackoverflow.com/questions/16976431/change-background-color-of-selected-item-on-a-listview
 */
public class ArtistArrayAdapter extends ArrayAdapter<ArtistParcelable> {

    private final Context context;
    private int transparent;
    private int holo_dark_green;
    private String artistId;

    public ArtistArrayAdapter(Context context, List<ArtistParcelable> artists) {
        super(context, -1, artists);
        this.context = context;
        transparent = context.getResources().getColor(android.R.color.transparent);
        holo_dark_green = context.getResources().getColor(android.R.color.holo_green_dark);
        artistId = "";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        ArtistViewHolder holder;
        ArtistParcelable artist = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.list_item_artist, parent, false);
            holder = new ArtistViewHolder();
            holder.pictView = (ImageView) rowView.findViewById(R.id.artistPict);
            holder.nameView = (TextView) rowView.findViewById(R.id.artistName);
            rowView.setTag(holder);
        } else {
            rowView = convertView;
            holder = (ArtistViewHolder) rowView.getTag();
        }

        if (artist.getImageUrl() != null) {
            Picasso.with(context).load(artist.getImageUrl()).into(holder.pictView);
        } else {
            holder.pictView.setImageResource(R.mipmap.ic_launcher);
        }

        holder.nameView.setText(artist.getName());

        if (artist.getId() != null && !artist.getId().equals("") && artist.getId().equals(artistId)) {
            rowView.setBackgroundColor(holo_dark_green);
        } else {
            rowView.setBackgroundColor(transparent);
        }

        return rowView;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

}
