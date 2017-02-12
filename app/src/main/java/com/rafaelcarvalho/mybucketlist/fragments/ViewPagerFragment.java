package com.rafaelcarvalho.mybucketlist.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rafaelcarvalho.mybucketlist.Interfaces.OnListChangeListener;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.activities.SearchActivity;
import com.rafaelcarvalho.mybucketlist.adapters.SectionsPagerAdapter;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.AppResources;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;
import com.rafaelcarvalho.mybucketlist.util.Constants;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewPagerFragment extends Fragment implements  View.OnClickListener{

    private static final String IS_ARCHIVE_KEY = "isArchiveKey";
    private static final String START_POSITION_KEY = "startPositionKey";

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FragmentActivity mContext;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private boolean mIsArchive;
    private int mStartPosition;

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ViewPagerFragment.
     */
    public static ViewPagerFragment newInstance(boolean isArchive, int startTabPosition) {

        Bundle args = new Bundle();
        args.putBoolean(IS_ARCHIVE_KEY,isArchive);
        args.putInt(START_POSITION_KEY, startTabPosition);
        ViewPagerFragment fragment = new ViewPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsArchive = getArguments().getBoolean(IS_ARCHIVE_KEY);
            mStartPosition = getArguments().getInt(START_POSITION_KEY);
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), mContext,
                mIsArchive);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_lists_tabbed, container, false);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) root.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((OnListChangeListener)mContext).applyChanges();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Give the TabLayout the ViewPager
        mTabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(mStartPosition);
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        if(mIsArchive){
            toolbar.setBackgroundColor(AppResources.getFromAttrTheme(mContext,R.attr.bsPrimaryGrayed));
            mTabLayout.setBackgroundColor(AppResources.getFromAttrTheme(mContext,R.attr.bsPrimaryGrayed));
            mTabLayout.setTabTextColors(getResources().getColor(android.R.color.white),
                    AppResources.getFromAttrTheme(mContext,R.attr.bsPrimaryGrayedDark));
            mTabLayout.setSelectedTabIndicatorColor(AppResources.getFromAttrTheme(mContext,
                        R.attr.bsPrimaryGrayedDark));
        }
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (FragmentActivity)context;
    }

    public void refreshList(BucketListItemType type, List<BucketListItem> items){
        for (Fragment fragment: getChildFragmentManager().getFragments()){
            if(((BucketListFragment) fragment).getType() == type){
                ((BucketListFragment) fragment).updateList(items);
            }
        }
    }

    @Override
    public void onClick(View view) {
        final Intent searchIntent = new Intent(mContext, SearchActivity.class);
        searchIntent.putExtra(Constants.ITEM_TYPE, mTabLayout.getSelectedTabPosition());
        getActivity().startActivityForResult(searchIntent, Constants.ADD_ITEM);
    }

    public int getTabPosition(){
        return mTabLayout == null? 0: mTabLayout.getSelectedTabPosition();
    }
}
