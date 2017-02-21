package com.rafaelcarvalho.mybucketlist.util;

import android.content.Context;
import android.util.Log;

import com.rafaelcarvalho.mybucketlist.Interfaces.DetailFetcher;
import com.rafaelcarvalho.mybucketlist.Interfaces.DetailFetcherCallback;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.gson.SimpleSearchGson;
import com.rafaelcarvalho.mybucketlist.Interfaces.ErrorCallback;
import com.rafaelcarvalho.mybucketlist.Interfaces.ItemSearcher;
import com.rafaelcarvalho.mybucketlist.Interfaces.SearchFinishedCallback;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


/**
 * Created by Rafael on 24/10/15.
 */
public class GoodReadsHelper extends HttpHelper{

    private static final String API_KEY = "GOODREADS_API_KEY";
    private static final String GOOD_READS_SEARCH_URL = "https://www.goodreads.com/search/index.xml?q=";
    private static final String GOOD_READS_SHOW_URL = "https://www.goodreads.com/book/show?id=";


    public static Call getGoodReads(Context context,String title, Callback callback){
        Properties properties = new PropertiesUtil(context).getProperties();

        String url = GOOD_READS_SEARCH_URL + title + "&key=" + properties.get(API_KEY);
        return get(callback,url);
    }

    public static Call fetchGoodReads(Context context,String id, Callback callback){
        Properties properties = new PropertiesUtil(context).getProperties();
        String url = GOOD_READS_SHOW_URL + id + "&format=xml&key=" +  properties.get(API_KEY);
        Log.d("Parser", url);
        return get(callback,url);
    }

    /**
     * Class created so we can call it when searching for series or movies
     */
    public static class GoodReadsSearcher implements ItemSearcher {

        private Context mContext;

        public GoodReadsSearcher(Context context) {
            mContext = context;
        }

        @Override
        public void search(String query, final SearchFinishedCallback callback, final ErrorCallback
                           errorCallback) {
            GoodReadsHelper.getGoodReads(mContext,query, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    errorCallback.onError(mContext.getString(R.string.error_no_results_found));
                    callback.onSearchFinished(null);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);


                    String body = response.body().string();

                    GoodReadsParser parser = new GoodReadsParser();
                    ArrayList<SimpleSearchGson.SearchItem> books = null;
                    try {
                        books = (ArrayList<SimpleSearchGson.SearchItem>)
                                parser.parseList(new ByteArrayInputStream(body.getBytes()));
                        callback.onSearchFinished(books);
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    public static class GoodReadsFetcher implements DetailFetcher
    {
        private Context mContext;

        public GoodReadsFetcher(Context context) {
            mContext = context;
        }

        @Override
        public void fetchDetails(String id, int position, final DetailFetcherCallback callback, final ErrorCallback errorCallback) {

            GoodReadsHelper.fetchGoodReads(mContext,id, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    errorCallback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    String body = response.body().string();

                    GoodReadsParser parser = new GoodReadsParser();
                    BucketListItem book = null;
                    try {
                        book = parser.parseShow(new ByteArrayInputStream(body.getBytes()));
                        callback.onFetchFinished(book);
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    }


}
