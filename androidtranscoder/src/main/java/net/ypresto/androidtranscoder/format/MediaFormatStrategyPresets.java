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

public class MediaFormatStrategyPresets {
    /**
     * @deprecated Use {@link #createExportPreset960x540Strategy()}.
     */
    @Deprecated
    public static final MediaFormatStrategy EXPORT_PRESET_960x540 = new ExportPreset960x540Strategy();

    /**
     * Preset based on Nexus 4 camera recording with 720p quality.
     * This preset is ensured to work on any Android >=4.3 devices by Android CTS (if codec is available).
     * Default bitrate is 5Mbps. {@link #createAndroid720pStrategy(int)} to specify bitrate.
     */
    public static MediaFormatStrategy createAndroid720pStrategy() {
        return new Android720pFormatStrategy();
    }

    /**
     * Preset based on Nexus 4 camera recording with 720p quality.
     * This preset is ensured to work on any Android >=4.3 devices by Android CTS (if codec is available).
     *
     * @param bitRate Preferred bit rate for encoding.
     */
    public static MediaFormatStrategy createAndroid720pStrategy(int bitRate) {
        return new Android720pFormatStrategy(bitRate);
    }

    /**
     * Preset based on Nexus 4 camera recording with 720p quality.
     * This preset is ensured to work on any Android >=4.3 devices by Android CTS (if codec is available).
     *
     * @param bitRate Preferred bit rate for encoding.
     * @param frameRate Preferred frame rate for encoding.
     * @param frameInterval Preferred frame interval for encoding.
     * @return
     */
    public static MediaFormatStrategy createAndroid720pStrategy(int bitRate,int frameRate, int frameInterval) {
        return new Android720pFormatStrategy(bitRate, frameRate, frameInterval);
    }

    /**
     * Preset based on Nexus 4 camera recording with 720p quality.
     * This preset is ensured to work on any Android >=4.3 devices by Android CTS (if codec is available).
     * Default bitrate is 5Mbps. {@link #createAndroid1080pStrategy(int)} to specify bitrate.
     */
    public static MediaFormatStrategy createAndroid1080pStrategy() {
        return new Android1080pFormatStrategy();
    }

    /**
     * Preset based on Nexus 4 camera recording with 720p quality.
     * This preset is ensured to work on any Android >=4.3 devices by Android CTS (if codec is available).
     *
     * @param bitRate Preferred bit rate for encoding.
     */
    public static MediaFormatStrategy createAndroid1080pStrategy(int bitRate) {
        return new Android1080pFormatStrategy(bitRate);
    }

    /**
     * Preset based on Nexus 4 camera recording with 1080p quality.
     * This preset is ensured to work on any Android >=4.3 devices by Android CTS (if codec is available).
     *
     * @param bitRate Preferred bit rate for encoding.
     * @param frameRate Preferred frame rate for encoding.
     * @param frameInterval Preferred frame interval for encoding.
     * @return
     */
    public static MediaFormatStrategy createAndroid1080pStrategy(int bitRate,int frameRate, int frameInterval) {
        return new Android1080pFormatStrategy(bitRate, frameRate, frameInterval);
    }

    /**
     * Preset based on Nexus 4 camera recording with 4K quality.
     * This preset is ensured to work on any Android >=4.3 devices by Android CTS (if codec is available).
     * Default bitrate is 5Mbps. {@link #createAndroid2160pStrategy(int)} to specify bitrate.
     */
    public static MediaFormatStrategy createAndroid2160pStrategy() {
        return new Android2160pFormatStrategy();
    }

    /**
     * Preset based on Nexus 4 camera recording with 4K quality.
     * This preset is ensured to work on any Android >=4.3 devices by Android CTS (if codec is available).
     *
     * @param bitRate Preferred bit rate for encoding.
     */
    public static MediaFormatStrategy createAndroid2160pStrategy(int bitRate) {
        return new Android2160pFormatStrategy(bitRate);
    }

    /**
     * Preset based on Nexus 4 camera recording with 4K quality.
     * This preset is ensured to work on any Android >=4.3 devices by Android CTS (if codec is available).
     *
     * @param bitRate Preferred bit rate for encoding.
     * @param frameRate Preferred frame rate for encoding.
     * @param frameInterval Preferred frame interval for encoding.
     * @return
     */
    public static MediaFormatStrategy createAndroid2160pStrategy(int bitRate,int frameRate, int frameInterval) {
        return new Android2160pFormatStrategy(bitRate, frameRate, frameInterval);
    }

    /**
     * Preset similar to iOS SDK's AVAssetExportPreset960x540.
     * Note that encoding resolutions of this preset are not supported in all devices e.g. Nexus 4.
     * On unsupported device encoded video stream will be broken without any exception.
     */
    public static MediaFormatStrategy createExportPreset960x540Strategy() {
        return new ExportPreset960x540Strategy();
    }

    private MediaFormatStrategyPresets() {
    }
}
