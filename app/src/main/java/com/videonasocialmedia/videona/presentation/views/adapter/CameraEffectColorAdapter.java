/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.listener.CameraEffectColorViewClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * This class is used to show the camera color gallery.
 */
public class CameraEffectColorAdapter extends RecyclerView.Adapter<CameraEffectColorAdapter.CameraEffectColor> {

    private Context context;
    private List<CameraEffectColorList> cameraEffectColorLists;
    private CameraEffectColorViewClickListener cameraEffectColorViewClickListener;
    private int selectedCameraEffectColorPosition = 0;

    /**
     * Constructor.
     *
     * @param cameraEffectColorList the list of the available color effects
     */
    public CameraEffectColorAdapter(List<CameraEffectColorList> cameraEffectColorList) {
        this.cameraEffectColorLists = cameraEffectColorList;
    }

    /**
     * This method returns the list of the available color effects
     *
     * @return
     */
    public List<CameraEffectColorList> getElementList() {
        return cameraEffectColorLists;
    }

    @Override
    public CameraEffectColor onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.record_item_camera_effect_color, viewGroup, false);
        this.context = viewGroup.getContext();
        return new CameraEffectColor(rowView, cameraEffectColorViewClickListener);
    }

    @Override
    public void onBindViewHolder(CameraEffectColor holder, int position) {
        CameraEffectColorList selectedCameraEffectColor = cameraEffectColorLists.get(position);
        holder.thumb.setImageResource(selectedCameraEffectColor.getIconCameraEffectColorId());
        //holder.thumb.setBackgroundResource(selectedCameraFx.getColorResourceId());
        Glide.with(context)
                .load(selectedCameraEffectColor.getIconCameraEffectColorId())
                .error(R.drawable.gatito_rules)
                .into(holder.thumb);
       // holder.overlay.setSelected(position == selectedCameraEffectColorPosition);
       // holder.thumbPressed.setSelected(position == selectedCameraEffectColorPosition);
        if(position == selectedCameraEffectColorPosition){
            holder.thumb.setImageResource(selectedCameraEffectColor.getIconCameraEffectColorIdPressed());
        }
    }

    @Override
    public int getItemCount() {
        return cameraEffectColorLists.size();
    }

    /**
     * Returns the cameraEffectColor for a given position
     *
     * @param position the position of the cameraEffectColor element
     * @return
     */
    public CameraEffectColorList getCameraEffectColor(int position) {
        return cameraEffectColorLists.get(position);
    }

    /**
     * Sets the listener of the recycler view
     *
     * @param cameraEffectColorViewClickListener
     */
    public void setCameraEffectColorViewClickListener(CameraEffectColorViewClickListener cameraEffectColorViewClickListener) {
        this.cameraEffectColorViewClickListener = cameraEffectColorViewClickListener;
    }

    /**
     * Appends new cameraEffectColor to the actual cameraEffectColor list
     *
     * @param cameraEffectColorList
     */
    public void appendCameraEffectColor(List<CameraEffectColorList> cameraEffectColorList) {
        this.cameraEffectColorLists.addAll(cameraEffectColorList);
    }

    /**
     * Checks if the cameraEffectColor list is empty
     *
     * @return
     */
    public boolean isCameraEffectColorListEmpty() {
        return cameraEffectColorLists.isEmpty();
    }

    /**
     * This class is used to controls an item view of cameraEffectColor and metadata about its place within
     * the recycler view.
     */
    class CameraEffectColor extends RecyclerView.ViewHolder implements View.OnTouchListener {

        CameraEffectColorViewClickListener onClickListener;

        @InjectView(R.id.imageCameraEffectColor)
        ImageView thumb;

        /**
         * Constructor.
         *
         * @param itemView        the view of the cameraEffectColor elements
         * @param onClickListener the listener which controls the actions over the cameraEffectColor elements
         */
        public CameraEffectColor(View itemView, CameraEffectColorViewClickListener onClickListener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            thumb.setOnTouchListener(this);
            this.onClickListener = onClickListener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                notifyItemChanged(selectedCameraEffectColorPosition);
                selectedCameraEffectColorPosition = getAdapterPosition();
                notifyItemChanged(selectedCameraEffectColorPosition);
                onClickListener.onClickCameraEffectColor(selectedCameraEffectColorPosition);
            }
            return true;
        }
    }
}