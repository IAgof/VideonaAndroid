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
import com.videonasocialmedia.videona.presentation.mvp.views.PreviewView;
import com.videonasocialmedia.videona.presentation.mvp.views.TrimView;
import com.videonasocialmedia.videona.presentation.views.customviews.AspectRatioVideoView;
import com.videonasocialmedia.videona.presentation.views.customviews.OnRangeChangeListener;
import com.videonasocialmedia.videona.presentation.views.customviews.RangeSeekBar;
import com.videonasocialmedia.videona.presentation.views.customviews.TrimRangeSeekBar;
import com.videonasocialmedia.videona.presentation.views.listener.OnTrimConfirmListener;
import com.videonasocialmedia.videona.utils.TimeUtils;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class VideoTrimActivity extends VideonaActivity implements PreviewView, TrimView,
        SeekBar.OnSeekBarChangeListener, OnRangeChangeListener {

    @Bind(R.id.video_trim_preview)
    AspectRatioVideoView preview;
    @Bind(R.id.button_trim_play_pause)
    ImageButton playButton;
    @Bind(R.id.seekbar_trim_preview)
    SeekBar seekBar;

    @Bind(R.id.trim_text_start_trim)
    TextView startTimeTag;
    @Bind(R.id.trim_text_end_trim)
    TextView stopTimeTag;
    @Bind(R.id.trim_text_time_trim)
    TextView durationTag;

    @Bind(R.id.rangeSeekBar)
    TrimRangeSeekBar rangeSeekBar;

    RangeSeekBar<Double> trimBar;
    int videoIndexOnTrack;
    private TrimPreviewPresenter presenter;

    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private OnTrimConfirmListener onTrimConfirmListener;
    private Video video;
    protected Handler handler = new Handler();
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };
    private int startTimeMs = 0;
    private int finishTimeMs = 0;
    private boolean afterTrimming = false;
    private String TAG = "VideoTrimActivity";
    private double timeCorrector;
    private double seekBarMin;
    private double seekBarMax;

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

        presenter = new TrimPreviewPresenter(this, this);
        rangeSeekBar.setOnRangeListener(this);

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);
        mediaController = new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);
        videoIndexOnTrack = 0;
        //videoIndexOnTrack = getArguments().getInt("VIDEO_INDEX", 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
        releaseVideoView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.init(videoIndexOnTrack);
        presenter.onResume();

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
        getMenuInflater().inflate(R.menu.menu_edit_room, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent record = new Intent(this, RecordActivity.class);
        startActivity(record);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
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
    public void onClickPlayPausePreview(){
        if (videoPlayer != null) {
            if (videoPlayer.isPlaying()) {
                pausePreview();
            } else {
                playPreview();
            }
            updateSeekBarProgress();
        }
    }

    @OnClick(R.id.button_trim_accept)
    public void onClickTrimAccept(){
        presenter.modifyVideoStartTime(startTimeMs);
        presenter.modifyVideoFinishTime(finishTimeMs);
        finish();
    }

    @OnClick(R.id.button_trim_cancel)
    public void onClickTrimCancel(){
        finish();
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
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void playPreview() {
        if (videoPlayer != null) {
            if (afterTrimming) {
                videoPlayer.seekTo((int) Math.round(trimBar.getSelectedMinValue()));
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
        videoPlayer.seekTo(timeInMsec);
    }

    @Override
    public void updateVideoList() {

    }

    @Override
    public void showPreview(List<Video> movieList) {
        video = movieList.get(0);
        seekBar.setMax(video.getDuration());
        initVideoPlayer(video.getMediaPath());
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void updateSeekBarDuration(int projectDuration) {

    }

    @Override
    public void updateSeekBarSize() {
        seekBar.setProgress(0);
        seekBar.setMax(video.getDuration());
    }

    @Override
    public void showTrimBar(int videoDuration, int leftMarkerPosition, int rightMarkerPosition) {

        startTimeMs = leftMarkerPosition;
        finishTimeMs = rightMarkerPosition;

        timeCorrector = videoDuration / 100;

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
    public void setRangeChangeListener(View view, double minPosition, double maxPosition) {
        Log.d(TAG," setRangeChangeListener " + minPosition + " - " + maxPosition);

        startTimeMs = (int) (minPosition*timeCorrector);
        finishTimeMs = (int) (maxPosition*timeCorrector);

        startTimeTag.setText(TimeUtils.toFormattedTime(startTimeMs));
        stopTimeTag.setText(TimeUtils.toFormattedTime(finishTimeMs));
        durationTag.setText(TimeUtils.toFormattedTime((finishTimeMs - startTimeMs)));



        if(seekBarMin != minPosition){
            seekTo(startTimeMs);
            seekBarMin = minPosition;
            seekBarMax = maxPosition;
            seekBar.setProgress(startTimeMs);
            return;
        }

        if(seekBarMax != maxPosition){
            seekTo(finishTimeMs);
            seekBarMin = minPosition;
            seekBarMax = maxPosition;
            seekBar.setProgress(finishTimeMs);
            return;
        }
    }



    private void releaseVideoView() {
        preview.stopPlayback();
        preview.clearFocus();
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
    }

    private void updateSeekBarProgress() {
        if (videoPlayer != null) {
            if (videoPlayer.isPlaying()) {
                seekBar.setProgress(videoPlayer.getCurrentPosition() - video.getFileStartTime());
                refreshStartTimeTag(videoPlayer.getCurrentPosition());
                if (isEndOfVideo()) {
                    videoPlayer.pause();
                    playButton.setVisibility(View.VISIBLE);
                    refreshStartTimeTag(video.getFileStartTime());
                    seekBar.setProgress(0);
                    videoPlayer.seekTo(video.getFileStartTime());
                }
            }
            handler.postDelayed(updateTimeTask, 20);
        }
    }

    private boolean isEndOfVideo() {
        return seekBar.getProgress() >= video.getDuration();
    }

    private void initVideoPlayer(final String videoPath) {
        if (videoPlayer == null) {
            preview.setVideoPath(videoPath);
            preview.setMediaController(mediaController);
            preview.canSeekBackward();
            preview.canSeekForward();
            preview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoPlayer = mp;
                    seekBar.setProgress(videoPlayer.getCurrentPosition() - video.getFileStartTime());
                    videoPlayer.setVolume(0.5f, 0.5f);
                    videoPlayer.setLooping(false);
                    videoPlayer.start();
                    videoPlayer.seekTo(100 + video.getFileStartTime());
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
                    seekBar.setProgress(videoPlayer.getCurrentPosition() - video.getFileStartTime());
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

}
