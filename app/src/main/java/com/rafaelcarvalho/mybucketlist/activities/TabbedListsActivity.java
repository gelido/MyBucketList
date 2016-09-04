package com.rafaelcarvalho.mybucketlist.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.database.DatabaseHandler;
import com.rafaelcarvalho.mybucketlist.fragments.BucketListFragment;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;

import java.util.List;

import static com.rafaelcarvalho.mybucketlist.util.BucketListItemType.BOOKS;
import static com.rafaelcarvalho.mybucketlist.util.BucketListItemType.MOVIES;
import static com.rafaelcarvalho.mybucketlist.util.BucketListItemType.SERIES;

public class TabbedListsActivity extends AppCompatActivity
        implements BucketListFragment.OnFragmentInteractionListener,View.OnClickListener {

    private ViewPager mViewPager;

    //CONSTANTS
    private static final int ADD_ITEM = 1;



    private TabLayout mTabLayout;

    //Handlers and Helpers
    private DatabaseHandler mDatabaseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_lists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(sectionsPagerAdapter);

        // Give the TabLayout the ViewPager
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        //Set up the database
        mDatabaseHandler = DatabaseHandler.getDatabaseReference(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println("INTERACTION");
    }

    @Override
    public void onClick(View source) {
        final Intent searchIntent = new Intent(this, SearchActivity.class);
        searchIntent.putExtra(SearchActivity.ITEM_TYPE, mTabLayout.getSelectedTabPosition());
        startActivityForResult(searchIntent, ADD_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == ADD_ITEM){

                //fetches the info from the DB on another thread depending on the type sent
                final BucketListItemType type = BucketListItemType
                        .values()[data.getIntExtra(SearchActivity.ITEM_TYPE,-1)];

                AsyncTask<Integer,Void, List<BucketListItem>> task =
                        new AsyncTask<Integer, Void, List<BucketListItem>>() {
                    @Override
                    protected List<BucketListItem> doInBackground(Integer ... params) {
                        BucketListItemType type = BucketListItemType.values()[params[0]];

                        return mDatabaseHandler.getAllFromType(type);
                    }


                            @Override
                            protected void onPostExecute(List<BucketListItem> items) {
                                //TODO: update lists
                                for (Fragment fragment: getSupportFragmentManager().getFragments()){
                                    if(((BucketListFragment) fragment).getType() == type){
                                        ((BucketListFragment) fragment).updateList(items);
                                    }

                                }
                            }
                        };

                task.execute(data.getIntExtra(SearchActivity.ITEM_TYPE,-1));
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public Fragment getItem(int position) {

            //Get the list of items from the database and create a fragment with that info
            // Can either be movies, series or books

            BucketListItemType type = BucketListItemType.values()[position];
            List<BucketListItem> items = mDatabaseHandler.getAllFromType(type);
            return BucketListFragment.newInstance(items,type);
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
                    return MOVIES.getTitle();
                case SERIES:
                    return SERIES.getTitle();
                case BOOKS:
                    return BOOKS.getTitle();
            }
            return null;
        }
    }
}
