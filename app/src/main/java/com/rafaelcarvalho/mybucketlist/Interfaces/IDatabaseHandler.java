package com.rafaelcarvalho.mybucketlist.Interfaces;

import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;
import com.rafaelcarvalho.mybucketlist.util.Modification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rafael on 07/02/17.
 */

public interface IDatabaseHandler {

    void addItem(BucketListItem item);

    ArrayList<String> getAllIds();
    List<BucketListItem> getAllFromType(BucketListItemType type);

    List<BucketListItem> getAllFromTypeAndSeen(BucketListItemType type, boolean isSeen);

    BucketListItem remove(String id);
    void applyChanges(List<Modification> modifications);
}
