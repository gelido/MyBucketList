package com.rafaelcarvalho.mybucketlist.gson;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Rafael on 16/10/15.
 */
public class SimpleSearchGson {

    @SerializedName("Search")
    @Nullable
    List<SearchItem> search;

    @Nullable
    public List<SearchItem> getSearch() {
        return search;
    }

    public static class SearchItem implements Serializable{

        @SerializedName("Title")
        public String title;
        @SerializedName("Poster")
        public String poster;
        @SerializedName("Year")
        public String year;
        @SerializedName("imdbID")
        public String id;

        public SearchItem() {
        }

        public SearchItem(String title, String poster, String id, String year) {
            this.title = title;
            this.poster = poster;
            this.id = id;
            this.year = year; //serves as author in books
        }

        @Override
        public String toString() {
            return title + " - " + id + " - " + poster;
        }


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }
    }

}
