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
import com.videonasocialmedia.videona.model.entities.editor.utils.Size;
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
 */
public class ExporterImpl2 implements Exporter2 {
    private Video finalResult;
    private OnExportEndedListener onExportEndedListener;
    private Project project;
    private static final String TAG = "Exporter implementation";
    private Transcoder transcoder;
    private boolean transcodeCorrect = true;
    private ArrayList<String> videoTranscoded;

    public ExporterImpl2(Project project, OnExportEndedListener onExportEndedListener) {
        this.onExportEndedListener = onExportEndedListener;
        this.project = project;
    }

    @Override
    public void export() {
        LinkedList<Media> medias = getMediasFromProject();
        ArrayList<String> videoTrimmedPaths = trimVideos(medias);
        String pathVideoEdited = Constants.PATH_APP_EDITED + File.separator + "V_EDIT_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";

        if(videoTrimmedPaths != null) {
            ArrayList<String> videoTranscodedPaths = transcode(videoTrimmedPaths);
            if(videoTranscodedPaths != null) {
                Movie result = appendFiles(videoTranscodedPaths);
                if(result != null) {
                    try {
                        com.example.android.androidmuxer.utils.Utils.createFile(result, pathVideoEdited);
                        finalResult = new Video(pathVideoEdited);
                        onExportEndedListener.onExportSuccess(finalResult);
                    } catch (IOException e) {
                        onExportEndedListener.onExportError(String.valueOf(e));
                    }
                }
            }
        }
    }

    private LinkedList<Media> getMediasFromProject() {
        LinkedList<Media> medias = project.getMediaTrack().getItems();
        return medias;
    }

    private ArrayList<String> trimVideos(LinkedList<Media> medias) {
        boolean failed = false;
        ArrayList<String> videoTrimmedPaths = new ArrayList<String>();
        Trimmer trimmer;
        Movie movie;
        int i = 1;
        for (Media video : medias) {
            try {
                // TODO change this path
                String outPath =  com.example.android.androidmuxer.utils.Constants.TEMP_TRIM_DIRECTORY + File.separator +
                        "video_trimmed_"+i+".mp4";
                int startTime = video.getFileStartTime();
                int endTime = video.getFileStopTime();
                if((endTime - startTime) != video.getDuration()) {
                    trimmer = new VideoTrimmer();
                    movie = trimmer.trim(video.getMediaPath(), startTime, endTime);
                    com.example.android.androidmuxer.utils.Utils.createFile(movie, outPath);
                    videoTrimmedPaths.add(outPath);
                } else {
                    videoTrimmedPaths.add(video.getMediaPath());
                }
                i++;
            } catch (IOException e) {
                failed = true;
                onExportEndedListener.onExportError(String.valueOf(e));
            }
        }
        if(failed) {
            videoTrimmedPaths = null;
            Log.d(TAG, "ok");
        }
        return videoTrimmedPaths;
    }

    private ArrayList<String> transcode(ArrayList<String> videoPaths) {
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
                Log.d(TAG,"ya ha acabadoooooooooooooooooo");
            }

            @Override
            public void onTranscodeFailed(Exception exception) {
                transcodeCorrect = false;
                videoTranscoded = null;
                onExportEndedListener.onExportError(String.valueOf(exception));
            }
        };
        int index = 0;
        do {
            String video = videoPaths.get(index);
            try {
                transcoder.transcodeFile(video, listener);
            } catch (IOException e) {
                Log.d(TAG, String.valueOf(e));
            }
            index++;
        } while(transcodeCorrect && videoPaths.size() > index);

        return videoTranscoded;
    }

    private Transcoder createTranscoder() {
        Size.Resolution resolution = project.getProfile().getResolution();
        switch (resolution) {
            case HD720:
                return new Transcoder(Transcoder.Resolution.HD720);
            case HD1080:
                return new Transcoder(Transcoder.Resolution.HD1080);
            case HD4K:
                return new Transcoder(Transcoder.Resolution.HD4K);
            default:
                return new Transcoder(Transcoder.Resolution.HD720);
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
        } catch (IOException e) {
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
            } catch (IOException e) {
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
            } catch (IOException e) {
                onExportEndedListener.onExportError(String.valueOf(e));
                // TODO se debe continuar sin música o lo paro??
            }
        }

        return movie;
    }
}
