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
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.VideonaPlayerView;
import com.videonasocialmedia.videona.presentation.views.listener.VideonaPlayerListener;

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
public class VideonaPlayer extends RelativeLayout implements VideonaPlayerView, SeekBar.OnSeekBarChangeListener {

    private final Context context;
    protected Handler handler = new Handler();
    @Bind(R.id.video_editor_preview)
    AspectRatioVideoView videoPreview;
    @Bind(R.id.seekbar_editor_preview)
    SeekBar seekBar;
    @Bind(R.id.button_editor_play_pause)
    ImageButton playButton;

    private View videonaPlayerView;
    private VideonaPlayerListener videonaPlayerListener;
    private MediaController mediaController;
    private AudioManager audio;
    //    private Project videonaProject;
    private MediaPlayer videoPlayer;
    private MediaPlayer musicPlayer;
    private int totalVideoDuration = 0;
    private List<Video> videoList;
    private int instantTime = 0;
    private int currentVideoIndex = 0;
    private boolean isFullScreenBack = false;
    private List<Integer> videoStartTimes;
    private List<Integer> videoStopTimes;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };
    private Music music;

    public VideonaPlayer(Context context) {
        super(context);
        this.context = context;
        this.videonaPlayerView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.video_preview, this, true);
        ButterKnife.bind(this, videonaPlayerView);
    }

    public VideonaPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.videonaPlayerView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.video_preview, this, true);
        ButterKnife.bind(this, videonaPlayerView);
    }

    public VideonaPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.videonaPlayerView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.video_preview, this, true);
        ButterKnife.bind(this, videonaPlayerView);
    }

    public void initVideoPreviewFromVideonaProject(Project videonaProject, VideonaPlayerListener videonaPlayerListener) {
//        this.videonaProject = videonaProject;
        this.videoList = new ArrayList<>(); // TODO(jliarte): should initialize with videos from project
        // TODO(jliarte): initialize with music from project
//        this.music = (Music) videonaProject.getAudioTracks().get(0).getItems().get(0);
        setListener(videonaPlayerListener);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);
        mediaController = new MediaController(context);
        mediaController.setVisibility(View.INVISIBLE);
        audio = (AudioManager) context.getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);

        seekBar.setProgress(0); // TODO(jliarte): duplicated?
    }

    public void setListener(VideonaPlayerListener videonaPlayerListener) {
        this.videonaPlayerListener = videonaPlayerListener;
    }


    private boolean playerHasVideos() {
//        List<Media> list = videonaProject.getMediaTrack().getItems(); // TODO: This is a existing UseCase
//        return list.size() > 0;
        return this.videoList.size() > 0;
    }

    @Override
    public void playPreview() {
        initPreviewLists(videoList);
        seekBar.setMax(totalVideoDuration);
        initPreview();
        hidePlayButton();
    }

    @Override
    public void pausePreview() {
        pauseVideo();
        pauseMusic();
        showPlayButton();
    }

    public void showPlayButton() {
        playButton.setVisibility(View.VISIBLE);
    }

    public void hidePlayButton() {
        playButton.setVisibility(View.INVISIBLE);
    }

    public void pauseMusic() {
        if (musicPlayer != null && musicPlayer.isPlaying())
            musicPlayer.pause();
    }

    public void pauseVideo() {
        if (videoPlayer != null && videoPlayer.isPlaying())
            videoPlayer.pause();
    }

    @Override
    public void seekTo(int timeInMsec) {
        if (videoPlayer != null)
            videoPlayer.seekTo(timeInMsec);
    }


    public void initPreview() {
        if (videoList.size() > 0) {
            Video video = getVideoByProgress(instantTime);
            currentVideoIndex = getVideoPositioninList(video);
            int timeInMsec = instantTime - videoStartTimes.get(currentVideoIndex) +
                    videoList.get(currentVideoIndex).getFileStartTime();
            if (isFullScreenBack) {
                if (playButton.getVisibility() == View.INVISIBLE)
                    showPlayButton();
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
            showPlayButton();
            currentVideoIndex = 0;
            instantTime = 0;
        }
    }

    public void initPreviewLists(List<Video> videoList) {
        this.videoList = videoList;
        this.updatePreviewTimeLists();
        this.setTotalVideoDuration();
    }

    public void updatePreviewTimeLists() {
        totalVideoDuration = 0;
        videoStartTimes = new ArrayList<>();
        videoStopTimes = new ArrayList<>();
        for (Video video : videoList) {
            videoStartTimes.add(totalVideoDuration);
            totalVideoDuration = totalVideoDuration + video.getDuration();
            videoStopTimes.add(totalVideoDuration);
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
        if (0 <= progress && progress < videoStopTimes.get(0)) {
            currentVideoIndex = 0;
        } else {
            for (int i = 0; i < videoStopTimes.size(); i++) {
                if (i < videoStopTimes.size() - 1) {
                    boolean inRange = videoStopTimes.get(i) <= progress &&
                            progress < videoStopTimes.get(i + 1);
                    if (inRange) {
                        result = i + 1;
                    }
                }
            }
            if (result == -1) {
                currentVideoIndex = videoStopTimes.size() - 1;
            } else {
                currentVideoIndex = result;
            }
        }
        return videoList.get(currentVideoIndex);
    }

    private int getVideoPositioninList(Video seekVideo) {
        int position = 0;
        for (Video video : videoList) {
            if (video == seekVideo) {
                position = videoList.indexOf(video); // TODO(jliarte): this could be done with just this line?
            }
        }
        return position;
    }

    private void playNextVideo(final Video video, final int instantToStart) {
        notifyNewClipPlayed();
//        timeLineAdapter.updateSelection(currentVideoIndex);
//        videoListRecyclerView.scrollToPosition(currentVideoIndex);
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    videoPlayer.setLooping(false);
                    videoPlayer.start();
                    if (playButton.getVisibility() == View.VISIBLE)
                        hidePlayButton();
                    videoPlayer.seekTo(instantToStart);
                    if (videoHasMusic()) {
                        muteVideo();
                        playMusicSyncWithVideo();
                    } else {
                        releaseMusicPlayer();
                        videoPlayer.setVolume(0.5f, 0.5f);
                    }
                } catch (Exception e) {
                    // TODO don't force media player. Media player must be null here
                    seekBar.setProgress(0);
                    showPlayButton();
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

    private void notifyNewClipPlayed() {
        if (videonaPlayerListener != null)
            videonaPlayerListener.newClipPlayed(currentVideoIndex);
    }

    private void playNextVideoClicked(final Video video, final int instantToStart) {
        notifyNewClipPlayed();
//        timeLineAdapter.updateSelection(currentVideoIndex);
//        videoListRecyclerView.scrollToPosition(currentVideoIndex);

        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    videoPlayer.setLooping(false);
                    videoPlayer.start();
                    videoPlayer.seekTo(instantToStart);
                    if (videoHasMusic()) {
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
                    showPlayButton();
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
        notifyNewClipPlayed();
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
                    showPlayButton();
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
                            videoStartTimes.get(currentVideoIndex) -
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

    private boolean videoHasMusic() {
        return (music != null);
//        music = videonaProject.getAudioTracks().get(0).getItems().get(0)
//        return videonaProject.getAudioTracks().size() > 0 &&
//                videonaProject.getAudioTracks().get(0).getItems().size() > 0;
    }

    private void muteVideo() {
        videoPlayer.setVolume(0, 0);
    }

    private void playMusicSyncWithVideo() {
        releaseMusicPlayer();
        initMusicPlayer();
        if (!musicPlayer.isPlaying()) {
            musicPlayer.start();
        }
//            musicPlayer.seekTo(seekBar.getProgress());
//        } else {
//        }
        musicPlayer.seekTo(seekBar.getProgress());
    }

    private void initMusicPlayer() {
        if (musicPlayer == null) {
//            music = (Music) videonaProject.getAudioTracks().get(0).getItems().get(0);
            musicPlayer = MediaPlayer.create(context, music.getMusicResourceId());
            musicPlayer.setVolume(0.5f, 0.5f);
        }
    }

    private boolean isEndOfVideo() {
        return seekBar.getProgress() >= videoStopTimes.get(currentVideoIndex);
    }

    public void releaseView() {
        showPlayButton();
        releaseVideoView();
        currentVideoIndex = 0;
        if (videoList.size() > 0)
            initVideoPlayer(videoList.get(currentVideoIndex),
                    videoList.get(currentVideoIndex).getFileStartTime() + 100);
        seekBar.setProgress(0);
        instantTime = 0;
        notifyNewClipPlayed();
//        timeLineAdapter.updateSelection(currentVideoIndex);
//        videoListRecyclerView.scrollToPosition(currentVideoIndex);
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.pause();
            releaseMusicPlayer();
        }
    }

    @OnClick(R.id.button_editor_play_pause)
    public void onClickPlayPauseButton() {
        if (playerHasVideos()) {
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
        boolean result = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onClickPlayPauseButton();
            result = true;
        }
//        else {
//            result = false;
//        }
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
        totalVideoDuration = 0;
        instantTime = 0;
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

    private void releaseMusicPlayer() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    public void seekToClip(int position) {
        currentVideoIndex = position;

        int progress = videoStartTimes.get(currentVideoIndex) -
                videoList.get(currentVideoIndex).getFileStartTime();

        instantTime = progress;
        seekBar.setProgress(progress);

        int timeInMsec = progress - videoStartTimes.get(currentVideoIndex) +
                videoList.get(currentVideoIndex).getFileStartTime();

        if (videoPlayer != null) {
            seekToNextVideo(videoList.get(position), timeInMsec);
        } else {
            initVideoPlayer(videoList.get(position), timeInMsec);
        }

        showPlayButton();
    }

    public void setBlackBackgroundColor() {
        videoPreview.setBackgroundColor(Color.BLACK);
    }

    public void setTotalVideoDuration() {
        seekBar.setMax(totalVideoDuration);
    }

    /**
     * Seekbar listeners
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (playerHasVideos()) {
                Video video = getVideoByProgress(progress);
                int timeInMsec = progress - videoStartTimes.get(currentVideoIndex) +
                        videoList.get(currentVideoIndex).getFileStartTime();

                if (videoPlayer != null) {
                    playNextVideo(video, timeInMsec);
                } else {
                    initVideoPlayer(video, timeInMsec);
                }
                hidePlayButton();
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

    public void setMusicTrack(Music music) {
        this.music = music;
    }
}
