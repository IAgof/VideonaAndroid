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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.videonasocialmedia.videona.R;

public class CustomManualFocusView extends View {

    private Paint paint;

    private Bitmap bitmap;

    private Canvas canvas;

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

        //TODO change resource to new design
        // TODO FUTURE: add animation
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.common_filter_aqua_ad1_normal);

        canvas = new Canvas(bitmap.copy(Bitmap.Config.ARGB_8888, true));


    }


    @Override
    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (showDraw) {

            canvas.drawBitmap(bitmap, x,y, paint);

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

    public void onPreviewTouchEvent(Context context){

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        x =  size.x / 2;
        y = size.y / 3;

        Log.d("Focus", " x " + x + " y " + y);

        invalidate();

        showDraw = true;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // invalidate();
                showDraw = false;
                invalidate();

                Log.d("Focus", " postDelayed");

            }
        }, 1500);


    }
}
