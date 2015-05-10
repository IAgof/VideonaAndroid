package com.videonasocialmedia.videona.presentation.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.presenters.LoadingProjectPresenter;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.videona.utils.ConfigUtils;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.UserPreferences;
import com.videonasocialmedia.videona.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VideonaMainActivity extends Activity {

    public static native int cut(String inputFile, String outputFile, int startTime, int length);

    private final String LOG_TAG = this.getClass().getSimpleName();

    public static Typeface tf;

    private static final int CAMERA_RECORD_VIDEO_REQUEST_CODE = 100;
    private static final int VIDEO_SHARE_REQUEST_CODE = 500;

    private LoadingProjectPresenter loadingProjectPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");

        UserPreferences appPrefs = new UserPreferences(getApplicationContext());

        Log.d(LOG_TAG, "getIsMusicON " + appPrefs.getIsMusicON());


        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            ConfigUtils.isAndroidL = true;
        }


        appPrefs.setIsColorEffect(false);

        try {
            checkPathApp();
        } catch (IOException e) {
            Log.e("CHECK_PATH", "Error checking path", e);
        }

        loadingProjectPresenter = new LoadingProjectPresenter();

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

    /**
     * Check Videona app paths, PATH_APP, pathVideoTrim, pathVideoMusic, ...
     *
     * @throws IOException
     */
    private void checkPathApp() throws IOException {

        File fEdited = new File(Constants.PATH_APP);

        if (!fEdited.exists()) {

            fEdited.mkdir();

            Log.d(LOG_TAG, "Path Videona created");

            File fTemp = new File(Constants.PATH_APP_TEMP);

            if (!fTemp.exists()) {

                fTemp.mkdir();

                Log.d(LOG_TAG, "Path " + Constants.PATH_APP_TEMP + " created");
            }
        }

        File fMaster = new File(Constants.PATH_APP_MASTERS);

        if (!fMaster.exists()) {

            fMaster.mkdir();

            Log.d(LOG_TAG, "Path Videona Masters created");
        }


        File fTempAV = new File(Constants.VIDEO_MUSIC_TEMP_FILE);

        if (fTempAV.exists()) {
            fTempAV.delete();
        }


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

                // Loading project use case
                loadingProjectPresenter.startLoadingProject();

                // TODO: change this variable of 30MB (size of the raw folder)
                if (Utils.isAvailableSpace(30)) {
                    downloadingMusicResources();
                }

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

            // TODO: change this variable of 30MB (size of the raw folder)
            if (Utils.isAvailableSpace(30)) {
                startActivity(new Intent(getApplicationContext(), RecordActivity.class));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(VideonaMainActivity.this);
                builder.setMessage(R.string.edit_text_insufficient_memory)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                (VideonaMainActivity.this).finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            /*
            //launchCameraActivity();
            if (loggedIn) {

                //  if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                //      startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                //  } else {
                startActivity(new Intent(getApplicationContext(), RecordActivity.class));
                //  }

            } else {
                //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                startActivity(new Intent(getApplicationContext(), RecordActivity.class));
            }
            */
        }
    }

    /**
     * Download music to sdcard.
     * Download items during loading screen, first time the user open the app.
     * Export video engine, need  a music resources in file system, not raw folder.
     * <p/>
     * TODO DownloadResourcesUseCase
     */
    private void downloadingMusicResources() {

        List<Music> musicList = getMusicList();

        for (Music resource : musicList) {
            try {
                downloadMusicResource(resource.getMusicResourceId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Copy resource from raw folder app to sdcard.
     *
     * @param raw_resource
     * @throws IOException
     */

    private void downloadMusicResource(int raw_resource) throws IOException {


        InputStream in = getResources().openRawResource(raw_resource);

        String nameFile = getResources().getResourceName(raw_resource);
        nameFile = nameFile.substring(nameFile.lastIndexOf("/") + 1);

        Log.d(LOG_TAG, "downloadResource " + nameFile);

        File fSong = new File(Constants.PATH_APP_TEMP + File.separator + nameFile + Constants.AUDIO_MUSIC_FILE_EXTENSION);

        if (!fSong.exists()) {


            FileOutputStream out = null;
            try {
                out = new FileOutputStream(Constants.PATH_APP_TEMP + File.separator + nameFile + Constants.AUDIO_MUSIC_FILE_EXTENSION);


                byte[] buff = new byte[1024];
                int read = 0;

                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }

            } catch (IOException e) {
                e.printStackTrace();

            } finally {

                in.close();
                out.close();
            }



        }
    }

    /**
     * TODO obtaing this List from model
     *
     * @return getMusicList
     */
    private List<Music> getMusicList() {

        List<Music> elementList = new ArrayList<>();

        elementList.add(new Music(R.drawable.activity_music_icon_rock_normal, "audio_rock", R.raw.audio_rock, R.color.pastel_palette_pink_2));
        elementList.add(new Music(R.drawable.activity_music_icon_ambiental_normal, "audio_ambiental", R.raw.audio_ambiental, R.color.pastel_palette_red));
        elementList.add(new Music(R.drawable.activity_music_icon_clarinet_normal, "audio_clasica_flauta", R.raw.audio_clasica_flauta, R.color.pastel_palette_blue));
        elementList.add(new Music(R.drawable.activity_music_icon_classic_normal, "audio_clasica_piano", R.raw.audio_clasica_piano, R.color.pastel_palette_brown));
        elementList.add(new Music(R.drawable.activity_music_icon_folk_normal, "audio_folk", R.raw.audio_folk, R.color.pastel_palette_red));
        elementList.add(new Music(R.drawable.activity_music_icon_hip_hop_normal, "audio_hiphop", R.raw.audio_hiphop, R.color.pastel_palette_green));
        elementList.add(new Music(R.drawable.activity_music_icon_pop_normal, "audio_pop", R.raw.audio_pop, R.color.pastel_palette_purple));
        elementList.add(new Music(R.drawable.activity_music_icon_reggae_normal, "audio_reggae", R.raw.audio_reggae, R.color.pastel_palette_orange));
        elementList.add(new Music(R.drawable.activity_music_icon_violin_normal, "audio_clasica_violin", R.raw.audio_clasica_violin, R.color.pastel_palette_yellow));
        elementList.add(new Music(R.drawable.activity_music_icon_remove_normal, "Remove", R.raw.audio_clasica_violin, R.color.pastel_palette_grey));

        return elementList;
    }

}
