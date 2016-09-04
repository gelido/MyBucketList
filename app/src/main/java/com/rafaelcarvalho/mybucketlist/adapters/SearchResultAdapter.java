package com.rafaelcarvalho.mybucketlist.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rafaelcarvalho.mybucketlist.Interfaces.AddItemHandler;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.activities.SearchActivity;
import com.rafaelcarvalho.mybucketlist.Interfaces.DetailFetcher;
import com.rafaelcarvalho.mybucketlist.Interfaces.DetailFetcherCallback;
import com.rafaelcarvalho.mybucketlist.Interfaces.ErrorCallback;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.gson.SimpleSearchGson;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Rafael on 16/10/15.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private DetailFetcher mFetcher;
    private Context mContext;
    private List<SimpleSearchGson.SearchItem> mData;
    private AddItemHandler mAddItemHandler;

    private int mResourceLayoutItem;

    public SearchResultAdapter(final Context context, List<SimpleSearchGson.SearchItem> data
            ,int resourceLayoutItem, DetailFetcher fetcher) {
        this.mContext = context;
        this.mData = data;
        this.mFetcher = fetcher;
        this.mResourceLayoutItem = resourceLayoutItem;
        try{
            this.mAddItemHandler = (AddItemHandler) context;
        }catch(ClassCastException e){
            throw new UnsupportedOperationException("Context for this class must implement " +
                    "AddItemHandler");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(mResourceLayoutItem, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemHolder = (ItemViewHolder) holder;
        itemHolder.bind(mContext, mData.get(position));

        final SimpleSearchGson.SearchItem searchItem = mData.get(position);

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                //start the progressbar here and close in on the callbacks
                final ProgressBar progressBar = ((ItemViewHolder) holder).getProgressBar();
                progressBar.setIndeterminate(true);
                progressBar.setVisibility(View.VISIBLE);



                mFetcher.fetchDetails(searchItem.getId(), position, new DetailFetcherCallback() {
                    @Override
                    public void onFetchFinished(final BucketListItem item) {
                        //Has to run on UI Thread because this is a callback
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((SearchActivity) mContext).
                                        startActivityWithAnimation(v, position, item);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }, new ErrorCallback() {
                    @Override
                    public void onError(final String message) {
                        //Has to run on UI Thread because this is a callback
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, message,Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });
            }
        });

        itemHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ItemViewHolder) holder).getProgressBar().setIndeterminate(true);
                ((ItemViewHolder) holder).getProgressBar().setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).getBtnAccept().setVisibility(View.INVISIBLE);
                mAddItemHandler.addItem(holder, mFetcher);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<SimpleSearchGson.SearchItem> mData) {
        this.mData = mData;
    }

    public List<SimpleSearchGson.SearchItem> getData() {
        return mData;
    }

    public void add(int location, SimpleSearchGson.SearchItem item){
        mData.add(location, item);
    }

    public void remove(int position){
        mData.remove(position);
    }

    public String getBucketItemId(int position) {
        return mData.get(position).getId();
    }


    /**
     * Holder for the items.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTitle;
        private ImageView iv_cover;
        private TextView txtYear;
        private ProgressBar progressBar;
        private ImageButton btnAccept;

        public ItemViewHolder(View itemView) {
            super(itemView);

            txtYear = (TextView) itemView.findViewById(R.id.tv_subtitle);
            txtTitle = (TextView) itemView.findViewById(R.id.tv_title);
            iv_cover = (ImageView) itemView.findViewById(R.id.iv_cover);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            btnAccept = (ImageButton) itemView.findViewById(R.id.btn_accept);
        }

        public void bind(Context context,SimpleSearchGson.SearchItem item){
            txtTitle.setText(item.title);
            txtYear.setText(item.year);
            Picasso.with(context).load(item.poster).placeholder(R.mipmap.ic_launcher)
                    .into(iv_cover);
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public ImageButton getBtnAccept() {
            return btnAccept;
        }
    }



}
