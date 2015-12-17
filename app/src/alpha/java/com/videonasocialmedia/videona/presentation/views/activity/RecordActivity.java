/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.videonasocialmedia.avrecorder.view.GLCameraEncoderView;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.customviews.CircleImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Álvaro Martínez Marco
 */

/**
 * RecordActivity manages a single live record.
 */
public class RecordActivity extends RecordBaseActivity implements DrawerLayout.DrawerListener,
        RecordView {

    @InjectView(R.id.button_record)
    ImageButton recButton;
    @InjectView(R.id.cameraPreview)
    GLCameraEncoderView cameraView;
    @InjectView(R.id.button_change_camera)
    ImageButton rotateCameraButton;
    @InjectView(R.id.button_navigate_edit)
    CircleImageView buttonThumbClipRecorded;
    @InjectView(R.id.record_catalog_recycler_distortion_effects)
    RecyclerView effectsRecycler;
    @InjectView(R.id.record_catalog_recycler_color_effects)
    RecyclerView colorFilterRecycler;
    @InjectView(R.id.imageRecPoint)
    ImageView recordingIndicator;
    @InjectView(R.id.chronometer_record)
    Chronometer chronometer;
    @InjectView(R.id.button_toggle_flash)
    ImageButton flashButton;
    @InjectView(R.id.button_camera_effect_fx)
    ImageButton buttonCameraEffectFx;
    @InjectView(R.id.button_camera_effect_color)
    ImageButton buttonCameraEffectColor;
    @InjectView(R.id.text_view_num_videos)
    TextView numVideosRecorded;
    @InjectView(R.id.rotateDeviceHint)
    ImageView rotateDeviceHint;
    @InjectView(R.id.button_share)
    ImageButton shareButton;
    @InjectView(R.id.activity_record_drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.activity_record_navigation_drawer)
    View navigatorView;
    @InjectView(R.id.button_navigate_drawer)
    ImageButton drawerButton;
    @InjectView(R.id.drawer_full_background)
    ImageView drawerBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.inject(this);

        recordPresenter = new RecordPresenter(this, this, cameraView);
        initActivity();
        drawerLayout.setDrawerListener(this);
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
    public void onPause() {
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

    @Override
    public void showRecordButton() {
        super.showRecordButton();
        unLockNavigator();
    }

    private void unLockNavigator() {
        drawerButton.setVisibility(View.VISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void showStopButton() {
        super.showStopButton();
        lockNavigator();
    }

    private void lockNavigator() {
        drawerButton.setVisibility(View.INVISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(navigatorView);
        } else if (buttonBackPressed) {
            buttonBackPressed = false;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);
        } else {
            buttonBackPressed = true;
            Toast.makeText(getApplicationContext(), getString(R.string.toast_exit),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button_navigate_edit)
    public void navigateToEdit() {
        if (!recording) {
            Intent edit = new Intent(this, EditActivity.class);
            //edit.putExtra("SHARE", false);
            startActivity(edit);
            mixpanel.track("Navigate edit Button clicked in Record Activity", null);
        }
    }


    @Override
    public void showMenuOptions(){
        navigatorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMenuOptions() {
        navigatorView.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.button_navigate_drawer)
    public void showDrawer() {
        if (!recording) {
            drawerLayout.openDrawer(navigatorView);
            drawerBackground.setVisibility(View.VISIBLE);
            mixpanel.track("Navigate drawer Button clicked in Record Activity", null);
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        drawerButton.setVisibility(View.GONE);
        drawerBackground.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        drawerButton.setVisibility(View.VISIBLE);
        drawerBackground.setVisibility(View.GONE);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

}
