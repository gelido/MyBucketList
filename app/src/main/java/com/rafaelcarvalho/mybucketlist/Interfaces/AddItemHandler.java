package com.rafaelcarvalho.mybucketlist.Interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Rafael on 26/12/15.
 */
public interface AddItemHandler {

    void addItem(RecyclerView.ViewHolder viewHolder, DetailFetcher fetcher);
}
