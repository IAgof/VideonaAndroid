package com.videonasocialmedia.muxer;

import android.util.Log;

import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Veronica Lago Fominaya
 */
public class Appender {

    public Movie appendVideos(List<String> videoPaths, boolean addOriginalAudio) throws IOException {
        List<Movie> movieList = getMovieList(videoPaths);
        List<Track> videoTracks = new LinkedList<>();
        List<Track> audioTracks = new LinkedList<>();

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

    private List<Movie> getMovieList(List<String> videoPaths) throws IOException {
        List<Movie> movieList = new ArrayList<>();

        for (String videoPath : videoPaths) {
           long start=System.currentTimeMillis();
            Movie movie= MovieCreator.build(videoPath);
            long spent=System.currentTimeMillis()-start;
            Log.d("BUILDING MOVIE", "time spent in millis: " + spent);
            movieList.add(movie);
        }
        return movieList;
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
        Trimmer trimer = new AudioTrimmer();
        List<Track> audioTracks = new LinkedList<>();

        for (String audio : audioPaths) {
            audioList.add(trimer.trim(audio, 0, movieDuration));
        }
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
