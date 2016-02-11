/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.utils.AnalyticsConstants;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BetaDialogFragment extends DialogFragment {
    private MixpanelAPI mixpanel;
    private SharedPreferences sharedPreferences;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mixpanel = MixpanelAPI.getInstance(this.getActivity(), BuildConfig.MIXPANEL_TOKEN);
        sharedPreferences = this.getActivity().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_leave_beta, null);
        builder.setView(v);

        ButterKnife.inject(this, v);

        return builder.create();
    }

    @OnClick(R.id.negativeButton)
    public void goToLeaveBeta() {
        sendBetaLeaveTracking();
        String url = "https://play.google.com/apps/testing/com.videonasocialmedia.videona";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void sendBetaLeaveTracking() {
        int totalVideosRecorded = sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED, 0);
        int totalVideosShared = sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, 0);
        JSONObject betaLeavedProperties = new JSONObject();
        try {
            betaLeavedProperties.put(AnalyticsConstants.DATE,
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
            betaLeavedProperties.put(AnalyticsConstants.TOTAL_VIDEOS_RECORDED, totalVideosRecorded);
            betaLeavedProperties.put(AnalyticsConstants.TOTAL_VIDEOS_SHARED, totalVideosShared);
            mixpanel.track(AnalyticsConstants.BETA_LEAVED, betaLeavedProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.positiveButton)
    public void dismissBetaDialog() {
        this.dismiss();
    }
}


