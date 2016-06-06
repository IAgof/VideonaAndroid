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
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.SplitPreviewPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.SplitView;
import com.videonasocialmedia.videona.presentation.views.customviews.VideonaPlayer;
import com.videonasocialmedia.videona.presentation.views.listener.VideonaPlayerListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.TimeUtils;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoSplitActivity extends VideonaActivity implements SplitView,VideonaPlayerListener,
    SeekBar.OnSeekBarChangeListener {

    private String TAG = this.getClass().getName();
    private static final String SPLIT_POSITION = "split_position";
    @Bind(R.id.videona_player)
    VideonaPlayer videonaPlayer;
    @Bind(R.id.text_time_split)
    TextView timeTag;
    @Bind(R.id.seekBar_split)
    SeekBar splitSeekBar;
    int videoIndexOnTrack;
    private SplitPreviewPresenter presenter;
    private Video video;
    private int currentPosition = 0;

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

        presenter = new SplitPreviewPresenter(this);

        videonaPlayer.initVideoPreview(this);

        splitSeekBar.setProgress(0);
        splitSeekBar.setOnSeekBarChangeListener(this);
        timeTag.setText(TimeUtils.toFormattedTime(0));

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);

        restoreState(savedInstanceState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(SPLIT_POSITION, 0);
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

//    private void updateSeekBarProgress() {
//        if (videoPlayer != null) {
//            if (videoPlayer.isPlaying()) {
//                currentPosition = videoPlayer.getCurrentPosition() - video.getFileStartTime();
//                videoSeekBar.setProgress(currentPosition);
//                splitSeekBar.setProgress(currentPosition);
//                refreshTimeTag(currentPosition);
//                if (isEndOfVideo()) {
//                    videoPlayer.pause();
//                    playButton.setVisibility(View.VISIBLE);
//                    refreshTimeTag(0);
//                    videoSeekBar.setProgress(0);
//                    splitSeekBar.setProgress(0);
//                    videoPlayer.seekTo(video.getFileStartTime());
//                    currentPosition = video.getFileStartTime();
//                }
//            }
//            handler.postDelayed(updateTimeTask, 20);
//        }
//    }

    private void refreshTimeTag(int currentPosition) {

        timeTag.setText(TimeUtils.toFormattedTime(currentPosition));
    }

//    private boolean isEndOfVideo() {
//        return videoSeekBar.getProgress() >= ( video.getFileStopTime() - video.getFileStartTime() );
//    }

    @OnClick(R.id.seekBar_split)
    public void onClickSeekbarSplit() {
//        if (videoPlayer != null && videoPlayer.isPlaying()) {
//            pausePreview();
//            updateSeekBarProgress();
//        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            videonaPlayer.seekTo(progress + video.getFileStartTime());
            videonaPlayer.pause();

            currentPosition = videonaPlayer.getCurrentPosition() - video.getFileStartTime();
            splitSeekBar.setProgress(progress);
            timeTag.setText(TimeUtils.toFormattedTime(progress));
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
        splitSeekBar.setProgress(currentPosition);
        timeTag.setText(TimeUtils.toFormattedTime(currentPosition));
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
        videonaPlayer.initPreview(currentPosition);
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
    }

}
