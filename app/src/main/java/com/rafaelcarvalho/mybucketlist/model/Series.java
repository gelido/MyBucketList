package com.rafaelcarvalho.mybucketlist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Rafael on 28/09/15.
 */
public class Series extends BucketListMediaItem implements Parcelable{

//    private ArrayList<String> mEpisodes;

    public Series(String title, String description, float rating, String cover,String id, boolean seen
                    ,String year) {
        super(title, description, rating, cover, id, seen, year);
    }

    public Series() {

    }

//    public ArrayList<String> getmEpisodes() {
//        return mEpisodes;
//    }
//
//    public void setmEpisodes(ArrayList<String> mEpisodes) {
//        this.mEpisodes = mEpisodes;
//    }

    protected Series(Parcel in) {
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

    public static final Creator<Series> CREATOR = new Creator<Series>() {
        @Override
        public Series createFromParcel(Parcel in) {
            return new Series(in);
        }

        @Override
        public Series[] newArray(int size) {
            return new Series[size];
        }
    };
}
