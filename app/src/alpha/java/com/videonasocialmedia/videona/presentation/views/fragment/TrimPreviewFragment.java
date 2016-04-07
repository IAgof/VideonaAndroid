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

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.eventbus.events.project.UpdateProjectDurationEvent;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.TrimPreviewPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.PreviewView;
import com.videonasocialmedia.videona.presentation.mvp.views.TrimView;
import com.videonasocialmedia.videona.presentation.views.customviews.RangeSeekBar;
import com.videonasocialmedia.videona.presentation.views.listener.OnTrimConfirmListener;
import com.videonasocialmedia.videona.utils.Size;
import com.videonasocialmedia.videona.utils.TimeUtils;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.greenrobot.event.EventBus;

/**
 * This class is used to show the right panel of the audio fx menu
 */
public class TrimPreviewFragment extends VideonaFragment implements PreviewView, TrimView,
        RangeSeekBar.OnRangeSeekBarChangeListener, SeekBar.OnSeekBarChangeListener {

    protected Handler handler = new Handler();
    @Bind(R.id.edit_preview_player)
    VideoView preview;
    @Bind(R.id.edit_button_play)
    ImageButton playButton;
    @Bind(R.id.edit_seek_bar)
    SeekBar seekBar;
    @Bind(R.id.edit_text_start_trim)
    TextView startTimeTag;
    @Bind(R.id.edit_text_end_trim)
    TextView stopTimeTag;
    @Bind(R.id.edit_text_time_trim)
    TextView durationTag;
    @Bind(R.id.linearLayoutRangeSeekBar)
    ViewGroup layoutSeekBar;
    @Bind({R.id.imageViewFrame1, R.id.imageViewFrame2, R.id.imageViewFrame3,
            R.id.imageViewFrame4, R.id.imageViewFrame5, R.id.imageViewFrame6, R.id.imageViewFrame7,
            R.id.imageViewFrame8})
    List<ImageView> videoThumbs;

    private final Handler thumbCreationHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            ImageView currentThumb = videoThumbs.get(msg.what);
            currentThumb.setImageBitmap((Bitmap) msg.obj);
            currentThumb.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    };

    RangeSeekBar<Double> trimBar;
    int videoIndexOnTrack;
    private TrimPreviewPresenter presenter;
    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private OnTrimConfirmListener onTrimConfirmListener;
    private Video video;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };
    private int startTimeMs = 0;
    private int finishTimeMs = 0;
    private boolean afterTrimming = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTrimConfirmListener = (OnTrimConfirmListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onTrimConfirmListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_fragment_trim_preview, container, false);
        ButterKnife.bind(this, view);
        presenter = new TrimPreviewPresenter(this, this);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);
        mediaController = new MediaController(getActivity());
        mediaController.setVisibility(View.INVISIBLE);
        videoIndexOnTrack = this.getArguments().getInt("VIDEO_INDEX", 0);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        thumbCreationHandler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.init(videoIndexOnTrack);
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
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

    public void validateTrim() {
        onTrimConfirmListener.onTrimConfirmed();
    }

    @OnClick(R.id.edit_button_play)
    public void playPausePreview() {
        if (videoPlayer != null) {
            if (videoPlayer.isPlaying()) {
                pausePreview();
            } else {
                playPreview();
            }
            updateSeekBarProgress();
        }
    }

    @Override
    public void playPreview() {
        if (videoPlayer != null) {
            if (afterTrimming) {
                videoPlayer.seekTo((int) Math.round(trimBar.getSelectedMinValue()));
                afterTrimming = false;
            }
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
    public void seekTo(int timeInSec) {
        videoPlayer.seekTo(timeInSec);
    }

    @Override
    public void updateVideoList() {
    }

    @Override
    public void showPreview(List<Video> videoList) {
        //showTimeTags(projectDuration);
        video = videoList.get(0);
        seekBar.setMax(video.getDuration());
        initVideoPlayer(video.getMediaPath());
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
                    seekBar.setProgress(videoPlayer.getCurrentPosition() - video.getFileStartTime());
                    videoPlayer.setVolume(0.5f, 0.5f);
                    videoPlayer.setLooping(false);
                    videoPlayer.start();
                    videoPlayer.seekTo(100 + video.getFileStartTime());
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
                    seekBar.setProgress(videoPlayer.getCurrentPosition() - video.getFileStartTime());
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

    @Override
    public void showError(String message) {

    }

    @Override
    public void updateSeekBarDuration(int projectDuration) {

    }

    @Override
    public void updateSeekBarSize() {
        seekBar.setProgress(0);
        seekBar.setMax(video.getDuration());
    }

    private void updateSeekBarProgress() {
        if (videoPlayer != null) {
            if (videoPlayer.isPlaying()) {
                seekBar.setProgress(videoPlayer.getCurrentPosition() - video.getFileStartTime());
                refreshStartTimeTag(videoPlayer.getCurrentPosition());
                if (isEndOfVideo()) {
                    videoPlayer.pause();
                    playButton.setVisibility(View.VISIBLE);
                    refreshStartTimeTag(video.getFileStartTime());
                    seekBar.setProgress(0);
                    videoPlayer.seekTo(video.getFileStartTime());
                }
            }
            handler.postDelayed(updateTimeTask, 20);
        }
    }

    private boolean isEndOfVideo() {
        return seekBar.getProgress() >= video.getDuration();
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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Size thumbSize = determineThumbsSize();
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoPath);
                for (int thumbOrder = 0; thumbOrder < videoThumbs.size(); thumbOrder++) {
                    int frameTime = getFrameTime(videoDuration, thumbOrder, videoThumbs.size());
                    try {
                        Bitmap thumbImage = createVideoThumb(retriever, thumbSize, frameTime);
                        Message msg = thumbCreationHandler.obtainMessage(thumbOrder, thumbImage);
                        msg.sendToTarget();
                    } catch (Exception Exception) {
                        //TODO treat exception properly. Probably do nothing is fine for the time being
                    }
                }
            }
        });
        t.start();
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
        return ( videoDuration / numberOfThumbs ) * thumbOrder * 1000;
    }

    @Override
    public void showTrimBar(int videoFileDuration, int leftMarkerPosition, int rightMarkerPosition) {
        trimBar = new RangeSeekBar<>(
                (double) 0, (double) videoFileDuration, this.getActivity(), videoFileDuration);
        trimBar.setSelectedMinValue((double) leftMarkerPosition);
        startTimeMs = leftMarkerPosition;
        trimBar.setSelectedMaxValue((double) rightMarkerPosition);
        finishTimeMs = rightMarkerPosition;
        trimBar.setOnRangeSeekBarChangeListener(this);
        trimBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (startTimeMs != (int) Math.round(trimBar.getSelectedMinValue())) {
                        startTimeMs = (int) Math.round(trimBar.getSelectedMinValue());
                        presenter.modifyVideoStartTime(startTimeMs);
                    }
                    if (finishTimeMs != (int) Math.round(trimBar.getSelectedMaxValue())) {
                        finishTimeMs = (int) Math.round(trimBar.getSelectedMaxValue());
                        presenter.modifyVideoFinishTime(finishTimeMs);
                    }
                    afterTrimming = true;
                    EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
                }
                return false;
            }
        });
        trimBar.setNotifyWhileDragging(true);
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
    public void onRangeSeekBarValuesChanged(RangeSeekBar trimBar, Object minValue, Object maxValue,
                                            RangeSeekBar.Thumb pressedThumb) {
        if (videoPlayer != null && videoPlayer.isPlaying()) {
            videoPlayer.pause();
        }
        if (pressedThumb == RangeSeekBar.Thumb.MIN)
            videoPlayer.seekTo((int) Math.round((double) minValue));
        else if (pressedThumb == RangeSeekBar.Thumb.MAX)
            videoPlayer.seekTo((int) Math.round((double) maxValue));
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
            afterTrimming = false;
            refreshStartTimeTag(progress + video.getFileStartTime());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public int getCurrentVideoTimeInMsec() {
        return seekBar.getProgress();
    }
}
