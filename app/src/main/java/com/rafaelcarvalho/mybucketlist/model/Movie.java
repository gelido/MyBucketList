package com.rafaelcarvalho.mybucketlist.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rafael on 28/09/15.
 */
public class Movie extends BucketListMediaItem implements Parcelable {

    public Movie(String title, String description, float rating, String cover,String id,boolean seen
                    ,String year) {
        super(title, description, rating, cover,id, seen,year);
    }

    public Movie() {
        super();
    }

    protected Movie(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
