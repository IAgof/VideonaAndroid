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
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.TrimPreviewPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.PreviewView;
import com.videonasocialmedia.videona.presentation.mvp.views.TrimView;
import com.videonasocialmedia.videona.presentation.views.customviews.RangeSeekBar;
import com.videonasocialmedia.videona.utils.Size;
import com.videonasocialmedia.videona.utils.TimeUtils;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * This class is used to show the right panel of the audio fx menu
 */
public class TrimPreviewFragment extends Fragment implements PreviewView, TrimView, RangeSeekBar.OnRangeSeekBarChangeListener, SeekBar.OnSeekBarChangeListener {

    protected Handler handler = new Handler();
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
    @InjectView(R.id.linearLayoutRangeSeekBar)
    ViewGroup layoutSeekBar;
    @InjectViews({R.id.imageViewFrame1, R.id.imageViewFrame2, R.id.imageViewFrame3,
            R.id.imageViewFrame4, R.id.imageViewFrame5, R.id.imageViewFrame6})
    List<ImageView> videoThumbs;
    RangeSeekBar<Double> trimBar;
    int videoIndexOnTrack;
    private TrimPreviewPresenter presenter;
    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };

    /**
     * Tracker google analytics
     */
    private Tracker tracker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trim_preview, container, false);
        ButterKnife.inject(this, view);
        VideonaApplication app = (VideonaApplication) getActivity().getApplication();
        tracker = app.getTracker();
        presenter = new TrimPreviewPresenter(this, this);
        seekBar.setProgress(0);
        mediaController = new MediaController(getActivity());
        mediaController.setVisibility(View.INVISIBLE);
        videoIndexOnTrack = this.getArguments().getInt("VIDEO_INDEX", 0);
        presenter.init(videoIndexOnTrack);
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
        //updateVideoList();
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
            playButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void pause() {
        if (videoPlayer != null && videoPlayer.isPlaying())
            videoPlayer.pause();
        playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void seekTo(int timeInSec) {
        videoPlayer.seekTo(timeInSec);
    }

    @Override
    public void updateVideoList() {
        presenter.update();
    }

    @Override
    public void showPreview(List<Video> videoList) {
        //showTimeTags(projectDuration);
        Video video = videoList.get(0);
        seekBar.setMax(video.getFileDuration());
        initVideoPlayer(videoList.get(0).getMediaPath());
    }

    private void showTimeTags(int duration) {
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
                    pause();
                    updateSeekBarProgress();
                }
            });
            preview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                   /* videoToPlay++;
                    if(videoToPlay < movieList.size()) {
                        initVideoPlayer(movieList.get(videoToPlay));
                    }

                    if(videoToPlay == movieList.size()) {
                        playButton.setVisibility(View.VISIBLE);
                        releaseVideoView();
                        videoToPlay = 0;
                        initVideoPlayer(movieList.get(videoToPlay));
                    }*/
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
            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    @Override
    public void showError(String message) {

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
    }

    public void refreshStartTimeTag(int time) {
        startTimeTag.setText(TimeUtils.toFormattedTime(time));
    }

    public void refreshStopTimeTag(int time) {
        stopTimeTag.setText(TimeUtils.toFormattedTime(time));
    }

    public void refreshDurationTag(int duration) {
        durationTag.setText(TimeUtils.toFormattedTime(duration));
    }


    @Override
    public void createAndPaintVideoThumbs(final String videoPath, final int videoDuration) {
        Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {
                Size thumbSize = determineThumbsSize();

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoPath);
                for (int thumbOrder = 0; thumbOrder < videoThumbs.size(); thumbOrder++) {
                    int frameTime = getFrameTime(videoDuration, thumbOrder, videoThumbs.size());
                    try {
                        Bitmap thumbImage = createVideoThumb(retriever, thumbSize, frameTime);
                        ImageView currentThumb = videoThumbs.get(thumbOrder);
                        currentThumb.setImageBitmap(thumbImage);
                        currentThumb.setScaleType(ImageView.ScaleType.FIT_XY);
                    } catch (Exception Exception) {
                        //TODO treat exception properly. Probably do nothing is fine for the time being
                    }
                }
            }
        });
    }

    private Size determineThumbsSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screnWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        int numberOfThumbs = videoThumbs.size();
        int width_opt = screnWidth / numberOfThumbs;
        return new Size(width_opt, screenHeight);
    }

    private Bitmap createVideoThumb(MediaMetadataRetriever retriever, Size size, int frameTime) throws Exception {
        Bitmap bitmap = retriever.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        if (bitmap == null)
            bitmap = retriever.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_CLOSEST);
        if (bitmap == null)
            bitmap = retriever.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_NEXT_SYNC);
        if (bitmap == null)
            bitmap = retriever.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
        if (bitmap == null) {
            throw new Exception();
        }
        return Bitmap.createScaledBitmap(bitmap, size.getWidth(), size.getHeight(), false);
    }

    private int getFrameTime(int videoDuration, int thumbOrder, int numberOfThumbs) {
        return (videoDuration / numberOfThumbs) * thumbOrder * 1000;
    }

    @Override
    public void showTrimBar(int videoFileDuration, int leftMarkerPosition, int RightMarkerPosition) {
        trimBar = new RangeSeekBar<>(
                (double) 0, (double) videoFileDuration, this.getActivity(), videoFileDuration);
        trimBar.setSelectedMinValue((double) leftMarkerPosition);
        trimBar.setSelectedMaxValue((double) RightMarkerPosition);
        trimBar.setOnRangeSeekBarChangeListener(this);
        layoutSeekBar.addView(trimBar);
    }

    /**
     * Listens to trimBar events.
     * <p/>
     * modifies the video start time and the video finishTime
     *
     * @param trimBar
     * @param minValue
     * @param maxValue
     */
    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar trimBar, Object minValue, Object maxValue) {
        int startTimeMs = (int) Math.round((double) minValue);
        int finishTimeMs = (int) Math.round((double) maxValue);
        presenter.modifyVideoStartTime(startTimeMs);
        presenter.modifyVideoFinishTime(finishTimeMs);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            videoPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @OnClick({R.id.edit_button_full_screen})
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
