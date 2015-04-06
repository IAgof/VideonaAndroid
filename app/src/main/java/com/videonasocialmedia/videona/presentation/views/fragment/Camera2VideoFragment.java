/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.videonasocialmedia.videona.presentation.views.fragment;

//
//
//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//public class Camera2VideoFragment extends Fragment implements RecordView, ColorEffectClickListener, View.OnClickListener {
//
//    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
//
//    /**
//     * String LOG_TAG
//     */
//    private static final String TAG = "Camera2VideoFragment";
//
//    /**
//     * Int request code
//     */
//    private static final int CAMERA_TRIM_VIDEO_REQUEST_CODE = 300;
//
//    /**
//     * Int media types
//     */
//    public static final int MEDIA_TYPE_VIDEO = 2;
//
//    /**
//     * Chronometer, indicate time recording video
//     */
//    @InjectView(R.id.chronometer_record)
//    Chronometer chronometerRecord;
//
//    /**
//     * Uri, file url to store image/video
//     */
//    private Uri fileUri;
//
//    /**
//     * String path to video Recorded
//     */
//    private String videoRecord;
//
//    static {
//
//        ORIENTATIONS.append(Surface.ROTATION_0, 90);
//        ORIENTATIONS.append(Surface.ROTATION_90, 0);
//        ORIENTATIONS.append(Surface.ROTATION_180, 270);
//        ORIENTATIONS.append(Surface.ROTATION_270, 180);
//
//    }
//
//    /**
//     * An {@link com.videonasocialmedia.videona.presentation.views.AutoFitTextureView} for camera preview.
//     */
//    @InjectView(R.id.autofit_texture_view)
//    AutoFitTextureView autoFitTextureView;
//
//    /**
//     * Button to record video
//     */
//    @InjectView(R.id.button_record)
//    ImageButton buttonRecord;
//
//    /**
//     * Button to apply color effects
//     */
//    @InjectView(R.id.button_color_effect)
//    ImageButton buttonColorEffect;
//
//    /**
//     * ListView to use horizontal adapter
//     */
//    @InjectView(R.id.listview_items_color_effect)
//    TwoWayView listViewItemsColorEffect;
//
//    /**
//     * RelativeLayout to show and hide color effects
//     */
//    @InjectView(R.id.relativelayout_color_effect)
//    RelativeLayout relativeLayoutColorEffects;
//
//    /**
//     * Adapter to add images color effect
//     */
//    private ColorEffectAdapter colorEffectAdapter;
//
//    /**
//     * A refernce to the opened {@link android.hardware.camera2.CameraDevice}.
//     */
//    private CameraDevice mCameraDevice;
//
//    /**
//     * A reference to the current {@link android.hardware.camera2.CameraCaptureSession} for preview.
//     */
//    private CameraCaptureSession mPreviewSession;
//
//    /**
//     * {@link android.view.TextureView.SurfaceTextureListener} handles several lifecycle events on a
//     * {@link android.view.TextureView}.
//     */
//    private TextureView.SurfaceTextureListener mSurfaceTextureListener
//            = new TextureView.SurfaceTextureListener() {
//
//        @Override
//        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
//                                              int width, int height) {
//            openCamera(width, height);
//        }
//
//        @Override
//        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
//                                                int width, int height) {
//            configureTransform(width, height);
//        }
//
//        @Override
//        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
//            return true;
//        }
//
//        @Override
//        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
//        }
//
//    };
//
//    /**
//     * The {@link android.util.Size} of camera preview.
//     */
//    private Size mPreviewSize;
//
//    /**
//     * The {@link android.util.Size} of video recording.
//     */
//    private Size mVideoSize;
//
//    /**
//     * Camera preview.
//     */
//    private CaptureRequest.Builder mPreviewBuilder;
//
//    /**
//     * MediaRecorder
//     */
//    private MediaRecorder mMediaRecorder;
//
//    /**
//     * Whether the app is recording video now
//     */
//    private boolean mIsRecordingVideo;
//
//    /**
//     * An additional thread for running tasks that shouldn't block the UI.
//     */
//    private HandlerThread mBackgroundThread;
//
//    /**
//     * A {@link android.os.Handler} for running tasks in the background.
//     */
//    private Handler mBackgroundHandler;
//
//    /**
//     * A {@link java.util.concurrent.Semaphore} to prevent the app from exiting before closing the camera.
//     */
//    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
//
//    /**
//     * {@link android.hardware.camera2.CameraDevice.StateCallback} is called when {@link android.hardware.camera2.CameraDevice} changes its status.
//     */
//    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
//
//        @Override
//        public void onOpened(CameraDevice cameraDevice) {
//            Log.d(TAG, "onOpened");
//
//            mCameraDevice = cameraDevice;
//            startPreview(camera, cameraPreview);
//            mCameraOpenCloseLock.release();
//            if (null != autoFitTextureView) {
//                configureTransform(autoFitTextureView.getWidth(), autoFitTextureView.getHeight());
//            }
//        }
//
//        @Override
//        public void onDisconnected(CameraDevice cameraDevice) {
//            mCameraOpenCloseLock.release();
//            cameraDevice.close();
//            mCameraDevice = null;
//        }
//
//        @Override
//        public void onError(CameraDevice cameraDevice, int error) {
//            mCameraOpenCloseLock.release();
//            cameraDevice.close();
//            mCameraDevice = null;
//            Activity activity = getActivity();
//            if (null != activity) {
//                activity.finish();
//            }
//        }
//
//    };
//
//    public static Camera2VideoFragment newInstance() {
//        Camera2VideoFragment fragment = new Camera2VideoFragment();
//        fragment.setRetainInstance(true);
//        return fragment;
//    }
//
//    /**
//     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes larger
//     * than 1080p, since MediaRecorder cannot handle such a high-resolution video.
//     *
//     * @param choices The list of available sizes
//     * @return The video size
//     */
//    private static Size chooseVideoSize(Size[] choices) {
//        for (Size size : choices) {
//
//            Log.d("Camera2", "chooseVideSize choices " + size.getHeight() + " width " + size.getWidth());
//            if (size.getWidth() == 1280 && size.getHeight() == 720) {
//
//                Log.d("Camera2", "chooseVideSize selection width " + size.getWidth() + " height " + size.getHeight());
//                return size;
//            }
//        }
//        Log.e(TAG, "Couldn't find any suitable video size");
//        return choices[choices.length - 1];
//    }
//
//    /**
//     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
//     * width and height are at least as large as the respective requested values, and whose aspect
//     * ratio matches with the specified value.
//     *
//     * @param choices     The list of sizes that the camera supports for the intended output class
//     * @param width       The minimum desired width
//     * @param height      The minimum desired height
//     * @param aspectRatio The aspect ratio
//     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
//     */
//
//    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
//
//        // Collect the supported resolutions that are at least as big as the preview Surface
//        List<Size> bigEnough = new ArrayList<Size>();
//        int w = aspectRatio.getWidth();
//        int h = aspectRatio.getHeight();
//
//        Log.d("Camera2", "chooseOptimalSize bigEnough height " + h + " / " + height + " width " + w + " / " + width);
//
//        for (Size option : choices) {
//
//            Log.d("Camera2", "chooseOptimalSize choices " + option.getHeight() + " width " + option.getWidth());
//            // if (option.getHeight() >= height && option.getWidth() >= width) {
//            if (option.getHeight() == option.getWidth() * h / w &&
//                    option.getWidth() >= width && option.getHeight() >= height) {
//                bigEnough.add(option);
//                Log.d("Camera2", "chooseOptimalSize choices add selection " + option.getHeight() + " width " + option.getWidth());
//            }
//
//
//      /*   if (option.getHeight() <= Config.VIDEO_SIZE_HEIGHT && option.getWidth() <= Config.VIDEO_SIZE_WIDTH) {
//             Log.d("Camera2", "chooseOptimalSize selection height " + option.getHeight() + " width " + option.getWidth());
//             bigEnough.add(option);
//
//         }
//       */
//
//        }
//
//        // Pick the smallest of those, assuming we found any
//        if (bigEnough.size() > 0) {
//            return Collections.min(bigEnough, new CompareSizesByArea());
//        } else {
//            Log.e(TAG, "Couldn't find any suitable preview size");
//            return choices[0];
//        }
//
//    }
//
//    // https://android.googlesource.com/platform/packages/apps/Camera2/+/36ebcb1/src/com/android/camera/util/CameraUtil.java
//
//    private static Point getDefaultDisplaySize(Activity activity, Point size) {
//        activity.getWindowManager().getDefaultDisplay().getSize(size);
//        return size;
//    }
//
//    public static Size getOptimalPreviewSize(Activity currentActivity,
//                                             List<Size> sizes, double targetRatio) {
//        Point[] points = new Point[sizes.size()];
//        int index = 0;
//        for (Size s : sizes) {
//            points[index++] = new Point(s.getWidth(), s.getHeight());
//        }
//        int optimalPickIndex = getOptimalPreviewSize(currentActivity, points, targetRatio);
//        return (optimalPickIndex == -1) ? null : sizes.get(optimalPickIndex);
//    }
//
//
//    public static int getOptimalPreviewSize(Activity currentActivity,
//                                            Point[] sizes, double targetRatio) {
//        // Use a very small tolerance because we want an exact match.
//        //final double ASPECT_TOLERANCE = 0.01;
//        final double ASPECT_TOLERANCE = 0.05;
//        if (sizes == null) return -1;
//        int optimalSizeIndex = -1;
//        double minDiff = Double.MAX_VALUE;
//        // Because of bugs of overlay and layout, we sometimes will try to
//        // layout the viewfinder in the portrait orientation and thus get the
//        // wrong size of preview surface. When we change the preview size, the
//        // new overlay will be created before the old one closed, which causes
//        // an exception. For now, just get the screen size.
//        Point point = getDefaultDisplaySize(currentActivity, new Point());
//        int targetHeight = Math.min(point.x, point.y);
//        // Try to find an size match aspect ratio and size
//        for (int i = 0; i < sizes.length; i++) {
//            Point size = sizes[i];
//            double ratio = (double) size.x / size.y;
//
//            Log.d("Camera2", " ratio getOptimalPreviewSize " + ratio);
//
//
//            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
//            if (Math.abs(size.y - targetHeight) < minDiff) {
//                optimalSizeIndex = i;
//                minDiff = Math.abs(size.y - targetHeight);
//            }
//        }
//        // Cannot find the one match the aspect ratio. This should not happen.
//        // Ignore the requirement.
//        if (optimalSizeIndex == -1) {
//            Log.w(TAG, "No preview size match the aspect ratio");
//            minDiff = Double.MAX_VALUE;
//            for (int i = 0; i < sizes.length; i++) {
//                Point size = sizes[i];
//                if (Math.abs(size.y - targetHeight) < minDiff) {
//                    optimalSizeIndex = i;
//                    minDiff = Math.abs(size.y - targetHeight);
//                }
//            }
//        }
//        return optimalSizeIndex;
//    }
//
//    // Returns the largest picture size which matches the given aspect ratio.
//    public static Size getOptimalVideoSnapshotPictureSize(
//            List<Size> sizes, double targetRatio) {
//        // Use a very small tolerance because we want an exact match.
//        final double ASPECT_TOLERANCE = 0.001;
//        if (sizes == null) return null;
//        Size optimalSize = null;
//        // Try to find a size matches aspect ratio and has the largest width
//        for (Size size : sizes) {
//            double ratio = (double) size.getWidth() / size.getHeight();
//            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
//            if (optimalSize == null || size.getWidth() > optimalSize.getWidth()) {
//                optimalSize = size;
//            }
//        }
//        // Cannot find one that matches the aspect ratio. This should not happen.
//        // Ignore the requirement.
//        if (optimalSize == null) {
//            Log.w(TAG, "No picture size match the aspect ratio");
//            for (Size size : sizes) {
//                if (optimalSize == null || size.getWidth() > optimalSize.getWidth()) {
//                    optimalSize = size;
//                }
//            }
//        }
//        return optimalSize;
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_camera2_video, container, false);
//    }
//
//    @Override
//    public void onViewCreated(final View view, Bundle savedInstanceState) {
//
//        ButterKnife.inject(this, view);
//
//        buttonRecord.setOnClickListener(this);
//
//        buttonColorEffect.setOnClickListener(this);
//
//        relativeLayoutColorEffects.setVisibility(View.INVISIBLE);
//
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        startBackgroundThread();
//        if (autoFitTextureView.isAvailable()) {
//            openCamera(autoFitTextureView.getWidth(), autoFitTextureView.getHeight());
//        } else {
//            autoFitTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
//        }
//    }
//
//    @Override
//    public void onPause() {
//        closeCamera();
//        stopBackgroundThread();
//        super.onPause();
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.button_record: {
//                if (mIsRecordingVideo) {
//                    stopRecordVideo();
//                } else {
//                    startRecordVideo();
//                }
//                break;
//            }
//
//            case R.id.button_color_effect: {
//
//                Log.d(TAG, "Camera2 buttonColorEffect pressed");
//
//                if (relativeLayoutColorEffects.isShown()) {
//
//
//                    relativeLayoutColorEffects.setVisibility(View.INVISIBLE);
//
//
//                    buttonColorEffect.setImageResource(R.drawable.common_icon_filters_normal);
//
//                    break;
//
//                }
//
//
//                relativeLayoutColorEffects.setVisibility(View.VISIBLE);
//
//                colorEffectAdapter = new ColorEffectAdapter(getActivity(), new ColorEffectList().getColorEffectList());
//
//                colorEffectAdapter.setViewClickListenerLollipop(this);
//
//                listViewItemsColorEffect.setAdapter(colorEffectAdapter);
//
//                buttonColorEffect.setImageResource(R.drawable.common_icon_filters_pressed);
//
//                break;
//            }
//
//
//        }
//    }
//
//    /**
//     * Starts a background thread and its {@link android.os.Handler}.
//     */
//    private void startBackgroundThread() {
//        mBackgroundThread = new HandlerThread("CameraBackground");
//        mBackgroundThread.start();
//        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
//    }
//
//    /**
//     * Stops the background thread and its {@link android.os.Handler}.
//     */
//    private void stopBackgroundThread() {
//        mBackgroundThread.quitSafely();
//        try {
//            mBackgroundThread.join();
//            mBackgroundThread = null;
//            mBackgroundHandler = null;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Tries to open a {@link android.hardware.camera2.CameraDevice}. The result is listened by `mStateCallback`.
//     */
//    private void openCamera(int width, int height) {
//        final Activity activity = getActivity();
//        if (null == activity || activity.isFinishing()) {
//            return;
//        }
//        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
//        try {
//            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
//                throw new RuntimeException("Time out waiting to lock camera opening.");
//            }
//            String cameraId = manager.getCameraIdList()[0];
//
//            // Choose the sizes for camera preview and video recording
//            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
//            StreamConfigurationMap map = characteristics
//                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//
//
//            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
//            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
//                    width, height, mVideoSize);
//
//
//            int orientation = getResources().getConfiguration().orientation;
//
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                autoFitTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//            } else {
//                autoFitTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
//            }
//
//
//            // Landscape
//            autoFitTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//
//            configureTransform(width, height);
//
//            mMediaRecorder = new MediaRecorder();
//            Log.d(TAG, " manager " + manager.getCameraIdList());
//            manager.openCamera(cameraId, mStateCallback, null);
//        } catch (CameraAccessException e) {
//            Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, " Error Cannot access the camera " + e.getMessage());
//            activity.finish();
//        } catch (NullPointerException e) {
//            // Currently an NPE is thrown when the Camera2API is used but not supported on the
//            // device this code runs.
//            new ErrorDialog().show(getFragmentManager(), "dialog");
//        } catch (InterruptedException e) {
//            throw new RuntimeException("Interrupted while trying to lock camera opening.");
//        }
//    }
//
//    private void closeCamera() {
//        try {
//            mCameraOpenCloseLock.acquire();
//            if (null != mCameraDevice) {
//                mCameraDevice.close();
//                mCameraDevice = null;
//            }
//            if (null != mMediaRecorder) {
//                mMediaRecorder.release();
//                mMediaRecorder = null;
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException("Interrupted while trying to lock camera closing.");
//        } finally {
//            mCameraOpenCloseLock.release();
//        }
//    }
//
//    /**
//     * Start the camera preview.
//     * @param camera
//     * @param cameraPreview
//     */
//    private void startPreview(Camera camera, CameraPreview cameraPreview) {
//        if (null == mCameraDevice || !autoFitTextureView.isAvailable() || null == mPreviewSize) {
//            return;
//        }
//        try {
//            setUpMediaRecorder();
//            SurfaceTexture texture = autoFitTextureView.getSurfaceTexture();
//            assert texture != null;
//            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
//            List<Surface> surfaces = new ArrayList<Surface>();
//
//            Surface previewSurface = new Surface(texture);
//            surfaces.add(previewSurface);
//            mPreviewBuilder.addTarget(previewSurface);
//
//
//            // Settings. CameraMetadata to set Color_Effects
//            mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//            //  mPreviewBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_AQUA);
//
//            Surface recorderSurface = mMediaRecorder.getSurface();
//            surfaces.add(recorderSurface);
//            mPreviewBuilder.addTarget(recorderSurface);
//
//            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
//
//                @Override
//                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
//                    mPreviewSession = cameraCaptureSession;
//                    updatePreview();
//                }
//
//                @Override
//                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
//                    Activity activity = getActivity();
//                    if (null != activity) {
//                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }, mBackgroundHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Update the camera preview. {@link RecordView#startPreview(android.hardware.Camera, com.videonasocialmedia.videona.presentation.views.CameraPreview)} needs to be called in advance.
//     */
//    private void updatePreview() {
//        if (null == mCameraDevice) {
//            return;
//        }
//        try {
//            setUpCaptureRequestBuilder(mPreviewBuilder);
//            HandlerThread thread = new HandlerThread("CameraPreview");
//            thread.start();
//            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
//        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//    }
//
//    /**
//     * Configures the necessary {@link android.graphics.Matrix} transformation to `autoFitTextureView`.
//     * This method should not to be called until the camera preview size is determined in
//     * openCamera, or until the size of `autoFitTextureView` is fixed.
//     *
//     * @param viewWidth  The width of `autoFitTextureView`
//     * @param viewHeight The height of `autoFitTextureView`
//     */
//    private void configureTransform(int viewWidth, int viewHeight) {
//
//        Log.d("Camera2", "configureTransform width " + viewWidth + " heigth " + viewHeight);
//
//        Activity activity = getActivity();
//        if (null == autoFitTextureView || null == mPreviewSize || null == activity) {
//            return;
//        }
//        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//
//        Log.d("Camera2", "rotation " + rotation);
//
//        Matrix matrix = new Matrix();
//        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
//        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
//        float centerX = viewRect.centerX();
//        float centerY = viewRect.centerY();
//        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
//            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
//            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
//            float scale = Math.max(
//                    (float) viewHeight / mPreviewSize.getHeight(),
//                    (float) viewWidth / mPreviewSize.getWidth());
//            matrix.postScale(scale, scale, centerX, centerY);
//            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
//        }
//
//        //Log.d("Camera2", "matrix " + matrix.g);
//
//        autoFitTextureView.setTransform(matrix);
//    }
//
//    private void setUpMediaRecorder() throws IOException {
//        final Activity activity = getActivity();
//        if (null == activity) {
//            return;
//        }
//
//        // Change to Videona Video Settings
//
//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//
//        mMediaRecorder.setAudioSamplingRate(ConfigUtils.AUDIO_SAMPLING_RATE);
//        mMediaRecorder.setAudioChannels(ConfigUtils.AUDIO_CHANNELS);
//        mMediaRecorder.setAudioEncodingBitRate(ConfigUtils.AUDIO_ENCODING_BIT_RATE);
//
//        mMediaRecorder.setVideoFrameRate(ConfigUtils.VIDEO_FRAME_RATE);
//        mMediaRecorder.setVideoSize(ConfigUtils.VIDEO_SIZE_WIDTH, ConfigUtils.VIDEO_SIZE_HEIGHT);
//        mMediaRecorder.setVideoEncodingBitRate(ConfigUtils.VIDEO_ENCODING_BIT_RATE);
//        //mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
//
//
//        // Step 4: Set output file
//        videoRecord = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
//        // Check if videoRecordName exists
//        File f = new File(videoRecord);
//        if (f.exists()) {
//            videoRecord = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString() + "1";
//        }
//
//        Log.d(TAG, "videoRecord " + videoRecord);
//
//        mMediaRecorder.setOutputFile(videoRecord);
//
//        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        int orientation = ORIENTATIONS.get(rotation);
//        mMediaRecorder.setOrientationHint(orientation);
//        mMediaRecorder.prepare();
//    }
//
//
//    /**
//     * Create a file Uri for saving an image or video
//     */
//    private static Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(getOutputMediaFile(type));
//    }
//
//    private static File getOutputMediaFile(int type) {
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//
//        // File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//        //       Environment.DIRECTORY_MOVIES), "VideonaApp");
//
//        File mediaStorageDir = new File(Constants.PATH_APP);
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("Camera2VideoFragment", "failed to create directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        if (type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "VID_" + timeStamp + ".mp4");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }
//
//
//    private void setChronometer() {
//
//        chronometerRecord.setBase(SystemClock.elapsedRealtime());
//
//        chronometerRecord.setOnChronometerTickListener(new android.widget.Chronometer.OnChronometerTickListener() {
//            @Override
//            public void onChronometerTick(android.widget.Chronometer chronometer) {
//
//
//                Activity activity = getActivity();
//
//                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
//                int h = (int) (time / 3600000);
//                int m = (int) (time - h * 3600000) / 60000;
//                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
//                // String hh = h < 10 ? "0"+h: h+"";
//                String mm = m < 10 ? "0" + m : m + "";
//                String ss = s < 10 ? "0" + s : s + "";
//                // chronometerRecord.setText(hh+":"+mm+":"+ss);
//                chronometer.setText(mm + ":" + ss);
//
//            }
//        });
//
//    }
//
//    @Override
//    public void onColorEffectClicked(int position) {
//
//        Log.d("Camera2", "onImageClicked " + position);
//
//        colorEffect(position);
//
//    }
//
//    @Override
//    public void startRecordVideo() {
//
//        try {
//            // UI
//            // buttonRecord.setText(R.string.stop);
//
//            buttonRecord.setImageResource(R.drawable.activity_record_icon_stop_normal);
//            buttonRecord.setImageAlpha(125);
//
//
//            mIsRecordingVideo = true;
//
//            // Start recording
//            mMediaRecorder.start();
//
//            startChronometer();
//
//
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void stopRecordVideo() {
//
//        // UI
//        mIsRecordingVideo = false;
//        // buttonRecord.setText(R.string.record);
//        // Stop recording
//        mMediaRecorder.stop();
//        mMediaRecorder.reset();
//
//        stopChronometer();
//
//        Activity activity = getActivity();
//        if (null != activity) {
//            Toast.makeText(activity, "Video saved: " + videoRecord,
//                    Toast.LENGTH_SHORT).show();
//        }
//
//        // startPreview to continue record new videos
//        //startPreview();
//
//        Intent trim = new Intent();
//        trim.putExtra("MEDIA_OUTPUT", videoRecord);
//        trim.setClass(getActivity(), EditActivity.class);
//        startActivityForResult(trim, CAMERA_TRIM_VIDEO_REQUEST_CODE);
//
//    }
//
//    @Override
//    public void startChronometer() {
//
//        setChronometer();
//        chronometerRecord.start();
//
//    }
//
//    @Override
//    public void stopChronometer() {
//
//        chronometerRecord.stop();
//    }
//
//    @Override
//    public void colorEffect(int position) {
//
//        mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//
//        switch (position) {
//            case 0:
//                mPreviewBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_OFF);
//
//                break;
//            case 1:
//                mPreviewBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_MONO);
//
//                break;
//            case 2:
//                mPreviewBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_NEGATIVE);
//
//                break;
//            case 3:
//                mPreviewBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_SOLARIZE);
//
//                break;
//            case 4:
//                mPreviewBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_SEPIA);
//
//                break;
//            case 5:
//                mPreviewBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_POSTERIZE);
//
//                break;
//            case 6:
//                mPreviewBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_WHITEBOARD);
//
//                break;
//            case 7:
//                mPreviewBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_BLACKBOARD);
//
//                break;
//            case 8:
//                mPreviewBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_AQUA);
//
//                break;
//        }
//
//        updatePreview();
//
//    }
//
//    @Override
//    public Context getContext() {
//        return null;
//    }
//
//    /**
//     * Compares two {@code Size}s based on their areas.
//     */
//    static class CompareSizesByArea implements Comparator<Size> {
//
//        @Override
//        public int compare(Size lhs, Size rhs) {
//            //  We cast here to ensure the multiplications won't overflow
//            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
//                    (long) rhs.getWidth() * rhs.getHeight());
//        }
//
//    }
//
//    public static class ErrorDialog extends DialogFragment {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Activity activity = getActivity();
//            return new AlertDialog.Builder(activity)
//                    .setMessage("This device doesn't support Camera2 API.")
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            activity.finish();
//                        }
//                    })
//                    .create();
//        }
//
//    }
//
//
//}
