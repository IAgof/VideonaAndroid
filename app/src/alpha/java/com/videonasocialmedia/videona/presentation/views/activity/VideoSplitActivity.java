package com.videonasocialmedia.videona.presentation.views.activity;
/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.SplitPreviewPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.SplitView;
import com.videonasocialmedia.videona.presentation.views.customviews.VideonaPlayer;
import com.videonasocialmedia.videona.presentation.views.listener.VideonaPlayerListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.TimeUtils;
import com.videonasocialmedia.videona.utils.UserEventTracker;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class VideoSplitActivity extends VideonaActivity implements SplitView, VideonaPlayerListener,
    SeekBar.OnSeekBarChangeListener {

    private static final String SPLIT_POSITION = "split_position";
    private static final String SPLIT_VIDEO_POSITION = "split_video_position";
    @Bind(R.id.videona_player)
    VideonaPlayer videonaPlayer;
    @Bind(R.id.text_time_split)
    TextView timeTag;
    @Bind(R.id.seekBar_split)
    SeekBar splitSeekBar;
    int videoIndexOnTrack;
    private SplitPreviewPresenter presenter;
    private Video video;
    private int currentSplitPosition = 0;
    private int currentVideoPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_split);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        UserEventTracker userEventTracker = UserEventTracker.getInstance(MixpanelAPI.getInstance(this, BuildConfig.MIXPANEL_TOKEN));
        presenter = new SplitPreviewPresenter(this, userEventTracker);

        splitSeekBar.setProgress(0);
        splitSeekBar.setOnSeekBarChangeListener(this);
        timeTag.setText(TimeUtils.toFormattedTime(0));

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);

        restoreState(savedInstanceState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentSplitPosition = savedInstanceState.getInt(SPLIT_POSITION, 0);
            currentVideoPosition = savedInstanceState.getInt(SPLIT_VIDEO_POSITION, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videonaPlayer.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videonaPlayer.pause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadProjectVideo(videoIndexOnTrack);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings_edit_options:
                navigateTo(SettingsActivity.class);
                return true;
            case R.id.action_settings_edit_gallery:
                navigateTo(GalleryActivity.class);
                return true;
            case R.id.action_settings_edit_tutorial:
                //navigateTo(TutorialActivity.class);
                return true;
            default:

        }

        return super.onOptionsItemSelected(item);
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        if (cls == GalleryActivity.class) {
            intent.putExtra("SHARE", false);
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SPLIT_VIDEO_POSITION, videonaPlayer.getCurrentPosition()  );
        outState.putInt(SPLIT_POSITION, currentSplitPosition);
        super.onSaveInstanceState(outState);
    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
    }

    @OnClick(R.id.button_split_accept)
    public void onClickSplitAccept() {

        presenter.splitVideo(video, videoIndexOnTrack, currentSplitPosition);
        finish();
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @OnClick(R.id.button_split_cancel)
    public void onClickSplitCancel() {
        finish();
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    private void refreshTimeTag(int currentPosition) {

        timeTag.setText(TimeUtils.toFormattedTime(currentPosition));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            currentSplitPosition = progress;
            splitSeekBar.setProgress(progress);
            refreshTimeTag(currentSplitPosition);
            videonaPlayer.seekTo(video.getFileStartTime() + progress);
            videonaPlayer.setSeekBarProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void initSplitView(int maxSeekBar) {
        splitSeekBar.setMax(maxSeekBar);
        splitSeekBar.setProgress(currentSplitPosition);
        refreshTimeTag(currentSplitPosition);
    }


    @Override
    public void playPreview() {
        videonaPlayer.playPreview();
    }

    @Override
    public void pausePreview() {
        videonaPlayer.pausePreview();
    }

    @Override
    public void showPreview(List<Video> movieList) {
        video = movieList.get(0);
        videonaPlayer.initPreviewLists(movieList);
        videonaPlayer.initPreview(currentVideoPosition);
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
    }

}
