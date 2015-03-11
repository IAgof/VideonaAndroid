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

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.GetVideoRecordedUseCase;
import com.videonasocialmedia.videona.domain.editor.GetVideoRecordedUseCaseController;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;

public class RecordPresenter extends Presenter {
    
    GetVideoRecordedUseCase useCase = new GetVideoRecordedUseCaseController();

    private final RecordView mRecordView;

    public RecordPresenter(RecordView mRecordView) {

        this.mRecordView = mRecordView;
    }

    /**
     * Called when the presenter is initialized
     */
    @Override
    public void start() {

        useCase.startRecordFile();

    }

    /**
     * Called when the presenter is stop, i.e when an activity
     * or a fragment finishes
     */
    @Override
    public void stop() {

        useCase.stopRecordFile();

    }

    /**
     * Called when the activity need a path to save record file data.
     *
     * @return String recordFile
     */
    public String getRecordFileString() {

        return useCase.getRecordFileString();

    }

    /**
     * Called when the user stop to record, save record file duration
     *
     * @param recordFileDuration
     */
    public void setRecordFileDuration(long recordFileDuration){

        useCase.setRecordFileDuration(recordFileDuration);
    }

    /**
     * Called when the user stop to record, save record file duration
     *
     * @param colorEffect
     */
    public void setColorEffect(String colorEffect){

        useCase.setColorEffect(colorEffect);
    }
}
