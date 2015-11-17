package com.b3sk.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A  fragment containing a grid view filled with movie poster thumbnails.
 */
public class MovieFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieList;
    public final static String PAR_KEY = "com.b3sk.popularmovies.par";





    private void updateMovie(){
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();
    }

    //Check if there is a previously saved activity state.
    //Utilizes parcelable interface.
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey(PAR_KEY)) {
            movieList = new ArrayList<Movie>();
        }
        else {
            movieList = savedInstanceState.getParcelableArrayList(PAR_KEY);
        }
    }

    public MovieFragment() {
    }

    //Saves state of activity as parcelable.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PAR_KEY, movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Instantiates a new custom array adapter to handle the movie objects.
        movieAdapter = new MovieAdapter(getActivity(), movieList);

        //Use the custom adapter to populate a grid view.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieAdapter);

        //Set on click listener to launch movie info activity after clicking a
        //movie poster thumbnail on the gridview.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent mIntent = new Intent(getActivity(), InfoActivity.class);
                Movie mMovie = movieAdapter.getItem(i);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable(PAR_KEY, mMovie);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });


        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovie();
    }

    public class FetchMovieTask extends AsyncTask<Void, Void, Movie[]> {



        private Movie[] getMovieDataFromJson(String movieJsonStr)throws JSONException {


            final String MV_LIST = "results";
            final String MV_PATH = "poster_path";
            final String MV_TITLE = "original_title";
            final String MV_DATE = "release_date";
            final String MV_RATE = "vote_average";
            final String MV_SYNOP = "overview";

            //Create a JSON object with raw data back from API.
            JSONObject movieJson = new JSONObject(movieJsonStr);
            //Generate JSON array from raw data each element representing
            //a set of data for a movie.
            JSONArray movieArray = movieJson.getJSONArray(MV_LIST);

            Movie[] resultMovies = new Movie[movieArray.length()];

            //Step through each movie element in the JSON array.
            //Parse out the needed data to form Movie objects.
            //Store Movie in an array of Movies.
            for(int i = 0; i < movieArray.length(); i++){
                JSONObject movieInfo = movieArray.getJSONObject(i);
                String postLink = movieInfo.getString(MV_PATH);
                String title = movieInfo.getString(MV_TITLE);
                String rDate = movieInfo.getString(MV_DATE);
                String rating = movieInfo.getString(MV_RATE);
                String overview = movieInfo.getString(MV_SYNOP);
                resultMovies[i] = new Movie(postLink, title, rDate, overview, rating);
            }
            return resultMovies;
        }

        @Override
        protected Movie[] doInBackground(Void... params){


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;




            try {
                // Construct the URL for the Movie Database query

                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APIKEY_PARAM = "api_key";


                SharedPreferences sharedPrefs =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sortMethod = sharedPrefs.getString(
                        getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_label_popularity));

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortMethod)
                        .appendQueryParameter(APIKEY_PARAM, "YOUR_API_KEY_HERE")
                        .build();

                URL url = new URL(builtUri.toString());



                // Create the request to Movie Database, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();


            } catch (IOException e) {

                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {

                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {

                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the Movie data.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            //If the background thread yields an array of movies
            //step through it and add each one to the adapter.
            if (result != null) {
                movieAdapter.clear();
                for(Movie movie : result) {
                    movieAdapter.add(movie);
                }

            }
        }



    }



}
