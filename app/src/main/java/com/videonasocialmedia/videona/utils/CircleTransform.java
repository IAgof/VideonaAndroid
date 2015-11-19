/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.utils;

import android.content.Context;
import android.graphics.Bitmap;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class CircleTransform extends BitmapTransformation {

        public CircleTransform(Context context) {
            super(context);
        }
        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
            return Utils.getCircularBitmapImage(source);
        }
        @Override
        public String getId() {
            return "Glide_Circle_Transformation";
        }

}
