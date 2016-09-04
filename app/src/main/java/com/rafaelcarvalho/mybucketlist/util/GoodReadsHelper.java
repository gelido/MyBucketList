package com.rafaelcarvalho.mybucketlist.util;

import android.content.Context;

import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.gson.SimpleSearchGson;
import com.rafaelcarvalho.mybucketlist.Interfaces.ErrorCallback;
import com.rafaelcarvalho.mybucketlist.Interfaces.ItemSearcher;
import com.rafaelcarvalho.mybucketlist.Interfaces.SearchFinishedCallback;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Rafael on 24/10/15.
 */
public class GoodReadsHelper extends HttpHelper{

    private static final String API_KEY = "rctbiuVRT1Pxqhm2WPQcg";
    private static final String GOOD_READS_SEARCH_URL = "https://www.goodreads.com/search/index.xml?q=";


    public static Call getGoodReads(String title, Callback callback){
        String url = GOOD_READS_SEARCH_URL + title + "&key=" + API_KEY;
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
            GoodReadsHelper.getGoodReads(query, new Callback() {
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


}
