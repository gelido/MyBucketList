package com.rafaelcarvalho.mybucketlist.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rafael on 28/09/15.
 */
public class Book extends BucketListItem implements Parcelable{

    private String author;

    public Book(String title, String description, float rating, String cover, String id, boolean seen,
                String author) {
        super(title, description, rating, cover, id,seen);
        this.author = author;
    }

    public Book() {

    }

    protected Book(Parcel in) {
        super(in);
        author = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(author);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
