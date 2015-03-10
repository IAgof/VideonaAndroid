package com.videonasocialmedia.videona.presentation.views.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.listener.ColorEffectClickListener;
import com.videonasocialmedia.videona.presentation.views.fragment.Camera2VideoFragment;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.videona.utils.utils.ConstantsUtils;
import com.videonasocialmedia.videona.utils.UserPreferences;

import java.util.ArrayList;

/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */
public class ColorEffectAdapter extends ArrayAdapter<String> {

    private String LOG_TAG = this.getClass().getSimpleName();

    private Activity activity;

    private LayoutInflater inflater;

    private ArrayList<String> colorEffectItems;

    private UserPreferences appPrefs;

    private ColorEffectClickListener mColorEffectClickListener;

    private Boolean isPressed = true;
    private int lastPositionPressed = 0;


    public void setViewClickListener(RecordActivity viewClickListener) {

        mColorEffectClickListener = (ColorEffectClickListener) viewClickListener;
    }

    public void setViewClickListenerLollipop(Camera2VideoFragment viewClickListener) {

        mColorEffectClickListener = (ColorEffectClickListener) viewClickListener;
    }

    public ColorEffectAdapter(Activity activity, ArrayList<String> colorEffectItems) {
        super(activity, R.layout.item_color_effect, colorEffectItems);
        this.colorEffectItems = colorEffectItems;
        this.activity = activity;
    }

    public int getCount() {

        return colorEffectItems.size();
    }

    public String getItem(int position) {
        return colorEffectItems.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }


    // create a new ImageView and TextView for each item referenced by the Adapter
    @SuppressLint("ResourceAsColor")
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_color_effect, null, false);

            viewHolder = new ViewHolder();

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageColorEffect);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textColorEffect);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }


        String colorEffectName = colorEffectItems.get(position);

        String colorEffectDrawableName = "common_filter_" + colorEffectName;

        // colorEffectName, key value to obtain drawable resources effect_ + colorEffectName
        int resourceId = activity.getResources().getIdentifier(colorEffectDrawableName, "drawable", activity.getPackageName());
        viewHolder.imageView.setImageResource(resourceId);

        viewHolder.textView.setText(getColorEffectName(colorEffectName));


        //TODO  Save position color_effect, setBackground and bold text

       /*

       if(RecordActivity.colorEffectLastPosition == position && isPressed) {

            Log.d(LOG_TAG, " setBackground in position " + position );

            viewHolder.imageView.setBackgroundColor(R.color.videona_blue_5);
            viewHolder.textView.setTypeface(null, Typeface.BOLD);

            isPressed = false;

        }
        */

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mColorEffectClickListener.onColorEffectClicked(position);

                viewHolder.imageView.setBackgroundColor(R.color.videona_blue_5);
                viewHolder.textView.setTypeface(null, Typeface.BOLD);


            }
        });

        return convertView;
    }


    private static class ViewHolder {

        ImageView imageView;
        TextView textView;

    }

    /**
     * Return String name, different languages
     */

    private String getColorEffectName(String colorEffectName) {

        String colorName = " ";

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_AQUA) == 0) {

            return getContext().getString(R.string.filter_effect_aqua);
        }

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_BLACKBOARD) == 0) {

            return getContext().getString(R.string.filter_effect_blackboard);
        }

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_MONO) == 0) {

            return getContext().getString(R.string.filter_effect_mono);
        }

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_NEGATIVE) == 0) {

            return getContext().getString(R.string.filter_effect_negative);
        }

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_NONE) == 0) {

            return getContext().getString(R.string.filter_effect_none);
        }

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_POSTERIZE) == 0) {

            return getContext().getString(R.string.filter_effect_posterice);
        }

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_SEPIA) == 0) {

            return getContext().getString(R.string.filter_effect_sepia);
        }

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_WHITEBOARD) == 0) {

            return getContext().getString(R.string.filter_effect_whiteboard);
        }

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_SOLARIZE) == 0) {

            return getContext().getString(R.string.filter_effect_solarice);
        }

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_EMBOSS) == 0) {

            return getContext().getString(R.string.filter_effect_emboss);
        }

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_SKETCH) == 0) {

            return getContext().getString(R.string.filter_effect_sketch);
        }

        if (colorEffectName.compareTo(ConstantsUtils.COLOR_EFFECT_NEON) == 0) {

            return getContext().getString(R.string.filter_effect_neon);
        }

        return colorName;
    }

}

