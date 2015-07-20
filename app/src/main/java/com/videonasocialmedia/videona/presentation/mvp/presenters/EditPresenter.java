/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.util.Log;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.editor.AddMusicToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.CheckIfVideoFilesExistUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.export.ExportProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class EditPresenter implements OnExportFinishedListener, ModifyVideoDurationlistener,
        OnAddMediaFinishedListener, OnRemoveMediaFinishedListener, OnVideosRetrieved {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    Video videoToEdit;
    /**
     * Export project use case
     */
    private ExportProjectUseCase exportProjectUseCase;

    private AddMusicToProjectUseCase addMusicToProjectUseCase;
    private RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
    private RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    private CheckIfVideoFilesExistUseCase checkIfVideoFilesExistUseCase;
    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    /**
     * Editor View
     */
    private EditorView editorView;

    public EditPresenter(EditorView editorView) {
        this.editorView = editorView;
        exportProjectUseCase = new ExportProjectUseCase(this);

        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        addMusicToProjectUseCase = new AddMusicToProjectUseCase();
        removeVideoFromProjectUseCase = new RemoveVideoFromProjectUseCase();
        removeMusicFromProjectUseCase = new RemoveMusicFromProjectUseCase();
        checkIfVideoFilesExistUseCase = new CheckIfVideoFilesExistUseCase();
    }

    /**
     * on Create Presenter
     */
    public void onCreate() {
    }


    /**
     * on Start Presenter
     */
    public void onStart() {
        // TODO edit use case onStart
    }

    public void onResume() {
        checkIfVideoFilesExistUseCase.check();
        
        /*
        List<Media> listMedia = getMediaListFromProjectUseCase.getMediaListFromProject();
        videoToEdit = (Video) listMedia.get(listMedia.size()-1);

        String videoPath = videoToEdit.getMediaPath();
        Log.d(LOG_TAG, "EditPresenter onCreate pathMedia " + videoPath);

        editorView.initVideoPlayer(videoPath);
        editorView.showTrimBar(videoToEdit.getFileDuration(), videoToEdit.getFileStartTime(), videoToEdit.getFileStopTime());
        showTimeTags();
        try {
            editorView.createAndPaintVideoThumbs(videoPath, videoToEdit.getFileDuration());
        } catch (Exception e) {
            //TODO Determine what to do when the thumbs cannot be drawn
        }
        */

    }

    public void prepareMusicPreview() {
        try {
            Project project = Project.getInstance(null, null, null);
            Music music = (Music) project.getAudioTracks().get(0).getItems().get(0);
            editorView.initMusicPlayer(music);
        } catch (Exception e) {
            //do nothing
        }
    }

    private void showTimeTags() {
        editorView.refreshDurationTag(videoToEdit.getDuration());
        editorView.refreshStartTimeTag(videoToEdit.getFileStartTime());
        editorView.refreshStopTimeTag(videoToEdit.getFileStopTime());
    }

    /**
     * Ok edit button click listener
     */
    public void startExport() {
        //editorView.showProgressDialog();

        //check VideoList is not empty, if true exportProjectUseCase
        getMediaListFromProjectUseCase.getMediaListFromProject(this);


        //exportProjectUseCase.export();
    }


    @Override
    public void onVideoDurationModified(Video modifiedVideo) {
        editorView.refreshDurationTag(modifiedVideo.getDuration());
        editorView.refreshStopTimeTag(modifiedVideo.getFileStopTime());
        editorView.refreshStartTimeTag(modifiedVideo.getFileStartTime());
    }

    public void addMusic(Music music) {
        addMusicToProjectUseCase.addMusicToTrack(music, 0, this);
    }

    public void removeMusic(Music music) {
        removeMusicFromProjectUseCase.removeMusicFromProject(music, 0, this);
    }

    public void removeAllMusic() {
        removeMusicFromProjectUseCase.removeAllMusic(0, this);
    }

    @Override
    public void onAddMediaItemToTrackError() {
        //TODO modify error message
        editorView.showError(R.string.addMediaItemToTrackError);
    }

    @Override
    public void onAddMediaItemToTrackSuccess(Media media) {
        if (media instanceof Music)
            editorView.enableMusicPlayer((Music) media);
    }

    @Override
    public void onRemoveMediaItemFromTrackError() {

    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess() {
        editorView.disableMusicPlayer();
    }

    public void cancel() {
    }

    @Override
    public void onExportError(String error) {
        Log.d("error", error);
        editorView.hideProgressDialog();
        //TODO modify error message
        editorView.showError(R.string.addMediaItemToTrackError);
    }

    @Override
    public void onExportSuccess(Video exportedVideo) {
        editorView.hideProgressDialog();
        //editorView.goToShare(exportedVideo.getMediaPath());
        editorView.showError(R.string.addMediaItemToTrackError);
    }

    public void resetProject() {

        Project project = Project.getInstance(null, null, null);
        MediaTrack mediaTrack = project.getMediaTrack();
        LinkedList<Media> listMedia = mediaTrack.getItems();
        ArrayList<Media> items = new ArrayList<>(listMedia);
        if (items.size() > 0) {
            removeVideoFromProjectUseCase.removeMediaItemsFromProject(items, this);
        }

        removeMusicFromProjectUseCase.removeAllMusic(0, this);

    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        exportProjectUseCase.export();
    }

    @Override
    public void onNoVideosRetrieved() {
        // Toast no Video
        editorView.hideProgressDialog();
        editorView.showMessage(R.string.add_videos_to_project);
        // Toast.makeText(this, R.string.add_videos_to_project, Toast.LENGTH_SHORT).show();

    }
}
