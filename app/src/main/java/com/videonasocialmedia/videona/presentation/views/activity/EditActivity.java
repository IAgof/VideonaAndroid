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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.EditorElement;
import com.videonasocialmedia.videona.model.entities.editor.media.audio.Music;
import com.videonasocialmedia.videona.presentation.mvp.presenters.EditPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;
import com.videonasocialmedia.videona.presentation.views.fragment.AudioFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.FxCatalogFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.LookFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.ScissorsFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.VideoFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectMenuSelectedListener;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerClickListener;
import com.videonasocialmedia.videona.utils.RangeSeekBar;

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

    private Handler handler = new Handler();

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            seekBar.setProgress(videoPlayer.getCurrentPosition());
            seekBar.setMax(videoPlayer.getDuration());
            handler.postDelayed(this, 50);
        }
    };

    private void updateSeekProgress() {

        if (videoPlayer != null && videoPlayer.isPlaying()) {

            seekBar.setProgress(videoPlayer.getCurrentPosition());
            handler.postDelayed(updateTimeTask, 50);

            /*if (isMusicON) {

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
            }*/
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

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateTimeTask);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateTimeTask);
                videoPlayer.seekTo(seekBar.getProgress());
                updateSeekProgress();
            }
        });

        //TODO mover a donde se deba
        audioFxMenuFragment = new AudioFxMenuFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.edit_right_panel, audioFxMenuFragment).commit();

        this.initVideoPlayer(this.getIntent().getStringExtra("MEDIA_OUTPUT"));
    }

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
    public boolean pausePreview(MotionEvent event) {
        boolean result;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            playPreview();
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @OnClick(R.id.edit_button_play)
    public void playPreview() {

        if (videoPlayer.isPlaying()) {
            videoPlayer.pause();
            if (musicPlayer != null) {
                musicPlayer.pause();
            }
            playButton.setVisibility(View.VISIBLE);
        } else {
            videoPlayer.start();
            if (musicPlayer != null) {
                musicPlayer.start();
                updateSeekProgress();
            }
            playButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (fxCatalogFragment == null) {
            fxCatalogFragment = new FxCatalogFragment();
        } else {
            //TODO cambiar la lista del fragment
        }
        switchFragment(fxCatalogFragment, R.id.edit_bottom_panel);
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

                    //

                    videoPlayer = mp;

                    //TODO esto no va a valer
                    duration = videoPlayer.getDuration();
                    seekBar.setMax(duration);
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
    public void onClick(int position) {
        List<EditorElement> musicList=fxCatalogFragment.getFxList();
        Music selectedMusic= (Music)musicList.get(position);
        Log.d(LOG_TAG, "adquirida la música");
    }

    @Override
    public void initMusicPlayer(String musicPath) {
        if (musicPlayer == null) {
            Uri audioUri = Uri.parse(musicPath);
            musicPlayer = MediaPlayer.create(getApplicationContext(), audioUri);
            musicPlayer.setVolume(5.0f, 5.0f);
        }
    }
}
