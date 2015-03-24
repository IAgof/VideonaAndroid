/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.EditPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;
import com.videonasocialmedia.videona.presentation.views.fragment.AudioFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.FxCatalogFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.LookFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.ScissorsFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.VideoFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectMenuSelectedListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * @author Juan Javier Cabanas Abascal
 */
public class EditActivity extends Activity implements EditorView, OnEffectMenuSelectedListener {

    @InjectView(R.id.edit_button_fx)
    ImageButton videoFxButton;
    @InjectView(R.id.edit_button_look)
    ImageButton lookFxButton;
    @InjectView(R.id.edit_button_scissor)
    ImageButton scissorButton;
    @InjectView(R.id.edit_button_audio)
    ImageButton audioFxButton;

    @InjectView(R.id.edit_preview_player)
    VideoView previewPlayer;

    private EditPresenter editPresenter;
    private VideoFxMenuFragment videoFxMenuFragment;
    private AudioFxMenuFragment audioFxMenuFragment;
    private ScissorsFxMenuFragment scissorsFxMenuFragment;
    private LookFxMenuFragment lookFxMenuFragment;
    private FxCatalogFragment fxCatalogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.inject(this);

        editPresenter = new EditPresenter(this);

        //TODO mover a donde se deba
        audioFxMenuFragment = new AudioFxMenuFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.edit_right_panel, audioFxMenuFragment).commit();
    }

    @OnClick(R.id.edit_button_fx)
    public void showVideoFxMenu() {
        if (videoFxMenuFragment == null)
            videoFxMenuFragment = new VideoFxMenuFragment();
        this.switchFragment(videoFxMenuFragment, R.id.edit_right_panel);
    }

    @OnClick(R.id.edit_button_audio)
    public void showAudioFxMenu() {
        if (audioFxButton == null)
            audioFxMenuFragment = new AudioFxMenuFragment();
        this.switchFragment(audioFxMenuFragment, R.id.edit_right_panel);
    }

    @OnClick(R.id.edit_button_scissor)
    public void showScissorsFxMenu() {
        if (scissorsFxMenuFragment == null)
            scissorsFxMenuFragment = new ScissorsFxMenuFragment();
        this.switchFragment(scissorsFxMenuFragment, R.id.edit_right_panel);
    }

    @OnClick(R.id.edit_button_look)
    public void showLookFxMenu() {
        if (lookFxMenuFragment == null)
            lookFxMenuFragment = new LookFxMenuFragment();
        this.switchFragment(lookFxMenuFragment, R.id.edit_right_panel);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void switchFragment(Fragment f, int panel) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(panel, f).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    @Override
    public void navigate() {

    }

    @Override
    public void onEffectMenuSelected() {
        if (fxCatalogFragment== null){
             fxCatalogFragment= new FxCatalogFragment();
        } else {
            //TODO cambiar la lista del fragment
        }
        switchFragment(fxCatalogFragment,R.id.edit_bottom_panel);
    }
}
