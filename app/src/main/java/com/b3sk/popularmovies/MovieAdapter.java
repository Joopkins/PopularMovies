package com.b3sk.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Barry on 11/11/2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    /**
     *
     * @param context the current context
     * @param movies  A list of movie posters to display in a grid
     */

    public MovieAdapter(Activity context, List<Movie> movies){
        super(context, 0, movies);
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }

        ImageView posterView = (ImageView) convertView.findViewById(R.id.grid_item_image);
        String thumbLink = "http://image.tmdb.org/t/p/w185/"+movie.link;
        Picasso.with(getContext()).load(thumbLink).into(posterView);
        return convertView;
    }

}
