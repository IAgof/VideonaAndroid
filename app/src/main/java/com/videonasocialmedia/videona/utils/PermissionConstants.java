package com.videonasocialmedia.videona.utils;

import android.Manifest;

/**
 * Created by jca on 13/11/15.
 */
public class PermissionConstants {

    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_CONTACTS = 2;
    public static final int REQUEST_NOTIFICATIONS = 3;
    public static final int REQUEST_CAMERA = 4;
    public static final int REQUEST_AUDIO = 5;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static String[] PERMISSIONS_CONTACTS = {
            Manifest.permission.GET_ACCOUNTS
    };
    public static String[] PERMISSIONS_NOTIFICATIONS = {
            Manifest.permission.RECEIVE_WAP_PUSH
    };
    public static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };
    public static String[] PERMISSIONS_AUDIO = {
            Manifest.permission.RECORD_AUDIO
    };
}
