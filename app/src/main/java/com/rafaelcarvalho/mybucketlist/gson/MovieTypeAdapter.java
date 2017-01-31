package com.rafaelcarvalho.mybucketlist.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.rafaelcarvalho.mybucketlist.model.Movie;

import java.io.IOException;

/**
 * Created by Rafael on 28/01/17.
 */

public class MovieTypeAdapter extends TypeAdapter<Movie> {

    @Override
    public void write(JsonWriter out, Movie value) throws IOException {
        throw  new IOException("Oops, forgot to implement the Write! Check back at this!");
    }

    @Override
    public Movie read(JsonReader in) throws IOException {
        Movie movie = new Movie();
        in.beginObject();
        while(in.hasNext()){
            String name = in.nextName();
            switch (name){
                case "Title":
                    movie.setTitle(in.nextString());
                    break;
                case "Plot":
                    movie.setDescription(in.nextString());
                    break;
                case "imdbRating":
                    try {
                        movie.setRating(Float.parseFloat(in.nextString()));
                    }catch (NumberFormatException nex){
                        movie.setRating(0L);
                    }
                    break;
                case "Poster":
                    movie.setCover(in.nextString());
                    break;
                case "imdbID":
                    movie.setId(in.nextString());
                    break;
                default:
                    in.skipValue();
            }
        }

        in.endObject();
        return movie;
    }
}
