package com.videonasocialmedia.videona.record.domain;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;

import com.videonasocialmedia.videona.avrecorder.VideoEncoderConfig;
import com.videonasocialmedia.videona.avrecorder.gles.EglCore;
import com.videonasocialmedia.videona.avrecorder.gles.FullFrameRect;
import com.videonasocialmedia.videona.avrecorder.gles.WindowSurface;
import com.videonasocialmedia.videona.record.events.FrameAvailableEvent;


/**
 * Created by jca on 13/8/15.
 */
public class VideoEncoder {

    // ----- accessed exclusively by encoder thread -----
    private WindowSurface inputWindowSurface;
    private EglCore eglCore;
    private FullFrameRect fullScreen;
    private int textureId;
    private int frameNumber;
    private VideoEncoderCore videoEncoderCore;

    boolean recording;

    volatile EncoderHandler encoderHandler;

    public VideoEncoder(){
        encoderHandler=new EncoderHandler();
        recording=false;
    }



    public void onEvent(FrameAvailableEvent event){
        encoderHandler.sendMessage(
                encoderHandler.getMessageName(MSG_FRAME_AVAILABLE, event.surfaceTexture));
    }

    public void handleFrameAvailable(SurfaceTexture surfaceTexture){
        synchronized (readyForFrameFence){
            if (readyForFrames){
                frameNumber++;
                if (recording){
                    encodeFrame(surfaceTexture);
                }
            }
        }
    }

    private void encodeFrame(SurfaceTexture surfaceTexture){
        prepareEncoderForNewFrame();
        render(surfaceTexture);
        sendFrameToEncoderCore(surfaceTexture);
    }

    private void prepareEncoderForNewFrame() {
        inputWindowSurface.makeCurrent();
        videoEncoderCore.drainEncoder(false);
    }

    private void render(SurfaceTexture surfaceTexture){
        //TODO manage change of effects
        //TODO use FBOs to handle several shaders/effects per frame
        surfaceTexture.getTransformMatrix(transformMatrix);
        fullScreen.drawFrame(textureId, transformMatrix);
    }

    private void sendFrameToEncoderCore(SurfaceTexture surfaceTexture){
        inputWindowSurface.setPresentationTime(surfaceTexture.getTimeStamp());
        inputWindowSurface.swapBuffers();
    }


    class EncoderHandler extends Handler{


        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            Object obj = msg.obj;
            switch (what) {
                case MSG_START_RECORDING:
                    encoder.handleStartRecording((VideoEncoderConfig) obj);
                    break;
                case MSG_STOP_RECORDING:
                    encoder.handleStopRecording();
                    break;
                case MSG_FRAME_AVAILABLE:
                    long timestamp = (((long) inputMessage.arg1) << 32) |
                            (((long) inputMessage.arg2) & 0xffffffffL);
                    encoder.handleFrameAvailable((float[]) obj, timestamp);
                    break;
                case MSG_SET_TEXTURE_ID:
                    encoder.handleSetTexture(inputMessage.arg1);
                    break;
                case MSG_UPDATE_SHARED_CONTEXT:
                    encoder.handleUpdateSharedContext((EGLContext) inputMessage.obj);
                    break;
                case MSG_QUIT:
                    Looper.myLooper().quit();
                    break;
                default:
                    throw new RuntimeException("Unhandled msg what=" + what);
            }
        }
    }
}
