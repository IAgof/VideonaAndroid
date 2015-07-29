/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.initapp.LoadingProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.videona.presentation.views.activity.AppIntroActivity;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * InitAppPresenter. Presenter to initialize the app.
 * <p/>
 * The view part is only a splashScreen.
 * <p/>
 * Initialize all use cases needed to start the app.
 */
public class InitAppPresenter implements OnInitAppEventListener {

    /**
     * LOG_TAG
     */
    private static final String LOG_TAG = "InitAppPresenter";

    private InitAppView initAppView;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Camera camera;
    private int numSupportedCameras;

    private LoadingProjectUseCase loadingProjectUseCase;

    /**
     * Constructor.
     *
     * @param initAppView
     * @param context
     * @param sharedPreferences
     * @param editor
     */
    public InitAppPresenter(InitAppView initAppView, Context context,
                            SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {

        this.initAppView = initAppView;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.editor = editor;
        initSettings();
        loadingProjectUseCase = new LoadingProjectUseCase(context);
    }

    /**
     * Initializes the camera id parameter in shared preferences to back camera
     */
    private void initSettings() {
        editor.putInt(ConfigPreferences.CAMERA_ID, ConfigPreferences.BACK_CAMERA).commit();
    }

    /**
     * Checks the available settings of the camera (back/front) and checks the principal directories
     * of the app
     */
    public void start() {
        checkCameraSettings();
        checkPathsApp(this);
    }

    /**
     * Checks the available cameras on the device (back/front), supported flash mode and the
     * supported resolutions
     */
    private void checkCameraSettings() {
        checkAvailableCameras();
        checkFlashMode();
        checkCameraVideoSize();
    }

    /**
     * Checks the available cameras on the device (back/front)
     */
    private void checkAvailableCameras() {
        if (camera == null) {
            camera = getCameraInstance(sharedPreferences.getInt(ConfigPreferences.CAMERA_ID,
                    ConfigPreferences.BACK_CAMERA));
        }

        editor.putBoolean(ConfigPreferences.BACK_CAMERA_SUPPORTED, true).commit();
        Log.d(LOG_TAG, "BACK_CAMERA_SUPPORTED");

        numSupportedCameras = Camera.getNumberOfCameras();
        if (numSupportedCameras > 1) {
            editor.putBoolean(ConfigPreferences.FRONT_CAMERA_SUPPORTED, true).commit();
            Log.d(LOG_TAG, "FRONT_CAMERA_SUPPORTED");
        }
        releaseCamera();
    }

    /**
     * Checks if the device supports the flash mode
     */
    private void checkFlashMode() {
        if (camera != null) {
            releaseCamera();
        }
        if (numSupportedCameras > 1) {
            camera = getCameraInstance(ConfigPreferences.FRONT_CAMERA);
            if (camera.getParameters().getSupportedFlashModes() != null) {
                editor.putBoolean(ConfigPreferences.FRONT_CAMERA_FLASH_SUPPORTED, true).commit();
            } else {
                editor.putBoolean(ConfigPreferences.FRONT_CAMERA_FLASH_SUPPORTED, false).commit();
            }
            releaseCamera();
        }
        camera = getCameraInstance(ConfigPreferences.BACK_CAMERA);
        if (camera.getParameters().getSupportedFlashModes() != null) {
            editor.putBoolean(ConfigPreferences.BACK_CAMERA_FLASH_SUPPORTED, true).commit();
        } else {
            editor.putBoolean(ConfigPreferences.BACK_CAMERA_FLASH_SUPPORTED, false).commit();
        }
        releaseCamera();
    }

    /**
     * Checks the supported resolutions by the device
     */
    private void checkCameraVideoSize() {
        List<Camera.Size> supportedVideoSizes;
        if (camera != null) {
            releaseCamera();
        }
        if (numSupportedCameras > 1) {
            camera = getCameraInstance(ConfigPreferences.FRONT_CAMERA);
            supportedVideoSizes = camera.getParameters().getSupportedVideoSizes();
            boolean frontCameraResolutionSupported = false;
            if (supportedVideoSizes != null) {
                for (Camera.Size size : supportedVideoSizes) {
                    if (size.width == 1280 && size.height == 720) {
                        editor.putBoolean(ConfigPreferences.FRONT_CAMERA_720P_SUPPORTED, true).commit();
                        frontCameraResolutionSupported = true;
                        Log.d(LOG_TAG, "FRONT_CAMERA_720P_SUPPORTED");
                    }
                    if (size.width == 1920 && size.height == 1080) {
                        editor.putBoolean(ConfigPreferences.FRONT_CAMERA_1080P_SUPPORTED, true).commit();
                        frontCameraResolutionSupported = true;
                        Log.d(LOG_TAG, "FRONT_CAMERA_1080P_SUPPORTED");
                    }
                    if (size.width == 3840 && size.height == 2160) {
                        editor.putBoolean(ConfigPreferences.FRONT_CAMERA_2160P_SUPPORTED, true).commit();
                        frontCameraResolutionSupported = true;
                        Log.d(LOG_TAG, "FRONT_CAMERA_2160P_SUPPORTED");
                    }
                }
            } else {
                supportedVideoSizes = camera.getParameters().getSupportedPreviewSizes();
                if (supportedVideoSizes != null) {
                    for (Camera.Size size : supportedVideoSizes) {
                        if (size.width == 1280 && size.height == 720) {
                            editor.putBoolean(ConfigPreferences.FRONT_CAMERA_720P_SUPPORTED, true).commit();
                            frontCameraResolutionSupported = true;
                        }
                        if (size.width == 1920 && size.height == 1080) {
                            editor.putBoolean(ConfigPreferences.FRONT_CAMERA_1080P_SUPPORTED, true).commit();
                            frontCameraResolutionSupported = true;
                        }
                        if (size.width == 3840 && size.height == 2160) {
                            editor.putBoolean(ConfigPreferences.FRONT_CAMERA_2160P_SUPPORTED, true).commit();
                            frontCameraResolutionSupported = true;
                        }
                    }
                } else {
                    editor.putBoolean(ConfigPreferences.FRONT_CAMERA_720P_SUPPORTED, false).commit();
                    editor.putBoolean(ConfigPreferences.FRONT_CAMERA_1080P_SUPPORTED, false).commit();
                    editor.putBoolean(ConfigPreferences.FRONT_CAMERA_2160P_SUPPORTED, false).commit();
                }
            }
            if (!frontCameraResolutionSupported) {
                editor.putBoolean(ConfigPreferences.FRONT_CAMERA_SUPPORTED, false).commit();
                Log.d(LOG_TAG, "FRONT_CAMERA_SUPPORTED");
            }
            releaseCamera();
        }
        camera = getCameraInstance(ConfigPreferences.BACK_CAMERA);
        supportedVideoSizes = camera.getParameters().getSupportedVideoSizes();
        if (supportedVideoSizes != null) {
            for (Camera.Size size : camera.getParameters().getSupportedVideoSizes()) {
                if (size.width == 1280 && size.height == 720) {
                    editor.putBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, true).commit();
                }
                if (size.width == 1920 && size.height == 1080) {
                    editor.putBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, true).commit();
                }
                if (size.width == 3840 && size.height == 2160) {
                    editor.putBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, true).commit();
                }
            }
        } else {
            supportedVideoSizes = camera.getParameters().getSupportedPreviewSizes();
            if (supportedVideoSizes != null) {
                for (Camera.Size size : camera.getParameters().getSupportedPreviewSizes()) {
                    if (size.width == 1280 && size.height == 720) {
                        editor.putBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, true).commit();
                    }
                    if (size.width == 1920 && size.height == 1080) {
                        editor.putBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, true).commit();
                    }
                    if (size.width == 3840 && size.height == 2160) {
                        editor.putBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, true).commit();
                    }
                }
            } else {
                editor.putBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, false).commit();
                editor.putBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, false).commit();
                editor.putBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, false).commit();
                editor.putBoolean(ConfigPreferences.BACK_CAMERA_SUPPORTED, false).commit();
            }
        }
        releaseCamera();
    }

    /**
     * Releases the camera object
     */
    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    /**
     * Gets an instance of the camera object
     *
     * @param cameraId
     * @return
     */
    public Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId);
        } catch (Exception e) {
            Log.d("DEBUG", "Camera did not open");
        }
        return c;
    }

    private void startLoadingProject() {
        loadingProjectUseCase.checkProjectState(this);
    }

    /**
     * Check Videona app paths, PATH_APP, pathVideoTrim, pathVideoMusic, ...
     *
     * @throws IOException
     */
    private void checkPath() throws IOException {
        File fEdited = new File(Constants.PATH_APP);
        if (!fEdited.exists()) {
            fEdited.mkdir();
        }
        File fTemp = new File(Constants.PATH_APP_TEMP);
        if (!fTemp.exists()) {
            fTemp.mkdir();
        }
        File fMaster = new File(Constants.PATH_APP_MASTERS);
        if (!fMaster.exists()) {
            fMaster.mkdir();
        }
        File fTempAV = new File(Constants.VIDEO_MUSIC_TEMP_FILE);
        if (fTempAV.exists()) {
            fTempAV.delete();
        }
        File privateDataFolderModel = context.getDir(Constants.FOLDER_VIDEONA_PRIVATE_MODEL, Context.MODE_PRIVATE);
        String privatePath = privateDataFolderModel.getAbsolutePath();
        editor.putString(ConfigPreferences.PRIVATE_PATH, privatePath).commit();

        // TODO: change this variable of 30MB (size of the raw folder)
        if (Utils.isAvailableSpace(30)) {
            downloadingMusicResources();
        }
    }

    /**
     * Downloads music to sdcard.
     * Downloads items during loading screen, first time the user open the app.
     * Export video engine, need  a music resources in file system, not raw folder.
     * <p/>
     * TODO DownloadResourcesUseCase
     */
    private void downloadingMusicResources() {
        List<Music> musicList = getMusicList();
        for (Music music : musicList) {
            try {
                Utils.copyMusicResourceToTemp(context, music.getMusicResourceId());
            } catch (IOException e) {
                Log.d("Init App", "Error copying resources to temp");
            }
        }
    }

    /**
     * Checks the paths of the app
     *
     * @param listener
     */
    private void checkPathsApp(OnInitAppEventListener listener) {
        try {
            checkPath();
            listener.onCheckPathsAppSuccess();
        } catch (IOException e) {
            Log.e("CHECK PATH", "error", e);
        }
    }

    /**
     * TODO obtaing this List from model
     *
     * @return getMusicList
     */
    private List<Music> getMusicList() {
        List<Music> musicList = new ArrayList<>();
        musicList.add(new Music(R.drawable.activity_music_icon_rock_normal, "audio_rock", R.raw.audio_rock, R.color.pastel_palette_pink_2));
        musicList.add(new Music(R.drawable.activity_music_icon_ambiental_normal, "audio_ambiental", R.raw.audio_ambiental, R.color.pastel_palette_red));
        musicList.add(new Music(R.drawable.activity_music_icon_clarinet_normal, "audio_clasica_flauta", R.raw.audio_clasica_flauta, R.color.pastel_palette_blue));
        musicList.add(new Music(R.drawable.activity_music_icon_classic_normal, "audio_clasica_piano", R.raw.audio_clasica_piano, R.color.pastel_palette_brown));
        musicList.add(new Music(R.drawable.activity_music_icon_folk_normal, "audio_folk", R.raw.audio_folk, R.color.pastel_palette_red));
        musicList.add(new Music(R.drawable.activity_music_icon_hip_hop_normal, "audio_hiphop", R.raw.audio_hiphop, R.color.pastel_palette_green));
        musicList.add(new Music(R.drawable.activity_music_icon_pop_normal, "audio_pop", R.raw.audio_pop, R.color.pastel_palette_purple));
        musicList.add(new Music(R.drawable.activity_music_icon_reggae_normal, "audio_reggae", R.raw.audio_reggae, R.color.pastel_palette_orange));
        musicList.add(new Music(R.drawable.activity_music_icon_violin_normal, "audio_clasica_violin", R.raw.audio_clasica_violin, R.color.pastel_palette_yellow));
        musicList.add(new Music(R.drawable.activity_music_icon_remove_normal, "Remove", R.raw.audio_clasica_violin, R.color.pastel_palette_grey));
        return musicList;
    }

    private void startLoadingProject(OnInitAppEventListener listener) {
        //TODO Define project title (by date, by project count, ...)
        //TODO Define path project. By default, path app. Path .temp, private data
        Project.getInstance(Constants.PROJECT_TITLE, sharedPreferences.getString(ConfigPreferences.PRIVATE_PATH, ""), checkProfile());
        listener.onLoadingProjectSuccess();
    }

    //TODO Check user profile, by default 720p free
    private Profile checkProfile() {
        return Profile.getInstance(Profile.ProfileType.free);
    }

    @Override
    public void onCheckPathsAppSuccess() {
        startLoadingProject(this);
    }

    @Override
    public void onCheckPathsAppError() {
    }

    @Override
    public void onLoadingProjectSuccess() {
        //TODO navigate to last activity saved or whatever
        //TODO control time splashScreen
        // Dummy wait two seconds to show splashScreen
        SplashScreenTask splashScreenTask = new SplashScreenTask();
        splashScreenTask.execute();
    }

    @Override
    public void onLoadingProjectError() {
    }

    /**
     * Shows the splash screen
     */
    class SplashScreenTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            //boolean loggedIn = isSessionActive();
            try {
                // 3 seconds, time in milliseconds
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //return loggedIn;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean loggedIn) {
            if(sharedPreferences.getBoolean(ConfigPreferences.FIRST_TIME, true)) {
                initAppView.navigate(AppIntroActivity.class);
            } else {
                initAppView.navigate(RecordActivity.class);
            }
        }
    }
}
