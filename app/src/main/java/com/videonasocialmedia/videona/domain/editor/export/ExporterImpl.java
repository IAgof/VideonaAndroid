package com.videonasocialmedia.videona.domain.editor.export;

import android.os.SystemClock;
import android.util.Log;

import com.example.android.androidmuxer.Appender;
import com.example.android.androidmuxer.AudioTrimmer;
import com.example.android.androidmuxer.Trimmer;
import com.example.android.androidmuxer.VideoTrimmer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.utils.VideoResolution;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.Utils;

import net.ypresto.androidtranscoder.Transcoder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Juan Javier Cabanas
 * @author Verónica Lago Fominaya
 */
public class ExporterImpl implements Exporter {

    private static final String TAG = "Exporter implementation";
    private OnExportEndedListener onExportEndedListener;
    private Project project;
    private Transcoder transcoder;
    private boolean trimCorrect = true;
    private boolean transcodeCorrect = true;
    private ArrayList<String> videoTranscoded;
    private int numFilesToTranscoder = 1;
    private int numFilesTranscoded = 0;
    private String trimTempPath = Constants.PATH_APP_TEMP + File.separator + "trim";
    private String tempTranscodeDirectory = Constants.PATH_APP_TEMP + File.separator + "transcode";
    private String pathVideoEdited;

    public ExporterImpl(Project project, OnExportEndedListener onExportEndedListener) {
        this.onExportEndedListener = onExportEndedListener;
        this.project = project;
    }

    @Override
    public void export() {
        pathVideoEdited = Constants.PATH_APP_EDITED + File.separator + "V_EDIT_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
        LinkedList<Media> medias = getMediasFromProject();
        ArrayList<String> videoTrimmedPaths = trimVideos(medias);
        if(trimCorrect) {
            //transcode(videoTrimmedPaths);
            Movie result = appendFiles(videoTrimmedPaths);
            if(result != null) {
                saveFinalVideo(result);
                Utils.cleanDirectory(new File(trimTempPath));
            }
        }
    }

    private LinkedList<Media> getMediasFromProject() {
        LinkedList<Media> medias = project.getMediaTrack().getItems();
        return medias;
    }

    private ArrayList<String> trimVideos(LinkedList<Media> medias) {
        final File tempDir = new File (trimTempPath);
        if (!tempDir.exists())
            tempDir.mkdirs();
        ArrayList<String> videoTrimmedPaths = new ArrayList<>();
        Trimmer trimmer;
        Movie movie;
        int index = 0;
        do {
            try {
                String videoTrimmedTempPath =  trimTempPath + File.separator + "video_trimmed_" +
                        index + ".mp4";
                int startTime = medias.get(index).getFileStartTime();
                int endTime = medias.get(index).getFileStopTime();
                int editedFileDuration = medias.get(index).getFileStopTime() - medias.get(index).getFileStartTime();
                int originalFileDuration = ((Video)medias.get(index)).getFileDuration();
                if(editedFileDuration < originalFileDuration) {
                    trimmer = new VideoTrimmer();
                    movie = trimmer.trim(medias.get(index).getMediaPath(), startTime, endTime);
                    com.example.android.androidmuxer.utils.Utils.createFile(movie, videoTrimmedTempPath);
                    videoTrimmedPaths.add(videoTrimmedTempPath);
                } else {
                    videoTrimmedPaths.add(medias.get(index).getMediaPath());
                }
            } catch (IOException | NullPointerException e) {
                trimCorrect = false;
                videoTrimmedPaths = null;
                onExportEndedListener.onExportError(String.valueOf(e));
            }
            index++;
        } while(trimCorrect && medias.size() > index);

        return videoTrimmedPaths;
    }

    private void transcode(ArrayList<String> videoPaths) {
        final long startTime = SystemClock.uptimeMillis();
        videoTranscoded = new ArrayList<>();
        transcoder = createTranscoder();
        Transcoder.Listener listener = new Transcoder.Listener() {
            @Override
            public void onTranscodeProgress(double progress) {}

            @Override
            public void onTranscodeCompleted(String path) {
                Log.d(TAG, "transcoding finished listener");
                Log.d(TAG, "transcoding took " + (SystemClock.uptimeMillis() - startTime) + "ms");
                videoTranscoded.add(path);
            }

            @Override
            public void onTranscodeFinished() {
                numFilesTranscoded++;
                if (numFilesTranscoded == numFilesToTranscoder) {
                    Movie result = appendFiles(videoTranscoded);
                    if(result != null) {
                        saveFinalVideo(result);
                    }
                    numFilesTranscoded = 0;
                }
            }

            @Override
            public void onTranscodeFailed(Exception exception) {
                transcodeCorrect = false;
                numFilesTranscoded = 0;
                videoTranscoded = null;
                onExportEndedListener.onExportError(String.valueOf(exception));
            }
        };
        int index = 0;
        do {
            String video = videoPaths.get(index);
            try {
                transcoder.transcodeFile(video, listener);
            } catch (IOException | NullPointerException e) {
                Log.d(TAG, String.valueOf(e));
            }
            index++;
        } while(transcodeCorrect && videoPaths.size() > index);
    }

    private Transcoder createTranscoder() {
        VideoResolution.Resolution resolution = project.getProfile().getResolution();
        final File tempDir = new File (tempTranscodeDirectory);
        if (!tempDir.exists())
            tempDir.mkdirs();
        switch (resolution) {
            case HD720:
                return new Transcoder(Transcoder.Resolution.HD720, tempTranscodeDirectory);
            case HD1080:
                return new Transcoder(Transcoder.Resolution.HD1080, tempTranscodeDirectory);
            case HD4K:
                return new Transcoder(Transcoder.Resolution.HD4K, tempTranscodeDirectory);
            default:
                return new Transcoder(Transcoder.Resolution.HD720, tempTranscodeDirectory);
        }
    }

    private Movie appendFiles(ArrayList<String> videoTranscoded) {
        Movie result;
        if (isMusicOnProject()) {
            Movie merge = appendVideos(videoTranscoded, false);

            Music music = (Music) project.getAudioTracks().get(0).getItems().getFirst();
            File musicFile = Utils.getMusicFileById(music.getMusicResourceId());
            ArrayList<String> audio = new ArrayList<>();
            audio.add(musicFile.getPath());

            result = addAudio(merge, audio, project.getMediaTrack().getDuration());
        } else {
            result = appendVideos(videoTranscoded, true);
        }
        return result;
    }

    private boolean isMusicOnProject() {
        return project.getAudioTracks().size() > 0 && project.getAudioTracks().get(0).getItems().size() > 0;
    }

    private Movie appendVideos(ArrayList<String> videoTranscodedPaths, boolean addOriginalAudio) {
        Appender appender = new Appender();
        Movie merge;
        try {
            merge = appender.appendVideos(videoTranscodedPaths, addOriginalAudio);
        } catch (Exception e) {
            merge = null;
            onExportEndedListener.onExportError(String.valueOf(e));
        }
        return merge;
    }

    private Movie addAudio(Movie movie, ArrayList<String> audioPaths, double movieDuration) {
        ArrayList<Movie> audioList = new ArrayList<>();
        List<Track> audioTracks = new LinkedList<>();
        Trimmer trimmer = new AudioTrimmer();

        // TODO change this for do while
        for (String audio : audioPaths) {
            try {
                audioList.add(trimmer.trim(audio, 0, movieDuration));
            } catch (IOException | NullPointerException e) {
                onExportEndedListener.onExportError(String.valueOf(e));
            }
        }

        for (Movie m : audioList) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                }
            }
        }

        if (audioTracks.size() > 0) {
            try {
                movie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            } catch (IOException | NullPointerException e) {
                onExportEndedListener.onExportError(String.valueOf(e));
                // TODO se debe continuar sin música o lo paro??
            }
        }

        return movie;
    }

    private void saveFinalVideo(Movie result) {
        try {
            long start=System.currentTimeMillis();
            com.example.android.androidmuxer.utils.Utils.createFile(result, pathVideoEdited);
            long spent=System.currentTimeMillis()-start;
            Log.d("WRITING VIDEO FILE", "time spent in millis: " + spent);
            onExportEndedListener.onExportSuccess(new Video(pathVideoEdited));
        } catch (IOException | NullPointerException e) {
            onExportEndedListener.onExportError(String.valueOf(e));
        }
    }
}