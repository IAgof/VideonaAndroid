package com.videonasocialmedia.videona.presentation.views.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.InAppNotification;
import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnInitAppEventListener;
import com.videonasocialmedia.videona.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.videona.utils.AnalyticsConstants;
import com.videonasocialmedia.videona.utils.AppStart;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
import com.videonasocialmedia.videona.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * InitAppActivity.
 * <p/>
 * According to clean code and model, use InitAppView, InitAppPresenter for future use.
 * <p/>
 * Main Activity of the app, launch from manifest.
 * <p/>
 * First activity when the user open the app.
 * <p/>
 * Show a dummy splash screen and initialize all data needed to start
 */

public class InitAppActivity extends VideonaActivity implements InitAppView, OnInitAppEventListener {


    /**
     * LOG_TAG
     */
    private final String LOG_TAG = this.getClass().getSimpleName();
    protected Handler handler = new Handler();
    @Bind(R.id.videona_version)
    TextView versionName;
    private long MINIMUN_WAIT_TIME;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Camera camera;
    private int numSupportedCameras;
    private long startTime;
    private String androidId = null;
    private String initState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        //remove title, mode fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_init_app);
        ButterKnife.bind(this);

        setVersionCode();

        SplashScreenTask splashScreenTask = new SplashScreenTask(this);
        splashScreenTask.execute();

        if (BuildConfig.DEBUG) {
            //Wait longer while debug so we can start qordoba sandbox mode on splash screen
            MINIMUN_WAIT_TIME = 10000;
        } else {
            MINIMUN_WAIT_TIME = 900;
        }
    }

    private void setVersionCode() {
        String version = "v "+ BuildConfig.VERSION_NAME;
        versionName.setText(version);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        startTime = System.currentTimeMillis();
        checkAndRequestPermissions();
        sharedPreferences = getSharedPreferences(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    /**
     * Releases the camera object
     */
    private void releaseCamera() {
        if (camera != null) {
            //camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setup() {
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        setupPathsApp(this);
        setupStartApp();
        trackUserProfileGeneralTraits();
    }

    /**
     * Checks the paths of the app
     *
     * @param listener
     */
    private void setupPathsApp(OnInitAppEventListener listener) {
        try {
            initPaths();
            listener.onCheckPathsAppSuccess();
        } catch (IOException e) {
            Log.e("CHECK PATH", "error", e);
        }
    }

    private void setupStartApp() {
        AppStart appStart = new AppStart();
        switch (appStart.checkAppStart(this, sharedPreferences)) {
            case NORMAL:
                Log.d(LOG_TAG, " AppStart State NORMAL");
                initState = AnalyticsConstants.INIT_STATE_RETURNING;
                trackAppStartupProperties(false);
                initSettings();
                break;
            case FIRST_TIME_VERSION:
                Log.d(LOG_TAG, " AppStart State FIRST_TIME_VERSION");
                initState = AnalyticsConstants.INIT_STATE_UPGRADE;
                trackAppStartupProperties(false);
                // Repeat this method for security, if user delete app data miss this configs.
                setupCameraSettings();
                trackUserProfile();
                initSettings();
                joinBetaFortnight();
                break;
            case FIRST_TIME:
                Log.d(LOG_TAG, " AppStart State FIRST_TIME");
                initState = AnalyticsConstants.INIT_STATE_FIRST_TIME;
                trackAppStartupProperties(true);
                setupCameraSettings();
                trackUserProfile();
                trackCreatedSuperProperty();
                initSettings();
                joinBetaFortnight();
                break;
            default:
                break;
        }
    }

    private void trackUserProfileGeneralTraits() {
        mixpanel.getPeople().increment(AnalyticsConstants.APP_USE_COUNT, 1);
        JSONObject userProfileProperties = new JSONObject();
        String userType = AnalyticsConstants.USER_TYPE_FREE;
        if (BuildConfig.FLAVOR.equals("alpha")) {
            userType = AnalyticsConstants.USER_TYPE_BETA;
        }
        try {
            userProfileProperties.put(AnalyticsConstants.TYPE, userType);
            userProfileProperties.put(AnalyticsConstants.LOCALE,
                    Locale.getDefault().toString());
            userProfileProperties.put(AnalyticsConstants.LANG, Locale.getDefault().getISO3Language());
            mixpanel.getPeople().set(userProfileProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check Videona app paths, PATH_APP, pathVideoTrim, pathVideoMusic, ...
     *
     * @throws IOException
     */
    private void initPaths() throws IOException {
        checkAndInitPath(Constants.PATH_APP);
        checkAndInitPath(Constants.PATH_APP_TEMP);
        checkAndInitPath(Constants.PATH_APP_MASTERS);

        File privateDataFolderModel = getDir(Constants.FOLDER_VIDEONA_PRIVATE_MODEL, Context.MODE_PRIVATE);
        String privatePath = privateDataFolderModel.getAbsolutePath();
        editor.putString(ConfigPreferences.PRIVATE_PATH, privatePath).commit();
    }

    private void trackAppStartupProperties(boolean state) {
        JSONObject appStartupSuperProperties = new JSONObject();
        int appUseCount;
        try {
            appUseCount = mixpanel.getSuperProperties().getInt(AnalyticsConstants.APP_USE_COUNT);
        } catch (JSONException e) {
            appUseCount = 0;
        }
        try {
            appStartupSuperProperties.put(AnalyticsConstants.APP_USE_COUNT, ++appUseCount);
            appStartupSuperProperties.put(AnalyticsConstants.FIRST_TIME, state);
            appStartupSuperProperties.put(AnalyticsConstants.APP, "Videona");
            appStartupSuperProperties.put(AnalyticsConstants.FLAVOR, BuildConfig.FLAVOR);
            mixpanel.registerSuperProperties(appStartupSuperProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the camera id parameter in shared preferences to back camera
     */
    private void initSettings() {
        editor.putInt(ConfigPreferences.CAMERA_ID, ConfigPreferences.BACK_CAMERA).commit();
    }

    /**
     * Checks the available cameras on the device (back/front), supported flash mode and the
     * supported resolutions
     */
    private void setupCameraSettings() {
        checkAvailableCameras();
        checkFlashMode();
        checkCameraVideoSize();
    }

    private void trackUserProfile() {
        mixpanel.identify(androidId);
        mixpanel.getPeople().identify(androidId);
        JSONObject userProfileProperties = new JSONObject();
        try {
            userProfileProperties.put(AnalyticsConstants.CREATED,
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
            mixpanel.getPeople().setOnce(userProfileProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Prepare app to launch join beta daialog every 15 days
    private void joinBetaFortnight() {
        editor.putBoolean(ConfigPreferences.EMAIL_BETA_FORTNIGHT, true);
        editor.commit();
    }

    private void trackCreatedSuperProperty() {
        JSONObject createdSuperProperty = new JSONObject();
        try {
            createdSuperProperty.put(AnalyticsConstants.CREATED,
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
            mixpanel.registerSuperPropertiesOnce(createdSuperProperty);
        } catch (JSONException e) {
            Log.e("ANALYTICS", "Error sending created super property");
        }
    }

    private void checkAndInitPath(String pathApp) {
        File fEdited = new File(pathApp);
        if (!fEdited.exists()) {
            fEdited.mkdirs();
        }
    }

    /**
     * Checks the available cameras on the device (back/front)
     */
    private void checkAvailableCameras() {
        if (camera != null) {
            releaseCamera();
        }
        camera = getCameraInstance(sharedPreferences.getInt(ConfigPreferences.CAMERA_ID,
                ConfigPreferences.BACK_CAMERA));
        editor.putBoolean(ConfigPreferences.BACK_CAMERA_SUPPORTED, true).commit();
        numSupportedCameras = Camera.getNumberOfCameras();
        if (numSupportedCameras > 1) {
            editor.putBoolean(ConfigPreferences.FRONT_CAMERA_SUPPORTED, true).commit();
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
            Log.e("DEBUG", "Camera did not open", e);
        }
        return c;
    }

    private void trackAppStartup() {
        JSONObject initAppProperties = new JSONObject();
        try {
            initAppProperties.put(AnalyticsConstants.TYPE, AnalyticsConstants.TYPE_ORGANIC);
            initAppProperties.put(AnalyticsConstants.INIT_STATE, initState);
            mixpanel.track(AnalyticsConstants.APP_STARTED, initAppProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckPathsAppSuccess() {
        startLoadingProject(this);
        moveVideonaToDcim();
    }

    private void startLoadingProject(OnInitAppEventListener listener) {
        //TODO Define project title (by date, by project count, ...)
        //TODO Define path project. By default, path app. Path .temp, private data
        Project.getInstance(Constants.PROJECT_TITLE, sharedPreferences.getString(ConfigPreferences.PRIVATE_PATH, ""), checkProfile());
    }

    private void moveVideonaToDcim() {

        String pathVideonaOld = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES) + File.separator + Constants.FOLDER_VIDEONA;
        String pathVideonaTempOld = pathVideonaOld + File.separator + Constants.FOLDER_VIDEONA_TEMP;

        String pathVideonaMasterOld = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES) + File.separator + Constants.FOLDER_VIDEONA_MASTERS;

        File videonaOldDirectory = new File(pathVideonaOld);
        if (videonaOldDirectory.exists()) {
            for (File f : videonaOldDirectory.listFiles()) {
                if (f.isDirectory()) {
                    for (File fTemp : f.listFiles()) {
                        File newF = new File(Constants.PATH_APP_TEMP, fTemp.getName());
                        if (!newF.exists())
                            fTemp.renameTo(newF);
                    }
                } else {
                    File newF = new File(Constants.PATH_APP, f.getName());
                    if (!newF.exists())
                        f.renameTo(newF);
                }

            }

        }

        File videonaOldMasterDirectory = new File(pathVideonaMasterOld);
        if (videonaOldMasterDirectory.exists()) {
            for (File fMaster : videonaOldMasterDirectory.listFiles()) {
                File newF = new File(Constants.PATH_APP_MASTERS, fMaster.getName());
                if (!newF.exists())
                    fMaster.renameTo(newF);
            }

        }


        File videonaTempOld = new File(pathVideonaTempOld);
        if (videonaTempOld.exists() && videonaTempOld.listFiles().length == 0) {
            videonaTempOld.delete();
        }

        if (videonaOldDirectory.exists() && videonaOldDirectory.listFiles().length == 0) {
            videonaOldDirectory.delete();
        }

        if (videonaOldMasterDirectory.exists() && videonaOldMasterDirectory.listFiles().length == 0) {
            videonaOldMasterDirectory.delete();
        }
    }

    //TODO Check user profile, by default 720p free
    private Profile checkProfile() {
        return Profile.getInstance(Profile.ProfileType.free);
    }

    @Override
    public void onCheckPathsAppError() {

    }

    @Override
    public void onLoadingProjectSuccess() {

    }

    @Override
    public void onLoadingProjectError() {

    }

    @Override
    public void navigate(Class cls) {
        startActivity(new Intent(getApplicationContext(), cls));
    }

    /**
     * Shows the splash screen
     */
    class SplashScreenTask extends AsyncTask<Void, Void, Boolean> {

        private Activity parentActivity;

        public SplashScreenTask(Activity activity) {
            this.parentActivity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                waitForCriticalPermissions();
                setup();
                trackAppStartup();
            } catch (Exception e) {
                Log.e("SETUP", "setup failed", e);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean loggedIn) {
            long currentTimeEnd = System.currentTimeMillis();
            long timePassed = currentTimeEnd - startTime;
            if (timePassed < MINIMUN_WAIT_TIME) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitSplashScreen();
                    }
                }, MINIMUN_WAIT_TIME - timePassed);
            } else {
                exitSplashScreen();
            }
        }

        private void exitSplashScreen() {
            if(sharedPreferences.getBoolean(ConfigPreferences.FIRST_TIME, true)) {
                navigate(IntroAppActivity.class);
            } else {
                InAppNotification notification = mixpanel.getPeople().getNotificationIfAvailable();
                navigate(RecordActivity.class);
                if (notification != null) {
                    Log.d("INAPP", "in-app notification received");
                    mixpanel.getPeople().showGivenNotification(notification, parentActivity);
                    mixpanel.getPeople().trackNotificationSeen(notification);
                }
            }

        }

        private void waitForCriticalPermissions() {
            while (!areCriticalPermissionsGranted()) {
                //just wait
                //TODO reimplement using handlers and semaphores
            }
        }

        private boolean areCriticalPermissionsGranted() {
            boolean granted=ContextCompat.checkSelfPermission(InitAppActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(InitAppActivity.this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(InitAppActivity.this,
                            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            return granted;
        }
    }
}
