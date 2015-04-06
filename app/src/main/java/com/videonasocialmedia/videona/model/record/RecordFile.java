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

import java.util.ArrayList;


public class RecordFile {

    String colorEffect;

    long colorEffectTime;

    // cameraId = 0 Back Camera
    // cameraId = 1 Front Camera
    int cameraId;

    long recordFileDuration;

    Uri recordFileUri;

    String recordFilePathString;

    boolean isRecordingFile;

    /**
     * Color Filter, sort
     *
     */
    public ArrayList<String> colorFilter;

    public RecordFile() {

    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId( int camera) {
        this.cameraId = cameraId;
    }

    public void setRecordFileDuration(long recordFileDuration){

        this.recordFileDuration = recordFileDuration;
    }

    public long getRecordFileDuration(){
        return recordFileDuration;
    }

    public void setColorEffect(String colorEffect, long colorEffectTime){
        this.colorEffect = colorEffect;
        this.colorEffectTime = colorEffectTime;
    }

    public String getColorEffect(){
        return colorEffect;
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


    /**
     *  Sort Color Effect List
     *
     * List sort by filter number, AD0, AD5, ... Design
     *
     * @return ArrayList
     */

    public static ArrayList<String> sortColorEffectList(ArrayList<String> colorEffects) {

        ArrayList<String> colorEffectsSorted = new ArrayList<String>();

        //   ArrayList<String> colorEffects = getColorEffectList();


        for(String effect: colorEffects) {

            Log.d("RecordActivity", " colorEffects " + effect);

            if (Constants.COLOR_EFFECT_NONE.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_NONE);

            }

        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_AQUA.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_AQUA);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_BLACKBOARD.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_BLACKBOARD);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_EMBOSS.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_EMBOSS);

            }
        }

        for(String effect: colorEffects) {
            if (Constants.COLOR_EFFECT_MONO.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_MONO);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_NEGATIVE.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_NEGATIVE);

            }
        }

        for(String effect: colorEffects) {
            if (Constants.COLOR_EFFECT_NEON.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_NEON);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_POSTERIZE.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_POSTERIZE);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_SEPIA.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_SEPIA);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_SKETCH.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_SKETCH);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_SOLARIZE.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_SOLARIZE);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_WHITEBOARD.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_WHITEBOARD);

            }
        }


        return colorEffectsSorted;
    }

}
