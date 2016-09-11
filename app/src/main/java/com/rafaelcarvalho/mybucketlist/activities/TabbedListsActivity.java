package com.rafaelcarvalho.mybucketlist.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rafaelcarvalho.mybucketlist.Interfaces.OnListChangeListener;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.database.DatabaseHandler;
import com.rafaelcarvalho.mybucketlist.fragments.BucketListFragment;
import com.rafaelcarvalho.mybucketlist.fragments.SettingsActivityFragment;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;
import com.rafaelcarvalho.mybucketlist.util.Modification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabbedListsActivity extends AppCompatActivity
        implements BucketListFragment.OnFragmentInteractionListener,View.OnClickListener,
        OnListChangeListener{

    private static final String PREFS_NAME = "prefs";
    private static final String PREF_ORANGE_THEME = "OrangeTheme";
    private ViewPager mViewPager;

    private int mThemeId = -1;

    //CONSTANTS
    private static final int ADD_ITEM = 1;


    //It's a Hashmap so we can make sure the same field got only the last change
    private HashMap<Modification.Field,Modification> mModifications = new HashMap<>();

    private TabLayout mTabLayout;

    //Handlers and Helpers
    private DatabaseHandler mDatabaseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String colorValue = prefs.getString(SettingsActivityFragment.COLOR_LIST_KEY, "0");

        switch (colorValue)
        {
            case "0":
                setTheme(R.style.BlueBucket);
                break;
            case "1":
                setTheme(R.style.OrangeBucket);
                break;
            default:
                setTheme(R.style.BlueBucket);
        }


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
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                applyChanges();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
        switch (item.getItemId()) {

            case R.id.action_settings:
//                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
//                editor.putBoolean(PREF_ORANGE_THEME, true);
//                editor.apply();
//
//                Intent intent = getIntent();
//                finish();
//
//                startActivity(intent);
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

                //TODO: so you don't lose the train of though, when this reaches here, supposedly the
                // added item is already on the DB (and hopefully on the AppResources), so here we should just
                // need to update the list with what's on the Singleton

                if (data.getBooleanExtra(SearchActivity.IS_MODIFIED, true)) {

                    final BucketListItemType type = BucketListItemType
                            .values()[data.getIntExtra(SearchActivity.ITEM_TYPE,-1)];


                    //fetches the info from the DB on another thread depending on the type sent
                    AsyncTask<Integer,Void, List<BucketListItem>> task =
                            new AsyncTask<Integer, Void, List<BucketListItem>>() {
                        @Override
                        protected List<BucketListItem> doInBackground(Integer ... params) {
                            BucketListItemType type = BucketListItemType.values()[params[0]];

                            return mDatabaseHandler.getAllFromType(type);
                        }


                                @Override
                                protected void onPostExecute(List<BucketListItem> items) {
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
    }

    @Override
    public void itemChanged(Modification mod) {
        //Add the modifications, so afterwards we can apply them all
        mModifications.put(mod.getField(),mod);
    }

    @Override
    public void applyChanges() {


        //Save to DB in background
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mDatabaseHandler.applyChanges(new ArrayList<>(mModifications.values()));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mModifications.clear();
            }
        }.execute();


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
            return BucketListFragment.newInstance(items,type, TabbedListsActivity.this);
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
                    return getResources().getString(R.string.movies);
                case SERIES:
                    return getResources().getString(R.string.series);
                case BOOKS:
                    return getResources().getString(R.string.books);
            }
            return null;
        }
    }
}
