package com.videonasocialmedia.videona;

import android.os.Environment;

import java.io.File;

public class Config {
	
    final public static String nameFolderVideos = "Videona";
    final public static String nameFolderVideosTrim = "VideonaTrim";
    final public static String nameFolderVideosMusic = "VideonaMusic";
    final public static String nameFolderTemp = ".temp";
    final public static String pathApp = Environment.getExternalStorageDirectory () + File.separator + nameFolderVideos;
    final public static String pathVideoTrim = pathApp + File.separator + nameFolderVideosTrim;
    final public static String pathVideoMusic = pathApp + File.separator + nameFolderVideosMusic;
    final public static String pathVideoTemp = pathApp + File.separator + nameFolderTemp;
    final public static String videoMusicTempFile = pathApp + File.separator + nameFolderTemp + File.separator + "tempAV.mp4";
    final public static String videoCutAuxName = "/videona_trim.mp4";
    final public static String audioMusicExtension = ".m4a";

    final public static int maxDurationVideo = 60;

    //final public static int widthDisplay = 1080;


    // Audio Settings
    final public static int AUDIO_SAMPLING_RATE = 48000;
    final public static int AUDIO_CHANNELS = 2;
    final public static int AUDIO_ENCODING_BIT_RATE = 192000;

    // Video Settings
    final public static int VIDEO_FRAME_RATE = 30;
    final public static int VIDEO_SIZE_WIDTH = 1280;
    final public static int VIDEO_SIZE_HEIGHT = 720;
    final public static int VIDEO_ENCODING_BIT_RATE = 5000000;

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




}
