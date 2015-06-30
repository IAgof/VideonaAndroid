/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor.export;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnExportFinishedListener2;


public class ExportProjectUseCase2 implements OnExportEndedListener {

    private OnExportFinishedListener2 onExportFinishedListener;
    private Exporter2 exporter;
    private Project project;

    public ExportProjectUseCase2(OnExportFinishedListener2 onExportFinishedListener) {
        this.onExportFinishedListener = onExportFinishedListener;
        project = Project.getInstance(null, null, null);
        exporter = new ExporterImpl2(project, this);
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
