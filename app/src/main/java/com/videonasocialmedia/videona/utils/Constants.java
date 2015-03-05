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
 */

import android.os.Environment;

import java.io.File;

public class Constants {

    //TODO cambiar el endpoint a la dirección de producción
    public static final String API_ENDPOINT="http://192.168.0.22/Videona/web/app_dev.php/api";

    // Color Effects
    final public static String COLOR_EFFECT_NONE = "none";
    final public static String COLOR_EFFECT_MONO = "mono";
    final public static String COLOR_EFFECT_NEGATIVE = "negative";
    final public static String COLOR_EFFECT_SOLARIZE = "solarize";
    final public static String COLOR_EFFECT_SEPIA = "sepia";
    final public static String COLOR_EFFECT_POSTERIZE = "posterize";
    final public static String COLOR_EFFECT_WHITEBOARD = "whiteboard";
    final public static String COLOR_EFFECT_BLACKBOARD = "blackboard";
    final public static String COLOR_EFFECT_AQUA = "aqua";


    final public static String nameFolderVideos = "Videona";
    final public static String nameFolderVideosTrim = "VideonaTrim";
    final public static String nameFolderVideosMusic = "VideonaMusic";
    final public static String nameFolderTemp = ".temp";

    final public static String pathApp = Environment.getExternalStorageDirectory() + File.separator + nameFolderVideos;

    final public static String pathVideoTrim = pathApp + File.separator + nameFolderVideosTrim;
    final public static String pathVideoMusic = pathApp + File.separator + nameFolderVideosMusic;
    final public static String pathVideoTemp = pathApp + File.separator + nameFolderTemp;

    final public static String videoMusicTempFile = pathApp + File.separator + nameFolderTemp + File.separator + "tempAV.mp4";
    final public static String videoCutAuxName = "/videona_trim.mp4";

    final public static String audioMusicExtension = ".m4a";


}
