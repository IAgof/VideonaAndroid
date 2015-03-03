package com.videonasocialmedia.videona.presentation.views.record;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.videonasocialmedia.videona.Config;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.utils.UserPreferences;

import java.util.ArrayList;

/**
 * Created by root on 30/01/15.
 */
public class ImageColorEffectAdapter extends ArrayAdapter<String> {

    private String LOG_TAG = this.getClass().getSimpleName();

    private Activity activity;

    private LayoutInflater inflater;

    private ArrayList<String> colorEffectItems;

    private UserPreferences appPrefs;

    private ViewClickListener mViewClickListener;

    private Boolean isPressed = true;
    private  int lastPositionPressed = 0;

    public interface ViewClickListener {

        void onImageClicked(int position);

    }

    public void setViewClickListener (RecordActivity viewClickListener) {

        mViewClickListener = (ViewClickListener) viewClickListener;
    }

    public void setViewClickListenerLollipop (Camera2VideoFragment viewClickListener) {

        mViewClickListener = (ViewClickListener) viewClickListener;
    }

    public ImageColorEffectAdapter(Activity activity, ArrayList<String> colorEffectItems) {
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

                mViewClickListener.onImageClicked(position);

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
    *
    */

    private String getColorEffectName(String colorEffectName) {

        String colorName = " ";

        if(colorEffectName.compareTo(Config.COLOR_EFFECT_AQUA) == 0) {

            return getContext().getString(R.string.filter_effect_aqua);
        }

        if(colorEffectName.compareTo(Config.COLOR_EFFECT_BLACKBOARD) == 0) {

            return getContext().getString(R.string.filter_effect_blackboard);
        }

        if(colorEffectName.compareTo(Config.COLOR_EFFECT_MONO) == 0) {

            return getContext().getString(R.string.filter_effect_mono);
        }

        if(colorEffectName.compareTo(Config.COLOR_EFFECT_NEGATIVE) == 0) {

            return getContext().getString(R.string.filter_effect_negative);
        }

        if(colorEffectName.compareTo(Config.COLOR_EFFECT_NONE) == 0) {

            return getContext().getString(R.string.filter_effect_none);
        }

        if(colorEffectName.compareTo(Config.COLOR_EFFECT_POSTERIZE) == 0) {

            return getContext().getString(R.string.filter_effect_posterice);
        }

        if(colorEffectName.compareTo(Config.COLOR_EFFECT_SEPIA) == 0) {

            return getContext().getString(R.string.filter_effect_sepia);
        }

        if(colorEffectName.compareTo(Config.COLOR_EFFECT_WHITEBOARD) == 0) {

            return getContext().getString(R.string.filter_effect_whiteboard);
        }

        if(colorEffectName.compareTo(Config.COLOR_EFFECT_SOLARIZE) == 0) {

            return getContext().getString(R.string.filter_effect_solarice);
        }

        if(colorEffectName.compareTo("emboss") == 0) {

            return getContext().getString(R.string.filter_effect_emboss);
        }

        if(colorEffectName.compareTo("sketch") == 0) {

            return getContext().getString(R.string.filter_effect_sketch);
        }

        if(colorEffectName.compareTo("neon") == 0) {

            return getContext().getString(R.string.filter_effect_neon);
        }

        return colorName;
    }

}

