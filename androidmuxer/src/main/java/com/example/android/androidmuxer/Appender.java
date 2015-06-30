package com.example.android.androidmuxer;

import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Veronica Lago Fominaya on 25/06/2015.
 */
public class Appender {

    private Trimmer trimer;

    public Movie appendVideos(ArrayList<String> videoPaths, boolean addOriginalAudio) throws IOException {

        ArrayList<Movie> movieList = new ArrayList<>();

        for (String video : videoPaths) {
            movieList.add(MovieCreator.build(video));
        }

        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();

        for (Movie m : movieList) {
            for (Track t : m.getTracks()) {
                if(addOriginalAudio) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                }
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
            }
        }

        return createMovie(audioTracks, videoTracks);
    }

    private Movie createMovie(List<Track> audioTracks, List<Track> videoTracks) throws IOException {
        Movie result = new Movie();

        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }

        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        return result;
    }

    public Movie addAudio(Movie movie, ArrayList<String> audioPaths, double movieDuration) throws IOException {

        ArrayList<Movie> audioList = new ArrayList<>();
        trimer = new AudioTrimmer();

        for (String audio : audioPaths) {
            audioList.add(trimer.trim(audio, 0, movieDuration));
        }

        List<Track> audioTracks = new LinkedList<>();

        for (Movie m : audioList) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                }
            }
        }

        if (audioTracks.size() > 0) {
            movie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }

        return movie;
    }

}
