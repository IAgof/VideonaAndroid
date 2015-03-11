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

package com.videonasocialmedia.videona.presentation.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class CustomManualFocusView extends View {

    private Paint paint;

    public static boolean showDraw = false;

    float x = 0;
    float y = 0;

    public CustomManualFocusView(Context context) {
        super(context);

        setFocusable(true);

        paint = new Paint();
        paint.setColor(0xeed7d7d7);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);

    }


    @Override
    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (showDraw) {
            canvas.drawCircle(x, y, 50, paint);
        }

    }


    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            x = event.getX();
            y = event.getY();
            invalidate();

            showDraw = true;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // invalidate();
                    showDraw = false;
                    invalidate();
                }
            }, 1000);

        }
        return false;
    }
}
