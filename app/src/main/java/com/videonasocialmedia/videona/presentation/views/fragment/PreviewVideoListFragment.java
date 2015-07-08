/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas Abascal
 * Verónica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.PreviewPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.PreviewView;
import com.videonasocialmedia.videona.utils.TimeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * This class is used to show the right panel of the audio fx menu
 */
public class PreviewVideoListFragment extends Fragment implements PreviewView, SeekBar.OnSeekBarChangeListener {

    @InjectView(R.id.edit_preview_player)
    VideoView preview;
    @InjectView(R.id.edit_button_play)
    ImageButton playButton;
    @InjectView(R.id.edit_seek_bar)
    SeekBar seekBar;
    @InjectView(R.id.edit_text_start_trim)
    TextView startTimeTag;
    @InjectView(R.id.edit_text_end_trim)
    TextView stopTimeTag;
    @InjectView(R.id.edit_text_time_trim)
    TextView durationTag;



    protected Handler handler = new Handler();
    private PreviewPresenter previewPresenter;
    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };
    private int projectDuration = 0;
    private ArrayList<String> movieList;
    private ArrayList<Integer> videoTimeInProject;
    private int videoToPlay = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        ButterKnife.inject(this, view);

        previewPresenter = new PreviewPresenter(this);

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);

        mediaController = new MediaController(getActivity());
        mediaController.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateVideoList();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseVideoView();
    }

    @OnTouch(R.id.edit_preview_player)
    public boolean onTouchPreview(MotionEvent event) {
        boolean result;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            playPausePreview();
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @OnClick(R.id.edit_button_play)
    public void playPausePreview() {
        if (videoPlayer.isPlaying()) {
            pause();
        } else {
            play();
        }
        updateSeekBarProgress();
    }

    @Override
    public void play() {
        if (videoPlayer != null) {
            videoPlayer.start();
            /*
            if (musicPlayer != null) {
                playMusicSyncedWithVideo();
            }
            */
            playButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void pause() {
        if (videoPlayer != null && videoPlayer.isPlaying())
            videoPlayer.pause();
        /*
        if (musicPlayer != null && musicPlayer.isPlaying())
            musicPlayer.pause();
            */
        playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void seekTo(int timeInSec) {
        videoPlayer.seekTo(timeInSec);
    }

    @Override
    public void updateVideoList() {
        previewPresenter.update();
    }

    @Override
    public void showPreview(List<Video> videoList) {
        //movieStack = new MovieStack(videoList.size());
        movieList = new ArrayList<>();
        videoTimeInProject = new ArrayList<>();
        for (Video video : videoList) {
            videoTimeInProject.add(projectDuration);
            projectDuration = projectDuration + video.getDuration();
            movieList.add(video.getMediaPath());
        }
        showTimeTags(projectDuration);
        seekBar.setMax(projectDuration);

        initVideoPlayer(movieList.get(videoToPlay));
    }

    private void showTimeTags(int duration) {
        refreshStartTimeTag(0);
        refreshDurationTag(duration / 2);
        refreshStopTimeTag(duration);
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
                    seekBar.setProgress(videoPlayer.getCurrentPosition());
                    videoPlayer.setVolume(0.5f, 0.5f);
                    videoPlayer.setLooping(false);
                    videoPlayer.start();
                    videoPlayer.seekTo(100);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    videoPlayer.pause();

                    //editPresenter.prepareMusicPreview();
                    pause();
                    updateSeekBarProgress();
                }
            });
            preview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoToPlay++;
                    if(videoToPlay < movieList.size()) {
                        initVideoPlayer(movieList.get(videoToPlay));
                    }

                    if(videoToPlay == movieList.size()) {
                        playButton.setVisibility(View.VISIBLE);
                        releaseVideoView();
                        videoToPlay = 0;
                        initVideoPlayer(movieList.get(videoToPlay));
                    }

                    /*
                    if (musicPlayer != null && musicPlayer.isPlaying()) {
                        musicPlayer.pause();
                    }
                    */
                    //updateSeekBarProgress();
                }
            });

            preview.requestFocus();

        } else {

            preview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setProgress(videoPlayer.getCurrentPosition());
                    videoPlayer.setVolume(0.5f, 0.5f);
                    videoPlayer.setLooping(false);

                    //editPresenter.prepareMusicPreview();

                    updateSeekBarProgress();
                }
            });

            try {
                videoPlayer.reset();
                videoPlayer.setDataSource(videoPath);
                videoPlayer.prepare();
                videoPlayer.start();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //updateSeekBarProgress();
        }
    }

    private void playNextVideo(int videoPosition, int instantToStart) {

    }

    @Override
    public void showError(String message) {

    }

    /**
     * Listener seekBar, videoPlayer
     *
     * @param seekBar  the seekBar of the event
     * @param progress the new progress of the seekBar
     * @param fromUser true if the event was caused by an action from the user. False otherwise
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            // TODO calcular aquí el tiempo del proyecto y el vídeo correspondiente
            videoPlayer.seekTo(progress);
            /*
            if (musicPlayer != null)
                syncMusicWithVideo(progress);
                */
        } else {
            /*
            if (musicPlayer != null) {
                if (isOnSelectedVideoSection()) {
                    playMusicSyncedWithVideo();
                    videoPlayer.setVolume(0.0f, 0.0f);
                } else {
                    videoPlayer.setVolume(0.5f, 0.5f);
                    musicPlayer.pause();
                }
            }
            */
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void updateSeekBarProgress() {
        if (videoPlayer != null) {
            if (videoPlayer.isPlaying())
                seekBar.setProgress(videoPlayer.getCurrentPosition());
            handler.postDelayed(updateTimeTask, 20);
        }
    }

    /**
     * Releases the media player and the video view
     */
    private void releaseVideoView() {
        preview.stopPlayback();
        preview.clearFocus();
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
        //disableMusicPlayer();
    }

    private void refreshStartTimeTag(int time) {
        startTimeTag.setText(TimeUtils.toFormattedTime(time));
    }

    private void refreshStopTimeTag(int time) {
        stopTimeTag.setText(TimeUtils.toFormattedTime(time));
    }

    private void refreshDurationTag(int duration) {
        durationTag.setText(TimeUtils.toFormattedTime(duration));
    }

}
