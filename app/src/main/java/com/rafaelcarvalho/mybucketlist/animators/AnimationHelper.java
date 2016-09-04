package com.rafaelcarvalho.mybucketlist.animators;

import android.animation.Animator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.rafaelcarvalho.mybucketlist.R;

/**
 * Created by Rafael on 08/11/15.
 */
public class AnimationHelper {

    private Context mContext;
    private LinearLayout mHoverLayout;
    private int cx;
    private int cy;
    private AnimationEndCallback callback;

    public AnimationHelper(Context context, LinearLayout hoverLayout,
                           int cx, int cy) {
        this.mContext = context;
        this.mHoverLayout = hoverLayout;
        this.cx = cx;
        this.cy = cy;
    }

    /**
     * This method reveals the overlay layout so and fades it so we get a nice animation
     * for the start of the activity
     */
    public void circularRevealActivity() {

        float finalRadius = Math.max(mHoverLayout.getWidth(), mHoverLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(mHoverLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(250);
        //Add a Fade when the animation finishes
        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mHoverLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (callback != null)
                    callback.onAnimationEndCallback();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        circularReveal.start();
    }

    /**
     * This method shows the overlayed layout and it hides it with a circular effect
     * and finishes the activity
     */
    public void circularHideActivity(){


        //Since we are hiding, the starting radious is the max one and it finishes in 0
        float initialRadius = Math.max(mHoverLayout.getWidth(), mHoverLayout.getHeight());



        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(mHoverLayout, cx, cy, initialRadius, 0);
        circularReveal.setDuration(500);

        //Before we start with the hide, we show the overlay with a Fade.
        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(callback != null)
                    callback.onAnimationEndCallback();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        circularReveal.start();

    }


    public void setCallback(AnimationEndCallback callback) {
        this.callback = callback;
    }

    public void setX(int cx) {
        this.cx = cx;
    }

    public void setY(int cy) {
        this.cy = cy;
    }

    public interface AnimationEndCallback{
        public void onAnimationEndCallback();
    }

}
