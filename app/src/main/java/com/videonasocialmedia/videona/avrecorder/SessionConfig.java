package com.videonasocialmedia.videona.avrecorder;



import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;


/**
 * Configuration information for a Broadcasting or Recording session.
 * Includes meta data, video + audio encoding
 * and muxing parameters
 */
public class SessionConfig {

    private final VideoEncoderConfig mVideoConfig;
    private final AudioEncoderConfig mAudioConfig;
    private File mOutputDirectory;
    private Muxer mMuxer;
    private boolean mConvertVerticalVideo;
    private boolean mAttachLocation;


    public SessionConfig() {
        //mVideoConfig = new VideoEncoderConfig(1280, 720, 2 * 1000 * 1000);
        //mAudioConfig = new AudioEncoderConfig(1, 44100, 96 * 1000);

        // videona config
        mVideoConfig = new VideoEncoderConfig(1280, 720, 5 * 1000 * 1000);
        mAudioConfig = new AudioEncoderConfig(2, 48000, 192 * 1000);


        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "VID_" + timeStamp + ".mp4";

        File rootDir = new File(Constants.PATH_APP_MASTERS);
        File outputFile = new File(rootDir, fileName);
        rootDir.mkdir();

        mMuxer = AndroidMuxer.create(outputFile.getAbsolutePath(), Muxer.FORMAT.MPEG4);

    }

    public SessionConfig(Muxer muxer, VideoEncoderConfig videoConfig,
                         AudioEncoderConfig audioConfig) {

        mVideoConfig = checkNotNull(videoConfig);
        mAudioConfig = checkNotNull(audioConfig);

        mMuxer = checkNotNull(muxer);

    }


    public Muxer getMuxer() {
        return mMuxer;
    }


    public void setOutputDirectory(File outputDirectory) {
        mOutputDirectory = outputDirectory;
    }

    public File getOutputDirectory() {
        return mOutputDirectory;
    }

    public String getOutputPath() {
        return mMuxer.getOutputPath();
    }

    public int getTotalBitrate() {
        return mVideoConfig.getBitRate() + mAudioConfig.getBitrate();
    }

    public int getVideoWidth() {
        return mVideoConfig.getWidth();
    }

    public int getVideoHeight() {
        return mVideoConfig.getHeight();
    }

    public int getVideoBitrate() {
        return mVideoConfig.getBitRate();
    }

    public int getNumAudioChannels() {
        return mAudioConfig.getNumChannels();
    }

    public int getAudioBitrate() {
        return mAudioConfig.getBitrate();
    }

    public int getAudioSamplerate() {
        return mAudioConfig.getSampleRate();
    }

    public boolean isConvertingVerticalVideo() {
        return mConvertVerticalVideo;
    }


    public void setConvertVerticalVideo(boolean convertVerticalVideo) {
        mConvertVerticalVideo = convertVerticalVideo;
    }

    public boolean shouldAttachLocation() {
        return mAttachLocation;
    }

    public void setAttachLocation(boolean mAttachLocation) {
        this.mAttachLocation = mAttachLocation;
    }

    public static class Builder {
        private int mWidth;
        private int mHeight;
        private int mVideoBitrate;

        private int mAudioSamplerate;
        private int mAudioBitrate;
        private int mNumAudioChannels;

        private Muxer mMuxer;
        private File mOutputDirectory;
        private boolean mConvertVerticalVideo;


        /**
         * Configure a SessionConfig quickly with intelligent path interpretation.
         * Valid inputs are "/path/to/name.m3u8", "/path/to/name.mp4"
         * <p/>
         * For file-based outputs (.m3u8, .mp4) the file structure is managed
         * by a recording UUID.
         * <p/>
         * Given an absolute file-based outputLocation like:
         * <p/>
         * /sdcard/test.m3u8
         * <p/>
         * the output will be available in:
         * <p/>
         * /sdcard/<UUID>/test.m3u8
         * /sdcard/<UUID>/test0.ts
         * /sdcard/<UUID>/test1.ts
         * ...
         * <p/>
         * You can query the final outputLocation after building with
         * SessionConfig.getOutputPath()
         *
         * @param outputLocation desired output location. For file based recording,
         *                       recordings will be stored at <outputLocationParent>/<UUID>/<outputLocationFileName>
         */
        public Builder(String outputLocation) {
            setAVDefaults();
            setMetaDefaults();

            if (outputLocation.contains(".mp4")) {
                mMuxer = AndroidMuxer.create(createRecordingPath(outputLocation), Muxer.FORMAT.MPEG4);
                //mMuxer = FFmpegMuxer.create(createRecordingPath(outputLocation), Muxer.FORMAT.MPEG4);
            } else
                throw new RuntimeException("Unexpected muxer output. Expected a .mp4. Got: " + outputLocation);

        }

        /**
         * Use this builder to manage file hierarchy manually
         * or to provide your own Muxer
         *
         * @param muxer
         */
        public Builder(Muxer muxer) {
            setAVDefaults();
            setMetaDefaults();
            mMuxer = checkNotNull(muxer);
            mOutputDirectory = new File(mMuxer.getOutputPath()).getParentFile();

        }

        /**
         * Inserts a directory into the given path based on the
         * value of mUUID.
         *
         * @param outputPath a desired storage location like /path/filename.ext
         * @return a File pointing to /path/UUID/filename.ext
         */
        private String createRecordingPath(String outputPath) {
            File desiredFile = new File(outputPath);
            String desiredFilename = desiredFile.getName();
            //File outputDir = new File(desiredFile.getParent(), mUUID.toString());
            File outputDir = desiredFile.getParentFile();
            mOutputDirectory = outputDir;
            outputDir.mkdirs();
            return new File(outputDir, desiredFilename).getAbsolutePath();
        }

        private void setAVDefaults() {
            mWidth = 1280;
            mHeight = 720;
            mVideoBitrate = 5 * 1000 * 1000;

            mAudioSamplerate = 48000;
            mAudioBitrate = 192 * 1000;
            mNumAudioChannels = 1;
        }

        private void setMetaDefaults() {

            mConvertVerticalVideo = false;
        }

        public Builder withMuxer(Muxer muxer) {
            mMuxer = checkNotNull(muxer);
            return this;
        }

        public Builder withVerticalVideoCorrection(boolean convertVerticalVideo) {
            mConvertVerticalVideo = convertVerticalVideo;
            return this;
        }

        public Builder withVideoResolution(int width, int height) {
            mWidth = width;
            mHeight = height;
            return this;
        }

        public Builder withVideoBitrate(int bitrate) {
            mVideoBitrate = bitrate;
            return this;
        }

        public Builder withAudioSamplerate(int samplerate) {
            mAudioSamplerate = samplerate;
            return this;
        }

        public Builder withAudioBitrate(int bitrate) {
            mAudioBitrate = bitrate;
            return this;
        }

        public Builder withAudioChannels(int numChannels) {
            checkArgument(numChannels == 0 || numChannels == 1 ); //|| numChannels == 2); // added numChannels == 2
            mNumAudioChannels = numChannels;
            return this;
        }


        public SessionConfig build() {
            SessionConfig session = new SessionConfig(mMuxer,
                    new VideoEncoderConfig(mWidth, mHeight, mVideoBitrate),
                    new AudioEncoderConfig(mNumAudioChannels, mAudioSamplerate, mAudioBitrate));

            session.setConvertVerticalVideo(mConvertVerticalVideo);
            session.setOutputDirectory(mOutputDirectory);

            return session;
        }


    }
}
