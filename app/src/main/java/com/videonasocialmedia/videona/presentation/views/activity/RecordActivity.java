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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.videonasocialmedia.videona.presentation.mvp.views.ShareView;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraColorFilterAdapter;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectColor;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectFx;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectsAdapter;
import com.videonasocialmedia.videona.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.videona.presentation.views.listener.OnColorEffectSelectedListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnFxSelectedListener;
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
public class RecordActivity extends VideonaActivity implements RecordView,
        ShareView, OnColorEffectSelectedListener, OnFxSelectedListener {

    private final String LOG_TAG = getClass().getSimpleName();
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

    private static RecordActivity parent;
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
    private ProgressDialog progressDialog;

    public Thread performOnBackgroundThread(RecordActivity parent, final Runnable runnable) {
        this.parent = parent;
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                }
            }
        };
        t.start();
        return t;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        ButterKnife.inject(this);

        cameraView.setKeepScreenOn(true);
        recordPresenter = new RecordPresenter(this, this, this, cameraView);

        initEffectsRecycler();
        configChronometer();
        initOrientationHelper();

        navigateToEditButton.setBorderWidth(5);
        navigateToEditButton.setBorderColor(Color.WHITE);
        numVideosRecorded.setVisibility(View.GONE);

        VideonaApplication app = (VideonaApplication) getApplication();
        tracker = app.getTracker();
        createProgressDialog();
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

    private void createProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_processing));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setIndeterminate(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recordPresenter.onStart();
        onColorFiltersButtonClicked();
        mixpanel.timeEvent("Time in Record Activity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordPresenter.onResume();
        recording = false;
        disableShareButton();
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
        buttonCameraEffectColor.setActivated(true);
    }


    @Override
    public void lockScreenRotation() {
        orientationHelper.stopMonitoringOrientation();
        lockRotation = true;
    }

    @Override
    public void reStartScreenRotation() {
        try {
            lockRotation = false;
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

    @OnClick(R.id.button_navigate_edit)
    public void navigateToEdit() {
        if (!recording) {
           // Intent edit = new Intent(this, EditActivity.class);
            //edit.putExtra("SHARE", false);
          //  startActivity(edit);
            mixpanel.track("Navigate edit Button clicked in Record Activity", null);
        }
    }

    @OnClick(R.id.button_share)
    public void exportAndShare() {
        if (!recording) {
            showProgressDialog();
            sendMetadataTracking();
            final Runnable r = new Runnable() {
                public void run() {
                    recordPresenter.startExport();
                }
            };
            performOnBackgroundThread(this, r);
        }
    }
    
    @Override
    public void showProgressDialog() {
        progressDialog.show();
        progressDialog.setIcon(R.drawable.icon_cut_normal);

        ((TextView) progressDialog.findViewById(Resources.getSystem()
                .getIdentifier("message", "id", "android")))
                .setTextColor(Color.WHITE);

        ((TextView) progressDialog.findViewById(Resources.getSystem()
                .getIdentifier("alertTitle", "id", "android")))
                .setTextColor(Color.WHITE);

        progressDialog.findViewById(Resources.getSystem().getIdentifier("topPanel", "id",
                "android")).setBackgroundColor(getResources().getColor(R.color.videona_blue_1));

        progressDialog.findViewById(Resources.getSystem().getIdentifier("customPanel", "id",
                "android")).setBackgroundColor(getResources().getColor(R.color.videona_blue_2));
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
    public void showSettings(){
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
    public void onColorEffectSelected(CameraEffectColor colorEffect) {
        recordPresenter.applyEffect(colorEffect.getFilterId());
        sendButtonTracked(colorEffect.getIconResourceId());
        cameraEffectsAdapter.resetSelectedEffect();
        scrollColorEffectList(colorEffect);
    }

    private void scrollColorEffectList(CameraEffectColor colorEffect) {
        List effects = cameraColorFilterAdapter.getElementList();
        int index = effects.indexOf(colorEffect);
        int scroll= index>cameraColorFilterAdapter.getPreviousSelectionPosition()?1:-1;
        colorFilterRecycler.scrollToPosition(index + scroll);
    }

    @Override
    public void onFxSelected(CameraEffectFx fx) {
        recordPresenter.applyEffect(fx.getFilterId());
        sendButtonTracked(fx.getIconResourceId());
        cameraColorFilterAdapter.resetSelectedEffect();
        scrollColorEffectList(fx);
    }
    private void scrollColorEffectList(CameraEffectFx fx) {
        List effects = cameraEffectsAdapter.getElementList();
        int index = effects.indexOf(fx);
        int scroll= index>cameraEffectsAdapter.getPreviousSelectionPosition()?1:-1;
        effectsRecycler.scrollToPosition(index + scroll);
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
                mixpanel.track("None color filter selected", null);
                break;
            case R.drawable.common_filter_ad1_aqua_normal:
                label = "Aqua color filter";
                mixpanel.track("Aqua color filter selected", null);
                break;
            case R.drawable.common_filter_ad2_postericebw_normal:
                label = "Posterize bw color filter";
                mixpanel.track("Posterize bw color filter selected", null);
                break;
            case R.drawable.common_filter_ad3_emboss_normal:
                label = "Emboss color filter";
                mixpanel.track("Emboss color filter selected", null);
                break;
            case R.drawable.common_filter_ad4_mono_normal:
                label = "Mono color filter";
                mixpanel.track("Mono color filter selected", null);
                break;
            case R.drawable.common_filter_ad5_negative_normal:
                label = "Negative color filter";
                mixpanel.track("Negative color filter selected", null);
                break;
            case R.drawable.common_filter_ad6_green_normal:
                label = "Green color filter";
                mixpanel.track("Green color filter selected", null);
                break;
            case R.drawable.common_filter_ad7_posterize_normal:
                label = "Posterize color filter";
                mixpanel.track("Posterize color filter selected", null);
                break;
            case R.drawable.common_filter_ad8_sepia_normal:
                label = "Sepia color filter";
                mixpanel.track("Sepia color filter selected", null);
                break;
            case R.drawable.common_filter_fx_fx0_none_normal:
                label = "None fx filter";
                mixpanel.track("None fx filter selected", null);
                break;
            case R.drawable.common_filter_fx_fx1_fisheye_normal:
                label = "Fisheye fx filter";
                mixpanel.track("Fisheye fx filter selected", null);
                break;
            case R.drawable.common_filter_fx_fx2_stretch_normal:
                label = "Stretch fx filter";
                mixpanel.track("Stretch fx filter selected", null);
                break;
            case R.drawable.common_filter_fx_fx3_dent_normal:
                label = "Dent fx filter";
                mixpanel.track("Dent fx filter selected", null);
                break;
            case R.drawable.common_filter_fx_fx4_mirror_normal:
                label = "Mirror fx filter";
                mixpanel.track("Mirror fx filter selected", null);
                break;
            case R.drawable.common_filter_fx_fx5_squeeze_normal:
                label = "Squeeze fx filter";
                mixpanel.track("Squeeze fx filter selected", null);
                break;
            case R.drawable.common_filter_fx_fx6_tunnel_normal:
                label = "Tunnel fx filter";
                mixpanel.track("Tunnel fx filter selected", null);
                break;
            case R.drawable.common_filter_fx_fx7_twirl_normal:
                label = "Twirl fx filter";
                mixpanel.track("Twirl fx filter selected", null);
                break;
            case R.drawable.common_filter_fx_fx8_bulge_normal:
                label = "Bulge filter";
                mixpanel.track("Bulge filter selected", null);
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
