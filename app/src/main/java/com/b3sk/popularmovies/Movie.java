package com.b3sk.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Barry on 11/11/2015.
 */
public class Movie implements Parcelable {
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
    String link;
    String title;
    String date;
    String overview;
    String rating;


    public Movie(String thumbLink, String title, String date, String overview, String rating) {
        this.link = thumbLink;
        this.title = title;
        this.date = date;
        this.overview = overview;
        this.rating = rating;
    }


    //Implement the parcelable interface.
    private Movie(Parcel in) {
        link = in.readString();
        title = in.readString();
        date = in.readString();
        overview = in.readString();
        rating = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(link);
        parcel.writeString(title);
        parcel.writeString(date);
        parcel.writeString(overview);
        parcel.writeString(rating);
    }


}
