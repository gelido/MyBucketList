package com.rafaelcarvalho.mybucketlist.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.activities.DetailActivity;
import com.rafaelcarvalho.mybucketlist.model.Book;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.model.BucketListMediaItem;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;
import com.rafaelcarvalho.mybucketlist.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rafael on 30/09/15.
 *
 * [WARNING] This class is not used anymore
 *
 */
public class BucketListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<Item> mData;
    private HashMap<BucketListItemType, List<Item>> mHiddenItems;


    public static final int GROUP = 0;
    public static final int LIST_ITEM = 1;

    private int mResourcesGroupLayout;
    private int mResourcesSubItemLayout;


    private ColorMatrixColorFilter mGrayScale;


    public BucketListRecyclerAdapter(Context context, HashMap<BucketListItemType, List<BucketListItem>> categoryItems, int resourcesGroupLayout, int resourcesSubItemLayout) {
        this.mContext = context;
        this.mResourcesGroupLayout = resourcesGroupLayout;
        this.mResourcesSubItemLayout = resourcesSubItemLayout;
        this.mData = new ArrayList<>();
        this.mHiddenItems = new HashMap<>();


        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        mGrayScale= new ColorMatrixColorFilter(matrix);
        computeData(categoryItems);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = null;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch(type){
            case GROUP:

                view = inflater.inflate(mResourcesGroupLayout, viewGroup, false);
                return new GroupViewHolder(view);
            case LIST_ITEM:
                view = inflater.inflate(mResourcesSubItemLayout, viewGroup, false);
                return new ItemViewHolder(view);
            default:

            return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final Item item = mData.get(position);

        switch (item.type){
            case GROUP:
                final GroupViewHolder groupHolder = (GroupViewHolder) viewHolder;
                groupHolder.txtCategory.setText(item.title);
                groupHolder.imageIcon.setImageResource(item.resourceId);
                groupHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(item.toggled){
                            int pos = mData.indexOf(item);

                            //Removes all of the subItems that from the clicked category and
                            while(mData.size()> pos + 1 && mData.get(pos+1).type == LIST_ITEM){
                                mData.remove(pos +1);
                            }

                            notifyDataSetChanged();
                            item.toggled = false;
                        }else{
                            int pos = mData.indexOf(item);
                            int index = pos + 1;
                            //Gets all the hidden items and puts them below the category
                            for(Item i : mHiddenItems.get(BucketListItemType.
                                    valueOf(item.title.toUpperCase()))){
                                mData.add(index,i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);

                            item.toggled = true;
                        }
                    }
                });
                break;
            case LIST_ITEM:
                final ItemViewHolder itemHolder = (ItemViewHolder) viewHolder;
                itemHolder.txtTitle.setText(mData.get(position).title);
                itemHolder.txtSubTitle.setText(mData.get(position).subTitle);
                Picasso.with(mContext).load(item.info.getCover()).placeholder(R.mipmap.ic_launcher)
                        .into(itemHolder.iv_cover);

                if(item.info.isSeen()){
                    //If the item was seen, image turns to gray scale
                    itemHolder.iv_cover.setColorFilter(mGrayScale);

                }

//                itemHolder.ib_archive.setChecked(item.info.isSeen());
                itemHolder.ib_archive.setOnClickListener(new View.OnClickListener() {
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
                break;

        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
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
        Item item = mData.get(position);
        detailIntent.putExtra(Constants.TITLE, item.title);
        detailIntent.putExtra(Constants.COVER, item.info.getCover());
        detailIntent.putExtra(Constants.DESCRIPTION, item.info.getDescription());
        detailIntent.putExtra(Constants.RATING, item.info.getRating());

        Pair<View,String> pair3 = new Pair<View, String>(v.findViewById(R.id.iv_cover_item)
                ,uniqueCoverTransitionName);

        //Start activity with the shared elements
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation((Activity)mContext, pair3);

        mContext.startActivity(detailIntent,options.toBundle());
    }

    public void computeData(HashMap<BucketListItemType, List<BucketListItem>> data) {
        //Add the categories to the list. They will always appear.
        mData = new ArrayList<>();
        for(BucketListItemType category: BucketListItemType.values()){
            mData.add(new Item(GROUP, category.getTitle(), category.getIcon()));
            List<Item> items = new ArrayList<>();
            for(BucketListItem info : data.get(category)){
                items.add(new Item(LIST_ITEM, info));
            }
            mHiddenItems.put(category,items);
        }
    }

    /**
     *  Holder for the Categories
     */
    private static class GroupViewHolder extends RecyclerView.ViewHolder{

        private TextView txtCategory;
        private ImageView imageIcon;

        public GroupViewHolder(View itemView) {
            super(itemView);

            txtCategory = (TextView) itemView.findViewById(R.id.tv_category);
            imageIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
        }
    }


    /**
     * Holder for the items.
     */
    private static class ItemViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTitle;
        private ImageView iv_cover;
        private ImageButton ib_archive;
        public TextView txtSubTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.tv_title);
            txtSubTitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            iv_cover = (ImageView) itemView.findViewById(R.id.iv_cover_item);
            ib_archive = (ImageButton) itemView.findViewById(R.id.ib_archive);
        }
    }


    /**
     * Class created to contain the info for both classes and subItems
     */
    private static class Item{
        private BucketListItem info;
        public int type; // GROUP or LIST_ITEM
        public String title; //cotains category name, or title of itemList
        public String subTitle;
        public int resourceId;
        public boolean toggled; // only for Categories



        public Item(int type, String title, int resourceId) {
            this.type = type;
            this.title = title;
            this.toggled = false;
            this.resourceId = resourceId;
            this.info = null;
            this.subTitle = null;
        }

        public Item(int type, BucketListItem bucketListItem) {
            this.type = type;
            this.title = bucketListItem.getTitle();
            this.resourceId = 0;
            this.toggled = false;
            this.info = bucketListItem;
            if(bucketListItem instanceof Book){
                this.subTitle = ((Book)bucketListItem).getAuthor();
            }else if(bucketListItem instanceof BucketListMediaItem){
                this.subTitle = ((BucketListMediaItem) bucketListItem).getYear();
            }

        }
    }
}
