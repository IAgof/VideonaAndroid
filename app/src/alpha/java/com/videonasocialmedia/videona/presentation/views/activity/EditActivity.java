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

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.qordoba.sdk.Qordoba;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.eventbus.events.PreviewingVideoChangedEvent;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.EditPresenter;
import com.videonasocialmedia.videona.presentation.mvp.presenters.fx.ScissorsFxPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;
import com.videonasocialmedia.videona.presentation.mvp.views.ScissorsFxView;
import com.videonasocialmedia.videona.presentation.views.dialog.VideonaDialog;
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
import com.videonasocialmedia.videona.presentation.views.listener.OnVideonaDialogListener;
import com.videonasocialmedia.videona.presentation.views.listener.RazorClipListener;
import com.videonasocialmedia.videona.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.videona.utils.AnalyticsConstants;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
import com.videonasocialmedia.videona.utils.TimeUtils;
import com.videonasocialmedia.videona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


/**
 * @author Juan Javier Cabanas Abascal
 */
public class EditActivity extends VideonaActivity implements EditorView, ScissorsFxView,
        MusicRecyclerViewClickListener, VideoTimeLineRecyclerViewClickListener,
        OnRemoveAllProjectListener, DrawerLayout.DrawerListener, OnTrimConfirmListener,
        DuplicateClipListener, RazorClipListener, OnVideonaDialogListener {

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
    @InjectView(R.id.edit_button_ok)
    ImageButton buttonOkEditActivity;
    @InjectView(R.id.edit_button_ok_trim_detail)
    ImageButton buttonOkTrimDetail;
    @InjectView(R.id.activity_edit_drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.activity_edit_navigation_drawer)
    View navigatorView;

    @InjectView(R.id.linear_layout_black_background)
    LinearLayout linearLayoutBlackBackground;
    /**
     * Button navigation drawer
     */
    @InjectView(R.id.button_navigate_drawer)
    ImageButton buttonNavigateDrawer;
    /*Navigation*/
    private PreviewVideoListFragment previewVideoListFragment;
    private VideoFxMenuFragment videoFxMenuFragment;
    private AudioFxMenuFragment audioFxMenuFragment;
    private ScissorsFxMenuFragment scissorsFxMenuFragment;
    private LookFxMenuFragment lookFxMenuFragment;
    private MusicGalleryFragment musicGalleryFragment;
    private VideoTimeLineFragment videoTimeLineFragment;
    private TrimPreviewFragment trimFragment;
    private VideonaDialog dialog;
    private final int REQUEST_CODE_REMOVE_VIDEOS_FROM_PROJECT = 1;
    /*mvp*/
    private EditPresenter editPresenter;
    private ScissorsFxPresenter scissorsFxPresenter;
    /**
     * Tracker google analytics
     */
    private Tracker tracker;
    /**
     * Boolean, register button back pressed to go to record Activity
     */
    private boolean buttonBackPressed = false;
    private AlertDialog progressDialog;
    //TODO refactor to get rid of the global variable
    private int selectedMusicIndex = 0;
    private SharedPreferences sharedPreferences;

    public Thread performOnBackgroundThread(EditActivity parent, final Runnable runnable) {
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.inject(this);
        sharedPreferences = getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);

        VideonaApplication app = (VideonaApplication) getApplication();
        tracker = app.getTracker();

        editPresenter = new EditPresenter(this);
        scissorsFxPresenter = new ScissorsFxPresenter(this);

        previewVideoListFragment = new PreviewVideoListFragment();
        scissorsFxMenuFragment = new ScissorsFxMenuFragment();
        videoTimeLineFragment = new VideoTimeLineFragment();

        switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);
        switchFragment(scissorsFxMenuFragment, R.id.edit_right_panel);
        switchFragment(videoTimeLineFragment, R.id.edit_bottom_panel);
        scissorButton.setActivated(true);

        editPresenter.onCreate();
        createProgressDialog();
        drawerLayout.setDrawerListener(this);
    }

    private void createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_export_progress, null);
        progressDialog = builder.setCancelable(false)
                .setView(dialogView)
                .create();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DownloadMusicTask downloadMusicTask = new DownloadMusicTask();
        downloadMusicTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        editPresenter.onResume();
        scissorsFxPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editPresenter.onPause();
        scissorsFxPresenter.onPause();
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


    @OnClick(R.id.button_navigate_drawer)
    public void navigationDrawerListener() {
        drawerLayout.openDrawer(navigatorView);
    }

    @OnClick(R.id.edit_button_ok)
    public void okEditActivity() {
        pausePreview();
        showProgressDialog();
        mixpanel.timeEvent(AnalyticsConstants.VIDEO_EXPORTED);
        final Runnable r = new Runnable() {
            public void run() {
                editPresenter.startExport();
            }
        };
        performOnBackgroundThread(this, r);
    }

    private void trackVideoExported() {
        JSONObject videoExportedProperties = new JSONObject();
        try {
            int projectDuration = editPresenter.getProjectDuration();
            int numVideosOnProject = editPresenter.getNumVideosOnProject();
            videoExportedProperties.put(AnalyticsConstants.VIDEO_LENGTH, projectDuration);
            videoExportedProperties.put(AnalyticsConstants.RESOLUTION,
                    editPresenter.getResolution());
            videoExportedProperties.put(AnalyticsConstants.NUMBER_OF_CLIPS, numVideosOnProject);
            mixpanel.track(AnalyticsConstants.VIDEO_EXPORTED, videoExportedProperties);
        } catch (JSONException e) {
            Log.e("TRACK_FAILED", String.valueOf(e));
        }
    }

    private void saveVideoFeaturesToConfig() {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, editPresenter.getProjectDuration());
        preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, editPresenter.getNumVideosOnProject());
        preferencesEditor.putString(ConfigPreferences.RESOLUTION, editPresenter.getResolution());
        preferencesEditor.commit();
    }

    @OnClick(R.id.edit_button_ok_trim_detail)
    public void okTrimDetail() {
        pausePreview();
        if (trimFragment != null && trimFragment.isVisible()) {
            switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);
            removeTrimFragment();
        }
        if (musicGalleryFragment != null && musicGalleryFragment.isVisible()) {
            if (scissorsFxMenuFragment == null) {
                scissorsFxMenuFragment = new ScissorsFxMenuFragment();
            }
            switchFragment(videoTimeLineFragment, R.id.edit_bottom_panel);
            switchFragment(scissorsFxMenuFragment, R.id.edit_right_panel);
            if (audioFxButton.isActivated()) {
                scissorButton.setActivated(true);
                audioFxButton.setActivated(false);
            }
            hideOkDetailButton();
        }
    }

    private void hideOkDetailButton() {
        if(buttonOkTrimDetail.getVisibility() == View.VISIBLE) {
            buttonOkTrimDetail.setVisibility(View.GONE);
            buttonOkEditActivity.setVisibility(View.VISIBLE);
        }
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
        VideonaDialog dialog = new VideonaDialog.Builder()
                .withTitle(getString(R.string.error))
                .withMessage(getResources().getString(causeTextResource))
                .create();
        dialog.show(getFragmentManager(), "errorDialog");
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
        hideOkDetailButton();
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
                removeTrimFragment();
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
        hideOkDetailButton();

        if (scissorsFxMenuFragment == null) {
            scissorsFxMenuFragment = new ScissorsFxMenuFragment();
        }

        switchFragment(scissorsFxMenuFragment, R.id.edit_right_panel);
        switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);

        if (videoTimeLineFragment == null) {
            videoTimeLineFragment = new VideoTimeLineFragment();
        }
        switchFragment(videoTimeLineFragment, R.id.edit_bottom_panel);
        //scissorsFxMenuFragment.habilitateTrashButton();

        if (trimFragment != null) {
            removeTrimFragment();
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
        hideOkDetailButton();
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
            if (f instanceof TrimPreviewFragment || f instanceof MusicGalleryFragment) {
                buttonOkEditActivity.setVisibility(View.GONE);
                buttonOkTrimDetail.setVisibility(View.VISIBLE);
            } else if (f instanceof PreviewVideoListFragment) {
                if (buttonOkTrimDetail.getVisibility() == View.VISIBLE) {
                    buttonOkTrimDetail.setVisibility(View.GONE);
                    buttonOkEditActivity.setVisibility(View.VISIBLE);
                    EventBus.getDefault().post(new PreviewingVideoChangedEvent(0, false));
                }
            }
            Qordoba.setCurrentNavigationRoute(panel,this.getClass().getName());
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(panel, f).setTransition(FragmentTransaction.TRANSIT_ENTER_MASK).commit();
        }
    }

    @Override
    public void goToShare(String videoToSharePath) {
        trackVideoExported();
        saveVideoFeaturesToConfig();
        Intent intent = new Intent(this, ShareVideoActivity.class);
        intent.putExtra("VIDEO_EDITED", videoToSharePath);
        startActivity(intent);
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
        //scissorsFxMenuFragment.inhabilitateTrashButton();
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
        dialog = new VideonaDialog.Builder()
                .withTitle(getString(R.string.confirmDeleteVideosFromProjectTitle))
                .withImage(R.drawable.common_icon_eddyt)
                .withMessage(getString(R.string.confirmDeleteVideosFromProjectMessage))
                .withPositiveButton(getString(R.string.yes))
                .withNegativeButton(getString(R.string.no))
                .withCode(REQUEST_CODE_REMOVE_VIDEOS_FROM_PROJECT)
                .withListener(this)
                .create();
        dialog.show(getFragmentManager(), "removeVideosFromProjectDialog");
    }

    private void removeTrimFragment() {
        this.getFragmentManager().beginTransaction().remove(trimFragment).commit();
    }

    private void removeVideoTimelineFragment() {
        this.getFragmentManager().beginTransaction().remove(videoTimeLineFragment).commit();
    }

    @Override
    public void onTrimConfirmed() {
        if (trimFragment != null) {
            removeTrimFragment();
        }

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
        //scissorsFxMenuFragment.habilitateTrashButton();
    }

    @Override
    public void duplicateSelectedClip() {
        int positionInAdapter = getCurrentPosition();
        if (positionInAdapter < 0) {
            Toast.makeText(getApplicationContext(), R.string.addVideosToProject, Toast.LENGTH_SHORT).show();
        } else {
            editPresenter.duplicateClip(getCurrentVideo(), positionInAdapter);
            if (trimFragment != null && trimFragment.isVisible()) {
                switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);
                removeTrimFragment();
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
        if (positionInAdapter < 0) {
            Toast.makeText(getApplicationContext(), R.string.addVideosToProject, Toast.LENGTH_SHORT).show();
        } else {
            int timeVideoInSeekBarInMsec = calculateCutPoint(positionInAdapter);
            int projectDuration = editPresenter.getProjectDuration();
            if (timeVideoInSeekBarInMsec > 0 && (projectDuration - timeVideoInSeekBarInMsec) > 200) {
                Video video = getCurrentVideo();
                if (trimFragment != null && trimFragment.isVisible()) {
                    editPresenter.razorClip(getCurrentVideo(), positionInAdapter, timeVideoInSeekBarInMsec);
                    switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);
                    removeTrimFragment();
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
        if (previewVideoListFragment.isVisible())
            timeVideoInSeekBarInMsec = previewVideoListFragment.getCurrentVideoTimeInMsec(positionInAdapter);
        else if (trimFragment.isVisible())
            timeVideoInSeekBarInMsec = trimFragment.getCurrentVideoTimeInMsec();
        return timeVideoInSeekBarInMsec;
    }

    /**
     * OnClick buttons, tracking Google Analytics
     */
    @OnClick({R.id.button_navigate_drawer, R.id.edit_button_ok, R.id.edit_button_fx,
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
            case R.id.button_navigate_drawer:
                label = "Navigation drawer, show drawer options";
                break;
            case R.id.edit_button_ok:
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
            case R.drawable.activity_music_icon_folk_normal:
                label = "Spanish guitar music icon selected";
                break;
            case R.drawable.activity_music_icon_rock_normal:
                label = "Electric guitar music icon selected";
                break;
            case R.drawable.activity_music_icon_remove_normal:
                label = "Remove music icon selected";
                break;
            case R.drawable.activity_music_icon_birthday_normal:
                label = "Happy birthday music selected";
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
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        buttonNavigateDrawer.setVisibility(View.INVISIBLE);
        linearLayoutBlackBackground.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        buttonNavigateDrawer.setVisibility(View.VISIBLE);
        linearLayoutBlackBackground.setBackground(null);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onClickPositiveButton(int id) {
        if(id == REQUEST_CODE_REMOVE_VIDEOS_FROM_PROJECT) {
            editPresenter.resetProject();
            showMessage(R.string.deletedVideosFromProject);
            removeVideoTimelineFragment();
            if (trimFragment != null && trimFragment.isVisible()) {
                switchFragment(previewVideoListFragment, R.id.edit_fragment_all_preview);
                removeTrimFragment();
            }
            dialog.dismiss();
        }
    }

    @Override
    public void onClickNegativeButton(int id) {
        if(id == REQUEST_CODE_REMOVE_VIDEOS_FROM_PROJECT)
            dialog.dismiss();
    }

    @Override
    public void habilitateTrashButton() {
        scissorsFxMenuFragment.habilitateTrashButton();
    }

    @Override
    public void inhabilitateTrashButton() {
        scissorsFxMenuFragment.inhabilitateTrashButton();
    }

    /**
     * Copy music from raw folder to sdcard
     *
     * AsyncTask, do not block UI
     *
     */
    class DownloadMusicTask extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {

            setupMusicResources();

            return true;
        }
    }

    // Copy music to sdcard from raw folder
    //TODO Develope backend
    private void setupMusicResources() {
        if (Utils.isAvailableSpace(30)) {
            downloadingMusicResources();
        }
    }

    /**
     * Downloads music to sdcard.
     * Downloads items during loading screen, first time the user open the app.
     * Export video engine, need  a music resources in file system, not raw folder.
     * <p/>
     */
    private void downloadingMusicResources() {
        List<Music> musicList = getMusicList();
        for (Music music : musicList) {
            try {
                Utils.copyMusicResourceToTemp(this, music.getMusicResourceId());
            } catch (IOException e) {
                Log.d("Init App", "Error copying resources to temp");
            }
        }
    }

    /**
     * TODO obtaing this List from model
     *
     * @return getMusicList
     */
    private List<Music> getMusicList() {
        List<Music> musicList = new ArrayList<>();
        musicList.add(new Music(R.drawable.activity_music_icon_rock_normal, "audio_rock", R.raw.audio_rock, R.color.colorBlack));
        musicList.add(new Music(R.drawable.activity_music_icon_ambiental_normal, "audio_ambiental", R.raw.audio_ambiental, R.color.colorBlack));
        musicList.add(new Music(R.drawable.activity_music_icon_clarinet_normal, "audio_clasica_flauta", R.raw.audio_clasica_flauta, R.color.colorBlack));
        musicList.add(new Music(R.drawable.activity_music_icon_classic_normal, "audio_clasica_piano", R.raw.audio_clasica_piano, R.color.colorBlack));
        musicList.add(new Music(R.drawable.activity_music_icon_folk_normal, "audio_folk", R.raw.audio_folk, R.color.colorBlack));
        musicList.add(new Music(R.drawable.activity_music_icon_hip_hop_normal, "audio_hiphop", R.raw.audio_hiphop, R.color.colorBlack));
        return musicList;
    }


}