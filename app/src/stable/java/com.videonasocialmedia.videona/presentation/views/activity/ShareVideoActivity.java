package com.videonasocialmedia.videona.presentation.views.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.videonasocialmedia.videona.R;
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
 * Created by jca on 11/12/15.
 */
public class ShareVideoActivity extends VideonaActivity implements ShareVideoView, VideoPlayerView,
        SocialNetworkAdapter.OnSocialNetworkClickedListener,
        SeekBar.OnSeekBarChangeListener {

    @Bind(R.id.video_preview)
    VideoView videoPreview;
    @Bind(R.id.main_social_network_list)
    RecyclerView mainSocialNetworkList;
    @Bind(R.id.play_pause_button)
    ImageButton playPauseButton;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.button_more_networks)
    FloatingActionButton fab;
    @Bind(R.id.seekbar)
    SeekBar seekBar;
    @Nullable
    @Bind(R.id.bottom_panel)
    RelativeLayout bottomPanel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        sharedPreferences =
                getSharedPreferences(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                        Context.MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();
        presenter = new ShareVideoPresenter(this);
        presenter.onCreate();
        initToolbar();
        initFloatingActionButton();
        if (videoPosition == 0)
            videoPosition = 100;
        boolean isPlaying = false;
        if (savedInstanceState != null) {
            videoPosition = savedInstanceState.getInt("videoPosition", 100);
            isPlaying = savedInstanceState.getBoolean("videoPlaying", false);
        }
        initVideoPreview(videoPosition, isPlaying);
        initNetworksList();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            if (isLandscapeOriented()) {
                ab.setDisplayShowTitleEnabled(false);
                toolbar.getBackground().setAlpha(0);
            } else
                ab.setDisplayShowHomeEnabled(true);
        }
    }



    private void initFloatingActionButton() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < android.os.Build.VERSION_CODES.LOLLIPOP)
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorButton)));
    }

    private void initVideoPreview(final int position, final boolean playing) {
        videoPath = getIntent().getStringExtra(Constants.VIDEO_TO_SHARE_PATH);
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
                if (isLandscapeOriented()) {
                    showBottomPanel();
                    showToolbar();
                }
                seekBar.setProgress(0);
            }
        });
    }

    private void initNetworksList() {
        mainSocialNetworkAdapter = new SocialNetworkAdapter(this);

        int orientation = LinearLayoutManager.VERTICAL;
        if (isLandscapeOriented())
            orientation = LinearLayoutManager.HORIZONTAL;

        mainSocialNetworkList.setLayoutManager(
                new LinearLayoutManager(this, orientation, false));
        mainSocialNetworkList.setAdapter(mainSocialNetworkAdapter);
    }

    private void initSeekBar(int progress, int max) {
        seekBar.setMax(max);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(this);
        updateSeekbar();
    }

    @OnClick(R.id.play_pause_button)
    @Override
    public void playVideo() {
        videoPreview.start();
        playPauseButton.setVisibility(View.GONE);
        if (isLandscapeOriented()) {
            hideToolBar();
            hideBottomPanel();
        }
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

    private void showBottomPanel() {
        fab.show();
        runTranslateAnimation(bottomPanel, 1, new AccelerateInterpolator(3));
    }

    private void showToolbar() {
        runTranslateAnimation(toolbar, 0, new DecelerateInterpolator(3));
    }

    private void updateSeekbar() {
        if (!draggingSeekBar)
            seekBar.setProgress(videoPreview.getCurrentPosition());
        updateSeekBarTaskHandler.postDelayed(updateSeekBarTask, 20);
    }

    private void hideToolBar() {
        runTranslateAnimation(toolbar,
                -toolbar.getHeight(), new AccelerateInterpolator(3));
    }

    private void hideBottomPanel() {
        runTranslateAnimation(bottomPanel, bottomPanel.getHeight(), new AccelerateInterpolator(3));
        fab.hide();
    }

    private void runTranslateAnimation(View view, int translateY, Interpolator interpolator) {
        Animator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", translateY);
        slideInAnimation.setDuration(view.getContext().getResources()
                .getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimation.setInterpolator(interpolator);
        slideInAnimation.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseVideo();
        videoPosition = videoPreview.getCurrentPosition();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("videoPosition", videoPreview.getCurrentPosition() - 200);
        outState.putBoolean("videoPlaying", videoPreview.isPlaying());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void showShareNetworksAvailable(List<SocialNetwork> networks) {
        // TODO move this to presenter in merging alpha and stable.
        SocialNetwork saveToGallery = new SocialNetwork("SaveToGallery",getString(R.string.save_to_gallery), "", "", this.getResources().getDrawable(R.drawable.activity_share_save_to_gallery), "");
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
        draggingSeekBar = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        draggingSeekBar = false;
    }

    @OnClick(R.id.button_more_networks)
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
            socialNetworkName = socialNetwork.getIdSocialNetwork();
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

    @OnTouch(R.id.video_preview)
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
    public void onSocialNetworkClicked(SocialNetwork socialNetwork) {
        trackVideoShared(socialNetwork);
        if(socialNetwork.getName().equals(getString(R.string.save_to_gallery)) ){
            showMessage(R.string.video_saved);
            return;
        }
        presenter.shareVideo(videoPath, socialNetwork, this);
        updateNumTotalVideosShared();
    }

    public void showMessage(final int stringToast) {
        Snackbar snackbar = Snackbar.make(fab, stringToast, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
