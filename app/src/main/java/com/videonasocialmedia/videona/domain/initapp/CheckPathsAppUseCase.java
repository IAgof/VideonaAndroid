/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 */

package com.videonasocialmedia.videona.domain.initapp;

import android.content.Context;
import android.util.Log;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnInitAppEventListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.UserPreferences;
import com.videonasocialmedia.videona.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Init application user case
 * <p/>
 * Check Videona paths to record and export video
 * <p/>
 * Check internal storage, needed to persistence in app model.
 */
public class CheckPathsAppUseCase {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * Context of application
     */
    private Context context;

    UserPreferences userPreferences;

    public CheckPathsAppUseCase(Context context) {

        this.context = context;

        userPreferences = new UserPreferences(context);

    }

    public void checkPaths(OnInitAppEventListener listener) {

        try {
            checkPathApp(context);
        } catch (IOException e) {
            Log.e("CHECK PATH", "error", e);
        }

        listener.onCheckPathsAppSuccess();
    }


    /**
     * Check Videona app paths, PATH_APP, pathVideoTrim, pathVideoMusic, ...
     *
     * @throws IOException
     */
    private void checkPathApp(Context context) throws IOException {

        File fEdited = new File(Constants.PATH_APP);

        if (!fEdited.exists()) {

            fEdited.mkdir();

            //  Log.d(LOG_TAG, "Path Videona created");

        }

        File fTemp = new File(Constants.PATH_APP_TEMP);

        if (!fTemp.exists()) {

            fTemp.mkdir();

            //  Log.d(LOG_TAG, "Path " + Constants.PATH_APP_TEMP + " created");
        }

        File fMaster = new File(Constants.PATH_APP_MASTERS);

        if (!fMaster.exists()) {

            fMaster.mkdir();

            // Log.d(LOG_TAG, "Path Videona Masters created");
        }


        File fTempAV = new File(Constants.VIDEO_MUSIC_TEMP_FILE);

        if (fTempAV.exists()) {
            fTempAV.delete();
        }


        // Private data folder model
        File fModel = context.getDir(Constants.FOLDER_VIDEONA_PRIVATE_MODEL, Context.MODE_PRIVATE);

        String privatePath = fModel.getAbsolutePath();

        Log.d(LOG_TAG, "private path " + privatePath);

        userPreferences.setPrivatePath(privatePath);

        // TODO: change this variable of 30MB (size of the raw folder)
        if (Utils.isAvailableSpace(30)) {

            downloadingMusicResources();

            Log.d(LOG_TAG, "downloadingMusicResources" + privatePath);
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


        InputStream in = context.getResources().openRawResource(raw_resource);

        String nameFile = context.getResources().getResourceName(raw_resource);
        nameFile = nameFile.substring(nameFile.lastIndexOf("/") + 1);

        Log.d(LOG_TAG, "copyResourceToTemp " + nameFile);

        File fSong = new File(Constants.PATH_APP_TEMP + File.separator + nameFile + Constants.AUDIO_MUSIC_FILE_EXTENSION);

        if (!fSong.exists()) {


            FileOutputStream out = null;
            try {
                out = new FileOutputStream(Constants.PATH_APP_TEMP + File.separator + nameFile + Constants.AUDIO_MUSIC_FILE_EXTENSION);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }

            // Prevent null pointer exception
            if (out == null) {

            } else {

                byte[] buff = new byte[1024];
                int read = 0;

                try {

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
