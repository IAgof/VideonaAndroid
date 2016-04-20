package com.videonasocialmedia.videona.presentation.views.customviews;
/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;

public class AspectRatioVideoView extends VideoView {

    private static final int FOUR_THREE_ATTR = 1;
    private static final int SIXTEEN_NINE_ATTR = 2;
    private static final double FOUR_THREE_VALUE = 1.333333D;
    private static final double SIXTEEN_NINE_VALUE = 1.777778D;
    private static final String TAG = "AFL";
    private double mTargetAspect = -1.0D;

    public AspectRatioVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initAspectRatio(context, attrs, 0);
    }

    public AspectRatioVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initAspectRatio(context, attrs, defStyle);
    }

    public AspectRatioVideoView(Context context) {
        super(context);
    }



    private void initAspectRatio(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioVideoView, defStyleAttr, 0);
        int attrAspect = a.getInt(R.styleable.AspectRatioVideoView_aspect, 1);
        switch(attrAspect) {
            case 1:
                this.mTargetAspect = 1.333333D;
                break;
            case 2:
                this.mTargetAspect = 1.777778D;
        }

        a.recycle();
    }

    public void setAspectRatio(double aspectRatio) {
        if(aspectRatio < 0.0D) {
            throw new IllegalArgumentException();
        } else {
            Log.d("AFL", "Setting aspect ratio to " + aspectRatio + " (was " + this.mTargetAspect + ")");
            if(this.mTargetAspect != aspectRatio) {
                this.mTargetAspect = aspectRatio;
                this.requestLayout();
            }

        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("AFL", "onMeasure target=" + this.mTargetAspect + " width=[" + MeasureSpec.toString(widthMeasureSpec) + "] height=[" + MeasureSpec.toString(heightMeasureSpec) + "]");
        if(this.mTargetAspect > 0.0D) {
            int initialWidth = MeasureSpec.getSize(widthMeasureSpec);
            int initialHeight = MeasureSpec.getSize(heightMeasureSpec);
            int horizPadding = this.getPaddingLeft() + this.getPaddingRight();
            int vertPadding = this.getPaddingTop() + this.getPaddingBottom();
            initialWidth -= horizPadding;
            initialHeight -= vertPadding;
            double viewAspectRatio = (double)initialWidth / (double)initialHeight;
            double aspectDiff = this.mTargetAspect / viewAspectRatio - 1.0D;
            if(Math.abs(aspectDiff) < 0.01D) {
                Log.d("AFL", "aspect ratio is good (target=" + this.mTargetAspect + ", view=" + initialWidth + "x" + initialHeight + ")");
            } else {
                if(aspectDiff > 0.0D) {
                    initialHeight = (int)((double)initialWidth / this.mTargetAspect);
                } else {
                    initialWidth = (int)((double)initialHeight * this.mTargetAspect);
                }

                Log.d("AFL", "new size=" + initialWidth + "x" + initialHeight + " + padding " + horizPadding + "x" + vertPadding);
                initialWidth += horizPadding;
                initialHeight += vertPadding;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(initialWidth, 1073741824);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(initialHeight, 1073741824);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
