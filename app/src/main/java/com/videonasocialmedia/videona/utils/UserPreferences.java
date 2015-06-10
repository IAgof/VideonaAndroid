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

package com.videonasocialmedia.videona.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String USER_INFO = "USER_INFO";
    private SharedPreferences appUserPreferences;
    private SharedPreferences.Editor prefsEditor;

    private String userName = "user_name_prefs";
    private String userId = "user_id_prefs";
    private String userSession = "user_session";

    private String seekBarStart = "seekBarStart";
    private String seekBarEnd = "seekBarEnd";
    private String videoProgress = "videoProgress";
    private String videoDuration = "videoDuration";
    private String videoDurationTrim = "videoDurationTrim";

    private String colorEffect = "setEffect";
    private String isColorEffect = "isColorEffect";
    private String cameraId = "back_camera";

    private String privatePath = "private_path";

    private String musicSelected = "musicSelected";
    private String isMusicSelected = "isMusicSelected";

    private String frontCameraFlashSupported = "frontCameraFlashSupported";
    private String backCameraFlashSupported = "backCameraFlashSupported";

    private String frontCameraSupported = "frontCameraSupported";
    private String frontCamera720pSupported = "frontCamera720pSupported"; // HD
    private String frontCamera1080pSupported = "frontCamera1080pSupported"; // Full HD

    private String backCamera720pSupported = "backCamera720pSupported";     // HD
    private String backCamera1080pSupported = "backCamera1080pSupported";  // Full HD
    private String backCamera2160pSupported = "backCamera2160pSupported"; // 4k

    private String videoEncodingBitRate = "videoEncodingBitRate";

	 
	public UserPreferences(Context context){
	 this.appUserPreferences = context.getSharedPreferences(USER_INFO, Activity.MODE_PRIVATE);
	 this.prefsEditor = appUserPreferences.edit();
	}
    

    // Private path
    public String getPrivatePath() {
        return appUserPreferences.getString(privatePath, "");
    }

    public void setPrivatePath(String path) {
        prefsEditor.putString(privatePath, path).commit();
        
    }

    /*
    public String getUserId() {
	 return appUserPreferences.getString(userId, "");
	}
	 
	public void setUserId( String userid) {
	 prefsEditor.putString(userId, userid).commit();
	}
	
	public String getUseName() {
	 return appUserPreferences.getString(userName, "unkown");
    }
	 
	public void setUserName( String username) {
	 prefsEditor.putString(userName, username).commit();
	}

	public String getUserSession() {
		return appUserPreferences.getString(userSession, "");
	}

	public void setUserSession(String userkey) {
		prefsEditor.putString(userSession, userkey).commit();;
	}

    */


    //Future delete trimming values
    public int getSeekBarStart() {
        return appUserPreferences.getInt(seekBarStart, 0);
    }

    public void setSeekBarStart(int start) {
        prefsEditor.putInt(seekBarStart, start).commit();
    }

    public int getSeekBarEnd() {
        return appUserPreferences.getInt(seekBarEnd, 60);
    }

    public void setSeekBarEnd(int end) {
        prefsEditor.putInt(seekBarEnd, end).commit();
    }


    //TODO delete this part and obtaind this data from Project
    public String getMusicSelected() {
        return appUserPreferences.getString(musicSelected, "");
    }

    public void setMusicSelected(String pathMusic) {
        prefsEditor.putString(musicSelected, pathMusic).commit();
    }

    public boolean getIsMusicSelected() {
        return appUserPreferences.getBoolean(isMusicSelected, false);
    }

    public void setIsMusicSelected(boolean isMusicON) {
        prefsEditor.putBoolean(isMusicSelected, isMusicON).commit();
    }

    // Front camera flash supported
    public boolean getFrontCameraFlashSupported(){
        return appUserPreferences.getBoolean(frontCameraFlashSupported, false);
    }

    public void setFrontCameraFlashSupported(boolean isFrontCameraFlashSupported){
        prefsEditor.putBoolean(frontCameraFlashSupported, isFrontCameraFlashSupported).commit();
    }

    // Back camera flash supported
    public boolean getBackCameraFlashSupported(){
        return appUserPreferences.getBoolean(backCameraFlashSupported, false);
    }

    public void setBackCameraFlashSupported(boolean isBackCameraFlashSupported){
        prefsEditor.putBoolean(backCameraFlashSupported, isBackCameraFlashSupported).commit();
    }

    // Front camera supported
    public boolean getFrontCameraSupported(){
        return appUserPreferences.getBoolean(frontCameraSupported, false);
    }

    public void setFrontCameraSupported(boolean isFrontCameraSupported){
        prefsEditor.putBoolean(frontCameraSupported, isFrontCameraSupported).commit();
    }

    // Front camera 720p supported
    public boolean getFrontCamera720pSupported(){
        return appUserPreferences.getBoolean(frontCamera720pSupported, false);
    }

    public void setFrontCamera720pSupported(boolean isFrontCamera720pSupported){
        prefsEditor.putBoolean(frontCamera720pSupported, isFrontCamera720pSupported).commit();
    }

    // Front camera 1080p supported
    public boolean getFrontCamera1080pSupported(){
        return appUserPreferences.getBoolean(frontCamera1080pSupported, false);
    }

    public void setFrontCamera1080pSupported(boolean isFrontCamera1080pSupported){
        prefsEditor.putBoolean(frontCamera1080pSupported, isFrontCamera1080pSupported).commit();
    }


    // Back camera 720p supported
    public boolean getBackCamera720pSupported(){
        return appUserPreferences.getBoolean(backCamera720pSupported, false);
    }

    public void setBackCamera720pSupported(boolean isBackCamera720pSupported){
        prefsEditor.putBoolean(backCamera720pSupported, isBackCamera720pSupported).commit();
    }

    // Back camera 1080p supported
    public boolean getBackCamera1080pSupported(){
        return appUserPreferences.getBoolean(backCamera1080pSupported, false);
    }

    public void setBackCamera1080pSupported(boolean isBackCamera1080pSupported){
        prefsEditor.putBoolean(backCamera1080pSupported, isBackCamera1080pSupported).commit();
    }

    // Back camera 2160p supported
    public boolean getBackCamera2160pSupported(){
        return appUserPreferences.getBoolean(backCamera2160pSupported, false);
    }

    public void setBackCamera2160pSupported(boolean isBackCamera2160pSupported){
        prefsEditor.putBoolean(backCamera2160pSupported, isBackCamera2160pSupported).commit();
    }


    // Video Encoder Bit Rate
    public int getVideoEncodingBitRate() {
        return appUserPreferences.getInt(videoEncodingBitRate, 5000000);
    }

    public void setVideoEncodingBitRate( int videoBitRate) {
        prefsEditor.putInt(videoEncodingBitRate, videoBitRate).commit();
    }

    public int getCameraId() {
        return appUserPreferences.getInt(cameraId, 0);
    }

    public void setCameraId( int camera) {
        prefsEditor.putInt(cameraId, camera).commit();
    }



}
