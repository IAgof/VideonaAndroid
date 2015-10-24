/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.google.android.gms.analytics.Tracker;

public class InstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //handleIntent(context, intent);

        new CampaignTrackingReceiver().onReceive(context, intent);
    }

    // Handle the intent data
    public void handleIntent(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String referrerString = extras.getString("referrer");

        Log.d("InstallReceiver", "IT WORKS!!! " + referrerString);
        Toast.makeText(context, "IT WORKS!! " + referrerString, Toast.LENGTH_SHORT).show();
    }
}
