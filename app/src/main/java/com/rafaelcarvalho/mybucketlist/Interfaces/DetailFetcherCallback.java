package com.rafaelcarvalho.mybucketlist.Interfaces;

import com.rafaelcarvalho.mybucketlist.model.BucketListItem;

/**
 * Created by Rafael on 08/11/15.
 */
public interface DetailFetcherCallback {

    public void onFetchFinished(BucketListItem item);

}
