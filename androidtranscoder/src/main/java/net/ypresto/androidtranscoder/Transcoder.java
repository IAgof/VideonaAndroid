package net.ypresto.androidtranscoder;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import net.ypresto.androidtranscoder.engine.MediaTranscoderEngine;
import net.ypresto.androidtranscoder.format.MediaFormatStrategy;
import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Veronica Lago Fominaya on 17/06/2015.
 */
//public class AndroidTranscoder implements ThreadCompleteListener {
public class Transcoder {
    private static final String TAG = "FileTranscoder";
    private final Resolution outResolution;
    private static final int BITRATE_2MBPS = 2000 * 1000;
    private static final int BITRATE_5MBPS = 5000 * 1000;
    private static final int BITRATE_8MBPS = 8000 * 1000;
    private static final int BITRATE_10MBPS = 10000 * 1000;
    private static final int FRAMERATE_20FPS = 20;
    private static final int FRAMERATE_25FPS = 25;
    private static final int FRAMERATE_30FPS = 30;
    private static final int FRAMEINTERVAL_3SEC = 3;
    private static final int FRAMEINTERVAL_5SEC = 5;
    private static final int DEFAULT_BITRATE = BITRATE_5MBPS;
    private static final int DEFAULT_FRAMERATE = FRAMERATE_30FPS;
    private static final int DEFAULT_FRAMEINTERVAL = FRAMEINTERVAL_3SEC;
    private final int bitRate;
    private final int frameRate;
    private final int frameInterval;
    private int numFilesToTranscoder = 1;
    private int numFilesTranscoded = 0;

    // TODO: implements the transcoder with threads
    /*
    @Override
    public void notifyOfThreadComplete(Thread thread) {
        Log.d(TAG,"ha acabadoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
    }
    */

    public enum Resolution {
        HD720, HD1080, HD4K
    }

    /**
     * Constructor.
     */
    public Transcoder(Resolution resolution) {
        this.outResolution = resolution;
        this.bitRate = DEFAULT_BITRATE;
        this.frameRate = DEFAULT_FRAMERATE;
        this.frameInterval = DEFAULT_FRAMEINTERVAL;
    }

    /**
     * Constructor.
     */
    public Transcoder(Resolution resolution, int bitRate) {
        this.outResolution = resolution;
        this.bitRate = getBitRate(bitRate);
        this.frameRate = DEFAULT_FRAMERATE;
        this.frameInterval = DEFAULT_FRAMEINTERVAL;
    }

    /**
     * Constructor.
     */
    public Transcoder(Resolution resolution, int bitRate, int frameRate, int frameInterval) {
        this.outResolution = resolution;
        this.bitRate = getBitRate(bitRate);
        this.frameRate = getFrameRate(frameRate);
        this.frameInterval = getFrameInterval(frameInterval);
    }

    private int getBitRate(int bitRate) {
        int outBitRate = bitRate;
        if (bitRate != BITRATE_2MBPS &&
                bitRate != BITRATE_5MBPS &&
                bitRate != BITRATE_8MBPS &&
                bitRate != BITRATE_10MBPS) {
            outBitRate = BITRATE_5MBPS;
        }
        return outBitRate;
    }

    private int getFrameRate(int frameRate) {
        int outFrameRate = frameRate;
        if (frameRate != FRAMERATE_20FPS &&
                frameRate != FRAMERATE_25FPS &&
                frameRate != FRAMERATE_30FPS) {
            outFrameRate = FRAMERATE_30FPS;
        }
        return outFrameRate;
    }

    private int getFrameInterval(int frameInterval) {
        int outFrameInterval = frameInterval;
        if (frameInterval != FRAMEINTERVAL_3SEC && frameInterval != FRAMEINTERVAL_5SEC) {
            outFrameInterval = FRAMEINTERVAL_5SEC;
        }
        return outFrameInterval;
    }

    public void transcodeFile(String filePath, final Listener transcoderListener) throws IOException {
        File file = new File(filePath);
        if(file.isDirectory()) {
            transcodeDirectory(file, transcoderListener);
        } else {
            transcode(file, transcoderListener);
        }
    }

    public void transcodeFile(ArrayList<String> videoList, final Listener transcoderListener) throws IOException {
        numFilesToTranscoder = videoList.size();
        for (String video : videoList) {
            transcode(new File(video), transcoderListener);
        }
    }

    public void transcodeFile(File file, final Listener transcoderListener) throws IOException {
        if(file.isDirectory()) {
            transcodeDirectory(file, transcoderListener);
        } else {
            transcode(file, transcoderListener);
        }
    }

    private void transcodeDirectory(File file, final Listener transcoderListener) throws IOException {
        File[] videoList = file.listFiles();
        numFilesToTranscoder = videoList.length;
        if(numFilesToTranscoder > 0) {
            for (File video : videoList) {
                transcode(video, transcoderListener);
            }
        }
        /*
        if(videoList.length > 0) {
            for (final File video : videoList) {
                NotifyingThread thread1 = new NotifyingThread() {
                    @Override
                    public void doRun() {
                        transcode(video, transcoderListener);
                    }
                };
                thread1.addListener(this); // add ourselves as a listener
                thread1.start();
            }
        }
        */
    }

    private void transcode(final File file, final Listener transcoderListener) throws IOException {

        final File tempDir = new File (Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "Transcoder_temp");
        if (!tempDir.exists())
            tempDir.mkdirs();
        final File tempFile = new File(tempDir, file.getName());
        final long startTime = SystemClock.uptimeMillis();
        MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
            @Override
            public void onTranscodeProgress(double progress) {
                transcoderListener.onTranscodeProgress(progress);
            }

            @Override
            public void onTranscodeCompleted() {
                Log.d(TAG, "transcoding finished");
                Log.d(TAG, "transcoding took " + (SystemClock.uptimeMillis() - startTime) + "ms");
                transcoderListener.onTranscodeCompleted(tempFile.getAbsolutePath());
                numFilesTranscoded++;
                if (numFilesTranscoded == numFilesToTranscoder) {
                    transcoderListener.onTranscodeFinished();
                    numFilesTranscoded = 0;
                }
            }

            @Override
            public void onTranscodeFailed(Exception exception) {
                transcoderListener.onTranscodeFailed(exception);
                numFilesTranscoded++;
                if (numFilesTranscoded == numFilesToTranscoder) {
                    transcoderListener.onTranscodeFinished();
                    numFilesTranscoded = 0;
                }
            }
        };
        Log.d(TAG, "transcoding into " + tempFile);
        MediaTranscoder.getInstance().transcodeVideo(file, tempFile.getAbsolutePath(),
                getFormatStrategy(outResolution), listener);
    }

    private MediaFormatStrategy getFormatStrategy(Resolution resolution) {
        switch (resolution) {
            case HD720:
                return MediaFormatStrategyPresets.createAndroid720pStrategy(bitRate, frameRate, frameInterval);
            case HD1080:
                return MediaFormatStrategyPresets.createAndroid1080pStrategy(bitRate, frameRate, frameInterval);
            case HD4K:
                return MediaFormatStrategyPresets.createAndroid2160pStrategy(bitRate, frameRate, frameInterval);
            default:
                return MediaFormatStrategyPresets.createAndroid720pStrategy(bitRate, frameRate, frameInterval);
        }
    }

    public interface Listener {
        /**
         * Called to notify progress.
         *
         * @param progress Progress in [0.0, 1.0] range, or negative value if progress is unknown.
         */
        void onTranscodeProgress(double progress);

        /**
         * Called when transcode completed.
         *
         * @param path
         */
        void onTranscodeCompleted(String path);

        /**
         * Called when transcode of all videos finished.
         */
        void onTranscodeFinished();

        /**
         * Called when transcode failed.
         *
         * @param exception Exception thrown from {@link MediaTranscoderEngine#transcodeVideo(String, MediaFormatStrategy)}.
         *                  Note that it IS NOT {@link Throwable}. This means {@link Error} won't be caught.
         */
        void onTranscodeFailed(Exception exception);
    }
}
