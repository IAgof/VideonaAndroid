/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Author:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * This class is used to show a preview of the selected video.
 */
public class VideoPreviewActivity extends Activity implements OnPreparedListener, OnErrorListener {

    @InjectView(R.id.videoView)
    VideoView videoView;

    private final String LOG_TAG = "VIDEO PREVIEW ACTIVITY";
    private MediaPlayer mediaPlayer = null;
    MediaController mediaController;
    String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        videoPath = bundle.getString("videoPath");
    }

    @Override
     protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");

        if (videoPath == null || videoPath.isEmpty()) {
            showError();
        } else {
            mediaController = new MediaController(this);
            mediaController.setVisibility(View.VISIBLE);
            mediaController.setAnchorView(videoView);

            videoView.setVideoPath(videoPath);
            videoView.setMediaController(mediaController);
            videoView.requestFocus();
            videoView.setOnPreparedListener(this);
            videoView.setOnErrorListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseVideoView();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
        releaseVideoView();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(LOG_TAG, "onPrepared");
        mediaPlayer = mp;
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(LOG_TAG, "onError");
        showError();
        return true;
    }

    /**
     * This method release the media player and the video view
     */
    private void releaseVideoView() {
        videoView.stopPlayback();
        videoView.clearFocus();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Shows an alert dialog if an error occurs and returns to the previous activity
     */
    private void showError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoPreviewActivity.this,
                AlertDialog.THEME_HOLO_LIGHT);
        builder.setMessage(R.string.invalid_video)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        (VideoPreviewActivity.this).finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
