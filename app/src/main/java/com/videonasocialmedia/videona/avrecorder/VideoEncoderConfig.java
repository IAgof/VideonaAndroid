package com.videonasocialmedia.videona.avrecorder;

/**
 * @hide
 */
public class VideoEncoderConfig {
    private final int width;
    private final int height;
    private final int bitRate;
    private final int frameRate;
    private final int iFrameInterval;
    private final String mimeType;


    public VideoEncoderConfig(int width, int height, int bitRate, int iFrameInterval,
                              int frameRate) {
        this.iFrameInterval = iFrameInterval;
        this.frameRate = frameRate;
        this.bitRate = bitRate;
        this.height = height;
        this.width = width;
        this.mimeType = "video/avc";
    }

    public VideoEncoderConfig(int width, int height, int bitRate) {
        this(width, height, bitRate, 5, 30);
    }

    public VideoEncoderConfig() {
        this(1280, 720, 5000000, 5, 30);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBitRate() {
        return bitRate;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public int getIFrameInterval() {
        return iFrameInterval;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String toString() {
        return "VideoEncoderConfig: " + width + "x" + height + " @" + bitRate + " bps";
    }
}