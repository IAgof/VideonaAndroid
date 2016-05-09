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

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.EditPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;
import com.videonasocialmedia.videona.presentation.mvp.views.TimeLineView;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoPreviewView;
import com.videonasocialmedia.videona.presentation.views.adapter.VideoTimeLineAdapter;
import com.videonasocialmedia.videona.presentation.views.adapter.helper.ItemTouchHelperCallback;
import com.videonasocialmedia.videona.presentation.views.customviews.AspectRatioVideoView;
import com.videonasocialmedia.videona.presentation.views.dialog.VideonaDialog;
import com.videonasocialmedia.videona.presentation.views.listener.OnVideonaDialogListener;
import com.videonasocialmedia.videona.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.recyclerselectionsupport.ItemClickSupport;
import com.videonasocialmedia.videona.utils.recyclerselectionsupport.ItemSelectionSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class EditActivity extends VideonaActivity implements TimeLineView, EditorView,
        VideoTimeLineRecyclerViewClickListener, OnVideonaDialogListener, VideoPreviewView,
        SeekBar.OnSeekBarChangeListener{

    @Bind(R.id.video_editor_preview)
    AspectRatioVideoView videoPreview;
    @Bind(R.id.seekbar_editor_preview)
    SeekBar seekBar;
    @Bind (R.id.button_editor_play_pause)
    ImageButton playButton;

    @Bind(R.id.recyclerview_editor_timeline)
    RecyclerView videoListRecyclerView;

    private MediaController mediaController;
    private MediaPlayer videoPlayer;
    private MediaPlayer musicPlayer;
    private AudioManager audio;
    private int projectDuration = 0;
    private List<Video> videoList;
    private List<Integer> videoStartTimeInProject;
    private List<Integer> videoStopTimeInProject;
    private int currentVideoIndex = 0;
    private int instantTime = 0;
    private boolean isFullScreenBack = false;
    private Project project;
    private Music music;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateSeekBarProgress();
        }
    };
    protected Handler handler = new Handler();

    private EditPresenter editPresenter;

    private VideoTimeLineAdapter timeLineAdapter;
    private final int NUM_COLUMNS_GRID_TIMELINE_HORIZONTAL = 3;
    private final int NUM_COLUMNS_GRID_TIMELINE_VERTICAL = 4;
    private ItemTouchHelper touchHelper;
    private ItemTouchHelper.Callback callback;

    protected ItemClickSupport clickSupport;
    protected ItemSelectionSupport selectionSupport;


    private VideonaDialog dialogRemoveVideoSelected;
    private final int REQUEST_CODE_REMOVE_VIDEO_SELECTED = 0;
    private int selectedVideoRemovePosition;
    private String videoToSharePath;

    private AlertDialog progressDialog;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        initVideoPreview();

        editPresenter = new EditPresenter(this);

        createProgressDialog();

    }

    private void initVideoPreview() {

        updateVideoList();

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);
        mediaController = new MediaController(this);
        mediaController.setVisibility(View.INVISIBLE);
        audio = (AudioManager) getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);


        seekBar.setProgress(0);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseVideoView();
        releaseMusicPlayer();
        projectDuration = 0;
        instantTime = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        editPresenter.obtainVideos();

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            if(bundle.containsKey(Constants.CURRENT_VIDEO_INDEX)) {
                currentVideoIndex = getIntent().getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
                instantTime = videoStartTimeInProject.get(currentVideoIndex);
                initVideoPlayer(videoList.get(currentVideoIndex),
                        videoList.get(currentVideoIndex).getFileStartTime());
                timeLineAdapter.updateSelection(currentVideoIndex);
                seekBar.setProgress(instantTime);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        initVideoListRecycler();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent record = new Intent(this, RecordActivity.class);
        startActivity(record);
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

    ///// GO TO ANOTHER ACTIVITY

    @OnClick(R.id.fab_edit_room)
    public void onClickFabEditor(){
        // navigateTo(Activity.class)
    }

    @OnClick (R.id.button_music_navigator)
    public void onClickMusicNavigator(){
        // navigateTo(Activity.class)
    }

    @OnClick (R.id.button_share_navigator)
    public void onClickShareNavigator(){
       // navigateTo(ShareVideoActivity.class, videoToSharePath);
        pausePreview();
        showProgressDialog();
        final Runnable r = new Runnable() {
            public void run() {
                editPresenter.startExport();
            }
        };
        performOnBackgroundThread(this, r);

    }

    @OnClick (R.id.button_edit_fullscreen)
    public void onClickEditFullscreen(){
        // navigateTo(Activity.class)
    }

    @OnClick (R.id.button_editor_duplicate)
    public void onClickEditDuplicate(){
        navigateTo(VideoDuplicateActivity.class, currentVideoIndex);
    }

    @OnClick (R.id.button_edit_trim)
    public void onClickEditTrim(){
         navigateTo(VideoTrimActivity.class, currentVideoIndex);
    }

    @OnClick (R.id.button_edit_split)
    public void onClickEditSplit(){
        navigateTo(VideoSplitActivity.class, currentVideoIndex);
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
    }

    public void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
    }

    public void navigateTo(Class cls, String videoToSharePath) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.VIDEO_TO_SHARE_PATH, videoToSharePath);
        startActivity(intent);
    }


    @OnClick (R.id.button_editor_play_pause)
    public void onClickPlayPauseButton(){
        if (isVideosOnProject()) {
            if (videoPlayer != null) {
                if (videoPlayer.isPlaying()) {
                    pausePreview();
                    instantTime = seekBar.getProgress();
                } else {
                    playPreview();
                }
            } else {
                playPreview();
            }
        } else {
            seekBar.setProgress(0);
        }
    }

    @OnTouch(R.id.video_editor_preview)
    public boolean onTouchPreview(MotionEvent event) {
        boolean result;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onClickPlayPauseButton();
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                this.onBackPressed();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                return false;
        }
    }

    private boolean isVideosOnProject() {
        List<Media> list = editPresenter.checkVideosOnProject();
        return list.size() > 0;
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
        RecyclerView.LayoutManager layoutManager;
        if (isLandscapeOriented()) {
            layoutManager = new GridLayoutManager(this, NUM_COLUMNS_GRID_TIMELINE_HORIZONTAL,
                    orientation, false);
        } else {
            layoutManager = new GridLayoutManager(this, NUM_COLUMNS_GRID_TIMELINE_VERTICAL,
                    orientation, false);
        }


        videoListRecyclerView.setHasFixedSize(true);
        videoListRecyclerView.setLayoutManager(layoutManager);

        videoListRecyclerView.setAdapter(timeLineAdapter);

        callback = new ItemTouchHelperCallback(timeLineAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(videoListRecyclerView);

    }

    ///// VIDEO TIME LINE VIEW
    @Override
    public void showVideoList(List<Video> videoList) {

        timeLineAdapter.setVideoList(videoList);
        timeLineAdapter.notifyDataSetChanged();
    }

    ////// RECYCLER VIDEO TIME LINE
    @Override
    public void onVideoClicked(int position) {
        //update player to this video
        if(position == currentVideoIndex){
            onClickPlayPauseButton();
        } else {
            currentVideoIndex = position;

            int timeInMsec = videoList.get(position).getFileStartTime();

            if (videoPlayer != null) {
                playNextVideo(videoList.get(position), timeInMsec);
            } else {
                initVideoPlayer(videoList.get(position), timeInMsec);
            }
        }


    }

    @Override
    public void onVideoLongClicked() {
        onClickPlayPauseButton();
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

    ////////// VIDEONA DIALOG

    @Override
    public void onClickPositiveButton(int id) {
        if(id == REQUEST_CODE_REMOVE_VIDEO_SELECTED){
            timeLineAdapter.remove(selectedVideoRemovePosition);
            dialogRemoveVideoSelected.dismiss();
            editPresenter.removeVideoFromProject(videoList.get(selectedVideoRemovePosition));
        }
    }

    @Override
    public void onClickNegativeButton(int id) {

        if(id == REQUEST_CODE_REMOVE_VIDEO_SELECTED){
            dialogRemoveVideoSelected.dismiss();
        }

    }

    ////////// PREVIEW

    @Override
    public void playPreview() {
        updateVideoList();
        playButton.setVisibility(View.INVISIBLE);

    }

    @Override
    public void pausePreview() {
        if (videoPlayer != null && videoPlayer.isPlaying())
            videoPlayer.pause();
        if (musicPlayer != null && musicPlayer.isPlaying())
            musicPlayer.pause();
        playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void seekTo(int timeInMsec) {
        if (videoPlayer != null)
            videoPlayer.seekTo(timeInMsec);
    }

    @Override
    public void updateVideoList() {
        editPresenter.obtainVideos();
    }

    @Override
    public void showPreview(List<Video> videoList) {
        initPreviewLists(videoList);
        seekBar.setMax(projectDuration);
        initPreview();
    }

    @Override
    public void showError(String message) {
        releaseVideoView();
        releaseMusicPlayer();
        Toast.makeText(getApplicationContext(), getString(R.string.toast_add_videos),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateSeekBarDuration(int projectDuration) {
        seekBar.setMax(projectDuration);
    }

    @Override
    public void updateSeekBarSize() {

    }

    ///////// SEEKBAR_LISTENER

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (isVideosOnProject()) {
                Video video = getVideoByProgress(progress);
                int timeInMsec = progress - videoStartTimeInProject.get(currentVideoIndex) +
                        videoList.get(currentVideoIndex).getFileStartTime();

                if (videoPlayer != null) {
                    playNextVideo(video, timeInMsec);
                } else {
                    initVideoPlayer(video, timeInMsec);
                }
                playButton.setVisibility(View.INVISIBLE);
            } else {
                seekBar.setProgress(0);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }



    private void initPreviewLists(List<Video> videoList) {
        projectDuration = 0;
        this.videoList = videoList;
        videoStartTimeInProject = new ArrayList<>();
        videoStopTimeInProject = new ArrayList<>();
        for (Video video : videoList) {
            videoStartTimeInProject.add(projectDuration);
            projectDuration = projectDuration + video.getDuration();
            videoStopTimeInProject.add(projectDuration);
        }
    }

    private void initPreview() {
        if (videoList.size() > 0) {
            Video video = getVideoByProgress(instantTime);
            currentVideoIndex = getPositionVideo(video);
            int timeInMsec = instantTime - videoStartTimeInProject.get(currentVideoIndex) +
                    videoList.get(currentVideoIndex).getFileStartTime();
            if (isFullScreenBack) {
                if (playButton.getVisibility() == View.INVISIBLE)
                    playButton.setVisibility(View.VISIBLE);
                initVideoPlayer(video, timeInMsec);
                isFullScreenBack = false;
            } else {
                if (videoPlayer == null) {
                    initVideoPlayer(video, timeInMsec);
                } else {
                    playNextVideo(video, timeInMsec);
                }
            }
        } else {
            seekBar.setProgress(0);
            playButton.setVisibility(View.VISIBLE);
            currentVideoIndex = 0;
            instantTime = 0;
        }
    }

    private Video getVideoByProgress(int progress) {
        int result = -1;
        if (0 <= progress && progress < videoStopTimeInProject.get(0)) {
            currentVideoIndex = 0;
        } else {
            for (int i = 0; i < videoStopTimeInProject.size(); i++) {
                if (i < videoStopTimeInProject.size() - 1) {
                    boolean inRange = videoStopTimeInProject.get(i) <= progress &&
                            progress < videoStopTimeInProject.get(i + 1);
                    if (inRange) {
                        result = i + 1;
                    }
                }
            }
            if (result == -1) {
                currentVideoIndex = videoStopTimeInProject.size() - 1;
            } else {
                currentVideoIndex = result;
            }
        }
        return videoList.get(currentVideoIndex);
    }

    private int getPositionVideo(Video seekVideo) {
        int position = 0;
        for (Video video : videoList) {
            if (video == seekVideo) {
                position = videoList.indexOf(video);
            }
        }
        return position;
    }

    private void initVideoPlayer(final Video video, final int startTime) {

        videoPreview.setVideoPath(video.getMediaPath());
        videoPreview.setMediaController(mediaController);
        videoPreview.canSeekBackward();
        videoPreview.canSeekForward();
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoPlayer = mp;
                videoPlayer.setVolume(0.5f, 0.5f);
                videoPlayer.setLooping(false);
                videoPlayer.start();
                videoPlayer.seekTo(startTime);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                videoPlayer.pause();
                updateSeekBarProgress();
            }
        });
        videoPreview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, instantTime);
                return false;
            }
        });
        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentVideoIndex++;
                if (hasNextVideoToPlay()) {
                    playNextVideo(videoList.get(currentVideoIndex),
                            videoList.get(currentVideoIndex).getFileStartTime());
                }
            }
        });
        videoPreview.requestFocus();
    }

    private boolean hasNextVideoToPlay() {
        return currentVideoIndex < videoList.size();
    }

    private void playNextVideo(final Video video, final int instantToStart) {

        timeLineAdapter.updateSelection(currentVideoIndex);

        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    videoPlayer.setLooping(false);
                    videoPlayer.start();
                    if (playButton.getVisibility() == View.VISIBLE)
                        playButton.setVisibility(View.INVISIBLE);
                    videoPlayer.seekTo(instantToStart);
                    if (isMusicOnProject()) {
                        muteVideo();
                        playMusicSyncWithVideo();
                    } else {
                        releaseMusicPlayer();
                        videoPlayer.setVolume(0.5f, 0.5f);
                    }
                } catch (Exception e) {
                    // TODO don't force media player. Media player must be null here
                    seekBar.setProgress(0);
                    playButton.setVisibility(View.VISIBLE);
                }
            }
        });
        videoPreview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, instantTime);
                return false;
            }
        });
        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentVideoIndex++;
                if (hasNextVideoToPlay()) {
                    playNextVideo(videoList.get(currentVideoIndex),
                            videoList.get(currentVideoIndex).getFileStartTime());
                } else {
                    releaseView();
                }
            }
        });
        try {
            videoPlayer.reset();
            videoPlayer.setDataSource(video.getMediaPath());
            videoPlayer.prepare();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        videoPreview.requestFocus();
    }

    private boolean isMusicOnProject() {
        project = Project.getInstance(null, null, null);
        return project.getAudioTracks().size() > 0 &&
                project.getAudioTracks().get(0).getItems().size() > 0;
    }

    private void muteVideo() {
        videoPlayer.setVolume(0, 0);
    }

    private void playMusicSyncWithVideo() {
        releaseMusicPlayer();
        initMusicPlayer();
        if (musicPlayer.isPlaying()) {
            musicPlayer.seekTo(seekBar.getProgress());
        } else {
            musicPlayer.start();
            musicPlayer.seekTo(seekBar.getProgress());
        }
    }

    private void releaseMusicPlayer() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    private void initMusicPlayer() {
        if (musicPlayer == null) {
            music = (Music) project.getAudioTracks().get(0).getItems().get(0);
            musicPlayer = MediaPlayer.create(this, music.getMusicResourceId());
            musicPlayer.setVolume(0.5f, 0.5f);
        }
    }

    private void releaseView() {
        playButton.setVisibility(View.VISIBLE);
        releaseVideoView();
        currentVideoIndex = 0;
        if (videoList.size() > 0)
            initVideoPlayer(videoList.get(currentVideoIndex),
                    videoList.get(currentVideoIndex).getFileStartTime() + 100);
        seekBar.setProgress(0);
        instantTime = 0;
        timeLineAdapter.updateSelection(currentVideoIndex);
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.pause();
            releaseMusicPlayer();
        }
    }

    /**
     * Releases the media player and the video view
     */
    private void releaseVideoView() {
        videoPreview.stopPlayback();
        videoPreview.clearFocus();
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
    }

    private void updateSeekBarProgress() {
        if (videoPlayer != null) {
            try {
                if (videoPlayer.isPlaying() && currentVideoIndex < videoList.size()) {
                    seekBar.setProgress(videoPlayer.getCurrentPosition() +
                            videoStartTimeInProject.get(currentVideoIndex) -
                            videoList.get(currentVideoIndex).getFileStartTime());
                   // refreshStartTimeTag(videoSeekBar.getProgress());
                    if (isEndOfVideo()) {
                        currentVideoIndex++;
                        if (hasNextVideoToPlay()) {

                            playNextVideo(videoList.get(currentVideoIndex),
                                    videoList.get(currentVideoIndex).getFileStartTime());
                        } else {
                            releaseView();
                        }
                    }
                }
            } catch (Exception e) {

            }
            handler.postDelayed(updateTimeTask, 20);
        }
    }

    private boolean isEndOfVideo() {
        return seekBar.getProgress() >= videoStopTimeInProject.get(currentVideoIndex);
    }


    private void createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_export_progress, null);
        progressDialog = builder.setCancelable(false)
                .setView(dialogView)
                .create();
    }


    @Override
    public void goToShare(String videoToSharePath) {
        navigateTo(ShareVideoActivity.class, videoToSharePath);
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
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), stringToast, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
