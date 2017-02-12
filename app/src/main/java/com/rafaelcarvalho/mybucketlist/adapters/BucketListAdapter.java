package com.rafaelcarvalho.mybucketlist.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.ColorMatrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rafaelcarvalho.mybucketlist.Interfaces.OnListChangeListener;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.activities.DetailActivity;
import com.rafaelcarvalho.mybucketlist.activities.NavigationDrawerActivity;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.AppResources;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;
import com.rafaelcarvalho.mybucketlist.util.Constants;
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

    private OnListChangeListener changeListener;

    private BucketListItemType mType;

    private boolean mIsArchive;

    public BucketListAdapter(Context context, List<BucketListItem> items, int resourcesItemLayout,
                             OnListChangeListener listener, BucketListItemType type, boolean isArchive) {
        this.mContext = context;
        this.mResourcesItemLayout = resourcesItemLayout;
        this.mData = new ArrayList<>();
        this.mType = type;
        this.mIsArchive = isArchive;
        if (listener == null){
            this.changeListener = (NavigationDrawerActivity)
                    mContext;
        }else{
            this.changeListener = listener;
        }

        sortData(items);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
    }

    private void sortData(List<BucketListItem> items) {
        mData.clear();
        mData.addAll(items);
        Collections.sort(mData, mItemComparator);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = null;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(mResourcesItemLayout, viewGroup, false);
        final ItemViewHolder viewHolder = new ItemViewHolder(view);

        viewHolder.ib_archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newValue = !mIsArchive;
                final BucketListItem item = viewHolder.getItem();
                if(item == null){
                    Toast.makeText(mContext,mContext.getString(R.string.error_checking_item),Toast.LENGTH_SHORT).show();
                    return;
                }
                final int position = mData.indexOf(item);
                mData.remove(position);

                notifyItemRemoved(position);

                setSeenDrawable(newValue, viewHolder);

                //To avoid constant DB access instead of adding to the DB every time we change, we'll
                // add it to a list that does everything in one go
                final Modification<Boolean> mod = new Modification<>(Modification.Type.UPDATE, item.getId());
                mod.setField(Modification.Field.SEEN);
                mod.setNewValue(newValue);
                changeListener.itemChanged(mod);

                int color = AppResources.getFromAttrTheme(mContext,R.attr.bsAccentColor);
                Snackbar snackbar = Snackbar.make(v, mContext.getString(R.string.item_archived),
                        Snackbar.LENGTH_LONG).setAction(mContext.getString(R.string.btn_undo),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeListener.removeChange(mod);
                                mData.add(position,item);
                                notifyItemInserted(position);
                            }
                        }).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                    }
                }).setActionTextColor(color);
                snackbar.show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final BucketListItem item = mData.get(position);

        final ItemViewHolder itemHolder = (ItemViewHolder) viewHolder;
        itemHolder.txtTitle.setText(mData.get(position).getTitle());
        itemHolder.txtSubTitle.setText(mData.get(position).getSubtitle());
        Picasso.with(mContext).load(item.getCover()).placeholder(R.mipmap.ic_launcher)
                .into(itemHolder.iv_cover);

        setSeenDrawable(mIsArchive,itemHolder);
        itemHolder.setItem(item);


        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAnimation(v, position);
            }
        });
    }



    private void setSeenDrawable(boolean isSeen, ItemViewHolder itemHolder) {
        Drawable drawable = mContext.getDrawable(isSeen ? R.drawable.ic_visibility_black_24dp : R.drawable.ic_visibility_off_black_24dp);
        if (isSeen) {
            drawable.setColorFilter(AppResources.getFromAttrTheme(mContext,R.attr.bsPrimaryColor), PorterDuff.Mode.SRC_ATOP);
        }
        itemHolder.ib_archive.setImageDrawable(drawable);
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
        detailIntent.putExtra(Constants.COVER_TRANSITION, uniqueCoverTransitionName);

        //Send data
        BucketListItem item = mData.get(position);
        detailIntent.putExtra(Constants.TITLE, item.getTitle());
        detailIntent.putExtra(Constants.COVER, item.getCover());
        detailIntent.putExtra(Constants.DESCRIPTION, item.getDescription());
        detailIntent.putExtra(Constants.RATING, item.getRating());
        detailIntent.putExtra(Constants.ITEM_ID,item.getId());
        detailIntent.putExtra(Constants.ITEM_TYPE, mType.ordinal());
        //Create a pair so the animation knows where to go.
        Pair<View,String> pair3 = new Pair<View, String>(v.findViewById(R.id.iv_cover_item)
                ,uniqueCoverTransitionName);

        //Start activity with the shared elements
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation((Activity)mContext, pair3);

        ((AppCompatActivity)mContext).startActivityForResult(detailIntent,Constants.ITEM_DETAIL,options.toBundle());
    }

    public List<BucketListItem> getData() {
        return mData;
    }

    public void setData(List<BucketListItem> mData) {
        sortData(mData);
    }


    /**
     * Holder for the items.
     */
    private static class ItemViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTitle;
        private ImageView iv_cover;
        private ImageButton ib_archive;
        private TextView txtSubTitle;
        private BucketListItem item;

        public ItemViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.tv_title);
            txtSubTitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            iv_cover = (ImageView) itemView.findViewById(R.id.iv_cover_item);
            ib_archive = (ImageButton) itemView.findViewById(R.id.ib_archive);
        }

        public BucketListItem getItem() {
            return item;
        }

        public void setItem(BucketListItem item) {
            this.item = item;
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
