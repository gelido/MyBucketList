package com.rafaelcarvalho.mybucketlist.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.activities.DetailActivity;
import com.rafaelcarvalho.mybucketlist.model.Book;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.model.BucketListMediaItem;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rafael on 30/09/15.
 */
public class BucketListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<Item> mData;

    private int mResourcesItemLayout;


    private ColorMatrixColorFilter mGrayScale;


    public BucketListAdapter(Context context, List<BucketListItem> items, int resourcesItemLayout) {
        this.mContext = context;
        this.mResourcesItemLayout = resourcesItemLayout;
        this.mData = new ArrayList<>();

        computeData(items);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        mGrayScale= new ColorMatrixColorFilter(matrix);
    }

    private void computeData(List<BucketListItem> items) {
        for(BucketListItem item: items){
            mData.add(new Item(item));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = null;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(mResourcesItemLayout, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final Item item = mData.get(position);

        final ItemViewHolder itemHolder = (ItemViewHolder) viewHolder;
        itemHolder.txtTitle.setText(mData.get(position).title);
        itemHolder.txtSubTitle.setText(mData.get(position).subTitle);
        Picasso.with(mContext).load(item.info.getCover()).placeholder(R.mipmap.ic_launcher)
                .into(itemHolder.iv_cover);

        if(item.info.isSeen()){
            //If the item was seen, image turns to gray scale
            itemHolder.iv_cover.setColorFilter(mGrayScale);

        }

        itemHolder.switch_seen.setChecked(item.info.isSeen());
        itemHolder.switch_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!item.info.isSeen()){
                    //apply gray scale
                    itemHolder.iv_cover.setColorFilter(mGrayScale);
                    item.info.setSeen(true);
                }else{
                    //remove gray scale
                    itemHolder.iv_cover.setColorFilter(null);
                    item.info.setSeen(false);
                }
            }
        });

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAnimation(v, position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    /**
     * This method prepares the transition names for the shared elements and starts the activity
     *
     * @param v view that was clicked and contains the holder
     * @param position position on the recyclerView
     */
    private void startActivityWithAnimation(View v, int position) {
        Intent detailIntent = new Intent(mContext, DetailActivity.class);
        String uniqueCoverTransitionName = "coverTransition" + position;

        //Animation information
        detailIntent.putExtra(DetailActivity.COVER_TRANSITION, uniqueCoverTransitionName);

        //Send data
        Item item = mData.get(position);
        detailIntent.putExtra(DetailActivity.TITLE, item.title);
        detailIntent.putExtra(DetailActivity.COVER, item.info.getCover());
        detailIntent.putExtra(DetailActivity.DESCRIPTION, item.info.getDescription());
        detailIntent.putExtra(DetailActivity.RATING, item.info.getRating());

        Pair<View,String> pair3 = new Pair<View, String>(v.findViewById(R.id.iv_cover_item)
                ,uniqueCoverTransitionName);

        //Start activity with the shared elements
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation((Activity)mContext, pair3);

        mContext.startActivity(detailIntent,options.toBundle());
    }


    /**
     * Holder for the items.
     */
    private static class ItemViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTitle;
        private ImageView iv_cover;
        private SwitchCompat switch_seen;
        public TextView txtSubTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.tv_title);
            txtSubTitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            iv_cover = (ImageView) itemView.findViewById(R.id.iv_cover_item);
            switch_seen = (SwitchCompat) itemView.findViewById(R.id.switch_seen);
        }
    }


    /**
     * Class created to contain the info for each category and subItems
     */
    private static class Item{
        private BucketListItem info;
        public String title; //contains category name, or title of itemList
        public String subTitle;

        public Item(BucketListItem bucketListItem) {
            this.title = bucketListItem.getTitle();
            this.info = bucketListItem;
            if(bucketListItem instanceof Book){
                this.subTitle = ((Book)bucketListItem).getAuthor();
            }else if(bucketListItem instanceof BucketListMediaItem){
                this.subTitle = ((BucketListMediaItem) bucketListItem).getYear();
            }

        }
    }
}
