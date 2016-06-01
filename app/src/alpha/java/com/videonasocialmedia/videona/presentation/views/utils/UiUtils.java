package com.videonasocialmedia.videona.presentation.views.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageButton;

import com.videonasocialmedia.videona.R;

/**
 * Created by root on 1/06/16.
 */
public class UiUtils {

    public static void tintButton(@NonNull ImageButton button) {
        ColorStateList editButtonsColors = button.getResources().getColorStateList(R.color.button_color);
        Drawable button_image = DrawableCompat.wrap(button.getDrawable());
        DrawableCompat.setTintList(button_image, editButtonsColors);
        button.setImageDrawable(button_image);
    }
}
