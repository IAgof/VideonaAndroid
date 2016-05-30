package com.videonasocialmedia.videona.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageButton;

/**
 *
 */
public class UIUtils {

    public static void tintButton(@NonNull ImageButton button, int tintListId) {
        ColorStateList editButtonsColors =
                button.getResources().getColorStateList(tintListId);
        Drawable button_image = DrawableCompat.wrap(button.getDrawable());
        DrawableCompat.setTintList(button_image, editButtonsColors);
        button.setImageDrawable(button_image);
    }
}
