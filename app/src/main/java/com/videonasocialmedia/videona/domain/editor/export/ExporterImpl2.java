package com.videonasocialmedia.videona.domain.editor.export;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.example.android.androidmuxer.Appender;
import com.example.android.androidmuxer.Trimmer;
import com.example.android.androidmuxer.VideoTrimmer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.Utils;

import net.ypresto.androidtranscoder.Transcoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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


    public ExporterImpl2(Project project, OnExportEndedListener onExportEndedListener) {
        this.onExportEndedListener = onExportEndedListener;
        this.project = project;
    }

    @Override
    public void export() {

        // Trim videos
        LinkedList<Media> medias = getMediasFromProject();
        ArrayList<String> videoTrimmedPaths = trim(medias);

        if(videoTrimmedPaths != null) {
            transcode(videoTrimmedPaths);
            Log.d(TAG, "ok");
        } else {
            Log.d(TAG, "fail");
            onExportEndedListener.onExportError();
        }


        // Transcoder videos

        // Append videos with/without audio

        // Add music or not

        // Write file and save in finalResult

        // Call to onExportEndedListener.onExportSuccess(result) or onExportEndedListener.onExportError()


    }

    private LinkedList<Media> getMediasFromProject() {
        LinkedList<Media> medias = project.getMediaTrack().getItems();
        return medias;
    }

    private ArrayList<String> getVideoPaths(LinkedList<Media> medias) {
        ArrayList<String> videoPaths = new ArrayList<>();

        for (Media m : medias) {
            videoPaths.add(m.getMediaPath());
        }

        return videoPaths;
    }

    private ArrayList<String> trim(LinkedList<Media> medias) {
        boolean success = true;
        ArrayList<String> videoTrimmedPaths = new ArrayList<String>();
        Trimmer trimmer;
        Movie movie;

        int i = 1;
        for (Media video : medias) {
            try {
                // TODO change this path
                String outPath =  com.example.android.androidmuxer.utils.Constants.TEMP_TRIM_DIRECTORY + File.separator +
                        "video_trimmed_"+i+".mp4";
                trimmer = new VideoTrimmer();
                movie = trimmer.trim(video.getMediaPath(), video.getFileStartTime(), video.getFileStopTime());
                com.example.android.androidmuxer.utils.Utils.createFile(movie, outPath);
                videoTrimmedPaths.add(outPath);
                //success = true;
                i++;
            } catch (IOException e) {
                Log.d(TAG, String.valueOf(e));
                success = false;
            }
        }
        if(!success) {
            videoTrimmedPaths = null;
            Log.d(TAG, "ok");
        }
        return videoTrimmedPaths;
    }

    private void transcode() {
        private void transcode(ArrayList<String> videoPaths) {
            String videoPath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES) + File.separator + "original_One_plus_one_3.mp4";
            String directory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES) + File.separator + "prueba";
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setMax(1000);
            final long startTime = SystemClock.uptimeMillis();

            final ArrayList<String> videoTranscoded = new ArrayList<>();
            Transcoder.Listener listener = new Transcoder.Listener() {
                @Override
                public void onTranscodeProgress(double progress) {
                    if (progress < 0) {
                        progressBar.setIndeterminate(true);
                    } else {
                        progressBar.setIndeterminate(false);
                        progressBar.setProgress((int) Math.round(progress * 1000));
                    }
                }

                @Override
                public void onTranscodeCompleted(String path) {
                    Log.d(TAG, "transcoding finished listener");
                    Log.d(TAG, "transcoding took " + (SystemClock.uptimeMillis() - startTime) + "ms");
                    Toast.makeText(TranscoderActivity.this, "transcoded file placed on " + path, Toast.LENGTH_LONG).show();
                    progressBar.setIndeterminate(false);
                    progressBar.setProgress(1000);
                    videoTranscoded.add(path);
                }

                @Override
                public void onTranscodeFinished() {
                    //TODO ver cómo llamarlo cuando el último se codifique
                    Appender appender = new Appender();
                    try {
                        ArrayList<String> audio = new ArrayList<>();
                        String audioPath = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_MOVIES) + File.separator + "audio_pop.m4a";
                        audio.add(audioPath);
                        double movieDuration = 60000;
                        String outPath = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_MOVIES) + File.separator + "resultGordo.mp4";;
                        Movie merge = appender.appendVideos(videoTranscoded, true);
                        //Movie result = appender.addAudio(merge,audio,movieDuration);
                        //Utils.createFile(result, outPath);
                        com.example.android.androidmuxer.utils.Utils.createFile(merge, outPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"ya ha acabadoooooooooooooooooo");
                }

                @Override
                public void onTranscodeFailed(Exception exception) {
                    progressBar.setIndeterminate(false);
                    progressBar.setProgress(0);
                    Toast.makeText(TranscoderActivity.this, "Transcoder error occurred.", Toast.LENGTH_LONG).show();
                }
            };
            try {
                transcoder.transcodeFile(videoPaths, listener);
            } catch (IOException e) {
                Log.d(TAG, String.valueOf(e));
            }
        }
    }


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




    public void export2() {
        boolean success;
        Project project = Project.getInstance(null, null, null);
        String pathVideoEdited = Constants.PATH_APP_EDITED + File.separator + "V_EDIT_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
        String videoWithMusicPath = pathVideoEdited+"_music";
        Video video = (Video) project.getMediaTrack().getItems().getFirst();

        try {
            video = exporter.trimVideo(video, pathVideoEdited);
            success=true;
        } catch (IOException e) {
            Log.e("ERROR", "trimming video", e);
            success= false;
        }
        //TODO refactor this condition
        if (success && isMusicOnProject(project)) {
            try {
                Music music = (Music) project.getAudioTracks().get(0).getItems().getFirst();
                exporter.addMusicToVideo(video, music, videoWithMusicPath);

                File videoFile=new File(pathVideoEdited);
                videoFile.delete();

                File videoWithMusicFile = new File (videoWithMusicPath);
                videoWithMusicFile.renameTo(new File(pathVideoEdited));

                video= new Video(pathVideoEdited);
                success=true;
            } catch (Exception e) {
                Log.e("ERROR", "adding Music", e);
                success=false;
            }
        }

        if (success){
            onExportFinishedListener.onExportSuccess(video);
        }else{
            onExportFinishedListener.onExportError();
        }
    }


    private boolean isMusicOnProject(Project project) {
        return project.getAudioTracks().size() > 0 && project.getAudioTracks().get(0).getItems().size() > 0;
    }



}
