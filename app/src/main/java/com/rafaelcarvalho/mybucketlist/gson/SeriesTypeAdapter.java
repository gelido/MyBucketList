package com.rafaelcarvalho.mybucketlist.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.rafaelcarvalho.mybucketlist.model.Series;

import java.io.IOException;

/**
 * Created by Rafael on 28/01/17.
 */

public class SeriesTypeAdapter extends TypeAdapter<Series> {

    @Override
    public void write(JsonWriter out, Series value) throws IOException {
        throw  new IOException("Oops, forgot to implement the Write! Check back at this!");
    }

    @Override
    public Series read(JsonReader in) throws IOException {
        Series series = new Series();
        in.beginObject();
        while(in.hasNext()){
            String name = in.nextName();
            switch (name){
                case "Title":
                    series.setTitle(in.nextString());
                    break;
                case "Plot":
                    series.setDescription(in.nextString());
                    break;
                case "imdbRating":
                    try {
                        series.setRating(Float.parseFloat(in.nextString()));
                    }catch (NumberFormatException nex){
                        series.setRating(0L);
                    }
                    break;
                case "Poster":
                    series.setCover(in.nextString());
                    break;
                case "imdbID":
                    series.setId(in.nextString());
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();
        return series;
    }
}
