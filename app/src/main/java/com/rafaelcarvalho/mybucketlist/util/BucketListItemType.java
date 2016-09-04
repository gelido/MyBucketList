package com.rafaelcarvalho.mybucketlist.util;

import com.rafaelcarvalho.mybucketlist.R;

/**
 * Created by Rafael on 10/10/15.
 */
public enum BucketListItemType {
    MOVIES("Movies", R.drawable.ic_movie_white_24dp),
    SERIES("Series", R.drawable.ic_series_white_24dp),
    BOOKS("Books", R.drawable.ic_book_white_24dp);

    String mTitle;
    int mResourceIcon;

    BucketListItemType(String title, int resourceId) {
        mTitle = title;
        mResourceIcon = resourceId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getIcon() {
        return mResourceIcon;
    }

    public void setIcon(int mResourceIcon) {
        this.mResourceIcon = mResourceIcon;
    }


}
