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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
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
import com.videonasocialmedia.videona.presentation.views.listener.DuplicateClipListener;
import com.videonasocialmedia.videona.presentation.views.listener.MusicRecyclerViewClickListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnRemoveAllProjectListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnTrimConfirmListener;
import com.videonasocialmedia.videona.presentation.views.listener.RazorClipListener;
import com.videonasocialmedia.videona.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.videona.utils.TimeUtils;
import com.videonasocialmedia.videona.utils.Utils;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * @author Juan Javier Cabanas Abascal
 */
public class EditActivity extends Activity implements EditorView, MusicRecyclerViewClickListener
        , VideoTimeLineRecyclerViewClickListener, OnRemoveAllProjectListener,
        OnTrimConfirmListener, DuplicateClipListener, RazorClipListener {

    private static EditActivity parent;
    private final String LOG_TAG = "EDIT ACTIVITY";
    //protected Handler handler = new Handler();
    @InjectView(R.id.edit_button_scissor)
    ImageButton scissorButton;
    @InjectView(R.id.edit_button_audio)
    ImageButton audioFxButton;
    @InjectView(R.id.project_duration)
    TextView projectDuration;
    @InjectView(R.id.num_videos)
    TextView numVideos;

    @InjectView(R.id.activity_edit_drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.activity_edit_navigation_drawer)
    View navigatorView;
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

    public Thread performOnBackgroundThread(EditActivity parent, final Runnable runnable) {
        this.parent = parent;
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
        videoTimeLineFragment = new VideoTimeLineFragment();

        switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);
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
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        editPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editPresenter.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @OnClick(R.id.buttonCancelEditActivity)
    public void cancelEditActivity() {
        this.onBackPressed();
    }

    @OnClick(R.id.buttonOkEditActivity)
    public void okEditActivity() {
        pausePreview();
        showProgressDialog();
        final Runnable r = new Runnable() {
            public void run() {
                editPresenter.startExport();
            }
        };
        performOnBackgroundThread(this, r);
    }


    void pausePreview() {
        if (previewVideoListFragment.isVisible())
            previewVideoListFragment.pausePreview();
        else if (trimFragment.isVisible()) {
            trimFragment.pausePreview();
        }
    }

    @Override
    public void showError(final int causeTextResource) {
        parent.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent,
                        AlertDialog.THEME_HOLO_LIGHT);
                builder.setMessage(causeTextResource)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public void showMessage(final int message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void updateProjectDuration(int time) {
        projectDuration.setText(TimeUtils.toFormattedTime(time));
    }

    @Override
    public void updateNumVideosInProject(int num) {
        numVideos.setText(String.valueOf(num));
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show();
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
            if (musicGalleryFragment == null) {
                musicGalleryFragment = new MusicGalleryFragment();
            }
            switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);
            switchFragment(audioFxMenuFragment, R.id.edit_right_panel);
            switchFragment(musicGalleryFragment, R.id.edit_bottom_panel);
            if (trimFragment != null) {
                this.getFragmentManager().beginTransaction().remove(trimFragment).commit();
            }
        }
        scissorButton.setActivated(false);
        audioFxButton.setActivated(true);

        // onTrimConfirmed();
    }


    @OnClick(R.id.edit_button_scissor)
    public void showScissorsFxMenu() {
        audioFxButton.setActivated(false);
        scissorButton.setActivated(true);

        if (scissorsFxMenuFragment == null) {
            scissorsFxMenuFragment = new ScissorsFxMenuFragment();
        }

        switchFragment(scissorsFxMenuFragment, R.id.edit_right_panel);
        switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);

        if (videoTimeLineFragment == null) {
            videoTimeLineFragment = new VideoTimeLineFragment();
        }
        switchFragment(videoTimeLineFragment, R.id.edit_bottom_panel);
        scissorsFxMenuFragment.habilitateTrashButton();

        if (trimFragment != null) {
            this.getFragmentManager().beginTransaction().remove(trimFragment).commit();
        }

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
            Intent record = new Intent(this, RecordActivity.class);
            startActivity(record);
            return;
        }
        buttonBackPressed = true;
        Toast.makeText(getApplicationContext(), getString(R.string.toast_exit_edit),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                this.onBackPressed();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                previewVideoListFragment.onKeyDown(keyCode, event);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                previewVideoListFragment.onKeyDown(keyCode, event);
                return true;
            default:
                return false;
        }
    }

    private void switchFragment(Fragment f, int panel) {
        getFragmentManager().executePendingTransactions();
        if (!f.isAdded()) {
            if (f instanceof TrimPreviewFragment) {
                //TODO HAZ VISIBLE TIC
            }else if (f instanceof PreviewVideoListFragment) {
                //TODO HAZ VISIBLE DISCO
            }
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(panel, f).setTransition(FragmentTransaction.TRANSIT_ENTER_MASK).commit();
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


    /**
     * Method that receives events from Music recyclerview
     *
     * @param position clicked on recycler
     */
    @Override
    public void onClick(int position) {
        if (isAlreadySelected(position)) {
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
        trimFragment = new TrimPreviewFragment();
        Bundle args = new Bundle();
        args.putInt("VIDEO_INDEX", position);
        trimFragment.setArguments(args);
        switchFragment(trimFragment, R.id.edit_fragment_trim_preview);
        this.getFragmentManager().beginTransaction().remove(previewVideoListFragment).commit();
        scissorsFxMenuFragment.inhabilitateTrashButton();
    }

    private boolean isAlreadySelected(int musicPosition) {
        return selectedMusicIndex == musicPosition;
    }

    private boolean isRemoveMusicPressed(int position) {
        return position == 0;
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onRemoveAllProjectSelected() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.confirmDeleteVideosFromProjectTitle)
                .setMessage(R.string.confirmDeleteVideosFromProjectMessage)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editPresenter.resetProject();
                        showMessage(R.string.deletedVideosFromProject);
                        removeVideoTimelineFragment();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void removeVideoTimelineFragment() {
        this.getFragmentManager().beginTransaction().remove(videoTimeLineFragment).commit();
    }

    @Override
    public void onTrimConfirmed() {
        this.getFragmentManager().beginTransaction().remove(trimFragment).commit();

        if (!scissorButton.isActivated()) {
            scissorButton.setActivated(true);
            audioFxButton.setActivated(false);
        }

        if (scissorsFxMenuFragment == null) {
            scissorsFxMenuFragment = new ScissorsFxMenuFragment();
        }
        if (videoTimeLineFragment == null) {
            videoTimeLineFragment = new VideoTimeLineFragment();
        }

        switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);
        switchFragment(scissorsFxMenuFragment, R.id.edit_right_panel);
        switchFragment(videoTimeLineFragment, R.id.edit_bottom_panel);
        scissorsFxMenuFragment.habilitateTrashButton();
    }

    @Override
    public void duplicateSelectedClip() {
        int positionInAdapter = getCurrentPosition();
        if(positionInAdapter < 0) {
            Toast.makeText(getApplicationContext(), R.string.addVideosToProject, Toast.LENGTH_SHORT).show();
        } else {
            editPresenter.duplicateClip(getCurrentVideo(), positionInAdapter);
            if(trimFragment != null && trimFragment.isVisible()) {
                switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);
                this.getFragmentManager().beginTransaction().remove(trimFragment).commit();
            }
        }
    }

    private Video getCurrentVideo() {
        return videoTimeLineFragment.getCurrentVideo();
    }

    private int getCurrentPosition() {
        return videoTimeLineFragment.getCurrentPosition();
    }

    @Override
    public void cutSelectedClip() {
        int positionInAdapter = getCurrentPosition();
        if(positionInAdapter < 0) {
            Toast.makeText(getApplicationContext(), R.string.addVideosToProject, Toast.LENGTH_SHORT).show();
        } else {
            int timeVideoInSeekBarInMsec = calculateCutPoint(positionInAdapter);
            Log.d("seekbar", String.valueOf(timeVideoInSeekBarInMsec));
            if(timeVideoInSeekBarInMsec > 0) {
                Video video = getCurrentVideo();
                if(trimFragment != null && trimFragment.isVisible()) {
                    editPresenter.razorClip(getCurrentVideo(), positionInAdapter, timeVideoInSeekBarInMsec);
                    switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);
                    this.getFragmentManager().beginTransaction().remove(trimFragment).commit();
                } else {
                    editPresenter.razorClip(getCurrentVideo(), positionInAdapter, timeVideoInSeekBarInMsec + video.getFileStartTime());
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.invalidRazorTime, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int calculateCutPoint(int positionInAdapter) {
        int timeVideoInSeekBarInMsec = 0;
        if(previewVideoListFragment.isVisible())
            timeVideoInSeekBarInMsec = previewVideoListFragment.getCurrentVideoTimeInMsec(positionInAdapter);
        else if(trimFragment.isVisible())
            timeVideoInSeekBarInMsec = trimFragment.getCurrentVideoTimeInMsec();
        return timeVideoInSeekBarInMsec;
    }

    /**
     * OnClick buttons, tracking Google Analytics
     */
    @OnClick({R.id.buttonCancelEditActivity, R.id.buttonOkEditActivity, R.id.edit_button_fx,
            R.id.edit_button_audio, R.id.edit_button_scissor, R.id.edit_button_look})
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