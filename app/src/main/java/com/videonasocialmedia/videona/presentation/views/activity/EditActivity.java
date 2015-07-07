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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.videonasocialmedia.videona.presentation.views.customviews.RangeSeekBar;
import com.videonasocialmedia.videona.presentation.views.fragment.AudioFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.LookFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.MusicGalleryFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.PreviewFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.ScissorsFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.VideoFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectMenuSelectedListener;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerViewClickListener;
import com.videonasocialmedia.videona.utils.Size;
import com.videonasocialmedia.videona.utils.Utils;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;


/**
 * @author Juan Javier Cabanas Abascal
 */
public class EditActivity extends Activity implements EditorView, OnEffectMenuSelectedListener, RecyclerViewClickListener, RangeSeekBar.OnRangeSeekBarChangeListener {

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
    @InjectView(R.id.linearLayoutRangeSeekBar)
    ViewGroup layoutSeekBar;
    @InjectView(R.id.relativeLayoutPreviewVideo)
    RelativeLayout relativeLayoutPreviewVideo;
    @InjectView(R.id.edit_bottom_panel)
    FrameLayout edit_bottom_panel;
    @InjectViews({R.id.imageViewFrame1, R.id.imageViewFrame2, R.id.imageViewFrame3,
            R.id.imageViewFrame4, R.id.imageViewFrame5, R.id.imageViewFrame6})
    List<ImageView> videoThumbs;
    RangeSeekBar<Double> trimBar;
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
    private PreviewFragment previewFragment;
    private VideoFxMenuFragment videoFxMenuFragment;
    private AudioFxMenuFragment audioFxMenuFragment;
    private ScissorsFxMenuFragment scissorsFxMenuFragment;
    private LookFxMenuFragment lookFxMenuFragment;
    private MusicGalleryFragment musicGalleryFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ButterKnife.inject(this);

        VideonaApplication app = (VideonaApplication) getApplication();
        tracker = app.getTracker();

        editPresenter = new EditPresenter(this);

        previewFragment = new PreviewFragment();
        scissorsFxMenuFragment = new ScissorsFxMenuFragment();
        audioFxMenuFragment = new AudioFxMenuFragment();

        FragmentTransaction previewTransaction = getFragmentManager().beginTransaction();
        previewTransaction.add(R.id.edit_fragment_preview, previewFragment).commit();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.edit_right_panel, audioFxMenuFragment).commit();
        audioFxButton.setActivated(true);
        this.onEffectMenuSelected();

        /*
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);

        mediaController = new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);
        */

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
        previewFragment.pause();
        showProgressDialog();
        final Runnable r = new Runnable() {
            public void run() {
                editPresenter.startExport();
            }
        };
        performOnBackgroundThread(r);
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
            switchFragment(audioFxMenuFragment, R.id.edit_right_panel);
            onEffectMenuSelected();
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

        relativeLayoutPreviewVideo.setVisibility(View.VISIBLE);
        if (musicGalleryFragment != null)
            this.getFragmentManager().beginTransaction().remove(musicGalleryFragment).commit();

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

    /*
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
    */

    /**
     * Register back pressed to exit app
     */
    @Override
    public void onBackPressed() {

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

        if (keyCode == KeyEvent.KEYCODE_BACK && buttonBackPressed) {
            // do something on back.

            editPresenter.cancel();
            finish();

            // Go to RecordActivity
            Intent record = new Intent(this, RecordActivity.class);
            startActivity(record);

            return true;
        }


        buttonBackPressed = true;
        Toast.makeText(getApplicationContext(), getString(R.string.toast_exit_edit), Toast.LENGTH_SHORT).show();

       // return super.onKeyDown(keyCode, event);
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
    /*
    @Override
    public void initVideoPlayer(final String videoPath) {

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

                    editPresenter.prepareMusicPreview();
                    pausePreview();
                    updateSeekBarProgress();
                }
            });
            preview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playButton.setVisibility(View.VISIBLE);
                    if (musicPlayer != null && musicPlayer.isPlaying()) {
                        musicPlayer.pause();
                    }
                    updateSeekBarProgress();
                }
            });

            preview.requestFocus();

        }
    }
    */


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
            previewFragment.playPausePreview();
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
        /*
        if (videoPlayer != null)
            videoPlayer.setVolume(0.5f, 0.5f);

        updateSeekBarProgress();

        playPreviewFromTrimmingStart();
        */
    }

    @Override
    public void enableMusicPlayer(Music music) {
        initMusicPlayer(music);
        //playPreviewFromTrimmingStart();
    }

    /*
    private void playPreviewFromTrimmingStart() {
        seekToTrimmingStart();
        playPreview();
    }

    private void seekToTrimmingStart() {
        if (videoPlayer != null) {
            pausePreview();
            int trimBarStart = (int) Math.round(trimBar.getSelectedMinValue());
            videoPlayer.seekTo(trimBarStart);
        }
    }
    */

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
    public void createAndPaintVideoThumbs(final String videoPath, final int videoDuration) throws Exception {
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
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
    public void showTrimBar(int videoFileDuration, int min, int max) {
        //seekBar.setMax(videoFileDuration);
        trimBar = new RangeSeekBar<>(
                (double) 0, (double) videoFileDuration, getBaseContext()
                .getApplicationContext(), videoFileDuration);
        trimBar.setSelectedMinValue((double) min);
        trimBar.setSelectedMaxValue((double) max);
        trimBar.setOnRangeSeekBarChangeListener(this);
        layoutSeekBar.addView(trimBar);
    }

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

    /*
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            videoPlayer.seekTo(progress);
            if (musicPlayer != null)
                syncMusicWithVideo(progress);
        } else {
            if (musicPlayer != null) {
                if (isOnSelectedVideoSection()) {
                    playMusicSyncedWithVideo();
                    videoPlayer.setVolume(0.0f, 0.0f);
                } else {
                    videoPlayer.setVolume(0.5f, 0.5f);
                    musicPlayer.pause();
                }
            }
        }
    }
    */

    private void syncMusicWithVideo(int videoProgress) {
        int trimBarStart = (int) Math.round(trimBar.getSelectedMinValue());
        musicPlayer.seekTo(videoProgress - trimBarStart);
    }

    private boolean isOnSelectedVideoSection() {
        //int videoProgress = videoPlayer.getCurrentPosition();
        int videoProgress = 1;

        int trimBarStart = (int) Math.round(trimBar.getSelectedMinValue());
        int trimBarEnd = (int) Math.round(trimBar.getSelectedMaxValue());
        return videoProgress >= trimBarStart && videoProgress <= trimBarEnd;
    }

    private void playMusicSyncedWithVideo() {
        /*
        if (!musicPlayer.isPlaying() && videoPlayer.isPlaying()) {
            int videoProgress = videoPlayer.getCurrentPosition();
            syncMusicWithVideo(videoProgress);
            musicPlayer.start();
        }
        */
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
        editPresenter.modifyVideoStartTime(startTimeMs);
        editPresenter.modifyVideoFinishTime(finishTimeMs);

        /*
        if (videoPlayer.isPlaying()) {
            playPreviewFromTrimmingStart();
        } else {
            seekToTrimmingStart();
        }
        */

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
}