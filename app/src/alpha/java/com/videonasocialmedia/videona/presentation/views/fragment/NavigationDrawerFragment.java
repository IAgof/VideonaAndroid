/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas Abascal
 * Verónica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.presentation.views.activity.EditActivity;
import com.videonasocialmedia.videona.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.videona.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.videona.presentation.views.activity.ShareActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class is used to show the right panel of the video fx menu
 */
public class NavigationDrawerFragment extends VideonaFragment {

    /*CONFIG*/
    /**
     * Tracker google analytics
     */
    private Tracker tracker;
    /**
     * LOG_TAG
     */
    private static final String LOG_TAG = "NavigationDrawerFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_fragment_navigator, container, false);
        ButterKnife.inject(this, view);

        VideonaApplication app = (VideonaApplication) getActivity().getApplication();
        tracker = app.getTracker();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @OnClick(R.id.fragment_navigator_record_button)
    public void navigateToRecord() {

        // Hide fragment if user came from Record screen.
        if(this.getActivity().getClass() == RecordActivity.class ){

            this.getActivity().onBackPressed();

        } else {
            Intent record = new Intent(this.getActivity(), RecordActivity.class);
            startActivity(record);
        }
    }

    @OnClick(R.id.fragment_navigator_edit_button)
    public void navigateToEdit() {

        // In ShareActivity, Edit goBack to editActivity, not to GalleryActivity.
        // In EditActivity go back to editActivity
        if(this.getActivity().getClass() == EditActivity.class){

            this.getActivity().onBackPressed();

        } else if(this.getActivity().getClass() == ShareActivity.class) {

            ShareActivity activity = (ShareActivity) this.getActivity();
            activity.navigateToEdit();

        } else {

            RecordActivity activity = (RecordActivity) this.getActivity();
            activity.navigateToEdit();

        }
    }


    @OnClick(R.id.fragment_navigator_share_button)
    public void navigateToShare() {

        Intent share = new Intent(this.getActivity(), GalleryActivity.class);
        share.putExtra("SHARE", true);
        startActivity(share);
    }

    @OnClick(R.id.fragment_navigator_settings_button)
    public void navigateToSettings() {
        Intent intent = new Intent(this.getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.fragment_navigator_rate_button)
    public void navigateToRate(){

        Uri uri = Uri.parse("market://details?id=" + this.getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            this.getActivity().startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity(), R.style.VideonaAlertDialogDark);
            builder.setMessage(getResources().getString(R.string.error_rate_app))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    @OnClick (R.id.fragment_navigator_drawer_button)
    public void navigateToBack(){
        this.getActivity().onBackPressed();
    }

    @OnClick (R.id.fragment_navigator_exit_button)
    public void navigateToExitApp(){

        new AlertDialog.Builder(this.getActivity(), R.style.VideonaAlertDialogDark)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.exit_app_title)
                .setMessage(R.string.exit_app_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                        startActivity(intent);
                        getActivity().finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();

    }

    @OnClick({R.id.fragment_navigator_record_button, R.id.fragment_navigator_edit_button,
            R.id.fragment_navigator_share_button, R.id.fragment_navigator_settings_button,
            R.id.fragment_navigator_rate_button})
    public void trackClicks(View view) {
        sendButtonTracked(view.getId());
    }



    /**
     * Sends button clicks to Google Analytics
     *
     * @param id the identifier of the clicked button
     */
    private void sendButtonTracked(int id) {
        String label;
        switch (id) {
            case R.id.fragment_navigator_record_button:
                label = "Go to Record from " + this.getActivity().getLocalClassName();
                break;
            case R.id.fragment_navigator_edit_button:
                label = "Go to edit " + this.getActivity().getLocalClassName();
                break;
            case R.id.fragment_navigator_share_button:
                label = "Go to share " + this.getActivity().getLocalClassName();
                break;
            case R.id.fragment_navigator_rate_button:
                label = "Go to rate " + this.getActivity().getLocalClassName();
                break;
            case R.id.fragment_navigator_exit_button:
                label = "Go to exit " + this.getActivity().getLocalClassName();
                break;
            case R.id.fragment_navigator_settings_button:
                label = "Go to settings " + this.getActivity().getLocalClassName();
                break;
            default:
                label = "Other";
        }

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Navigation Drawer")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getActivity().getApplication().getBaseContext()).dispatchLocalHits();
    }

}
