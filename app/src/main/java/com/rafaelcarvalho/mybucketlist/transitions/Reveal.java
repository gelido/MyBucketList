package com.rafaelcarvalho.mybucketlist.transitions;


import android.animation.Animator;
import android.content.Context;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

/**
 * Created by Rafael on 25/10/15.
 */
public class Reveal extends Visibility {

    public Reveal(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return createRevealAnimator(view, Math.max(view.getWidth(), view.getHeight()));
    }


    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return createRevealAnimator(view,0);
    }

    public Animator createRevealAnimator(View view,int finalRadius) {
        int cx = view.getWidth()/2;
        int cy = view.getHeight()/2;

        return ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
    }

}
