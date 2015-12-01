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
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.videonasocialmedia.avrecorder.view.GLCameraEncoderView;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.eventbus.events.survey.JoinBetaEvent;
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.mvp.views.ShareView;
import com.videonasocialmedia.videona.presentation.views.adapter.Effect;
import com.videonasocialmedia.videona.presentation.views.adapter.EffectAdapter;
import com.videonasocialmedia.videona.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.videona.presentation.views.fragment.JoinBetaDialogFragment;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectSelectedListener;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
import com.videonasocialmedia.videona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * @author Álvaro Martínez Marco
 */

/**
 * RecordActivity manages a single live record.
 */
public class RecordActivity extends VideonaActivity implements RecordView,
        ShareView, OnEffectSelectedListener {

    private final String LOG_TAG = getClass().getSimpleName();
    @InjectView(R.id.button_record)
    ImageButton recButton;
    @InjectView(R.id.cameraPreview)
    GLCameraEncoderView cameraView;
    @InjectView(R.id.button_change_camera)
    ImageButton rotateCameraButton;
    @InjectView(R.id.button_navigate_edit)
    CircleImageView navigateToEditButton;
    @InjectView(R.id.record_catalog_recycler_distortion_effects)
    RecyclerView effectsRecycler;
    @InjectView(R.id.record_catalog_recycler_color_effects)
    RecyclerView colorFilterRecycler;
    @InjectView(R.id.imageRecPoint)
    ImageView recordingIndicator;
    @InjectView(R.id.chronometer_record)
    Chronometer chronometer;
    @InjectView(R.id.button_settings)
    ImageButton buttonSettings;
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

    private RecordPresenter recordPresenter;
    private EffectAdapter cameraDistortionEffectsAdapter;
    private EffectAdapter cameraColorEffectsAdapter;
    private boolean buttonBackPressed;
    private boolean fxHidden;
    private boolean colorFilterHidden;
    private boolean recording;
    private OrientationHelper orientationHelper;
    private AlertDialog progressDialog;

    private boolean mUseImmersiveMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        ButterKnife.inject(this);

        cameraView.setKeepScreenOn(true);

        SharedPreferences sharedPreferences = getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        recordPresenter = new RecordPresenter(this, this, this, cameraView, sharedPreferences);
        initEffectsRecycler();
        configChronometer();
        initOrientationHelper();
        configThumbsView();
        createProgressDialog();
    }

    private void configThumbsView() {
        navigateToEditButton.setBorderWidth(5);
        navigateToEditButton.setBorderColorResource(R.color.textColorNumVideos);
        numVideosRecorded.setVisibility(View.GONE);
    }

    private void initOrientationHelper() {
        orientationHelper = new OrientationHelper(this);
    }

    private void initEffectsRecycler() {
        cameraDistortionEffectsAdapter = new EffectAdapter(Effect.getDistortionEffectList(), this);
        effectsRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        effectsRecycler.setAdapter(cameraDistortionEffectsAdapter);
        fxHidden = true;

        cameraColorEffectsAdapter = new EffectAdapter(Effect.getColorEffectList(), this);
        colorFilterRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        colorFilterRecycler.setAdapter(cameraColorEffectsAdapter);
        colorFilterHidden = false;
        buttonCameraEffectColor.setActivated(true);
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

    private void createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_export_progress, null);
        progressDialog = builder.setCancelable(false)
                .setView(dialogView)
                .create();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestPermissions();
        recordPresenter.onStart();
        mixpanel.timeEvent("Time in Record Activity");
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        recordPresenter.onResume();
        recording = false;
        disableShareButton();
        hideSystemUi();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
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

    @OnClick(R.id.button_record)
    public void OnRecordButtonClicked() {
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

    @OnClick(R.id.button_navigate_edit)
    public void OnButtonNavigateEditClicked() {
        new JoinBetaDialogFragment().show(getFragmentManager(), "joinBetaDialogFragment");
    }

    public void onEvent(JoinBetaEvent event) {
        String email = event.email;
        mixpanel.getPeople().identify(mixpanel.getDistinctId());
        mixpanel.getPeople().set("$email", email); //Special properties in Mixpanel use $ before
                                                   // property name
    }

    @Override
    public void showRecordButton() {
        recButton.setImageResource(R.drawable.activity_record_icon_rec_normal);
        recButton.setAlpha(1f);
        recording = false;

    }

    @Override
    public void showStopButton() {
        recButton.setImageResource(R.drawable.activity_record_icon_stop_normal);
        recButton.setAlpha(1f);
        recording = true;
    }

    @Override
    public void startChronometer() {
        resetChronometer();
        chronometer.start();
        showRecordingIndicator();
        disableShareButton();
    }

    private void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setText(getString(R.string.chronometerDefault));
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
        enableShareButton();
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
        int height = calculateTranslation(colorFilterRecycler);
        runTranslateAnimation(colorFilterRecycler, height, new DecelerateInterpolator(3));
        colorFilterHidden = true;
        buttonCameraEffectColor.setActivated(false);
    }

    private void hideView(View view) {
        runTranslateAnimation(view, -Math.round(view.getTranslationY()), new DecelerateInterpolator(3));
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
    public void showCameraEffectFx(List<Effect> effects) {
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
    public void showCameraEffectColor(List<Effect> effects) {
        runTranslateAnimation(colorFilterRecycler, 0, new AccelerateInterpolator(3));
        colorFilterHidden = false;
        buttonCameraEffectColor.setActivated(true);
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
        navigateToEditButton.setVisibility(View.VISIBLE);
        Glide.with(this).load(path).into(navigateToEditButton);
    }

    @Override
    public void hideRecordedVideoThumb() {
        navigateToEditButton.setVisibility(View.INVISIBLE);
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

        if (buttonBackPressed) {
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
        recordPresenter.removeMasterVideos();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        Uri uri = Utils.obtainUriToShare(this, videoToSharePath);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
    }

    @Override
    public void showMessage(final int message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.button_settings)
    public void navigateToSettings() {
        if (!recording) {
            mixpanel.track("Navigate settings Button clicked in Record Activity", null);
        }

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    @Override
    public void showSettings() {
        buttonSettings.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSettings() {
        buttonSettings.setVisibility(View.INVISIBLE);
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
    public void enableShareButton() {
        shareButton.setAlpha(1f);
        shareButton.setClickable(true);
    }

    @Override
    public void disableShareButton() {
        shareButton.setAlpha(0.25f);
        shareButton.setClickable(false);
    }

    @Override
    public void onEffectSelected(Effect effect) {
        recordPresenter.applyEffect(effect.getFilterId());
        sendButtonTracked(effect.getIconResourceId());
        resetOtherEffects();
        scrollEffectList(effect);
    }

    private void resetOtherEffects() {
        if (!colorFilterHidden) {
            cameraDistortionEffectsAdapter.resetSelectedEffect();
        } else if (!fxHidden)
            cameraColorEffectsAdapter.resetSelectedEffect();
    }

    private void scrollEffectList(Effect effect) {
        if (!colorFilterHidden) {
            List effects = cameraColorEffectsAdapter.getElementList();
            int index = effects.indexOf(effect);
            int scroll = index > cameraColorEffectsAdapter.getPreviousSelectionPosition() ? 1 : -1;
            colorFilterRecycler.scrollToPosition(index + scroll);
        } else if (!fxHidden) {
            List effects = cameraDistortionEffectsAdapter.getElementList();
            int index = effects.indexOf(effect);
            int scroll = index > cameraDistortionEffectsAdapter.getPreviousSelectionPosition() ? 1 : -1;
            effectsRecycler.scrollToPosition(index + scroll);
        }
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
                Log.d(LOG_TAG, "determineOrientation rotationPreview " + rotation +
                        " cameraInfoOrientation ");
            }
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

    @OnClick({R.id.button_record, R.id.button_toggle_flash, R.id.button_camera_effect_color,
            R.id.button_camera_effect_fx, R.id.button_change_camera})
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
            case R.id.button_camera_effect_fx:
                label = "Fx filters";
                break;
            case R.id.button_camera_effect_color:
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

}
