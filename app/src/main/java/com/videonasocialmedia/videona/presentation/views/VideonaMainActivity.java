package com.videonasocialmedia.videona.presentation.views;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;

import com.videonasocialmedia.videona.presentation.views.login.LoginActivity;
import com.videonasocialmedia.videona.presentation.views.record.RecordActivity;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.UserPreferences;

import java.io.File;
import java.io.IOException;

public class VideonaMainActivity extends Activity {

    static {

        System.loadLibrary("ffmpeg");
        System.loadLibrary("videona-editor");

    }

    public static native int cut(String inputFile, String outputFile, int startTime, int length);

    private final String LOG_TAG = this.getClass().getSimpleName();

    public static Typeface tf;

    private static final int CAMERA_RECORD_VIDEO_REQUEST_CODE = 100;
    private static final int VIDEO_SHARE_REQUEST_CODE = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");

        UserPreferences appPrefs = new UserPreferences(getApplicationContext());

        Log.d(LOG_TAG, "getIsMusicON " + appPrefs.getIsMusicON());

     // appPrefs.setCameraId(0);

        appPrefs.setIsColorEffect(false);


    }

    private void launchCameraActivity() {

        //Intent i = new Intent(getApplicationContext(), RecordActivity.class);
       // startActivity(i);
        //TODO remove next line when remember-me is working and uncomment the rest of the method

        //if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
        //    startActivity(new Intent(getApplicationContext(), CameraActivity.class));
        //} else {
            startActivity(new Intent(getApplicationContext(), RecordActivity.class));
        //}


    }

    @Override
    protected void onStart() {
        super.onStart();

        SplashScreenTask splashScreenTask = new SplashScreenTask();
        splashScreenTask.execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void checkPath() throws IOException {

        File f = new File(Constants.pathApp);
        if (!f.exists()) {
            f.mkdir();

            Log.d(LOG_TAG, "Path Videona created");
        }

        File fTrim = new File(Constants.pathVideoTrim);
        if (!fTrim.exists()) {
            fTrim.mkdir();

            Log.d(LOG_TAG, "Path " + Constants.pathVideoTrim + " created");
        }

        File fMusic = new File(Constants.pathVideoMusic);
        if (!fMusic.exists()) {
            fMusic.mkdir();

            Log.d(LOG_TAG, "Path " + Constants.pathVideoMusic + " created");
        }

        File fTemp = new File(Constants.pathVideoTemp);
        if (!fTemp.exists()) {
            fTemp.mkdir();

            Log.d(LOG_TAG, "Path " + Constants.pathVideoTemp + " created");
        }


        File fTempAV = new File(Constants.videoMusicTempFile);

        if (fTempAV.exists()) {
            fTempAV.delete();
        }

      /*  File f3=new File(pathtemporal);
        if(!f3.exists())
        {
            f3.mkdir();
            if(log) Log.d(TAG,"Path temp created");
        }

       */

        // Copiamos recurso básico from raw to sdcard

    /*    File fAudioTest = new File(Config.pathApp + "/audio_aac.aac");

        if (!fAudioTest.exists()) {

            InputStream in = getResources().openRawResource(R.raw.audio_aac);
            FileOutputStream out = new FileOutputStream(Config.pathApp + "/audio_aac.aac");
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.flush();
                out.close();
            }
        }

      */

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


    class SplashScreenTask extends AsyncTask<Void, Void, Boolean> {

        private final String LOG_TAG = this.getClass().getSimpleName();

        @Override
        protected Boolean doInBackground(Void... voids) {


            // Waiting time to show splash screen
            // Dummy screen

            //boolean loggedIn = isSessionActive();

            try {
                checkPath();
            } catch (IOException e) {
                Log.e("CHECK_PATH", "Error checking path", e);
            }


            try {
                // 3 seconds, time in milliseconds
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //return loggedIn;
            return true;

        }

        @Override
        protected void onPostExecute(Boolean loggedIn) {

            //launchCameraActivity();
            if (loggedIn) {

              //  if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
              //      startActivity(new Intent(getApplicationContext(), CameraActivity.class));
              //  } else {
//                    startActivity(new Intent(getApplicationContext(), RecordActivity.class));
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
              //  }

            } else {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                //startActivity(new Intent(getApplicationContext(), RecordActivity.class));
            }
        }
    }

}
