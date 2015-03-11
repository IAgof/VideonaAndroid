/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.record;

import android.net.Uri;
import android.util.Log;

import com.videonasocialmedia.videona.model.record.RecordFile;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetVideoRecordedUseCaseController implements GetVideoRecordedUseCase {


    public static final int MEDIA_TYPE_VIDEO = 2;
    public static RecordFile recordFile;

    String recordFilePathString;


    public GetVideoRecordedUseCaseController(){

        recordFile = new RecordFile();

    }

    @Override
    public void execute() {




    }

    /**
     * Start to record File
     */
    @Override
    public void startRecordFile() {

        recordFile.setIsRecordingFile(true);

        recordFile.setRecordFilePathString(getRecordFileString());

    }


    /**
     * Stop to record File
     */
    @Override
    public void stopRecordFile() {

        recordFile.setIsRecordingFile(false);

        /// TODO setRecordFileDurationLong
        //recordFile.setRecordFileDurationLong();

    }

    /**
     * Set color effect
     *
     * @param colorEffect
     */
    @Override
    public void setColorEffect(String colorEffect) {

        recordFile.setColorEffect(colorEffect);

    }

    /**
     * @return
     */
    @Override
    public String getRecordFileString() {


        return getOutputRecordFile(MEDIA_TYPE_VIDEO).toString();
    }

    /**
     * Set video duration
     *
     * @param recordFileDurationLong
     */
    @Override
    public void setRecordFileDurationLong(long recordFileDurationLong) {

        recordFile.setRecordFileDurationLong(recordFileDurationLong);

    }

    /**
     * get Record File
     *
     * @return absolute path RecordFile
     */
    public static String getRecordFile(){

        return recordFile.getRecordFilePathString();

    }

    /**
     * set Record File Duration
     *
     * @param videoDuration
     */
    public static void setRecordFileDuration(long videoDuration){

        recordFile.setRecordFileDurationLong(videoDuration);

    }


    /**
     * Create a file Uri for saving video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputRecordFile(type));
    }

    private static File getOutputRecordFile(int type) {

        File mediaStorageDir = new File(Constants.pathApp);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                Log.d("GetVideoRecordedUseCaseController", "failed to create directory");
                return null;

            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
