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
import android.util.Log;

import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordFile {

    String colorEffect;

    // cameraId = 0 Back Camera
    // cameraId = 1 Front Camera
    int cameraId;

    long recordFileDurationLong;

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

    public void setRecordFileDurationLong(long recordFileDurationLong){

        this.recordFileDurationLong = recordFileDurationLong;
    }

    public long getRecordFileDurationLong(){
        return recordFileDurationLong;
    }

    public void setColorEffect(String colorEffect){
        this.colorEffect = colorEffect;
    }

    public int getVideoDuration() {
        return cameraId;
    }

    public void setVideoDuration (long recordFileDuration) {
        this.recordFileDurationLong = recordFileDuration;
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
