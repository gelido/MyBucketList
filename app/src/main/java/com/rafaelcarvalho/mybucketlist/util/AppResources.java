package com.rafaelcarvalho.mybucketlist.util;

import com.rafaelcarvalho.mybucketlist.model.BucketListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rafael on 28/09/15.
 */
public class AppResources {

    private AppResources() {} //don't instantiate

    private static AppResources instance;

    public static AppResources getInstance(){
        if (instance == null){
            instance = new AppResources();
        }
        return instance;
    }

    /**
     * A map of sample (dummy) items, by ID.
     */

    private List<BucketListItem> movies;
    private List<BucketListItem> series;
    private List<BucketListItem> books;


    public List<BucketListItem> getMovies() {

        if(movies == null){
            movies = new ArrayList<>();
        }
        return movies;
    }

    public void setMovies(List<BucketListItem> movies) {
        this.movies = movies;
    }

    public List<BucketListItem> getSeries() {
        if(series == null){
            series = new ArrayList<>();
        }
        return series;
    }

    public void setSeries(List<BucketListItem> series) {
        this.series = series;
    }

    public List<BucketListItem> getBooks() {
        if(books == null){
            books = new ArrayList<>();
        }
        return books;
    }

    public void setBooks(List<BucketListItem> books) {
        this.books = books;
    }

    public void addMovie(BucketListItem movie)
    {
        movies.add(movie);
    }

    public void addSeries(BucketListItem series)
    {
        this.series.add(series);
    }

    public void addBook(BucketListItem book)
    {
        books.add(book);
    }

    public void removeMovie(BucketListItem movie)
    {
        movies.remove(movie);
    }

    public void removeSeries(BucketListItem series)
    {
        this.series.remove(series);
    }

    public void removeBook(BucketListItem book)
    {
        books.remove(book);
    }

    public void updateSeen(int position, boolean newValue, BucketListItemType type)
    {
        List<BucketListItem> list;
        switch (type)
        {
            case MOVIES:
                list = movies;
                break;
            case SERIES:
                list = series;
                break;
            case BOOKS:
                list = books;
                break;
            default:
                return;
        }
        list.get(position).setSeen(newValue);
    }
}
