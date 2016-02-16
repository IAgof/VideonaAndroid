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
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.videonasocialmedia.avrecorder.view.GLCameraEncoderView;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.eventbus.events.survey.JoinBetaEvent;
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.adapter.EffectAdapter;
import com.videonasocialmedia.videona.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.videona.presentation.views.fragment.BetaDialogFragment;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectSelectedListener;
import com.videonasocialmedia.videona.utils.AnalyticsConstants;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
import com.videonasocialmedia.videona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.greenrobot.event.EventBus;

/**
 * @author Álvaro Martínez Marco
 */

/**
 * RecordActivity manages a single live record.
 */
public class RecordActivity extends VideonaActivity implements RecordView,
        OnEffectSelectedListener {

    private final String LOG_TAG = getClass().getSimpleName();
    @InjectView(R.id.button_record)
    ImageButton recButton;
    @InjectView(R.id.cameraPreview)
    GLCameraEncoderView cameraView;
    @InjectView(R.id.button_change_camera)
    ImageButton rotateCameraButton;
    @InjectView(R.id.button_navigate_edit)
    CircleImageView navigateToEditButton;
    @InjectView(R.id.record_catalog_recycler_shader_effects)
    RecyclerView shaderEffectsRecycler;
    @InjectView(R.id.record_catalog_recycler_overlay_effects)
    RecyclerView overlayFilterRecycler;
    @InjectView(R.id.imageRecPoint)
    ImageView recordingIndicator;
    @InjectView(R.id.chronometer_record)
    Chronometer chronometer;
    @InjectView(R.id.button_settings)
    ImageButton buttonSettings;
    @InjectView(R.id.button_toggle_flash)
    ImageButton flashButton;
    @InjectView(R.id.button_camera_effect_shader)
    ImageButton buttonCameraEffectShader;
    @InjectView(R.id.button_camera_effect_overlay)
    ImageButton buttonCameraEffectOverlay;
    @InjectView(R.id.text_view_num_videos)
    TextView numVideosRecorded;
    @InjectView(R.id.rotateDeviceHint)
    ImageView rotateDeviceHint;
    @InjectView(R.id.button_share)
    ImageButton shareButton;
    @InjectView(R.id.button_remove_filters)
    ImageButton removeFilters;

    private RecordPresenter recordPresenter;
    private EffectAdapter cameraShaderEffectsAdapter;
    private EffectAdapter cameraOverlayEffectsAdapter;
    private boolean buttonBackPressed;
    private boolean shaderFilterHidden;
    private boolean overlayFilterHidden;
    private boolean removeFilterActivated;
    private boolean recording;
    private OrientationHelper orientationHelper;
    private AlertDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean mUseImmersiveMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.inject(this);

        cameraView.setKeepScreenOn(true);

        sharedPreferences = getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        recordPresenter = new RecordPresenter(this, this, cameraView, sharedPreferences);
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
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        recordPresenter.onResume();
        recording = false;
        hideSystemUi();
        checkAction();
    }

    private void checkAction() {
        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals(MediaStore.ACTION_VIDEO_CAPTURE)) {
                Log.d("info", String.valueOf(getIntent()));
                Log.d("path", String.valueOf(getIntent().getClipData().getItemAt(0).getUri().toString()));
                Log.d("INTENT", "video capture");
                // TODO change this to an activity that sends the result to intent-filter
            } else {
                Log.d("INTENT", "nothing");
            }
        }
    }

    private void setResult() {
//        String urlStr = "hola";
//        Uri myVideoUri = new URI(new File(urlStr)).getPath();
//        Intent returnIntent = new Intent();
//        returnIntent.setData(myVideoUri);
//        setResult(RESULT_OK, returnIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        recordPresenter.onPause();
        orientationHelper.stopMonitoringOrientation();
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

    @OnClick(R.id.button_navigate_edit)
    public void OnButtonNavigateEditClicked() {

        mixpanel.track("Navigate edit Button clicked in Record Activity", null);

        if(sharedPreferences.getBoolean(ConfigPreferences.EMAIL_BETA_DONE,false)){
            exportAndShare();
        } else {
            // Every two weeks, ask for mail to join beta
            if( (Calendar.WEEK_OF_YEAR % 2 == 1) &&
                    sharedPreferences.getBoolean(ConfigPreferences.EMAIL_BETA_FORTNIGHT, false)) {
                new BetaDialogFragment().show(getFragmentManager(), "BetaDialogFragment");
                editor.putBoolean(ConfigPreferences.EMAIL_BETA_FORTNIGHT, false);
                editor.commit();
            } else {
                exportAndShare();
                editor.putBoolean(ConfigPreferences.EMAIL_BETA_FORTNIGHT, true);
                editor.commit();
            }
        }
    }

    public void onEvent(JoinBetaEvent event) {
        String email = event.email;
        mixpanel.getPeople().identify(mixpanel.getDistinctId());
        mixpanel.getPeople().set("$email", email); //Special properties in Mixpanel use $ before// property name

        editor.putBoolean(ConfigPreferences.EMAIL_BETA_DONE, true);
        editor.commit();
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
            trackUserInteracted(AnalyticsConstants.SET_FILTER_GROUP,
                    AnalyticsConstants.FILTER_GROUP_SHADER);
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
            trackUserInteracted(AnalyticsConstants.SET_FILTER_GROUP,
                    AnalyticsConstants.FILTER_GROUP_OVERLAY);
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
    }

    @OnClick(R.id.button_share)
    public void exportAndShare() {
        if (!recording) {
            showProgressDialog();
            mixpanel.timeEvent(AnalyticsConstants.VIDEO_EXPORTED);
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

    @Override
    public void goToShare(String videoToSharePath) {
        trackVideoExported();
        saveVideoFeaturesToConfig();
        recordPresenter.removeMasterVideos();
        Intent intent = new Intent(this, ShareVideoActivity.class);
        intent.putExtra("VIDEO_EDITED", videoToSharePath);
        startActivity(intent);
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
            trackUserInteracted(AnalyticsConstants.INTERACTION_OPEN_SETTINGS, null);
        }

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void trackUserInteracted(String interaction, String result) {
        JSONObject userInteractionsProperties = new JSONObject();
        try {
            userInteractionsProperties.put(AnalyticsConstants.ACTIVITY, getClass().getSimpleName());
            userInteractionsProperties.put(AnalyticsConstants.RECORDING, recording);
            userInteractionsProperties.put(AnalyticsConstants.INTERACTION, interaction);
            userInteractionsProperties.put(AnalyticsConstants.RESULT, result);
            mixpanel.track(AnalyticsConstants.USER_INTERACTED, userInteractionsProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        recordPresenter.applyEffect(effect);
        scrollEffectList(effect);
        showRemoveFilters();
        removeFilterActivated = true;
        sendFilterSelectedTracking(effect.getType(),
                effect.getName().toLowerCase(),
                effect.getIdentifier().toLowerCase());
    }

    private void sendFilterSelectedTracking(String type, String name, String code) {
        JSONObject userInteractionsProperties = new JSONObject();
        List<String> effectsCombinedList = getEffectsCombinedList();
        boolean combined = false;
        if(effectsCombinedList.size() > 1)
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
    }

    private List<String> getEffectsCombinedList() {
        List<String> effects = new ArrayList<>();
        Effect effect1 = recordPresenter.getSelectedShaderEffect();
        Effect effect2 = recordPresenter.getSelectedOverlayEffect();
        if(effect1 != null)
            effects.add(effect1.getName().toLowerCase());
        if(effect2 != null)
            effects.add(effect2.getName().toLowerCase());
        return effects;
    }

    @Override
    public void onEffectSelectionCancel(Effect effect) {
        recordPresenter.removeEffect(effect);
        if(!cameraOverlayEffectsAdapter.isEffectSelected() &&
                !cameraShaderEffectsAdapter.isEffectSelected()){
            hideRemoveFilters();
        }
    }

    private void scrollEffectList(com.videonasocialmedia.videona.model.entities.editor.effects.Effect effect) {
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

}
