package com.rafaelcarvalho.mybucketlist.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.rafaelcarvalho.mybucketlist.Interfaces.IDatabaseHandler;
import com.rafaelcarvalho.mybucketlist.Interfaces.OnListChangeListener;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.database.DatabaseHandler;
import com.rafaelcarvalho.mybucketlist.fragments.BucketListFragment;
import com.rafaelcarvalho.mybucketlist.fragments.SettingsActivityFragment;
import com.rafaelcarvalho.mybucketlist.fragments.ViewPagerFragment;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;
import com.rafaelcarvalho.mybucketlist.util.Constants;
import com.rafaelcarvalho.mybucketlist.util.Modification;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BucketListFragment.OnFragmentInteractionListener,
        OnListChangeListener {

    //It's a Hashmap so we can make sure the same field got only the last change
    private List<Modification> mModifications = new LinkedList<>();

    private static final String VIEW_PAGER_FRAG_KEY  = NavigationDrawerActivity.class.getSimpleName()+".viewPagerFragment";

    //Handlers and Helpers
    private IDatabaseHandler mDatabaseHandler;

    private boolean mIsArchive;
    private FloatingActionButton mFab;

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
        setContentView(R.layout.activity_navigation_drawer);


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

        //Set up the database
        mDatabaseHandler = DatabaseHandler.getDatabaseReference(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(mIsArchive?R.id.nav_archived:R.id.nav_media);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        setViewPagerFragment(false); //This has the fab's setOnClickListener

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        applyChanges();
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
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivityForResult(intent, Constants.SETTINGS_CHANGE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_media) {
            setViewPagerFragment(false);
        } else if (id == R.id.nav_archived) {
            setViewPagerFragment(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setViewPagerFragment(boolean isArchive) {

        //get the current fragment's position before we change the variable
        ViewPagerFragment oldViewPagerFrag = ((ViewPagerFragment)(getSupportFragmentManager()
                .findFragmentByTag(VIEW_PAGER_FRAG_KEY+mIsArchive)));
        //Null check for the fragment
        int tabPosition = oldViewPagerFrag != null? oldViewPagerFrag.getTabPosition():0;

        mIsArchive = isArchive;
        ViewPagerFragment fragment = ViewPagerFragment.newInstance(isArchive, tabPosition);
        mFab.setOnClickListener(fragment);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment,
                VIEW_PAGER_FRAG_KEY+isArchive).commit();
    }




    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == Constants.ADD_ITEM){

                if (data.getBooleanExtra(SearchActivity.IS_MODIFIED, true)) {

                    BucketListItemType type = BucketListItemType
                            .values()[data.getIntExtra(Constants.ITEM_TYPE,-1)];
                    refreshBy(type,mIsArchive);
                }
            }
            else if(requestCode == Constants.SETTINGS_CHANGE)
            {
                if (data.getBooleanExtra(SearchActivity.IS_MODIFIED, true)) {
                    recreate();
                }
            }else if(requestCode == Constants.ITEM_DETAIL){
                if(data.getBooleanExtra(DetailActivity.IS_DELETED, false)){
                    BucketListItemType type = BucketListItemType
                            .values()[data.getIntExtra(Constants.ITEM_TYPE,-1)];
                    refreshBy(type,mIsArchive);
                }
            }
        }
    }

    private void refreshBy(final BucketListItemType type, final boolean isSeen) {
        //fetches the info from the DB on another thread depending on the type sent
        AsyncTask<Integer,Void, List<BucketListItem>> task =
                new AsyncTask<Integer, Void, List<BucketListItem>>() {
                    @Override
                    protected List<BucketListItem> doInBackground(Integer ... params) {
                        BucketListItemType type = BucketListItemType.values()[params[0]];
                        return mDatabaseHandler.getAllFromTypeAndSeen(type, isSeen);
                    }


                    @Override
                    protected void onPostExecute(List<BucketListItem> items) {

                        ViewPagerFragment fragment =
                                (ViewPagerFragment) getSupportFragmentManager()
                                        .findFragmentByTag(VIEW_PAGER_FRAG_KEY+isSeen);
                        fragment.refreshList(type,items);
                    }
                };

        task.execute(type.ordinal());
    }

    @Override
    public void itemChanged(Modification mod) {
        //Add the modifications, so afterwards we can apply them all
        mModifications.add(mod);
    }

    @Override
    public void applyChanges() {
        //Save to DB in background

        if (!mModifications.isEmpty()) {
            new AsyncTask<Void,Void,Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    mDatabaseHandler.applyChanges(new ArrayList<>(mModifications));
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    mModifications.clear();
                }
            }.execute();
        }


    }

    @Override
    public void removeChange(Modification mod) {
        mModifications.remove(mod);
    }

    public IDatabaseHandler getDatabaseHandler() {
        return mDatabaseHandler;
    }
}
