package com.rafaelcarvalho.mybucketlist.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rafaelcarvalho.mybucketlist.Interfaces.IDatabaseHandler;
import com.rafaelcarvalho.mybucketlist.Interfaces.OnListChangeListener;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.activities.NavigationDrawerActivity;
import com.rafaelcarvalho.mybucketlist.fragments.BucketListFragment;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private boolean mIsArchive;


    public SectionsPagerAdapter(FragmentManager fm, Context context, boolean mIsArchive) {
        super(fm);
        this.mIsArchive = mIsArchive;
        this.mContext = context;
    }



    @Override
    public Fragment getItem(int position) {

        //Get the list of items from the database and create a fragment with that info
        // Can either be movies, series or books

        BucketListItemType type = BucketListItemType.values()[position];
        List<BucketListItem> items;
        try {

                items = ((NavigationDrawerActivity) mContext)
                        .getDatabaseHandler().getAllFromTypeAndSeen(type, mIsArchive);

        }catch (ClassCastException | NullPointerException ex){
            items = new ArrayList<>();
        }
        return BucketListFragment.newInstance(items,type, (OnListChangeListener) mContext, mIsArchive);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        BucketListItemType type = BucketListItemType.values()[position];

        switch (type) {
            case MOVIES:
                return mContext.getResources().getString(R.string.movies);
            case SERIES:
                return mContext.getResources().getString(R.string.series);
            case BOOKS:
                return mContext.getResources().getString(R.string.books);
        }
        return null;
    }
}