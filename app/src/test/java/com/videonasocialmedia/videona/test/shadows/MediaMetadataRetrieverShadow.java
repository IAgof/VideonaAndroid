package com.videonasocialmedia.videona.test.shadows;

import android.media.MediaMetadataRetriever;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowMediaMetadataRetriever;

/**
 * Created by jliarte on 14/06/16.
 */
@Implements(MediaMetadataRetriever.class)
public class MediaMetadataRetrieverShadow extends ShadowMediaMetadataRetriever {
    @Override
    public String extractMetadata(int keyCode) {
        return "2";
    }
}
