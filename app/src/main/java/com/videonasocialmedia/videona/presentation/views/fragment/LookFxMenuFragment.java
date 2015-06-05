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

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class is used to show the right panel of the look fx menu
 */
public class LookFxMenuFragment extends Fragment {

    /*CONFIG*/
    /**
     * Tracker google analytics
     */
    private VideonaApplication app;
    private Tracker tracker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_fragment_look, container, false);
        ButterKnife.inject(this, view);

        app = (VideonaApplication) getActivity().getApplication();
        tracker = app.getTracker();

        return view;
    }

    @OnClick({R.id.edit_fragment_look_button_item1, R.id.edit_fragment_look_button_item2,
            R.id.edit_fragment_look_button_item3, R.id.edit_fragment_look_button_item4})
    public void clickListener(View view) {
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
            case R.id.edit_fragment_look_button_item1:
                label = "Go to item1 of edit fragment look";
                Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.edit_text_special), Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_fragment_look_button_item2:
                label = "Go to item2 of edit fragment look";
                Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.edit_text_special), Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_fragment_look_button_item3:
                label = "Go to item3 of edit fragment look";
                Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.edit_text_special), Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_fragment_look_button_item4:
                label = "Go to item4 of edit fragment look";
                Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.edit_text_special), Toast.LENGTH_SHORT).show();
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("LookFxMenuFragment")
                .setAction("item menu clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getActivity().getApplication().getBaseContext()).dispatchLocalHits();
    }


}
