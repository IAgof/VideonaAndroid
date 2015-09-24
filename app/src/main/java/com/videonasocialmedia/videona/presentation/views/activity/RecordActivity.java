/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.avrecorder.view.GLCameraEncoderView;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter2;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraColorFilterAdapter;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectColor;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectFx;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectsAdapter;
import com.videonasocialmedia.videona.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.videona.presentation.views.listener.OnColorEffectSelectedListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnFxSelectedListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Álvaro Martínez Marco
 */

/**
 * RecordActivity manages a single live record.
 */
public class RecordActivity extends Activity implements DrawerLayout.DrawerListener, RecordView,
        OnColorEffectSelectedListener, OnFxSelectedListener {

    private final String LOG_TAG = getClass().getSimpleName();
    @InjectView(R.id.activity_record_drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.activity_record_navigation_drawer)
    View navigatorView;
    @InjectView(R.id.button_record)
    ImageButton recButton;
    @InjectView(R.id.cameraPreview)
    GLCameraEncoderView cameraView;
    @InjectView(R.id.button_change_camera)
    ImageButton rotateCameraButton;
    @InjectView(R.id.button_navigate_edit)
    CircleImageView navigateToEditButton;
    @InjectView(R.id.record_catalog_recycler_fx)
    RecyclerView effectsRecycler;
    @InjectView(R.id.record_catalog_recycler_color)
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
    @InjectView(R.id.button_navigate_drawer)
    ImageButton drawerButton;
    @InjectView(R.id.text_view_num_videos)
    TextView numVideosRecorded;

    RecordPresenter2 recordPresenter;
    CameraEffectsAdapter cameraEffectsAdapter;
    CameraColorFilterAdapter cameraColorFilterAdapter;

    private boolean buttonBackPressed;
    private boolean fxHidden;
    private boolean colorFilterHidden;
    private boolean recording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        ButterKnife.inject(this);
        drawerLayout.setDrawerListener(this);
        cameraView.setKeepScreenOn(true);
        recordPresenter = new RecordPresenter2(this, this, cameraView);
        initEffectsRecycler();
        configChronometer();

        navigateToEditButton.setBorderWidth(5);
        navigateToEditButton.setBorderColor(Color.WHITE);
    }

    private void initEffectsRecycler() {
        cameraEffectsAdapter = new CameraEffectsAdapter(this);
        effectsRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        effectsRecycler.setAdapter(cameraEffectsAdapter);
        fxHidden = true;

        cameraColorFilterAdapter = new CameraColorFilterAdapter(this);
        colorFilterRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        colorFilterRecycler.setAdapter(cameraColorFilterAdapter);
        colorFilterHidden = true;
    }

    private void configChronometer() {
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();

                int h = (int) (elapsedTime / 3600000);
                int m = (int) (elapsedTime - h * 3600000) / 60000;
                int s = (int) (elapsedTime - h * 3600000 - m * 60000) / 1000;
                // String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                String time = mm + ":" + ss;

                chronometer.setText(time);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        recordPresenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordPresenter.onResume();
        recording = false;
    }

    @OnClick(R.id.button_record)
    public void OnRecordButtonClicked() {
        if (!recording)
            recordPresenter.requestRecord();
        else
            recordPresenter.stopRecord();
    }

    @Override
    public void showRecordButton() {
        recButton.setImageResource(R.drawable.activity_record_icon_stop_normal);
        recButton.setAlpha(0.5f);
        recording = true;
    }

    @Override
    public void showStopButton() {
        recButton.setImageResource(R.drawable.activity_record_icon_rec_normal);
        recButton.setAlpha(1f);
        recording = false;
    }

    @Override
    public void startChronometer() {
        resetChronometer();
        chronometer.start();
        showRecordingIndicator();
    }

    private void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setText("00:00");
    }

    private void showRecordingIndicator() {
        recordingIndicator.setVisibility(View.VISIBLE);
        AnimationDrawable frameAnimation = (AnimationDrawable) recordingIndicator.getDrawable();
        frameAnimation.setCallback(recordingIndicator);
        frameAnimation.setVisible(true, true);
    }

    @Override
    public void stopChronometer() {
        chronometer.stop();
        hideRecordingIndicator();
    }

    private void hideRecordingIndicator() {
        recordingIndicator.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.button_camera_effect_fx)
    public void onFxButtonClicked() {
        if (!colorFilterHidden) {
            hideColorFilters();
        }
        if (!fxHidden) {
            hideFx();
        } else {
            showCameraEffectFx(null);
        }
    }

    private void hideColorFilters() {
        hideView(colorFilterRecycler);
        colorFilterHidden = true;
        buttonCameraEffectColor.setActivated(false);
    }

    private void hideView(View view) {
        runTranslateAnimation(view, 0, new DecelerateInterpolator(3));
    }

    private void runTranslateAnimation(View view, int translateY, Interpolator interpolator) {
        Animator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", translateY);
        slideInAnimation.setDuration(view.getContext().getResources()
                .getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimation.setInterpolator(interpolator);
        slideInAnimation.start();
    }

    private void hideFx() {
        hideView(effectsRecycler);
        fxHidden = true;
        buttonCameraEffectFx.setActivated(false);
    }

    @Override
    public void showCameraEffectFx(List<CameraEffectFx> effects) {
        showEffectsRecyler(effectsRecycler);
        fxHidden = false;
        buttonCameraEffectFx.setActivated(true);
    }

    private void showEffectsRecyler(View view) {
        int height = calculateTranslation(view);
        int translateY = -height;
        runTranslateAnimation(view, translateY, new AccelerateInterpolator(3));
    }

    /**
     * Takes height + margins
     *
     * @param view View to translate
     * @return translation in pixels
     */
    private int calculateTranslation(View view) {
        int height = view.getHeight();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        //int margins = params.topMargin + params.bottomMargin;
        int margins = 0;
        return height + margins;
    }

    @OnClick(R.id.button_camera_effect_color)
    public void onColorFiltersButtonClicked() {
        if (!fxHidden) {
            hideFx();
        }
        if (!colorFilterHidden) {
            hideColorFilters();
        } else {
            showCameraEffectColor(null);
        }
    }


    @Override
    public void showCameraEffectFxSelected(String colorEffect) {

    }

    @Override
    public void showCameraEffectColor(List<CameraEffectColor> effects) {
        showEffectsRecyler(colorFilterRecycler);
        colorFilterHidden = false;
        buttonCameraEffectFx.setActivated(true);
    }

    @Override
    public void showCameraEffectColorSelected(String colorEffect) {

    }

    @Override
    public void navigateEditActivity(String durationVideoRecorded) {
        //TODO stop record if recording
        Intent edit = new Intent(this, EditActivity.class);
        startActivity(edit);
    }

    @Override
    public void lockScreenRotation() {

    }

    public void lockNavigator() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    public void unLockNavigator() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @OnClick(R.id.button_toggle_flash)
    public void toggleFlash() {
        recordPresenter.toggleFlash();
    }

    @Override
    public void showFlashOn(boolean on) {
        flashButton.setActivated(on);
    }

    @Override
    public void showFrontCameraSelected() {
        rotateCameraButton.setActivated(false);
    }

    @Override
    public void showBackCameraSelected() {
        rotateCameraButton.setActivated(false);
    }


    @Override
    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(int stringResourceId) {
        Toast.makeText(this, this.getText(stringResourceId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reStartFragment() {

    }


    @Override
    public void showRecordedVideoThumb(String path) {
       // Glide.with(this).load(path).centerCrop().crossFade().into(navigateToEditButton);
        Glide.with(this).load(path).into(navigateToEditButton);
    }

    @Override
    public void showVideosRecordedNumber(int numberOfVideos) {
        numVideosRecorded.setText(String.valueOf(numberOfVideos));
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


    @OnClick(R.id.button_change_camera)
    public void changeCamera() {
        recordPresenter.changeCamera();
    }

    @OnClick(R.id.button_navigate_edit)
    public void navigateToEdit() {
        Intent edit = new Intent(this, EditActivity.class);
        edit.putExtra("SHARE", false);
        startActivity(edit);
    }


    @OnClick(R.id.button_navigate_drawer)
    public void showDrawer() {
        drawerLayout.openDrawer(navigatorView);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        drawerButton.setVisibility(View.GONE);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        drawerButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onColorEffectSelected(CameraEffectColor colorEffect) {
        recordPresenter.applyEffect(colorEffect.getFilterId());
    }

    @Override
    public void onFxSelected(CameraEffectFx fx) {
        recordPresenter.applyEffect(fx.getFilterId());
    }
}
