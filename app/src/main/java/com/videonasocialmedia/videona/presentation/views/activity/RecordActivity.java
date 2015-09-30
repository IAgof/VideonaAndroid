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
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.Surface;
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
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.avrecorder.view.GLCameraEncoderView;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter;
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
    @InjectView(R.id.rotateDeviceHint)
    ImageView rotateDeviceHint;
    @InjectView(R.id.drawer_full_background)
    ImageView drawerBackground;

    private RecordPresenter recordPresenter;
    private CameraEffectsAdapter cameraEffectsAdapter;
    private CameraColorFilterAdapter cameraColorFilterAdapter;
    private Tracker tracker;

    private boolean buttonBackPressed;
    private boolean fxHidden;
    private boolean colorFilterHidden;
    private boolean recording;
    private OrientationHelper orientationHelper;
    private boolean lockRotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        ButterKnife.inject(this);
        drawerLayout.setDrawerListener(this);

        cameraView.setKeepScreenOn(true);
        recordPresenter = new RecordPresenter(this, this, cameraView);

        initEffectsRecycler();
        configChronometer();
        initOrientationHelper();

        navigateToEditButton.setBorderWidth(5);
        navigateToEditButton.setBorderColor(Color.WHITE);
        numVideosRecorded.setVisibility(View.GONE);

        VideonaApplication app = (VideonaApplication) getApplication();
        tracker = app.getTracker();
    }

    private void initOrientationHelper() {
        lockRotation = false;
        orientationHelper = new OrientationHelper(this);
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
        orientationHelper.stopMonitoringOrientation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordPresenter.onResume();
        unlockScreenRotation();
        recording = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recordPresenter.onDestroy();
    }

    @OnClick(R.id.button_record)
    public void OnRecordButtonClicked() {
        if (!recording) {
            recordPresenter.requestRecord();
            sendButtonTracked("Start recording");
        } else {
            recordPresenter.stopRecord();
            sendButtonTracked("Stop recording");
        }
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
    public void showCameraEffectColor(List<CameraEffectColor> effects) {
        showEffectsRecyler(colorFilterRecycler);
        colorFilterHidden = false;
        buttonCameraEffectFx.setActivated(true);
    }

    @Override
    public void lockScreenRotation() {
        orientationHelper.stopMonitoringOrientation();
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
    public void showRecordedVideoThumb(String path) {
        // Glide.with(this).load(path).centerCrop().crossFade().into(navigateToEditButton);
        Glide.with(this).load(path).into(navigateToEditButton);
    }

    @Override
    public void showVideosRecordedNumber(int numberOfVideos) {
        numVideosRecorded.setVisibility(View.VISIBLE);
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
        //edit.putExtra("SHARE", false);
        startActivity(edit);
    }


    @OnClick(R.id.button_navigate_drawer)
    public void showDrawer() {
        drawerLayout.openDrawer(navigatorView);
        drawerBackground.setVisibility(View.VISIBLE);
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
    public void onColorEffectSelected(CameraEffectColor colorEffect) {
        recordPresenter.applyEffect(colorEffect.getFilterId());
        sendButtonTracked(colorEffect.getIconResourceId());
        cameraEffectsAdapter.resetSelectedEffect();
    }

    @Override
    public void onFxSelected(CameraEffectFx fx) {
        recordPresenter.applyEffect(fx.getFilterId());
        sendButtonTracked(fx.getIconResourceId());
        cameraColorFilterAdapter.resetSelectedEffect();
    }

    @OnClick({R.id.button_record, R.id.button_toggle_flash, R.id.button_camera_effect_color,
            R.id.button_camera_effect_fx, R.id.button_change_camera})
    public void clickListener(View view) {
        sendButtonTracked(view.getId());
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
            case R.id.button_camera_effect_fx:
                label = "Fx filters";
                break;
            case R.id.button_camera_effect_color:
                label = "Color filters";
                break;
            case R.drawable.common_filter_ad0_none_normal:
                label = "None color filter";
                break;
            case R.drawable.common_filter_ad1_aqua_normal:
                label = "Aqua color filter";
                break;
            case R.drawable.common_filter_ad2_postericebw_normal:
                label = "Posterize bw color filter";
                break;
            case R.drawable.common_filter_ad3_emboss_normal:
                label = "Emboss color filter";
                break;
            case R.drawable.common_filter_ad4_mono_normal:
                label = "Mono color filter";
                break;
            case R.drawable.common_filter_ad5_negative_normal:
                label = "Negative color filter";
                break;
            case R.drawable.common_filter_ad6_green_normal:
                label = "Green color filter";
                break;
            case R.drawable.common_filter_ad7_posterize_normal:
                label = "Posterize color filter";
                break;
            case R.drawable.common_filter_ad8_sepia_normal:
                label = "Sepia color filter";
                break;
            case R.drawable.common_filter_fx_fx0_none_normal:
                label = "None fx filter";
                break;
            case R.drawable.common_filter_fx_fx1_fisheye_normal:
                label = "Fisheye fx filter";
                break;
            case R.drawable.common_filter_fx_fx2_stretch_normal:
                label = "Stretch fx filter";
                break;
            case R.drawable.common_filter_fx_fx3_dent_normal:
                label = "Dent fx filter";
                break;
            case R.drawable.common_filter_fx_fx4_mirror_normal:
                label = "Mirror fx filter";
                break;
            case R.drawable.common_filter_fx_fx5_squeeze_normal:
                label = "Squeeze fx filter";
                break;
            case R.drawable.common_filter_fx_fx6_tunnel_normal:
                label = "Tunnel fx filter";
                break;
            case R.drawable.common_filter_fx_fx7_twirl_normal:
                label = "Twirl fx filter";
                break;
            case R.drawable.common_filter_fx_fx8_bulge_normal:
                label = "Bulge filter";
                break;
            default:
                label = "Other";
        }
        sendButtonTracked(label);
    }

    private void sendButtonTracked(String label) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("RecordActivity")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getApplication().getBaseContext()).dispatchLocalHits();
    }


    private class OrientationHelper extends OrientationEventListener {

        Context context;
        private int rotationView;
        private boolean detectScreenOrientation90;
        private boolean detectScreenOrientation270;

        public OrientationHelper(Context context) {
            super(context);
            detectScreenOrientation90 = false;
            detectScreenOrientation270 = false;
            this.context = context;
        }

        /**
         *
         */
        public void startMonitoringOrientation() throws NoOrientationSupportException {
            rotationView = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
            determineInitialOrientation();
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

        private void determineInitialOrientation() {
            if (rotationView == Surface.ROTATION_90) {
                detectScreenOrientation90 = true;

            } else if (rotationView == Surface.ROTATION_270) {
                detectScreenOrientation270 = true;
            }
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (!lockRotation) {
                if (orientation > 85 && orientation < 95) {
                    //  Log.d(LOG_TAG, "rotationPreview onOrientationChanged " + orientation);
                    rotateDeviceHint.setVisibility(View.GONE);
                    if (detectScreenOrientation90) {
                        if (rotationView == Surface.ROTATION_90 && detectScreenOrientation270) {
                            return;
                        }
                        if (rotationView == Surface.ROTATION_270) {
                            rotationView = Surface.ROTATION_90;
                            if (recordPresenter != null) {
                                recordPresenter.rotateCamera(rotationView);
                            }
                        } else {
                            if (rotationView == Surface.ROTATION_90) {
                                rotationView = Surface.ROTATION_270;
                                if (recordPresenter != null) {
                                    recordPresenter.rotateCamera(rotationView);
                                }
                            }
                        }
                        detectScreenOrientation90 = false;
                        detectScreenOrientation270 = true;
                    }
                } else if (orientation > 265 && orientation < 275) {
                    rotateDeviceHint.setVisibility(View.GONE);
                    if (detectScreenOrientation270) {
                        //  Log.d("CameraPreview", "rotationPreview onOrientationChanged .*.*.*.*.*.* 270");
                        if (rotationView == Surface.ROTATION_270 && detectScreenOrientation90) {
                            return;
                        }
                        if (rotationView == Surface.ROTATION_270) {
                            rotationView = Surface.ROTATION_90;
                            if (recordPresenter != null) {
                                recordPresenter.rotateCamera(rotationView);
                            }
                        } else {
                            if (rotationView == Surface.ROTATION_90) {
                                rotationView = Surface.ROTATION_270;
                                if (recordPresenter != null) {
                                    recordPresenter.rotateCamera(rotationView);
                                }
                            }
                        }
                        detectScreenOrientation90 = true;
                        detectScreenOrientation270 = false;
                    }
                } else if ((orientation > 345 || orientation < 15) && orientation != -1) {
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
        }

        private class NoOrientationSupportException extends Exception {
        }
    }
}
