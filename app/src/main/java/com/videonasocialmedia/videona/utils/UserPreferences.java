package com.videonasocialmedia.videona.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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

public class UserPreferences {

	private static final String USER_INFO = "USER_INFO";
	private SharedPreferences appUserPreferences;
	private SharedPreferences.Editor prefsEditor;
	private String userName = "user_name_prefs";
	private String userId = "user_id_prefs";
	private String userSession = "user_session";

    private String checkIndiegogo = "check_indiegogo";
    private String checkIndiegogoToday = "check_indiegogo_today";

    private String isMusicON = "isMusicON";

    private String positionMusic = "positionMusic";

    private String videoMusicAux = "videoMusicAux";

    private String seekBarStart = "seekBarStart";
    private String seekBarEnd = "seekBarEnd";
    private String videoProgress = "videoProgress";
    private String videoDuration = "videoDuration";
    private String videoDurationTrim = "videoDurationTrim";



    private String colorEffect = "colorEffect";
    private String isColorEffect = "isColorEffect";
    private String cameraId = "back_camera";
	 
	public UserPreferences(Context context){
	 this.appUserPreferences = context.getSharedPreferences(USER_INFO, Activity.MODE_PRIVATE);
	 this.prefsEditor = appUserPreferences.edit();
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

    public boolean getCheckIndiegogo() {
        return appUserPreferences.getBoolean(checkIndiegogo, true);
    }

    public void setCheckIndiegogo( Boolean welcomeIndiegogo) {
        prefsEditor.putBoolean(checkIndiegogo, welcomeIndiegogo).commit();
    }

    public boolean getIsMusicON() {
        return appUserPreferences.getBoolean(isMusicON, false);
    }

    public void setIsMusicON( Boolean musicON) {
        prefsEditor.putBoolean(isMusicON, musicON).commit();
    }

    public int getPositionMusic() {
        return appUserPreferences.getInt(positionMusic, 0);
    }

    public void setPositionMusic( int position) {
        prefsEditor.putInt(positionMusic, position).commit();
    }

    public String getVideoMusicAux() {
        return appUserPreferences.getString(videoMusicAux, "unkown");
    }

    public void setVideoMusicAux( String username) {
        prefsEditor.putString(videoMusicAux, username).commit();
    }

    public int getSeekBarStart() {
        return appUserPreferences.getInt(seekBarStart, 0);
    }

    public void setSeekBarStart( int start) {
        prefsEditor.putInt(seekBarStart, start).commit();
    }

    public int getSeekBarEnd() {
        return appUserPreferences.getInt(seekBarEnd, 60);
    }

    public void setSeekBarEnd( int end) {
        prefsEditor.putInt(seekBarEnd, end).commit();
    }

    public int getVideoProgress() {
        return appUserPreferences.getInt(videoProgress, 0);
    }

    public void setVideoProgress( int progress) {
        prefsEditor.putInt(videoProgress, progress).commit();
    }

    public int getVideoDuration() {
        return appUserPreferences.getInt(videoDuration, 0);
    }

    public void setVideoDuration( int duration) {
        prefsEditor.putInt(videoDuration, duration).commit();
    }

    public int getVideoDurationTrim() {
        return appUserPreferences.getInt(videoDurationTrim, 0);
    }

    public void setVideoDurationTrim( int duration) {
        prefsEditor.putInt(videoDurationTrim, duration).commit();
    }

    public int getCameraId() {
        return appUserPreferences.getInt(cameraId, 0);
    }

    public void setCameraId( int camera) {
        prefsEditor.putInt(cameraId, camera).commit();
    }


    public String getColorEffect() {
        return appUserPreferences.getString(colorEffect, "unkown");
    }

    public void setColorEffect( String colorEffectSelected) {
        prefsEditor.putString(colorEffect, colorEffectSelected).commit();
    }

    public boolean getIsColorEffect() {
        return appUserPreferences.getBoolean(isColorEffect, false);
    }

    public void setIsColorEffect( Boolean isColorEffectSelected) {
        prefsEditor.putBoolean(isColorEffect, isColorEffectSelected).commit();
    }

}
