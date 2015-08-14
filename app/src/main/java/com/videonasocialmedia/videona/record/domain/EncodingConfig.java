package com.videonasocialmedia.videona.record.domain;

import com.videonasocialmedia.videona.avrecorder.AudioEncoderConfig;
import com.videonasocialmedia.videona.avrecorder.VideoEncoderConfig;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Configuration information for a Recording session.
 * Including video + audio encoding parameters and output path
 */
public class EncodingConfig {

    private final VideoEncoderConfig mVideoConfig;
    private final AudioEncoderConfig mAudioConfig;

    public EncodingConfig() {
        mVideoConfig = new VideoEncoderConfig(1280, 720, 5 * 1000 * 1000);
        mAudioConfig = new AudioEncoderConfig(1, 48000, 192 * 1000);
    }

    public EncodingConfig(VideoEncoderConfig videoConfig,
                          AudioEncoderConfig audioConfig) {
        mVideoConfig = checkNotNull(videoConfig);
        mAudioConfig = checkNotNull(audioConfig);
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
}
