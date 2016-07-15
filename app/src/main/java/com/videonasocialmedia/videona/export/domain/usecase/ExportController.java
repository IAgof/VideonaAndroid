/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.export.domain.usecase;

import android.os.Handler;
import android.os.Message;

import com.videonasocialmedia.videona.export.domain.Exporter;
import com.videonasocialmedia.videona.export.domain.ExporterImpl;
import com.videonasocialmedia.videona.export.domain.callback.ExportProgressListener;
import com.videonasocialmedia.videona.model.entities.editor.Project;

import java.lang.ref.WeakReference;

/**
 * Created by alvaro on 11/07/16.
 */

// Use case
public class ExportController implements ExportProgressListener {

    // TODO:(alvaro.martinez) 11/07/16 Define these codes
    private static final int MSG_PROGRESS_UPDATED = 101;
    private static final int MSG_PROGRESS_SUCCESS = 102;
    private static final int MSG_PROGRESS_ERROR = 103;
    private final Project project;


    private volatile ExportHandler exportHandler;
    private Exporter exporter;

    public ExportController() {

        project = Project.getInstance(null, null, null);

        exporter = new ExporterImpl(project, this);
    }


    public void export(final ExportProgressListener progressListener) {

        exportHandler = new ExportHandler(progressListener);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                exporter.export(project.getTitle());
                exportHandler.sendMessage(exportHandler.obtainMessage(MSG_PROGRESS_SUCCESS));

            }
        };

        Thread t = new Thread(runnable);
        t.start();
    }

    @Override
    public void onExportSuccessFinished(String mediaPath) {
        exportHandler.sendMessage(exportHandler.obtainMessage(MSG_PROGRESS_SUCCESS, mediaPath));
    }

    @Override
    public void onExportError(String error) {
        exportHandler.sendMessage(exportHandler.obtainMessage(MSG_PROGRESS_ERROR, error));
    }

    @Override
    public void onExportProgressUpdated(int progress) {
        exportHandler.sendMessage(exportHandler.obtainMessage(MSG_PROGRESS_UPDATED, progress));
    }


    private static class ExportHandler extends Handler {

        private WeakReference<ExportProgressListener> weakListener;

        public ExportHandler(ExportProgressListener progressListener) {
            weakListener = new WeakReference<>(progressListener);
        }

        /**
         * Called on Encoder thread
         *
         * @param inputMessage
         */
        @Override
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;
            switch (what) {
                case MSG_PROGRESS_UPDATED:
                    weakListener.get().onExportProgressUpdated((Integer) obj);
                    break;
                case MSG_PROGRESS_ERROR:
                    weakListener.get().onExportError((String) obj);
                    break;
                case MSG_PROGRESS_SUCCESS:
                    weakListener.get().onExportSuccessFinished((String) obj);
                    break;
                default:
                    throw new RuntimeException("Unexpected msg what=" + what);
            }
        }

    }
}
