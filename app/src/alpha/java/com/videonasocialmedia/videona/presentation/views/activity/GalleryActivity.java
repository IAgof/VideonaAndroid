package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.GalleryPagerPresenter;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.videona.presentation.views.dialog.VideonaDialog;
import com.videonasocialmedia.videona.presentation.views.fragment.VideoGalleryFragment;
import com.videonasocialmedia.videona.presentation.views.listener.OnSelectionModeListener;
import com.videonasocialmedia.videona.presentation.views.listener.VideonaDialogListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jca on 20/5/15.
 */
public class GalleryActivity extends VideonaActivity implements ViewPager.OnPageChangeListener,
        GalleryPagerView, OnSelectionModeListener, VideonaDialogListener {

    private final String MASTERS_FRAGMENT_TAG="MASTERS";
    private final String EDITED_FRAGMENT_TAG="EDITED";

    MyPagerAdapter adapterViewPager;
    boolean sharing = false;
    int selectedPage = 0;
    private int countVideosSelected = 0;
    GalleryPagerPresenter galleryPagerPresenter;
    private VideonaDialog dialog;
    private final int REQUEST_CODE_REMOVE_VIDEOS_FROM_GALLERY = 1;

    @Bind(R.id.button_ok_gallery)
    ImageButton okButton;
    @Bind(R.id.gallery_count_selected_videos)
    TextView videoCounter;
    @Bind(R.id.gallery_image_view_clips)
    ImageView galleryImageViewClips;
    @Bind(R.id.selection_mode)
    LinearLayout selectionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        Log.d("GALLERY ACTIVITY", "Creating Activity");
        sharing = this.getIntent().getBooleanExtra("SHARE", true);

        if (sharing)
            okButton.setImageResource(R.drawable.share_activity_button_share_selector);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getFragmentManager(), savedInstanceState);
        vpPager.setAdapter(adapterViewPager);

        vpPager.setOnPageChangeListener(this);
        galleryPagerPresenter = new GalleryPagerPresenter(this);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorBlack));
        pagerTabStrip.setTextColor(getResources().getColor(R.color.colorBlack));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getFragmentManager().putFragment(outState, MASTERS_FRAGMENT_TAG, adapterViewPager.getItem(0));
        getFragmentManager().putFragment(outState, EDITED_FRAGMENT_TAG,adapterViewPager.getItem(1));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        countVideosSelected = getSelectedVideos().size();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectedPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private List<Video> getSelectedVideosFromCurrentFragment() {
        VideoGalleryFragment selectedFragment = adapterViewPager.getItem(selectedPage);
        return selectedFragment.getSelectedVideoList();

    }

    private List<Video> getSelectedVideosFromFragment(int selectedFragmentId) {
        VideoGalleryFragment selectedFragment = adapterViewPager.getItem(selectedFragmentId);
        return selectedFragment.getSelectedVideoList();

    }

    private List<Video> getSelectedVideos() {
        List<Video> result = new ArrayList<>();
        for (int i = 0; i < adapterViewPager.getCount(); i++) {
            VideoGalleryFragment selectedFragment = adapterViewPager.getItem(i);
            Log.d("GALLERY ACTIVITY", selectedFragment.toString());
            List<Video> videosFromFragment = selectedFragment.getSelectedVideoList();
            result.addAll(videosFromFragment);
        }
        return result;
    }

    @OnClick(R.id.button_ok_gallery)
    public void onClick() {
        List<Video> videoList;
        if (sharing) {
            videoList = getSelectedVideosFromCurrentFragment();
            if (videoList.size() > 0)
                shareVideo(videoList.get(0));
        } else {
            videoList = getSelectedVideos();
            if (videoList.size() > 0)
                galleryPagerPresenter.loadVideoListToProject(videoList);

        }
    }

    private void shareVideo(Video selectedVideo) {
        String videoPath = selectedVideo.getMediaPath();
        Intent intent = new Intent(this, ShareVideoActivity.class);
        intent.putExtra("VIDEO_EDITED", videoPath);
        startActivity(intent);
    }

    @OnClick(R.id.button_cancel_gallery)
    public void goBack() {
        this.finish();
    }

    @OnClick(R.id.button_trash)
    public void deleteFiles() {
        final List<Video> videoList = getSelectedVideos();
        int numVideosSelected = videoList.size();
        if (numVideosSelected > 0) {
            String title;
            if(numVideosSelected == 1) {
                title = getResources().getString(R.string.confirmDeleteTitle) + " " +
                        String.valueOf(numVideosSelected) + " " +
                        getResources().getString(R.string.confirmDeleteTitle1);
            } else {
                title = getResources().getString(R.string.confirmDeleteTitle) + " " +
                        String.valueOf(numVideosSelected) + " " +
                        getResources().getString(R.string.confirmDeleteTitle2);
            }
            dialog = new VideonaDialog.Builder()
                    .withTitle(title)
                    .withImage(R.drawable.common_icon_bobina)
                    .withMessage(getString(R.string.confirmDeleteMessage))
                    .withPositiveButton(getString(R.string.positiveButton))
                    .withNegativeButton(getString(R.string.negativeButton))
                    .withCode(REQUEST_CODE_REMOVE_VIDEOS_FROM_GALLERY)
                    .withListener(this)
                    .create();
            dialog.show(getFragmentManager(), "removeVideosFromGalleryDialog");
        }
    }

    @Override
    public void onClickPositiveButton(int id) {
        if(id == REQUEST_CODE_REMOVE_VIDEOS_FROM_GALLERY) {
            final List<Video> videoList = getSelectedVideos();
            for (Video video : videoList) {
                File file = new File(video.getMediaPath());
                file.delete();
            }
            for (int i = 0; i < adapterViewPager.getCount(); i++) {
                VideoGalleryFragment selectedFragment = adapterViewPager.getItem(i);
                selectedFragment.updateView();
            }
            countVideosSelected = 0;
            updateCounter();
            dialog.dismiss();
        }
    }

    @Override
    public void onClickNegativeButton(int id) {
        if(id == REQUEST_CODE_REMOVE_VIDEOS_FROM_GALLERY)
            dialog.dismiss();
    }

    @Override
    public void navigate() {
        if (!sharing) {
            Intent intent;
            intent = new Intent(this, EditActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onNoItemSelected() {
        // todo: out of selection mode
    }

    @Override
    public void onItemChecked() {
        if(sharing) {
            countVideosSelected = 1;
            videoCounter.setVisibility(View.GONE);
            galleryImageViewClips.setVisibility(View.GONE);
        } else {
            countVideosSelected++;
        }
        updateCounter();
    }

    @Override
    public void onItemUnchecked() {
        if(!sharing) {
            countVideosSelected--;
            updateCounter();
        }
    }

    @Override
    public void onExitSelection() {

    }

    @Override
    public void onConfirmSelection() {

    }

    private void updateCounter() {
        if(selectionMode.getVisibility() != View.VISIBLE)
            selectionMode.setVisibility(View.VISIBLE);
        if(!sharing) {
            videoCounter.setText(Integer.toString(countVideosSelected));
            if(countVideosSelected == 0)
                selectionMode.setVisibility(View.GONE);
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        private final int NUM_ITEMS = 2;

        private VideoGalleryFragment mastersFragment;
        private VideoGalleryFragment editedFragment;

        public MyPagerAdapter(FragmentManager fragmentManager, Bundle savedStateInstance) {
            super(fragmentManager);
            if (savedStateInstance==null) {
                createFragments();
            }else{
                restoreFragments(fragmentManager, savedStateInstance);
            }
        }

        private void createFragments(){
            int selectionMode = sharing ? VideoGalleryFragment.SELECTION_MODE_SINGLE
                    :VideoGalleryFragment.SELECTION_MODE_MULTIPLE;

            mastersFragment = VideoGalleryFragment.newInstance
                    (VideoGalleryPresenter.MASTERS_FOLDER, selectionMode);
            editedFragment = VideoGalleryFragment.newInstance
                    (VideoGalleryPresenter.EDITED_FOLDER, selectionMode);
        }

        private void restoreFragments(FragmentManager fragmentManager, Bundle savedStateInstance) {
            mastersFragment=(VideoGalleryFragment)
                    fragmentManager.getFragment(savedStateInstance, MASTERS_FRAGMENT_TAG);
            editedFragment=(VideoGalleryFragment)
                    fragmentManager.getFragment(savedStateInstance, EDITED_FRAGMENT_TAG);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public VideoGalleryFragment getItem(int position) {
            VideoGalleryFragment result;
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    result = mastersFragment;
                    break;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    result = editedFragment;
                    break;
                default:
                    result = null;
            }
            return result;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.mastersFolderTitle);
                case 1:
                    return getResources().getString(R.string.editedFolderTitle);
                default:
                    return getResources().getString(R.string.galleryActivityTitle);
            }
        }
    }

}


