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
import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.ModifyVideoDurationUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.export.ExportProjectUseCase2;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;

import java.util.LinkedList;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class EditPresenter2 implements OnExportFinishedListener, ModifyVideoDurationlistener, OnAddMediaFinishedListener, OnRemoveMediaFinishedListener {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    Video videoToEdit;
    /**
     * Export project use case
     */
    private ExportProjectUseCase2 exportProjectUseCase;
    private ModifyVideoDurationUseCase modifyVideoDurationUseCase;
    private AddMusicToProjectUseCase addMusicToProjectUseCase;
    private RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    /**
     * Editor View
     */
    private EditorView editorView;

    public EditPresenter2(EditorView editorView) {
        this.editorView = editorView;
        exportProjectUseCase = new ExportProjectUseCase2(this);
        modifyVideoDurationUseCase = new ModifyVideoDurationUseCase();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        addMusicToProjectUseCase= new AddMusicToProjectUseCase();
        removeMusicFromProjectUseCase= new RemoveMusicFromProjectUseCase();
    }

    /**
     * on Create Presenter
     */
    public void onCreate() {
        LinkedList<Media> listMedia = getMediaListFromProjectUseCase.getMediaListFromProject();
        videoToEdit = (Video) listMedia.getLast();

        String videoPath = videoToEdit.getMediaPath();
        Log.d(LOG_TAG, "EditPresenter onCreate pathMedia " + videoPath);

        //Sustituir el path con el video??
        editorView.initVideoPlayer(videoPath);
        editorView.showTrimBar(videoToEdit.getFileDuration());
        showTagsWithInitialValues();
        try {
            editorView.createAndPaintVideoThumbs(videoPath, videoToEdit.getDuration());
        } catch (Exception e) {
            //TODO Determine what to do when the thumbs cannot be drawn
        }
    }

    private void showTagsWithInitialValues() {
        editorView.refreshDurationTag(videoToEdit.getDuration());
        editorView.refreshStartTimeTag(0);
        editorView.refreshStopTimeTag(videoToEdit.getFileStopTime());
    }

    /**
     * on Start Presenter
     */
    public void onStart() {
        // TODO edit use case onStart
    }


    /**
     * Ok edit button click listener
     */
    public void startExport() {
        exportProjectUseCase.export();
    }

    public void modifyVideoStartTime(int startTime) {
        modifyVideoDurationUseCase.modifyVideoStartTime(videoToEdit, startTime, this);
    }

    public void modifyVideoFinishTime(int finishTime) {
        modifyVideoDurationUseCase.modifyVideoFinishTime(videoToEdit, finishTime, this);
    }

    @Override
    public void onVideoDurationModified(Video modifiedVideo) {
        editorView.refreshDurationTag(modifiedVideo.getDuration());
        editorView.refreshStartTimeTag(modifiedVideo.getFileStartTime());
        editorView.refreshStopTimeTag(modifiedVideo.getFileStopTime());
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
    public void onExportError() {
        //editorView.hideProgressDialog();
        //TODO modify error message
        //editorView.showError(R.string.addMediaItemToTrackError);
    }

    @Override
    public void onExportSuccess(Video exportedVideo) {
        editorView.goToShare(exportedVideo.getMediaPath());
    }
}
