package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.export.presentation.service.ExportService;
import com.videonasocialmedia.videona.export.presentation.service.OnExportProjectListener;
import com.videonasocialmedia.videona.export.presentation.service.OnExportServiceListener;
import com.videonasocialmedia.videona.model.entities.social.SocialNetwork;
import com.videonasocialmedia.videona.presentation.mvp.presenters.ShareVideoPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoPlayerView;
import com.videonasocialmedia.videona.presentation.views.adapter.SocialNetworkAdapter;
import com.videonasocialmedia.videona.utils.AnalyticsConstants;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by root on 31/05/16.
 */
public class ShareActivity extends VideonaActivity implements ShareVideoView, VideoPlayerView,
        SocialNetworkAdapter.OnSocialNetworkClickedListener, SeekBar.OnSeekBarChangeListener {

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.video_share_preview)
    VideoView videoPreview;
    @Bind(R.id.main_social_network_list)
    RecyclerView mainSocialNetworkList;
    @Bind(R.id.button_share_play_pause)
    ImageButton playPauseButton;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab_share_room)
    FloatingActionButton fab;
    @Bind(R.id.seekbar_share_preview)
    SeekBar seekBar;
    boolean isPlaying = false;
    private String videoPath;
    private ShareVideoPresenter presenter;
    private SocialNetworkAdapter mainSocialNetworkAdapter;
    private int videoPosition;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferencesEditor;
    private Handler updateSeekBarTaskHandler = new Handler();
    private boolean draggingSeekBar;
    private Runnable updateSeekBarTask = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
        }
    };

    ExportService myService = null;
    boolean isBound;
    OnExportServiceListener serviceListener;

    private ServiceConnection myConnection =
            new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {

                    serviceListener = (OnExportServiceListener) service;
                    serviceListener.registerListener(listener);
                    isBound = true;
                    requestExportProject();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    myService = null;
                    serviceListener.unregisterListener();
                    isBound = false;
                    listener = null;
                }
            };
    private ProgressDialog barProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        setupToolBar();

        sharedPreferences =
                getSharedPreferences(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                        Context.MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();
        presenter = new ShareVideoPresenter(this);
        presenter.onCreate();

        restoreState(savedInstanceState);

        initNetworksList();

        Bundle extras = this.getIntent().getExtras();
        if(extras!= null) {
            videoPath = extras.getString("VideoExportedFinished");
        }
        initVideoPreview(videoPosition, isPlaying);

        barProgressDialog = new ProgressDialog(ShareActivity.this);

        barProgressDialog.setTitle("Exporting project ...");

        barProgressDialog.setMessage("Starting ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setIndeterminate(true);

    }

    private void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (videoPosition == 0)
            videoPosition = 100;

        if (savedInstanceState != null) {
            videoPosition = savedInstanceState.getInt("videoPosition", 100);
            isPlaying = savedInstanceState.getBoolean("videoPlaying", false);
            videoPath = savedInstanceState.getString("videoPath", null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        pauseVideo();
        videoPosition = videoPreview.getCurrentPosition();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        videoPath = intent.getStringExtra("VideoExportedFinished");
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();

        if(videoPath != null){
            initVideoPreview(videoPosition, isPlaying);
        }

        NotificationManager notificationManager = (NotificationManager) VideonaApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIFICATION_EXPORT_ID);
    }

    @Override
    protected void onStart(){
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, ExportService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop(){
        super.onStop();
        if (isBound) {
            serviceListener.unregisterListener();
            unbindService(myConnection);
            isBound = false;
        }

    }

    private void initVideoPreview(final int position, final boolean playing) {
       // videoPath = getIntent().getStringExtra(Constants.VIDEO_TO_SHARE_PATH);
        if (videoPath != null) {
            videoPreview.setVideoPath(videoPath);
            Log.d("TAG", "MESSAGE");
        }
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                try {
                    seekTo(position);
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.d("Share", "error while preparing preview");
                }
                initSeekBar(videoPreview.getCurrentPosition(), videoPreview.getDuration());
                mediaPlayer.pause();
                if (playing)
                    playVideo();
            }
        });

        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playPauseButton.setVisibility(View.VISIBLE);
                seekBar.setProgress(0);
            }
        });

    }

    private void initSeekBar(int progress, int max) {
        seekBar.setMax(max);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(this);
        updateSeekbar();
    }

    ///// GO TO ANOTHER ACTIVITY

    @OnClick(R.id.button_share_play_pause)
    @Override
    public void playVideo() {
        videoPreview.start();
        playPauseButton.setVisibility(View.GONE);
    }

    private void updateSeekbar() {
        if (!draggingSeekBar)
            seekBar.setProgress(videoPreview.getCurrentPosition());
        updateSeekBarTaskHandler.postDelayed(updateSeekBarTask, 20);
    }

    @Override
    public void pauseVideo() {
        videoPreview.pause();
        playPauseButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void seekTo(int millisecond) {
        videoPreview.seekTo(millisecond);
    }

    private void initNetworksList() {
        mainSocialNetworkAdapter = new SocialNetworkAdapter(this);

        int orientation = LinearLayoutManager.VERTICAL;
        // if (isLandscapeOriented())
        //     orientation = LinearLayoutManager.HORIZONTAL;

        mainSocialNetworkList.setLayoutManager(
                new LinearLayoutManager(this, orientation, false));
        mainSocialNetworkList.setAdapter(mainSocialNetworkAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("videoPosition", videoPreview.getCurrentPosition() - 200);
        outState.putBoolean("videoPlaying", videoPreview.isPlaying());
        outState.putString("videoPath", videoPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings_edit_options:
                navigateTo(SettingsActivity.class);
                return true;
            case R.id.action_settings_edit_gallery:
                navigateTo(GalleryActivity.class);
                return true;
            case R.id.action_settings_edit_tutorial:
                //navigateTo(TutorialActivity.class);
                return true;
            default:

        }

        return super.onOptionsItemSelected(item);
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        if (cls == GalleryActivity.class) {
            intent.putExtra("SHARE", false);
        }
        startActivity(intent);
    }

    @OnClick(R.id.fab_share_room)
    public void showMoreNetworks() {
        updateNumTotalVideosShared();
        trackVideoShared(null);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        Uri uri = Utils.obtainUriToShare(this, videoPath);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
    }

    private void updateNumTotalVideosShared() {
        int totalVideosShared = sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, 0);
        preferencesEditor.putInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, ++totalVideosShared);
        preferencesEditor.commit();
    }

    private void trackVideoShared(SocialNetwork socialNetwork) {
        trackVideoSharedSuperProperties();
        String socialNetworkName = null;
        if (socialNetwork != null)
            socialNetworkName = socialNetwork.getName();
        JSONObject socialNetworkProperties = new JSONObject();
        try {
            socialNetworkProperties.put(AnalyticsConstants.SOCIAL_NETWORK, socialNetworkName);
            socialNetworkProperties.put(AnalyticsConstants.VIDEO_LENGTH, presenter.getVideoLength());
            socialNetworkProperties.put(AnalyticsConstants.RESOLUTION, presenter.getResolution());
            socialNetworkProperties.put(AnalyticsConstants.NUMBER_OF_CLIPS, presenter.getNumberOfClips());
            socialNetworkProperties.put(AnalyticsConstants.TOTAL_VIDEOS_SHARED,
                    sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, 0));
            mixpanel.track(AnalyticsConstants.VIDEO_SHARED, socialNetworkProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.getPeople().increment(AnalyticsConstants.TOTAL_VIDEOS_SHARED, 1);
        mixpanel.getPeople().set(AnalyticsConstants.LAST_VIDEO_SHARED,
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
    }

    private void trackVideoSharedSuperProperties() {
        JSONObject updateSuperProperties = new JSONObject();
        int numPreviousVideosShared;
        try {
            numPreviousVideosShared =
                    mixpanel.getSuperProperties().getInt(AnalyticsConstants.TOTAL_VIDEOS_SHARED);
        } catch (JSONException e) {
            numPreviousVideosShared = 0;
        }
        try {
            updateSuperProperties.put(AnalyticsConstants.TOTAL_VIDEOS_SHARED,
                    ++numPreviousVideosShared);
            mixpanel.registerSuperProperties(updateSuperProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnTouch(R.id.video_share_preview)
    public boolean togglePlayPause(MotionEvent event) {
        boolean result = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (videoPreview.isPlaying()) {
                pauseVideo();
                result = true;
            } else {
                playVideo();
                result = false;
            }
        }
        return result;
    }

    @Override
    public void showShareNetworksAvailable(List<SocialNetwork> networks) {
        // TODO move this to presenter in merging alpha and stable.
        SocialNetwork saveToGallery = new SocialNetwork(getString(R.string.save_to_gallery), "", "", this.getResources().getDrawable(R.drawable.activity_share_save_to_gallery), "");
        networks.add(saveToGallery);
        mainSocialNetworkAdapter.setSocialNetworkList(networks);
    }

    @Override
    public void hideShareNetworks() {

    }

    @Override
    public void showMoreNetworks(List<SocialNetwork> networks) {

    }

    @Override
    public void hideExtraNetworks() {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            seekTo(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onSocialNetworkClicked(SocialNetwork socialNetwork) {
        trackVideoShared(socialNetwork);
        if (socialNetwork.getName().equals(getString(R.string.save_to_gallery))) {
            showMessage(R.string.video_saved);
            if(isBound) requestExportProject();
            return;
        }
        presenter.shareVideo(videoPath, socialNetwork, this);
        updateNumTotalVideosShared();
    }

    private void showMessage(String stringMessage) {
        //Snackbar snackbar = Snackbar.make(coordinatorLayout, stringMessage, Snackbar.LENGTH_LONG);
        //snackbar.show();

        barProgressDialog.setMessage(stringMessage);
    }

    private void requestExportProject() {
        barProgressDialog.show();
        Intent intent = new Intent(this, ExportService.class);
        startService(intent);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    public void showMessage(final int stringResource) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, stringResource, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private OnExportProjectListener listener = new OnExportProjectListener() {
        @Override
        public void messageService(String message) {

            if(message.compareTo("ExportFinished") == 0){
                showMessage(message);
            }
            if(message.compareTo("Error") == 0){
                showMessage(message);
                barProgressDialog.dismiss();
            }
            if(message.compareTo("Adding music") == 0){
                showMessage(message);
            }

        }

        @Override
        public void onSuccessVideoExported(String mediaPath) {
            videoPath = mediaPath;
            initVideoPreview(videoPosition, isPlaying);
            barProgressDialog.dismiss();
        }

    };
}
