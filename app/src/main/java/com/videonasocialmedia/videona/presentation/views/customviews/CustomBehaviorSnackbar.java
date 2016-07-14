package com.videonasocialmedia.videona.presentation.views.customviews;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ruth on 12/07/16.
 */
public class CustomBehaviorSnackbar extends CoordinatorLayout.Behavior<View> {

    public CustomBehaviorSnackbar(Context context, AttributeSet attributes){

        super(context, attributes);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {


       float translationY = Math.min(0, dependency.getTranslationY()-dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }
}
