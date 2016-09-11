package com.rafaelcarvalho.mybucketlist.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rafaelcarvalho.mybucketlist.Interfaces.OnListChangeListener;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.activities.DetailActivity;
import com.rafaelcarvalho.mybucketlist.activities.TabbedListsActivity;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.Modification;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Rafael on 30/09/15.
 */
public class BucketListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<BucketListItem> mData;

    private int mResourcesItemLayout;

    private Comparator<BucketListItem> mItemComparator = new ItemComparator<>();

    private ColorMatrixColorFilter mGrayScale;

    private OnListChangeListener changeListener;


    public BucketListAdapter(Context context, List<BucketListItem> items, int resourcesItemLayout,
                             OnListChangeListener listener) {
        this.mContext = context;
        this.mResourcesItemLayout = resourcesItemLayout;
        this.mData = new ArrayList<>();
        if (listener == null){
            this.changeListener = (TabbedListsActivity)
                    mContext;
        }else{
            this.changeListener = listener;
        }

        sortData(items);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        mGrayScale= new ColorMatrixColorFilter(matrix);
    }

    private void sortData(List<BucketListItem> items) {
        mData = items;
        Collections.sort(mData, mItemComparator);
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
        final BucketListItem item = mData.get(position);

        final ItemViewHolder itemHolder = (ItemViewHolder) viewHolder;
        itemHolder.txtTitle.setText(mData.get(position).getTitle());
        itemHolder.txtSubTitle.setText(mData.get(position).getSubtitle());
        Picasso.with(mContext).load(item.getCover()).placeholder(R.mipmap.ic_launcher)
                .into(itemHolder.iv_cover);

        if(item.isSeen()){
            //If the item was seen, image turns to gray scale
            itemHolder.iv_cover.setColorFilter(mGrayScale);

        }

        itemHolder.switch_seen.setChecked(item.isSeen());
        itemHolder.switch_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newValue;

                if(!item.isSeen()){
                    //apply gray scale
                    itemHolder.iv_cover.setColorFilter(mGrayScale);
                    newValue = true;
                    item.setSeen(true);
                }else{
                    //remove gray scale
                    itemHolder.iv_cover.setColorFilter(null);
                    newValue = false;
                    item.setSeen(false);
                }

                sortData(mData);
                int newPosition = mData.indexOf(item);
                notifyItemMoved(viewHolder.getAdapterPosition(),newPosition);

                //To avoid constant DB access instead of adding to the DB every time we change, we'll
                // add it to a list that does everything in one go
                Modification<Boolean> mod = new Modification<>(Modification.Type.UPDATE, item.getId());
                mod.setField(Modification.Field.SEEN);
                mod.setNewValue(newValue);
                changeListener.itemChanged(mod);
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
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        ((ItemViewHolder) holder).iv_cover.setColorFilter(null);
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
        BucketListItem item = mData.get(position);
        detailIntent.putExtra(DetailActivity.TITLE, item.getTitle());
        detailIntent.putExtra(DetailActivity.COVER, item.getCover());
        detailIntent.putExtra(DetailActivity.DESCRIPTION, item.getDescription());
        detailIntent.putExtra(DetailActivity.RATING, item.getRating());

        Pair<View,String> pair3 = new Pair<View, String>(v.findViewById(R.id.iv_cover_item)
                ,uniqueCoverTransitionName);

        //Start activity with the shared elements
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation((Activity)mContext, pair3);

        mContext.startActivity(detailIntent,options.toBundle());
    }

    public List<BucketListItem> getData() {
        return mData;
    }

    public void setData(List<BucketListItem> mData) {
        this.mData = mData;
        sortData(mData);
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


    private class ItemComparator <T extends BucketListItem> implements Comparator<T>
    {

        @Override
        public int compare(T lItem, T rItem) {
            boolean isSame = lItem.isSeen() == rItem.isSeen();
            return (!isSame)? (lItem.isSeen()?1:-1): compareTitle(lItem, rItem);
        }

        private int compareTitle(T lItem, T rItem) {
            return lItem.getTitle().compareTo(rItem.getTitle());
        }


    }
}
