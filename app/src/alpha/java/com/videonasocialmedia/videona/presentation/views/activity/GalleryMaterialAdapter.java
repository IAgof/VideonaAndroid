package com.videonasocialmedia.videona.presentation.views.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.videonasocialmedia.videona.R;

import java.util.List;

/**
 * Created by acer on 27/06/2016.
 */
public class GalleryMaterialAdapter  extends RecyclerView.Adapter<GalleryMaterialAdapter.GalleryViewHolder> {

private List<Integer> items;


    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public ImageView imagen;

        public GalleryViewHolder (View v) {
            super(v);
            imagen= (ImageView)v.findViewById(R.id.ivimageGalleryMaterial1);
        }

    }

    public GalleryMaterialAdapter(List<Integer> items) {
        this.items = items;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_galllery_material_image, parent, false);
        return new GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder viewHolder, int i) {
        viewHolder.imagen.setImageResource(items.get(i));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}



