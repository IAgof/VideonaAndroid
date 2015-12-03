package com.videonasocialmedia.avrecorder;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import com.videonasocialmedia.avrecorder.overlay.Overlay;
import com.videonasocialmedia.avrecorder.overlay.Watermark;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @hide
 */
class CameraSurfaceRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "CameraSurfaceRenderer";
    private static final boolean VERBOSE = false;
    private final float[] mSTMatrix = new float[16];
    boolean showBox = false;
    private CameraEncoder mCameraEncoder;
    private FullFrameRect mFullScreenCamera;
    private int mOverlayTextureId;
    private int mCameraTextureId;
    private boolean mRecordingEnabled;
    private int mFrameCount;
    // Keep track of selected filters + relevant state
    private boolean mIncomingSizeUpdated;
    private int mIncomingWidth;
    private int mIncomingHeight;
    private int mCurrentFilter;
    private int mNewFilter;

    private int screenWidth;
    private int screenHeight;

    private List<Overlay> overlayList;
    private Watermark watermark;

    /**
     * Constructs CameraSurfaceRenderer.
     * <p/>
     *
     * @param recorder video encoder object
     */
    public CameraSurfaceRenderer(CameraEncoder recorder) {
        mCameraEncoder = recorder;

        mCameraTextureId = -1;
        mFrameCount = -1;

        SessionConfig config = recorder.getConfig();
        mIncomingWidth = config.getVideoWidth();
        mIncomingHeight = config.getVideoHeight();
        mIncomingSizeUpdated = true;        // Force texture size update on next onDrawFrame

        mCurrentFilter = -1;
        mNewFilter = Filters.FILTER_NONE;

        mRecordingEnabled = false;
    }

    public void setOverlayList(List<Overlay> overlayList) {
        this.overlayList = overlayList;
    }

    public void setWatermark(Watermark watermark) {
        this.watermark = watermark;
    }

    public void removeWatermark() {
        watermark = null;
    }

    /**
     * Notifies the renderer that we want to stop or start recording.
     */
    public void changeRecordingState(boolean isRecording) {
        Log.d(TAG, "changeRecordingState: was " + mRecordingEnabled + " now " + isRecording);
        mRecordingEnabled = isRecording;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");
        // Set up the texture blitter that will be used for on-screen display.  This
        // is *not* applied to the recording, because that uses a separate shader.
        mFullScreenCamera = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
        mCameraTextureId = mFullScreenCamera.createTextureObject();
        mCameraEncoder.onSurfaceCreated(mCameraTextureId);
        mFrameCount = 0;
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        Log.d(TAG, "onSurfaceChanged " + width + "x" + height);
        screenWidth = width;
        screenHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        if (VERBOSE) {
            if (mFrameCount % 30 == 0) {
                Log.d(TAG, "onDrawFrame tex=" + mCameraTextureId);
                mCameraEncoder.logSavedEglState();
            }
        }

        if (mCurrentFilter != mNewFilter) {
            Filters.updateFilter(mFullScreenCamera, mNewFilter);
            mCurrentFilter = mNewFilter;
            mIncomingSizeUpdated = true;
        }

        if (mIncomingSizeUpdated) {
            mFullScreenCamera.getProgram().setTexSize(mIncomingWidth, mIncomingHeight);
            mIncomingSizeUpdated = false;
            Log.i(TAG, "setTexSize on display Texture");
        }

        // Draw the video frame.
        if (mCameraEncoder.isSurfaceTextureReadyForDisplay()) {
            mCameraEncoder.getSurfaceTextureForDisplay().updateTexImage();
            mCameraEncoder.getSurfaceTextureForDisplay().getTransformMatrix(mSTMatrix);
            GLES20.glViewport(0, 0, screenWidth, screenHeight);
            mFullScreenCamera.drawFrame(mCameraTextureId, mSTMatrix);
            drawOverlayList();
            if (watermark != null) {
                if (!watermark.isInitialized())
                    watermark.initProgram();
                watermark.draw();
            }

            GLES20.glDisable(GLES20.GL_BLEND);
        }
        mFrameCount++;
    }

    private void drawOverlayList() {
        if (overlayList != null && overlayList.size() > 0) {
            GLES20.glEnable(GLES20.GL_BLEND);
            for (Overlay overlay : overlayList) {
                if (!overlay.isInitialized())
                    overlay.initProgram();
                overlay.draw();
            }
        }
    }

    public void signalVertialVideo(FullFrameRect.SCREEN_ROTATION isVertical) {
        if (mFullScreenCamera != null) mFullScreenCamera.adjustForVerticalVideo(isVertical, false);
    }

    /**
     * Changes the filter that we're applying to the camera preview.
     */
    public void changeFilterMode(int filter) {
        mNewFilter = filter;
    }

    public void handleTouchEvent(MotionEvent ev) {
        mFullScreenCamera.handleTouchEvent(ev);
    }

}