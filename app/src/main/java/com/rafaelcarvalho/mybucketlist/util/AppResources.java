package com.rafaelcarvalho.mybucketlist.util;

import com.rafaelcarvalho.mybucketlist.model.Book;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.model.Movie;
import com.rafaelcarvalho.mybucketlist.model.Series;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rafael on 28/09/15.
 */
public class AppResources {

    private AppResources() {} //don't instantiate


    /**
     * A map of sample (dummy) items, by ID.
     */
    public static HashMap<BucketListItemType, List<BucketListItem>> ITEM_MAP = new HashMap<BucketListItemType, List<BucketListItem>>();

    public static HashMap<BucketListItemType, List<BucketListItem>> getItemMap(){
        HashMap<BucketListItemType, List<BucketListItem>> itemMap = new HashMap<BucketListItemType, List<BucketListItem>>();
        itemMap.put(BucketListItemType.MOVIES, new LinkedList<BucketListItem>());
        itemMap.put(BucketListItemType.SERIES,new LinkedList<BucketListItem>());
        itemMap.put(BucketListItemType.BOOKS,new LinkedList<BucketListItem>());
        return itemMap;
    }
}
