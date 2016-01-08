/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.avrecorder.view.GLCameraEncoderView;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.adapter.EffectAdapter;
import com.videonasocialmedia.videona.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectSelectedListener;
import com.videonasocialmedia.videona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

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
public class RecordActivity extends VideonaActivity implements DrawerLayout.DrawerListener,
        RecordView, OnEffectSelectedListener {

    private final String LOG_TAG = getClass().getSimpleName();

    @InjectView(R.id.activity_record_drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.activity_record_navigation_drawer)
    View navigatorView;
    @InjectView(R.id.button_record)
    ImageButton recButton;
    @InjectView(R.id.button_share)
    ImageButton shareButton;
    @InjectView(R.id.cameraPreview)
    GLCameraEncoderView cameraView;
    @InjectView(R.id.button_change_camera)
    ImageButton rotateCameraButton;
    @InjectView(R.id.button_navigate_edit)
    CircleImageView buttonThumbClipRecorded;
    @InjectView(R.id.record_catalog_recycler_shader_effects)
    RecyclerView shaderEffectsRecycler;
    @InjectView(R.id.record_catalog_recycler_overlay_effects)
    RecyclerView overlayFilterRecycler;
    @InjectView(R.id.imageRecPoint)
    ImageView recordingIndicator;
    @InjectView(R.id.chronometer_record)
    Chronometer chronometer;
    @InjectView(R.id.button_toggle_flash)
    ImageButton flashButton;
    @InjectView(R.id.button_camera_effect_shader)
    ImageButton buttonCameraEffectShader;
    @InjectView(R.id.button_camera_effect_overlay)
    ImageButton buttonCameraEffectOverlay;
    @InjectView(R.id.button_navigate_drawer)
    ImageButton drawerButton;
    @InjectView(R.id.text_view_num_videos)
    TextView numVideosRecorded;
    @InjectView(R.id.rotateDeviceHint)
    ImageView rotateDeviceHint;
    @InjectView(R.id.drawer_full_background)
    ImageView drawerBackground;
    @InjectView(R.id.button_remove_filters)
    ImageButton removeFilters;

    private RecordPresenter recordPresenter;
    private EffectAdapter cameraShaderEffectsAdapter;
    private EffectAdapter cameraOverlayEffectsAdapter;
    private Tracker tracker;
    private boolean buttonBackPressed;
    private boolean shaderFilterHidden;
    private boolean overlayFilterHidden;
    private boolean removeFilterActivated;
    private boolean recording;
    private OrientationHelper orientationHelper;
    private AlertDialog progressDialog;

    private boolean mUseImmersiveMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.inject(this);
        drawerLayout.setDrawerListener(this);

        cameraView.setKeepScreenOn(true);
        recordPresenter = new RecordPresenter(this, this, cameraView);

        initEffectsRecycler();
        configChronometer();
        initOrientationHelper();
        configThumbsView();
        createProgressDialog();

        buttonThumbClipRecorded.setBorderWidth(5);
        buttonThumbClipRecorded.setBorderColor(Color.WHITE);
        numVideosRecorded.setVisibility(View.GONE);

        VideonaApplication app = (VideonaApplication) getApplication();
        tracker = app.getTracker();
        recButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!recording) {
                        recordPresenter.requestRecord();
                        sendButtonTracked("Start recording");
                        mixpanel.timeEvent("Time recording one video");
                        mixpanel.track("Start recording");
                    } else {
                        recordPresenter.stopRecord();
                        sendButtonTracked("Stop recording");
                        mixpanel.track("Time recording one video");
                        mixpanel.track("Stop recording");
                    }
                }
                return true;
            }
        });
    }

    private void initOrientationHelper() {
        orientationHelper = new OrientationHelper(this);
    }

    private void configThumbsView() {
        buttonThumbClipRecorded.setBorderWidth(5);
        buttonThumbClipRecorded.setBorderColorResource(R.color.textColorNumVideos);
        numVideosRecorded.setVisibility(View.GONE);
    }

    private void createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_export_progress, null);
        progressDialog = builder.setCancelable(false)
                .setView(dialogView)
                .create();
    }

    private void initEffectsRecycler() {

        //cameraShaderEffectsAdapter = new EffectAdapter(recordPresenter.getDistortionEffectList(), this);
        cameraShaderEffectsAdapter = new EffectAdapter(recordPresenter.getShaderEffectList(), this);
        shaderEffectsRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        shaderEffectsRecycler.setAdapter(cameraShaderEffectsAdapter);
        shaderFilterHidden = true;

        //cameraOverlayEffectsAdapter = new EffectAdapter(recordPresenter.getColorEffectList(), this);
        cameraOverlayEffectsAdapter = new EffectAdapter(recordPresenter.getOverlayEffects(), this);
        overlayFilterRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        overlayFilterRecycler.setAdapter(cameraOverlayEffectsAdapter);
        overlayFilterHidden = false;
        buttonCameraEffectOverlay.setActivated(true);
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
    protected void onStart() {
        super.onStart();
        recordPresenter.onStart();
        mixpanel.timeEvent("Time in Record Activity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordPresenter.onResume();
        recording = false;
        hideSystemUi();
    }

    @Override
    public void onPause() {
        super.onPause();
        recordPresenter.onPause();
        orientationHelper.stopMonitoringOrientation();
        mixpanel.track("Time in Record Activity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        recordPresenter.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recordPresenter.onDestroy();
    }

    private void hideSystemUi() {
        if (!Utils.isKitKat() || !mUseImmersiveMode) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (mUseImmersiveMode) {
            setKitKatWindowFlags();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Utils.isKitKat() && hasFocus && mUseImmersiveMode) {
            setKitKatWindowFlags();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setKitKatWindowFlags() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    public void showRecordButton() {
        recButton.setImageResource(R.drawable.activity_record_icon_rec_normal);
        recButton.setAlpha(1f);
        recording = false;
        unLockNavigator();

    }

    @Override
    public void showStopButton() {
        recButton.setImageResource(R.drawable.activity_record_icon_stop_normal);
        recButton.setAlpha(1f);
        recording = true;
        lockNavigator();
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

    @OnClick(R.id.button_camera_effect_shader)
    public void onShaderButtonClicked() {
        if (!overlayFilterHidden) {
            hideOverlayFilters();
        }
        if (!shaderFilterHidden) {
            hideShaderFilters();
            hideRemoveFilters();
        } else {
            showCameraEffectShader(null);
            if(removeFilterActivated){
                showRemoveFilters();
            }
        }
    }

    private void hideOverlayFilters() {
        int height = calculateTranslation(overlayFilterRecycler);
        runTranslateAnimation(overlayFilterRecycler, height, new DecelerateInterpolator(3));
        overlayFilterHidden = true;
        buttonCameraEffectOverlay.setActivated(false);
    }

    private void hideEffectsRecyclerView(View view) {
        runTranslateAnimation(view, -Math.round(view.getTranslationY()), new DecelerateInterpolator(3));
    }

    private void runTranslateAnimation(View view, int translateY, Interpolator interpolator) {
        Animator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", translateY);
        slideInAnimation.setDuration(view.getContext().getResources()
                .getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimation.setInterpolator(interpolator);
        slideInAnimation.start();
    }

    private void hideShaderFilters() {
        hideEffectsRecyclerView(shaderEffectsRecycler);
        shaderFilterHidden = true;
        buttonCameraEffectShader.setActivated(false);
    }

    private void hideRemoveView(View view) {
        runTranslateAnimation(view, -Math.round(view.getTranslationY()), new DecelerateInterpolator(3));
    }

    private void showRemoveView(View view) {
        int height = calculateTranslation(view);
        int translateY = -height;;
        runTranslateAnimation(view, translateY, new AccelerateInterpolator(3));
    }

    private void hideRemoveFilters(){

        hideRemoveView(removeFilters);
    }

    private void showRemoveFilters() {

        showRemoveView(removeFilters);
    }

    @Override
    public void showCameraEffectShader(List<Effect> effects) {
        showEffectsRecylerView(shaderEffectsRecycler);
        shaderFilterHidden = false;
        buttonCameraEffectShader.setActivated(true);
    }

    private void showEffectsRecylerView(View view) {
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

    @OnClick(R.id.button_camera_effect_overlay)
    public void onOverlayFiltersButtonClicked() {
        if (!shaderFilterHidden) {
            hideShaderFilters();
        }
        if (!overlayFilterHidden) {
            hideOverlayFilters();
            hideRemoveFilters();
        } else {
            showCameraEffectOverlay(null);
            if(removeFilterActivated){
                showRemoveFilters();
            }
        }
    }

    @Override
    public void showCameraEffectOverlay(List<Effect> effects) {
        runTranslateAnimation(overlayFilterRecycler, 0, new AccelerateInterpolator(3));
        overlayFilterHidden = false;
        buttonCameraEffectOverlay.setActivated(true);
    }


    @OnClick(R.id.button_remove_filters)
    public void onRemoveFiltersButtonClicked(){


        Effect effectOverlay = cameraOverlayEffectsAdapter.getEffect(cameraOverlayEffectsAdapter.getSelectionPosition());
        Effect effectShader = cameraShaderEffectsAdapter.getEffect(cameraShaderEffectsAdapter.getSelectionPosition());

        onEffectSelectionCancel(effectOverlay);
        onEffectSelectionCancel(effectShader);

        // Reset background filter accent
        cameraShaderEffectsAdapter.resetSelectedEffect();
        cameraOverlayEffectsAdapter.resetSelectedEffect();

        // Hide filters
        if (!overlayFilterHidden) {
            hideOverlayFilters();
        }
        if (!shaderFilterHidden) {
            hideShaderFilters();
        }

        hideRemoveFilters();

        removeFilterActivated = false;

    }

    @Override
    public void lockScreenRotation() {
        orientationHelper.stopMonitoringOrientation();
    }

    @Override
    public void reStartScreenRotation() {
        try {
            orientationHelper.startMonitoringOrientation();
        } catch (OrientationHelper.NoOrientationSupportException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unlockScreenRotation() {

        try {
            orientationHelper.startMonitoringOrientation();
        } catch (OrientationHelper.NoOrientationSupportException e) {
            e.printStackTrace();
        }
    }

    public void lockNavigator() {
        drawerButton.setVisibility(View.INVISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    public void unLockNavigator() {
        drawerButton.setVisibility(View.VISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @OnClick(R.id.button_toggle_flash)
    public void toggleFlash() {
        recordPresenter.toggleFlash();
        mixpanel.track("Toggle flash Button clicked", null);
    }

    @Override
    public void showFlashOn(boolean on) {
        flashButton.setActivated(on);
    }

    @Override
    public void showFlashSupported(boolean supported) {
        if (supported) {
            flashButton.setImageAlpha(255);
            flashButton.setActivated(false);
            flashButton.setActivated(false);
            flashButton.setEnabled(true);
        } else {
            flashButton.setImageAlpha(65);
            flashButton.setActivated(false);
            flashButton.setEnabled(false);
        }
    }

    @Override
    public void showFrontCameraSelected() {
        rotateCameraButton.setActivated(false);
        mixpanel.track("Front camera selected", null);
        try {
            orientationHelper.reStartMonitoringOrientation();
        } catch (OrientationHelper.NoOrientationSupportException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showBackCameraSelected() {
        rotateCameraButton.setActivated(false);
        mixpanel.track("Back camera selected", null);
        try {
            orientationHelper.reStartMonitoringOrientation();
        } catch (OrientationHelper.NoOrientationSupportException e) {
            e.printStackTrace();
        }
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
    public void showRecordedVideoThumb(String path) {
        buttonThumbClipRecorded.setVisibility(View.VISIBLE);
        Glide.with(this).load(path).into(buttonThumbClipRecorded);
    }

    @Override
    public void showVideosRecordedNumber(int numberOfVideos) {
        numVideosRecorded.setVisibility(View.VISIBLE);
        numVideosRecorded.setText(String.valueOf(numberOfVideos));
    }

    @Override
    public void hideVideosRecordedNumber() {
        numVideosRecorded.setVisibility(View.INVISIBLE);
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
        recordPresenter.setFlashOff();
        recordPresenter.changeCamera();
        if (recording)
            mixpanel.track("Change camera Button clicked while recording", null);
        else
            mixpanel.track("Change camera Button clicked on preview", null);
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

    @OnClick(R.id.button_share)
    public void exportAndShare() {
        if (!recording) {
            showProgressDialog();
            sendMetadataTracking();
            startExportThread();
        }
    }

    private void startExportThread() {
        final Thread t = new Thread() {
            @Override
            public void run() {
                recordPresenter.startExport();
            }
        };
        t.start();
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void showMessage(final int message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMetadataTracking() {
        try {
            int projectDuration = recordPresenter.getProjectDuration();
            int numVideosOnProject = recordPresenter.getNumVideosOnProject();
            JSONObject props = new JSONObject();
            props.put("Number of videos", numVideosOnProject);
            props.put("Duration of the exported video in msec", projectDuration);
            mixpanel.track("Exported video", props);
        } catch (JSONException e) {
            Log.e("TRACK_FAILED", String.valueOf(e));
        }
    }

    @Override
    public void goToShare(String videoToSharePath) {

        Intent intent = new Intent(this, ShareVideoActivity.class);
        intent.putExtra("VIDEO_EDITED", videoToSharePath);
        startActivity(intent);
    }


    @Override
    public void showMenuOptions() {
        navigatorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMenuOptions() {
        navigatorView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void showChronometer() {
        chronometer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideChronometer() {
        chronometer.setVisibility(View.INVISIBLE);
    }


    @Override
    public void hideRecordedVideoThumb() {
        buttonThumbClipRecorded.setVisibility(View.INVISIBLE);
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

    @Override
    public void onEffectSelected(Effect effect) {
        recordPresenter.applyEffect(effect);
        sendButtonTracked(effect.getIconId());
        scrollEffectList(effect);
        showRemoveFilters();
        removeFilterActivated = true;

    }



    @Override
    public void onEffectSelectionCancel(Effect effect) {
        recordPresenter.removeEffect(effect);

        if(!cameraOverlayEffectsAdapter.isEffectSelected() &&
                !cameraShaderEffectsAdapter.isEffectSelected()){
            hideRemoveFilters();
        }
    }

    private void scrollEffectList(Effect effect) {
        if (!overlayFilterHidden) {
            List effects = cameraOverlayEffectsAdapter.getElementList();
            int index = effects.indexOf(effect);
            int scroll = index > cameraOverlayEffectsAdapter.getPreviousSelectionPosition() ? 1 : -1;
            overlayFilterRecycler.scrollToPosition(index + scroll);
        } else if (!shaderFilterHidden) {
            List effects = cameraShaderEffectsAdapter.getElementList();
            int index = effects.indexOf(effect);
            int scroll = index > cameraShaderEffectsAdapter.getPreviousSelectionPosition() ? 1 : -1;
            shaderEffectsRecycler.scrollToPosition(index + scroll);
        }
    }

    @OnClick({R.id.button_record, R.id.button_toggle_flash, R.id.button_camera_effect_overlay,
            R.id.button_camera_effect_shader, R.id.button_change_camera})
    public void clickListener(View view) {
        sendButtonTracked(view.getId());
    }

    private void sendButtonTracked(String label) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("RecordActivity")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getApplication().getBaseContext()).dispatchLocalHits();
    }

    /**
     * Sends button clicks to Google Analytics
     *
     * @param id identifier of the clicked view
     */
    private void sendButtonTracked(int id) {
        String label;
        switch (id) {
            case R.id.button_record:
                label = "Capture ";
                break;
            case R.id.button_change_camera:
                label = "Change camera";
                break;
            case R.id.button_toggle_flash:
                label = "Flash camera";
                break;
            case R.id.button_camera_effect_shader:
                label = "Fx filters";
                break;
            case R.id.button_camera_effect_overlay:
                label = "Color filters";
                break;
            case R.drawable.common_filter_color_ad1_aqua:
                label = "Aqua color filter AD1";
                mixpanel.track("Aqua color filter selected AD1", null);
                break;
            case R.drawable.common_filter_color_ad2_posterizebw:
                label = "Posterize bw filter AD2";
                mixpanel.track("Posterize bw filter selected AD2", null);
                break;
            case R.drawable.common_filter_color_ad3_emboss:
                label = "Emboss color filter AD3";
                mixpanel.track("Emboss color filter selected AD3", null);
                break;
            case R.drawable.common_filter_color_ad4_mono:
                label = "Mono color filter AD4";
                mixpanel.track("Mono color filter selected AD4", null);
                break;
            case R.drawable.common_filter_color_ad5_negative:
                label = "Negative color filter AD5";
                mixpanel.track("Negative color filter selected AD5", null);
                break;
            case R.drawable.common_filter_color_ad6_green:
                label = "Green color filter AD6";
                mixpanel.track("Green color filter selected AD6", null);
                break;
            case R.drawable.common_filter_color_ad7_posterize:
                label = "Posterize color filter AD7";
                mixpanel.track("Posterize color filter selected AD7", null);
                break;
            case R.drawable.common_filter_color_ad8_sepia:
                label = "Sepia color filter AD8";
                mixpanel.track("Sepia color filter selected AD8", null);
                break;
            case R.drawable.common_filter_distortion_fx1_fisheye:
                label = "Fisheye fx filter FX1";
                mixpanel.track("Fisheye fx filter selected FX1", null);
                break;
            case R.drawable.common_filter_distortion_fx2_stretch:
                label = "Stretch fx filter FX2";
                mixpanel.track("Stretch fx filter selected FX2", null);
                break;
            case R.drawable.common_filter_distortion_fx3_dent:
                label = "Dent fx filter FX3";
                mixpanel.track("Dent fx filter selected FX3", null);
                break;
            case R.drawable.common_filter_distortion_fx4_mirror:
                label = "Mirror fx filter FX4";
                mixpanel.track("Mirror fx filter selected FX4", null);
                break;
            case R.drawable.common_filter_distortion_fx5_squeeze:
                label = "Squeeze fx filter FX5";
                mixpanel.track("Squeeze fx filter selected FX5", null);
                break;
            case R.drawable.common_filter_distortion_fx6_tunnel:
                label = "Tunnel fx filter FX6";
                mixpanel.track("Tunnel fx filter selected FX6", null);
                break;
            case R.drawable.common_filter_distortion_fx7_twirl:
                label = "Twirl fx filter FX7";
                mixpanel.track("Twirl fx filter selected FX7", null);
                break;
            case R.drawable.common_filter_distortion_fx8_bulge:
                label = "Bulge filter FX8";
                mixpanel.track("Bulge filter selected FX8", null);
                break;

            default:
                label = "Other";
        }
        sendButtonTracked(label);
    }

    @Override
    public void enableShareButton() {
        shareButton.setAlpha(1f);
        shareButton.setClickable(true);
    }

    @Override
    public void disableShareButton() {
        shareButton.setAlpha(0.25f);
        shareButton.setClickable(false);
    }

    private class OrientationHelper extends OrientationEventListener {

        Context context;
        private int rotationView;
        private boolean orientationHaveChanged = false;
        private boolean isNormalOrientation;

        public OrientationHelper(Context context) {
            super(context);

            this.context = context;
        }

        /**
         *
         */
        public void startMonitoringOrientation() throws NoOrientationSupportException {
            rotationView = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
            if (rotationView == Surface.ROTATION_90) {
                isNormalOrientation = true;
                orientationHaveChanged = false;
            } else {
                isNormalOrientation = false;
            }
            determineOrientation(rotationView);
            if (this.canDetectOrientation()) {
                this.enable();
            } else {
                this.disable();
                throw new NoOrientationSupportException();
            }
        }

        public void stopMonitoringOrientation() {
            this.disable();
        }

        public void reStartMonitoringOrientation() throws NoOrientationSupportException {
            rotationView = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
            if (rotationView == Surface.ROTATION_90) {
                isNormalOrientation = true;
                orientationHaveChanged = false;
            } else {
                isNormalOrientation = false;
            }
            determineOrientation(rotationView);
        }

        private void determineOrientation(int rotationView) {
            Log.d(LOG_TAG, " determineOrientation" + " rotationView " + rotationView);
            int rotation = -1;
            if (rotationView == Surface.ROTATION_90) {
                rotation = 90;
                recordPresenter.rotateCamera(rotationView);
            } else {
                if (rotationView == Surface.ROTATION_270) {
                    rotation = 270;
                    recordPresenter.rotateCamera(rotationView);
                }
            }
            Log.d(LOG_TAG, "determineOrientation rotationPreview " + rotation +
                    " cameraInfoOrientation ");
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation > 85 && orientation < 95) {
                if (isNormalOrientation && !orientationHaveChanged) {
                    Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
                    recordPresenter.rotateCamera(Surface.ROTATION_270);
                    orientationHaveChanged = true;
                } else {
                    if (orientationHaveChanged) {
                        Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
                        recordPresenter.rotateCamera(Surface.ROTATION_270);
                        orientationHaveChanged = false;
                    }
                }
            } else if (orientation > 265 && orientation < 275) {
                if (isNormalOrientation) {
                    if (orientationHaveChanged) {
                        Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
                        recordPresenter.rotateCamera(Surface.ROTATION_90);
                        orientationHaveChanged = false;
                    }
                } else {
                    if (!orientationHaveChanged) {
                        Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
                        recordPresenter.rotateCamera(Surface.ROTATION_90);
                        orientationHaveChanged = true;
                    }
                }
            }
            checkShowRotateDevice(orientation);
        }

        // Show image to Rotate Device
        private void checkShowRotateDevice(int orientation) {
            if ((orientation > 345 || orientation < 15) && orientation != -1) {
                rotateDeviceHint.setRotation(270);
                rotateDeviceHint.setRotationX(0);
                rotateDeviceHint.setVisibility(View.VISIBLE);
            } else if (orientation > 165 && orientation < 195) {
                rotateDeviceHint.setRotation(-270);
                rotateDeviceHint.setRotationX(180);
                rotateDeviceHint.setVisibility(View.VISIBLE);
            } else {
                rotateDeviceHint.setVisibility(View.GONE);
            }
        }

        private class NoOrientationSupportException extends Exception {
        }
    }

}
