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


import com.videonasocialmedia.videona.domain.UseCase;
import com.videonasocialmedia.videona.domain.editor.GetVideoRecordedUseCaseController;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;

public class RecordPresenter extends Presenter {


    private final RecordView mRecordView;

    public RecordPresenter(RecordView mRecordView) {

        this.mRecordView = mRecordView;
    }

    /**
     * Called when the presenter is initialized
     */
    @Override
    public void start() {

        UseCase useCase = new GetVideoRecordedUseCaseController();
        useCase.execute();

    }

    /**
     * Called when the presenter is stop, i.e when an activity
     * or a fragment finishes
     */
    @Override
    public void stop() {

    }
}
