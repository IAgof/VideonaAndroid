/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import com.videonasocialmedia.videona.eventbus.events.survey.JoinBetaEvent;

import de.greenrobot.event.EventBus;

/**
 * This class is used to manage the setting menu.
 */
public class SettingsActivity extends SettingsBaseActivity {

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(JoinBetaEvent event){
        String email = event.email;
        mixpanel.getPeople().identify(mixpanel.getDistinctId());
        mixpanel.getPeople().set("$email", email); //Special properties in Mixpanel use $ before
                                                   // property name
    }


}
