/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.eventbus.events.survey.JoinBetaEvent;
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.mvp.views.ShareView;
import com.videonasocialmedia.videona.presentation.views.fragment.JoinBetaDialogFragment;
import com.videonasocialmedia.videona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

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
public class RecordActivity extends RecordBaseActivity implements RecordView,
        ShareView {

    @InjectView(R.id.button_settings)
    ImageButton buttonSettings;

    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        ButterKnife.inject(this);

        recordPresenter = new RecordPresenter(this, this, this, cameraView, sharedPreferences);
        initActivity();
        createProgressDialog();
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
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        disableShareButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void startChronometer() {
        super.startChronometer();
        disableShareButton();
    }

    @Override
    public void stopChronometer() {
        super.stopChronometer();
        enableShareButton();
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

    @OnClick(R.id.button_share)
    public void exportAndShare() {
        if (!recording) {
            showProgressDialog();
            sendMetadataTracking();
            startExportThread();
        }
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show();
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
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
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
    public void enableShareButton() {
        shareButton.setAlpha(1f);
        shareButton.setClickable(true);
    }

    @Override
    public void disableShareButton() {
        shareButton.setAlpha(0.25f);
        shareButton.setClickable(false);
    }

}
