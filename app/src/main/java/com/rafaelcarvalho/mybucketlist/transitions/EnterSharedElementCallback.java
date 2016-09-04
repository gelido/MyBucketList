package com.rafaelcarvalho.mybucketlist.transitions;

import android.app.SharedElementCallback;
import android.view.View;

import com.rafaelcarvalho.mybucketlist.Interfaces.OnAnimationEndListener;

import java.util.List;

/**
 * Created by Rafael on 04/10/15.
 */
public class EnterSharedElementCallback extends SharedElementCallback {

    private static final String TAG = "EnterSharedElementCallback";

    private OnAnimationEndListener mListener;

    public EnterSharedElementCallback(OnAnimationEndListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {


//            View view = sharedElements.get(0);
//            int cx = view.getWidth() / 2;
//            int cy = view.getHeight() /2;
//
//            // get the final radius for the clipping circle
//            int finalRadius = Math.max(view.getWidth(), view.getHeight());
//
//            // create the animator for this view (the start radius is zero)
//            Animator anim =
//                    ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
//            anim.setDuration(1500);
//            anim.setInterpolator(new AccelerateDecelerateInterpolator());
//
//
//            // make the view visible and start the animation
//            view.setVisibility(View.VISIBLE);
//            anim.start();



    }

    @Override
    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
        super.onSharedElementEnd(sharedElementNames,sharedElements,sharedElementSnapshots);
//        View view = sharedElements.get(0);
//        int cx = view.getWidth() / 2;
//        int cy = view.getHeight() /2;
//
//        // get the final radius for the clipping circle
//        int finalRadius = Math.max(view.getWidth(), view.getHeight());
//
//        // create the animator for this view (the start radius is zero)
//        Animator anim =
//                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
//        anim.setDuration(1500);
//        anim.setInterpolator(new AccelerateDecelerateInterpolator());
//
//
//        // make the view visible and start the animation
//        view.setVisibility(View.VISIBLE);
//        anim.start();
    }

    @Override
    public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, OnSharedElementsReadyListener listener) {
        super.onSharedElementsArrived(sharedElementNames, sharedElements, listener);
        //mListener.onAnimationEnd();
    }
}
