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

    public UserPreferences(Context context) {
        this.appUserPreferences = context.getSharedPreferences(USER_INFO, Activity.MODE_PRIVATE);
        this.prefsEditor = appUserPreferences.edit();
    }


    // Private path
    public String getPrivatePath() {
        return appUserPreferences.getString(privatePath, "");
    }

    public void setPrivatePath(String path) {
        prefsEditor.putString(privatePath, path).commit();
        ;
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

    /*
     // Future use, save last cameraId used

    public int getCameraId() {
        return appUserPreferences.getInt(cameraId, 0);
    }

    public void setCameraId( int camera) {
        prefsEditor.putInt(cameraId, camera).commit();
    }
    */


}
