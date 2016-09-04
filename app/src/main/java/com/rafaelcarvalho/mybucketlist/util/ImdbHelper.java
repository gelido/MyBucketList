package com.rafaelcarvalho.mybucketlist.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.gson.SimpleSearchGson;
import com.rafaelcarvalho.mybucketlist.Interfaces.DetailFetcher;
import com.rafaelcarvalho.mybucketlist.Interfaces.DetailFetcherCallback;
import com.rafaelcarvalho.mybucketlist.Interfaces.ErrorCallback;
import com.rafaelcarvalho.mybucketlist.Interfaces.ItemSearcher;
import com.rafaelcarvalho.mybucketlist.Interfaces.SearchFinishedCallback;
import com.rafaelcarvalho.mybucketlist.model.Movie;
import com.rafaelcarvalho.mybucketlist.model.Series;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Rafael on 15/10/15.
 */
public class ImdbHelper extends HttpHelper{

    private static final String IMDB_URL_DETAIL_ID = "http://www.omdbapi.com/?i=";

    private static final String IMDB_URL_SEARCH = "http://www.omdbapi.com/?s=";
    private static final String IMDB_MOVIE_SUFFIX = "&type=movie&plot=full&r=json";
    private static final String IMDB_SERIES_SUFFIX = "&type=series&plot=full&r=json";

    public static Call postImdb(String title,BucketListItemType type, String json, Callback callback) throws IOException {
        String url = null;
        switch(type){
            case MOVIES:
                url = IMDB_URL_SEARCH + title.trim() + IMDB_MOVIE_SUFFIX;
                break;
            case SERIES:
                url = IMDB_URL_SEARCH + title.trim() + IMDB_SERIES_SUFFIX;
                break;

        }
        return post(json, callback, url);
    }


    public static Call fetchWithId(String id, BucketListItemType type, String json, Callback callback){
        String url = null;

        switch (type){
            case MOVIES:
                url = IMDB_URL_DETAIL_ID + id.trim() + IMDB_MOVIE_SUFFIX;
                break;
            case SERIES:
                url = IMDB_URL_DETAIL_ID + id.trim() + IMDB_MOVIE_SUFFIX;
                break;
        }

        return post(json, callback, url);
    }

    public static class ImdbFetcher implements DetailFetcher {

        private Context mContext;
        private BucketListItemType mItemType;
        private Gson mGson;

        public ImdbFetcher(Context mContext, BucketListItemType mItemType) {
            this.mContext = mContext;
            this.mItemType = mItemType;
            this.mGson = new Gson();
        }

        @Override
        public void fetchDetails(final String id, final int position, final DetailFetcherCallback
                callback, final ErrorCallback errorCallback) {


            //First get the full info from the Apis
            ImdbHelper.fetchWithId(id, mItemType, "", new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    errorCallback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);


                    String body;
                    try {
                        body = response.body().string();

                        //We first parse to see if there is an error. If it has the gson wont work
                        JsonParser parser = new JsonParser();
                        JsonObject json = parser.parse(body).getAsJsonObject();
                        if (json.has("Error")) {
                            errorCallback.onError(json.get("Error").getAsString());
                        } else {
                            //No errors were found, so we create a pojo with the info
                            //and update the list

                            switch (mItemType){
                                case MOVIES:
                                    final Movie movie = mGson.fromJson(body,
                                            Movie.class);
                                    callback.onFetchFinished(movie);
                                    break;
                                case SERIES:
                                    final Series series = mGson.fromJson(body,
                                            Series.class);
                                    callback.onFetchFinished(series);

                                    break;

                            }
                        }


                    } catch (ClassCastException | JsonSyntaxException |
                            NullPointerException exception) {
                        Log.e("ImdbHelper", exception.getMessage());
                    }
                }
            });

        }
    }

    /**
     * Class created so we can call it when searching for series or movies
     */
    public static class ImdbSearcher implements ItemSearcher {

        private Context mContext;
        private BucketListItemType mItemType;
        private Gson mGson;

        public ImdbSearcher(Context context, BucketListItemType itemType) {
            this.mContext = context;
            this.mItemType = itemType;
            this.mGson = new Gson();
        }

        @Override
        public void search(String query, final SearchFinishedCallback callback, final
                           ErrorCallback errorCallback) {
            try {
                ImdbHelper.postImdb(query, mItemType, "", new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        errorCallback.onError(mContext.getString(R.string.error_no_results_found));
                        callback.onSearchFinished(null);
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);


                        String body;
                        try {
                            body = response.body().string();

                            //We first parse to see if there is an error. If it has the gson wont work
                            JsonParser parser = new JsonParser();
                            JsonObject json = parser.parse(body).getAsJsonObject();
                            if (json.has("Error")) {
                                callback.onSearchFinished(new ArrayList<SimpleSearchGson.SearchItem>());
                                errorCallback.onError(json.get("Error").getAsString());
                            } else {
                                //No errors were found, so we create a pojo with the info
                                //and update the list
                                SimpleSearchGson result = mGson.fromJson(body,
                                        SimpleSearchGson.class);
                                callback.onSearchFinished(result.getSearch());

                            }


                        } catch (JsonSyntaxException | NullPointerException exception) {

                            //If omdb doesn't find a movie the gson will fail.
                            // So we show and error Message stating results were not found.
                            errorCallback.onError(mContext.getString(R.string.error_no_results_found));
                        }


                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
