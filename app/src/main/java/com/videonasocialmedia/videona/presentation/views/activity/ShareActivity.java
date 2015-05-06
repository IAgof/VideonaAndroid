package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;

/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 * Verónica Lago Fominaya
 */
public class ShareActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    /*VIEWS*/
    @InjectView(R.id.share_button_play)
    ImageButton buttonPlay;
    @InjectView(R.id.share_video_view)
    VideoView videoView;
    @InjectView(R.id.share_seekbar)
    SeekBar seekBar;

    /*CONFIG*/
    private final String LOG_TAG = this.getClass().getSimpleName();

    // Intent
    private static final int CHOOSE_SHARE_REQUEST_CODE = 600;

    //Preview
    private MediaController mediaController;
    private static MediaPlayer mediaPlayer;

    private int durationVideoRecorded;
    private String videoEdited;

    private boolean isRunning = false;

    Uri uri;

    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekProgress();
        }
    };
    /**
     * Tracker google analytics
     */
    private VideonaApplication app;
    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share);

        ButterKnife.inject(this);

        app = (VideonaApplication) getApplication();
        tracker = app.getTracker();


        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);

        mediaController = new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);

        // getting intent data
        Intent in = getIntent();
        videoEdited = in.getStringExtra("MEDIA_OUTPUT");

        // Log.d(LOG_TAG, "VideoEdited " + videoEdited);

        setVideoInfo();

        initMediaPlayer(videoEdited);

        ContentValues content = new ContentValues(4);
        content.put(Video.VideoColumns.TITLE, videoEdited);
        content.put(Video.VideoColumns.DATE_ADDED,
                System.currentTimeMillis() / 1000);
        content.put(Video.Media.MIME_TYPE, "video/mp4");
        content.put(MediaStore.Video.Media.DATA, videoEdited);
        ContentResolver resolver = getContentResolver();
        uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                content);


    }

    @OnClick(R.id.share_button_about)
    public void showAbout() {
        Intent intent = new Intent(ShareActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.share_button_play)
    public void playPauseVideo() {


        if (mediaPlayer.isPlaying()) {

            mediaPlayer.pause();
            buttonPlay.setVisibility(View.VISIBLE);

        } else {

            mediaPlayer.start();
            buttonPlay.setVisibility(View.INVISIBLE);

        }

        updateSeekProgress();


    }

    @OnClick(R.id.share_button_rate_app)
    public void rateApp() {

        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        // Redirect to videozone
        //Uri uri = Uri.parse("market://details?id=" + "com.visiona.videozone");
        Intent rateApp = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(rateApp);
    }

    @OnClick(R.id.share_button_share)
    public void shareVideo() {

        // Log.d(LOG_TAG, "shareClickListener");

        if (mediaPlayer.isPlaying()) {

            mediaPlayer.pause();
            buttonPlay.setVisibility(View.VISIBLE);

        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.share_using)), CHOOSE_SHARE_REQUEST_CODE);

    }


    private void updateSeekProgress() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {

            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(updateTimeTask, 50);

        }
    }

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mediaPlayer.isPlaying() && isRunning) {

                mediaPlayer.pause();

            }


        }

    };


    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    /**
     * Use screen touches to toggle the video between playing and paused.
     */
    @OnTouch(R.id.share_video_view)
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        /*    if (mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
                buttonPlay.setVisibility(View.VISIBLE);

            } else {

                buttonPlay.setVisibility(View.VISIBLE);

            }
         */

            playPauseVideo();

            result = true;

            seekBar.setProgress(mediaPlayer.getCurrentPosition());


        } else {
            result = false;
        }
        return result;
    }


    public void initMediaPlayer(final String videoPath) {


        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        videoView.setVideoPath(videoPath);
        videoView.setMediaController(mediaController);
        videoView.canSeekBackward();
        videoView.canSeekForward();

        videoView.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                mediaPlayer = mp;

                seekBar.setMax(durationVideoRecorded * 1000);
                seekBar.setProgress(mediaPlayer.getCurrentPosition());

                mediaPlayer.start();

                mediaPlayer.seekTo(100);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mediaPlayer.pause();

            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                // Log.d(LOG_TAG, "EditVideoActivity setOnCompletionListener");

                buttonPlay.setVisibility(View.VISIBLE);

                updateSeekProgress();

            }

        });

        videoView.requestFocus();
    }


    /*@Override
    public void onBackPressed() {

            setResult(Activity.RESULT_OK);

            finish();

    }*/

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            buttonPlay.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
        } else {
            // Log.d(LOG_TAG, "requestCode " + requestCode + "resultCode " + resultCode + "intent data " + data.getDataString());
            if (requestCode == CHOOSE_SHARE_REQUEST_CODE) {
                //setResult(Activity.RESULT_OK);
                //finish();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            buttonPlay.setVisibility(View.VISIBLE);
        }
        updateSeekProgress();
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
    }

    private void setVideoInfo() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoEdited);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(time);
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        durationVideoRecorded = (int) duration;
    }

    @OnClick({R.id.share_button_share, R.id.share_button_rate_app})
    public void clickListener(View view) {
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
            case R.id.share_button_share:
                label = "Share video";
                break;
            case R.id.share_button_rate_app:
                label = "Vote app";
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("ShareActivity")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getApplication().getBaseContext()).dispatchLocalHits();
    }
}
