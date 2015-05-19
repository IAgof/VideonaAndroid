/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.UserPreferences;
import com.videonasocialmedia.videona.VideonaApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @deprecated
 */
public class MusicActivity extends Activity {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private UserPreferences appPrefs;
    private Typeface tf;

    private MediaPlayer mediaPlayerMusic;

    private ImageView image0;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private ImageView image5;
    private ImageView image6;
    private ImageView image7;
    private ImageView image8;

    private int mThumbMusicImageAux;
    private int mThumbMusicTextAux;
    private int mThumbMusicAudioAux;
    private int mThumbMusicImageBackgroundAux;

    private ImageButton btnMusicSelected;

    private TextView textMusicSelected;


    // Position image selected
    private int position = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // Keep screen ON
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        appPrefs = new UserPreferences(getApplicationContext());

        tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");

        image0 = (ImageView) findViewById(R.id.imageMusic0);
        image0.setOnClickListener(image0Listener());

        image1 = (ImageView) findViewById(R.id.imageMusic1);
        image1.setOnClickListener(image1Listener());

        image2 = (ImageView) findViewById(R.id.imageMusic2);
        image2.setOnClickListener(image2Listener());

        image3 = (ImageView) findViewById(R.id.imageMusic3);
        image3.setOnClickListener(image3Listener());

        image4 = (ImageView) findViewById(R.id.imageMusic4);
        image4.setOnClickListener(image4Listener());

        image5 = (ImageView) findViewById(R.id.imageMusic5);
        image5.setOnClickListener(image5Listener());

        image6 = (ImageView) findViewById(R.id.imageMusic6);
        image6.setOnClickListener(image6Listener());

        image7 = (ImageView) findViewById(R.id.imageMusic7);
        image7.setOnClickListener(image7Listener());

        image8 = (ImageView) findViewById(R.id.imageMusic8);
        image8.setOnClickListener(image8Listener());


        btnMusicSelected = (ImageButton) findViewById(R.id.imageBtnOkMusic);
        btnMusicSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayerMusic != null) {
                    mediaPlayerMusic.stop();
                    mediaPlayerMusic.release();
                    mediaPlayerMusic = null;
                }

                Intent select = new Intent();
                select.putExtra("position", position);
                select.putExtra("music_image", mThumbMusicImage[0]);
                select.putExtra("music_text", mThumbMusicText[0]);
                select.putExtra("music_audio", mThumbMusicAudio[0]);
                select.putExtra("music_color", mThumbMusicImageBackground[0]);


                setResult(Activity.RESULT_OK, select);

                trackMusicUsed(getResources().getString(mThumbMusicText[0]));

                finish();
            }
        });

        textMusicSelected = (TextView) findViewById(R.id.textViewMusicSelected);
        textMusicSelected.setTypeface(tf);

        mThumbMusicImageAux = mThumbMusicImage[0];
        mThumbMusicTextAux = mThumbMusicText[0];
        mThumbMusicAudioAux = mThumbMusicAudio[0];
        mThumbMusicImageBackgroundAux = mThumbMusicImageBackground[0];


    }


    private View.OnClickListener image0Listener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                position = 0;
                updateDataMusic(position);

                try {
                    startMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        };
    }

    private View.OnClickListener image1Listener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (position == 1) {

                } else {

                    image0.setImageDrawable(getResources().getDrawable(mThumbMusicImage[1]));
                    image0.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[1]));

                    image1.setImageDrawable(getResources().getDrawable(mThumbMusicImage[position]));
                    image1.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[position]));

                    position = 1;

                    updateDataMusic(position);

                }

                try {
                    startMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        };
    }

    private View.OnClickListener image2Listener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (position == 2) {

                } else {

                    image0.setImageDrawable(getResources().getDrawable(mThumbMusicImage[2]));
                    image0.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[2]));

                    image2.setImageDrawable(getResources().getDrawable(mThumbMusicImage[position]));
                    image2.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[position]));

                    position = 2;

                    updateDataMusic(position);
                }

                try {
                    startMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        };
    }

    private View.OnClickListener image3Listener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (position == 3) {

                } else {

                    image0.setImageDrawable(getResources().getDrawable(mThumbMusicImage[3]));
                    image0.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[3]));

                    image3.setImageDrawable(getResources().getDrawable(mThumbMusicImage[position]));
                    image3.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[position]));

                    position = 3;

                    updateDataMusic(position);
                }

                try {
                    startMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
    }

    private View.OnClickListener image4Listener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (position == 4) {

                } else {

                    image0.setImageDrawable(getResources().getDrawable(mThumbMusicImage[4]));
                    image0.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[4]));

                    image4.setImageDrawable(getResources().getDrawable(mThumbMusicImage[position]));
                    image4.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[position]));

                    position = 4;

                    updateDataMusic(position);
                }

                try {
                    startMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private View.OnClickListener image5Listener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (position == 5) {

                } else {

                    image0.setImageDrawable(getResources().getDrawable(mThumbMusicImage[5]));
                    image0.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[5]));

                    image5.setImageDrawable(getResources().getDrawable(mThumbMusicImage[position]));
                    image5.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[position]));

                    position = 5;
                    updateDataMusic(position);
                }

                try {
                    startMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
    }

    private View.OnClickListener image6Listener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (position == 6) {

                } else {

                    image0.setImageDrawable(getResources().getDrawable(mThumbMusicImage[6]));
                    image0.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[6]));

                    image6.setImageDrawable(getResources().getDrawable(mThumbMusicImage[position]));
                    image6.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[position]));

                    position = 6;

                    updateDataMusic(position);
                }

                try {
                    startMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        };
    }

    private View.OnClickListener image7Listener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (position == 7) {

                } else {

                    image0.setImageDrawable(getResources().getDrawable(mThumbMusicImage[7]));
                    image0.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[7]));

                    image7.setImageDrawable(getResources().getDrawable(mThumbMusicImage[position]));
                    image7.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[position]));

                    position = 7;

                    updateDataMusic(position);

                }

                try {
                    startMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        };
    }

    private View.OnClickListener image8Listener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (position == 8) {

                } else {

                    image0.setImageDrawable(getResources().getDrawable(mThumbMusicImage[8]));
                    image0.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[8]));

                    image8.setImageDrawable(getResources().getDrawable(mThumbMusicImage[position]));
                    image8.setBackgroundColor(getResources().getColor(mThumbMusicImageBackground[position]));

                    position = 8;

                    updateDataMusic(position);
                }

                try {
                    startMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        };
    }

    private void startMusic() throws IOException {

        if (mediaPlayerMusic != null) {

            mediaPlayerMusic.stop();
            mediaPlayerMusic.release();
            mediaPlayerMusic = null;
        }

        mediaPlayerMusic = MediaPlayer.create(getBaseContext(), mThumbMusicAudio[0]);
        mediaPlayerMusic.start();
        mediaPlayerMusic.setVolume(5.0f, 5.0f);

        textMusicSelected.setText(getString(mThumbMusicText[0]));

        downloadResource(mThumbMusicAudio[0]);


        position = 0;

    }


    // references to our orginial images and text music order
    private Integer[] mThumbMusicImage = {
            R.drawable.activity_music_icon_folk_normal, R.drawable.activity_music_icon_hip_hop_normal,
            R.drawable.activity_music_icon_pop_normal, R.drawable.activity_music_icon_reggae_normal,
            R.drawable.activity_music_icon_rock_normal, R.drawable.activity_music_icon_classic_normal,
            R.drawable.activity_music_icon_violin_normal, R.drawable.activity_music_icon_clarinet_normal,
            R.drawable.activity_music_icon_ambiental_normal
    };

    private Integer[] mThumbMusicImageBackground = {
            R.color.pastel_palette_red, R.color.pastel_palette_green,
            R.color.pastel_palette_purple, R.color.pastel_palette_orange,
            R.color.pastel_palette_pink_2, R.color.pastel_palette_brown,
            R.color.pastel_palette_yellow, R.color.pastel_palette_blue,
            R.color.pastel_palette_grey
    };

    private Integer[] mThumbMusicText = {
            R.string.music_genre_folk, R.string.music_genre_hiphop,
            R.string.music_genre_pop, R.string.music_genre_reggae,
            R.string.music_genre_rock, R.string.music_genre_classic,
            R.string.music_genre_classic_violin, R.string.music_genre_classic_clarinet,
            R.string.music_genre_ambiental
    };

    private Integer[] mThumbMusicAudio = {
            R.raw.audio_folk, R.raw.audio_hiphop,
            R.raw.audio_pop, R.raw.audio_reggae,
            R.raw.audio_rock, R.raw.audio_clasica_piano,
            R.raw.audio_clasica_violin, R.raw.audio_clasica_flauta,
            R.raw.audio_ambiental
    };

    private void updateDataMusic(int position) {

        mThumbMusicImageAux = mThumbMusicImage[0];
        mThumbMusicImage[0] = mThumbMusicImage[position];
        mThumbMusicImage[position] = mThumbMusicImageAux;

        mThumbMusicAudioAux = mThumbMusicAudio[0];
        mThumbMusicAudio[0] = mThumbMusicAudio[position];
        mThumbMusicAudio[position] = mThumbMusicAudioAux;

        mThumbMusicTextAux = mThumbMusicText[0];
        mThumbMusicText[0] = mThumbMusicText[position];
        mThumbMusicText[position] = mThumbMusicTextAux;

        mThumbMusicImageBackgroundAux = mThumbMusicImageBackground[0];
        mThumbMusicImageBackground[0] = mThumbMusicImageBackground[position];
        mThumbMusicImageBackground[position] = mThumbMusicImageBackgroundAux;

        Log.d(LOG_TAG, " updateDataMusic position: " + position + " mThumbMusicImage[0] " + mThumbMusicImage[0].toString() + " mThumbMusicImage[position] " + mThumbMusicImage[position].toString());

    }

    public void downloadResource(int raw_resource) throws IOException {


        InputStream in = getResources().openRawResource(raw_resource);

        String nameFile = getResources().getResourceName(raw_resource);
        nameFile = nameFile.substring(nameFile.lastIndexOf("/") + 1);

        Log.d(LOG_TAG, "downloadResource " + nameFile);

        File fSong = new File(Constants.PATH_APP_TEMP + File.separator + nameFile + Constants.AUDIO_MUSIC_FILE_EXTENSION);

        if (fSong.exists()) {


        } else {

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(Constants.PATH_APP_TEMP + File.separator + nameFile + Constants.AUDIO_MUSIC_FILE_EXTENSION);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }
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
                out.flush();
                out.close();
            }


        }
    }

    @Override
    public void onBackPressed() {

        if (mediaPlayerMusic != null) {

            mediaPlayerMusic.stop();
            mediaPlayerMusic.release();
            mediaPlayerMusic = null;
        }

        setResult(Activity.RESULT_CANCELED);
        trackMusicUsed("none");

        finish();
    }

    /**
     * Sends to GA the music genre used in a video
     *
     * @param genre the music style applied
     */
    private void trackMusicUsed(String genre) {
        Tracker t = ((VideonaApplication) this.getApplication()).getTracker();
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Edit")
                .setAction("Music applied")
                .setCategory(genre)
                .build());
    }


}
