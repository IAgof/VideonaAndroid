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

import com.videonasocialmedia.videona.eventbus.events.survey.JoinBetaEvent;

import de.greenrobot.event.EventBus;

public class DialogPreferences extends DialogPreference {

    public DialogPreferences(Context oContext, AttributeSet attrs)
    {
        super(oContext, attrs);
    }

    @Override
    public void onClick(DialogInterface dialog, int which){
        if(which == DialogInterface.BUTTON_POSITIVE) {
            EventBus.getDefault().post(new JoinBetaEvent());
        }
    }
}
