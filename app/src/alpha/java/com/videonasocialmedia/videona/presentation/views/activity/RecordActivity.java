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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
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
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.avrecorder.view.GLCameraView;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.effects.domain.model.Effect;
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.adapter.EffectAdapter;
import com.videonasocialmedia.videona.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.videona.presentation.views.dialog.VideonaToast;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectSelectedListener;
import com.videonasocialmedia.videona.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.videona.utils.AnalyticsConstants;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * @author Álvaro Martínez Marco
 */

/**
 * RecordActivity manages a single live record.
 */
public class RecordActivity extends VideonaActivity implements DrawerLayout.DrawerListener,
        RecordView, OnEffectSelectedListener {

    private final String LOG_TAG = getClass().getSimpleName();

    @Bind(R.id.activity_record_drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.activity_record_navigation_drawer)
    View navigatorView;
    @Bind(R.id.button_record)
    ImageButton recButton;
    @Bind(R.id.button_share)
    ImageButton shareButton;
    @Bind(R.id.cameraPreview)
    GLCameraView cameraView;
    @Bind(R.id.button_change_camera)
    ImageButton rotateCameraButton;
    @Bind(R.id.button_navigate_edit)
    CircleImageView buttonThumbClipRecorded;
    @Bind(R.id.record_catalog_recycler_shader_effects)
    RecyclerView shaderEffectsRecycler;
    @Bind(R.id.record_catalog_recycler_overlay_effects)
    RecyclerView overlayFilterRecycler;
    @Bind(R.id.imageRecPoint)
    ImageView recordingIndicator;
    @Bind(R.id.chronometer_record)
    Chronometer chronometer;
    @Bind(R.id.button_toggle_flash)
    ImageButton flashButton;
    @Bind(R.id.button_camera_effect_shader)
    ImageButton buttonCameraEffectShader;
    @Bind(R.id.button_camera_effect_overlay)
    ImageButton buttonCameraEffectOverlay;
    @Bind(R.id.button_navigate_drawer)
    ImageButton drawerButton;
    @Bind(R.id.text_view_num_videos)
    TextView numVideosRecorded;
    @Bind(R.id.rotateDeviceHint)
    ImageView rotateDeviceHint;
    @Bind(R.id.drawer_full_background)
    ImageView drawerBackground;
    @Bind(R.id.button_remove_filters)
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
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * if for result
     **/
    private String resultVideoPath;
    private boolean externalIntent = false;

    // TODO define Effects ID
    private String OVERLAY_EFFECT_GIFT_ID = "GIFT_OV";

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String videoToSharePath = bundle.getString(ExportProjectService.FILEPATH);
                int resultCode = bundle.getInt(ExportProjectService.RESULT);
                if (resultCode == RESULT_OK) {
                    hideProgressDialog();
                    goToShare(videoToSharePath);
                } else {
                    hideProgressDialog();
                    showError(R.string.addMediaItemToTrackError);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_record);
        keepScreenOn();
        ButterKnife.bind(this);
        drawerLayout.setDrawerListener(this);
        checkAction();
        sharedPreferences = getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        recordPresenter = new RecordPresenter(this, this, cameraView, sharedPreferences, externalIntent);

        initEffectsRecycler();
        configChronometer();
        initOrientationHelper();
        configThumbsView();
        createProgressDialog();

        buttonThumbClipRecorded.setBorderWidth(5);
        buttonThumbClipRecorded.setBorderColor(Color.WHITE);
        numVideosRecorded.setVisibility(View.GONE);
    }

    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void checkAction() {
        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals(MediaStore.ACTION_VIDEO_CAPTURE)) {
                if (getIntent().getClipData() != null) {
                    resultVideoPath = getIntent().getClipData().getItemAt(0).getUri().toString();
                    if (resultVideoPath.startsWith("file://"))
                        resultVideoPath = resultVideoPath.replace("file://", "");
                }
                externalIntent = true;
                drawerButton.setVisibility(View.GONE);
            }
        }
    }

    private void initEffectsRecycler() {
        cameraShaderEffectsAdapter = new EffectAdapter(recordPresenter.getShaderEffectList(), this);
        shaderEffectsRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        shaderEffectsRecycler.setAdapter(cameraShaderEffectsAdapter);
        shaderFilterHidden = true;

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

                int h = (int) ( elapsedTime / 3600000 );
                int m = (int) ( elapsedTime - h * 3600000 ) / 60000;
                int s = (int) ( elapsedTime - h * 3600000 - m * 60000 ) / 1000;
                // String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                String time = mm + ":" + ss;
                chronometer.setText(time);
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

    @Override
    protected void onDestroy() {
        recordPresenter.onDestroy();
        cameraView = null;
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onPause() {
        unregisterReceiver(receiver);
        recordPresenter.onPause();
        orientationHelper.stopMonitoringOrientation();
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        registerReceiver(receiver, new IntentFilter(ExportProjectService.NOTIFICATION));
        recordPresenter.onResume();
        recording = false;
        hideSystemUi();
        cameraOverlayEffectsAdapter.notifyDataSetChanged();
        cameraShaderEffectsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
        recordPresenter.onStart();
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
    protected void onStop() {
        recordPresenter.onStop();
        Log.d(LOG_TAG, "OnStop");
        super.onStop();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Utils.isKitKat() && hasFocus && mUseImmersiveMode) {
            setKitKatWindowFlags();
        }
    }

    @OnTouch(R.id.button_record) boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!recording) {
                recordPresenter.requestRecord();
                checkSelectedFilters();
            } else {
                recordPresenter.stopRecord();
            }
        }
        return true;
    }

    private void checkSelectedFilters() {
        Effect shaderEffect = recordPresenter.getSelectedShaderEffect();
        Effect overlayEffect = recordPresenter.getSelectedOverlayEffect();
        if(shaderEffect != null)
            sendFilterSelectedTracking(shaderEffect.getType(),
                    shaderEffect.getName().toLowerCase(),
                    shaderEffect.getIdentifier().toLowerCase());
        if(overlayEffect != null)
            sendFilterSelectedTracking(overlayEffect.getType(),
                    overlayEffect.getName().toLowerCase(),
                    overlayEffect.getIdentifier().toLowerCase());
    }

    private void sendFilterSelectedTracking(String type, String name, String code) {
        JSONObject userInteractionsProperties = new JSONObject();
        List<String> effectsCombinedList = getEffectsCombinedList();
        boolean combined = false;
        if (effectsCombinedList.size() > 1)
            combined = true;
        try {
            userInteractionsProperties.put(AnalyticsConstants.TYPE, type);
            userInteractionsProperties.put(AnalyticsConstants.NAME, name);
            userInteractionsProperties.put(AnalyticsConstants.CODE, code);
            userInteractionsProperties.put(AnalyticsConstants.RECORDING, recording);
            userInteractionsProperties.put(AnalyticsConstants.COMBINED, combined);
            userInteractionsProperties.put(AnalyticsConstants.FILTERS_COMBINED, effectsCombinedList);
            mixpanel.track(AnalyticsConstants.FILTER_SELECTED, userInteractionsProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.getPeople().increment(AnalyticsConstants.TOTAL_FILTERS_USED, 1);
    }

    private List<String> getEffectsCombinedList() {
        List<String> effects = new ArrayList<>();
        Effect effect1 = recordPresenter.getSelectedShaderEffect();
        Effect effect2 = recordPresenter.getSelectedOverlayEffect();
        if (effect1 != null)
            effects.add(effect1.getName().toLowerCase());
        if (effect2 != null)
            effects.add(effect2.getName().toLowerCase());
        return effects;
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

    @Override
    public void showCameraEffectShader(List<Effect> effects) {
        showEffectsRecylerView(shaderEffectsRecycler);
        shaderFilterHidden = false;
        buttonCameraEffectShader.setActivated(true);
    }

    @Override
    public void showCameraEffectOverlay(List<Effect> effects) {
        runTranslateAnimation(overlayFilterRecycler, 0, new AccelerateInterpolator(3));
        overlayFilterHidden = false;
        buttonCameraEffectOverlay.setActivated(true);
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

    @Override
    public void reStartScreenRotation() {
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

    @Override
    public void showFlashOn(boolean on) {
        trackUserInteracted(AnalyticsConstants.CHANGE_FLASH, String.valueOf(on));
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
        trackUserInteracted(AnalyticsConstants.CHANGE_CAMERA, AnalyticsConstants.CAMERA_FRONT);
        try {
            orientationHelper.reStartMonitoringOrientation();
        } catch (OrientationHelper.NoOrientationSupportException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showBackCameraSelected() {
        rotateCameraButton.setActivated(false);
        trackUserInteracted(AnalyticsConstants.CHANGE_CAMERA, AnalyticsConstants.CAMERA_BACK);
        try {
            orientationHelper.reStartMonitoringOrientation();
        } catch (OrientationHelper.NoOrientationSupportException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mixpanel.track(AnalyticsConstants.VIDEO_EXPORTED);
    }

    @Override
    public void showError(int stringResourceId) {
        Toast.makeText(this, this.getText(stringResourceId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goToShare(String videoToSharePath) {
        trackVideoExported();
        saveVideoFeaturesToConfig();
        Intent intent = new Intent(this, ShareActivity.class);
        intent.putExtra(Constants.VIDEO_TO_SHARE_PATH, videoToSharePath);
        startActivity(intent);
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
    public void finishActivityForResult(String originalVideoPath) {
        try {
            if (resultVideoPath != null) {
                Utils.copyFile(originalVideoPath, resultVideoPath);
                Utils.removeVideo(originalVideoPath);
            } else
                resultVideoPath = originalVideoPath;
            Uri videoUri = Uri.fromFile(new File(resultVideoPath));
            Intent returnIntent = new Intent();
            returnIntent.setData(videoUri);
            setResult(RESULT_OK, returnIntent);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void trackVideoExported() {
        JSONObject videoExportedProperties = new JSONObject();
        try {
            int projectDuration = recordPresenter.getProjectDuration();
            int numVideosOnProject = recordPresenter.getNumVideosOnProject();
            videoExportedProperties.put(AnalyticsConstants.VIDEO_LENGTH, projectDuration);
            videoExportedProperties.put(AnalyticsConstants.RESOLUTION,
                    recordPresenter.getResolution());
            videoExportedProperties.put(AnalyticsConstants.NUMBER_OF_CLIPS, numVideosOnProject);
            mixpanel.track(AnalyticsConstants.VIDEO_EXPORTED, videoExportedProperties);
            Log.d("ANALYTICS", "Tracked video exported event");
        } catch (JSONException e) {
            Log.e("TRACK_FAILED", String.valueOf(e));
        }
    }

    private void saveVideoFeaturesToConfig() {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, recordPresenter.getProjectDuration());
        preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, recordPresenter.getNumVideosOnProject());
        preferencesEditor.putString(ConfigPreferences.RESOLUTION, recordPresenter.getResolution());
        preferencesEditor.commit();
    }

    private void trackUserInteracted(String interaction, String result) {
        JSONObject userInteractionsProperties = new JSONObject();
        try {
            userInteractionsProperties.put(AnalyticsConstants.ACTIVITY, getClass().getSimpleName());
            userInteractionsProperties.put(AnalyticsConstants.RECORDING, recording);
            userInteractionsProperties.put(AnalyticsConstants.INTERACTION, interaction);
            userInteractionsProperties.put(AnalyticsConstants.RESULT, result);
            mixpanel.track(AnalyticsConstants.USER_INTERACTED, userInteractionsProperties);
            Log.d("ANALYTICS", "Tracked User Interacted event");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            trackUserInteracted(AnalyticsConstants.SET_FILTER_GROUP,
                    AnalyticsConstants.FILTER_GROUP_SHADER);
            showCameraEffectShader(null);
            if (removeFilterActivated) {
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

    private void hideShaderFilters() {
        hideEffectsRecyclerView(shaderEffectsRecycler);
        shaderFilterHidden = true;
        buttonCameraEffectShader.setActivated(false);
    }

    private void hideRemoveFilters() {

        hideRemoveView(removeFilters);
    }

    private void showRemoveFilters() {

        showRemoveView(removeFilters);
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

    private void runTranslateAnimation(View view, int translateY, Interpolator interpolator) {
        Animator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", translateY);
        slideInAnimation.setDuration(view.getContext().getResources()
                .getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimation.setInterpolator(interpolator);
        slideInAnimation.start();
    }

    private void hideEffectsRecyclerView(View view) {
        runTranslateAnimation(view, -Math.round(view.getTranslationY()), new DecelerateInterpolator(3));
    }

    private void hideRemoveView(View view) {
        runTranslateAnimation(view, -Math.round(view.getTranslationY()), new DecelerateInterpolator(3));
    }

    private void showEffectsRecylerView(View view) {
        int height = calculateTranslation(view);
        int translateY = -height;
        runTranslateAnimation(view, translateY, new AccelerateInterpolator(3));
    }

    private void showRemoveView(View view) {
        int height = calculateTranslation(view);
        int translateY = -height;
        runTranslateAnimation(view, translateY, new AccelerateInterpolator(3));
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
            trackUserInteracted(AnalyticsConstants.SET_FILTER_GROUP,
                    AnalyticsConstants.FILTER_GROUP_OVERLAY);
            showCameraEffectOverlay(null);
            if (removeFilterActivated) {
                showRemoveFilters();
            }
        }
    }

    @OnClick(R.id.button_remove_filters)
    public void onRemoveFiltersButtonClicked() {
        trackUserInteracted(AnalyticsConstants.CLEAR_FILTER, null);
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

    @OnClick(R.id.button_toggle_flash)
    public void toggleFlash() {
        recordPresenter.toggleFlash();
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
    }

    @OnClick(R.id.button_navigate_edit)
    public void navigateToEdit() {
        if (!recording) {
            //TODO(alvaro 130616) Save flash state
            recordPresenter.setFlashOff();
            Intent edit = new Intent(this, EditActivity.class);
            //edit.putExtra("SHARE", false);
            startActivity(edit);
        }
    }

    @OnClick(R.id.button_share)
    public void exportAndShare() {
        if (!recording) {
            recordPresenter.setFlashOff();
            Intent intent = new Intent(this, ExportProjectService.class);
            startService(intent);
            showProgressDialog();
            mixpanel.timeEvent(AnalyticsConstants.VIDEO_EXPORTED);
        }
    }

    @OnClick(R.id.button_navigate_drawer)
    public void showDrawer() {
        if (!recording) {
            trackUserInteracted(AnalyticsConstants.INTERACTION_OPEN_DRAWER, null);
            drawerLayout.openDrawer(navigatorView);
            drawerBackground.setVisibility(View.VISIBLE);
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

//        sendFilterSelectedTracking(effect.getType(),
//                effect.getName().toLowerCase(),
//                effect.getIdentifier().toLowerCase());
//
//        if (effect.getIdentifier().compareTo(OVERLAY_EFFECT_GIFT_ID) == 0 &&
//                !sharedPreferences.getBoolean(ConfigPreferences.FILTER_OVERLAY_GIFT, false)) {
//            // Reset effect to remove selected background
//            cameraOverlayEffectsAdapter.resetSelectedEffect();
//            trackGiftOpened(recordPresenter.getOverlayEffectGift());
//            showGiftFilterToast();
//            return;
//        }

        recordPresenter.applyEffect(effect);
        scrollEffectList(effect);
        showRemoveFilters();
        removeFilterActivated = true;

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

    @Override
    public void onEffectSelectionCancel(Effect effect) {
        recordPresenter.removeEffect(effect);
        if (!cameraOverlayEffectsAdapter.isEffectSelected() &&
                !cameraShaderEffectsAdapter.isEffectSelected()) {
            hideRemoveFilters();
        }
    }

    private void trackGiftOpened(Effect overlayEffectGift) {

        JSONObject giftDetails = new JSONObject();

        int giftsDownloadedCount;
        try {
            giftsDownloadedCount = mixpanel.getSuperProperties().getInt(AnalyticsConstants.TOTAL_GIFTS_DOWNLOADED);
        } catch (JSONException e) {
            giftsDownloadedCount = 0;
        }
        try {
            giftDetails.put(AnalyticsConstants.LAST_GIFT_DOWNLOADED_DATE,
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
            String giftResourceName = overlayEffectGift.getType() + " - " + overlayEffectGift.getName() + " " + overlayEffectGift.getIdentifier();
            giftDetails.put(AnalyticsConstants.LAST_GIFT_DOWNLOADED, giftResourceName);
            mixpanel.getPeople().set(giftDetails);
            giftDetails.put(AnalyticsConstants.TOTAL_GIFTS_DOWNLOADED, ++giftsDownloadedCount);
            mixpanel.registerSuperProperties(giftDetails);
        } catch (JSONException e) {
            Log.e("ANALYTICS", "Error sending created super property");
        }
        mixpanel.getPeople().increment(AnalyticsConstants.TOTAL_GIFTS_DOWNLOADED, 1);
    }

    private void showGiftFilterToast() {

        // Create and show toast
        VideonaToast toast = new VideonaToast.Builder(getApplicationContext())
                .withTitle(R.string.giftOverlayDialogTitle)
                .withMessage(R.string.giftOverlayDialogMessage)
                .withDrawableImage(R.drawable.common_filter_overlay_gift_open)
                .withDuration(Toast.LENGTH_LONG)
                .build();

        // Save user preferences
        editor.putBoolean(ConfigPreferences.FILTER_OVERLAY_GIFT, true);
        editor.commit();

        // Uptade overlay effect adapter
        cameraOverlayEffectsAdapter = new EffectAdapter(recordPresenter.getOverlayEffects(), this);
        overlayFilterRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        overlayFilterRecycler.setAdapter(cameraOverlayEffectsAdapter);


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
