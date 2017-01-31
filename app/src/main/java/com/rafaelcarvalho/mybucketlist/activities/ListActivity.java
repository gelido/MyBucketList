package com.rafaelcarvalho.mybucketlist.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.adapters.BucketListRecyclerAdapter;
import com.rafaelcarvalho.mybucketlist.animators.AnimationHelper;
import com.rafaelcarvalho.mybucketlist.database.DatabaseHandler;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;
import com.rafaelcarvalho.mybucketlist.util.FloatingActionButtonMenu;

import java.util.HashMap;
import java.util.List;

public class ListActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {

    private static final int ADD_ITEM = 1;
    private RecyclerView mBucketListView;
    private FloatingActionButton mFabAdd;
    private FloatingActionButton mFabAddMovies;
    private FloatingActionButton mFabAddSeries;
    private FloatingActionButton mFabAddBooks;
    private FloatingActionButtonMenu mFabMenu;
    private LinearLayout mHoverLayout;

    private AnimationHelper mAnimationHelper;
    private DatabaseHandler mDatabaseHandler;
    private BucketListRecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        init();
    }

    private void init() {

        //instantiate the DB
        mDatabaseHandler = DatabaseHandler.getDatabaseReference(this);

        //init the recycler view
        HashMap<BucketListItemType,List<BucketListItem>> itemMap = mDatabaseHandler.getAll();
        mBucketListView = (RecyclerView) findViewById(R.id.rv_bucketlist);
        mAdapter = new BucketListRecyclerAdapter(this, itemMap
                , R.layout.list_groupitem_item, R.layout.list_item_item);
        mBucketListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBucketListView.setAdapter(mAdapter);

        //create the FAButton
        mFabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        mFabAdd.setOnClickListener(this);
        mFabMenu = (FloatingActionButtonMenu) findViewById(R.id.fab_menu);
        mFabMenu.setMenuButton(mFabAdd);

        mFabAddMovies = (FloatingActionButton) findViewById(R.id.fab_movies);
        mFabAddSeries = (FloatingActionButton) findViewById(R.id.fab_series);
        mFabAddBooks = (FloatingActionButton) findViewById(R.id.fab_books);

        mFabAddMovies.setOnClickListener(this);
        mFabAddSeries.setOnClickListener(this);
        mFabAddBooks.setOnClickListener(this);

        mFabAdd.setOnLongClickListener(this);



        //Instantiate the helper that is going to handle the intro and close animations
        mHoverLayout = (LinearLayout) findViewById(R.id.hover_layout);
        //get the accent color from the theme
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.bsAccentColor, typedValue, true);
        int color = typedValue.data;
        mHoverLayout.setBackgroundColor(color);
        mHoverLayout.setVisibility(View.INVISIBLE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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
    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.fab_add:
                mFabMenu.toggle();
                break;
            case R.id.fab_movies:
                handleAddClickTo(BucketListItemType.MOVIES, v);
                break;
            case R.id.fab_series:
                handleAddClickTo(BucketListItemType.SERIES,v);
                break;
            case R.id.fab_books:
                handleAddClickTo(BucketListItemType.BOOKS,v);
                break;

        }

    }

    private void updateList(){
        mAdapter.computeData(mDatabaseHandler.getAll());
        mAdapter.notifyDataSetChanged();
    }

    private void handleAddClickTo(BucketListItemType type, final View source) {
        final Intent searchIntent = new Intent(this, SearchActivity.class);
        searchIntent.putExtra(TabbedListsActivity.ITEM_TYPE, type.ordinal());

        //Get the center of the FAButton clicked, so the animation knows where to start
        float x = source.getX() +(source.getWidth()/2);
        float y = source.getY() + (source.getHeight()/2);
        searchIntent.putExtra(SearchActivity.X_VALUE_SHOW, x);
        searchIntent.putExtra(SearchActivity.Y_VALUE_SHOW, y);
        searchIntent.putExtra(SearchActivity.VIEW_ID, source.getId());
        mAnimationHelper = new AnimationHelper(this,mHoverLayout,(int)x, (int)y);
        mAnimationHelper.setCallback(new AnimationHelper.AnimationEndCallback() {
            @Override
            public void onAnimationEndCallback() {
                startActivityForResult(searchIntent, ADD_ITEM);
            }
        });
        mAnimationHelper.circularRevealActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == ADD_ITEM){
                if(mAnimationHelper != null){

                    if(data.getBooleanExtra(SearchActivity.IS_MODIFIED, false))
                        updateList();

                    animateCircularHide(data);
                }else{
                    //Probably user changed orientation and reset the views.
                    //Make sure the animation has the views to be performed
                    mHoverLayout.setVisibility(View.VISIBLE);
                    ViewTreeObserver viewTreeObserver = mHoverLayout.getViewTreeObserver();
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                animateCircularHide(data);
                                mHoverLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }

                }

            }
        }
    }

    private void animateCircularHide(Intent data) {
        if(mAnimationHelper == null)
            mAnimationHelper = new AnimationHelper(ListActivity.this,mHoverLayout,0, 0);

        //Update the button location (In case user changes to landscape)
        View source = findViewById(data.getIntExtra(SearchActivity.VIEW_ID, 0));
        //Get the center of the FAButton clicked, so the animation knows where to start
        float x = source.getX() +(source.getWidth()/2);
        float y = source.getY() + (source.getHeight()/2);

        mAnimationHelper.setX((int) x);
        mAnimationHelper.setY((int) y);

        //When the animation ends I want the button to toggle off.
        mAnimationHelper.setCallback(new AnimationHelper.AnimationEndCallback() {
            @Override
            public void onAnimationEndCallback() {
                mHoverLayout.setVisibility(View.INVISIBLE);
                mFabMenu.toggle();
            }
        });
        mAnimationHelper.circularHideActivity();
    }

    /**
     * Method used to improve UX giving a hint on the FAButton
     *
     * @param v
     * @return
     */
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.fab_add:
                Snackbar.make(mFabAdd, R.string.hint_add_item, Snackbar.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
