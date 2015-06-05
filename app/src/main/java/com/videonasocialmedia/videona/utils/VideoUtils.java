/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.utils;

import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class VideoUtils {


    private static final String LOG_TAG = VideoUtils.class.getSimpleName();


    /**
     * Trim video. Create new file with same settings as origin.
     * Don't rencode video file, only trim.
     *
     * @param inputFile
     * @param starTime
     * @param endTime
     * @param outputFile
     * @throws IOException
     */

    public static void trimVideo(String inputFile, double starTime, double endTime, String outputFile) throws IOException {

        //Movie movie = new MovieCreator().build(new RandomAccessFile("/home/sannies/suckerpunch-distantplanet_h1080p/suckerpunch-distantplanet_h1080p.mov", "r").getChannel());
        //Movie movie = MovieCreator.build("C:\\Users\\sannies\\Downloads\\GOPR1008.MP4");

        Movie movie = MovieCreator.build(inputFile);

        List<Track> tracks = movie.getTracks();
        movie.setTracks(new LinkedList<Track>());
        // remove all tracks we will create new tracks from the old

        /*   double startTime1 = 5;
            double endTime1 = 8;
        */
        double startTime1 = starTime;
        double endTime1 = endTime;

        boolean timeCorrected = false;

        // Here we try to find a track that has sync samples. Since we can only start decoding
        // at such a sample we SHOULD make sure that the start of the new fragment is exactly
        // such a frame
        for (Track track : tracks) {
            if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                if (timeCorrected) {
                    // This exception here could be a false positive in case we have multiple tracks
                    // with sync samples at exactly the same positions. E.g. a single movie containing
                    // multiple qualities of the same video (Microsoft Smooth Streaming file)

                    throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
                }
                startTime1 = correctTimeToSyncSample(track, startTime1, false);
                endTime1 = correctTimeToSyncSample(track, endTime1, true);
                timeCorrected = true;
            }
        }

        for (Track track : tracks) {
            long currentSample = 0;
            double currentTime = 0;
            double lastTime = -1;
            long startSample1 = -1;
            long endSample1 = -1;

            for (int i = 0; i < track.getSampleDurations().length; i++) {
                long delta = track.getSampleDurations()[i];


                if (currentTime > lastTime && currentTime <= startTime1) {
                    // current sample is still before the new starttime
                    startSample1 = currentSample;
                }
                if (currentTime > lastTime && currentTime <= endTime1) {
                    // current sample is after the new start time and still before the new endtime
                    endSample1 = currentSample;
                }
                lastTime = currentTime;
                currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
            movie.addTrack(new AppendTrack(new CroppedTrack(track, startSample1, endSample1)));
        }
        long start1 = System.currentTimeMillis();
        Container out = new DefaultMp4Builder().build(movie);
        long start2 = System.currentTimeMillis();
        FileOutputStream fos = new FileOutputStream(String.format(outputFile, startTime1, endTime1));
        FileChannel fc = fos.getChannel();
        out.writeContainer(fc);

        fc.close();
        fos.close();
        long start3 = System.currentTimeMillis();
        System.err.println("Building IsoFile took : " + (start2 - start1) + "ms");
        System.err.println("Writing IsoFile took  : " + (start3 - start2) + "ms");
        //System.err.println("Writing IsoFile speed : " + (new File(String.format("output-%f-%f--%f-%f.mp4", startTime1, endTime1, startTime2, endTime2)).length() / (start3 - start2) / 1000) + "MB/s");
    }


    private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
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


    /**
     * Switch audio in video file for new audio file.
     * Create new output file with VideoTrack from video file and AudioTrack from audio file.
     *
     * @param pathVideoFile
     * @param newPathAudioFile
     * @param pathOutputFile
     * @throws IOException
     */
    public static void switchAudio(String pathVideoFile, String newPathAudioFile, String pathOutputFile) throws IOException {

        Log.d(LOG_TAG, " mergeAudio " + pathVideoFile + " .-.-.- " + newPathAudioFile + " .-.-.- " + pathOutputFile);

        Movie video = null;
        try {
            video = new MovieCreator().build(pathVideoFile);
        } catch (RuntimeException e) {
            e.printStackTrace();
            //return false;
        } catch (IOException e) {
            e.printStackTrace();
            //return false;
        }

        Movie audio = null;
        try {
            audio = new MovieCreator().build(newPathAudioFile);
        } catch (IOException e) {
            e.printStackTrace();
            //return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            //return false;
        }

        // Funciona, añade el segundo audio a vídeo

        // Track audioTrack = (Track) audio.getTracks().get(0);
        // video.addTrack((com.googlecode.mp4parser.authoring.Track) audioTrack);

        Track videoTrack = (Track) video.getTracks().get(0);
        audio.addTrack((com.googlecode.mp4parser.authoring.Track) videoTrack);

        //  Container out = new DefaultMp4Builder().build(video);

        Container out = new DefaultMp4Builder().build(audio);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(pathOutputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //return false;
        }

        BufferedWritableFileByteChannel byteBufferByteChannel = new BufferedWritableFileByteChannel(fos);

        try {
            out.writeContainer(byteBufferByteChannel);
            byteBufferByteChannel.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            //return false;
        }

    }

    /**
     * MergeFiles
     * Merge video files saved in speratedDirPath
     * Generate new file targetFileName
     * Delete previews video files saved in speratedDirPath
     *
     * @param speratedDirPath
     * @param targetFileName
     * @return boolean
     */
    public static boolean MergeFiles(String speratedDirPath,
                                     String targetFileName) {

        File videoSourceDirFile = new File(speratedDirPath);
        String[] videoList = videoSourceDirFile.list();
        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();
        for (String file : videoList) {
            Log.d(LOG_TAG, "source files" + speratedDirPath
                    + File.separator + file);
            try {

                FileChannel fc;
                fc = new FileInputStream(speratedDirPath
                        + File.separator + file).getChannel();

                Movie movie = null;

                String pathFile = speratedDirPath + File.separator + file;

                try {
                    movie = new MovieCreator().build(pathFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    //return false;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    //return false;
                }

                for (Track t : movie.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        Movie result = new Movie();

        try {

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            Container out = new DefaultMp4Builder().build(result);

            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(targetFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //return false;
            }

            BufferedWritableFileByteChannel byteBufferByteChannel = new BufferedWritableFileByteChannel(fos);

            try {
                out.writeContainer(byteBufferByteChannel);
                byteBufferByteChannel.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                //return false;
            }

            for (String file : videoList) {
                File TBRFile = new File(speratedDirPath + File.separator + file);
                TBRFile.delete();
            }

            boolean a = videoSourceDirFile.delete();

            Log.d(LOG_TAG, "try to delete dir:" + a);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Clear files from speratedDirPath
     *
     * @param speratedDirPath
     * @return boolean
     */
    public static boolean clearFiles(String speratedDirPath) {
        File videoSourceDirFile = new File(speratedDirPath);
        if (videoSourceDirFile != null
                && videoSourceDirFile.listFiles() != null) {
            File[] videoList = videoSourceDirFile.listFiles();
            for (File video : videoList) {
                video.delete();
            }
            videoSourceDirFile.delete();
        }
        return true;
    }

    public static int createSnapshot(String videoFile, int kind, String snapshotFilepath) {
        return 0;
    }

    ;

    public static int createSnapshot(String videoFile, int width, int height, String snapshotFilepath) {
        return 0;
    }
}


class BufferedWritableFileByteChannel implements WritableByteChannel {
    private static final int BUFFER_CAPACITY = 1000000;

    private boolean isOpen = true;
    private final OutputStream outputStream;
    private final ByteBuffer byteBuffer;
    private final byte[] rawBuffer = new byte[BUFFER_CAPACITY];

    BufferedWritableFileByteChannel(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.byteBuffer = ByteBuffer.wrap(rawBuffer);
    }

    public int write(ByteBuffer inputBuffer) throws IOException {
        int inputBytes = inputBuffer.remaining();

        if (inputBytes > byteBuffer.remaining()) {
            dumpToFile();
            byteBuffer.clear();

            if (inputBytes > byteBuffer.remaining()) {
                throw new BufferOverflowException();
            }
        }

        byteBuffer.put(inputBuffer);

        return inputBytes;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void close() throws IOException {
        dumpToFile();
        isOpen = false;
    }

    private void dumpToFile() {
        try {
            outputStream.write(rawBuffer, 0, byteBuffer.position());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}