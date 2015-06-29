package com.videonasocialmedia.videona.domain.editor.export;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

/**
 * Created by jca on 27/5/15.
 */
public interface OnExportEndedListener {
    void onExportError();

    void onExportSuccess(Video video);
}
