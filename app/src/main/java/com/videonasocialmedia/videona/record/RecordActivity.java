package com.videonasocialmedia.videona.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.videonasocialmedia.videona.Config;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.UserPreferences;
import com.videonasocialmedia.videona.edit.EditVideoActivity;
import com.videonasocialmedia.videona.share.ShareVideoActivity;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecordActivity extends Activity{

    private static final String LOG_TAG = "RecordActivity";

    //private final String LOG_TAT = this.getClass().getSimpleName();

    /** Called when the activity is first created. */
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    private ImageButton captureButton;

    private ImageButton btnColorEffect;
    private static Boolean isColorEffect;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    private static final int CAMERA_TRIM_VIDEO_REQUEST_CODE = 300;
    private static final int WELCOME_REQUEST_CODE = 400;
    private static final int VIDEO_SHARE_REQUEST_CODE = 500;


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Chronometer chronometer;

    private Uri fileUri; // file url to store image/video

    private String videoRecord;

    private boolean showWelcome;
	private boolean showWelcomeToday = false;
    
    private Typeface tf;
    
    private boolean btnBackPressed = false;


    private int cameraId = 0;

    private static UserPreferences appPrefs;

    private ListAdapter adapter;
    private ImageColorEffectAdadpter imageColorEffectAdadpter;
    private ListView listViewColorEffect;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);

        // Keep screen ON
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tf = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Medium.ttf");

        // Create an instance of Camera
        if(mCamera==null)
        mCamera = getCameraInstance();

        captureButton = (ImageButton) findViewById(R.id.button_capture);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);

        //FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        //preview.addView(mPreview);

        ((ViewGroup) findViewById(R.id.camera_preview)).addView(mPreview);

        Context context = getApplicationContext();
        appPrefs = new UserPreferences(context);

        Log.d(LOG_TAG, "getIsMusicON " + appPrefs.getIsMusicON());

        File fTempAV = new File(Config.videoMusicTempFile);

        if(appPrefs.getIsMusicON() && fTempAV.exists()) {

            Intent share = new Intent();
            //  share.putExtra("MEDIA_OUTPUT", pathvideoTrim);
            share.setClass(RecordActivity.this, ShareVideoActivity.class);
            startActivityForResult(share, VIDEO_SHARE_REQUEST_CODE);
        }



      //  listViewColorEffect = (ListView) findViewById(R.id.listViewColorEffect);


        chronometer = (Chronometer) findViewById(R.id.chronometerVideo);
        chronometer.setTypeface(tf);

        chronometer.setText(String.format("%02d:%02d", 0, 0));

      //  showWelcome = appPrefs.getCheckIndiegogo();

      //  Log.d(LOG_TAG, "appPrefs welcome " + showWelcome);

        Log.d(LOG_TAG, "onCreate height " + mCamera.getParameters().getPictureSize().height + " width " + mCamera.getParameters().getPictureSize().width);


        captureButton.setOnClickListener(
                new OnClickListener() {
                    public void onClick(View v) {
                        if (isRecording) {
                            // stop recording and release camera
                            mMediaRecorder.stop();  // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object
                            mCamera.lock();         // take camera access back from MediaRecorder

                            // inform the user that recording has stopped
                         //   captureButton.setImageResource(R.drawable.ic_btn_stop);  //setText("Capture");
                            isRecording = false;

                            stopChronometer();
                            
                            releaseCamera();

                            Intent trim = new Intent();
                            trim.putExtra("MEDIA_OUTPUT", videoRecord);
                            trim.setClass(RecordActivity.this, EditVideoActivity.class);
                            startActivityForResult(trim, CAMERA_TRIM_VIDEO_REQUEST_CODE);


                        } else {

                         //    mPreview.getHolder().setFixedSize(720, 1280);

                            // initialize video camera
                            if (prepareVideoRecorder()) {
                                // Camera is available and unlocked, MediaRecorder is prepared,
                                // now you can start recording

                                mMediaRecorder.start();

                                // inform the user that recording has started
                                captureButton.setImageResource(R.drawable.ic_action_stop);  //setText("Stop");
                                isRecording = true;


                                //initialize chronometer
                                setChronometer();

                                startChronometer();


                            } else {
                                // prepare didn't work, release the camera
                                releaseMediaRecorder();
                                // inform user
                            }
                        }

                    }
                }
       );


        btnColorEffect = (ImageButton) findViewById(R.id.btnColorEffect);
        btnColorEffect.setVisibility(View.VISIBLE);
        final TwoWayView lvTest = (TwoWayView) findViewById(R.id.lvItems);

        btnColorEffect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(LOG_TAG, " entro en btnColorEffect");

                ArrayList<String> colorEffects = new ArrayList<String>();

                colorEffects.add("aqua");
                colorEffects.add("emboss");
                colorEffects.add("posterize");

                imageColorEffectAdadpter = new ImageColorEffectAdadpter(RecordActivity.this, getApplicationContext(), colorEffects );



                lvTest.setAdapter(imageColorEffectAdadpter)  ;


                btnColorEffect.setVisibility(View.INVISIBLE);

            }
        });


        lvTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Log.d(LOG_TAG, " entro en twoWay");

                if (appPrefs.getColorEffect()) {
                    appPrefs.setColorEffect(false);
                } else {
                    appPrefs.setColorEffect(true);
                }

                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;

                onCreate(null);

                Toast.makeText(RecordActivity.this, "You Clicked at " + position, Toast.LENGTH_SHORT).show();

                //    lvTest.setVisibility(View.INVISIBLE);

            }

        });





    }

    private void openCamera(){

        // Create an instance of Camera
        if(mCamera==null)
            mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);

    }


    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }



    private void startChronometer()
    {

        chronometer.start();

    }

    private void stopChronometer()
    {
        chronometer.stop();

    }

    //initialize chronometer
    private void setChronometer()
    {

        chronometer.setBase(SystemClock.elapsedRealtime());

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {


                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
               // String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
               // chronometer.setText(hh+":"+mm+":"+ss);
                RecordActivity.this.chronometer.setText(mm+":"+ss);

            }
        });

    }


    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){

        Camera c = null;
        try {

            // int cameraId = appPrefs.getCameraId();
            //c = Camera.open(cameraId); // attempt to get a Camera instance

            c = Camera.open(); // attempt to get a Camera instance

            if (c != null){
                Camera.Parameters params = c.getParameters();


                params.setPictureSize(720,1280);
                c.setParameters(params);

                Log.d(LOG_TAG, "getCameraInstance height " + c.getParameters().getPictureSize().height + " width " + c.getParameters().getPictureSize().width);
            }

        }
        catch (Exception e){
            Log.d("DEBUG", "Camera did not open");
            // Camera is not available (in use or does not exist)
        }


        return c; // returns null if camera is unavailable
    }

    private boolean prepareVideoRecorder(){




     //   mCamera = getCameraInstance();


        mMediaRecorder = new MediaRecorder();


        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);


/*        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.stopPreview();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
*/

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);


        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)

        /*
        // Problem with front camera http://stackoverflow.com/questions/14681703/android-cant-record-video-with-front-facing-camera-mediarecorder-start-failed
        // Back camera
        if(appPrefs.getCameraId() == 0) {
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        } else {
            // front camera
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        }
        */

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

    /*    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);


        mMediaRecorder.setAudioSamplingRate(48000);
        mMediaRecorder.setAudioChannels(2);
        mMediaRecorder.setAudioEncodingBitRate(192000);


        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(1280, 720);
        mMediaRecorder.setVideoEncodingBitRate(5000000);

    */

        // Step 4: Set output file
        videoRecord = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();

        // Check if videoRecordName exists
        File f = new File(videoRecord);
        if(f.exists()) {
        	videoRecord = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString() + "1";
        }

        mMediaRecorder.setOutputFile(videoRecord);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("DEBUG", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("DEBUG", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
          return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

       // File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
         //         Environment.DIRECTORY_MOVIES), "VideonaApp");

        File mediaStorageDir = new File(Config.pathApp);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("RecordActivity", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO Auto-generated method stub
        // deleting image from external storage
        // FileHandler.deleteFromExternalStorage();
        // Create an instance of Camera.
      //  if (this.mCamera == null) {
        if (mCamera == null) {
            //this.mCamera = getCameraInstance();
            mCamera = getCameraInstance();

            Log.d(LOG_TAG, "onResume height " + mCamera.getParameters().getPictureSize().height + " width " + mCamera.getParameters().getPictureSize().width);
        }


    /*
        amm: Show welcome hide

       if(!showWelcomeToday) {
        	

            if (!showWelcome) {

                // Do nothing

            } else {

            //    Intent welcome = new Intent(getApplicationContext(), WelcomeActivity.class);
                Intent welcome = new Intent(getApplicationContext(), ScreenSlidePagerActivity.class);
                startActivityForResult(welcome, WELCOME_REQUEST_CODE);

            }

        } */


    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event.
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.setPreviewCallback(null);
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            isRecording = false;
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();        // release the camera for other applications
            mCamera = null;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
        if(requestCode==CAMERA_TRIM_VIDEO_REQUEST_CODE ) {
        	

            onCreate(null);

        }

   /*
     amm: Show welcome hide

      if(requestCode==WELCOME_REQUEST_CODE){

            // ¿Thanks for your support?

            showWelcomeToday = true;

            onCreate(null);

        }
     */

    }
    
    @Override
    public void onBackPressed() {
 	   
    	btnBackPressed = true;
    	
    	Toast.makeText(getApplicationContext(), getString(R.string.toast_exit), Toast.LENGTH_SHORT).show();
 	          	

    	
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
      if (keyCode == KeyEvent.KEYCODE_BACK && btnBackPressed == true) {
        // do something on back.
     	 btnBackPressed = false;
    	  Log.d(LOG_TAG,"onKeyDown" );
    	 //	finish();
    	 
        // Kill process. Exit app
	     //   int pid = android.os.Process.myPid();  
		 //   android.os.Process.killProcess(pid);
    	 
    	  Intent intent = new Intent(Intent.ACTION_MAIN);
          intent.addCategory(Intent.CATEGORY_HOME);
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
          startActivity(intent);
          finish();
          System.exit(0);
     	 	
        return true;
    }
        btnBackPressed = false;
        return super.onKeyDown(keyCode, event);
      
    }  
}