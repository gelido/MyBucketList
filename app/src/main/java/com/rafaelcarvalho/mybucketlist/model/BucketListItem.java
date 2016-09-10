package com.rafaelcarvalho.mybucketlist.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Rafael on 28/09/15.
 */
public class BucketListItem implements Parcelable{

    @SerializedName("Title")
    protected String title;
    @SerializedName("Plot")
    protected String description;
    @SerializedName("imdbRating")
    protected float rating;
    @SerializedName("Poster")
    protected String cover;
    @SerializedName("imdbID")
    protected String id;

    protected boolean isSeen;

    public BucketListItem() {
    }

    public BucketListItem(String title, String description, float rating, String cover, String id,
                          boolean wasSeen) {
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.cover = cover;
        this.isSeen = wasSeen;
        this.id = id;
    }

    protected BucketListItem(Parcel in) {
        title = in.readString();
        description = in.readString();
        rating = in.readFloat();
        cover = in.readString();
        id = in.readString();
        isSeen = in.readByte() != 0;
    }

    public static final Creator<BucketListItem> CREATOR = new Creator<BucketListItem>() {
        @Override
        public BucketListItem createFromParcel(Parcel in) {
            return new BucketListItem(in);
        }

        @Override
        public BucketListItem[] newArray(int size) {
            return new BucketListItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        this.isSeen = seen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeFloat(rating);
        dest.writeString(cover);
        dest.writeString(id);
        dest.writeByte((byte) (isSeen ? 1 : 0));
    }

    public String getSubtitle()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BucketListItem && Objects.equals(this.getId(), ((BucketListItem) o).getId())
                && Objects.equals(this.getTitle(), ((BucketListItem) o).getTitle());
    }
}
