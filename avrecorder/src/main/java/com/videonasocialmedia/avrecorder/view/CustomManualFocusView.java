/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.avrecorder.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.videonasocialmedia.avrecorder.R;

public class CustomManualFocusView extends View {

    public static boolean shouldDrawIcon = false;
    Drawable focusIcon;
    Rect focusIconBounds;

    public CustomManualFocusView(Context context) {
        super(context);
        setFocusable(true);
    }

    public CustomManualFocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CustomManualFocusView,
                0, 0);
        focusIcon = typedArray.getDrawable(R.styleable.CustomManualFocusView_focus_icon);
        focusIconBounds=new Rect();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shouldDrawIcon && focusIcon != null) {
            drawFocusIcon(canvas);
        }
    }

    private void drawFocusIcon(Canvas canvas){
        focusIcon.setBounds(focusIconBounds);
        focusIcon.draw(canvas);
    }


    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = Math.round(event.getX());
            int y = Math.round(event.getY());
            calculateBounds(x, y);

            invalidate();
            shouldDrawIcon = true;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    shouldDrawIcon = false;
                    invalidate();
                }
            }, 1000);
        }
        return false;
    }

    private void calculateBounds(int x, int y) {
        int halfHeight = focusIcon.getIntrinsicHeight();
        int halfWidth = focusIcon.getIntrinsicWidth();
        focusIconBounds.set(x - halfWidth, y - halfHeight, x + halfWidth, y + halfHeight);
    }
}
