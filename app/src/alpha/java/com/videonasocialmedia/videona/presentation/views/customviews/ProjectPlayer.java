package com.videonasocialmedia.videona.presentation.views.customviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.ProjectPlayerView;
import com.videonasocialmedia.videona.presentation.views.listener.ProjectPlayerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by jliarte on 13/05/16.
 */
public class ProjectPlayer extends RelativeLayout implements ProjectPlayerView, SeekBar.OnSeekBarChangeListener {

    private ProjectPlayerListener projectPlayerListener;
    private final Context context;
    @Bind(R.id.video_editor_preview) AspectRatioVideoView videoPreview;
    @Bind(R.id.seekbar_editor_preview) SeekBar seekBar;
    @Bind(R.id.button_editor_play_pause) ImageButton playButton;

    private View projectPlayerView;
    private MediaController mediaController;
    private AudioManager audio;
    private Project videonaProject;
    private MediaPlayer videoPlayer;
    private MediaPlayer musicPlayer;
    private int projectDuration = 0;
    private List<Video> videoList;
    private int instantTime = 0;
    private int currentVideoIndex = 0;
    private boolean isFullScreenBack = false;
    private List<Integer> videoStartTimeInProject;
    private List<Integer> videoStopTimeInProject;
    private Music music;
    protected Handler handler = new Handler();
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };

    public ProjectPlayer(Context context) {
        super(context);
        this.context = context;
        this.projectPlayerView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.video_preview, this, true);
        ButterKnife.bind(this, projectPlayerView);
    }

    public ProjectPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.projectPlayerView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.video_preview, this, true);
        ButterKnife.bind(this, projectPlayerView);
    }

    public ProjectPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.projectPlayerView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.video_preview, this, true);
        ButterKnife.bind(this, projectPlayerView);
    }

    public void initVideoPreview(Project videonaProject, ProjectPlayerListener projectPlayerListener) {
        this.videonaProject = videonaProject;
        this.videoList = new ArrayList<>();
        setListener(projectPlayerListener);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);
        mediaController = new MediaController(context);
        mediaController.setVisibility(View.INVISIBLE);
        audio = (AudioManager) context.getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);

        seekBar.setProgress(0);
    }

    public void setListener(ProjectPlayerListener projectPlayerListener) {
        this.projectPlayerListener = projectPlayerListener;
    }


    private boolean projectHasVideos() {
        List<Media> list = videonaProject.getMediaTrack().getItems(); // TODO: This is a existing UseCase
        return list.size() > 0;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (projectHasVideos()) {
                Video video = getVideoByProgress(progress);
                int timeInMsec = progress - videoStartTimeInProject.get(currentVideoIndex) +
                        videoList.get(currentVideoIndex).getFileStartTime();

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

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void playPreview() {
        initPreviewLists(videoList);
        seekBar.setMax(projectDuration);
        initPreview();
        playButton.setVisibility(View.INVISIBLE);
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
        if (videoPlayer != null)
            videoPlayer.seekTo(timeInMsec);
    }

    public void initPreview() {
        if (videoList.size() > 0) {
            Video video = getVideoByProgress(instantTime);
            currentVideoIndex = getPositionVideo(video);
            int timeInMsec = instantTime - videoStartTimeInProject.get(currentVideoIndex) +
                    videoList.get(currentVideoIndex).getFileStartTime();
            if (isFullScreenBack) {
                if (playButton.getVisibility() == View.INVISIBLE)
                    playButton.setVisibility(View.VISIBLE);
                initVideoPlayer(video, timeInMsec);
                isFullScreenBack = false;
            } else {
                if (videoPlayer == null) {
                    initVideoPlayer(video, timeInMsec);
                } else {
                    playNextVideo(video, timeInMsec);
                }
            }
        } else {
            seekBar.setProgress(0);
            playButton.setVisibility(View.VISIBLE);
            currentVideoIndex = 0;
            instantTime = 0;
        }
    }

    public void initPreviewLists(List<Video> videoList) {
        projectDuration = 0;
        this.videoList = videoList;
        videoStartTimeInProject = new ArrayList<>();
        videoStopTimeInProject = new ArrayList<>();
        for (Video video : videoList) {
            videoStartTimeInProject.add(projectDuration);
            projectDuration = projectDuration + video.getDuration();
            videoStopTimeInProject.add(projectDuration);
        }
    }


    private void initVideoPlayer(final Video video, final int startTime) {
        videoPreview.setVideoPath(video.getMediaPath());
        videoPreview.setMediaController(mediaController);
        videoPreview.canSeekBackward();
        videoPreview.canSeekForward();
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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
        videoPreview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, instantTime);
                return false;
            }
        });
        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentVideoIndex++;
                if (hasNextVideoToPlay()) {
                    playNextVideo(videoList.get(currentVideoIndex),
                            videoList.get(currentVideoIndex).getFileStartTime());
                }
            }
        });
        videoPreview.requestFocus();
    }

    private Video getVideoByProgress(int progress) {
        int result = -1;
        if (0 <= progress && progress < videoStopTimeInProject.get(0)) {
            currentVideoIndex = 0;
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
                currentVideoIndex = videoStopTimeInProject.size() - 1;
            } else {
                currentVideoIndex = result;
            }
        }
        return videoList.get(currentVideoIndex);
    }

    private int getPositionVideo(Video seekVideo) {
        int position = 0;
        for (Video video : videoList) {
            if (video == seekVideo) {
                position = videoList.indexOf(video);
            }
        }
        return position;
    }

    private void playNextVideo(final Video video, final int instantToStart) {
        projectPlayerListener.newClipPlayed(currentVideoIndex);
//        timeLineAdapter.updateSelection(currentVideoIndex);
//        videoListRecyclerView.scrollToPosition(currentVideoIndex);
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    videoPlayer.setLooping(false);
                    videoPlayer.start();
                    if (playButton.getVisibility() == View.VISIBLE)
                        playButton.setVisibility(View.INVISIBLE);
                    videoPlayer.seekTo(instantToStart);
                    if (isMusicOnProject()) {
                        muteVideo();
                        playMusicSyncWithVideo();
                    } else {
                        releaseMusicPlayer();
                        videoPlayer.setVolume(0.5f, 0.5f);
                    }
                } catch (Exception e) {
                    // TODO don't force media player. Media player must be null here
                    seekBar.setProgress(0);
                    playButton.setVisibility(View.VISIBLE);
                }
            }
        });
        videoPreview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, instantTime);
                return false;
            }
        });
        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentVideoIndex++;
                if (hasNextVideoToPlay()) {
                    playNextVideo(videoList.get(currentVideoIndex),
                            videoList.get(currentVideoIndex).getFileStartTime());
                } else {
                    releaseView();
                }
            }
        });
        try {
            videoPlayer.reset();
            videoPlayer.setDataSource(video.getMediaPath());
            videoPlayer.prepare();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        videoPreview.requestFocus();
    }

    private void playNextVideoClicked(final Video video, final int instantToStart) {
        projectPlayerListener.newClipPlayed(currentVideoIndex);
//        timeLineAdapter.updateSelection(currentVideoIndex);
//        videoListRecyclerView.scrollToPosition(currentVideoIndex);

        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    videoPlayer.setLooping(false);
                    videoPlayer.start();
                    videoPlayer.seekTo(instantToStart);
                    if (isMusicOnProject()) {
                        muteVideo();
                        playMusicSyncWithVideo();
                    } else {
                        releaseMusicPlayer();
                        videoPlayer.setVolume(0.5f, 0.5f);
                    }
                    videoPlayer.pause();
                } catch (Exception e) {
                    // TODO don't force media player. Media player must be null here
                    seekBar.setProgress(0);
                    playButton.setVisibility(View.VISIBLE);
                }
            }
        });
        videoPreview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, instantTime);
                return false;
            }
        });
        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentVideoIndex++;
                if (hasNextVideoToPlay()) {
                    playNextVideo(videoList.get(currentVideoIndex),
                            videoList.get(currentVideoIndex).getFileStartTime());
                } else {
                    releaseView();
                }
            }
        });
        try {
            videoPlayer.reset();
            videoPlayer.setDataSource(video.getMediaPath());
            videoPlayer.prepare();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        videoPreview.requestFocus();
    }


    private void seekToNextVideo(final Video video, final int instantToStart) {
        projectPlayerListener.newClipPlayed(currentVideoIndex);
//        timeLineAdapter.updateSelection(currentVideoIndex);
//        videoListRecyclerView.scrollToPosition(currentVideoIndex);

        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    videoPlayer.seekTo(instantToStart);
                } catch (Exception e) {
                    // TODO don't force media player. Media player must be null here
                    seekBar.setProgress(0);
                    playButton.setVisibility(View.VISIBLE);
                }
            }
        });
        videoPreview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, instantTime);
                return false;
            }
        });
        try {
            videoPlayer.reset();
            videoPlayer.setDataSource(video.getMediaPath());
            videoPlayer.prepare();
            videoPlayer.seekTo(instantToStart);
            videoPlayer.start();
            videoPlayer.pause();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSeekBarProgress() {
        if (videoPlayer != null) {
            try {
                if (videoPlayer.isPlaying() && currentVideoIndex < videoList.size()) {
                    seekBar.setProgress(videoPlayer.getCurrentPosition() +
                            videoStartTimeInProject.get(currentVideoIndex) -
                            videoList.get(currentVideoIndex).getFileStartTime());
                    // refreshStartTimeTag(videoSeekBar.getProgress());
                    if (isEndOfVideo()) {
                        currentVideoIndex++;
                        if (hasNextVideoToPlay()) {

                            playNextVideo(videoList.get(currentVideoIndex),
                                    videoList.get(currentVideoIndex).getFileStartTime());
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

    private boolean hasNextVideoToPlay() {
        return currentVideoIndex < videoList.size();
    }

    private boolean isMusicOnProject() {
        return videonaProject.getAudioTracks().size() > 0 &&
                videonaProject.getAudioTracks().get(0).getItems().size() > 0;
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
            music = (Music) videonaProject.getAudioTracks().get(0).getItems().get(0);
            musicPlayer = MediaPlayer.create(context, music.getMusicResourceId());
            musicPlayer.setVolume(0.5f, 0.5f);
        }
    }

    private boolean isEndOfVideo() {
        return seekBar.getProgress() >= videoStopTimeInProject.get(currentVideoIndex);
    }

    public void releaseView() {
        playButton.setVisibility(View.VISIBLE);
        releaseVideoView();
        currentVideoIndex = 0;
        if (videoList.size() > 0)
            initVideoPlayer(videoList.get(currentVideoIndex),
                    videoList.get(currentVideoIndex).getFileStartTime() + 100);
        seekBar.setProgress(0);
        instantTime = 0;
        projectPlayerListener.newClipPlayed(currentVideoIndex);
//        timeLineAdapter.updateSelection(currentVideoIndex);
//        videoListRecyclerView.scrollToPosition(currentVideoIndex);
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.pause();
            releaseMusicPlayer();
        }
    }

    /**
     * Releases the media player and the video view
     */
    private void releaseVideoView() {
        videoPreview.stopPlayback();
        videoPreview.clearFocus();
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
    }

    @OnClick(R.id.button_editor_play_pause)
    public void onClickPlayPauseButton(){
        if (projectHasVideos()) {
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

    @OnTouch(R.id.video_editor_preview)
    public boolean onTouchPreview(MotionEvent event) {
        boolean result;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onClickPlayPauseButton();
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                return false;
        }
    }

    public void destroy() {
        handler.removeCallbacksAndMessages(null);
    }

    public void pause() {
        releaseVideoView();
        releaseMusicPlayer();
        projectDuration = 0;
        instantTime = 0;
    }

    public void seekToClip(int position) {
        currentVideoIndex = position;

        int progress = videoStartTimeInProject.get(currentVideoIndex) -
                videoList.get(currentVideoIndex).getFileStartTime();

        instantTime = progress;
        seekBar.setProgress(progress);

        int timeInMsec = progress - videoStartTimeInProject.get(currentVideoIndex) +
                videoList.get(currentVideoIndex).getFileStartTime();

        if (videoPlayer != null) {
            seekToNextVideo(videoList.get(position), timeInMsec);
        } else {
            initVideoPlayer(videoList.get(position), timeInMsec);
        }

        playButton.setVisibility(View.VISIBLE);
    }

    public void setBlackBackgroundColor() {
        videoPreview.setBackgroundColor(Color.BLACK);
    }

    public void setProjectDuration() {
        seekBar.setMax(projectDuration);
    }
}
