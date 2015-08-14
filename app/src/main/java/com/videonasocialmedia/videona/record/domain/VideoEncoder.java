package com.videonasocialmedia.videona.record.domain;

import android.os.Handler;
import android.os.Message;

import com.videonasocialmedia.videona.record.events.FrameAvailableEvent;


/**
 * Created by jca on 13/8/15.
 */
public class VideoEncoder {

    EncoderHandler handler;

    public void onEvent(FrameAvailableEvent event){
    }

    class EncoderHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
