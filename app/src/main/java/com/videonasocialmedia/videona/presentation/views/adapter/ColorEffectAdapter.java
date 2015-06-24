package com.videonasocialmedia.videona.presentation.views.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.videona.presentation.views.listener.ColorEffectClickListener;
import com.videonasocialmedia.videona.utils.Constants;
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

    private String colorEffectName;
    private Boolean isPressed = false;


    public void setViewClickListener(RecordActivity viewClickListener) {

        mColorEffectClickListener = (ColorEffectClickListener) viewClickListener;
    }

    /*
     Descomentar Camera2VideoFragment

    public void setViewClickListenerLollipop(Camera2VideoFragment viewClickListener) {

        mColorEffectClickListener = (ColorEffectClickListener) viewClickListener;
    }

    */

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

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }


        colorEffectName = colorEffectItems.get(position);

        // Log.d(LOG_TAG, "getView position " + position + " colorEffectName " + colorEffectName + " positionColorEffectPressed " + RecordActivity.positionColorEffectPressed);

        String colorEffectDrawableName = "common_filter_" + colorEffectName;

        // colorEffectName, key value to obtain drawable resources effect_ + colorEffectName
        // int resourceId = activity.getResources().getIdentifier(colorEffectDrawableName, "drawable", activity.getPackageName());
        int resourceId = 0;

        if (RecordActivity.positionColorEffectPressed == position) {
            isPressed = true;
            resourceId = getResourceDrawableColorEffect(colorEffectName, isPressed);
            isPressed = false;
        } else {
            resourceId = getResourceDrawableColorEffect(colorEffectName, isPressed);
        }

        viewHolder.imageView.setImageResource(resourceId);


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


                // Restart last position


                isPressed = true;
                colorEffectName = colorEffectItems.get(position);

                mColorEffectClickListener.onColorEffectClicked(new ColorEffectAdapter(activity, colorEffectItems), colorEffectName, position);

                int resourceIdPressed = getResourceDrawableColorEffect(colorEffectName, isPressed);
                viewHolder.imageView.setImageResource(resourceIdPressed);
                isPressed = false;
                // viewHolder.imageView.setBackgroundColor(R.color.videona_blue_5);
                // viewHolder.textView.setTypeface(null, Typeface.BOLD);


            }
        });

        return convertView;
    }


    private static class ViewHolder {

        ImageView imageView;
        // TextView textView;

    }


    /**
     * Return String name, different languages
     */

    private String getColorEffectName(String colorEffectName) {

        String colorName = " ";

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_AQUA) == 0) {

            return getContext().getString(R.string.filter_effect_aqua);
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_BLACKBOARD) == 0) {

            return getContext().getString(R.string.filter_effect_blackboard);
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_MONO) == 0) {

            return getContext().getString(R.string.filter_effect_mono);
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_NEGATIVE) == 0) {

            return getContext().getString(R.string.filter_effect_negative);
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_NONE) == 0) {

            return getContext().getString(R.string.filter_effect_none);
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_POSTERIZE) == 0) {

            return getContext().getString(R.string.filter_effect_posterice);
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_SEPIA) == 0) {

            return getContext().getString(R.string.filter_effect_sepia);
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_WHITEBOARD) == 0) {

            return getContext().getString(R.string.filter_effect_whiteboard);
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_SOLARIZE) == 0) {

            return getContext().getString(R.string.filter_effect_solarice);
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_EMBOSS) == 0) {

            return getContext().getString(R.string.filter_effect_emboss);
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_SKETCH) == 0) {

            return getContext().getString(R.string.filter_effect_sketch);
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_NEON) == 0) {

            return getContext().getString(R.string.filter_effect_neon);
        }

        return colorName;
    }

    public int getResourceDrawableColorEffect(String colorEffectName, boolean isPressed) {

        String colorName = " ";

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_AQUA) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_aqua_ad1_normal;
            } else {
                return R.drawable.common_filter_aqua_ad1_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_BLACKBOARD) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_blackboard_ad2_normal;
            } else {
                return R.drawable.common_filter_blackboard_ad2_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_MONO) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_mono_ad4_normal;
            } else {
                return R.drawable.common_filter_mono_ad4_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_NEGATIVE) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_negative_ad5_normal;
            } else {
                return R.drawable.common_filter_negative_ad5_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_NONE) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_none_ad0_normal;
            } else {
                return R.drawable.common_filter_none_ad0_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_POSTERIZE) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_posterize_ad7_normal;
            } else {
                return R.drawable.common_filter_posterize_ad7_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_SEPIA) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_sepia_ad8_normal;
            } else {
                return R.drawable.common_filter_sepia_ad8_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_WHITEBOARD) == 0) {
            if (!isPressed) {
                return R.drawable.common_filter_whiteboard_ad11_normal;
            } else {
                return R.drawable.common_filter_whiteboard_ad11_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_SOLARIZE) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_solarize_ad10_normal;
            } else {
                return R.drawable.common_filter_solarize_ad10_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_EMBOSS) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_emboss_ad3_normal;
            } else {
                return R.drawable.common_filter_emboss_ad3_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_SKETCH) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_sketch_ad9_normal;
            } else {
                return R.drawable.common_filter_sketch_ad9_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_NEON) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_neon_ad6_normal;
            } else {
                return R.drawable.common_filter_neon_ad6_pressed;
            }
        }

        return 0;
    }

}

