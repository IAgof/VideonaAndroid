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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
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
    private MediaPlayer musicPlayer;
    private Music music;
    private Project project;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };
    private int projectDuration = 0;
    private List<Video> movieList;
    private List<Integer> videoStartTimeInProject;
    private List<Integer> videoStopTimeInProject;
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
        movieList = null;
        videoStartTimeInProject = null;
        videoStopTimeInProject = null;
        releaseVideoView();
        releaseMusicPlayer();
        projectDuration = 0;
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
        if(videoPlayer != null) {
            if (videoPlayer.isPlaying()) {
                pause();
            } else {
                play();
            }
        }
    }

    @Override
    public void play() {
        if (videoPlayer != null) {
            videoPlayer.start();
            if(isMusicOnProject()){
                muteVideo();
                playMusicSyncWithVideo();
            }
            playButton.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isMusicOnProject() {
        project = Project.getInstance(null,null,null);
        return project.getAudioTracks().size() > 0 && project.getAudioTracks().get(0).getItems().size() > 0;
    }

    private void muteVideo() {
        videoPlayer.setVolume(0, 0);
    }

    private void playMusicSyncWithVideo() {
        releaseMusicPlayer();
        initMusicPlayer();
        if(musicPlayer.isPlaying()) {
            musicPlayer.seekTo(seekBar.getProgress());
        } else {
            musicPlayer.start();
            musicPlayer.seekTo(seekBar.getProgress());
        }
    }

    private void releaseMusicPlayer() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    private void initMusicPlayer() {
        if(musicPlayer == null) {
            music = (Music) project.getAudioTracks().get(0).getItems().get(0);
            musicPlayer = MediaPlayer.create(getActivity(), music.getMusicResourceId());
            musicPlayer.setVolume(0.5f, 0.5f);
        }
    }

    @Override
    public void pause() {
        if (videoPlayer != null && videoPlayer.isPlaying())
            videoPlayer.pause();
        if (musicPlayer != null && musicPlayer.isPlaying())
            musicPlayer.pause();
        playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void seekTo(int timeInMsec) {
        videoPlayer.seekTo(timeInMsec);
    }

    @Override
    public void updateVideoList() {
        previewPresenter.update();
    }

    @Override
    public void showPreview(List<Video> videoList) {
        movieList = videoList;
        videoStartTimeInProject = new ArrayList<>();
        videoStopTimeInProject = new ArrayList<>();
        for (Video video : movieList) {
            videoStartTimeInProject.add(projectDuration);
            projectDuration = projectDuration + video.getDuration();
            videoStopTimeInProject.add(projectDuration);
        }
        showTimeTags(projectDuration);
        seekBar.setMax(projectDuration);
        initVideoPlayer(movieList.get(videoToPlay),
                movieList.get(videoToPlay).getFileStartTime() + 100);
    }

    private void showTimeTags(int duration) {
        refreshStartTimeTag(0);
        refreshDurationTag(0);
        refreshStopTimeTag(duration);
    }

    private void initVideoPlayer(final Video video, final int startTime) {
        preview.setVideoPath(video.getMediaPath());
        preview.setMediaController(mediaController);
        preview.canSeekBackward();
        preview.canSeekForward();
        preview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoPlayer = mp;
                videoPlayer.setVolume(0.5f, 0.5f);
                videoPlayer.setLooping(false);
                videoPlayer.start();
                videoPlayer.seekTo(startTime);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                videoPlayer.pause();
                updateSeekBarProgress();
            }
        });
        preview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoToPlay++;
                if (videoToPlay < movieList.size()) {
                    playNextVideo(movieList.get(videoToPlay), movieList.get(videoToPlay).getFileStartTime());
                }
            }
        });
        preview.requestFocus();
    }

    private void playNextVideo(final Video video, final int instantToStart) {
        preview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoPlayer.setVolume(0.5f, 0.5f);
                videoPlayer.setLooping(false);
                play();
                videoPlayer.seekTo(instantToStart);
            }
        });
        preview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoToPlay++;
                if (videoToPlay < movieList.size()) {
                    playNextVideo(movieList.get(videoToPlay), movieList.get(videoToPlay).getFileStartTime());
                }

                if (videoToPlay == movieList.size()) {
                    releaseView();
                }
            }
        });

        try {
            videoPlayer.reset();
            videoPlayer.setDataSource(video.getMediaPath());
            videoPlayer.prepare();
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

        preview.requestFocus();
    }

    @Override
    public void showError(String message) {
        releaseVideoView();
        releaseMusicPlayer();
        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_add_videos), Toast.LENGTH_SHORT).show();
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
            Video video = seekVideo(progress);
            int timeInMsec = progress - videoStartTimeInProject.get(videoToPlay) +
                    movieList.get(videoToPlay).getFileStartTime();
            if(videoPlayer != null) {
                playNextVideo(video, timeInMsec);
            } else {
                initVideoPlayer(video, timeInMsec);
            }
        }
    }

    private Video seekVideo(int progress) {
        int result = -1;
        if(0 <= progress && progress < videoStopTimeInProject.get(0)) {
            videoToPlay = 0;
        } else {
            for(int i=0; i<videoStopTimeInProject.size();i++) {
                if(i<videoStopTimeInProject.size()-1) {
                    boolean inRange = videoStopTimeInProject.get(i) <= progress &&
                            progress < videoStopTimeInProject.get(i+1);
                    if(inRange) {
                        result = i+1;
                    }
                }
            }
            if(result == -1) {
                videoToPlay = videoStopTimeInProject.size()-1;
            } else {
                videoToPlay = result;
            }
        }
        Log.d("video", String.valueOf(videoToPlay));
        return movieList.get(videoToPlay);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    private void updateSeekBarProgress() {
        if (videoPlayer != null) {
            try {
                if (videoPlayer.isPlaying() && videoToPlay < movieList.size()) {
                    seekBar.setProgress(videoPlayer.getCurrentPosition() + videoStartTimeInProject.get(videoToPlay));
                    refreshDurationTag(seekBar.getProgress());
                    if (seekBar.getProgress() >= videoStopTimeInProject.get(videoToPlay)) {
                        videoToPlay++;
                        if (videoToPlay < movieList.size()) {
                            playNextVideo(movieList.get(videoToPlay), movieList.get(videoToPlay).getFileStartTime());
                        } else {
                            releaseView();
                        }
                    }
                }
            } catch (Exception e){

            }
            handler.postDelayed(updateTimeTask, 20);
        }
    }

    private void releaseView() {
        playButton.setVisibility(View.VISIBLE);
        releaseVideoView();
        videoToPlay = 0;
        initVideoPlayer(movieList.get(videoToPlay),
                movieList.get(videoToPlay).getFileStartTime() + 100);
        seekBar.setProgress(0);

        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.pause();
            releaseMusicPlayer();
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
