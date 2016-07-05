/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor.export;

import android.os.Handler;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnExportFinishedListener;


public class ExportProjectUseCase implements OnExportEndedListener {

    private volatile Handler exportHandler = new Handler();

    private OnExportFinishedListener onExportFinishedListener;
    private Exporter exporter;

    public ExportProjectUseCase(OnExportFinishedListener onExportFinishedListener) {
        this.onExportFinishedListener = onExportFinishedListener;
        Project project = Project.getInstance(null, null, null);
        exporter = new ExporterImpl(project, this);
    }

    public void export() {

        Thread exportThread = new Thread(new Runnable() {
            @Override
            public void run() {
                exporter.export();
            }
        });
        exportThread.start();
    }

    @Override
    public void onExportError(final String error) {
        exportHandler.post(new Runnable() {
            @Override
            public void run() {
                onExportFinishedListener.onExportError(error);
            }
        });

    }

    @Override
    public void onExportSuccess(final Video video) {

        onExportFinishedListener.onExportSuccess(video);
    }

}
