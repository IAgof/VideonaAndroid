package com.videona.videona.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.videona.videona.Config;
import com.videona.videona.R;
import com.videona.videona.UserPreferences;
import com.videona.videona.VideonaMainActivity;
import com.videona.videona.utils.TimeUtils;

import java.io.File;

/**
 * Created by amm on 10/09/14.
 */
public class ShareVideoActivity  extends Activity {

    private final String LOG_TAG= this.getClass().getSimpleName();
    
    private static final int CHOOSE_SHARE_REQUEST_CODE = 600;
    
    private static VideoView videoView;
    private static MediaPlayer mediaPlayer;

    private TextView detailVideoName;
    private TextView detailVideoDuration;
    private TextView detailVideoDescription;
    
    private int durationVideoRecorded;
    
    private String videoEdited;
    
    // Buttons
    private ImageButton btnShare;
    private ImageButton btnRec;
    private ImageButton btnPlay;

    private SeekBar seekBar;

    private boolean isRunning = false;

    private ProgressDialog progressDialog;

    private UserPreferences appPrefs;

    private Typeface tf;

    private boolean music_selected = false;

    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            updateSeekProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share);

        tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        
        videoView = (VideoView) findViewById(R.id.videoViewShare);

        mediaPlayer = new MediaPlayer();
        
        detailVideoName=(TextView)findViewById(R.id.textViewVideoName);        
        detailVideoName.setTypeface(VideonaMainActivity.tf);
        
        detailVideoDuration=(TextView)findViewById(R.id.textViewVideoDuration);        
        detailVideoDuration.setTypeface(VideonaMainActivity.tf);
        
     //   detailVideoDescription=(TextView)findViewById(R.id.textViewVideoDescription);        
     //   detailVideoDescription.setTypeface(VideonaMainActivity.tf);
        
        btnShare = (ImageButton) findViewById(R.id.imageButtonShare);
        btnShare.setOnClickListener(shareClickListener());
        
        btnRec = (ImageButton) findViewById(R.id.imageBackRec);
        btnRec.setOnClickListener(recBackClickListener());


        appPrefs = new UserPreferences(getApplicationContext());

        Log.d(LOG_TAG, "getIsMusicON " + appPrefs.getIsMusicON());

        if(appPrefs.getIsMusicON()) {

            videoEdited = appPrefs.getVideoMusicAux() ;

            setVideoInfo();

            previewVideo();


            final Runnable r = new Runnable() {
                public void run() {

                    doTrimAudio();

                }
            };

            progressDialog = ProgressDialog.show(ShareVideoActivity.this, getString(R.string.dialog_processing_audio), getString(R.string.please_wait), true);

            // Custom progress dialog
            progressDialog.setIcon(R.drawable.ic_action_cut);

            ((TextView) progressDialog.findViewById(Resources.getSystem()
                    .getIdentifier("message", "id", "android")))
                    .setTypeface(tf);
            ((TextView) progressDialog.findViewById(Resources.getSystem()
                    .getIdentifier("message", "id", "android")))
                    .setTextColor(Color.WHITE);
            ((TextView) progressDialog.findViewById(Resources.getSystem()
                    .getIdentifier("alertTitle", "id", "android")))
                    .setTypeface(tf);
            ((TextView) progressDialog.findViewById(Resources.getSystem()
                    .getIdentifier("alertTitle", "id", "android")))
                    .setTextColor(Color.WHITE);

            progressDialog.findViewById(
                    Resources.getSystem().getIdentifier("topPanel", "id",
                            "android")).setBackgroundColor(R.color.videona_blue);
            progressDialog.findViewById(
                    Resources.getSystem().getIdentifier("customPanel", "id",
                            "android"))
                    .setBackgroundColor(R.color.videona_blue);


            performOnBackgroundThread(r);



        } else {

            // getting intent data
            Intent in = getIntent();
            videoEdited = in.getStringExtra("MEDIA_OUTPUT");

            Log.d(LOG_TAG, "VideoEdited " + videoEdited );

            if(videoEdited == null) {

                finish();

            } else {

                 setVideoInfo();

                 previewVideo();

            }

        }


        seekBar = (SeekBar) findViewById(R.id.seekBarShare);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(mediaPlayer.isPlaying()) {

                    mediaPlayer.pause();

                    btnPlay.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                if(fromUser) {

                    mediaPlayer.seekTo(progress);

                }

            }
        });

        btnPlay = (ImageButton) findViewById(R.id.imageButtonPlayShare);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnPlay.setVisibility(View.INVISIBLE);

                mediaPlayer.start();

                updateSeekProgress();

            }
        });




    }

    private void updateSeekProgress() {

        if (mediaPlayer !=null && mediaPlayer.isPlaying()) {

            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(r, 50);

        }
    }

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mediaPlayer.isPlaying() && isRunning) {
                // stop playback and set playback position on the end of trim
                // border
                mediaPlayer.pause();

                //mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            }
        }

    };



    private void doTrimAudio(){

        trimAudio();


        this.runOnUiThread(new Runnable() {
            public void run() {

                progressDialog.dismiss();

                setVideoInfo();

                previewVideo();

            }
        });


    }

    private void trimAudio(){

        videoEdited = appPrefs.getVideoMusicAux() ;

        String videonaMusic = "V_MUSIC_" + new File(videoEdited).getName().substring(7);

        String pathVideonaFinal = Config.pathVideoMusic + File.separator + videonaMusic;

        int length = appPrefs.getVideoDurationTrim();

        Log.d(LOG_TAG, "VideonaMainActivity cut " + Config.videoMusicTempFile + " .-.-.-. " +  pathVideonaFinal + " .-.-.-. " + length);

        VideonaMainActivity.cut(Config.videoMusicTempFile, pathVideonaFinal, 0, length);

        videoEdited = pathVideonaFinal;

        File temp = new File(Config.videoMusicTempFile);
        temp.deleteOnExit();

        appPrefs.setIsMusicON(false);

        music_selected = true;


    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    /**  Use screen touches to toggle the video between playing and paused. */
    @Override
    public boolean onTouchEvent (MotionEvent ev){
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            if(mediaPlayer.isPlaying()){

                mediaPlayer.pause();
                btnPlay.setVisibility(View.VISIBLE);


            } else {

                btnPlay.setVisibility(View.VISIBLE);

            }
            return true;
        } else {
            return false;
        }
    }
    
    private View.OnClickListener shareClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Log.d(LOG_TAG, "shareClickListener");
                
                videoView.pause();

                videoView.stopPlayback();
                
                videoView.suspend();


    			ContentValues content = new ContentValues(4);
    		    content.put(Video.VideoColumns.TITLE, videoEdited);
    		    content.put(Video.VideoColumns.DATE_ADDED,
    		    System.currentTimeMillis() / 1000);
    		    content.put(Video.Media.MIME_TYPE, "video/mp4");
    		    content.put(MediaStore.Video.Media.DATA, videoEdited);
    		    ContentResolver resolver = getContentResolver();
    		    Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
    		    content);

    		    Intent intent = new Intent(Intent.ACTION_SEND);
    		    intent.setType("video/*");
    		    intent.putExtra(Intent.EXTRA_STREAM, uri);
    		    startActivityForResult(Intent.createChooser(intent, getString(R.string.share_using)), CHOOSE_SHARE_REQUEST_CODE);                
             
                }

        };
    }
    
    private View.OnClickListener recBackClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Log.d(LOG_TAG, "recBackClickListener");
                
                videoView.pause();

                videoView.stopPlayback();
                
                videoView.suspend();


                // Kill process. Needed to load again ffmpeg libraries
       	        int pid = android.os.Process.myPid();  
       		    android.os.Process.killProcess(pid);   
             
            }

        };
    }
    
    private void setVideoInfo() {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoEdited);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong( time );
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);

        durationVideoRecorded = (int) duration;

    }
    
    public void previewVideo() {
        
    	try {

            videoView.setVideoPath(videoEdited);
            videoView.setMediaController(null);
            videoView.requestFocus();
            videoView.canSeekBackward();
            videoView.canSeekForward();
            videoView.setOnPreparedListener(new OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {


                    mediaPlayer = mp;


                    seekBar.setMax(durationVideoRecorded * 1000);

                    // avoid first black screen
                   // videoView.seekTo(500);

                    mediaPlayer.start();

                    mediaPlayer.seekTo(500);

                    mediaPlayer.pause();

                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    Log.d(LOG_TAG, "EditVideoActivity setOnCompletionListener" );

                    btnPlay.setVisibility(View.VISIBLE);

                }
            });



            refreshDetailView();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void refreshDetailView() {

        Log.d(LOG_TAG, "refreshDetailView");

        String duration = TimeUtils.toFormattedTime(durationVideoRecorded * 1000);
        String name = new File(videoEdited).getName();
        
        detailVideoDuration.append(duration);        
        detailVideoName.append(name);

    }
    
    @Override
    public void onBackPressed() {
 	   

        if(music_selected) {

            videoView.pause();

            videoView.stopPlayback();

            videoView.suspend();

            // Kill process. Needed to load again ffmpeg libraries
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);

        } else {

            setResult(Activity.RESULT_OK);

            finish();

        }

    }

    @Override
    protected void onStart(){
        super.onStart();

        isRunning = true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	//super.onActivityResult(requestCode, resultCode, data);
   	 
    	if(data == null) {
    		
    	} else {
    		Log.d(LOG_TAG, "requestCode " + requestCode + "resultCode " + resultCode + "intent data " + data.getDataString());
    	
	    	
	        if(requestCode==CHOOSE_SHARE_REQUEST_CODE ) {
	        	
	            //setResult(Activity.RESULT_OK);
	            //finish();
	            
	            // Kill process. Needed to load again ffmpeg libraries
	   	        int pid = android.os.Process.myPid();  
	   		    android.os.Process.killProcess(pid);
	
	        }
	        
    	}


    }
    

    
}
