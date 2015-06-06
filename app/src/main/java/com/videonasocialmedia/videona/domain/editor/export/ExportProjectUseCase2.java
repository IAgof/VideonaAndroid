/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor.export;

import android.util.Log;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ExportProjectUseCase2 {

    private OnExportFinishedListener onExportFinishedListener;
    private Exporter exporter;

    public ExportProjectUseCase2(OnExportFinishedListener onExportFinishedListener) {
        this.onExportFinishedListener = onExportFinishedListener;
        exporter=new ExporterImpl();
    }

    public void export() {
        Project project= Project.getInstance(null,null,null);
        String pathVideoEdited = Constants.PATH_APP_EDITED + File.separator + "V_EDIT_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
        Video video=(Video) project.getMediaTrack().getItems().getFirst();

        try {
            video = exporter.trimVideo(video, pathVideoEdited);
        } catch (IOException e) {
            Log.e("ERROR","trimming video", e);
        }

        try {

            Music music = (Music) project.getAudioTracks().get(0).getItems().getFirst();
            String pathWithMusic= Constants.PATH_APP_EDITED + File.separator + "V_EDIT_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "-music.mp4";
            video=exporter.addMusicToVideo(video,music,pathWithMusic);

        }catch (Exception e) {
            Log.e("ERROR","adding Music", e);
            //onExportFinishedListener.onExportError();
        }

        onExportFinishedListener.onExportSuccess(video);

    }
}
