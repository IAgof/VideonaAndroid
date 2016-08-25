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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.TrimPreviewPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.TrimView;
import com.videonasocialmedia.videona.presentation.views.customviews.TrimRangeSeekBarView;
import com.videonasocialmedia.videona.presentation.views.customviews.VideonaPlayer;
import com.videonasocialmedia.videona.presentation.views.listener.OnRangeSeekBarChangeListener;
import com.videonasocialmedia.videona.presentation.views.listener.VideonaPlayerListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.TimeUtils;
import com.videonasocialmedia.videona.utils.UserEventTracker;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoTrimActivity extends VideonaActivity implements TrimView,
        OnRangeSeekBarChangeListener, VideonaPlayerListener {

    @Bind(R.id.text_start_trim)
    TextView startTimeTag;
    @Bind(R.id.text_end_trim)
    TextView stopTimeTag;
    @Bind(R.id.text_time_trim)
    TextView durationTag;
    @Bind(R.id.trim_rangeSeekBar)
    TrimRangeSeekBarView trimmingRangeSeekBar;
    @Bind(R.id.videona_player)
    VideonaPlayer videonaPlayer;

    int videoIndexOnTrack;
    private TrimPreviewPresenter presenter;
    private Video video;
    private int videoDuration = 1;
    private int startTimeMs = 0;
    private int finishTimeMs = 100;
    private String TAG = "VideoTrimActivity";
    private int timeCorrector = 1;
    private double seekBarMin = 0;
    private double seekBarMax = 1;
    private int currentPosition = 0;
    private String VIDEO_POSITION = "video_position";
    private String START_TIME_TAG = "start_time_tag";
    private String STOP_TIME_TAG = "stop_time_tag";
    private boolean activityStateHasChanged = false;
    private boolean trimmingBarsHaveBeenInitialized = false;
    private boolean leftTimeTagIsUpdated = false;
    private boolean rightTimeTagIsUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trim);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        UserEventTracker userEventTracker = UserEventTracker.getInstance(MixpanelAPI.getInstance(this, BuildConfig.MIXPANEL_TOKEN));
        presenter = new TrimPreviewPresenter(this, userEventTracker);
        trimmingRangeSeekBar.setOnRangeListener(this);
        videonaPlayer.initVideoPreview(this);

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
        restoreState(savedInstanceState);
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

        presenter.init(videoIndexOnTrack);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(VIDEO_POSITION, 0);
            startTimeMs = savedInstanceState.getInt(START_TIME_TAG);
            finishTimeMs = savedInstanceState.getInt(STOP_TIME_TAG);

            activityStateHasChanged = true;
        }
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
        startActivity(new Intent(getApplicationContext(), cls));
    }

    @Override
    public void onBackPressed() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
        finish();
    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(VIDEO_POSITION, videonaPlayer.getCurrentPosition());
        outState.putInt(START_TIME_TAG, startTimeMs);
        outState.putInt(STOP_TIME_TAG, finishTimeMs);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.button_trim_accept)
    public void onClickTrimAccept() {
        presenter.setTrim(startTimeMs, finishTimeMs);
        navigateTo(EditActivity.class, videoIndexOnTrack);
        finish();
    }

    @OnClick(R.id.button_trim_cancel)
    public void onClickTrimCancel() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    public void showTrimBar(int videoStartTime, int videoStopTime, int videoFileDuration) {
        if (activityStateHasChanged) {
            initTrimmingBars();
            activityStateHasChanged = false;
            return;
        }

        startTimeMs = videoStartTime;
        finishTimeMs = videoStopTime;

        double rangeSeekBarMin = (double) videoStartTime / videoFileDuration;
        double rangeSeekBarMax = (double) videoStopTime / videoFileDuration;
        trimmingRangeSeekBar.setInitializedPosition(rangeSeekBarMin,rangeSeekBarMax);
    }

    @Override
    public void refreshDurationTag(int duration) {
        durationTag.setText(TimeUtils.toFormattedTime(duration));
    }

    @Override
    public void refreshStartTimeTag(int startTime) {
        startTimeTag.setText(TimeUtils.toFormattedTime(startTime));
    }

    @Override
    public void refreshStopTimeTag(int stopTime) {
        stopTimeTag.setText(TimeUtils.toFormattedTime(stopTime));
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
    public void seekTo(int timeInMsec) {
        videonaPlayer.seekTo(timeInMsec);
    }

    @Override
    public void showPreview(List<Video> movieList) {
        video = movieList.get(0);
        // TODO(jliarte): check this workarround.
        Video untrimmedVideo = new Video(video);
        untrimmedVideo.setStartTime(0);
        untrimmedVideo.setStopTime(video.getFileDuration());
        List<Video> untrimedMovieList = new LinkedList<>();
        untrimedMovieList.add(untrimmedVideo);
        // end of workarround.

        videoDuration = video.getFileDuration();
        timeCorrector = videoDuration / 100;

        videonaPlayer.initPreviewLists(untrimedMovieList);
        videonaPlayer.initPreview(currentPosition);
    }

    @Override
    public void showError(String message) {
    }

    private void initTrimmingBars() {
        updateTrimingTextTags();
        trimmingBarsHaveBeenInitialized = true;
        seekBarMin = (double) startTimeMs / videoDuration;
        seekBarMax = (double) finishTimeMs / videoDuration;
        trimmingRangeSeekBar.setInitializedPosition(seekBarMin, seekBarMax);
    }

    private void updateTrimingTextTags() {
        startTimeTag.setText(TimeUtils.toFormattedTime(startTimeMs));
        stopTimeTag.setText(TimeUtils.toFormattedTime(finishTimeMs));

        int duration = ( ( finishTimeMs / 1000 ) - ( startTimeMs / 1000 ) ) * 1000;
        durationTag.setText(TimeUtils.toFormattedTime(duration));
    }

    @Override
    public void setRangeChangeListener(View view, double minPosition, double maxPosition) {
        Log.d(TAG, " setRangeChangeListener " + minPosition + " - " + maxPosition);
        videonaPlayer.pausePreview();

        int startTime = (int) ( minPosition * timeCorrector );
        int finishTime = (int) ( maxPosition * timeCorrector );

        if (seekBarMin != minPosition) {
            seekBarMin = minPosition;
            seekBarMax = maxPosition;
            videonaPlayer.seekTo(startTime);
            if(!trimmingBarsHaveBeenInitialized) {
                startTimeMs = startTime;
                updateTrimingTextTags();
            }
            return;
        }

        if (seekBarMax != maxPosition) {
            seekBarMin = minPosition;
            seekBarMax = maxPosition;
            videonaPlayer.seekTo(finishTime);
            if(!trimmingBarsHaveBeenInitialized) {
                finishTimeMs = finishTime;
                updateTrimingTextTags();
            }
            return;
        }
    }

    @Override
    public void setUpdateFinishTimeTag() {
        rightTimeTagIsUpdated = true;
        setUpdateTimeTags();
    }

    @Override
    public void setUpdateStartTimeTag() {
        leftTimeTagIsUpdated = true;
        setUpdateTimeTags();
    }

    public void setUpdateTimeTags() {
        Log.d(TAG, " setUpdateTimeTags ");
        if(leftTimeTagIsUpdated && rightTimeTagIsUpdated)
            trimmingBarsHaveBeenInitialized = false;
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
    }
}
