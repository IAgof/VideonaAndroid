/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.network.repository.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alvaro on 4/07/16.
 */
public class VideoMetadataRequest {

    /**
     *  Type of video created, recorded, edited
     */
    public enum VIDEO_TYPE { RECORDED, EDITED}

    @SerializedName("lat")
    private double locationLatitude;
    @SerializedName("lon")
    private double locationLongitude;
    @SerializedName("height")
    private int height;
    @SerializedName("width")
    private int width;
    @SerializedName("rotation")
    private int rotation;
    @SerializedName("duration")
    private long fileDuration;
    @SerializedName("size")
    private long size;
    @SerializedName("date")
    private String date;
    @SerializedName("bitrate")
    private int bitRate;
    @SerializedName("title")
    private String title;
    @SerializedName("video_type")
    private VIDEO_TYPE videoType;

    public VideoMetadataRequest(double locationLatitude, double locationLongitude, int height,
                                int width, int rotation, long fileDuration, long size, String date,
                                int bitRate, String title, VIDEO_TYPE videoType) {
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.height = height;
        this.width = width;
        this.rotation = rotation;
        this.fileDuration = fileDuration;
        this.size = size;
        this.date = date;
        this.bitRate = bitRate;
        this.title = title;
        this.videoType = videoType;
    }


    public String toJson() {
        return new Gson().toJson(this);
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getRotation() {
        return rotation;
    }

    public long getFileDuration() {
        return fileDuration;
    }

    public long getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }

    public int getBitRate() {
        return bitRate;
    }

    public String getTitle() {
        return title;
    }

    public VIDEO_TYPE getVideoType(){
        return videoType;
    }


}
