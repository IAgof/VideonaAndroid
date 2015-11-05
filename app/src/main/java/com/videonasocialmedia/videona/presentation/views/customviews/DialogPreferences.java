/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.customviews;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;

public class DialogPreferences extends DialogPreference {

    public DialogPreferences(Context oContext, AttributeSet attrs)
    {
        super(oContext, attrs);
    }

    @Override
    public void onClick(DialogInterface dialog, int which){

        if(which == DialogInterface.BUTTON_POSITIVE) {
            // do your stuff to handle positive button
            Log.d("Dialog preferences", " OK Button" );

        }else if(which == DialogInterface.BUTTON_NEGATIVE){
            // do your stuff to handle negative button
            Log.d("Dialog preferences", " Cancel Button" );

        }
    }
}
