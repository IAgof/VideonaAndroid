package com.videonasocialmedia.videona.presentation.views.activity;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.eventbus.events.config.PermissionGrantedEvent;
import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnInitAppEventListener;
import com.videonasocialmedia.videona.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.videona.utils.AppStart;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

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

    @InjectView(R.id.videona_version)
    TextView versionName;

    private long MINIMUN_WAIT_TIME;

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = this.getClass().getSimpleName();
    protected Handler handler = new Handler();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Camera camera;
    private int numSupportedCameras;
    private long startTime;
    private String androidId = null;
    private SplashScreenTask splashScreenTask;

    // Storage Permissions
    private int numPermissionsGranted = 0;
    private static final int NUM_PERMISSIONS_REQUESTED = 4;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CONTACTS = 2;
    private static final int REQUEST_NOTIFICATIONS = 3;
    private static final int REQUEST_CAMERA = 4;
    private static final int REQUEST_AUDIO = 5;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String[] PERMISSIONS_CONTACTS = {
            Manifest.permission.GET_ACCOUNTS
    };
    private static String[] PERMISSIONS_NOTIFICATIONS = {
            Manifest.permission.RECEIVE_WAP_PUSH
    };
    private static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };
    private static String[] PERMISSIONS_AUDIO = {
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_init_app);
        ButterKnife.inject(this);
        if (BuildConfig.DEBUG){
            //Wait longer while debug so we can start qordoba sandbox mode on splash screen
            MINIMUN_WAIT_TIME = 10900;
        }else{
            MINIMUN_WAIT_TIME = 900;
        }
        checkVersionCode();
    }

    private void checkVersionCode() {
        String version= this.getString(R.string.versionTitle) + " "
                + BuildConfig.VERSION_NAME + "\n";
        versionName.setText(version);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTime = System.currentTimeMillis();
        sharedPreferences = getSharedPreferences(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        EventBus.getDefault().register(this);
        splashScreenTask = new SplashScreenTask();
        checkPermissionsRequested();
        checkPermissionsGranted();
        mixpanel.timeEvent("Time in Init Activity");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        mixpanel.track("Time in Init Activity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        numPermissionsGranted = 0;
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    public void onEvent(PermissionGrantedEvent event){
        checkPermissionsGranted();
    }


    private void checkPermissionsGranted() {
        if(numPermissionsGranted == NUM_PERMISSIONS_REQUESTED) {
            splashScreenTask.execute();
        }
    }

    private void checkPermissionsRequested() {
        checkStorage();
        checkCamera();
        checkAudio();
        checkContacts();
        checkNotifications();
    }

    private void checkStorage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            numPermissionsGranted++;
        }
    }

    private void checkCamera() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_CAMERA,
                    REQUEST_CAMERA
            );

        } else {
            numPermissionsGranted++;
        }
    }

    private void checkAudio() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_AUDIO,
                    REQUEST_AUDIO
            );
        } else {
            numPermissionsGranted++;
        }
    }

    private void checkContacts() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_CONTACTS,
                    REQUEST_CONTACTS
            );
        } else {
            numPermissionsGranted++;
        }
    }

    private void checkNotifications() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_WAP_PUSH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_NOTIFICATIONS,
                    REQUEST_NOTIFICATIONS
            );
        } else {
            numPermissionsGranted++;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    numPermissionsGranted++;
                    EventBus.getDefault().post(new PermissionGrantedEvent());
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    numPermissionsGranted++;
                    EventBus.getDefault().post(new PermissionGrantedEvent());
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_NOTIFICATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    numPermissionsGranted++;
                    EventBus.getDefault().post(new PermissionGrantedEvent());
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    numPermissionsGranted++;
                    EventBus.getDefault().post(new PermissionGrantedEvent());
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    numPermissionsGranted++;
                    EventBus.getDefault().post(new PermissionGrantedEvent());
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
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

    /**
     * Shows the splash screen
     */
    class SplashScreenTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                setup();
            } catch (Exception e) {
                Log.e("SETUP", "setup failed", e);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean loggedIn) {
            long currentTimeEnd = System.currentTimeMillis();
            long timePassed = currentTimeEnd- startTime;
            if (timePassed < MINIMUN_WAIT_TIME) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitSplashScreen();
                    }
                }, MINIMUN_WAIT_TIME-timePassed);
            } else {
                exitSplashScreen();
            }
        }

        private void exitSplashScreen() {
            navigate(RecordActivity.class);
        }
    }

    private void setup() {
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        setupPathsApp(this);
        setupStartApp();
    }

    private void setupStartApp() {
        AppStart appStart = new AppStart();
        switch (appStart.checkAppStart(this,sharedPreferences)) {
            case NORMAL:
                Log.d(LOG_TAG, " AppStart State NORMAL");
                initSettings();
                break;
            case FIRST_TIME_VERSION:
                Log.d(LOG_TAG, " AppStart State FIRST_TIME_VERSION");
                // example: show what's new
                // could be appear a mix panel popup with improvements.
                initSettings();
                break;
            case FIRST_TIME:
                Log.d(LOG_TAG, " AppStart State FIRST_TIME");
                // example: show a tutorial
                setupCameraSettings();
                createUserProfile();
                initSettings();
                break;
            default:
                break;
        }
    }

    private void createUserProfile() {
        mixpanel.identify(androidId);
        mixpanel.getPeople().identify(androidId);
        mixpanel.getPeople().set("User Type", "Free");
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

    /**
     * Check Videona app paths, PATH_APP, pathVideoTrim, pathVideoMusic, ...
     *
     * @throws IOException
     */
    private void initPaths() throws IOException {


        checkRootPathMovies();
        checkAndInitPath(Constants.PATH_APP);
        checkAndInitPath(Constants.PATH_APP_TEMP);
        checkAndInitPath(Constants.PATH_APP_MASTERS);
        checkAndInitPath(Constants.VIDEO_MUSIC_TEMP_FILE);

        checkAndDeleteOldMusicSongs();

        File privateDataFolderModel = getDir(Constants.FOLDER_VIDEONA_PRIVATE_MODEL, Context.MODE_PRIVATE);
        String privatePath = privateDataFolderModel.getAbsolutePath();
        editor.putString(ConfigPreferences.PRIVATE_PATH, privatePath).commit();
    }

    //TODO Delete this method, only util after release v0.3.23.
    // Clean old music files and temp folder.
    private void checkAndDeleteOldMusicSongs() {

        deleteMusicResources();

    }

    // Don't exist music resource ids in app
    // Delete one by one every song
    // Cannot delete folder temp and after create, app crash coming back from settings :(
    private void deleteMusicResources() {

        int musicId[] = {2131099648, 2131099650, 2131099651, 2131099653, 2131099654, 2131099655 };

        for (int i=0; i<musicId.length; i++) {
                Log.d(LOG_TAG, " deleteMusicResources " + musicId[i]);
                String nameFile = Constants.PATH_APP_TEMP + File.separator + musicId[i] + ".m4a";
                Log.d(LOG_TAG, " deleteMusicResources nameFile " + nameFile);
                File file = new File(nameFile);
                if(file.exists()){
                    file.delete();
                }

        }
    }

    private void checkRootPathMovies() {
        File fMovies = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES);
        if(!fMovies.exists()){
            fMovies.mkdir();
        }
    }

    private void checkAndInitPath(String pathApp) {
        File fEdited = new File(pathApp);
        if (!fEdited.exists()) {
            fEdited.mkdir();
        }
    }


    private void checkAndDeletePath(String pathApp){
        File folderTemp = new File(pathApp);
        if(folderTemp.exists()){
            deleteFolderRecursive(folderTemp);
        }
    }
    private void deleteFolderRecursive(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolderRecursive(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }


    private void startLoadingProject(OnInitAppEventListener listener) {
        //TODO Define project title (by date, by project count, ...)
        //TODO Define path project. By default, path app. Path .temp, private data
        Project.getInstance(Constants.PROJECT_TITLE, sharedPreferences.getString(ConfigPreferences.PRIVATE_PATH, ""), checkProfile());
        //listener.onLoadingProjectSuccess();
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

    }

    @Override
    public void onLoadingProjectError() {

    }

    @Override
    public void navigate(Class cls) {
        startActivity(new Intent(getApplicationContext(), cls));
    }

    /*+++++++++*/
    /* SESSION */
    /*+++++++++*/

    private boolean isSessionActive() {
        SharedPreferences config = getApplicationContext()
                .getSharedPreferences("USER_INFO", MODE_PRIVATE);
        boolean remembered = config.getBoolean("rememberUser", false);
        String sessionCookie = config.getString("sessionCookie", null);
        String rememberMeCookie = config.getString("rememberMeCookie", null);
        if (remembered && sessionCookie != null && rememberMeCookie != null) {
           /*
              Falla getApiHeaders()

            VideonaApplication app = (VideonaApplication) getApplication();
            app.getApiHeaders().setSessionCookieValue(sessionCookie);
            app.getApiHeaders().setRememberMeCookieValue(rememberMeCookie);
            ApiClient apiClient = app.getApiClient();
          */

            //apiClient.getUserName();
            //TODO Check session against server
            return true;
        }
        return false;
    }

}
