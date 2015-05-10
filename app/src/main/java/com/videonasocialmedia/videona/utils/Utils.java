/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Verónica Lago Fominaya
 */

package com.videonasocialmedia.videona.utils;

import android.os.Environment;
import android.os.StatFs;

/**
 * Created by Verónica Lago Fominaya on 09/05/2015.
 */
public class Utils {
    /**
     * Checks if there is sufficient space to put the input size in the directory
     *
     * @param size the size in megabytes
     * @return boolean true if there is sufficient space, false if not
     */
    public static boolean isAvailableSpace(float size) {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = stat.getAvailableBytes();
        float megabytesAvailable = bytesAvailable / (1024.f * 1024.f);

        if (size <= megabytesAvailable) {
            return true;
        } else {
            return false;
        }
    }
}
