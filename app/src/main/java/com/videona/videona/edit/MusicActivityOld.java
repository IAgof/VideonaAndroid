package com.videona.videona.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.videona.videona.R;
import com.videona.videona.UserPreferences;

/**
 * Created by root on 11/11/14.
 */




public class MusicActivityOld extends Activity  {

    private boolean listenMusic = false;
    private boolean selectedMusic = false;

    private MediaPlayer mediaPlayerMusic;

    protected ImageAdapter imageAdapter;

    private AsymmetricGridView listView;
    private ListAdapter adapter;
    private int currentOffset = 0;
   // private AsymmetricGridViewAdapter<DemoItem> asymmetricAdapter;



   // Test new Asymmetrics GridView
   // https://github.com/felipecsl/AsymmetricGridView/blob/master/README.md


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music);

        final UserPreferences appPrefs = new UserPreferences(getApplicationContext());

        imageAdapter = new ImageAdapter(this);
        final GridView gridView = (GridView) findViewById(R.id.gridview);

        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                if(appPrefs.getPositionMusic() != position && selectedMusic) {


                   View vv = gridView.getChildAt(appPrefs.getPositionMusic());
                   vv.setBackgroundColor(Color.TRANSPARENT);

                    if(mediaPlayerMusic != null) {
                        mediaPlayerMusic.stop();
                        mediaPlayerMusic.release();
                        mediaPlayerMusic = null;

                        listenMusic = false;
                    }

                    selectedMusic = false;

                }

                Toast.makeText(MusicActivityOld.this, "" + position, Toast.LENGTH_SHORT).show();


                if(!listenMusic) {

                    listenMusic = true;

                    if(!selectedMusic) {

                        v.setBackgroundColor(Color.GREEN);

                        appPrefs.setPositionMusic(position);

                        selectedMusic = true;

                    }

                    mediaPlayerMusic = MediaPlayer.create(getBaseContext(), R.raw.audio_aac);
                    mediaPlayerMusic.start();
                    mediaPlayerMusic.setVolume(5.0f, 5.0f);

                } else {

                    listenMusic = false;

                    if(selectedMusic) {
                        v.setBackgroundColor(Color.TRANSPARENT);


                        selectedMusic = false;
                    }


                    mediaPlayerMusic.stop();
                    mediaPlayerMusic.release();
                    mediaPlayerMusic = null;


                    Intent select = new Intent();
                    select.putExtra("position", position);

                    setResult(Activity.RESULT_OK, select);

                    finish();
                }




            }
        });
    }

}

