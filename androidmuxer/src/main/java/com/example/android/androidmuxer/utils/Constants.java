package com.example.android.androidmuxer.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Veronica Lago Fominaya on 25/06/2015.
 */
public class Constants {
    public static final String TEMP_DIRECTORY = String.format(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MOVIES)+ File.separator + "Videona" + File.separator +".temp");
    public static final String TEMP_TRIM_DIRECTORY = String.format(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MOVIES)+ File.separator + "Videona" + File.separator +".temp"
            + File.separator +"trim");
}
