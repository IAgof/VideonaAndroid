/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 * Verónica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.presentation.mvp.views.ShareView;
import com.videonasocialmedia.videona.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ShareActivity extends Activity implements ShareView, OnPreparedListener,
        DrawerLayout.DrawerListener, MediaPlayer.OnErrorListener {


    private final String LOG_TAG = this.getClass().getSimpleName();
    //@InjectView(R.id.share_button_play)
    //ImageButton buttonPlay;
    @InjectView(R.id.share_video_view)
    VideoView videoView;
    Uri uri;

    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private String videoPath;


    /**
     * Tracker google analytics
     */
    private Tracker tracker;

    @InjectView(R.id.activity_share_drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.activity_share_navigation_drawer)
    View navigatorView;

    /**
     * Button navigation drawer
     */
    @InjectView(R.id.button_navigate_drawer)
    ImageButton buttonNavigateDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.inject(this);
        //buttonPlay.setVisibility(View.GONE);

        VideonaApplication app = (VideonaApplication) getApplication();
        tracker = app.getTracker();

        Intent in = getIntent();
        videoPath = in.getStringExtra("VIDEO_EDITED");
        uri = Utils.obtainUriToShare(this, videoPath);

        drawerLayout.setDrawerListener(this);
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

        if (videoPath != null && !videoPath.isEmpty()) {
            initMediaPlayer(videoPath);
        } else {
            showError();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(navigatorView);
            return;
        }

        finish();

    }

    /**
     * Shows an alert dialog if an error occurs and returns to the previous activity
     */
    private void showError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                AlertDialog.THEME_HOLO_LIGHT);
        builder.setMessage(R.string.invalid_video)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        (ShareActivity.this).finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        showError();
        return true;
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
        //pauseVideo();
        releaseVideoView();
    }

    /**
     * Releases the media player and the video view
     */
    private void releaseVideoView() {
        videoView.stopPlayback();
        videoView.clearFocus();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
        releaseVideoView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @OnClick(R.id.button_navigate_drawer)
    public void navigationDrawerListener() {

        drawerLayout.openDrawer(navigatorView);

    }

    //@OnClick(R.id.share_button_play)
    public void playVideo() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            //buttonPlay.setVisibility(View.GONE);
            //updateSeekProgress();
        }
    }

    public void pauseVideo() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            //buttonPlay.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.share_button_share)
    public void shareVideo() {

        // Log.d(LOG_TAG, "shareClickListener");

        pauseVideo();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
    }


    /**
     * Use screen touches to toggle the video between playing and paused.
     */
//    @OnTouch(R.id.share_video_view)
//    public boolean onTouchEvent(MotionEvent ev) {
//        boolean result;
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            pauseVideo();
//            result = true;
//
//        } else {
//            result = false;
//        }
//        return result;
//    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(LOG_TAG, "onPrepared");
        mediaPlayer = mp;
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.start();
        mediaPlayer.seekTo(100);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Log.d(LOG_TAG, "error while preparing preview");
        }
        pauseVideo();
    }

    public void initMediaPlayer(final String videoPath) {
        mediaController = new MediaController(this);
        mediaController.setVisibility(View.VISIBLE);
        mediaController.setAnchorView(videoView);
        videoView.setVideoPath(videoPath);
        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(this);
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                buttonPlay.setVisibility(View.VISIBLE);
//            }
//
//        });

        videoView.requestFocus();
    }


    public void navigateToEdit() {

        Log.d(LOG_TAG, "navigateToEdit");

        Intent edit = new Intent(this, EditActivity.class);
        edit.putExtra("SHARE", false);
        startActivity(edit);
    }

    @OnClick(R.id.share_button_share)
    public void clickListener(View view) {
        sendButtonTracked(view.getId());
    }

    /**
     * Sends button clicks to Google Analytics
     *
     * @param id the identifier of the clicked button
     */
    private void sendButtonTracked(int id) {
        Log.d(LOG_TAG, "sendButtonTracked");
        String label;
        switch (id) {
            case R.id.share_button_share:
                label = "Share video";
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("ShareActivity")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getApplication().getBaseContext()).dispatchLocalHits();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

        buttonNavigateDrawer.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onDrawerClosed(View drawerView) {

        buttonNavigateDrawer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
