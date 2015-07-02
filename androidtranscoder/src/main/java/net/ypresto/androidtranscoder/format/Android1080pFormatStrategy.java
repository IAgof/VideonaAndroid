/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */
package net.ypresto.androidtranscoder.format;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;

class Android1080pFormatStrategy implements MediaFormatStrategy {
    private static final String TAG = "1080pFormatStrategy";
    private static final int LONGER_LENGTH = 1920;
    private static final int SHORTER_LENGTH = 1080;
    private static final int DEFAULT_BITRATE = 5000 * 1000;
    private static final int DEFAULT_FRAMERATE = 30;
    private static final int DEFAULT_FRAMEINTERVAL = 3;
    private final int mBitRate;
    private final int mFrameRate;
    private final int mFrameInterval;

    public Android1080pFormatStrategy() {
        mBitRate = DEFAULT_BITRATE;
        mFrameRate = DEFAULT_FRAMERATE;
        mFrameInterval = DEFAULT_FRAMEINTERVAL;
    }

    public Android1080pFormatStrategy(int bitRate) {
        mBitRate = bitRate;
        mFrameRate = DEFAULT_FRAMERATE;
        mFrameInterval = DEFAULT_FRAMEINTERVAL;
    }

    public Android1080pFormatStrategy(int bitRate, int frameRate, int frameInterval) {
        mBitRate = bitRate;
        mFrameRate = frameRate;
        mFrameInterval = frameInterval;
    }

    @Override
    public MediaFormat createVideoOutputFormat(MediaFormat inputFormat) {
        int width = inputFormat.getInteger(MediaFormat.KEY_WIDTH);
        int height = inputFormat.getInteger(MediaFormat.KEY_HEIGHT);
        int outWidth, outHeight;
        if (width >= height) {
            outWidth = LONGER_LENGTH;
            outHeight = SHORTER_LENGTH;
        } else {
            outWidth = SHORTER_LENGTH;
            outHeight = LONGER_LENGTH;
        }
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", outWidth, outHeight);
        // From Nexus 4 Camera in 720p
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameRate);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, mFrameInterval);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        return format;
    }

    @Override
    public MediaFormat createAudioOutputFormat(MediaFormat inputFormat) {
        return null;
    }
}
