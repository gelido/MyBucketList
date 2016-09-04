package com.rafaelcarvalho.mybucketlist.Interfaces;

import java.util.List;

/**
 * Created by Rafael on 06/12/15.
 */
public interface SearchFinishedCallback <E> {

    void onSearchFinished(List<E> data);
}
