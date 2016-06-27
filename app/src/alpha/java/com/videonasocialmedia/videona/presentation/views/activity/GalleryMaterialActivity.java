package com.videonasocialmedia.videona.presentation.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.GalleryPagerPresenter;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoGalleryView;
import com.videonasocialmedia.videona.presentation.views.listener.GalleryItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryMaterialActivity extends AppCompatActivity implements VideoGalleryView, GalleryItemClickListener, GalleryPagerView {

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    protected VideoGalleryPresenter videoGalleryPresenter;
    protected GalleryPagerPresenter galleryPagerPresenter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_material);

        videoGalleryPresenter= new VideoGalleryPresenter(this);
        galleryPagerPresenter= new GalleryPagerPresenter(this);

        recycler =(RecyclerView)findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para GridLayout
        lManager = new GridLayoutManager(this,3);
        recycler.setLayoutManager(lManager);


    }

    @Override

    public void onStart(){
        super.onStart();
        videoGalleryPresenter.obtainVideos(VideoGalleryPresenter.MASTERS_FOLDER);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showVideos(List<Video> videoList) {
        adapter = new GalleryMaterialAdapter(videoList, this);
        recycler.setAdapter(adapter);
    }

    @Override
    public boolean isTheListEmpty() {
        return false;
    }

    @Override
    public void appendVideos(List<Video> movieList) {


    }

    @Override
    public void showVideoTimeline() {

    }


    @Override
    public void OnVideoItemClick(Video video) {
        List<Video> videos = new ArrayList<Video> ();
        videos.add(video);

        galleryPagerPresenter.loadVideoListToProject(videos);
    }

    @Override
    public void navigate() {
        Intent intent = new Intent (this, EditActivity.class );
        startActivity(intent);

    }
}
