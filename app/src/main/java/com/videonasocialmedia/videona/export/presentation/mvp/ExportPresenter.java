/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.export.presentation.mvp;

import com.videonasocialmedia.videona.export.domain.usecase.ExportController;
import com.videonasocialmedia.videona.export.domain.callback.ExportProgressListener;

/**
 * Created by alvaro on 11/07/16.
 */
public class ExportPresenter implements ExportProgressListener {

    ExportView exportView;

    public void onCreate(ExportView exportView) {
        this.exportView = exportView;
    }


    public void startExportingProject() {
        ExportController exportControllerUseCase = new ExportController();
        exportControllerUseCase.export(this);
        exportView.showNotification(true);
    }

    @Override
    public void onExportSuccessFinished(String mediaPath) {
        exportView.onSuccessVideoExported(mediaPath);
        exportView.setNotificationProgress(110);
        exportView.showMessage("ExportFinished");
    }

    @Override
    public void onExportError(String error) {
        exportView.showMessage("Error");
    }

    @Override
    public void onExportProgressUpdated(int progress) {

        exportView.setNotificationProgress(80);

    }
}
