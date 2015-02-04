package com.videonasocialmedia.videona.utils;

import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 16/12/14.
 */

public class VideoUtils {


    private static final String LOG_TAG = VideoUtils.class.getSimpleName();

    //public <Track> void switchAudio(String pathVideoFile, String newPathAudioFile, String pathOutputFile) throws IOException {
    public static void switchAudio(String pathVideoFile, String newPathAudioFile, String pathOutputFile) throws IOException {

        Log.d(LOG_TAG, " mergeAudio " + pathVideoFile + " .-.-.- " + newPathAudioFile + " .-.-.- " + pathOutputFile );

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

                String pathFile = speratedDirPath  + File.separator + file;

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
    };
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