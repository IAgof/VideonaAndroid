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
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.videonasocialmedia.videona.presentation.views.fragment.MusicGalleryFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.PreviewVideoListFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.ScissorsFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.TrimPreviewFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.VideoFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.VideoTimeLineFragment;
import com.videonasocialmedia.videona.presentation.views.listener.MusicRecyclerViewClickListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectMenuSelectedListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnRemoveAllProjectListener;
import com.videonasocialmedia.videona.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.videona.utils.Utils;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * @author Juan Javier Cabanas Abascal
 */
public class EditActivity extends Activity implements EditorView, OnEffectMenuSelectedListener,
        MusicRecyclerViewClickListener, VideoTimeLineRecyclerViewClickListener,
        OnRemoveAllProjectListener {

    private final String LOG_TAG = "EDIT ACTIVITY";
    //protected Handler handler = new Handler();
    @InjectView(R.id.edit_button_fx)
    ImageButton videoFxButton;
    @InjectView(R.id.edit_button_scissor)
    ImageButton scissorButton;
    @InjectView(R.id.edit_button_audio)
    ImageButton audioFxButton;
    /*
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
    */
    @InjectView(R.id.activity_edit_drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.activity_edit_navigation_drawer)
    View navigatorView;
    /*Preview*/
    /*
    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };
    */
    private MediaPlayer musicPlayer;
    /*Navigation*/
    private PreviewVideoListFragment previewVideoListFragment;
    private VideoFxMenuFragment videoFxMenuFragment;
    private AudioFxMenuFragment audioFxMenuFragment;
    private ScissorsFxMenuFragment scissorsFxMenuFragment;
    private LookFxMenuFragment lookFxMenuFragment;
    private MusicGalleryFragment musicGalleryFragment;
    private VideoTimeLineFragment videoTimeLineFragment;
    private TrimPreviewFragment trimFragment;
    /*mvp*/
    private EditPresenter editPresenter;
    /**
     * Tracker google analytics
     */
    private Tracker tracker;
    /**
     * Boolean, register button back pressed to go to record Activity
     */
    private boolean buttonBackPressed = false;
    private ProgressDialog progressDialog;
    //TODO refactor to get rid of the global variable
    private int selectedMusicIndex = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ButterKnife.inject(this);

        VideonaApplication app = (VideonaApplication) getApplication();
        tracker = app.getTracker();

        editPresenter = new EditPresenter(this);

        previewVideoListFragment = new PreviewVideoListFragment();
        scissorsFxMenuFragment = new ScissorsFxMenuFragment();
        audioFxMenuFragment = new AudioFxMenuFragment();
        videoTimeLineFragment = new VideoTimeLineFragment();

        switchFragment(previewVideoListFragment, R.id.edit_fragment_preview);
        switchFragment(scissorsFxMenuFragment, R.id.edit_right_panel);
        switchFragment(videoTimeLineFragment, R.id.edit_bottom_panel);
        scissorButton.setActivated(true);

        editPresenter.onCreate();
        createProgressDialog();
    }

    private void createProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_processing));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setIndeterminate(true);
    }

    @Override
    protected void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
        editPresenter.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
        //releaseVideoView();
        disableMusicPlayer();
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        //handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * Releases the media player and the video view
     */
    /*
    private void releaseVideoView() {
        preview.stopPlayback();
        preview.clearFocus();
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
        disableMusicPlayer();
    }
    */
    /*
    @OnClick(R.id.edit_button_play)
    public void playPausePreview() {

        if (videoPlayer.isPlaying()) {
            pausePreview();
        } else {
            playPreview();
        }
        updateSeekBarProgress();
    }

    private void playPreview() {
        if (videoPlayer != null) {
            videoPlayer.start();
            if (musicPlayer != null) {
                playMusicSyncedWithVideo();
            }
            playButton.setVisibility(View.INVISIBLE);
        }
    }

    private void pausePreview() {
        if (videoPlayer != null && videoPlayer.isPlaying())
            videoPlayer.pause();
        if (musicPlayer != null && musicPlayer.isPlaying())
            musicPlayer.pause();
        playButton.setVisibility(View.VISIBLE);
    }
    */
    @OnClick(R.id.buttonCancelEditActivity)
    public void cancelEditActivity() {
        this.onBackPressed();
    }

    @OnClick(R.id.buttonOkEditActivity)
    public void okEditActivity() {
        //pausePreview();
        previewVideoListFragment.pause();
        showProgressDialog();
        final Runnable r = new Runnable() {
            public void run() {
                editPresenter.startExport();
            }
        };
        performOnBackgroundThread(r);
    }

    @Override
    public void showError(int causeTextResource) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                AlertDialog.THEME_HOLO_LIGHT);
        builder.setMessage(causeTextResource)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void showMessage(final int message) {

        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showProgressDialog() {
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

    }


    @OnClick(R.id.edit_button_fx)
    public void showVideoFxMenu() {
        audioFxButton.setActivated(false);
        videoFxButton.setActivated(true);
        scissorButton.setActivated(false);

        if (videoFxMenuFragment == null)
            videoFxMenuFragment = new VideoFxMenuFragment();
        this.switchFragment(videoFxMenuFragment, R.id.edit_right_panel);
        if (musicGalleryFragment != null)
            this.getFragmentManager().beginTransaction().remove(musicGalleryFragment).commit();
    }

    @OnClick(R.id.edit_button_audio)
    public void showAudioFxMenu() {
        if (!audioFxButton.isActivated()) {
            if (audioFxMenuFragment == null) {
                audioFxMenuFragment = new AudioFxMenuFragment();
            }
            switchFragment(previewVideoListFragment, R.id.edit_fragment_preview);
            switchFragment(audioFxMenuFragment, R.id.edit_right_panel);
            switchFragment(musicGalleryFragment, R.id.edit_bottom_panel);
        }
        scissorButton.setActivated(false);
        audioFxButton.setActivated(true);
        videoFxButton.setActivated(false);
    }


    @OnClick(R.id.edit_button_scissor)
    public void showScissorsFxMenu() {
        audioFxButton.setActivated(false);
        scissorButton.setActivated(true);

        if (scissorsFxMenuFragment == null) {
            scissorsFxMenuFragment = new ScissorsFxMenuFragment();
        }

        this.switchFragment(scissorsFxMenuFragment, R.id.edit_right_panel);
        switchFragment(previewVideoListFragment, R.id.edit_fragment_preview);

        if (videoTimeLineFragment == null) {
            videoTimeLineFragment = new VideoTimeLineFragment();
        }
        switchFragment(videoTimeLineFragment, R.id.edit_bottom_panel);

    }


    @OnClick(R.id.edit_button_look)
    public void showLookFxMenu() {

        scissorButton.setActivated(false);
        audioFxButton.setActivated(false);
        if (lookFxMenuFragment == null)
            lookFxMenuFragment = new LookFxMenuFragment();
        this.switchFragment(lookFxMenuFragment, R.id.edit_right_panel);

        if (musicGalleryFragment != null)
            this.getFragmentManager().beginTransaction().remove(musicGalleryFragment).commit();
    }


    /**
     * Register back pressed to exit app
     */
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(navigatorView);
            return;
        }

        if (buttonBackPressed) {
            editPresenter.cancel();
            finish();
            // Go to RecordActivity
            Intent record = new Intent(this, RecordActivity.class);
            startActivity(record);
            return;
        }
        buttonBackPressed = true;
        Toast.makeText(getApplicationContext(), getString(R.string.toast_exit_edit), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onBackPressed();
        }
        return true;
    }

    private void switchFragment(Fragment f, int panel) {
        getFragmentManager().executePendingTransactions();
        if (!f.isAdded()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(panel, f).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
    }

    @Override
    public void goToShare(String videoToSharePath) {
        //TODO share directly, without activity class
        Intent share = new Intent();
        share.putExtra("VIDEO_EDITED", videoToSharePath);
        share.setClass(this, ShareActivity.class);
        this.startActivity(share);
    }

    @Override
    public void onEffectMenuSelected() {
        if (musicGalleryFragment == null)
            musicGalleryFragment = new MusicGalleryFragment();
        switchFragment(musicGalleryFragment, R.id.edit_bottom_panel);
    }

    @Override
    public void onEffectTrimMenuSelected() {

    }


    /**
     * Method that receives events from Music recyclerview
     *
     * @param position clicked on recycler
     */
    @Override
    public void onClick(int position) {
        //updateSeekBarProgress();
        if (isAlreadySelected(position)) {
            //playPausePreview();
            previewVideoListFragment.playPausePreview();
        } else {
            editPresenter.removeAllMusic();
            if (!isRemoveMusicPressed(position)) {
                List<Music> musicList = musicGalleryFragment.getMusicList();
                Music selectedMusic = musicList.get(position);
                sendButtonTracked(selectedMusic.getIconResourceId());
                editPresenter.addMusic(selectedMusic);
                // TODO: change this variable of 30MB (size of the raw folder)
                if (Utils.isAvailableSpace(30)) {
                    try {
                        Utils.copyMusicResourceToTemp(this, selectedMusic.getMusicResourceId());
                    } catch (IOException e) {
                        //TODO Manejar excepciones como es debido
                    }
                }
            }
            selectedMusicIndex = position;
        }
    }


    @Override
    public void onVideoClicked(int position) {
        //TODO Start trim fragment
        this.getFragmentManager().beginTransaction().remove(videoTimeLineFragment).commit();
        trimFragment = new TrimPreviewFragment();
        Bundle args = new Bundle();
        args.putInt("VIDEO_INDEX", position);
        trimFragment.setArguments(args);
        switchFragment(trimFragment, R.id.edit_fragment_preview);
    }

    private boolean isAlreadySelected(int musicPosition) {
        return selectedMusicIndex == musicPosition;
    }

    private boolean isRemoveMusicPressed(int position) {
        return position == 0;
    }

    @Override
    public void disableMusicPlayer() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    @Override
    public void enableMusicPlayer(Music music) {
        initMusicPlayer(music);
        //playPreviewFromTrimmingStart();
    }


    @Override
    public void initMusicPlayer(Music music) {
        disableMusicPlayer();
        musicPlayer = MediaPlayer.create(this, music.getMusicResourceId());
        musicPlayer.setVolume(0.5f, 0.5f);
        //syncMusicWithVideo(videoPlayer.getCurrentPosition());
    }


    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    /*
    private void updateSeekBarProgress() {
        if (videoPlayer != null) {
            if (videoPlayer.isPlaying())
                seekBar.setProgress(videoPlayer.getCurrentPosition());
            handler.postDelayed(updateTimeTask, 20);
        }
    }
    */


    @Override
    public void refreshStartTimeTag(int time) {
        //startTimeTag.setText(TimeUtils.toFormattedTime(time));
    }

    @Override
    public void refreshStopTimeTag(int time) {
        //stopTimeTag.setText(TimeUtils.toFormattedTime(time));
    }

    @Override
    public void refreshDurationTag(int duration) {
        //durationTag.setText(TimeUtils.toFormattedTime(duration));
    }


    /**
     * OnClick buttons, tracking Google Analytics
     */
    @OnClick
            ({R.id.buttonCancelEditActivity, R.id.buttonOkEditActivity, R.id.edit_button_fx,
                    R.id.edit_button_audio, R.id.edit_button_scissor, R.id.edit_button_look,

            })
    public void clickListener(View view) {
        sendButtonTracked(view.getId());
    }

    /**
     * Sends button clicks to Google Analytics
     *
     * @param id the identifier of the clicked button
     */
    private void sendButtonTracked(int id) {
        Log.d(LOG_TAG, "sendButtonTracked");
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
            case R.drawable.activity_music_icon_ambiental_normal:
                label = "DJ music icon selected";
                break;
            case R.drawable.activity_music_icon_clarinet_normal:
                label = "Clarinet music icon selected";
                break;
            case R.drawable.activity_music_icon_classic_normal:
                label = "Treble clef music icon selected";
                break;
            case R.drawable.activity_music_icon_hip_hop_normal:
                label = "Cap music icon selected";
                break;
            case R.drawable.activity_music_icon_pop_normal:
                label = "Microphone music icon selected";
                break;
            case R.drawable.activity_music_icon_reggae_normal:
                label = "Conga drum music icon selected";
                break;
            case R.drawable.activity_music_icon_violin_normal:
                label = "Violin music icon selected";
                break;
            case R.drawable.activity_music_icon_folk_normal:
                label = "Spanish guitar music icon selected";
                break;
            case R.drawable.activity_music_icon_rock_normal:
                label = "Electric guitar music icon selected";
                break;
            case R.drawable.activity_music_icon_remove_normal:
                label = "Remove music icon selected";
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


    @Override
    public void onRemoveAllProjectSelected() {
        editPresenter.resetProject();
        showMessage(R.string.videos_removed);

        this.getFragmentManager().beginTransaction().remove(videoTimeLineFragment).commit();
    }

}