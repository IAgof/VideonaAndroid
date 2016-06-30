/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 * Danny R. Fonseca Arboleda
 */
package com.videonasocialmedia.videona.model.entities.editor.media;

import com.google.gson.annotations.SerializedName;
import com.videonasocialmedia.videona.model.entities.editor.transitions.Transition;
import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;
import com.videonasocialmedia.videona.presentation.views.utils.InfoVideoConstants;

import java.io.File;
import java.util.ArrayList;

/**
 * A media video item that represents a file (or part of a file) that can be used in project video
 * track.
 *
 * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
 */
public class Video extends Media {

    public static String VIDEO_PATH;

    @SerializedName(InfoVideoConstants.VIDEO_LATITUDE)
    private double locationLatitude;
    @SerializedName(InfoVideoConstants.VIDEO_LONGITUDE)
    private double locationLongitude;
    @SerializedName(InfoVideoConstants.VIDEO_HEIGHT)
    private int height;
    @SerializedName(InfoVideoConstants.VIDEO_WIDTH)
    private int width;
    @SerializedName(InfoVideoConstants.VIDEO_ROTATION)
    private int rotation;
    @SerializedName(InfoVideoConstants.VIDEO_DURATION)
    private int fileDuration;
    @SerializedName(InfoVideoConstants.VIDEO_SIZE)
    private long size;
    @SerializedName(InfoVideoConstants.VIDEO_DATE)
    private String date;
    @SerializedName(InfoVideoConstants.VIDEO_BITRATE)
    private int bitRate;
    /**
     * Define if a video has been split before
     */
    private boolean isSplit;


    /**
     * protected default empty constructor, trying to get injectMocks working
     */
    protected Video() {
        super();
    }

    /**
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Video(String identifier, String iconPath, String mediaPath, int fileStartTime,
                 int duration, ArrayList<User> authors, License license) {
        super(identifier, iconPath, mediaPath, fileStartTime, duration, authors, license);

    }

    /**
     * Parametrized constructor. It requires all possible attributes for an effect object.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Video(String identifier, String iconPath, String selectedIconPath, String title,
                 String mediaPath, int fileStartTime, int duration, Transition opening,
                 Transition ending, ArrayList<String> metadata, ArrayList<User> authors,
                 License license) {
        super(identifier, iconPath, selectedIconPath, title, mediaPath, fileStartTime, duration,
                opening, ending, metadata, authors, license);

    }

    /**
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Video(String mediaPath) {
        super(null, null, mediaPath, 0, 0, null, null);
        try {

            fileStartTime = 0;
            fileStopTime = duration;
            isSplit = false;
        } catch (Exception e) {
            fileDuration = 0;
            duration = 0;
            fileStopTime = 0;
            isSplit = false;
        }
    }

    public Video(String mediaPath, int fileStartTime, int duration) {
        super(null, null, mediaPath, fileStartTime, duration, null, null);
    }

    public Video(Video video) {
        super(null, null, video.getMediaPath(), video.getFileStartTime(),
                video.getDuration(), null, null);

        fileStopTime = video.getFileStopTime();
    }



    public int getFileDuration() {
        return fileDuration;
    }

    public void setFileDuration(int fileDuration){
        this.fileDuration = fileDuration;
    }

    public double getLocationLatitude(){
        return locationLatitude;
    }

    public void setLocationLatitude(double latitude){
        this.locationLatitude = latitude;
    }

    public double getLocationLongitude(){
        return locationLongitude;
    }

    public void setLocationLongitude(double longitude){
        this.locationLongitude = longitude;
    }

    public int getHeight(){
        return height;
    }

    public void setHeight(int height){
        this.height = height;
    }

    public int getWidth(){
        return width;
    }

    public void setWidth(int width){
        this.width = width;
    }

    public int getRotation(){
        return rotation;
    }

    public void setRotation(int rotation){
        this.rotation = rotation;
    }

    public long getSize(){
        return size;
    }
    public void setSize(long size){
        this.size = size;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public int getBitRate(){
        return bitRate;
    }

    public void setBitRate(int bitRate){
        this.bitRate = bitRate;
    }

    public boolean getIsSplit() {
        return isSplit;
    }

    public void setIsSplit(boolean isSplit) {
        this.isSplit = isSplit;
    }






}
