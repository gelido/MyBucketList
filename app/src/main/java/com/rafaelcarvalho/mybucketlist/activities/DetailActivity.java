package com.rafaelcarvalho.mybucketlist.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rafaelcarvalho.mybucketlist.Interfaces.IDatabaseHandler;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.database.DatabaseHandler;
import com.rafaelcarvalho.mybucketlist.fragments.SettingsActivityFragment;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.AppResources;
import com.rafaelcarvalho.mybucketlist.util.Constants;
import com.squareup.picasso.Picasso;

import static com.rafaelcarvalho.mybucketlist.util.Constants.COVER;
import static com.rafaelcarvalho.mybucketlist.util.Constants.COVER_TRANSITION;
import static com.rafaelcarvalho.mybucketlist.util.Constants.DESCRIPTION;
import static com.rafaelcarvalho.mybucketlist.util.Constants.ITEM_ID;
import static com.rafaelcarvalho.mybucketlist.util.Constants.RATING;
import static com.rafaelcarvalho.mybucketlist.util.Constants.SEARCH;
import static com.rafaelcarvalho.mybucketlist.util.Constants.TITLE;

public class DetailActivity extends AppCompatActivity {


    public static final String IS_DELETED = DetailActivity.class.getSimpleName()+ "Deleted";


    private TextView mTxtRating;
    private TextView mTxtDescription;
    private ImageView mCover;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private RatingBar mRatingBar;
    private IDatabaseHandler mDatabaseHandler;


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
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra(TITLE));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

        if(mCollapsingToolbar != null) {
            mCollapsingToolbar.setTitle(getIntent().getStringExtra(TITLE));
        }

        mDatabaseHandler = DatabaseHandler.getDatabaseReference(this);

        mCover = (ImageView) findViewById(R.id.iv_cover);
        Picasso.with(this).load(getIntent().getStringExtra(COVER)).into(mCover);
        mCover.setTransitionName(getIntent().getStringExtra(COVER_TRANSITION));


        mTxtDescription = (TextView) findViewById(R.id.tv_description);
        mTxtDescription.setText(getIntent().getStringExtra(DESCRIPTION));

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        float rating = getIntent().getFloatExtra(RATING, 0);
        mRatingBar.setRating(rating);
        //Changing the color of the stars on the rating bar
        LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(AppResources.
                getFromAttrTheme(this,R.attr.bsAccentColor), PorterDuff.Mode.SRC_ATOP);


        mTxtRating = (TextView) findViewById(R.id.tv_rating_number);
        mTxtRating.setText(rating + "/" + mRatingBar.getNumStars());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int resource = R.menu.menu_detail;

        if(getIntent().getBooleanExtra(SEARCH,false))
            resource =  R.menu.menu_detail_search;

        getMenuInflater().inflate(resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                return true;
            case R.id.action_add:
                //TODO: Finish this method
                return true;
            case R.id.action_remove:
                removeFromDBAndFinish();
                return true;
            case android.R.id.home:
                finishWithResult();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void removeFromDBAndFinish() {
        String id = getIntent().getStringExtra(ITEM_ID);
        BucketListItem item = mDatabaseHandler.remove(id);
        getIntent().putExtra(IS_DELETED, true);
        showSnackBarAdded(mCollapsingToolbar, item);
    }

    private void showSnackBarAdded(View view, final BucketListItem item) {
        int color = AppResources.getFromAttrTheme(this,R.attr.bsAccentColor);
        Snackbar snackbar = Snackbar.make(view, R.string.success_item_remove,
                Snackbar.LENGTH_LONG).setAction(getString(R.string.btn_undo),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabaseHandler.addItem(item);
                        getIntent().putExtra(IS_DELETED, false);
                    }
                }).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if(event != DISMISS_EVENT_ACTION){
                    finishWithResult();
                }
            }
        }).setActionTextColor(color);
        snackbar.show();
    }

    private void finishWithResult() {
        Intent i = new Intent(getIntent());
        setResult(Activity.RESULT_OK, i);
        finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
    }
}
