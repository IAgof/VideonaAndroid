/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import android.content.Context;
import android.util.Log;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.track.Track;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnExportProjectFinishedListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.UserPreferences;
import com.videonasocialmedia.videona.utils.VideoUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExportProjectUseCase {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * Context of application
     */
    private Context context;

    /**
     * UserPrefences, temporal values to save data.
     * TODO Use Project data instead of UserPreferences
     */
    UserPreferences userPreferences;

    /**
     * Project of app
     */
    Project project;

    /**
     *  Path VideoEdited
     */
    String pathVideoEdited;

    /**
     * Track
     */
    Track track;

    public ExportProjectUseCase(Context context){

        this.context = context;
        this.project = Project.getInstance(null, null, null);

        this.userPreferences = new UserPreferences(context);
        this.track = project.getMediaTrack();

        pathVideoEdited = Constants.PATH_APP + File.separator + "V_EDIT_" +  new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";

    }

    public void exportProject(OnExportProjectFinishedListener listener) {


        // 1st trimVideo

        int start = userPreferences.getSeekBarStart();
        // +1 seconds, trim extra second. Trimming doesn't work properly
        int length = userPreferences.getSeekBarEnd() - start + 1;

        //TODO obtain inputFileName from loop for
        String inputFileName = track.getItems().getLast().getMediaPath();

        Log.d(LOG_TAG, "inputFilename " + inputFileName);

        //VideonaMainActivity.cut(inputFileName, pathvideoTrim, start, length);

        try {
            VideoUtils.trimVideo(inputFileName, start, userPreferences.getSeekBarEnd(), pathVideoEdited);
        } catch (IOException e) {
            //Log.e(LOG_TAG, "Video Trimm failed", e);
        }

        // Log.d(LOG_TAG, "Video Trimmed");


        if (userPreferences.getIsMusicSelected()) {

            try {

                // 2nd Switch audio

                // Log.d(LOG_TAG, "pathVideoTrim " + pathvideoTrim + "  " + " musicSelected " + musicSelected);

                File fMusicSelected = new File(userPreferences.getMusicSelected());
                if(fMusicSelected.exists()) {
                    VideoUtils.switchAudio(pathVideoEdited, userPreferences.getMusicSelected(), Constants.VIDEO_MUSIC_TEMP_FILE);
                } else {

                  /*  this.runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            //   Toast.makeText(getApplicationContext(), getString(R.string.toast_trim), Toast.LENGTH_SHORT).show();
                        }
                    });

                    return true;
                    */

                }

                // Delete TRIM temporal file in this UserCase
                File fTrim = new File(pathVideoEdited);
                if (fTrim.exists()) {
                    fTrim.delete();
                }

            } catch (IOException e) {
                //Log.e(LOG_TAG, "Video isMusic ON switchAudio failed", e);
            }

            // Log.d(LOG_TAG, "Video isMusic ON switchAudio");


            // 3rd trim Video + Audio


            try {
                VideoUtils.trimVideo(Constants.VIDEO_MUSIC_TEMP_FILE, 0, length, pathVideoEdited);
            } catch (IOException e) {
                //Log.e(LOG_TAG, "Video isMusic ON trimVideo with audio failed", e);
            }

            // Delete TempAV temporal file
            File fTemp = new File(Constants.VIDEO_MUSIC_TEMP_FILE);

            if (fTemp.exists()) {
                fTemp.delete();
            }

            // Log.d(LOG_TAG, "Video isMusic ON trimVideo with audio ");

        }


        File fVideoFinal = new File(pathVideoEdited);
        if (fVideoFinal.exists()) {
            listener.onExportProjectSuccess(pathVideoEdited);
        } else {
            listener.onExportProjectError();
        }

    }
}
