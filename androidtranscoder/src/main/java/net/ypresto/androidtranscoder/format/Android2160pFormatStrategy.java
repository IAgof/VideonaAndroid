/*
 * Copyright (C) 2014 Yuya Tanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ypresto.androidtranscoder.format;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;

class Android2160pFormatStrategy implements MediaFormatStrategy {
    private static final String TAG = "4KFormatStrategy";
    private static final int LONGER_LENGTH = 3840;
    private static final int SHORTER_LENGTH = 2160;
    private static final int DEFAULT_BITRATE = 5000 * 1000;
    private static final int DEFAULT_FRAMERATE = 30;
    private static final int DEFAULT_FRAMEINTERVAL = 3;
    private final int mBitRate;
    private final int mFrameRate;
    private final int mFrameInterval;

    public Android2160pFormatStrategy() {
        mBitRate = DEFAULT_BITRATE;
        mFrameRate = DEFAULT_FRAMERATE;
        mFrameInterval = DEFAULT_FRAMEINTERVAL;
    }

    public Android2160pFormatStrategy(int bitRate) {
        mBitRate = bitRate;
        mFrameRate = DEFAULT_FRAMERATE;
        mFrameInterval = DEFAULT_FRAMEINTERVAL;
    }

    public Android2160pFormatStrategy(int bitRate, int frameRate, int frameInterval) {
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
