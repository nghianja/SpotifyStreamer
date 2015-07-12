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
 */
public class ArtistArrayAdapter extends ArrayAdapter<ArtistParcelable> {

    private final Context context;

    public ArtistArrayAdapter(Context context, List<ArtistParcelable> artists) {
        super(context, -1, artists);
        this.context = context;
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
        }

        holder.nameView.setText(artist.getName());
        return rowView;
    }

}
