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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.TrimPreviewPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.TrimView;
import com.videonasocialmedia.videona.presentation.views.customviews.AspectRatioVideoView;
import com.videonasocialmedia.videona.presentation.views.customviews.TrimRangeSeekBarView;
import com.videonasocialmedia.videona.presentation.views.listener.OnRangeSeekBarChangeListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnTrimConfirmListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.TimeUtils;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class VideoTrimActivity extends VideonaActivity implements TrimView,
        SeekBar.OnSeekBarChangeListener, OnRangeSeekBarChangeListener {

    protected Handler handler = new Handler();
    @Bind(R.id.video_trim_preview)
    AspectRatioVideoView preview;
    @Bind(R.id.button_trim_play_pause)
    ImageButton playButton;
    @Bind(R.id.seekbar_trim_preview)
    SeekBar seekBar;
    @Bind(R.id.text_start_trim)
    TextView startTimeTag;
    @Bind(R.id.text_end_trim)
    TextView stopTimeTag;
    @Bind(R.id.text_time_trim)
    TextView durationTag;
    @Bind(R.id.trim_rangeSeekBar)
    TrimRangeSeekBarView rangeSeekBar;
    int videoIndexOnTrack;
    private TrimPreviewPresenter presenter;
    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private OnTrimConfirmListener onTrimConfirmListener;
    private Video video;
    private int videoDuration = 1;
    private int startTimeMs = 0;
    private int finishTimeMs = 100;
    private boolean afterTrimming = false;
    private String TAG = "VideoTrimActivity";
    private double timeCorrector = 1;
    private double seekBarMin = 0;
    private double seekBarMax = 1;
    private int currentPosition = 0;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };
    private String VIDEO_POSITION = "video_position";
    private String TRIMMINGBAR_MIN = "trimming_bar_min";
    private String TRIMMINGBAR_MAX = "trimming_bar_max";
    private String START_TIME_TAG = "start_time_tag";
    private String STOP_TIME_TAG = "stop_time_tag";
    private boolean isInstanceState;

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

        presenter = new TrimPreviewPresenter(this);

        rangeSeekBar.setOnRangeListener(this);

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);
        mediaController = new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);

        if (savedInstanceState != null) {

            currentPosition = savedInstanceState.getInt(VIDEO_POSITION, 0);
            seekBarMin = savedInstanceState.getDouble(TRIMMINGBAR_MIN);
            seekBarMax = savedInstanceState.getDouble(TRIMMINGBAR_MAX);
            startTimeMs = savedInstanceState.getInt(START_TIME_TAG);
            finishTimeMs = savedInstanceState.getInt(STOP_TIME_TAG);

            isInstanceState = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseVideoView();
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

    private void releaseVideoView() {
        preview.stopPlayback();
        preview.clearFocus();
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
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
        finish();
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(VIDEO_POSITION, currentPosition);
        outState.putDouble(TRIMMINGBAR_MIN, seekBarMin);
        outState.putDouble(TRIMMINGBAR_MAX, seekBarMax);
        outState.putInt(START_TIME_TAG, startTimeMs);
        outState.putInt(STOP_TIME_TAG, finishTimeMs);
        super.onSaveInstanceState(outState);

    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
    }

    @OnTouch(R.id.video_trim_preview)
    public boolean onTouchPreview(MotionEvent event) {
        boolean result;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onClickPlayPausePreview();
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @OnClick(R.id.button_trim_play_pause)
    public void onClickPlayPausePreview() {
        if (videoPlayer != null) {
            if (videoPlayer.isPlaying()) {
                pausePreview();
            } else {
                playPreview();
            }
            updateSeekBarProgress();
        }
    }

    private void updateSeekBarProgress() {
        if (videoPlayer != null) {
            if (videoPlayer.isPlaying()) {
                currentPosition = videoPlayer.getCurrentPosition() - video.getFileStartTime();
                seekBar.setProgress(currentPosition);

                if (isEndOfVideo()) {
                    videoPlayer.pause();
                    playButton.setVisibility(View.VISIBLE);

                    seekBar.setProgress(0);
                    videoPlayer.seekTo(video.getFileStartTime());
                }
            }
            handler.postDelayed(updateTimeTask, 20);
        }
    }

    private boolean isEndOfVideo() {
        return seekBar.getProgress() >= videoDuration;
    }

    @OnClick(R.id.button_trim_accept)
    public void onClickTrimAccept() {
        presenter.modifyVideoStartTime(startTimeMs);
        presenter.modifyVideoFinishTime(finishTimeMs);
        finish();
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @OnClick(R.id.button_trim_cancel)
    public void onClickTrimCancel() {
        finish();
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (videoPlayer.isPlaying()) {
                videoPlayer.seekTo(progress + video.getFileStartTime());
            } else {
                videoPlayer.seekTo(progress + video.getFileStartTime());
                videoPlayer.pause();
            }
            afterTrimming = false;

            currentPosition = videoPlayer.getCurrentPosition() - video.getFileStartTime();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void showTrimBar(int videoFileDuration, int leftMarkerPosition, int rightMarkerPosition) {

        if (isInstanceState) {

            initTrimmingBars();

            return;
        }

        startTimeMs = leftMarkerPosition;
        finishTimeMs = rightMarkerPosition;

        videoDuration = videoFileDuration;

        timeCorrector = videoFileDuration / 100;

        double rangeSeekBarMin = (double) leftMarkerPosition / videoFileDuration;
        double rangeSeekBarMax = (double) rightMarkerPosition / videoFileDuration;

        rangeSeekBar.setInitializedPosition(rangeSeekBarMin, rangeSeekBarMax);
    }

    @Override
    public void refreshDurationTag(int duration) {

    }

    @Override
    public void refreshStartTimeTag(int startTime) {

    }

    @Override
    public void refreshStopTimeTag(int stopTime) {

    }

    @Override
    public void playPreview() {
        if (videoPlayer != null) {
            if (afterTrimming) {
                videoPlayer.seekTo((int) Math.round(rangeSeekBar.getMinPositionValue()));
                afterTrimming = false;
            }
            videoPlayer.start();
            playButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void pausePreview() {
        if (videoPlayer != null && videoPlayer.isPlaying())
            videoPlayer.pause();
        playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void seekTo(int timeInMsec) {
        if (videoPlayer != null) {
            if (video.getIsSplit()) {
                timeInMsec = timeInMsec + video.getFileStartTime();
            }
            videoPlayer.seekTo(timeInMsec);
        }
    }

    @Override
    public void showPreview(List<Video> movieList) {
        video = movieList.get(0);
        if (video.getIsSplit()) {
            videoDuration = video.getFileStopTime() - video.getFileStartTime();
        } else {
            videoDuration = video.getFileDuration();
        }
        timeCorrector = videoDuration / 100;
        seekBar.setMax(videoDuration);
        initVideoPlayer(currentPosition, video.getMediaPath());

    }

    @Override
    public void showError(String message) {

    }

    private void initVideoPlayer(final int position, final String videoPath) {
        if (videoPlayer == null) {
            preview.setVideoPath(videoPath);
            preview.setMediaController(mediaController);
            preview.canSeekBackward();
            preview.canSeekForward();
            preview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoPlayer = mp;
                    seekBar.setProgress(position);
                    videoPlayer.setVolume(0.5f, 0.5f);
                    videoPlayer.setLooping(false);
                    videoPlayer.start();
                    videoPlayer.seekTo(100 + position + video.getFileStartTime());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    videoPlayer.pause();
                    pausePreview();
                    updateSeekBarProgress();
                }
            });
            preview.requestFocus();

        } else {
            preview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setProgress(position);
                    videoPlayer.setVolume(0.5f, 0.5f);
                    videoPlayer.setLooping(false);
                    updateSeekBarProgress();
                }
            });

            try {
                videoPlayer.reset();
                videoPlayer.setDataSource(videoPath);
                videoPlayer.prepare();
                videoPlayer.start();
            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void initTrimmingBars() {

        startTimeTag.setText(TimeUtils.toFormattedTime(startTimeMs));
        stopTimeTag.setText(TimeUtils.toFormattedTime(finishTimeMs));
        durationTag.setText(TimeUtils.toFormattedTime(( finishTimeMs - startTimeMs )));

        rangeSeekBar.setInitializedPosition(seekBarMin / 100, seekBarMax / 100);

    }

    @Override
    public void setRangeChangeListener(View view, double minPosition, double maxPosition) {
        Log.d(TAG, " setRangeChangeListener " + minPosition + " - " + maxPosition);

        pausePreview();

        startTimeMs = (int) ( minPosition * timeCorrector );
        finishTimeMs = (int) ( maxPosition * timeCorrector );

        startTimeTag.setText(TimeUtils.toFormattedTime(startTimeMs));
        stopTimeTag.setText(TimeUtils.toFormattedTime(finishTimeMs));
        durationTag.setText(TimeUtils.toFormattedTime(( finishTimeMs - startTimeMs )));

        if (seekBarMin != minPosition) {
            seekBarMin = minPosition;
            seekBarMax = maxPosition;
            seekBar.setProgress(startTimeMs);
            seekTo(startTimeMs + video.getFileStartTime());
            currentPosition = videoPlayer.getCurrentPosition() - video.getFileStartTime();
            return;
        }

        if (seekBarMax != maxPosition) {
            seekBarMin = minPosition;
            seekBarMax = maxPosition;
            seekBar.setProgress(finishTimeMs);
            seekTo(finishTimeMs + video.getFileStartTime());
            currentPosition = videoPlayer.getCurrentPosition() - video.getFileStartTime();
            return;
        }
    }

}
