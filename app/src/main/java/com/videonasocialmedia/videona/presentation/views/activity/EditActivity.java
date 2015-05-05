/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas Abascal
 * Verónica Lago Fominaya
 * Álvaro Martínez Marco
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.presenters.EditPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;
import com.videonasocialmedia.videona.presentation.views.fragment.AudioFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.LookFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.MusicCatalogFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.ScissorsFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.VideoFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectMenuSelectedListener;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerClickListener;
import com.videonasocialmedia.videona.utils.ConfigUtils;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.RangeSeekBar;
import com.videonasocialmedia.videona.utils.TimeUtils;
import com.videonasocialmedia.videona.utils.UserPreferences;
import com.videonasocialmedia.videona.utils.VideoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;


/**
 * @author Juan Javier Cabanas Abascal
 */
public class EditActivity extends Activity implements EditorView, OnEffectMenuSelectedListener, RecyclerClickListener, SeekBar.OnSeekBarChangeListener, RangeSeekBar.OnRangeSeekBarChangeListener {

    private final String LOG_TAG = "EDIT ACTIVITY";

    @InjectView(R.id.edit_button_fx)
    ImageButton videoFxButton;
    @InjectView(R.id.edit_button_look)
    ImageButton lookFxButton;
    @InjectView(R.id.edit_button_scissor)
    ImageButton scissorButton;
    @InjectView(R.id.edit_button_audio)
    ImageButton audioFxButton;
    @InjectView(R.id.edit_preview_player)
    VideoView preview;
    @InjectView(R.id.edit_button_play)
    ImageButton playButton;
    @InjectView(R.id.edit_seek_bar)
    SeekBar seekBar;
    
        /*@InjectView(R.id.buttonCancelEditActivity)
    ImageButton buttonCancelEditActivity;
    @InjectView(R.id.buttonOkEditActivity)
    ImageButton buttonOkEditActivity;*/


    /*Preview*/
    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private MediaPlayer musicPlayer;

    /*Navigation*/
    private VideoFxMenuFragment videoFxMenuFragment;
    private AudioFxMenuFragment audioFxMenuFragment;
    private ScissorsFxMenuFragment scissorsFxMenuFragment;
    private LookFxMenuFragment lookFxMenuFragment;
    private MusicCatalogFragment musicCatalogFragment;

    /*mvp*/
    private EditPresenter editPresenter;

    /**
     * Tracker google analytics
     */
    private VideonaApplication app;
    private Tracker tracker;

    /* TextView trimVideo*/
    @InjectView(R.id.edit_text_start_trim)
    TextView textStartTrim;
    @InjectView(R.id.edit_text_end_trim)
    TextView textEndTrim;
    @InjectView(R.id.edit_text_time_trim)
    TextView textTimeTrim;


    //TODO de aquí en adelante hay que cambiarlo todo!!!!!

    Music selectedMusic;

    private boolean isMusicON = false;
    private boolean isOnTrimming = false;

    private static final int VIDEO_SHARE_REQUEST_CODE = 500;
    private static final int ADD_MUSIC_REQUEST_CODE = 600;

    public static int seekBarStart = 0;
    public static int seekBarEnd = 0;
    private static int videoProgress = 0;

    private boolean isRunning = false;

    private ViewGroup layoutSeekBar;

    private RelativeLayout relativeLayoutPreviewVideo;

    private FrameLayout edit_bottom_panel;

    private int durationVideoCut = 0;

    public static String videoRecorded;
    public static String videoTrim;
    public static String pathvideoTrim;
    public static int durationVideoRecorded;
    private static String musicSelected;


    private ProgressDialog progressDialog;

    private LinearLayout linearLayoutFrames;

    @InjectView(R.id.imageViewFrame1)
    ImageView image1;
    @InjectView(R.id.imageViewFrame2)
    ImageView image2;
    @InjectView(R.id.imageViewFrame3)
    ImageView image3;
    @InjectView(R.id.imageViewFrame4)
    ImageView image4;
    @InjectView(R.id.imageViewFrame5)
    ImageView image5;
    @InjectView(R.id.imageViewFrame6)
    ImageView image6;
    @InjectView(R.id.imageViewFrame7)
    ImageView image7;
    @InjectView(R.id.imageViewFrame8)
    ImageView image8;
    @InjectView(R.id.imageViewFrame9)
    ImageView image9;

    private int musicRawSelected;

    UserPreferences appPrefs;
    // create RangeSeekBar as Double
    RangeSeekBar<Double> seekBarRange;

    // Adapt navigation to beta April 22nd
    boolean isButtonAudioPressed = false;


    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekProgress();
        }
    };


    public EditActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.inject(this);

        app = (VideonaApplication) getApplication();
        tracker = app.getTracker();

        editPresenter = new EditPresenter(this);


        //TODO mover a donde se deba
        /// amm Start with ScissorFx
        scissorsFxMenuFragment = new ScissorsFxMenuFragment();
        audioFxMenuFragment = new AudioFxMenuFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.edit_right_panel, audioFxMenuFragment).commit();
        this.onEffectMenuSelected();
        //ft.add(R.id.edit_right_panel, scissorsFxMenuFragment).commit();

        this.initVideoPlayer(this.getIntent().getStringExtra("MEDIA_OUTPUT"));

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);

        appPrefs = new UserPreferences(getApplicationContext());
        appPrefs.setIsMusicON(false);
        appPrefs.setSeekBarStart(0);


        mediaController = new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);


        appPrefs.setVideoProgress(videoProgress);

        // getting intent data
        Intent in = getIntent();


        Log.d(LOG_TAG, " videoRecorded " + videoRecorded + " vs " + in.getStringExtra("MEDIA_OUTPUT"));
        videoRecorded = in.getStringExtra("MEDIA_OUTPUT");
        videoTrim = "V_EDIT_" + new File(videoRecorded).getName().substring(4);

        // TODO Probar si butterknife acepta estos findViewById y sus propiedades, son layout, no buttons.

        layoutSeekBar = (ViewGroup) findViewById(R.id.linearLayoutRangeSeekBar);

        linearLayoutFrames = (LinearLayout) findViewById(R.id.linearLayoutFrames);

        progressDialog = new ProgressDialog(EditActivity.this);

        relativeLayoutPreviewVideo = (RelativeLayout) findViewById(R.id.relativeLayoutPreviewVideo);

        edit_bottom_panel = (FrameLayout) findViewById(R.id.edit_bottom_panel);

        textStartTrim.setText(TimeUtils.toFormattedTime(0));


    }


    @OnClick(R.id.buttonCancelEditActivity)
    public void cancelEditActivity() {
        sendButtonTracked(R.id.buttonCancelEditActivity);
        this.onBackPressed();
    }

    @OnClick(R.id.buttonOkEditActivity)
    public void okEditActivity() {
        sendButtonTracked(R.id.buttonOkEditActivity);
        Log.d(LOG_TAG, "trimClickListener");

        if(seekBarEnd - seekBarStart > ConfigUtils.maxDurationVideo) {

            Toast.makeText(getApplicationContext(), "Please trim your video, max 1 min", Toast.LENGTH_SHORT).show();

            return;
        }

        preview.stopPlayback();

        if (videoPlayer != null) {
            // mediaPlayer.stop();
            //videoPlayer.pause();
            videoPlayer.release();
            videoPlayer = null;
        }
        if (musicPlayer != null) {
            // mediaPlayerMusic.stop();
            // musicPlayer.pause();
            musicPlayer.release();
            musicPlayer = null;
        }

        /// TODO Wait until define progressDialog Design
        progressDialog.setMessage(getString(R.string.dialog_processing));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        // Custom progress dialog
        progressDialog.setIcon(R.drawable.activity_edit_icon_cut_normal);

        ((TextView) progressDialog.findViewById(Resources.getSystem()
                .getIdentifier("message", "id", "android")))
                .setTextColor(Color.WHITE);

        ((TextView) progressDialog.findViewById(Resources.getSystem()
                .getIdentifier("alertTitle", "id", "android")))
                .setTextColor(Color.WHITE);

        progressDialog.findViewById(Resources.getSystem().getIdentifier("topPanel", "id",
                "android")).setBackgroundColor(getResources().getColor(R.color.videona_blue_1));

        progressDialog.findViewById(Resources.getSystem().getIdentifier("customPanel", "id",
                "android")).setBackgroundColor(getResources().getColor(R.color.videona_blue_2));

        final Runnable r = new Runnable() {
            public void run() {

                exportVideo();

            }
        };

        performOnBackgroundThread(r);
    }


    @OnClick(R.id.edit_button_fx)
    public void showVideoFxMenu() {
        audioFxButton.setActivated(false);
        videoFxButton.setActivated(true);
        scissorButton.setActivated(false);
        sendButtonTracked(R.id.edit_button_fx);
        if (videoFxMenuFragment == null)
            videoFxMenuFragment = new VideoFxMenuFragment();
        this.switchFragment(videoFxMenuFragment, R.id.edit_right_panel);
        if (musicCatalogFragment != null)
            this.getFragmentManager().beginTransaction().remove(musicCatalogFragment).commit();
    }

    @OnClick(R.id.edit_button_audio)
    public void showAudioFxMenu() {
        sendButtonTracked(R.id.edit_button_audio);
        scissorButton.setActivated(false);
        audioFxButton.setActivated(true);
        videoFxButton.setActivated(false);


        if (audioFxMenuFragment == null) {
            audioFxMenuFragment = new AudioFxMenuFragment();
        }
        switchFragment(audioFxMenuFragment, R.id.edit_right_panel);
        //if (musicCatalogFragment == null) {

        onEffectMenuSelected();
    }

    @OnClick(R.id.edit_button_scissor)
    public void showScissorsFxMenu() {
        audioFxButton.setActivated(false);
        scissorButton.setActivated(true);
        sendButtonTracked(R.id.edit_button_scissor);

        if (scissorsFxMenuFragment == null) {
            scissorsFxMenuFragment = new ScissorsFxMenuFragment();
        }

        this.switchFragment(scissorsFxMenuFragment, R.id.edit_right_panel);

        relativeLayoutPreviewVideo.setVisibility(View.VISIBLE);
        if (musicCatalogFragment != null)
            this.getFragmentManager().beginTransaction().remove(musicCatalogFragment).commit();

    }

    @OnClick(R.id.edit_button_look)
    public void showLookFxMenu() {
        sendButtonTracked(R.id.edit_button_look);
        scissorButton.setActivated(false);
        audioFxButton.setActivated(false);
        if (lookFxMenuFragment == null)
            lookFxMenuFragment = new LookFxMenuFragment();
        this.switchFragment(lookFxMenuFragment, R.id.edit_right_panel);

        if (musicCatalogFragment != null)
            this.getFragmentManager().beginTransaction().remove(musicCatalogFragment).commit();
    }

    @OnTouch(R.id.edit_preview_player)
    public boolean onTouchPreview(MotionEvent event) {
        boolean result;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            playPausePreview();
            result = true;

            // amm
            videoProgress = videoPlayer.getCurrentPosition();
            appPrefs.setVideoProgress(videoProgress);
            seekBar.setProgress(videoProgress);
           // textSeekBar.setText(TimeUtils.toFormattedTime(videoProgress));
            // amm End

        } else {
            result = false;
        }
        return result;
    }

    @OnClick(R.id.edit_button_play)
    public void playPausePreview() {

        if (videoPlayer.isPlaying()) {

            videoPlayer.pause();

            if (musicPlayer != null && musicPlayer.isPlaying()) {

                musicPlayer.pause();
            }

            playButton.setVisibility(View.VISIBLE);

            videoProgress = videoPlayer.getCurrentPosition();
            appPrefs.setVideoProgress(videoProgress);

            //seekBar.setProgress(videoProgress);
            // textSeekBar.setVisibility(View.VISIBLE);
            // textSeekBar.setText(TimeUtils.toFormattedTime(videoProgress));

        } else {

            videoPlayer.start();

            if (musicPlayer != null) {

                musicPlayer.start();

            }

            playButton.setVisibility(View.INVISIBLE);

            //textSeekBar.setVisibility(View.INVISIBLE);
        }

        updateSeekProgress();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(LOG_TAG, "onStart");
        seekBarEnd = durationVideoRecorded;

        Log.d(LOG_TAG, "onStart seekBarEnd " + seekBarEnd);

        isRunning = true;
        PaintFramesTask task = new PaintFramesTask();
        task.execute();

    }

    @Override
    protected void onResume() {
        super.onResume();


        setVideoInfo();

        Log.d(LOG_TAG, " onResume isMusicON " + isMusicON);

        seekBarStart = appPrefs.getSeekBarStart();
        seekBarEnd = appPrefs.getSeekBarEnd();

        //refreshDetailTrimView();

        Log.d(LOG_TAG, "onResume seekBar progress " + appPrefs.getVideoProgress());

       // this.onRangeSeekBarValuesChanged(seekBarRange, 0.0, 60.0);


    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "onPause");

        if (videoPlayer != null && videoPlayer.isPlaying()) {

            videoPlayer.pause();

            if (musicPlayer != null && musicPlayer.isPlaying()) {

                musicPlayer.pause();
            }

            playButton.setVisibility(View.VISIBLE);

            videoProgress = videoPlayer.getCurrentPosition();
            appPrefs.setVideoProgress(videoProgress);

        }


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * Overridden to save instance trim text and seekBar
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "Bundle savedInstanceState");

        savedInstanceState.putInt("START_TRIM", seekBarStart);
        savedInstanceState.putInt("END_TRIM", seekBarEnd);
        savedInstanceState.putInt("SEEKBAR_PROGRESS", videoProgress);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Overridden to restore instance state trim text and seekBar
     */
/*
    Repasar no funciona bien

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "Bundle onRestoreInstanceState");

        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        String stateSaved = savedInstanceState.getString("saved_state");


        seekBarStart = savedInstanceState.getInt("START_TRIM");
        seekBarEnd = savedInstanceState.getInt("END_TRIM");
        videoProgress = savedInstanceState.getInt("SEEKBAR_PROGRESS");

        refreshDetailTrimView();

        seekBar.setProgress(videoProgress);
    }
  */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, " requestCode " + requestCode + " resultCode " + resultCode);
        if (requestCode == VIDEO_SHARE_REQUEST_CODE) {
            setResult(Activity.RESULT_OK);
            finish();
        }

    }


    private void switchFragment(Fragment f, int panel) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(panel, f).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    @Override
    public void navigate() {

    }

    @Override
    public void onEffectMenuSelected() {
        if (musicCatalogFragment == null)
            musicCatalogFragment = new MusicCatalogFragment();
        switchFragment(musicCatalogFragment, R.id.edit_bottom_panel);


    }

    @Override
    public void onEffectTrimMenuSelected() {

        relativeLayoutPreviewVideo.setVisibility(View.VISIBLE);

        if (edit_bottom_panel.getVisibility() == View.VISIBLE) {
            edit_bottom_panel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Necesita una refactorización de cagarse encima
     *
     * @param videoPath path of the previewing video
     */
    @Override
    public void initVideoPlayer(final String videoPath) {

        if (videoPlayer == null) {

            //videoPlayer = MediaPlayer.create(getApplicationContext(), videoUri);

            preview.setVideoPath(videoPath);
            preview.setMediaController(mediaController);
            preview.canSeekBackward();
            preview.canSeekForward();


            preview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {

                    Log.d(LOG_TAG, "EditVideoActivity setOnPreparedListener onPrepared");


                    setVideoInfo();

                    videoPlayer = mp;

                    paintSeekBar();

                    //TODO esto no va a valer
                    durationVideoRecorded = videoPlayer.getDuration();

                    seekBar.setMax(durationVideoRecorded);
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


                    durationVideoRecorded = seekBarEnd;
                    appPrefs.setSeekBarEnd(seekBarEnd);


                }
            });


            preview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    Log.d(LOG_TAG, "EditVideoActivity setOnCompletionListener");

                    playButton.setVisibility(View.VISIBLE);

                    updateSeekProgress();

                }
            });

            preview.requestFocus();


        }
    }

    @Override
    public void initMusicPlayer(Music music) {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
            musicPlayer = null;
        }
        musicPlayer = MediaPlayer.create(getApplicationContext(), music.getMusicResourceId());
        musicPlayer.setVolume(0.5f, 0.5f);
        // videoPlayerMute
        //videoPlayer.setVolume(0.0f,0.0f);

        isMusicON = true;
        // amm
        appPrefs.setIsMusicON(true);
    }

    /**
     * Needs to refactor
     * <p/>
     * Method that receives events from recyclerview
     *
     * @param position clicked on recycler
     */
    @Override
    public void onClick(int position) {

        List<Music> musicList = musicCatalogFragment.getFxList();

        for (Music m : musicList) {
            int selectedBackground;
            //TODO change icon of selected music
            switch (m.getColorResourceId()) {
                case R.color.pastel_palette_red_dark:
                    selectedBackground = R.color.pastel_palette_red;
                    break;
                case R.color.pastel_palette_blue_dark:
                    selectedBackground = R.color.pastel_palette_blue;
                    break;
                case R.color.pastel_palette_brown_dark:
                    selectedBackground = R.color.pastel_palette_brown;
                    break;
                case R.color.pastel_palette_green_dark:
                    selectedBackground = R.color.pastel_palette_green;
                    break;
                case R.color.pastel_palette_purple_dark:
                    selectedBackground = R.color.pastel_palette_purple;
                    break;
                case R.color.pastel_palette_orange_dark:
                    selectedBackground = R.color.pastel_palette_orange;
                    break;
                case R.color.pastel_palette_yellow_dark:
                    selectedBackground = R.color.pastel_palette_yellow;
                    break;
                case R.color.pastel_palette_grey_dark:
                    selectedBackground = R.color.pastel_palette_grey;
                    break;
                case R.color.pastel_palette_pink_dark:
                    selectedBackground = R.color.pastel_palette_pink;
                    break;
                case R.color.pastel_palette_pink_2_dark:
                    selectedBackground = R.color.pastel_palette_pink_2;
                    break;
                default:
                    selectedBackground = m.getColorResourceId();
                    break;
            }
            m.setColorResourceId(selectedBackground);
        }
        int selectedBackground;
        switch (musicList.get(position).getColorResourceId()) {
            case R.color.pastel_palette_red:
                selectedBackground = R.color.pastel_palette_red_dark;
                break;
            case R.color.pastel_palette_blue:
                selectedBackground = R.color.pastel_palette_blue_dark;
                break;
            case R.color.pastel_palette_brown:
                selectedBackground = R.color.pastel_palette_brown_dark;
                break;
            case R.color.pastel_palette_green:
                selectedBackground = R.color.pastel_palette_green_dark;
                break;
            case R.color.pastel_palette_purple:
                selectedBackground = R.color.pastel_palette_purple_dark;
                break;
            case R.color.pastel_palette_orange:
                selectedBackground = R.color.pastel_palette_orange_dark;
                break;
            case R.color.pastel_palette_yellow:
                selectedBackground = R.color.pastel_palette_yellow_dark;
                break;
            case R.color.pastel_palette_grey:
                selectedBackground = R.color.pastel_palette_grey_dark;
                break;
            case R.color.pastel_palette_pink:
                selectedBackground = R.color.pastel_palette_pink_dark;
                break;
            case R.color.pastel_palette_pink_2:
                selectedBackground = R.color.pastel_palette_pink_2_dark;
                break;
            default:
                selectedBackground = musicList.get(position).getColorResourceId();
                break;
        }

        musicList.get(position).setColorResourceId(selectedBackground);


        if (videoPlayer.isPlaying()) {

            videoPlayer.pause();

            if (musicPlayer != null && musicPlayer.isPlaying()) {

                musicPlayer.pause();
            }

            playButton.setVisibility(View.VISIBLE);

            videoProgress = videoPlayer.getCurrentPosition();
            appPrefs.setVideoProgress(videoProgress);


        }


        if (position == (musicList.size() - 1)) {

            Log.d(LOG_TAG, "Detenida la música");
            if (musicPlayer != null) {
                musicPlayer.release();
                musicPlayer = null;
                isMusicON = false;
                videoPlayer.setVolume(0.5f, 0.5f);
                selectedMusic = null;

            }

        } else {

            selectedMusic = musicList.get(position);

            Log.d(LOG_TAG, "adquirida la música");

            initMusicPlayer(selectedMusic);

            musicSelected = Constants.PATH_APP_TEMP + File.separator + selectedMusic.getNameResourceId() + Constants.AUDIO_MUSIC_FILE_EXTENSION;

            try {
                downloadResource(selectedMusic.getMusicResourceId());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        musicCatalogFragment.getAdapter().notifyDataSetChanged();


        updateSeekProgress();

    }

    private void setEditVideoProgress(int videoProgress, int seekBarStart, int seekBarEnd) {
        videoProgress = videoProgress;
        Log.d(LOG_TAG, "setEditVideoProgress " + "videoProgress " + videoProgress + " seekBarStart " + seekBarStart + " seekBarEnd " + seekBarEnd);

      //   Trim video no afecta a videoPlayer o MusicPlayer
      /*  if (isMusicON) {
            if (videoProgress >= seekBarStart * 1000 && videoProgress <= seekBarEnd * 1000) {
                videoPlayer.setVolume(0f, 0f);
                isVideoMute = true;
                if (musicPlayer == null) {
                    musicPlayer = MediaPlayer.create(getBaseContext(), musicRawSelected);
                    musicPlayer.setVolume(5.0f, 5.0f);
                } else {
                    if (musicPlayer.isPlaying()) {
                        musicPlayer.pause();
                    }
                }
                musicPlayer.seekTo(videoProgress - seekBarStart * 1000);
            } else {
                if (isVideoMute) {
                    isVideoMute = false;
                    videoPlayer.setVolume(0.5f, 0.5f);
                }
                if (musicPlayer != null) {
                    musicPlayer.stop();
                    musicPlayer.release();
                    musicPlayer = null;
                }
            }
        }

      */


        videoPlayer.seekTo(videoProgress);


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

        Log.d(LOG_TAG, " setVideoInfo " + durationVideoRecorded + " duration " + duration);
    }


    private void updateSeekProgress() {

        if (videoPlayer != null && videoPlayer.isPlaying()) {

            seekBar.setProgress(videoPlayer.getCurrentPosition());
            handler.postDelayed(updateTimeTask, 50);

        }
    }

    //stop playback when out of thumb bounds
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (videoPlayer.isPlaying() && isRunning) {
// stop playback and set playback position on the end of trim
// border
                videoPlayer.pause();
                videoPlayer.seekTo(seekBarEnd * 1000);
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


    private void paintFramesVideo(String pathVideoName) {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //amm 1st May int width = metrics.widthPixels - 100;
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Log.d("MainActivity", "screen size width: " + width + " " + "heigh " + height);
        int width_opt = (int) width / 9;
        //int height_opt = (int) ((width_opt * 9) / 16); // 16:9
        //int height_opt = (int) ((width_opt * 3) /4); // 4:3

        int height_opt = height;
        Log.d("MainActivity", "screen size width_opt: " + width_opt + " " + "height_opt " + height_opt);
        File videoFile = new File(pathVideoName);
        Uri videoFileUri = Uri.parse(videoFile.toString());
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoFile.getAbsolutePath());

        // Prevent null pointer exception. App crush. Paint frames by default
        if(retriever.getFrameAtTime() == null) {
            return;
        }

        MediaPlayer mp = MediaPlayer.create(getBaseContext(), videoFileUri);

        int millis = mp.getDuration();

        // Get 9 key frames from video in separate time
        for (int j = 1; j < 10; j++) {
            int value = j;
            Bitmap bitmap = retriever.getFrameAtTime((int) (millis / 9) * 1000 * value, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            Bitmap bmpScaledSize = Bitmap.createScaledBitmap(bitmap, width_opt, height_opt, false);
            switch (value) {
                case 1:

                    image1.setImageBitmap(bmpScaledSize);
                    image1.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 2:

                    image2.setImageBitmap(bmpScaledSize);
                    image2.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 3:

                    image3.setImageBitmap(bmpScaledSize);
                    image3.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 4:

                    image4.setImageBitmap(bmpScaledSize);
                    image4.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 5:

                    image5.setImageBitmap(bmpScaledSize);
                    image5.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 6:

                    image6.setImageBitmap(bmpScaledSize);
                    image6.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 7:

                    image7.setImageBitmap(bmpScaledSize);
                    image7.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 8:

                    image8.setImageBitmap(bmpScaledSize);
                    image8.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 9:

                    image9.setImageBitmap(bmpScaledSize);
                    image9.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;

            }
        }
    }

    public void paintSeekBar() {

        // Initialize seekBarRange
        Log.d(LOG_TAG, "paintSeekBar Activity");
        //amm durationVideoCut = preview.getDuration() / 1000;
        durationVideoCut = preview.getDuration() / 1000;
        Log.d(LOG_TAG, "duracionvideo getDuration() " + durationVideoCut);
        seekBarStart = 0;
        // Rule to control maxDurationVideo. Not today
        //seekBarEnd = Math.min(durationVideoCut, seekBarStart + ConfigUtils.maxDurationVideo);
        seekBarEnd = durationVideoCut;

        durationVideoRecorded = seekBarEnd;

        //appPrefs.setSeekBarEnd(seekBarEnd);
        //refreshDetailTrimView();

        seekBarRange = new RangeSeekBar<Double>(
                (double) 0, (double) durationVideoCut, getBaseContext()
                .getApplicationContext());

        seekBarRange.setOnRangeSeekBarChangeListener(this);

        layoutSeekBar.addView(seekBarRange);

    }

    private void refreshDetailTrimView() {

        Log.d(LOG_TAG, "refreshDetailTrimView");

        int startSeekBar = appPrefs.getSeekBarStart();
        // Rule to force one minute, not today
        //int stopSeekBar = Math.min(seekBarEnd, seekBarStart + ConfigUtils.maxDurationVideo);
        int stopSeekBar = appPrefs.getSeekBarEnd();


        durationVideoCut = stopSeekBar - startSeekBar;


        textStartTrim.setText(TimeUtils.toFormattedTime(startSeekBar*1000));
        //textEndTrim.setText(TimeUtils.toFormattedTime(Math.min(stopSeekBar * 1000, 60000)));
        textTimeTrim.setText(TimeUtils.toFormattedTime(Math.min(durationVideoCut * 1000, ConfigUtils.maxDurationVideo*1000)));

        if(durationVideoCut > ConfigUtils.maxDurationVideo){
            textEndTrim.setText(TimeUtils.toFormattedTime((startSeekBar + ConfigUtils.maxDurationVideo) * 1000));
        } else {
            textEndTrim.setText(TimeUtils.toFormattedTime(stopSeekBar * 1000));
        }


        if(videoPlayer !=null) {
            videoPlayer.seekTo(startSeekBar * 1000);
            seekBar.setProgress(startSeekBar * 1000);
        }

    }


    private String[] mThumbPathAudioSelected = {
            "audio_folk.m4a", "audio_hiphop.m4a",
            "audio_pop.m4a", "audio_reggae.m4a",
            "audio_rock.m4a", "audio_clasica_piano.m4a",
            "audio_clasica_violin.m4a", "audio_clasica_flauta.m4a",
            "audio_ambiental.m4a"
    };


    /**
     * Listener seekBar, videoPlayer
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (fromUser) {

            setEditVideoProgress(progress, seekBarStart, seekBarEnd);

        } else {

            //   Trim video no afecta a videoPlayer o MusicPlayer
            if (isMusicON) {

                videoProgress = progress;

                if (videoProgress >= seekBarStart * 1000 && videoProgress <= seekBarEnd * 1000) {

                    Log.d(LOG_TAG, " Estoy dentro progress " + videoProgress);

                    if(isOnTrimming) {

                    } else {
                        musicPlayer.seekTo(videoProgress - (seekBarStart * 1000));
                        musicPlayer.start();
                        isOnTrimming = true;
                        videoPlayer.setVolume(0.0f, 0.0f);
                    }

                /*    if (musicPlayer == null) {
                        musicPlayer = MediaPlayer.create(getBaseContext(), musicRawSelected);
                        musicPlayer.setVolume(5.0f, 5.0f);
                    } else {
                        if (musicPlayer.isPlaying()) {
                            musicPlayer.pause();
                        }
                    }
                */


                } else {

                    Log.d(LOG_TAG, " Estoy fuera progress " + videoProgress);

                    if(isOnTrimming) {

                        videoPlayer.setVolume(0.5f, 0.5f);

                        isOnTrimming = false;

                        if (musicPlayer != null) {

                            musicPlayer.pause();

                        }

                    }
                }

            }

        }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        handler.removeCallbacks(updateTimeTask);


        videoPlayer.seekTo(seekBar.getProgress());

        if (isMusicON && (musicPlayer != null)) {
            //amm musicPlayer.seekTo(seekBar.getProgress() + musicPlayer.getCurrentPosition() - seekBarStart);
            musicPlayer.seekTo(seekBar.getProgress() + musicPlayer.getCurrentPosition() - seekBarStart);
        }

        updateSeekProgress();

        //amm Delete
        if (videoPlayer != null) {
            videoProgress = videoPlayer.getCurrentPosition();
            appPrefs.setVideoProgress(videoProgress);
            //textSeekBar.setText(TimeUtils.toFormattedTime(videoProgress));

            seekBar.setProgress(videoProgress);
        }

    }

    /**
     * Listener Trim Video, RangeSeekBar
     *
     * @param bar
     * @param minValue
     * @param maxValue
     */
    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {

        Log.d(LOG_TAG, " RangeSeekBar minValue " + minValue + " maxValue " + maxValue);

        isRunning = false; // set flag to prevent collision with

        seekBarStart = (int) Math.round((Double) minValue);

        // Rule to control maxDurationVideo. Not today
        //seekBarEnd = Math.min((int) Math.round((Double) maxValue), seekBarStart + 60);
        seekBarEnd = (int) Math.round((Double) maxValue);

           /*  SeekBarRange no afecta al player
                if (videoPlayer.isPlaying()) {
                    if (musicPlayer != null) {
                        if (musicPlayer.isPlaying()) {
                            musicPlayer.pause();
                        }
                    }
                    videoPlayer.pause();
                    playButton.setVisibility(View.VISIBLE);
                }

            */
        appPrefs.setSeekBarStart(seekBarStart);
        appPrefs.setSeekBarEnd(seekBarEnd);

        refreshDetailTrimView();

        textStartTrim.setVisibility(View.VISIBLE);
        textEndTrim.setVisibility(View.VISIBLE);
        textTimeTrim.setVisibility(View.VISIBLE);

        isRunning = true; // free flag to prevent collision with

    }


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

    public void downloadResource(int raw_resource) throws IOException {


        InputStream in = getResources().openRawResource(raw_resource);

        String nameFile = getResources().getResourceName(raw_resource);
        nameFile = nameFile.substring(nameFile.lastIndexOf("/") + 1);

        Log.d(LOG_TAG, "downloadResource " + nameFile);

        File fSong = new File(Constants.PATH_APP_TEMP + File.separator + nameFile + Constants.AUDIO_MUSIC_FILE_EXTENSION);

        if (fSong.exists()) {


        } else {

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(Constants.PATH_APP_TEMP + File.separator + nameFile + Constants.AUDIO_MUSIC_FILE_EXTENSION);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                in.close();
                out.flush();
                out.close();
            }


        }
    }

    public void exportVideo() {

        // 1st trimVideo

        int start = appPrefs.getSeekBarStart();
        int length = appPrefs.getSeekBarEnd() - start;
        String inputFileName = videoRecorded;
        pathvideoTrim = Constants.PATH_APP + File.separator + videoTrim;
        Log.d(LOG_TAG, "VideonaMainActivity input " + inputFileName + " output " + pathvideoTrim + " start " + start + " length " + length);


        String pathVideonaFinal = pathvideoTrim;

        //VideonaMainActivity.cut(inputFileName, pathvideoTrim, start, length);

        try {

            VideoUtils.trimVideo(inputFileName, start, appPrefs.getSeekBarEnd(), pathVideonaFinal);

        } catch (IOException e) {
            e.printStackTrace();

            Log.d(LOG_TAG, "Video Trimm failed");
        }

        Log.d(LOG_TAG, "Video Trimmed");


        if (isMusicON) {

            try {

                // 2nd Switch audio

                // String audio_test = Environment.getExternalStorageDirectory() + "/Videona/audio_m4a.m4a";
                // VideoUtils.switchAudio(pathvideoTrim, audio_test, Config.videoMusicTempFile);
                Log.d(LOG_TAG, "pathVideoTrim " + pathvideoTrim + "  " + " musicSelected " + musicSelected);

                VideoUtils.switchAudio(pathvideoTrim, musicSelected, Constants.VIDEO_MUSIC_TEMP_FILE);

                // Delete TRIM temporal file
                File fTrim = new File(pathvideoTrim);
                if (fTrim.exists()) {
                    fTrim.delete();
                }

            } catch (IOException e) {
                e.printStackTrace();

                Log.d(LOG_TAG, "Video isMusic ON switchAudio failed");

            }

            Log.d(LOG_TAG, "Video isMusic ON switchAudio");


            // 3rd trim Video + Audio

            String videonaMusic = "V_EDIT_" + new File(pathvideoTrim).getName().substring(7);

            pathVideonaFinal = Constants.PATH_APP + File.separator + videonaMusic;


            Log.d(LOG_TAG, "VideonaMainActivity trimAudio cut " + Constants.VIDEO_MUSIC_TEMP_FILE + " .-.-.-. " + pathVideonaFinal + " .-.-.-. " + appPrefs.getVideoDurationTrim());

            //  VideonaMainActivity.cut(Constants.VIDEO_MUSIC_TEMP_FILE, pathVideonaFinal, 0, length);

            try {

                VideoUtils.trimVideo(Constants.VIDEO_MUSIC_TEMP_FILE, 0, length, pathVideonaFinal);
            } catch (IOException e) {
                e.printStackTrace();

                Log.d(LOG_TAG, "Video isMusic ON trimVideo with audio failed");
            }

            // Delete TempAV temporal file
            File fTemp = new File(Constants.VIDEO_MUSIC_TEMP_FILE);

            if (fTemp.exists()) {
                fTemp.delete();
            }

            Log.d(LOG_TAG, "Video isMusic ON trimVideo with audio ");

            isMusicON = false;

        }


        this.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.toast_trim), Toast.LENGTH_SHORT).show();
            }
        });


        File fVideoFinal = new File(pathVideonaFinal);
        if (fVideoFinal.exists()) {

            Intent share = new Intent();
            share.putExtra("MEDIA_OUTPUT", pathVideonaFinal);
            share.setClass(EditActivity.this, ShareActivity.class);
            startActivityForResult(share, VIDEO_SHARE_REQUEST_CODE);

        } else {

            Toast.makeText(getApplicationContext(), "pathVideoFinal falló", Toast.LENGTH_SHORT).show();
        }


    }

    @OnClick({R.id.buttonCancelEditActivity, R.id.buttonOkEditActivity,
            R.id.edit_button_fx, R.id.edit_button_audio, R.id.edit_button_scissor,
            R.id.edit_button_look})
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
            case R.id.buttonCancelEditActivity:
                label = "Cancel, return to the camera";
                break;
            case R.id.buttonOkEditActivity:
                label = "Ok, export the project";
                break;
            case R.id.edit_button_fx:
                label = "Show video fx menu";
                break;
            case R.id.edit_button_audio:
                label = "Show audio fx menu";
                break;
            case R.id.edit_button_scissor:
                label = "Show scissor fx menu";
                break;
            case R.id.edit_button_look:
                label = "Show look fx menu";
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("EditActivity")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getApplication().getBaseContext()).dispatchLocalHits();
    }

}
