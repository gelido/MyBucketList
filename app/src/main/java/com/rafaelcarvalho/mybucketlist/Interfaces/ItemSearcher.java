package com.rafaelcarvalho.mybucketlist.Interfaces;

/**
 * Created by Rafael on 23/10/15.
 */
public interface ItemSearcher {

    public void search(String query, SearchFinishedCallback callback, ErrorCallback errorCallback);
}
