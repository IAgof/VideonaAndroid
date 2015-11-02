/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class AppStart {

    /**
     * Distinguishes different kinds of app starts: <li>
     * <ul>
     * First start ever ({@link #FIRST_TIME})
     * </ul>
     * <ul>
     * First start in this version ({@link #FIRST_TIME_VERSION})
     * </ul>
     * <ul>
     * Normal app start ({@link #NORMAL})
     * </ul>
     *
     * @author williscool
     * inspired by
     * @author schnatterer
     *
     * source: http://stackoverflow.com/questions/4636141/determine-if-android-app-is-the-first-time-used
     */
    public enum AppStartState {
        FIRST_TIME, FIRST_TIME_VERSION, NORMAL;
    }

    /**
     * The app version code (not the version name!) that was used on the last
     * start of the app.
     */
    private static final String LAST_APP_VERSION = "1";

    /**
     * Caches the result of {@link #checkAppStart(Context context, SharedPreferences sharedPreferences)}. To allow idempotent method
     * calls.
     */
    private static AppStartState appStartState = null;

    /**
     *  LOG_TAG
     */
    private String LOG_TAG = "AppStart";

    /**
     * Finds out started for the first time (ever or in the current version).
     *
     * @return the type of app start
     */
    public AppStartState checkAppStart(Context context, SharedPreferences sharedPreferences) {
        PackageInfo pInfo;

        try {
            pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            int lastVersionCode = sharedPreferences.getInt(
                    LAST_APP_VERSION, -1);
            // String versionName = pInfo.versionName;
            int currentVersionCode = pInfo.versionCode;
            appStartState = checkAppStart(currentVersionCode, lastVersionCode);

            // Update version in preferences
            sharedPreferences.edit()
                    .putInt(LAST_APP_VERSION, currentVersionCode).commit(); // must use commit here or app may not update prefs in time and app will loop into walkthrough
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(LOG_TAG,
                    "Unable to determine current app version from package manager. Defensively assuming normal app start.");
        }
        return appStartState;
    }

    private AppStartState checkAppStart(int currentVersionCode, int lastVersionCode) {
        if (lastVersionCode == -1) {
            return AppStartState.FIRST_TIME;
        } else if (lastVersionCode < currentVersionCode) {
            return AppStartState.FIRST_TIME_VERSION;
        } else if (lastVersionCode > currentVersionCode) {
            Log.w(LOG_TAG, "Current version code (" + currentVersionCode
                    + ") is less then the one recognized on last startup ("
                    + lastVersionCode
                    + "). Defensively assuming normal app start.");
            return AppStartState.NORMAL;
        } else {
            return AppStartState.NORMAL;
        }
    }

}


// Guide to test this class
/*
public void testCheckAppStart() {
    // First start
    int oldVersion = -1;
    int newVersion = 1;
    assertEquals("Unexpected result", AppStart.FIRST_TIME,
            service.checkAppStart(newVersion, oldVersion));

    // First start this version
    oldVersion = 1;
    newVersion = 2;
    assertEquals("Unexpected result", AppStart.FIRST_TIME_VERSION,
            service.checkAppStart(newVersion, oldVersion));

    // Normal start
    oldVersion = 2;
    newVersion = 2;
    assertEquals("Unexpected result", AppStart.NORMAL,
            service.checkAppStart(newVersion, oldVersion));
}
 */