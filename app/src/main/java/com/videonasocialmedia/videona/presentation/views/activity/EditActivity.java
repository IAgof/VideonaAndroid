/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.EditorElement;
import com.videonasocialmedia.videona.model.entities.editor.media.audio.Music;
import com.videonasocialmedia.videona.presentation.mvp.presenters.EditPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;
import com.videonasocialmedia.videona.presentation.views.VideonaMainActivity;
import com.videonasocialmedia.videona.presentation.views.fragment.AudioFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.FxCatalogFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.LookFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.ScissorsFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.VideoFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectMenuSelectedListener;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerClickListener;
import com.videonasocialmedia.videona.utils.ConfigUtils;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.CutVideoPlayerState;
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
public class EditActivity extends Activity implements EditorView, OnEffectMenuSelectedListener, RecyclerClickListener {

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

    /*Preview*/
    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private MediaPlayer musicPlayer;

    /*Navigation*/
    private VideoFxMenuFragment videoFxMenuFragment;
    private AudioFxMenuFragment audioFxMenuFragment;
    private ScissorsFxMenuFragment scissorsFxMenuFragment;
    private LookFxMenuFragment lookFxMenuFragment;
    private FxCatalogFragment fxCatalogFragment;

    /*mvp*/
    private EditPresenter editPresenter;

    //TODO de aquí en adelante hay que cambiarlo todo!!!!!
    int duration;

    Music selectedMusic;

    private boolean isVideoMute = false;
    private boolean isMusicON = false;

    //******************************************************************************
    //******************************************************************************
    // Start to define variables for old EditActivity, delete after update apk

    @InjectView(R.id.buttonCancelEditActivity)
    Button buttonCancelEditActivity;
    @InjectView(R.id.buttonOkEditActivity)
    Button buttonOkEditActivity;

    private static final int VIDEO_SHARE_REQUEST_CODE = 500;
    private static final int ADD_MUSIC_REQUEST_CODE = 600;

    public static VideoView videoView;

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


    private int musicRawSelected;

    UserPreferences appPrefs;
    // create RangeSeekBar as Double
    RangeSeekBar<Double> seekBarRange;


    //******************************************************************************
    //******************************************************************************
    // End of changes

    //******************************************************************************
    //******************************************************************************

   /* private Handler handler = new Handler();

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
          // amm
            if(videoPlayer != null) {
                seekBar.setProgress(videoPlayer.getCurrentPosition());
                seekBar.setMax(videoPlayer.getDuration());
                handler.postDelayed(this, 50);
            }
        }
    };

    */

    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekProgress();
        }
    };


    //******************************************************************************
    //******************************************************************************
    // amm end of changes


    public EditActivity() {
    }

    private void updateSeekProgress() {

       // Log.d(LOG_TAG, "updateSeekProgress");

        if (videoPlayer != null && videoPlayer.isPlaying()) {

            seekBar.setProgress(videoPlayer.getCurrentPosition());
            handler.postDelayed(updateTimeTask, 50);

            if (isMusicON) {

                //Checks if the seek bar has reached de end of the trimmed segment
                //If true stops music and restores original video audio
                if ((int) (videoPlayer.getCurrentPosition()) == seekBarEnd) {

                    Log.d(LOG_TAG, "EditVideoActivity seekBarEnd Mute OFF");

                    if (isVideoMute) {

                        musicPlayer.stop();
                        //musicPlayer.release();
                        //musicPlayer = null;

                        videoPlayer.setVolume(0.5f, 0.5f);

                        isVideoMute = false;

                    }

                }
                //Checks if the seek bar has reached the start of the trimmed segment
                //If true mutes the video audio and starts music player
                if ((videoPlayer.getCurrentPosition()) == seekBarStart) {

                    Log.d(LOG_TAG, "EditVideoActivity seekBarStart Mute ON");

                    if (!isVideoMute) {

                        initMusicPlayer(selectedMusic);
                        musicPlayer = MediaPlayer.create(getBaseContext(), selectedMusic.getMusicResourceId());
                        musicPlayer.start();
                        musicPlayer.setVolume(5.0f, 5.0f);

                        musicPlayer.setVolume(0f, 0f);

                        isVideoMute = true;

                    }

                }
            }
        }
    }



    /*fin de la chapu*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.inject(this);

        editPresenter = new EditPresenter(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // amm Delete


                if (fromUser) {
                    setEditVideoProgress(progress, seekBarStart, seekBarEnd);
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
               if(videoPlayer!=null) {
                   videoProgress = videoPlayer.getCurrentPosition();
                   appPrefs.setVideoProgress(videoProgress);
               }



            }
        });

        //TODO mover a donde se deba
        audioFxMenuFragment = new AudioFxMenuFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.edit_right_panel, audioFxMenuFragment).commit();

        this.initVideoPlayer(this.getIntent().getStringExtra("MEDIA_OUTPUT"));





        //******************************************************************************
        //******************************************************************************
        // Changes on onCreate

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


        mediaController = new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);

        videoPlayer = new MediaPlayer();

        appPrefs.setVideoProgress(videoProgress);

        // getting intent data
        Intent in = getIntent();

        // Test pruebas 2 min
        //videoRecorded = Environment.getExternalStorageDirectory() + File.separator + "alvaro/dosminutos.mp4";
        this.initVideoPlayer(videoRecorded);
        // videoRecorded = Environment.getExternalStorageDirectory() + File.separator + "Videona/vlc-player.mp4";
        Log.d(LOG_TAG, " videoRecorded " + videoRecorded + " vs " + in.getStringExtra("MEDIA_OUTPUT"));
        videoRecorded = in.getStringExtra("MEDIA_OUTPUT");
        videoTrim = "V_EDIT_" + new File(videoRecorded).getName().substring(4);
        layoutSeekBar = (ViewGroup) findViewById(R.id.linearLayoutRangeSeekBar);
        linearLayoutFrames = (LinearLayout) findViewById(R.id.linearLayoutFrames);
        progressDialog = new ProgressDialog(EditActivity.this);

        relativeLayoutPreviewVideo = (RelativeLayout) findViewById(R.id.relativeLayoutPreviewVideo);

        edit_bottom_panel = (FrameLayout) findViewById(R.id.edit_bottom_panel);


        //******************************************************************************
        //******************************************************************************
        // End of changes on onCreate

    }


    //******************************************************************************
    //******************************************************************************
    // amm Cancel and Ok Edit Activity

    @OnClick(R.id.buttonCancelEditActivity)
    public void cancelEditActivity(){

        this.onBackPressed();
    }

    @OnClick(R.id.buttonOkEditActivity)
    public void okEditActivity(){

        Log.d(LOG_TAG, "trimClickListener");

       /* if(isMusicON) {
            try {
                downloadResource(selectedMusic.getMusicResourceId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */

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
/* progressDialog.setMessage(getString(R.string.dialog_processing));
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
*/
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




    //******************************************************************************
    //******************************************************************************

    @OnClick(R.id.edit_button_fx)
    public void showVideoFxMenu() {
        if (videoFxMenuFragment == null)
            videoFxMenuFragment = new VideoFxMenuFragment();
        this.switchFragment(videoFxMenuFragment, R.id.edit_right_panel);
    }

    @OnClick(R.id.edit_button_audio)
    public void showAudioFxMenu() {
        if (audioFxButton == null)
            audioFxMenuFragment = new AudioFxMenuFragment();
        this.switchFragment(audioFxMenuFragment, R.id.edit_right_panel);
    }

    @OnClick(R.id.edit_button_scissor)
    public void showScissorsFxMenu() {
        if (scissorsFxMenuFragment == null)
            scissorsFxMenuFragment = new ScissorsFxMenuFragment();
        this.switchFragment(scissorsFxMenuFragment, R.id.edit_right_panel);
    }

    @OnClick(R.id.edit_button_look)
    public void showLookFxMenu() {
        if (lookFxMenuFragment == null)
            lookFxMenuFragment = new LookFxMenuFragment();
        this.switchFragment(lookFxMenuFragment, R.id.edit_right_panel);
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
            if (musicPlayer != null) {
                musicPlayer.pause();
            }

            playButton.setVisibility(View.VISIBLE);

            // amm
            videoProgress = videoPlayer.getCurrentPosition();
            appPrefs.setVideoProgress(videoProgress);
            seekBar.setProgress(videoProgress);
            // amm End

        } else {
            videoPlayer.start();
            if (musicPlayer != null) {
                musicPlayer.start();
               // updateSeekProgress();
            }
            playButton.setVisibility(View.INVISIBLE);
        }

        updateSeekProgress();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // amm
        //******************************************************************************
        //******************************************************************************
        Log.d(LOG_TAG, "onStart");
        seekBarEnd = durationVideoRecorded;
        isRunning = true;
        PaintFramesTask task = new PaintFramesTask();
        task.execute();

        //******************************************************************************
        //******************************************************************************

    }

    @Override
    protected void onResume() {
        super.onResume();

        //******************************************************************************
        //******************************************************************************

        setVideoInfo();
        Log.d(LOG_TAG, " onResume isMusicON " + isMusicON);
        if (isMusicON) {
            videoPlayer = new MediaPlayer();
            videoPlayer.reset();
            seekBarStart = appPrefs.getSeekBarStart();
            seekBarEnd = Math.min(appPrefs.getSeekBarEnd(), seekBarStart + ConfigUtils.maxDurationVideo);
            preview.seekTo(appPrefs.getVideoProgress());
            setEditVideoProgress(appPrefs.getVideoProgress(), seekBarStart, seekBarEnd);
        } else {
            previewVideo();
            seekBarEnd = durationVideoRecorded;
        }
        refreshDetailView();

        //******************************************************************************
        //******************************************************************************

    }


    @Override
    protected void onPause() {
        super.onPause();


        //******************************************************************************
        //******************************************************************************

        Log.d(LOG_TAG, "onPause");
        appPrefs.setSeekBarStart(seekBarStart);
        appPrefs.setSeekBarEnd(seekBarEnd);
        appPrefs.setVideoProgress(videoProgress);
        appPrefs.setVideoDuration(durationVideoRecorded);
        seekBar.setProgress(videoProgress);

        //******************************************************************************
        //******************************************************************************
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // amm
    //******************************************************************************
    //******************************************************************************

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, " requestCode " + requestCode + " resultCode " + resultCode);
        if (requestCode == VIDEO_SHARE_REQUEST_CODE) {
            setResult(Activity.RESULT_OK);
            finish();
        }

    }

    //******************************************************************************
    //******************************************************************************

    private void switchFragment(Fragment f, int panel) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(panel, f).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    @Override
    public void navigate() {

    }

    @Override
    public void onEffectMenuSelected() {

        if(edit_bottom_panel.getVisibility() == View.INVISIBLE) {
            edit_bottom_panel.setVisibility(View.VISIBLE);
        }

        if(relativeLayoutPreviewVideo.getVisibility() == View.VISIBLE){
            relativeLayoutPreviewVideo.setVisibility(View.GONE);
        };

        if (fxCatalogFragment == null) {
            fxCatalogFragment = new FxCatalogFragment();
        } else {
            //TODO cambiar la lista del fragment
        }

        switchFragment(fxCatalogFragment, R.id.edit_bottom_panel);
    }

    @Override
    public void onEffectTrimMenuSelected(){

        relativeLayoutPreviewVideo.setVisibility(View.VISIBLE);

        if(edit_bottom_panel.getVisibility() == View.VISIBLE) {
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
                    //int duration = durationVideoRecorded * 1000;

                    setVideoInfo();

                    videoPlayer = mp;

                    //TODO esto no va a valer
                    durationVideoRecorded = videoPlayer.getDuration();
                    seekBar.setMax(durationVideoRecorded);
                    seekBar.setProgress(videoPlayer.getCurrentPosition());
                    videoPlayer.setVolume(0.5f, 0.5f);
                    videoPlayer.setLooping(false);

                    videoPlayer.start();

                    videoPlayer.seekTo(500);

                    videoPlayer.pause();
                }
            });


            preview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    Log.d(LOG_TAG, "EditVideoActivity setOnCompletionListener");

                    playButton.setVisibility(View.VISIBLE);

                    videoPlayer.stop();

                    updateSeekProgress();

                    //videoProgress = 0;

                    if (musicPlayer != null) {
                        musicPlayer.release();
                        musicPlayer = null;
                    }
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
        musicPlayer.setVolume(5.0f, 5.0f);
        isMusicON = true;
        // amm
        appPrefs.setIsMusicON(true);
    }


    /**
     * Needs to refactor
     *
     * @param position clicked on recycler
     */
    @Override
    public void onClick(int position) {

        if (videoPlayer.isPlaying()) {
            videoPlayer.pause();
            /*if (musicPlayer.isPlaying()) {

                musicPlayer.stop();
                musicPlayer.release();
                musicPlayer = null;

            }*/
            playButton.setVisibility(View.VISIBLE);
        }

        List<EditorElement> musicList = fxCatalogFragment.getFxList();

        if (position == (musicList.size()-1)) {

            Log.d(LOG_TAG, "Detenida la música");
            if(musicPlayer!=null){
                musicPlayer.release();
                musicPlayer=null;
                isMusicON=false;
                selectedMusic=null;

            }

        } else {

            selectedMusic = (Music) musicList.get(position);

            Log.d(LOG_TAG, "adquirida la música");

            initMusicPlayer(selectedMusic);

            musicSelected = Constants.PATH_APP_TEMP + File.separator + selectedMusic.getNameResourceId() + Constants.AUDIO_MUSIC_FILE_EXTENSION;

            try {
                downloadResource(selectedMusic.getMusicResourceId());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }


    //******************************************************************************
    //******************************************************************************
    //******************************************************************************
    //******************************************************************************
    // amm Delete



    private void setEditVideoProgress(int videoProgress, int seekBarStart, int seekBarEnd) {
        videoProgress = videoProgress;
        Log.d(LOG_TAG, "setEditVideoProgress " + "videoProgress " + videoProgress + " seekBarStart " + seekBarStart + " seekBarEnd " + seekBarEnd);

      /*    Trim video no afecta a videoPlayer o MusicPlayer
        if (isMusicON) {
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
        videoPlayer.seekTo(videoProgress);

        */
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

            @Override
            public void onClick(View arg0) {
                Log.d(LOG_TAG, "trimClickListener");
                videoPlayer.pause();
                preview.stopPlayback();
                if (videoPlayer != null) {
// mediaPlayer.stop();
                    videoPlayer.release();
                    videoPlayer = null;
                }
                if (musicPlayer != null) {
// mediaPlayerMusic.stop();
                    musicPlayer.release();
                    musicPlayer = null;
                }
/// TODO Wait until define progressDialog Design
/* progressDialog.setMessage(getString(R.string.dialog_processing));
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
*/
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
            if (videoPlayer.isPlaying() && isRunning) {
// stop playback and set playback position on the end of trim
// border
                videoPlayer.pause();
                videoPlayer.seekTo(seekBarEnd * 1000);
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
// String audio_test = Environment.getExternalStorageDirectory() + "/Videona/audio_m4a.m4a";
// VideoUtils.switchAudio(pathvideoTrim, audio_test, Config.videoMusicTempFile);
                Log.d(LOG_TAG, "pathVideoTrim " + pathvideoTrim + "  " + " musicSelected " + musicSelected);
                VideoUtils.switchAudio(pathvideoTrim, musicSelected, Constants.VIDEO_MUSIC_TEMP_FILE);

                // Delete TRIM temporal file
                File fTrim = new File(pathvideoTrim);
                if(fTrim.exists()){
                    fTrim.delete();
                }

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
        pathvideoTrim = Constants.PATH_APP + File.separator + videoTrim;
        Log.d(LOG_TAG, "VideonaMainActivity input " + inputFileName + " output " + pathvideoTrim + " start " + start + " length " + length);
        VideonaMainActivity.cut(inputFileName, pathvideoTrim, start, length);
    }
    private void renameTrimVideo(String videoTrim) {
        String newVideoTrim = videoTrim;
        String videoTrimAux = Constants.PATH_APP + Constants.VIDEO_CUT_AUX_NAME;
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
            preview.setVideoPath(videoRecorded);
            preview.setMediaController(mediaController);
            preview.canSeekBackward();
            preview.canSeekForward();
            preview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(LOG_TAG, "EditVideoActivity setOnPreparedListener onPrepared");
                    if (!isMusicON) {
                        paintSeekBar();
                    }
                    int duration = durationVideoRecorded * 1000;
                    seekBar.setMax(duration);
                    videoPlayer = mp;
                    videoPlayer.setVolume(0.5f, 0.5f);
                    videoPlayer.setLooping(false);
                    videoPlayer.start();
                    videoPlayer.seekTo(500);
                    videoPlayer.pause();
                }
            });
// to start video on time 500ms, avoid black screen
//videoView.seekTo(500);
            preview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(LOG_TAG, "EditVideoActivity setOnCompletionListener");
                    playButton.setVisibility(View.VISIBLE);
                    videoProgress = 0;
                    if (musicPlayer != null) {
                        musicPlayer.release();
                        musicPlayer = null;
                    }
                }
            });
            preview.requestFocus();
            refreshDetailView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Object onRetainNonConfigurationInstance() {
        return cutvideoPlayerState;
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
/* linearLayoutImages.setLayoutParams(new RelativeLayout.LayoutParams(
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
        //amm durationVideoCut = preview.getDuration() / 1000;
        durationVideoCut = preview.getDuration() / 1000;
        Log.d(LOG_TAG, "duracionvideo getDuration() " + durationVideoCut);
        seekBarStart = 0;
        seekBarEnd = Math.min(durationVideoCut, seekBarStart + ConfigUtils.maxDurationVideo);

        durationVideoRecorded = seekBarEnd;

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


    //******************************************************************************
    //******************************************************************************
    //******************************************************************************
    //******************************************************************************
    // amm Delete


}
