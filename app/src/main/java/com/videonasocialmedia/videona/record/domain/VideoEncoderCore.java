package com.videonasocialmedia.videona.record.domain;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.Surface;

import com.videonasocialmedia.videona.avrecorder.VideoEncoderConfig;

import java.io.IOException;

/**
 * Quizás sea mejor sustituirla por el muxer (parecido a como lo hacen en kickflip.io)
 */
public final class VideoEncoderCore {

    private final MediaCodec encoder;
    private final MediaCodec.BufferInfo bufferInfo;
    private Surface InputSurface;

    public VideoEncoderCore(VideoEncoderConfig encoderConfig) throws IOException {
        bufferInfo= new MediaCodec.BufferInfo();
        MediaFormat format = initMediaFormat(encoderConfig);
        encoder = MediaCodec.createEncoderByType(encoderConfig.getMimeType());
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        InputSurface = encoder.createInputSurface();
    }

    private MediaFormat initMediaFormat(VideoEncoderConfig encoderConfig) {
        MediaFormat format = MediaFormat.createVideoFormat(encoderConfig.getMimeType(),
                encoderConfig.getWidth(), encoderConfig.getHeight());

        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, encoderConfig.getBitRate());
        format.setInteger(MediaFormat.KEY_FRAME_RATE, encoderConfig.getFrameRate());
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, encoderConfig.getIFrameInterval());
        return format;
    }

    public void startEncoder(){
        encoder.start();
    }

    public void drainEncoder(){

    }
}
