package com.videonasocialmedia.videona.presentation.views.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.VideonaMainActivity;
import com.videonasocialmedia.videona.utils.CutVideoPlayerState;
import com.videonasocialmedia.videona.utils.ConfigUtils;
import com.videonasocialmedia.videona.utils.utils.ConstantsUtils;
import com.videonasocialmedia.videona.utils.RangeSeekBar;
import com.videonasocialmedia.videona.utils.UserPreferences;
import com.videonasocialmedia.videona.utils.TimeUtils;
import com.videonasocialmedia.videona.utils.VideoUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by amm on 5/09/14.
 */

public class EditActivity extends Activity {


    private Uri fileUri; // file url to store image/video

    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final int VIDEO_SHARE_REQUEST_CODE = 500;
    private static final int ADD_MUSIC_REQUEST_CODE = 600;

    public static VideoView videoView;

    private MediaController mediaController;

    private TextView detailVideoCut;
    private TextView detailVideoSeek;
    private TextView detailViewDetails;
    private CutVideoPlayerState cutvideoPlayerState = new CutVideoPlayerState();

    public static int seekBarStart = 0;
    public static int seekBarEnd = 0;
    private static int videoProgress = 0;

    // public static int seekBar = 0;
    public static int seekBarPositionFull;

    private int progress;

    private boolean isRunning = false;

    private ViewGroup layoutSeekBar;

    private int durationVideoCut = 0;
    private int durationVideoOriginal = 0;

    public static String videoRecorded;
    public static String videoTrim;
    public static String pathvideoTrim;
    public static int durationVideoRecorded;

    private static String musicSelected;

    private ProgressDialog progressDialog;

    // Buttons
    private Button btnTrim;
    private ImageButton btnMusic;
    private ImageButton btnPlay;

    private Typeface tf;

    private SeekBar seekBar;

    private LinearLayout linearLayoutFrames;

    private Handler mHandler = new Handler();
    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            updateSeekProgress();
        }
    };


    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private ImageView image5;
    private ImageView image6;
    private ImageView image7;
    private ImageView image8;
    private ImageView image9;

    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;
    private ProgressBar progressBar4;
    private ProgressBar progressBar5;
    private ProgressBar progressBar6;
    private ProgressBar progressBar7;
    private ProgressBar progressBar8;
    private ProgressBar progressBar9;

    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerMusic;

    private int musicRawSelected;

    private boolean isVideoMute = false;
    private boolean isMusicON = false;

    UserPreferences appPrefs;

    // create RangeSeekBar as Double
    RangeSeekBar<Double> seekBarRange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Keep screen ON
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");


        image1 = (ImageView) findViewById(R.id.imageViewFrame1);
        image2 = (ImageView) findViewById(R.id.imageViewFrame2);
        image3 = (ImageView) findViewById(R.id.imageViewFrame3);
        image4 = (ImageView) findViewById(R.id.imageViewFrame4);
        image5 = (ImageView) findViewById(R.id.imageViewFrame5);
        image6 = (ImageView) findViewById(R.id.imageViewFrame6);
        image7 = (ImageView) findViewById(R.id.imageViewFrame7);
        image8 = (ImageView) findViewById(R.id.imageViewFrame8);
        image9 = (ImageView) findViewById(R.id.imageViewFrame9);

        progressBar1 = (ProgressBar) findViewById(R.id.progressBarImageFrame1);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBarImageFrame2);
        progressBar3 = (ProgressBar) findViewById(R.id.progressBarImageFrame3);
        progressBar4 = (ProgressBar) findViewById(R.id.progressBarImageFrame4);
        progressBar5 = (ProgressBar) findViewById(R.id.progressBarImageFrame5);
        progressBar6 = (ProgressBar) findViewById(R.id.progressBarImageFrame6);
        progressBar7 = (ProgressBar) findViewById(R.id.progressBarImageFrame7);
        progressBar8 = (ProgressBar) findViewById(R.id.progressBarImageFrame8);
        progressBar9 = (ProgressBar) findViewById(R.id.progressBarImageFrame9);

        appPrefs = new UserPreferences(getApplicationContext());
        appPrefs.setIsMusicON(false);

        videoView = (VideoView) findViewById(R.id.videoView);

        mediaController = new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);

        mediaPlayer = new MediaPlayer();

        appPrefs.setVideoProgress(videoProgress);

        // getting intent data
        Intent in = getIntent();

        // Test pruebas 2 min
        //  videoRecorded = Environment.getExternalStorageDirectory() + File.separator + "amm/dosminutos.mp4";
        //  videoRecorded = Environment.getExternalStorageDirectory() + File.separator + "Videona/vlc-player.mp4";

        Log.d(LOG_TAG, " videoRecorded " + videoRecorded + " vs " + in.getStringExtra("MEDIA_OUTPUT"));
        videoRecorded = in.getStringExtra("MEDIA_OUTPUT");

        videoTrim = "V_TRIM_" + new File(videoRecorded).getName().substring(4);

        detailVideoCut = (TextView) findViewById(R.id.DetailsVideoCut);
        detailVideoCut.setBackgroundColor(Color.TRANSPARENT);
        detailVideoCut.setTypeface(tf);


        layoutSeekBar = (ViewGroup) findViewById(R.id.linearLayoutRangeSeekBar);

        linearLayoutFrames = (LinearLayout) findViewById(R.id.linearLayoutFrames);

        progressDialog = new ProgressDialog(EditActivity.this);

        seekBar = (SeekBar) findViewById(R.id.seekBarPoint);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayerMusic != null) {
                        if (mediaPlayerMusic.isPlaying()) {
                            mediaPlayerMusic.pause();
                        }
                    }

                    mediaPlayer.pause();
                    videoProgress = mediaPlayer.getCurrentPosition();
                    appPrefs.setVideoProgress(videoProgress);

                    btnPlay.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                if (fromUser) {

                    setEditVideoProgress(progress, seekBarStart, seekBarEnd);

                }
            }
        });


        btnTrim = (Button) findViewById(R.id.imageButtonCut);
        btnTrim.setOnClickListener(trimClickListener());

        btnMusic = (ImageButton) findViewById(R.id.imageButtonMusic);
        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.pause();
                videoProgress = mediaPlayer.getCurrentPosition();

                if (mediaPlayerMusic != null) {
                    // mediaPlayerMusic.stop();
                    mediaPlayerMusic.release();
                    mediaPlayerMusic = null;
                }


                if (isMusicON) {
                    btnMusic.setImageResource(R.drawable.activity_edit_icon_add_music_normal);

                    isMusicON = false;

                    appPrefs.setIsMusicON(false);

                    appPrefs.setSeekBarStart(seekBarStart);
                    appPrefs.setSeekBarEnd(seekBarEnd);
                    appPrefs.setVideoProgress(videoProgress);

                    seekBar.setProgress(videoProgress);


                } else {

                    Intent music = new Intent();
                    music.setClass(EditActivity.this, MusicActivity.class);
                    startActivityForResult(music, ADD_MUSIC_REQUEST_CODE);

                }


            }
        });

        btnPlay = (ImageButton) findViewById(R.id.imageButtonPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnPlay.setVisibility(View.INVISIBLE);

                mediaPlayer.start();

                if (mediaPlayerMusic != null) {
                    mediaPlayerMusic.start();
                }

                updateSeekProgress();

            }
        });


    }  // onCreate

    private void updateSeekProgress() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {

            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(r, 50);

            if (isMusicON) {

                if ((int) (mediaPlayer.getCurrentPosition() / 1000) == seekBarEnd) {

                    Log.d(LOG_TAG, "EditVideoActivity seekBarEnd Mute OFF");

                    if (isVideoMute) {

                        mediaPlayerMusic.stop();
                        mediaPlayerMusic.release();
                        mediaPlayerMusic = null;

                        mediaPlayer.setVolume(0.5f, 0.5f);

                        isVideoMute = false;

                    }

                }

                if ((int) (mediaPlayer.getCurrentPosition() / 1000) == seekBarStart) {

                    Log.d(LOG_TAG, "EditVideoActivity seekBarStart Mute ON");

                    if (!isVideoMute) {

                        mediaPlayerMusic = MediaPlayer.create(getBaseContext(), musicRawSelected);
                        mediaPlayerMusic.start();
                        mediaPlayerMusic.setVolume(5.0f, 5.0f);

                        mediaPlayer.setVolume(0f, 0f);

                        isVideoMute = true;

                    }

                }
            }
        }
    }

    private void setEditVideoProgress(int videoProgress, int seekBarStart, int seekBarEnd) {

        videoProgress = videoProgress;

        Log.d(LOG_TAG, "setEditVideoProgress " + "videoProgress " + videoProgress + " seekBarStart " + seekBarStart + " seekBarEnd " + seekBarEnd);

        if (isMusicON) {

            if (videoProgress >= seekBarStart * 1000 && videoProgress <= seekBarEnd * 1000) {

                mediaPlayer.setVolume(0f, 0f);
                isVideoMute = true;

                if (mediaPlayerMusic == null) {
                    mediaPlayerMusic = MediaPlayer.create(getBaseContext(), musicRawSelected);
                    mediaPlayerMusic.setVolume(5.0f, 5.0f);


                } else {
                    if (mediaPlayerMusic.isPlaying()) {
                        mediaPlayerMusic.pause();
                    }

                }

                mediaPlayerMusic.seekTo(videoProgress - seekBarStart * 1000);


            } else {

                if (isVideoMute) {
                    isVideoMute = false;
                    mediaPlayer.setVolume(0.5f, 0.5f);
                }

                if (mediaPlayerMusic != null) {
                    mediaPlayerMusic.stop();
                    mediaPlayerMusic.release();
                    mediaPlayerMusic = null;
                }

            }

        }


        mediaPlayer.seekTo(videoProgress);


    }


    private void setVideoInfo() {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoRecorded);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(time);
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);

        durationVideoRecorded = (int) duration;

        appPrefs.setVideoDuration(durationVideoRecorded);


    }

    private View.OnClickListener trimClickListener() {
        return new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View arg0) {

                Log.d(LOG_TAG, "trimClickListener");

                mediaPlayer.pause();

                videoView.stopPlayback();

                if (mediaPlayer != null) {
                    // mediaPlayer.stop();

                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                if (mediaPlayerMusic != null) {
                    // mediaPlayerMusic.stop();
                    mediaPlayerMusic.release();
                    mediaPlayerMusic = null;
                }


                //  progressDialog = ProgressDialog.show(getParent(), getString(R.string.dialog_processing), getString(R.string.please_wait), true);

                progressDialog.setMessage(getString(R.string.dialog_processing));
                progressDialog.setTitle(getString(R.string.please_wait));
                progressDialog.setIndeterminate(true);
                progressDialog.show();


                // Custom progress dialog
                progressDialog.setIcon(R.drawable.activity_edit_icon_cut_normal);


                ((TextView) progressDialog.findViewById(Resources.getSystem()
                        .getIdentifier("message", "id", "android")))
                        .setTypeface(tf);
                ((TextView) progressDialog.findViewById(Resources.getSystem()
                        .getIdentifier("message", "id", "android")))
                        .setTextColor(Color.WHITE);
                ((TextView) progressDialog.findViewById(Resources.getSystem()
                        .getIdentifier("alertTitle", "id", "android")))
                        .setTypeface(tf);
                ((TextView) progressDialog.findViewById(Resources.getSystem()
                        .getIdentifier("alertTitle", "id", "android")))
                        .setTextColor(Color.WHITE);

                progressDialog.findViewById(
                        Resources.getSystem().getIdentifier("topPanel", "id",
                                "android")).setBackgroundColor(getResources().getColor(R.color.videona_blue_1));
                progressDialog.findViewById(
                        Resources.getSystem().getIdentifier("customPanel", "id",
                                "android"))
                        .setBackgroundColor(getResources().getColor(R.color.videona_blue_1));


                final Runnable r = new Runnable() {
                    public void run() {

                        doTrimVideo();


                        Intent share = new Intent();
                        share.putExtra("MEDIA_OUTPUT", pathvideoTrim);
                        share.setClass(EditActivity.this, ShareActivity.class);
                        startActivityForResult(share, VIDEO_SHARE_REQUEST_CODE);

                    }
                };

                performOnBackgroundThread(r);

            }

        };
    }

    //stop playback when out of thumb bounds
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mediaPlayer.isPlaying() && isRunning) {
                // stop playback and set playback position on the end of trim
                // border
                mediaPlayer.pause();

                mediaPlayer.seekTo(seekBarEnd * 1000);
            }
        }

    };


    private void doTrimVideo() {

        trimVideo();

        // renameTrimVideo, to overwrite videoRecorded file. Future use.
        // renameTrimVideo(videoRecorded);

        // Return value to 0, needed if you trim two o more videos 
        seekBarStart = 0;

        appPrefs = new UserPreferences(getApplicationContext());

        if (appPrefs.getIsMusicON()) {

            try {

                //  String audio_test = Environment.getExternalStorageDirectory() + "/Videona/audio_m4a.m4a";
                //  VideoUtils.switchAudio(pathvideoTrim, audio_test, Config.videoMusicTempFile);

                VideoUtils.switchAudio(pathvideoTrim, musicSelected, ConstantsUtils.videoMusicTempFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

            appPrefs.setSeekBarStart(seekBarStart);
            appPrefs.setSeekBarEnd(seekBarEnd);

            appPrefs.setVideoMusicAux(pathvideoTrim);

            killProcess();

        }


        this.runOnUiThread(new Runnable() {
            public void run() {

                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), getString(R.string.toast_trim), Toast.LENGTH_SHORT).show();

            }
        });
    }


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

    private void killProcess() {
        System.gc();
        int pid = android.os.Process.myPid();

        android.os.Process.killProcess(pid);
        System.out.println(" killProcess ");
    }


    private void trimVideo() {


        int start = appPrefs.getSeekBarStart();

        int length = appPrefs.getSeekBarEnd() - start;

        String inputFileName = videoRecorded;

        pathvideoTrim = ConstantsUtils.pathVideoTrim + File.separator + videoTrim;

        Log.d(LOG_TAG, "VideonaMainActivity input " + inputFileName + " output " + pathvideoTrim + " start " + start + " length " + length);

        VideonaMainActivity.cut(inputFileName, pathvideoTrim, start, length);


    }

    private void renameTrimVideo(String videoTrim) {

        String newVideoTrim = videoTrim;
        String videoTrimAux = ConstantsUtils.pathApp + ConstantsUtils.videoCutAuxName;

        File originalVideo = new File(videoTrim);
        if (originalVideo.exists()) {
            originalVideo.delete();
        }

        File newTrimVideo = new File(videoTrimAux);
        newTrimVideo.renameTo(originalVideo);

    }

    /**
     * Previewing recorded video
     */
    public void previewVideo() {


        Log.d(LOG_TAG, "previewVideo");

        try {

            videoView.setVideoPath(videoRecorded);
            videoView.setMediaController(mediaController);
            videoView.canSeekBackward();
            videoView.canSeekForward();


            videoView.setOnPreparedListener(new OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {

                    Log.d(LOG_TAG, "EditVideoActivity setOnPreparedListener onPrepared");

                    if (!isMusicON) {

                        paintSeekBar();

                    }

                    int duration = durationVideoRecorded * 1000;

                    seekBar.setMax(duration);

                    mediaPlayer = mp;

                    mediaPlayer.setVolume(0.5f, 0.5f);
                    mediaPlayer.setLooping(false);

                    mediaPlayer.start();

                    mediaPlayer.seekTo(500);

                    mediaPlayer.pause();

                }
            });


            // to start video on time 500ms, avoid black screen
            //videoView.seekTo(500);


            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    Log.d(LOG_TAG, "EditVideoActivity setOnCompletionListener");

                    btnPlay.setVisibility(View.VISIBLE);

                    videoProgress = 0;

                    if (mediaPlayerMusic != null) {
                        mediaPlayerMusic.release();
                        mediaPlayerMusic = null;
                    }
                }
            });

            videoView.requestFocus();

            refreshDetailView();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Use screen touches to toggle the video between playing and paused.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (mediaPlayer.isPlaying()) {
                if (mediaPlayerMusic != null) {
                    if (mediaPlayerMusic.isPlaying()) {
                        mediaPlayerMusic.pause();
                    }
                }
                mediaPlayer.pause();
                btnPlay.setVisibility(View.VISIBLE);

                videoProgress = mediaPlayer.getCurrentPosition();
                appPrefs.setVideoProgress(videoProgress);
                seekBar.setProgress(videoProgress);


            } else {

                btnPlay.setVisibility(View.VISIBLE);

            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return cutvideoPlayerState;
    }

    @Override
    protected void onResume() {
        super.onResume();

        setVideoInfo();

        Log.d(LOG_TAG, " onResume isMusicON " + isMusicON);

        if (isMusicON) {

            mediaPlayer = new MediaPlayer();

            mediaPlayer.reset();


            seekBarStart = appPrefs.getSeekBarStart();

            seekBarEnd = Math.min(appPrefs.getSeekBarEnd(), seekBarStart + ConfigUtils.maxDurationVideo);

            videoView.seekTo(appPrefs.getVideoProgress());

            setEditVideoProgress(appPrefs.getVideoProgress(), seekBarStart, seekBarEnd);


        } else {

            previewVideo();

            seekBarEnd = durationVideoRecorded;

        }

        refreshDetailView();

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "onPause");

        appPrefs.setSeekBarStart(seekBarStart);
        appPrefs.setSeekBarEnd(seekBarEnd);
        appPrefs.setVideoProgress(videoProgress);
        appPrefs.setVideoDuration(durationVideoRecorded);

        seekBar.setProgress(videoProgress);

    }

    public void onStart() {
        super.onStart();

        Log.d(LOG_TAG, "onStart");

        seekBarEnd = durationVideoRecorded;

        isRunning = true;

        PaintFramesTask task = new PaintFramesTask();
        task.execute();


    }

    private void paintFramesVideo(String pathVideoName) {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels - 100;
        int height = metrics.heightPixels;

        Log.d("MainActivity", "screen size width: " + width + " " + "heigh " + height);

        int width_opt = (int) width / 9;
        //int height_opt = (int) ((width_opt * 9) / 16); // 16:9
        //int height_opt = (int) ((width_opt * 3) /4); // 4:3

        //OJO Pablo esta jugando!!!//
        int height_opt = width_opt;

        Log.d("MainActivity", "screen size width_opt: " + width_opt + " " + "height_opt " + height_opt);

        File videoFile = new File(pathVideoName);


        Uri videoFileUri = Uri.parse(videoFile.toString());

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoFile.getAbsolutePath());
        //ArrayList<Bitmap> rev=new ArrayList<Bitmap>();

        //Create a new Media Player
        MediaPlayer mp = MediaPlayer.create(getBaseContext(), videoFileUri);

        // Fail when come back from music, nexus 4.
        int millis = mp.getDuration();


     /*   linearLayoutImages.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                height_opt));*/

        // Get 9 key frames from video in separate time
        for (int j = 1; j < 10; j++) {
            int value = j;
            Bitmap bitmap = retriever.getFrameAtTime((int) (millis / 9) * 1000 * value, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            Bitmap bmpScaledSize = Bitmap.createScaledBitmap(bitmap, width_opt, height_opt, false);
            switch (value) {
                case 1:

                    progressBar1.setVisibility(View.INVISIBLE);
                    image1.setVisibility(View.VISIBLE);
                    image1.setImageBitmap(bmpScaledSize);

                    break;

                case 2:

                    progressBar2.setVisibility(View.INVISIBLE);
                    image2.setVisibility(View.VISIBLE);
                    image2.setImageBitmap(bmpScaledSize);

                    break;

                case 3:

                    progressBar3.setVisibility(View.INVISIBLE);
                    image3.setVisibility(View.VISIBLE);
                    image3.setImageBitmap(bmpScaledSize);

                    break;

                case 4:

                    progressBar4.setVisibility(View.INVISIBLE);
                    image4.setVisibility(View.VISIBLE);
                    image4.setImageBitmap(bmpScaledSize);

                    break;

                case 5:

                    progressBar5.setVisibility(View.INVISIBLE);
                    image5.setVisibility(View.VISIBLE);
                    image5.setImageBitmap(bmpScaledSize);

                    break;

                case 6:

                    progressBar6.setVisibility(View.INVISIBLE);
                    image6.setVisibility(View.VISIBLE);
                    image6.setImageBitmap(bmpScaledSize);

                    break;

                case 7:

                    progressBar7.setVisibility(View.INVISIBLE);
                    image7.setVisibility(View.VISIBLE);
                    image7.setImageBitmap(bmpScaledSize);

                    break;

                case 8:

                    progressBar8.setVisibility(View.INVISIBLE);
                    image8.setVisibility(View.VISIBLE);
                    image8.setImageBitmap(bmpScaledSize);

                    break;

                case 9:

                    progressBar9.setVisibility(View.INVISIBLE);
                    image9.setVisibility(View.VISIBLE);
                    image9.setImageBitmap(bmpScaledSize);

                    break;

                // linearLayoutFrames.addView(image);


            }

        }
    }

    public void paintSeekBar() { // Initialize seekbar

        Log.d(LOG_TAG, "paintSeekBar Activity");

        durationVideoCut = videoView.getDuration() / 1000;

        durationVideoOriginal = durationVideoCut;

        Log.d(LOG_TAG, "duracionvideo getDuration() " + durationVideoCut);

        seekBarStart = 0;
        seekBarEnd = Math.min(durationVideoCut, seekBarStart + ConfigUtils.maxDurationVideo);

        seekBarRange = new RangeSeekBar<Double>(
                (double) 0, (double) durationVideoCut, getBaseContext()
                .getApplicationContext());


        seekBarRange.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Double>() {

            @Override
            public void onRangeSeekBarValuesChanged(
                    RangeSeekBar<?> bar, Double minValue,
                    Double maxValue) {


                Log.d(LOG_TAG, " RangeSeekBar minValue " + minValue + " maxValue " + maxValue);

                isRunning = false; // set flag to prevent collision with
                // get playback position and trip borders

                seekBarStart = (int) Math.round(minValue);

                //seekBarEnd = (int) Math.round(maxValue);
                seekBarEnd = Math.min((int) Math.round(maxValue), seekBarStart + ConfigUtils.maxDurationVideo);


                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayerMusic != null) {
                        if (mediaPlayerMusic.isPlaying()) {
                            mediaPlayerMusic.pause();
                        }
                    }

                    mediaPlayer.pause();

                    btnPlay.setVisibility(View.VISIBLE);

                }


                appPrefs.setSeekBarStart(seekBarStart);
                appPrefs.setSeekBarEnd(seekBarEnd);


                refreshDetailView();


                isRunning = true; // free flag to prevent collision with
                // get playback position and trip borders
            }

        });

        layoutSeekBar.addView(seekBarRange);


    }

    private void refreshDetailView() {

        Log.d(LOG_TAG, "refreshDetailView");

        int startSeekBar = seekBarStart;
        int stopSeekBar = Math.min(seekBarEnd, seekBarStart + ConfigUtils.maxDurationVideo);
        String start = TimeUtils.toFormattedTime(startSeekBar * 1000);
        String stop = TimeUtils.toFormattedTime(stopSeekBar * 1000);


        appPrefs.setSeekBarStart(startSeekBar);
        appPrefs.setSeekBarEnd(stopSeekBar);

        durationVideoCut = stopSeekBar - startSeekBar;

        appPrefs.setVideoDurationTrim(durationVideoCut);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOG_TAG, " requestCode " + requestCode + " resultCode " + resultCode);

        if (requestCode == VIDEO_SHARE_REQUEST_CODE) {

            setResult(Activity.RESULT_OK);

            finish();

        }

        if (requestCode == ADD_MUSIC_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_CANCELED) {

                onResume();

                return;
            }

            int music = appPrefs.getPositionMusic();

            int drawableImageMusic = data.getIntExtra("music_image", 0);

            musicRawSelected = data.getIntExtra("music_audio", 0);

            btnMusic.setImageDrawable(getResources().getDrawable(drawableImageMusic));

            musicSelected = Environment.getExternalStorageDirectory() + File.separator + "Videona/.temp/" + mThumbPathAudioSelected[music];

            Log.d(LOG_TAG, "music selected position " + music + " music_audio " + musicRawSelected + " music_image " + drawableImageMusic);

            isMusicON = true;

            appPrefs.setIsMusicON(true);

        }


    }

    private String[] mThumbPathAudioSelected = {
            "audio_folk.m4a", "audio_hiphop.m4a",
            "audio_pop.m4a", "audio_reggae.m4a",
            "audio_rock.m4a", "audio_clasica_piano.m4a",
            "audio_clasica_violin.m4a", "audio_clasica_flauta.m4a",
            "audio_ambiental.m4a"
    };

    private class PaintFramesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    paintFramesVideo(videoRecorded);
                }
            });

            return " null ";
        }

        @Override
        protected void onPostExecute(String result) {


        }

    }


}
