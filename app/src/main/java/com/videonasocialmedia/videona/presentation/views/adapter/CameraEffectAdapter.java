/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.fragment.RecordFragment;
import com.videonasocialmedia.videona.presentation.views.listener.CameraEffectClickListener;

import java.util.ArrayList;

public class CameraEffectAdapter extends ArrayAdapter<String> {

    private String LOG_TAG = this.getClass().getSimpleName();

    private RecordFragment recordFragment;

    private LayoutInflater inflater;

    private ArrayList<String> cameraEffectItems;

    private CameraEffectClickListener cameraEffectClickListener;

    private String cameraEffectName;
    private Boolean isPressed = false;


    public void setViewClickCameraEffectListener(RecordFragment viewClickListener) {

        cameraEffectClickListener = (CameraEffectClickListener) viewClickListener;
    }

    /*
     Descomentar Camera2VideoFragment

    public void setViewClickListenerLollipop(Camera2VideoFragment viewClickListener) {

        cameraEffectClickListener = (ColorEffectClickListener) viewClickListener;
    }

    */

    public CameraEffectAdapter(RecordFragment recordFragment, ArrayList<String> cameraEffectItems) {
        super(recordFragment.getActivity(), R.layout.item_camera_effect, cameraEffectItems);
        this.cameraEffectItems = cameraEffectItems;
        this.recordFragment = recordFragment;
    }

    public int getCount() {

        return cameraEffectItems.size();
    }

    public String getItem(int position) {
        return cameraEffectItems.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {


        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_camera_effect, null, false);

            viewHolder = new ViewHolder();

            viewHolder.textView = (TextView) convertView.findViewById(R.id.textCameraEffect);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }


        cameraEffectName = cameraEffectItems.get(position);

        String resourceTextView = "";

        if (RecordFragment.positionCameraEffectPressed == position) {
            isPressed = true;
            resourceTextView = cameraEffectName;
            isPressed = false;
        } else {
            resourceTextView = cameraEffectName; //"None";
        }

        viewHolder.textView.setText(resourceTextView);



        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Restart last position

                isPressed = true;
                cameraEffectName = cameraEffectItems.get(position);

                cameraEffectClickListener.onCameraEffectClicked(new CameraEffectAdapter(recordFragment, cameraEffectItems), cameraEffectName, position);

                 viewHolder.textView.setText(cameraEffectName);
                 isPressed = false;
                 viewHolder.textView.setBackgroundColor(Color.BLUE);
                 viewHolder.textView.setTypeface(null, Typeface.BOLD);


            }
        });

        return convertView;
    }


    private static class ViewHolder {

        TextView textView;
    }




}

