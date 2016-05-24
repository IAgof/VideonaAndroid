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
import com.videonasocialmedia.videona.presentation.mvp.presenters.SplitPreviewPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.SplitView;
import com.videonasocialmedia.videona.presentation.views.customviews.AspectRatioVideoView;
import com.videonasocialmedia.videona.presentation.views.listener.OnSplitConfirmListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.TimeUtils;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class VideoSplitActivity extends VideonaActivity implements SplitView,
        SeekBar.OnSeekBarChangeListener {

    private static final String SPLIT_POSITION = "split_position";
    protected Handler handler = new Handler();
    @Bind(R.id.video_split_preview)
    AspectRatioVideoView preview;
    @Bind(R.id.button_split_play_pause)
    ImageButton playButton;
    @Bind(R.id.seekbar_split_preview)
    SeekBar videoSeekBar;
    @Bind(R.id.text_time_split)
    TextView timeTag;
    @Bind(R.id.seekBar_split)
    SeekBar splitSeekBar;
    int videoIndexOnTrack;
    private SplitPreviewPresenter presenter;
    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private OnSplitConfirmListener onSplitConfirmListener;
    private Video video;
    private boolean afterSplitting = false;
    private String TAG = "VideoSplitActivity";
    private int currentPosition = 0;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };
    private boolean isInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_split);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        presenter = new SplitPreviewPresenter(this);

        splitSeekBar.setProgress(0);
        splitSeekBar.setOnSeekBarChangeListener(this);
        timeTag.setText(TimeUtils.toFormattedTime(0));
        videoSeekBar.setProgress(0);
        videoSeekBar.setOnSeekBarChangeListener(this);
        mediaController = new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(SPLIT_POSITION, 0);
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

        outState.putInt(SPLIT_POSITION, currentPosition);
        super.onSaveInstanceState(outState);

    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
    }

    @OnTouch(R.id.video_split_preview)
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

    @OnClick(R.id.button_split_play_pause)
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
                videoSeekBar.setProgress(currentPosition);
                splitSeekBar.setProgress(currentPosition);
                refreshTimeTag(currentPosition);
                if (isEndOfVideo()) {
                    videoPlayer.pause();
                    playButton.setVisibility(View.VISIBLE);
                    refreshTimeTag(0);
                    videoSeekBar.setProgress(0);
                    splitSeekBar.setProgress(0);
                    videoPlayer.seekTo(video.getFileStartTime());
                }
            }
            handler.postDelayed(updateTimeTask, 20);
        }
    }

    private void refreshTimeTag(int currentPosition) {

        timeTag.setText(TimeUtils.toFormattedTime(currentPosition));
    }

    private boolean isEndOfVideo() {
        return videoSeekBar.getProgress() >= ( video.getFileStopTime() - video.getFileStartTime() );
    }

    @OnClick(R.id.seekBar_split)
    public void onClickSeekbarSplit() {
//        onClickPlayPausePreview();
        if (videoPlayer != null && videoPlayer.isPlaying()) {
            pausePreview();
            updateSeekBarProgress();
        }
    }

    @OnClick(R.id.button_split_accept)
    public void onClickSplitAccept() {

        presenter.splitVideo(video, videoIndexOnTrack, currentPosition);
        finish();
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @OnClick(R.id.button_split_cancel)
    public void onClickSplitCancel() {
        finish();
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {

            videoPlayer.seekTo(progress + video.getFileStartTime());
            videoPlayer.pause();

            playButton.setVisibility(View.VISIBLE);

            currentPosition = videoPlayer.getCurrentPosition() - video.getFileStartTime();
            videoSeekBar.setProgress(progress);
            splitSeekBar.setProgress(progress);
            timeTag.setText(TimeUtils.toFormattedTime(progress));

            afterSplitting = false;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void seekTo(int timeInMsec) {
        if (videoPlayer != null)
            videoPlayer.seekTo(timeInMsec);
    }

    @Override
    public void initSplitView(int maxSeekBar) {

        splitSeekBar.setMax(maxSeekBar);

        if (isInstanceState) {
            initSplitBar();
        }

    }

    private void initSplitBar() {

        videoSeekBar.setProgress(currentPosition);
        splitSeekBar.setProgress(currentPosition);
        timeTag.setText(TimeUtils.toFormattedTime(currentPosition));
        if (videoPlayer != null) {
            videoPlayer.seekTo(currentPosition + video.getFileStartTime());
            videoPlayer.pause();
        }
    }

    @Override
    public void playPreview() {
        if (videoPlayer != null) {
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
    public void showPreview(List<Video> movieList) {
        video = movieList.get(0);
        int maxSeekBar = ( video.getFileStopTime() - video.getFileStartTime() );

        videoSeekBar.setProgress(0);
        videoSeekBar.setMax(maxSeekBar);

        splitSeekBar.setMax(maxSeekBar);
        splitSeekBar.setProgress(0);

        //currentPosition = video.getFileStartTime();
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
                    videoSeekBar.setProgress(position);
                    splitSeekBar.setProgress(position);
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
                    videoSeekBar.setProgress(position);
                    splitSeekBar.setProgress(position);
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
