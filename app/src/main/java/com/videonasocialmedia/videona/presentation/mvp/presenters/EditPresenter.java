/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class EditPresenter {
}//implements OnExportFinishedListener, OnAddMediaFinishedListener,
//    OnRemoveMediaFinishedListener{
//
//        /**
//         * LOG_TAG
//         */
//    private final String LOG_TAG = getClass().getSimpleName();
//
//    /**
//     * Editor View
//     */
//    private EditorView editorView;
//
//    /**
//     * Video Player
//     */
//    private MediaPlayer videoPlayer;
//
//    /**
//     * Audio Player, for music songs.
//     */
//    private MediaPlayer audioPlayer;
//
//    /**
//     * Context
//     */
//    private Context context;
//
//    /**
//     * Export project use case
//     */
//    ExportProjectUseCase exportProjectUseCase;
//
//    /**
//     * Remove video from project use case
//     */
//    RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
//
//    /**
//     * Get media list from project use case
//     */
//    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
//
//    /**
//     * Add Media to Project Use Case
//     */
//    AddVideoToProjectUseCase addVideoToProjectUseCase;
//
//    /**
//     *
//     */
//     boolean isOnExportSuccess = false;
//
//    /**
//     * String path video edited
//     */
//    private String pathVideoEdited;
//
//
//    /**
//     * Constructor
//     *
//     * @param editorView
//     * @param context
//     */
//    public EditPresenter(EditorView editorView, Context context){
//
//        this.editorView=editorView;
//        this.context = context;
//        exportProjectUseCase = new ExportProjectUseCase(context);
//        removeVideoFromProjectUseCase = new RemoveVideoFromProjectUseCase();
//        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
//        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
//    }
//
//
//    /**
//     * Start presenter ¿?
//     */
//    public void start(){
//        //TODO load edition project
//    }
//
//    /**
//     * on Create Presenter
//     */
//    public void onCreate(){
//
//        // Add videoRecorded to EditActivity, only one media
//        //TODO do not use static variable videoRecorded
//
//        LinkedList<Media> listMedia = getMediaListFromProjectUseCase.getMediaListFromProject();
//        String pathMedia = listMedia.getLast().getMediaPath();
//        EditActivity.videoRecorded = pathMedia;
//
//        Log.d(LOG_TAG, "EditPresenter onCreate pathMedia " + pathMedia );
//
//        editorView.initVideoPlayer(pathMedia);
//
//    }
//
//    /**
//     * on Start Presenter
//     */
//    public void onStart(){
//        // TODO edit use case onStart
//    }
//
//
//    /**
//     * on Resume Presenter
//     */
//    public void onResume(){
//        // TODO edit use case onResume
//    }
//
//
//    /**
//     * on Pause Presenter
//     */
//    public void onPause() {
//
//        // TODO edit use case onPause
//    }
//
//    /**
//     * on Restart Presenter
//     */
//    public void onRestart() {
//
//        // TODO edit use case onRestart
//    }
//
//    /**
//     * on Stop Presenter
//     */
//    public void onStop() {
//
//        // TODO edit use case onStop
//    }
//
//    /**
//     * Ok edit button click listener
//     */
//    public void okEditClickListener(){
//
//        exportProjectUseCase.exportProject(this);
//
//    }
//
//    /**
//     * Cancel edit button click listener
//     */
//    public void cancelEditClickListener(){
//
//        LinkedList<Media> listMedia = getMediaListFromProjectUseCase.getMediaListFromProject();
//        ArrayList<Media> list = new ArrayList<Media>(listMedia);
//
//
//        Log.d(LOG_TAG, "EditPresenter  cancelEditClickListener  " + list.get(0).getMediaPath() );
//
//
//        //TODO do this properly. Remove all videos from project, not only one.
//        removeVideoFromProjectUseCase.removeMediaItemsFromProject(list, this);
//
//    }
//
//    @Override
//    public void onExportError() {
//
////        editorView.exportProjectError();
//
//    }
//
//    @Override
//    public void onExportSuccess(String pathVideoEditedDone) {
//
//        isOnExportSuccess = true;
//        pathVideoEdited = pathVideoEditedDone;
//
//        //TODO Change this Delete video from project and addd video Edited
//
//        LinkedList<Media> listMedia = getMediaListFromProjectUseCase.getMediaListFromProject();
//        ArrayList<Media> list = new ArrayList<Media>(listMedia);
//
//        removeVideoFromProjectUseCase.removeMediaItemsFromProject(list, this);
//
//
//    }
//
//    @Override
//    public void onRemoveMediaItemFromTrackError() {
//
//    }
//
//    @Override
//    public void onRemoveMediaItemFromTrackSuccess(MediaTrack mediaTrack) {
//
//        if(isOnExportSuccess) {
//
//            // Add new video edited to project
//
//            Log.d(LOG_TAG, "EditPresenter  onRemoveMediaItemFromTrackSuccess onExportSuccess  " + pathVideoEdited );
//
//            ArrayList<String> listAddMedia = new ArrayList<String>();
//            listAddMedia.add(pathVideoEdited);
//
//            addVideoToProjectUseCase.addMediaItemsToProject(listAddMedia, this);
//
//
//        }
//
//
//    }
//
//    @Override
//    public void onAddMediaItemToTrackError() {
//
//    }
//
//    @Override
//    public void onAddMediaItemToTrackSuccess(MediaTrack mediaTrack) {
//
//        // Navigate to ShareActivity
//        //editorView.navigate(ShareActivity.class);
//    }
//}
