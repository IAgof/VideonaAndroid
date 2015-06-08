package com.videonasocialmedia.videona.domain.editor.export;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Juan Javier Cabanas
 */
public class ExporterImpl implements Exporter {
    @Override
    public Video trimVideo(Video videoToTrim, String outputPath) throws IOException {
        Movie movie = MovieCreator.build(videoToTrim.getMediaPath());
        List<Track> tracks = movie.getTracks();
        movie.setTracks(new LinkedList<Track>());

        //MP4 parser usa segundos en vez de ms
        double startTime = videoToTrim.getFileStartTime() / 1000;
        double endTime = videoToTrim.getFileStopTime() / 1000;
        boolean timeCorrected = false;
        for (Track track : tracks) {
            if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                if (timeCorrected) {
                    // This exception here could be a false positive in case we have multiple tracks
                    // with sync samples at exactly the same positions. E.g. a single movie containing
                    // multiple qualities of the same video (Microsoft Smooth Streaming file)
                    throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
                }
                startTime = correctTimeToSyncSample(track, startTime, false);
                endTime = correctTimeToSyncSample(track, endTime, true);
                timeCorrected = true;
            }
        }

        for (Track track : tracks) {
            long samples[] = getStartAndStopSamples(track, startTime, endTime);
            movie.addTrack(new AppendTrack(new CroppedTrack(track, samples[0], samples[1])));
        }
        Container out = new DefaultMp4Builder().build(movie);

        FileOutputStream fos = new FileOutputStream(String.format(outputPath, startTime, endTime));
        FileChannel fc = fos.getChannel();
        out.writeContainer(fc);
        fc.close();
        fos.close();

        return new Video(outputPath);
    }

    private long[] getStartAndStopSamples(Track track, double startTime, double endTime) {
        long currentSample = 0;
        double currentTime = 0;
        double lastTime = -1;
        long startSample = -1;
        long endSample = -1;

        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (currentTime > lastTime && currentTime <= startTime) {
                // current sample is still before the new starttime
                startSample = currentSample;
            }
            if (currentTime > lastTime && currentTime <= endTime) {
                // current sample is after the new start time and still before the new endtime
                endSample = currentSample;
            }
            lastTime = currentTime;
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;
        }
        return new long[]{startSample, endSample};
    }

    private double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                // samples always start with 1 but we start with zero therefore +1
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;
        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

    @Override
    public Video mergeVideos(List<Video> videoList) {
        return null;
    }

    @Override
    public Video addMusicToVideo(Video video, Music music, String outputPath) throws IOException {
        File musicFile = Utils.getMusicFileById(music.getMusicResourceId());
        if (musicFile != null) {
            Movie videoMovie = MovieCreator.build(video.getMediaPath());
            videoMovie.getTracks().remove(1);
            CroppedTrack musicTrack = trimMusicTrack(musicFile.getPath(), video.getDuration());
            videoMovie.addTrack(new AppendTrack(musicTrack));
            File f = new File(outputPath);
            if (f.exists())
                f.delete();
            {
                Container out = new DefaultMp4Builder().build(videoMovie);
                FileOutputStream fos = new FileOutputStream(new File(outputPath));
                out.writeContainer(fos.getChannel());
                fos.close();
            }
        }
        return new Video(outputPath);
    }

    private CroppedTrack trimMusicTrack(String musicFilePath, double endTime) throws IOException {
        Movie music = MovieCreator.build(musicFilePath);
        Track musicTrack = music.getTracks().get(0);
        //Mp4parser needs seconds instead of milliseconds
        endTime = endTime / 1000;
        if (musicTrack.getSyncSamples() != null && musicTrack.getSyncSamples().length > 0)
            endTime = correctTimeToSyncSample(musicTrack, endTime, true);
        long[] samples = getStartAndStopSamples(musicTrack, 0, endTime);
        return new CroppedTrack(musicTrack, samples[0], samples[1]);
    }

    @Override
    public Video transcodeVideo(Video video) {
        return null;
    }


}
