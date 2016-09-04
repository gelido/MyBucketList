package com.rafaelcarvalho.mybucketlist.Interfaces;

/**
 * Created by Rafael on 08/11/15.
 */
public interface DetailFetcher {

    void fetchDetails(String id, int position, DetailFetcherCallback callback, ErrorCallback errorCallback);
}
