/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.utils;

public class TimeUtils {


    /**
     * @param milliseconds
     * @return The time expressed as "hh:mm:ss" or "mm:ss" if there's less than one hour
     */
    public static String toFormattedTime(long milliseconds) {
        long remainingTime = milliseconds;

        long hours = remainingTime / MilliSeconds.ONE_HOUR;
        remainingTime -= hours * MilliSeconds.ONE_HOUR;

        long minutes = remainingTime / MilliSeconds.ONE_MINUTE;
        remainingTime -= minutes * MilliSeconds.ONE_MINUTE;

        long seconds = remainingTime / MilliSeconds.ONE_SECOND;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }

    public static String formatTimeinMinutesSeconds(String time){
        int timeInt= Integer.parseInt(time);
        return toFormattedTime(timeInt);
    }


    class MilliSeconds {
        static final int ONE_SECOND = 1000;
        static final int ONE_MINUTE = 60000;
        static final int ONE_HOUR = 3600000;
    }


}
