package com.rafaelcarvalho.mybucketlist.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rafaelcarvalho.mybucketlist.Interfaces.AddItemHandler;
import com.rafaelcarvalho.mybucketlist.Interfaces.DetailFetcher;
import com.rafaelcarvalho.mybucketlist.Interfaces.DetailFetcherCallback;
import com.rafaelcarvalho.mybucketlist.Interfaces.ErrorCallback;
import com.rafaelcarvalho.mybucketlist.Interfaces.ItemSearcher;
import com.rafaelcarvalho.mybucketlist.Interfaces.OnListChangeListener;
import com.rafaelcarvalho.mybucketlist.Interfaces.SearchFinishedCallback;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.adapters.SearchResultAdapter;
import com.rafaelcarvalho.mybucketlist.database.DatabaseHandler;
import com.rafaelcarvalho.mybucketlist.fragments.SettingsActivityFragment;
import com.rafaelcarvalho.mybucketlist.gson.SimpleSearchGson;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.AppResources;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;
import com.rafaelcarvalho.mybucketlist.util.CacheManager;
import com.rafaelcarvalho.mybucketlist.util.GoodReadsHelper;
import com.rafaelcarvalho.mybucketlist.util.ImdbHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        AddItemHandler{



    public static final String X_VALUE_SHOW = "XValueShow";
    public static final String Y_VALUE_SHOW = "YValueShow";
    public static final String VIEW_ID = "ViewId";
    public static final String IS_MODIFIED = "IsModified";

    private SearchResultAdapter mAdapter;
    private RecyclerView mSearchResultView;
    private BucketListItemType mItemType;
    private ItemSearcher mItemSearcher;
    private TextView mTxtEmpty;
    private CacheManager<SimpleSearchGson.SearchItem> mCacheManager;

    private DatabaseHandler mDatabaseHandler;
    private CoordinatorLayout mCoordinatorLayout;
    private int mResourceLayout;
    private boolean hasFinished = false;

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
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        init(savedInstanceState);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);


        //init the Search view so the user can search on the toolbar
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.action_search));
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setHintTextColor(getResources().getColor(R.color.toolbarText));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void init(Bundle savedInstanceState){

        //Instantiate the DB
        mDatabaseHandler = DatabaseHandler.getDatabaseReference(this);
        mCacheManager = new CacheManager<>(this);

        //Init the CoordinatorLayout for the SnackBars
        mCoordinatorLayout  = (CoordinatorLayout) findViewById(R.id.main_content);

        //init search on action bar
        mSearchResultView = (RecyclerView) findViewById(R.id.rv_search_result);
        mTxtEmpty = (TextView) findViewById(R.id.tv_empty);
        mItemType = BucketListItemType.values()[getIntent().getIntExtra(TabbedListsActivity.ITEM_TYPE, -1)];
        mResourceLayout = R.layout.list_search_item;
        //init detail fetcher for the items
        final DetailFetcher fetcher;
        switch (mItemType){
            case MOVIES:
                fetcher = new ImdbHelper.ImdbFetcher(this,mItemType);
                break;
            case SERIES:
                fetcher = new ImdbHelper.ImdbFetcher(this,mItemType);
                break;
            case BOOKS:
                fetcher = new GoodReadsHelper.GoodReadsFetcher();
                break;
            default:
                fetcher = new DetailFetcher() {
                    @Override
                    public void fetchDetails(String id, int position, DetailFetcherCallback callback
                            , ErrorCallback errorCallback) {
                    }
                };
        }

        //init the adapter
        if(mAdapter == null){

            ArrayList<SimpleSearchGson.SearchItem> data= new ArrayList<>();
            try {
                //Get the data from the cache.
                List<SimpleSearchGson.SearchItem> list = mCacheManager.read(mItemType);
                for(SimpleSearchGson.SearchItem item :list){
                    data.add(item);
                }
            } catch (IOException | ClassNotFoundException e) {
                //Anything goes wrong we show nothing
                data = new ArrayList<>();
            }
            mAdapter = new SearchResultAdapter(this,data,mResourceLayout, fetcher);
            mSearchResultView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mSearchResultView.setAdapter(mAdapter);
        }

        getIntent().putExtra(IS_MODIFIED, false);

        //Change the Searcher depending on the type clicked
        switch(mItemType){
            case MOVIES:
                getSupportActionBar().setTitle(getString(R.string.movies));
                mItemSearcher = new ImdbHelper.ImdbSearcher(this,mItemType);
                break;
            case SERIES:
                getSupportActionBar().setTitle(getString(R.string.series));
                mItemSearcher = new ImdbHelper.ImdbSearcher(this,mItemType);
                break;
            case BOOKS:
                getSupportActionBar().setTitle(getString(R.string.books));
                mItemSearcher = new GoodReadsHelper.GoodReadsSearcher(this);
                break;
        }




        //Add swipe to the recyclerView
        ItemTouchHelper.SimpleCallback simpleCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //Method not called
                return false;
            }


            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                addItem(viewHolder,fetcher);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                //This creates the green background on the card swipe
                View itemView = viewHolder.itemView;

                Drawable d = ContextCompat.getDrawable(SearchActivity.this, R.drawable.bg_swipe_item_right);
                d.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                d.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleCallBack);

        touchHelper.attachToRecyclerView(mSearchResultView);
        updateListVisibility();

    }

    @Override
    public void addItem(final RecyclerView.ViewHolder viewHolder, DetailFetcher fetcher) {
        //Gets the details and the callback adds to the DB and removes from list

            final ProgressBar progressBar =
                    ((SearchResultAdapter.ItemViewHolder) viewHolder).getProgressBar();
            final ImageButton acceptButton = ((SearchResultAdapter.ItemViewHolder) viewHolder).getBtnAccept();
            final int position  = viewHolder.getAdapterPosition();

        fetcher.fetchDetails(mAdapter.getBucketItemId(viewHolder.getAdapterPosition()),
                viewHolder.getAdapterPosition(), new DetailFetcherCallback() {
                    @Override
                    public void onFetchFinished(final BucketListItem item) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                acceptButton.setVisibility(View.VISIBLE);
                                //Snackback that can undo the remove
                            }
                        });

                        if (!hasFinished) {
                            getIntent().putExtra(IS_MODIFIED, true);
                            //Remove the item from the list, this is purely visual
                            //the method returns the one removed so we can undo
                            final SimpleSearchGson.SearchItem searchItem =
                                    removeFromList(viewHolder.getAdapterPosition());
                            updateListVisibility();
                            mDatabaseHandler.addItem(item);

                            showSnackBarAdded(mCoordinatorLayout,item, searchItem, position);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.unable_fetch_item,item.getTitle()), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }, new ErrorCallback() {
                    @Override
                    public void onError(String message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                acceptButton.setVisibility(View.VISIBLE);
                            }
                        });
                        showErrorMessage(message);
                    }
                });
    }


    private void showSnackBarAdded(View view, final BucketListItem item, final SimpleSearchGson.SearchItem searchItem,
                                   final int position) {
        int color = AppResources.getFromAttrTheme(this,R.attr.bsAccentColor);
        Snackbar snackbar = Snackbar.make(view, getString(R.string.succes_item_added),
                            Snackbar.LENGTH_LONG).setAction(getString(R.string.btn_undo),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDatabaseHandler.remove(item.getId());
                                        mAdapter.add(position,searchItem);

                                        mAdapter.notifyDataSetChanged();
                                    }
                                }).setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                    }
                                }).setActionTextColor(color);
        snackbar.show();
    }

    private SimpleSearchGson.SearchItem removeFromList(final int position) {
        //This method is called on the Background



        SimpleSearchGson.SearchItem removed = mAdapter.getData().get(position);
        mAdapter.remove(position);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemRemoved(position);
            }
        });
        return removed;
    }


    @Override
    public void onBackPressed() {
        leaveActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_search:
                return true;
            case android.R.id.home:
                leaveActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void leaveActivity() {
        if(getIntent().getBooleanExtra(IS_MODIFIED,false)){
            //We update the cached values in case the user added something and we removed it
            //from the list
            mCacheManager.clearCache(mItemType);
            try {
                mCacheManager.save(mItemType,mAdapter.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent i = new Intent(getIntent());
        setResult(Activity.RESULT_OK, i);
        hasFinished = true;
        finish();
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        mItemSearcher.search(query, new SearchFinishedCallback<SimpleSearchGson.SearchItem>() {
            @Override
            public void onSearchFinished(List<SimpleSearchGson.SearchItem> data) {
                if (data != null) {

                    data = removeRepeats(data, mDatabaseHandler.getAllIds());

                    mAdapter.setData(data);
                    try {
                        mCacheManager.clearCache(mItemType);
                        mCacheManager.save(mItemType,data);
                    } catch (IOException e) {
                        mCacheManager.clearCache(mItemType);
                    }
                }
                updateListVisibility();
            }
        }, new ErrorCallback() {
            @Override
            public void onError(String message) {
                showErrorMessage(message);
            }
        });
        return true;
    }

    private List<SimpleSearchGson.SearchItem> removeRepeats(List<SimpleSearchGson.SearchItem> items
            , ArrayList<String> ids) {
        List<SimpleSearchGson.SearchItem> unique = new ArrayList<>();

        for (SimpleSearchGson.SearchItem item : items)
        {
          if(ids.contains(item.getId()))
              continue;
          unique.add(item);
        }
        return unique;
    }



    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void showErrorMessage(final String error){
        //Run on UI thread is needed because method is called inside a callback
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(mCoordinatorLayout,error, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks if the list is empty. if it is it removes the list and puts the textView
     */
    private void updateListVisibility(){
        //This method is called on the Background
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                //If the search shows nothing we put a placeholder instead of an empty list
                if (mAdapter.getItemCount() != 0) {
                    mSearchResultView.setVisibility(View.VISIBLE);
                    mTxtEmpty.setVisibility(View.GONE);
                } else {
                    mSearchResultView.setVisibility(View.GONE);
                    mTxtEmpty.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void startActivityWithAnimation(View clickedView,int position, BucketListItem result) {
        if (!hasFinished) {
            Intent detailIntent = new Intent(this, DetailActivity.class);
            String uniqueCoverTransitionName = "coverTransition" + position;

            //Animation information
            detailIntent.putExtra(DetailActivity.COVER_TRANSITION, uniqueCoverTransitionName);

            //Send data
            detailIntent.putExtra(DetailActivity.TITLE, result.getTitle());
            detailIntent.putExtra(DetailActivity.COVER, result.getCover());
            detailIntent.putExtra(DetailActivity.DESCRIPTION, result.getDescription());
            detailIntent.putExtra(DetailActivity.RATING, result.getRating());
            detailIntent.putExtra(DetailActivity.SEARCH,true);

            Pair<View,String> pair3 = new Pair<View, String>(clickedView.findViewById(R.id.iv_cover)
                    ,uniqueCoverTransitionName);

            //Start activity with the shared elements
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, pair3);

            startActivity(detailIntent, options.toBundle());
        }
    }
}
