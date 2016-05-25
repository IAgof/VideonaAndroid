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
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.DuplicatePreviewPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.DuplicateView;
import com.videonasocialmedia.videona.presentation.views.customviews.AspectRatioVideoView;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class VideoDuplicateActivity extends VideonaActivity implements DuplicateView,
        SeekBar.OnSeekBarChangeListener {


    private static final String DUPLICATE_VIDEO_POSITION = "duplicate_video_position";
    protected Handler handler = new Handler();
    @Bind(R.id.video_duplicate_preview)
    AspectRatioVideoView preview;
    @Bind(R.id.button_duplicate_play_pause)
    ImageButton playButton;
    @Bind(R.id.seekbar_duplicate_preview)
    SeekBar videoSeekBar;
    @Bind(R.id.image_thumb_duplicate_video_left)
    ImageView imageThumbLeft;
    @Bind(R.id.image_thumb_duplicate_video_right)
    ImageView imageThumbRight;
    @Bind(R.id.textView_duplicate_num_increment)
    TextView textNumDuplicates;
    @Bind(R.id.button_duplicate_decrement_video)
    ImageButton decrementVideoButton;
    int videoIndexOnTrack;
    private DuplicatePreviewPresenter presenter;
    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private Video video;
    private String TAG = "VideoDuplicateActivity";
    private int currentPosition = 0;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };
    private int numDuplicateVideos = 2;
    private String NUM_DUPLICATE_VIDEOS = "num_duplicate_videos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_duplicate);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        presenter = new DuplicatePreviewPresenter(this);

        videoSeekBar.setProgress(0);
        videoSeekBar.setOnSeekBarChangeListener(this);
        mediaController = new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);

        restoreState(savedInstanceState);

        textNumDuplicates.setText("x" + numDuplicateVideos);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(DUPLICATE_VIDEO_POSITION, 0);
            numDuplicateVideos = savedInstanceState.getInt(NUM_DUPLICATE_VIDEOS, 2);
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
        presenter.onPause();
        releaseVideoView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.onResume();
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

        outState.putInt(DUPLICATE_VIDEO_POSITION, currentPosition);
        outState.putInt(NUM_DUPLICATE_VIDEOS, numDuplicateVideos);
        super.onSaveInstanceState(outState);

    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
    }

    @OnTouch(R.id.video_duplicate_preview)
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

    @OnClick(R.id.button_duplicate_play_pause)
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
                if (isEndOfVideo()) {
                    videoPlayer.pause();
                    playButton.setVisibility(View.VISIBLE);
                    videoSeekBar.setProgress(0);
                    videoPlayer.seekTo(video.getFileStartTime());
                    currentPosition = video.getFileStartTime();
                }
            }
            handler.postDelayed(updateTimeTask, 20);
        }
    }

    private boolean isEndOfVideo() {
        return videoSeekBar.getProgress() >= ( video.getFileStopTime() - video.getFileStartTime() );
    }

    @OnClick(R.id.button_duplicate_accept)
    public void onClickDuplicateAccept() {

        presenter.duplicateVideo(video, videoIndexOnTrack, numDuplicateVideos);
        finish();
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @OnClick(R.id.button_duplicate_cancel)
    public void onClickDuplicateCancel() {
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

            currentPosition = videoPlayer.getCurrentPosition();
            videoSeekBar.setProgress(progress);
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
    public void initDuplicateView(String path) {
        if (numDuplicateVideos > 2)
            decrementVideoButton.setVisibility(View.VISIBLE);
        else
            decrementVideoButton.setVisibility(View.GONE);
        showThumVideo(imageThumbLeft);
        showThumVideo(imageThumbRight);
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
        videoSeekBar.setMax(maxSeekBar);
        videoSeekBar.setProgress(0);
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

    private void showThumVideo(ImageView imageThumbLeft) {

        int microSecond = video.getFileStartTime() * 1000;
        BitmapPool bitmapPool = Glide.get(this).getBitmapPool();
        FileDescriptorBitmapDecoder decoder = new FileDescriptorBitmapDecoder(
                new VideoBitmapDecoder(microSecond),
                bitmapPool,
                DecodeFormat.PREFER_ARGB_8888);

        String path = video.getIconPath() != null
                ? video.getIconPath() : video.getMediaPath();
        Glide.with(this)
                .load(path)
                .asBitmap()
                .videoDecoder(decoder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .error(R.drawable.fragment_gallery_no_image)
                .into(imageThumbLeft);
    }

    @OnClick(R.id.button_duplicate_increment_video)
    public void onClickIncrementVideo() {
        numDuplicateVideos++;
        decrementVideoButton.setVisibility(View.VISIBLE);
        textNumDuplicates.setText("x" + numDuplicateVideos);
    }

    @OnClick(R.id.button_duplicate_decrement_video)
    public void onClickDecrementVideo() {
        numDuplicateVideos--;
        if (numDuplicateVideos <= 2)
            decrementVideoButton.setVisibility(View.GONE);
        textNumDuplicates.setText("x" + numDuplicateVideos);

    }

}
