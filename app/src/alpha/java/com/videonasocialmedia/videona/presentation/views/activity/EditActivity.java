package com.videonasocialmedia.videona.presentation.views.activity;
/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.EditPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;
import com.videonasocialmedia.videona.presentation.views.adapter.VideoTimeLineAdapter;
import com.videonasocialmedia.videona.presentation.views.adapter.helper.ItemTouchHelperCallback;
import com.videonasocialmedia.videona.presentation.views.customviews.ProjectPlayer;
import com.videonasocialmedia.videona.presentation.views.dialog.VideonaDialog;
import com.videonasocialmedia.videona.presentation.views.listener.ProjectPlayerListener;
import com.videonasocialmedia.videona.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.videona.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditActivity extends VideonaActivity implements EditorView,
        ProjectPlayerListener,
//        ProjectPlayerView, SeekBar.OnSeekBarChangeListener,
        VideoTimeLineRecyclerViewClickListener {

    private final int NUM_COLUMNS_GRID_TIMELINE_HORIZONTAL = 3;
    private final int NUM_COLUMNS_GRID_TIMELINE_VERTICAL = 4;
    @Bind(R.id.button_edit_navigator)
    ImageButton navigateToEditButton;
    @Bind(R.id.button_music_navigator)
    ImageButton navigateToMusicButton;
    @Bind(R.id.button_share_navigator)
    ImageButton navigateToShareButton;
    @Bind(R.id.button_edit_duplicate)
    ImageButton editDuplicateButton;
    @Bind(R.id.button_edit_trim)
    ImageButton editTrimButton;
    @Bind(R.id.button_edit_split)
    ImageButton editSplitButton;
    @Bind(R.id.recyclerview_editor_timeline)
    RecyclerView videoListRecyclerView;
    @Bind(R.id.project_player)
    ProjectPlayer projectPlayer;
    private List<Video> videoList;
    private int currentVideoIndex = 0;
    private EditPresenter editPresenter;
    private VideoTimeLineAdapter timeLineAdapter;
    private AlertDialog progressDialog;
    private int selectedVideoRemovePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        setupActivityButtons();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Project project = Project.getInstance(null, null, null);
        projectPlayer.initVideoPreview(project, this);
//        initVideoPreview();

        editPresenter = new EditPresenter(this, projectPlayer);

        createProgressDialog();
        if (savedInstanceState != null) {
            this.currentVideoIndex = savedInstanceState.getInt(Constants.CURRENT_VIDEO_INDEX);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        projectPlayer.destroy();
//        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        projectPlayer.pause();
//        releaseVideoView();
//        releaseMusicPlayer();
//        projectDuration = 0;
//        instantTime = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constants.CURRENT_VIDEO_INDEX)) {
                this.currentVideoIndex = getIntent().getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
            }
        }
        editPresenter.obtainVideos();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initVideoListRecycler();
    }

    private void initVideoListRecycler() {
        int orientation = LinearLayoutManager.VERTICAL;
        int num_grid_columns = NUM_COLUMNS_GRID_TIMELINE_VERTICAL;
        if (isLandscapeOriented()) {
            num_grid_columns = NUM_COLUMNS_GRID_TIMELINE_HORIZONTAL;
        }
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, num_grid_columns,
                orientation, false);
        videoListRecyclerView.setLayoutManager(layoutManager);

        timeLineAdapter = new VideoTimeLineAdapter(this);
        videoListRecyclerView.setAdapter(timeLineAdapter);

        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(timeLineAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(videoListRecyclerView);

    }

    private void setupActivityButtons() {
        navigateToEditButton.setSelected(true);
        tintEditButtons();
    }

    private void createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_export_progress, null);
        progressDialog = builder.setCancelable(false)
                .setView(dialogView)
                .create();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        super.onSaveInstanceState(outState);
    }

    private void tintEditButtons() {
        tintButton(navigateToEditButton);
        tintButton(navigateToMusicButton);
        tintButton(navigateToShareButton);
        tintButton(editDuplicateButton);
        tintButton(editSplitButton);
        tintButton(editTrimButton);
    }

    public static void tintButton(@NonNull ImageButton button) {
        ColorStateList editButtonsColors = button.getResources().getColorStateList(R.color.button_color);
        Drawable button_image = DrawableCompat.wrap(button.getDrawable());
        DrawableCompat.setTintList(button_image, editButtonsColors);
        button.setImageDrawable(button_image);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent record = new Intent(this, RecordActivity.class);
        startActivity(record);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    ///// GO TO ANOTHER ACTIVITY

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings_edit_options:
                navigateTo(SettingsActivity.class);
                return true;
            case R.id.action_settings_edit_gallery:
                navigateTo(GalleryActivity.class);
                return true;
            case R.id.action_settings_edit_tutorial:
                //navigateTo(TutorialActivity.class);
                return true;
            default:

        }

        return super.onOptionsItemSelected(item);
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        if (cls == GalleryActivity.class) {
            intent.putExtra("SHARE", false);
        }
        startActivity(intent);
    }

    @OnClick(R.id.fab_edit_room)
    public void onClickFabEditor() {
        // navigateTo(Activity.class)
        navigateTo(GalleryActivity.class);
    }

    @OnClick(R.id.button_music_navigator)
    public void onClickMusicNavigator() {
        showMessage(R.string.comingSoon);
    }

    @OnClick(R.id.button_share_navigator)
    public void onClickShareNavigator() {
        if (!navigateToShareButton.isEnabled() || videoList.size() == 0)
            return;

        projectPlayer.pausePreview();
        showProgressDialog();
        startExportThread();


    }

    private void startExportThread() {
        final Thread t = new Thread() {
            @Override
            public void run() {
                editPresenter.startExport();
            }
        };
        t.start();
    }

    @OnClick(R.id.button_edit_fullscreen)
    public void onClickEditFullscreen() {
        // navigateTo(Activity.class)
    }

    @OnClick(R.id.button_edit_duplicate)
    public void onClickEditDuplicate() {
        if (!editDuplicateButton.isEnabled())
            return;
        navigateTo(VideoDuplicateActivity.class, currentVideoIndex);
    }

    public void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
    }

    @OnClick(R.id.button_edit_trim)
    public void onClickEditTrim() {
        if (!editTrimButton.isEnabled())
            return;
        navigateTo(VideoTrimActivity.class, currentVideoIndex);
    }

    @OnClick(R.id.button_edit_split)
    public void onClickEditSplit() {
        if (!editSplitButton.isEnabled())
            return;
        navigateTo(VideoSplitActivity.class, currentVideoIndex);
    }

    public void navigateTo(Class cls, String videoToSharePath) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.VIDEO_TO_SHARE_PATH, videoToSharePath);
        startActivity(intent);
    }

    ////// RECYCLER VIDEO TIME LINE
    @Override
    public void onClipClicked(int position) {
        setSelectedClip(position);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                this.onBackPressed();
                return true;
            default:
                return false;
        }
    }

    public void setSelectedClip(int position) {
        currentVideoIndex = position;
        projectPlayer.seekToClip(position);
    }

    @Override
    public void onClipLongClicked() {
        projectPlayer.pausePreview();
    }

    @Override
    public void onClipRemoveClicked(int position) {
        selectedVideoRemovePosition = position;
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        timeLineAdapter.remove(selectedVideoRemovePosition);
                        updateProject();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
        builder.setMessage(R.string.dialog_edit_remove_message).setPositiveButton(R.string.dialog_edit_remove_accept, dialogClickListener)
                .setNegativeButton(R.string.dialog_edit_remove_cancel, dialogClickListener).show();
    }

    @Override
    public void onClipMoved(int toPosition) {
        editPresenter.moveItem(videoList.get(currentVideoIndex), toPosition);
        projectPlayer.updatePreviewTimeLists();
        this.setSelectedClip(toPosition);
//        currentVideoIndex = toPosition;
//        projectPlayer.seekToClip(currentVideoIndex);
    }

    @Override
    public void goToShare(String videoToSharePath) {
        Intent intent = new Intent(this, ShareVideoActivity.class);
        intent.putExtra(Constants.VIDEO_TO_SHARE_PATH, videoToSharePath);
        startActivity(intent);
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void showError(int causeTextResource) {
        VideonaDialog dialog = new VideonaDialog.Builder()
                .withTitle(getString(R.string.error))
                .withMessage(getResources().getString(causeTextResource))
                .create();
        dialog.show(getFragmentManager(), "errorDialog");
    }

    @Override
    public void showMessage(final int stringToast) {
        Snackbar snackbar = Snackbar.make(projectPlayer, stringToast, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void bindVideoList(List<Video> videoList) {
        this.videoList = videoList;
        projectPlayer.initPreviewLists(videoList);
//        projectPlayer.setProjectDuration();
        projectPlayer.initPreview();
        this.setSelectedClip(currentVideoIndex);

//        initPreviewLists(videoList);
//        seekBar.setMax(projectDuration);
//        initPreview();
        timeLineAdapter.setVideoList(videoList);
        timeLineAdapter.updateSelection(currentVideoIndex); // TODO: check this flow and previous updateSelection(0); in setVideoList
        videoListRecyclerView.scrollToPosition(currentVideoIndex);

        timeLineAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateProject() {
        editPresenter.obtainVideos();
    }

    @Override
    public void enableEditActions() {

        navigateToMusicButton.setEnabled(true);
        navigateToShareButton.setEnabled(true);

        editTrimButton.setEnabled(true);
        editSplitButton.setEnabled(true);
        editDuplicateButton.setEnabled(true);

    }

    @Override
    public void disableEditActions() {

        navigateToMusicButton.setEnabled(false);
        navigateToShareButton.setEnabled(false);

        editTrimButton.setEnabled(false);
        editSplitButton.setEnabled(false);
        editDuplicateButton.setEnabled(false);

        projectPlayer.releaseView();
        projectPlayer.setBlackBackgroundColor();
//        releaseView();
//        videoPreview.setBackgroundColor(Color.BLACK);

    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
        currentVideoIndex = currentClipIndex;
        timeLineAdapter.updateSelection(currentClipIndex);
        videoListRecyclerView.scrollToPosition(currentClipIndex);
    }








}
