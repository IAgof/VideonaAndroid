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
import com.videonasocialmedia.videona.presentation.views.listener.CameraEffectFxViewClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * This class is used to show the camera fx gallery.
 */
public class CameraEffectFxAdapter extends RecyclerView.Adapter<CameraEffectFxAdapter.CameraEffectFx> {

    private Context context;
    private List<CameraEffectFxList> cameraFxList;
    private CameraEffectFxViewClickListener cameraEffectFxRecyclerViewClickListener;
    private int selectedCameraEffectFxPosition = 0;

    /**
     * Constructor.
     *
     * @param cameraFxList the list of the available songs
     */
    public CameraEffectFxAdapter(List<CameraEffectFxList> cameraFxList) {
        this.cameraFxList = cameraFxList;
    }

    /**
     * This method returns the list of the available songs
     *
     * @return
     */
    public List<CameraEffectFxList> getElementList() {
        return cameraFxList;
    }

    @Override
    public CameraEffectFx onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.record_item_camera_effect_fx, viewGroup, false);
        this.context = viewGroup.getContext();
        return new CameraEffectFx(rowView, cameraEffectFxRecyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(CameraEffectFx holder, int position) {
        CameraEffectFxList selectedCameraFx = cameraFxList.get(position);
        holder.thumb.setImageResource(selectedCameraFx.getIconCameraFxId());
        //holder.thumb.setBackgroundResource(selectedCameraFx.getColorResourceId());
        Glide.with(context)
                .load(selectedCameraFx.getIconCameraFxId())
                .error(R.drawable.gatito_rules)
                .into(holder.thumb);
       // holder.overlay.setSelected(position == selectedCameraEffectFxPosition);
       // holder.thumbPressed.setSelected(position == selectedCameraEffectFxPosition);
        if(position == selectedCameraEffectFxPosition){
            holder.thumb.setImageResource(selectedCameraFx.getIconCameraFxIdPressed());
        }
    }

    @Override
    public int getItemCount() {
        return cameraFxList.size();
    }

    /**
     * Returns the cameraEffectFx for a given position
     *
     * @param position the position of the cameraEffectFx element
     * @return
     */
    public CameraEffectFxList getCameraEffectFx(int position) {
        return cameraFxList.get(position);
    }

    /**
     * Sets the listener of the recycler view
     *
     * @param cameraEffectFxRecyclerViewClickListener
     */
    public void setCameraEffectFxRecyclerViewClickListener(CameraEffectFxViewClickListener cameraEffectFxRecyclerViewClickListener) {
        this.cameraEffectFxRecyclerViewClickListener = cameraEffectFxRecyclerViewClickListener;
    }

    /**
     * Appends new cameraEffectFx to the actual cameraEffectFx list
     *
     * @param cameraFxListList
     */
    public void appendCameraEffectFx(List<CameraEffectFxList> cameraFxListList) {
        this.cameraFxList.addAll(cameraFxListList);
    }

    /**
     * Checks if the cameraEffectFx list is empty
     *
     * @return
     */
    public boolean isCameraEffectFxListEmpty() {
        return cameraFxList.isEmpty();
    }

    /**
     * This class is used to controls an item view of cameraEffectFx and metadata about its place within
     * the recycler view.
     */
    class CameraEffectFx extends RecyclerView.ViewHolder implements View.OnTouchListener {

        CameraEffectFxViewClickListener onClickListener;

        @InjectView(R.id.imageCameraEffectFx)
        ImageView thumb;

        /**
         * Constructor.
         *
         * @param itemView        the view of the cameraEffectFx elements
         * @param onClickListener the listener which controls the actions over the cameraEffectFx elements
         */
        public CameraEffectFx(View itemView, CameraEffectFxViewClickListener onClickListener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            thumb.setOnTouchListener(this);
            this.onClickListener = onClickListener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                notifyItemChanged(selectedCameraEffectFxPosition);
                selectedCameraEffectFxPosition = getAdapterPosition();
                notifyItemChanged(selectedCameraEffectFxPosition);
                onClickListener.onClickCameraEffectFx(selectedCameraEffectFxPosition);
            }
            return true;
        }
    }
}