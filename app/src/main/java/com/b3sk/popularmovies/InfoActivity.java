package com.b3sk.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new InfoFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class InfoFragment extends Fragment {


        public InfoFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_info, container, false);

            //Extract the bundle from the intent Extras and then get the Movie
            //parcelable out of the bundle.
            Intent mIntent = getActivity().getIntent();
            if (mIntent != null) {
                Bundle mBundle = mIntent.getExtras();
                Movie mMovie = mBundle.getParcelable(MovieFragment.PAR_KEY);

                //Populate the layout with the movie's info.

                ((TextView) rootView.findViewById(R.id.detail_title))
                        .setText(mMovie.title);

                ((TextView) rootView.findViewById(R.id.detail_date))
                        .setText("(" + mMovie.date + ")");

                ((TextView) rootView.findViewById(R.id.detail_description))
                        .setText(mMovie.overview);

                ((TextView) rootView.findViewById(R.id.detail_rating))
                        .setText("Rating: " + mMovie.rating + "/10");

                //Build link and use picasso to handle setting the thumbnail image.
                String thumbLink = "http://image.tmdb.org/t/p/w500/" + mMovie.link;
                ImageView posterView = (ImageView) rootView.findViewById(R.id.detail_poster);
                Picasso.with(getContext()).load(thumbLink).into(posterView);
            }

            return rootView;
        }


    }

}
