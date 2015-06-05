/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utils.
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

    public static void copyResourceToTemp(Context ctx, int rawResourceId, String fileTypeExtensionConstant) throws IOException {

        InputStream in = ctx.getResources().openRawResource(rawResourceId);

        String nameFile = ctx.getResources().getResourceName(rawResourceId);
        nameFile = nameFile.substring(nameFile.lastIndexOf("/") + 1);

        // Log.d(LOG_TAG, "copyResourceToTemp " + nameFile);

        File fSong = new File(Constants.PATH_APP_TEMP + File.separator + nameFile + fileTypeExtensionConstant);

        if (!fSong.exists()) {
            try {
                FileOutputStream out = new FileOutputStream(Constants.PATH_APP_TEMP + File.separator + nameFile + fileTypeExtensionConstant);
                byte[] buff = new byte[1024];
                int read = 0;
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
                out.close();
            } catch (FileNotFoundException e) {
                //TODO show error message
            } finally {
                in.close();
            }
        }
    }

    public static void copyMusicResourceToTemp(Context ctx, int rawResourceId) throws IOException {
        copyResourceToTemp(ctx, rawResourceId, Constants.AUDIO_MUSIC_FILE_EXTENSION);
    }
}
