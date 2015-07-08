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
import com.videonasocialmedia.videona.domain.editor.export.ExportProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class EditPresenter implements OnExportFinishedListener, ModifyVideoDurationlistener,
        OnAddMediaFinishedListener, OnRemoveMediaFinishedListener {

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
    private RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
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
        addMusicToProjectUseCase= new AddMusicToProjectUseCase();
        removeMusicFromProjectUseCase= new RemoveMusicFromProjectUseCase();
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

    public void onResume(){
        
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

    public void prepareMusicPreview(){
        try {
            Project project= Project.getInstance(null,null,null);
            Music music = (Music) project.getAudioTracks().get(0).getItems().get(0);
            editorView.initMusicPlayer(music);
        }catch (Exception e){
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
        exportProjectUseCase.export();
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
        editorView.goToShare(exportedVideo.getMediaPath());
    }
}
