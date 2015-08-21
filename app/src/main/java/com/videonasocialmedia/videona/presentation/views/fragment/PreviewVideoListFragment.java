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

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.PreviewPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.PreviewView;
import com.videonasocialmedia.videona.presentation.views.activity.VideolistPreviewActivity;
import com.videonasocialmedia.videona.utils.TimeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.greenrobot.event.EventBus;

/**
 * This class is used to show the right panel of the audio fx menu
 */
public class PreviewVideoListFragment extends Fragment implements PreviewView,
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

    //Hide relativeLayout, needed to show trimming bar
    //TODO change with EventBus
    @InjectView(R.id.relativeLayoutPreviewVideo)
    RelativeLayout relativeLayoutPreviewVideoTrim;
    
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
    private boolean isFullScreenBack = false;
    private AudioManager audio;
    /**
     * Tracker google analytics
     */
    private Tracker tracker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_fragment_all_preview, container, false);
        ButterKnife.inject(this, view);
        VideonaApplication app = (VideonaApplication) getActivity().getApplication();
        tracker = app.getTracker();

        previewPresenter = new PreviewPresenter(this);

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);

        mediaController = new MediaController(getActivity());
        mediaController.setVisibility(View.INVISIBLE);
        audio = (AudioManager) getActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

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
        previewPresenter.onResume();
        seekBar.setProgress(0);
        updateVideoList();

        relativeLayoutPreviewVideoTrim.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onPause() {
        super.onPause();
        previewPresenter.onPause();
        releaseVideoView();
        releaseMusicPlayer();
        projectDuration = 0;
        instantTime = 0;
    }

    @Override
    public void onStop() {
        super.onStop();
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
            musicPlayer = MediaPlayer.create(getActivity(), music.getMusicResourceId());
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
        if(movieList.size() > 0) {
            Video video = seekVideo(instantTime);
            videoToPlay = getPosition(video);
            int timeInMsec = instantTime - videoStartTimeInProject.get(videoToPlay) +
                    movieList.get(videoToPlay).getFileStartTime();

            if(isFullScreenBack) {
                isFullScreenBack = false;
                initVideoPlayer(video, timeInMsec);
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
        preview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, instantTime);
                return false;
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
                } catch(Exception e) {
                    // TODO don't force media player. Media player must be null here
                    seekBar.setProgress(0);
                    playButton.setVisibility(View.VISIBLE);
                }
            }
        });
        preview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, instantTime);
                return false;
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
        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_add_videos), Toast.LENGTH_SHORT).show();
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
        if(movieList.size() > 0)
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

    @OnClick({R.id.edit_button_fullscreen_in})
    public void onClickFullScreenInMode() {
        if(isVideosOnProject()) {
            isFullScreenBack = true;
            Intent i = new Intent(this.getActivity(), VideolistPreviewActivity.class);
            //i.putExtra("TIME", seekBar.getProgress());
            i.putExtra("TIME", 0);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this.getActivity(),
                                new Pair<View, String>(preview, "preview of video"));
                startActivity(i, options.toBundle());
            } else {
                startActivity(i);
            }
        }
    }

    @OnClick({R.id.edit_button_fullscreen_in})
    public void trackClicks(View view) {
        sendButtonTracked(view.getId());
    }


    /**
     * Sends button clicks to Google Analytics
     *
     * @param id the identifier of the clicked button
     */
    private void sendButtonTracked(int id) {
        String label;
        switch (id) {
            case R.id.fragment_navigator_record_button:
                label = "Go to full screen";
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Full Screen")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getActivity().getApplication().getBaseContext()).dispatchLocalHits();
    }

}
