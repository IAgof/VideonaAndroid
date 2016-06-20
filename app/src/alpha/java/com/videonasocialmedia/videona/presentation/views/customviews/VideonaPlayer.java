package com.videonasocialmedia.videona.presentation.views.customviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.videonasocialmedia.videona.R;
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

    private String TAG = VideonaPlayer.class.getCanonicalName();
    private View videonaPlayerView;
    private VideonaPlayerListener videonaPlayerListener;
    private MediaController mediaController;
    private AudioManager audio;
    private MediaPlayer videoPlayer;
    private MediaPlayer musicPlayer;
    private int totalVideoDuration = 0;
    private List<Video> videoList;
    private int currentTimePositionInList = 0;
    private int currentVideoListIndex = 0;
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

    /**** Videona player lifecycle methods ****/

    public void destroy() {
        handler.removeCallbacksAndMessages(null);
    }

    public void pause() {
        pausePreview();
        releaseVideoView();
        releaseMusicPlayer();
//        totalVideoDuration = 0;
//        currentTimePositionInList = 0;
    }

    /**** end of Videona player lifecycle methods ****/

    public void initVideoPreview(VideonaPlayerListener videonaPlayerListener) {
        this.videoList = new ArrayList<>(); // TODO(jliarte): should initialize with videos from project?
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
        return this.videoList.size() > 0;
    }

    @Override
    public void playPreview() {
        initPreviewLists(videoList);
        seekBar.setMax(totalVideoDuration);
        initPreview(currentTimePositionInList);
        hidePlayButton();
    }

    @Override
    public void pausePreview() {
        pauseVideo();
        pauseMusic();
        currentTimePositionInList = seekBar.getProgress();
        showPlayButton();
    }

    @Override
    public void seekTo(int timeInMsec) {
        if (videoPlayer != null) {
            currentTimePositionInList = timeInMsec;
            videoPlayer.seekTo(timeInMsec);
            seekBar.setProgress(timeInMsec);
        }
    }

    public void setSeekBarProgress(int progress){
        seekBar.setProgress(progress);
    }

    @Override
    public void setMusic(Music music) {
        this.music = music;
    }

    @Override
    public void bindVideoList(List<Video> videoList) {
        this.initPreviewLists(videoList);
        this.initPreview(0);
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

    public void initPreview(int instantTime) {
        this.currentTimePositionInList = instantTime;
        seekBar.setProgress(instantTime);
//        updateSeekBarProgress();
        if (videoList.size() > 0) {
            Video video = getVideoByProgress(this.currentTimePositionInList);
            currentVideoListIndex = getVideoPositioninList(video);
            int timeInMsec = this.currentTimePositionInList - videoStartTimes.get(currentVideoListIndex) +
                    videoList.get(currentVideoListIndex).getFileStartTime();
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
            currentVideoListIndex = 0;
            this.currentTimePositionInList = 0;
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
                videoPlayer.seekTo(startTime);
                seekBar.setProgress(startTime);
//                videoPlayer.start();
                // TODO(jliarte): 17/06/16 This has to be called in order updateTimeTask to start the thread
                updateSeekBarProgress();
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                videoPlayer.pause();
            }
        });
        videoPreview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, currentTimePositionInList);
                return false;
            }
        });
        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentVideoListIndex++;
                if (hasNextVideoToPlay()) {
                    playNextVideo(videoList.get(currentVideoListIndex),
                            videoList.get(currentVideoListIndex).getFileStartTime());
                }
            }
        });
        videoPreview.requestFocus();
    }

    private Video getVideoByProgress(int progress) {
        int result = -1;
        if (0 <= progress && progress < videoStopTimes.get(0)) {
            currentVideoListIndex = 0;
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
                currentVideoListIndex = videoStopTimes.size() - 1;
            } else {
                currentVideoListIndex = result;
            }
        }
        return videoList.get(currentVideoListIndex);
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
                initVideoPlayer(video, currentTimePositionInList);
                return false;
            }
        });
        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentVideoListIndex++;
                if (hasNextVideoToPlay()) {
                    playNextVideo(videoList.get(currentVideoListIndex),
                            videoList.get(currentVideoListIndex).getFileStartTime());
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
            videonaPlayerListener.newClipPlayed(currentVideoListIndex);
    }

    private void playNextVideoClicked(final Video video, final int instantToStart) {
        notifyNewClipPlayed();
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
                initVideoPlayer(video, currentTimePositionInList);
                return false;
            }
        });
        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentVideoListIndex++;
                if (hasNextVideoToPlay()) {
                    playNextVideo(videoList.get(currentVideoListIndex),
                            videoList.get(currentVideoListIndex).getFileStartTime());
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
                initVideoPlayer(video, currentTimePositionInList);
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
//                if (videoPlayer.isPlaying() && currentVideoListIndex < videoList.size()) {
                if (currentVideoListIndex < videoList.size()) {
                    seekBar.setProgress(videoPlayer.getCurrentPosition() +
                            videoStartTimes.get(currentVideoListIndex) -
                            videoList.get(currentVideoListIndex).getFileStartTime());
                    // refreshStartTimeTag(videoSeekBar.getProgress());
                    if (isEndOfVideo()) {
                        currentVideoListIndex++;
                        if (hasNextVideoToPlay()) {

                            playNextVideo(videoList.get(currentVideoListIndex),
                                    videoList.get(currentVideoListIndex).getFileStartTime());
                        } else {
                            releaseView();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "updateSeekBarProgress: exception updating videonaplayer seekbar");
                Log.d(TAG, e.getMessage());
            }
            handler.postDelayed(updateTimeTask, 20);
        }
    }

    private boolean hasNextVideoToPlay() {
        return currentVideoListIndex < videoList.size();
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
        if (musicPlayer == null && music != null) {
            musicPlayer = MediaPlayer.create(context, music.getMusicResourceId());
            musicPlayer.setVolume(0.5f, 0.5f);
        }
    }

    private boolean isEndOfVideo() {
        return seekBar.getProgress() >= videoStopTimes.get(currentVideoListIndex);
    }

    public void releaseView() {
        showPlayButton();
        releaseVideoView();
        currentVideoListIndex = 0;
        if (videoList.size() > 0)
            initVideoPlayer(videoList.get(currentVideoListIndex),
                    videoList.get(currentVideoListIndex).getFileStartTime() + 100);
        seekBar.setProgress(0);
        currentTimePositionInList = 0;
        notifyNewClipPlayed();
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
        currentVideoListIndex = position;

        int progress = videoStartTimes.get(currentVideoListIndex) -
                videoList.get(currentVideoListIndex).getFileStartTime();

        currentTimePositionInList = progress;
        seekBar.setProgress(progress);

        int timeInMsec = progress - videoStartTimes.get(currentVideoListIndex) +
                videoList.get(currentVideoListIndex).getFileStartTime();

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
                int timeInMsec = progress - videoStartTimes.get(currentVideoListIndex) +
                        videoList.get(currentVideoListIndex).getFileStartTime();

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

    public int getCurrentPosition() {
        return currentTimePositionInList;
    }
}
