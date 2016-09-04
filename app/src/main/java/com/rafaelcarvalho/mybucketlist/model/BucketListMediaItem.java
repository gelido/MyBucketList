package com.rafaelcarvalho.mybucketlist.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

/**
 * Created by Rafael on 28/09/15.
 */
public abstract class BucketListMediaItem extends BucketListItem implements Parcelable{

    @SerializedName("Year")
    public String year;

    public BucketListMediaItem(String title, String description, float rating, String cover,String id,
                               boolean seen, String year) {
        super(title, description, rating, cover, id, seen);
        this.year = year;
    }

    public BucketListMediaItem() {

    }

    protected BucketListMediaItem(Parcel in) {
        super(in);
        year = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(year);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
