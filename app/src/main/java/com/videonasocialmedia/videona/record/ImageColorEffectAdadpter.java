package com.videonasocialmedia.videona.record;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;

import java.util.ArrayList;

/**
 * Created by root on 30/01/15.
 */
public class ImageColorEffectAdadpter extends ArrayAdapter<String> {

        private Context mContext;

        private Activity activity;

        private LayoutInflater inflater;

        private ArrayList<String> colorEffectItems;

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


        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            TextView textView;

            if (inflater == null)
                //inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater = activity.getLayoutInflater();
            if (convertView == null)
                convertView = inflater.inflate(R.layout.item_color_effect, null, true);

                imageView = (ImageView)convertView.findViewById(R.id.imageColorEffect);
                textView = (TextView) convertView.findViewById(R.id.textColorEffect);

         /*   if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                // imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(3, 3, 3, 3);
            } else {
                imageView = (ImageView) convertView;
            }
        */


            LayoutInflater inflater = activity.getLayoutInflater();
         //   View rowView= inflater.inflate(R.layout.item_color_effect, null, true);

            imageView.setImageResource(mThumbIdsImage[position]);
            textView.setText(mThumbIdsText[position]);

            return convertView;
        }




        // references to our images
        private Integer[] mThumbIdsImage = {
                R.drawable.tucan_aqua, R.drawable.tucan_emboss,
                R.drawable.tucan_mono, R.drawable.tucan_negative,
                R.drawable.tucan_neon, R.drawable.tucan_posterize,
                R.drawable.tucan_sepia, R.drawable.tucan_sketch,
                R.drawable.tucan_solarize
        };


        // references to our images
        private String [] mThumbIdsText = {
            "aqua", "emboss",
            "mono", "negative",
            "neon", "posterize",
            "sepia", "sketch",
            "solarize"
        };



}

