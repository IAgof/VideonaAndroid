/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas Abascal
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;
import com.videonasocialmedia.videona.presentation.mvp.views.VideonaPlayerView;
import com.videonasocialmedia.videona.presentation.views.customviews.ToolbarNavigator;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

import java.util.ArrayList;
import java.util.List;

public class EditPresenter implements OnAddMediaFinishedListener, OnRemoveMediaFinishedListener,
        OnVideosRetrieved, OnReorderMediaListener, GetMusicFromProjectCallback {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    /**
     * UseCases
     */
    private RemoveVideoFromProjectUseCase remoVideoFromProjectUseCase;
    private ReorderMediaItemUseCase reorderMediaItemUseCase;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private ToolbarNavigator.ProjectModifiedCallBack projectModifiedCallBack;
    private GetMusicFromProjectUseCase getMusicFromProjectUseCase;
    /**
     * Editor View
     */
    private EditorView editorView;
    private VideonaPlayerView videonaPlayerView;
    private List<Video> videoList;

    public EditPresenter(EditorView editorView, VideonaPlayerView videonaPlayerView,
                         ToolbarNavigator.ProjectModifiedCallBack projectModifiedCallBack) {
        this.editorView = editorView;
        this.videonaPlayerView = videonaPlayerView;
        this.projectModifiedCallBack = projectModifiedCallBack;

        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        remoVideoFromProjectUseCase = new RemoveVideoFromProjectUseCase();
        reorderMediaItemUseCase = new ReorderMediaItemUseCase();
        getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();
    }


    public String getResolution() {
        SharedPreferences sharedPreferences = VideonaApplication.getAppContext().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);

        return sharedPreferences.getString(ConfigPreferences.RESOLUTION, "1280x720");
    }


    public List<Media> checkVideosOnProject() {
        return getMediaListFromProjectUseCase.getMediaListFromProject();
    }

    public void moveItem(int fromPosition, int toPositon) {
        reorderMediaItemUseCase.moveMediaItem(videoList.get(fromPosition), toPositon, this);
    }

    @Override
    public void onAddMediaItemToTrackError() {
        //TODO modify error message
        editorView.showError(R.string.addMediaItemToTrackError);
    }

    @Override
    public void onAddMediaItemToTrackSuccess(Media media) {
    }

    @Override
    public void onRemoveMediaItemFromTrackError() {
        //TODO modify error message
        editorView.showError(R.string.addMediaItemToTrackError);
    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess() {
        editorView.updateProject();
        projectModifiedCallBack.onProjectModified();
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        this.videoList = videoList;
        List<Video> videoCopy = new ArrayList<>(videoList);
        editorView.enableEditActions();
        //videonaPlayerView.bindVideoList(videoList);
        editorView.bindVideoList(videoCopy);
        projectModifiedCallBack.onProjectModified();
    }

    @Override
    public void onNoVideosRetrieved() {
        editorView.disableEditActions();
        editorView.hideProgressDialog();
        editorView.showMessage(R.string.add_videos_to_project);
        editorView.expandFabMenu();
        projectModifiedCallBack.onProjectModified();
    }

    public void removeVideoFromProject(Video selectedVideoRemove) {
        ArrayList<Media> mediaToDeleteFromProject = new ArrayList<>();
        mediaToDeleteFromProject.add(selectedVideoRemove);
        remoVideoFromProjectUseCase.removeMediaItemsFromProject(mediaToDeleteFromProject, this);
    }

    @Override
    public void onMediaReordered(Media media, int newPosition) {
        //If everything was right the UI is already updated since the user did the reordering
        videonaPlayerView.pausePreview();
        editorView.updateProject();
    }

    @Override
    public void onErrorReorderingMedia() {
        //The reordering went wrong so we ask the project for the actual video list
        obtainVideos();
    }

    public void obtainVideos() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    public void loadProject() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
        getMusicFromProjectUseCase.getMusicFromProject(this);
    }

    @Override
    public void onMusicRetrieved(Music music) {
        videonaPlayerView.setMusic(music);
    }
}
