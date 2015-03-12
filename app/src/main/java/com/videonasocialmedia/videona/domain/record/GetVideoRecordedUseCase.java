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

package com.videonasocialmedia.videona.domain.record;

import com.videonasocialmedia.videona.domain.UseCase;

public interface GetVideoRecordedUseCase extends UseCase {

    /**
     * Start to record File
     */
    public void startRecordFile();

    /**
     * Stop to record File
     */
    public void stopRecordFile();

    /**
     * ColorEffects
     */
    public void setColorEffect(String colorEffect);

    /**
     * Get record file path
     *
     * @return String
     */
    public String getRecordFileString();

    /**
     * Set video duration
     */
    public void setRecordFileDuration(long recordFileDuration);


}
