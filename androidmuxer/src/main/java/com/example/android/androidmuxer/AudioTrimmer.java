package com.example.android.androidmuxer;

import com.googlecode.mp4parser.authoring.Movie;

import java.io.IOException;

/**
 * Created by Veronica Lago Fominaya on 25/06/2015.
 */
public class AudioTrimmer extends Trimmer {

    @Override
    public Movie trim(String audioPath, double startTime, double endTime) throws IOException {
        return super.trim(audioPath, startTime, endTime);
    }
}
