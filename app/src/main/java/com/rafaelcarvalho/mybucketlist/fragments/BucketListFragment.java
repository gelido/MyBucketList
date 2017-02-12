package com.rafaelcarvalho.mybucketlist.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rafaelcarvalho.mybucketlist.Interfaces.OnListChangeListener;
import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.adapters.BucketListAdapter;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BucketListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BucketListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BucketListFragment extends Fragment{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ITEMS = "items";

    private RecyclerView mBucketListView;

    private OnFragmentInteractionListener mListener;

    private boolean mIsArchived;

    private BucketListAdapter mAdapter;

    private BucketListItemType mType;
    private OnListChangeListener mChangeListener;
    private TextView mTxtEmpty;


    public BucketListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BucketListFragment.
     */
    public static BucketListFragment newInstance(List<BucketListItem> items,
                                                 BucketListItemType type, OnListChangeListener listener,
                                                    boolean isSeen) {
        BucketListFragment fragment = new BucketListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ITEMS, (ArrayList<? extends Parcelable>) items);
        fragment.setType(type);
        fragment.setIsSeen(isSeen);
        fragment.setChangeListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<BucketListItem> items;
        if (getArguments() != null) {
             items = getArguments().getParcelableArrayList(ARG_ITEMS);
        }else{
            items = new ArrayList<>();
        }
        mAdapter = new BucketListAdapter(getActivity(), items, R.layout.list_item_item, mChangeListener, mType, mIsArchived);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_list, container, false);


        mBucketListView = (RecyclerView) root.findViewById(R.id.rv_bucketlist);

        mTxtEmpty = (TextView) root.findViewById(R.id.tv_empty);

        //This is needed, can't remember why though
        mBucketListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBucketListView.setAdapter(mAdapter);

        alterVisibility();
        return root;
    }

    private void alterVisibility() {
        //If the search shows nothing we put a placeholder instead of an empty list
        if (mAdapter.getItemCount() > 0) {
            mBucketListView.setVisibility(View.VISIBLE);
            mTxtEmpty.setVisibility(View.GONE);
        } else {
            mBucketListView.setVisibility(View.GONE);
            mTxtEmpty.setVisibility(View.VISIBLE);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean isSeen() {
        return mIsArchived;
    }

    public void setIsSeen(boolean mIsSeen) {
        this.mIsArchived = mIsSeen;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public BucketListItemType getType() {
        return mType;
    }

    public void setType(BucketListItemType type) {
        this.mType = type;
    }


    public OnListChangeListener getChangeListener() {
        return mChangeListener;
    }

    public void setChangeListener(OnListChangeListener mChangeListener) {
        this.mChangeListener = mChangeListener;
    }

    public void updateList(List<BucketListItem> items){
        mAdapter.setData(items);
        alterVisibility();
        mAdapter.notifyDataSetChanged();
    }

}
