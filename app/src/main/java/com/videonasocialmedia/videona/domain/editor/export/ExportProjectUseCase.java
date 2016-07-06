/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor.export;

import android.media.MediaMetadataRetriever;

import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.videona.presentation.views.location.DeviceLocation;

import java.io.File;


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
        addInfoToVideo(video);
        onExportFinishedListener.onExportSuccess(video);
    }

    private void addInfoToVideo(final Video videoToAdd) {

        String mediaPath = videoToAdd.getMediaPath();

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mediaPath);
        videoToAdd.setFileDuration(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        videoToAdd.setHeight(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)));
        videoToAdd.setWidth(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)));
        videoToAdd.setRotation(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)));
        videoToAdd.setBitRate(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)));
        videoToAdd.setDate(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));

        File f = new File(mediaPath);
        videoToAdd.setSize(f.length());
        videoToAdd.setTitle(f.getName());

        DeviceLocation.getLastKnownLocation(VideonaApplication.getAppContext(), false, new DeviceLocation.LocationResult() {
            @Override
            public void gotLocationLatLng(double latitude, double longitude) {
                videoToAdd.setLocationLatitude(latitude);
                videoToAdd.setLocationLongitude(longitude);
            }
        });

    }
}
