package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.GalleryPagerPresenter;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.videona.presentation.views.fragment.VideoGalleryFragment;
import com.videonasocialmedia.videona.presentation.views.listener.OnSelectionModeListener;
import com.videonasocialmedia.videona.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by jca on 20/5/15.
 */
public class GalleryActivity extends Activity implements ViewPager.OnPageChangeListener,
        GalleryPagerView, OnSelectionModeListener {

    MyPagerAdapter adapterViewPager;
    boolean sharing;
    int selectedPage = 0;
    GalleryPagerPresenter galleryPagerPresenter;

    @InjectView(R.id.button_ok_gallery)
    ImageButton okButton;
    @InjectView(R.id.button_trash)
    ImageButton trashButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.inject(this);

        sharing = this.getIntent().getBooleanExtra("SHARE", true);

        if (sharing)
            okButton.setImageResource(R.drawable.share_activity_button_share_selector);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        vpPager.setOnPageChangeListener(this);

        galleryPagerPresenter = new GalleryPagerPresenter(this);
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

    private List<Video> getSelectedVideos() {
        List<Video> result = new ArrayList<>();
        for (int i = 0; i < adapterViewPager.getCount(); i++) {
            VideoGalleryFragment selectedFragment = adapterViewPager.getItem(i);
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
        Uri uri = Utils.obtainUriToShare(this, videoPath);
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
        } else {
            Toast.makeText(this, R.string.shareError, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button_cancel_gallery)
    public void goBack() {
        this.finish();
    }

    @OnClick(R.id.button_trash)
    public void deleteFiles() {
        final List<Video> videoList = getSelectedVideos();
        if (videoList.size() > 0) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("probando")
                    .setMessage("really quit? "+videoList.size())
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Video video : videoList) {
                                File file = new File(video.getMediaPath());
                                boolean deleted = file.delete();
                            }
                            for (int i = 0; i < adapterViewPager.getCount(); i++) {
                                VideoGalleryFragment selectedFragment = adapterViewPager.getItem(i);
                                selectedFragment.updateView();
                            }
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }
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
    public void onItemSelected() {
        okButton.setVisibility(View.VISIBLE);
        trashButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoItemSelected() {
        okButton.setVisibility(View.GONE);
        trashButton.setVisibility(View.GONE);
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;

        private VideoGalleryFragment mastersFragment;
        private VideoGalleryFragment editedFragment;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
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
            int selectionMode = VideoGalleryFragment.SELECTION_MODE_MULTIPLE;
            if (sharing) {
                selectionMode = VideoGalleryFragment.SELECTION_MODE_SINGLE;
            }
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    if (mastersFragment == null) {

                        mastersFragment = VideoGalleryFragment.newInstance
                                (VideoGalleryPresenter.MASTERS_FOLDER, selectionMode);
                    }
                    result = mastersFragment;
                    break;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    if (editedFragment == null) {
                        editedFragment = VideoGalleryFragment.newInstance
                                (VideoGalleryPresenter.EDITED_FOLDER, selectionMode);
                    }
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


