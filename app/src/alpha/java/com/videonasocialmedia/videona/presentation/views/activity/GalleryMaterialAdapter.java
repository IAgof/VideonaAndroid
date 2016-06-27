package com.videonasocialmedia.videona.presentation.views.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.views.listener.GalleryItemClickListener;

import java.util.List;

/**
 * Created by acer on 27/06/2016.
 */
public class GalleryMaterialAdapter  extends RecyclerView.Adapter<GalleryMaterialAdapter.GalleryViewHolder> {

    private final GalleryItemClickListener galleryItemClickListener;
    private List<Video> items;
    private Context context;



    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public ImageView imagen;

        public GalleryViewHolder (View v) {
            super(v);
            imagen= (ImageView)v.findViewById(R.id.ivimageGalleryMaterial1);
        }

    }

    public GalleryMaterialAdapter(List<Video> items, GalleryItemClickListener galleryItemClickListener) {
        this.items = items;
        this.galleryItemClickListener= galleryItemClickListener;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_galllery_material_image, parent, false);

        this.context=parent.getContext();
        return new GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder viewHolder, final int i) {

        Video selectedVideo=items.get(i);
        String path=selectedVideo.getIconPath() !=null
                ? selectedVideo.getIconPath() : selectedVideo.getMediaPath();
        Glide.with(context).load(path).centerCrop().error(R.drawable.fragment_gallery_no_image).into(viewHolder.imagen);

        viewHolder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryItemClickListener.OnVideoItemClick(items.get(i));

            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}



