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
import com.videonasocialmedia.videona.presentation.mvp.views.FxMenuView;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectMenuSelectedListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class is used to show the right panel of the audio fx menu
 */
public class AudioFxMenuFragment extends Fragment implements FxMenuView {

    /**
     * Tracker google analytics
     */
    private Tracker tracker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_fragment_sound, container, false);
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


    public void showLog() {
        Log.d("Fragment Audio", "He pulsado el primer botón");
        //imgButton.setImageResource(R.drawable.activity_edit_icon_transition_normal);
    }

    @OnClick(R.id.edit_fragment_sound_button_music_catalog)
    public void showCatalog() {
        showLog();
    }

    @Override
    public void showItemList(List<ImageButton> buttonList) {
        //TODO mostrar la lista dinámica de botones
    }

    @OnClick({R.id.edit_fragment_sound_button_fx, R.id.edit_fragment_sound_button_music,
            R.id.edit_fragment_sound_button_audio, R.id.edit_fragment_sound_button_music_catalog})
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
            case R.id.edit_fragment_sound_button_fx:
                label = "Go to effects of edit fragment sound";
                Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.edit_text_special), Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_fragment_sound_button_music:
                label = "Go to music of edit fragment sound";
                Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.edit_text_special), Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_fragment_sound_button_audio:
                label = "Go to audio of edit fragment sound";
                Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.edit_text_special), Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_fragment_sound_button_music_catalog:
                label = "Go to music catalog of edit fragment sound";
                Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.edit_text_special), Toast.LENGTH_SHORT).show();
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("AudioFxMenuFragment")
                .setAction("item menu clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getActivity().getApplication().getBaseContext()).dispatchLocalHits();
    }
}
