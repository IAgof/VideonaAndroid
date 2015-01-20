package com.videona.videona;

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

    final public static int maxDurationVideo = 60;

    final public static int widthDisplay = 1080;
    

    

}
