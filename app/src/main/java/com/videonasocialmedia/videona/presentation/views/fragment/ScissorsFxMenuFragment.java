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

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.presentation.views.listener.OnGalleryListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnRemoveAllProjectListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * This class is used to show the right panel of the scissors fx menu
 */
public class ScissorsFxMenuFragment extends Fragment {

    private final String LOG_TAG = "ScissorsFxMenuFragment";

    @InjectView(R.id.edit_fragment_scissors_button_duplicate)
    ImageButton trashButton;

    /*CONFIG*/
    private OnRemoveAllProjectListener callbackRemoveAllProject;
    private OnGalleryListener galleryCalled;
    private Tracker tracker;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callbackRemoveAllProject = (OnRemoveAllProjectListener) activity;
            galleryCalled = (OnGalleryListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRemoveAllProjectListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_fragment_scissors, container, false);
        ButterKnife.inject(this, view);
        VideonaApplication app = (VideonaApplication) getActivity().getApplication();
        tracker = app.getTracker();
        return view;
    }

    public void showLog() {
        Log.d("Fragment Audio", "He pulsado un botón");
        //imgButton.setImageResource(R.drawable.activity_edit_icon_transition_normal);
    }

    public void habilitateTrashButton() {
        trashButton.setAlpha((float) 1);
        if(!trashButton.isClickable())
            trashButton.setClickable(true);
    }

    public void inhabilitateTrashButton() {
        trashButton.setAlpha((float) 0.2);
        if(trashButton.isClickable())
            trashButton.setClickable(false);
    }

    @OnClick(R.id.edit_fragment_scissors_button_crop)
    public void showTrimView() {
        //showLog();
    }

    @OnClick(R.id.edit_fragment_scissors_button_add_clip)
    public void addVideos() {
        Log.d(LOG_TAG, "addVideos");
        /*
        Intent gallery = new Intent(this.getActivity(), GalleryActivity.class);
        gallery.putExtra("SHARE", false);
        startActivity(gallery);
        */
        galleryCalled.onGalleryCalled();
    }

    @OnClick(R.id.edit_fragment_scissors_button_duplicate)
    public void clearProject() {
        Log.d(LOG_TAG, "clearProject");
        callbackRemoveAllProject.onRemoveAllProjectSelected();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.edit_fragment_scissors_button_razor, R.id.edit_fragment_scissors_button_crop,
            R.id.edit_fragment_scissors_button_add_clip,
            R.id.edit_fragment_scissors_button_duplicate})
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
            case R.id.edit_fragment_scissors_button_razor:
                label = "Go to razor of edit fragment scissor";
                Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.edit_text_special), Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_fragment_scissors_button_crop:
                label = "Go to crop of edit fragment scissor";
                Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.edit_text_special), Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_fragment_scissors_button_add_clip:
                label = "Go to add clip of edit fragment scissor";
                break;
            case R.id.edit_fragment_scissors_button_duplicate:
                label = "Go to duplicate of edit fragment scissor";
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("ScissorsFxMenuFragment")
                .setAction("item menu clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getActivity().getApplication().getBaseContext()).dispatchLocalHits();
    }

}
