package com.rafaelcarvalho.mybucketlist.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rafaelcarvalho.mybucketlist.R;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public static final String TITLE = "title";
    public static final String COVER = "cover";
    public static final String DESCRIPTION = "description";
    public static final String RATING = "rating";
    public static final String COVER_TRANSITION = "coverTransition";
    public static final String SEARCH = "search";

    private TextView mTxtRating;
    private TextView mTxtDescription;
    private ImageView mCover;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private RatingBar mRatingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        mCollapsingToolbar.setTitle(getIntent().getStringExtra(TITLE));


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
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.accent), PorterDuff.Mode.SRC_ATOP);


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
            case android.R.id.home:
                finishAfterTransition();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


}
