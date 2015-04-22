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

package com.videonasocialmedia.videona.model.record;

import android.net.Uri;


public class RecordFile {

    String colorEffect;

    // cameraId = 0 Back Camera
    // cameraId = 1 Front Camera
    int cameraId;

    long recordFileDuration;

    Uri recordFileUri;

    String recordFilePathString;

    boolean isRecordingFile;

    public RecordFile() {

    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId( int camera) {
        this.cameraId = cameraId;
    }

    public String getColorEffect(){
        return colorEffect;
    }

    public void setRecordFileDuration(long recordFileDuration){

        this.recordFileDuration = recordFileDuration;
    }

    public long getRecordFileDuration(){
        return recordFileDuration;
    }

    public void setColorEffect(String colorEffect){
        this.colorEffect = colorEffect;
    }

    public int getVideoDuration() {
        return cameraId;
    }

    public void setVideoDuration (long recordFileDuration) {
        this.recordFileDuration = recordFileDuration;
    }

    public Uri getRecordFileUri(){
        return recordFileUri;
    }

    public void setRecordFileUri(Uri recordFileUri){
        this.recordFileUri = recordFileUri;
    }

    public String getRecordFilePathString(){
        return recordFilePathString;
    }

    public void setRecordFilePathString(String recordFilePathString){
        this.recordFilePathString = recordFilePathString;
    }

    public boolean getIsRecordingFile(){
        return isRecordingFile;
    }

    public void setIsRecordingFile(boolean isRecordingFile){
        this.isRecordingFile = isRecordingFile;
    }



}
