package com.videonasocialmedia.videona.utils;

/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 *
 */


public class ConfigUtils {

    // Audio Settings
    final public static int AUDIO_SAMPLING_RATE = 48000;
    final public static int AUDIO_CHANNELS = 2;
    final public static int AUDIO_ENCODING_BIT_RATE = 192000;

    // Video Settings
    final public static int VIDEO_FRAME_RATE = 30;
    final public static int VIDEO_SIZE_WIDTH = 1280;
    final public static int VIDEO_SIZE_HEIGHT = 720;
    final public static int VIDEO_ENCODING_BIT_RATE = 12000000;

    final public static int MAX_VIDEO_DURATION_MILLIS = 99000;

    public static boolean isAndroidL = false;
}
