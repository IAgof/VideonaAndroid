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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

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

        String nameFile = String.valueOf(rawResourceId);

        // Log.d(LOG_TAG, "copyResourceToTemp " + nameFile);

        File file = new File(Constants.PATH_APP_TEMP + File.separator + nameFile + fileTypeExtensionConstant);

        if (!file.exists()) {
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


    public static File getMusicFileById(int rawResourceId){
        File f= new  File(Constants.PATH_APP_TEMP + File.separator + rawResourceId + Constants.AUDIO_MUSIC_FILE_EXTENSION);
        if (!f.exists())
            f=null;
        return f;
    }

    public static Uri obtainUriToShare(Context context,String videoPath) {
        Uri uri;
        if (videoPath != null) {
            ContentResolver resolver = context.getContentResolver();
            uri = getUriFromContentProvider(resolver,videoPath);
            if (uri == null) {
                uri = createUriToShare(resolver,videoPath);
            }
        } else {
            uri = null;
        }
        return uri;
    }

    private static Uri createUriToShare(ContentResolver resolver, String videoPath) {
        ContentValues content = new ContentValues(4);
        content.put(MediaStore.Video.VideoColumns.TITLE, videoPath);
        content.put(MediaStore.Video.VideoColumns.DATE_ADDED,
                System.currentTimeMillis());
        content.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        content.put(MediaStore.Video.Media.DATA, videoPath);
        return resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                content);
    }

    private static Uri getUriFromContentProvider(ContentResolver resolver, String videoPath) {
        Uri uri = null;
        String[] retCol = {MediaStore.Audio.Media._ID};
        Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                retCol,
                MediaStore.MediaColumns.DATA + "='" + videoPath + "'", null, null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id);
            cursor.close();
        }
        return uri;
    }

    /**
     * Returns whether the current device is running Android 4.4, KitKat, or newer
     *
     */
    public static boolean isKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static void cleanDirectory(File directory) {
        if(directory.exists()) {
            File[] files = directory.listFiles();
            if(files!=null) { //some JVMs return null for empty dirs
                for(File f: files) {
                    if(f.isDirectory()) {
                        cleanDirectory(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
    }

}
