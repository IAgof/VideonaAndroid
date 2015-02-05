package com.videonasocialmedia.videona.record;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.UserPreferences;

import java.util.ArrayList;

/**
 * Created by root on 30/01/15.
 */
public class ImageColorEffectAdadpter extends ArrayAdapter<String> {

        private Context mContext;

        private Activity activity;

        private LayoutInflater inflater;

        private ArrayList<String> colorEffectItems;

        private UserPreferences appPrefs;

        private ViewClickListener mViewClickListener;

        public interface ViewClickListener {
            void onImageClicked(int position);
        }

        public void setViewClickListener (RecordActivity viewClickListener) {
            mViewClickListener = viewClickListener;
        }


    public ImageColorEffectAdadpter(Activity activity, Context c, ArrayList<String> colorEffectItems) {
            super(activity, R.layout.item_color_effect, colorEffectItems);
            this.activity = activity;
            mContext = c;
            this.colorEffectItems = colorEffectItems;
    }

        public int getCount() {
           // return mThumbIdsImage.length;
            return colorEffectItems.size();
        }

        public String getItem(int position) {
            return colorEffectItems.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }


        // create a new ImageView and TextView for each item referenced by the Adapter
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            final TextView textView;

            if (inflater == null)
                //inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater = activity.getLayoutInflater();
            if (convertView == null)
                convertView = inflater.inflate(R.layout.item_color_effect, null, true);

                imageView = (ImageView)convertView.findViewById(R.id.imageColorEffect);
                textView = (TextView) convertView.findViewById(R.id.textColorEffect);


          //  imageView.setImageResource(mThumbIdsImage[position]);
            String colorEffectName = CameraPreview.colorEffects.get(position);
            String colorEffectDrawableName = "effect_" + colorEffectName;

            int resourceId = activity.getResources().getIdentifier(colorEffectDrawableName, "drawable", activity.getPackageName());
            imageView.setImageResource(resourceId);

            textView.setText(colorEffectName);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mViewClickListener.onImageClicked(position);

                }
            });

            return convertView;
        }

        // references to our images
        private Integer[] mThumbIdsImage = {
                R.drawable.effect_aqua, R.drawable.effect_emboss,
                R.drawable.effect_mono, R.drawable.effect_negative,
                R.drawable.effect_neon, R.drawable.effect_posterize,
                R.drawable.effect_sepia, R.drawable.effect_sketch,
                R.drawable.effect_solarize, R.drawable.effect_blackboard,
                R.drawable.effect_whiteboard, R.drawable.effect_none
        };






}

