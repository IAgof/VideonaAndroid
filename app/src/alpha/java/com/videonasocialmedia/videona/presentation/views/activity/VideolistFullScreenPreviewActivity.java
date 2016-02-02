/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Author:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
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
 * This class is used to show a preview of the selected video.
 */
public class VideolistFullScreenPreviewActivity extends VideonaActivity implements PreviewView,
        SeekBar.OnSeekBarChangeListener {

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

    private final String LOG_TAG = "VIDEO LIST PREVIEW ACTIVITY";
    protected Handler handler = new Handler();
    private PreviewPresenter previewPresenter;
    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private MediaPlayer musicPlayer;
    private Music music;
    private Project project;
    private int projectDuration = 0;
    private List<Video> movieList;
    private List<Integer> videoStartTimeInProject;
    private List<Integer> videoStopTimeInProject;
    private int videoToPlay = 0;
    private int instantTime = 0;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_videolist_preview);
        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        instantTime = bundle.getInt("TIME");

        previewPresenter = new PreviewPresenter(this);

        seekBar.setProgress(instantTime);
        seekBar.setOnSeekBarChangeListener(this);

        mediaController = new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateVideoList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseVideoView();
        releaseMusicPlayer();
        projectDuration = 0;
        instantTime = 0;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        ButterKnife.reset(this);
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
        if (isVideosOnProject()) {
            if (videoPlayer != null) {
                if (videoPlayer.isPlaying()) {
                    pausePreview();
                    instantTime = seekBar.getProgress();
                } else {
                    playPreview();
                }
            } else {
                playPreview();
            }
        } else {
            seekBar.setProgress(0);
        }
    }

    private boolean isVideosOnProject() {
        List<Media> list = previewPresenter.checkVideosOnProject();
        return list.size() > 0;
    }


    @Override
    public void playPreview() {
        updateVideoList();
        playButton.setVisibility(View.INVISIBLE);
    }

    private boolean isMusicOnProject() {
        project = Project.getInstance(null, null, null);
        return project.getAudioTracks().size() > 0 && project.getAudioTracks().get(0).getItems().size() > 0;
    }

    private void muteVideo() {
        videoPlayer.setVolume(0, 0);
    }

    private void playMusicSyncWithVideo() {
        releaseMusicPlayer();
        initMusicPlayer();
        if (musicPlayer.isPlaying()) {
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
        if (musicPlayer == null) {
            music = (Music) project.getAudioTracks().get(0).getItems().get(0);
            musicPlayer = MediaPlayer.create(this, music.getMusicResourceId());
            musicPlayer.setVolume(0.5f, 0.5f);
        }
    }

    @Override
    public void pausePreview() {
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
        projectDuration = 0;
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
        if (movieList.size() > 0) {
            Video video = seekVideo(instantTime);
            videoToPlay = getPosition(video);
            int timeInMsec = instantTime - videoStartTimeInProject.get(videoToPlay) +
                    movieList.get(videoToPlay).getFileStartTime();

            if (videoPlayer == null) {
                initVideoPlayer(video, timeInMsec);
            } else {
                playNextVideo(video, timeInMsec);
            }
        } else {
            seekBar.setProgress(0);
            playButton.setVisibility(View.VISIBLE);
            videoToPlay = 0;
            instantTime = 0;
        }
    }

    private int getPosition(Video seekVideo) {
        int position = 0;
        for (Video video : movieList) {
            if (video == seekVideo) {
                position = movieList.indexOf(video);
            }
        }
        return position;
    }

    private void showTimeTags(int duration) {
        refreshStartTimeTag(0);
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
                if (hasNextVideoToPlay()) {
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
                videoPlayer.setLooping(false);
                videoPlayer.start();
                videoPlayer.seekTo(instantToStart);
                if (isMusicOnProject()) {
                    muteVideo();
                    playMusicSyncWithVideo();
                } else {
                    videoPlayer.setVolume(0.5f, 0.5f);
                }
            }
        });
        preview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoToPlay++;
                if (hasNextVideoToPlay()) {
                    playNextVideo(movieList.get(videoToPlay), movieList.get(videoToPlay).getFileStartTime());
                } else {
                    releaseView();
                }
            }
        });

        try {
            videoPlayer.stop();
            videoPlayer.reset();
            videoPlayer.setDataSource(video.getMediaPath());
            videoPlayer.prepare();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        preview.requestFocus();
    }

    @Override
    public void showError(String message) {
        releaseVideoView();
        releaseMusicPlayer();
        Toast.makeText(this.getApplicationContext(), getString(R.string.toast_add_videos), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateSeekBarDuration(int projectDuration) {

    }

    @Override
    public void updateSeekBarSize() {

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
            if (isVideosOnProject()) {
                Video video = seekVideo(progress);
                int timeInMsec = progress - videoStartTimeInProject.get(videoToPlay) +
                        movieList.get(videoToPlay).getFileStartTime();
                if (videoPlayer != null) {
                    playNextVideo(video, timeInMsec);
                } else {
                    initVideoPlayer(video, timeInMsec);
                }
                playButton.setVisibility(View.INVISIBLE);
            } else {
                seekBar.setProgress(0);
            }
        }
    }

    private Video seekVideo(int progress) {
        int result = -1;
        if (0 <= progress && progress < videoStopTimeInProject.get(0)) {
            videoToPlay = 0;
        } else {
            for (int i = 0; i < videoStopTimeInProject.size(); i++) {
                if (i < videoStopTimeInProject.size() - 1) {
                    boolean inRange = videoStopTimeInProject.get(i) <= progress &&
                            progress < videoStopTimeInProject.get(i + 1);
                    if (inRange) {
                        result = i + 1;
                    }
                }
            }
            if (result == -1) {
                videoToPlay = videoStopTimeInProject.size() - 1;
            } else {
                videoToPlay = result;
            }
        }
        return movieList.get(videoToPlay);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private void updateSeekBarProgress() {
        if (videoPlayer != null) {
            try {
                if (videoPlayer.isPlaying() && videoToPlay < movieList.size()) {
                    seekBar.setProgress(videoPlayer.getCurrentPosition() +
                            videoStartTimeInProject.get(videoToPlay) -
                            movieList.get(videoToPlay).getFileStartTime());
                    refreshStartTimeTag(seekBar.getProgress());
                    if (isEndOfVideo()) {
                        videoToPlay++;
                        if (hasNextVideoToPlay()) {
                            playNextVideo(movieList.get(videoToPlay), movieList.get(videoToPlay).getFileStartTime());
                        } else {
                            releaseView();
                        }
                    }
                }
            } catch (Exception e) {

            }
            handler.postDelayed(updateTimeTask, 20);
        }
    }

    private boolean isEndOfVideo() {
        return seekBar.getProgress() >= videoStopTimeInProject.get(videoToPlay);
    }

    private boolean hasNextVideoToPlay() {
        return videoToPlay < movieList.size();
    }

    private void releaseView() {
        playButton.setVisibility(View.VISIBLE);
        releaseVideoView();
        videoToPlay = 0;
        if (movieList.size() > 0)
            initVideoPlayer(movieList.get(videoToPlay),
                    movieList.get(videoToPlay).getFileStartTime() + 100);
        seekBar.setProgress(0);
        instantTime = 0;
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
            /*
            if(videoPlayer.isPlaying()) {
                videoPlayer.pause();
                videoPlayer.stop();
            }
            */
            //videoPlayer.reset();
            //videoPlayer.stop();
            //videoPlayer.setNextMediaPlayer(null);
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

    @OnClick({R.id.edit_button_fullscreen_out})
    public void onClickFullScreenOutMode() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.reset();
        }
        releaseVideoView();
        releaseMusicPlayer();
        projectDuration = 0;
        instantTime = 0;
        //overridePendingTransition(0,0);
    }

    @OnClick({R.id.share_button_share})
    public void shareVideo() {
        /*
        pausePreview();
        showProgressDialog();
        final Runnable r = new Runnable() {
            public void run() {
                editPresenter.startExport();
            }
        };
        performOnBackgroundThread(this, r);
        */
    }

}
