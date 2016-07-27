/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.export;

/**
 * Created by alvaro on 27/07/16.
 */

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnExportFinishedListener;


public class ExportProjectUseCase implements OnExportEndedListener {

    private OnExportFinishedListener onExportFinishedListener;
    private Exporter exporter;
    private Project project;

    public ExportProjectUseCase(OnExportFinishedListener onExportFinishedListener) {
        this.onExportFinishedListener = onExportFinishedListener;
        project = Project.getInstance(null, null, null);
        exporter = new ExporterImpl(project, this);
    }

    public void export() {
        exporter.export();
    }

    @Override
    public void onExportError(String error) {
        onExportFinishedListener.onExportError(error);
    }

    @Override
    public void onExportSuccess(Video video) {
        onExportFinishedListener.onExportSuccess(video);

    }
}