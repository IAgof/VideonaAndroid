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

import android.os.Bundle;
import android.util.Log;

import com.qordoba.sdk.Qordoba;
import com.videonasocialmedia.videona.eventbus.events.survey.JoinBetaEvent;
import com.videonasocialmedia.videona.presentation.views.fragment.SettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * This class is used to manage the setting menu.
 */
public class SettingsActivity extends VideonaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        Qordoba.setCurrentNavigationRoute(android.R.id.content, this.getClass().getName());
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mixpanel.timeEvent("Time in Settings Activity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        mixpanel.track("Time in Settings Activity");
    }

    public void onEvent(JoinBetaEvent event){
        String email = event.email;
        mixpanel.getPeople().identify(mixpanel.getDistinctId());
        JSONObject props = new JSONObject();
        try {
            props.put("$email", email); //Special properties in Mixpanel use $ before property name
            mixpanel.getPeople().set(props);
        } catch (JSONException e) {
            Log.e("Mixpanel error", String.valueOf(e), e);
        }
    }

}
