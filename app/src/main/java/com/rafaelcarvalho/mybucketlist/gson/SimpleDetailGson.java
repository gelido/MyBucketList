package com.rafaelcarvalho.mybucketlist.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafael on 08/11/15.
 */
public class SimpleDetailGson {

    @SerializedName("Title")
    private String title;
    @SerializedName("imdbRating")
    private float rating;
    @SerializedName("Plot")
    private String description;
    @SerializedName("imdbId")
    private String id;
    @SerializedName("Poster")
    private String cover;




}
