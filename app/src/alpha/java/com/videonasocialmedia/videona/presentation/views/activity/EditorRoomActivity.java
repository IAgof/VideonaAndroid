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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoTimeLinePresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoTimeLineView;
import com.videonasocialmedia.videona.presentation.views.adapter.VideoTimeLineAdapter;
import com.videonasocialmedia.videona.presentation.views.adapter.helper.ItemTouchHelperCallback;
import com.videonasocialmedia.videona.presentation.views.dialog.VideonaDialog;
import com.videonasocialmedia.videona.presentation.views.listener.OnVideonaDialogListener;
import com.videonasocialmedia.videona.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.GridSpacingItemDecoration;
import com.videonasocialmedia.videona.utils.recyclerselectionsupport.ItemClickSupport;
import com.videonasocialmedia.videona.utils.recyclerselectionsupport.ItemSelectionSupport;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditorRoomActivity extends VideonaActivity implements VideoTimeLineView, VideoTimeLineRecyclerViewClickListener, OnVideonaDialogListener {

    @Bind(R.id.video_editor_preview)
    VideoView videoPreview;

    @Bind(R.id.recyclerview_editor_timeline)
    RecyclerView videoListRecyclerView;

    private VideoTimeLinePresenter timeLinePresenter;
    private VideoTimeLineAdapter timeLineAdapter;
    private final int NUM_COLUMNS_GRID_TIMELINE = 4;
    private ItemTouchHelper touchHelper;
    private ItemTouchHelper.Callback callback;

    protected ItemClickSupport clickSupport;
    protected ItemSelectionSupport selectionSupport;

    protected int selectionMode;

    public static final int SELECTION_MODE_SINGLE = 0;
    public static final int SELECTION_MODE_MULTIPLE = 1;

    private VideonaDialog dialogRemoveVideoSelected;
    private final int REQUEST_CODE_REMOVE_VIDEO_SELECTED = 0;
    private int selectedVideoRemovePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_room);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String path = Constants.PATH_APP + File.separator + "InputVideo.mp4";
        videoPreview.setVideoPath(path);
        videoPreview.seekTo(100);

        timeLinePresenter = new VideoTimeLinePresenter(this);

        initVideoListRecycler();
    }

    @Override
    public void onResume() {
        super.onResume();
        timeLinePresenter.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
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

    @OnClick(R.id.fabEditorRoom)
    public void onClickFabEditor(){
        // navigateTo(Activity.class)
    }

    @OnClick (R.id.button_editor_room_navigator)
    public void onClickEditorRoomNavigator(){
        // navigateTo(Activity.class)
    }

    @OnClick (R.id.button_music_room_navigator)
    public void onClickMusicRoomNavigator(){
        // navigateTo(Activity.class)
    }

    @OnClick (R.id.button_share_room_navigator)
    public void onClickShareRoomNavigator(){
        // navigateTo(Activity.class)
    }

    @OnClick (R.id.button_editor_room_fullscreen)
    public void onClickEditorRoomFullscreen(){
        // navigateTo(Activity.class)
    }

    @OnClick (R.id.button_editor_room_duplicate)
    public void onClickEditorRoomDuplicate(){
        // navigateTo(Activity.class)
    }

    @OnClick (R.id.button_editor_room_trim)
    public void onClickEditorRoomTrim(){
        // navigateTo(Activity.class)
    }

    @OnClick (R.id.button_editor_room_split)
    public void onClickEditorRoomSplit(){
        // navigateTo(Activity.class)
    }

    public void navigateTo(Class cls) {
        startActivity(new Intent(getApplicationContext(), cls));
    }

    private void initVideoListRecycler() {

        clickSupport = ItemClickSupport.addTo(videoListRecyclerView);
        clickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {

                selectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);

                selectionSupport.setItemChecked(position, true);

            }
        });

        selectionSupport = ItemSelectionSupport.addTo(videoListRecyclerView);

        timeLineAdapter = new VideoTimeLineAdapter();
        timeLineAdapter.setSelectionSupport(selectionSupport);
        timeLineAdapter.setClickListener(this);

        int orientation = LinearLayoutManager.VERTICAL;
        if (isLandscapeOriented())
            orientation = LinearLayoutManager.HORIZONTAL;

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, NUM_COLUMNS_GRID_TIMELINE,
                orientation, false);

        videoListRecyclerView.setHasFixedSize(true);
        videoListRecyclerView.setLayoutManager(layoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_layout_margin);
        videoListRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, spacingInPixels, true, 0));

        videoListRecyclerView.setAdapter(timeLineAdapter);

        callback = new ItemTouchHelperCallback(timeLineAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(videoListRecyclerView);

    }

    @Override
    public void showVideoList(List<Video> videoList) {

        timeLineAdapter.setVideoList(videoList);
        timeLineAdapter.notifyDataSetChanged();
    }

    @Override
    public void onVideoClicked(int position) {
        //update player to this video
    }

    @Override
    public void onVideoRemoveClicked(int position) {

        dialogRemoveVideoSelected = new VideonaDialog.Builder()
                .withTitle("Quitar clip de video")
                .withImage(R.drawable.common_icon_eddyt)
                .withMessage(" ")
                .withPositiveButton("SI")
                .withNegativeButton("NO")
                .withCode(REQUEST_CODE_REMOVE_VIDEO_SELECTED)
                .withListener(this)
                .create();

        dialogRemoveVideoSelected.show(getFragmentManager(), "removeVideoSelectedFromTimeline");

        selectedVideoRemovePosition = position;

    }


    @Override
    public void onClickPositiveButton(int id) {
        if(id == REQUEST_CODE_REMOVE_VIDEO_SELECTED){
            timeLineAdapter.remove(selectedVideoRemovePosition);
            dialogRemoveVideoSelected.dismiss();
        }
    }

    @Override
    public void onClickNegativeButton(int id) {

        if(id == REQUEST_CODE_REMOVE_VIDEO_SELECTED){
            dialogRemoveVideoSelected.dismiss();
        }

    }
}
