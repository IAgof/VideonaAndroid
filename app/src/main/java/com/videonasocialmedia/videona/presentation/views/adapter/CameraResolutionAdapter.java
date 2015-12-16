/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.listener.OnCameraResolutionSelectedListener;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

import java.util.ArrayList;

public class CameraResolutionAdapter extends BaseAdapter
{

    private LayoutInflater mInflater;
    CharSequence[] mEntries;
    CharSequence[] mEntryValues;
    ArrayList<RadioButton> rButtonList;
    SharedPreferences sharedPreferences;
    private Context mContext;

    OnCameraResolutionSelectedListener onClickListener;

    public CameraResolutionAdapter(Context context, CharSequence[] entries, CharSequence[] entryValues, OnCameraResolutionSelectedListener onClickListener)
    {
        mEntries = entries;
        mEntryValues = entryValues;
        rButtonList = new ArrayList<RadioButton>();
        mContext = context;
        this.onClickListener = onClickListener;
        mInflater = LayoutInflater.from(context);
        sharedPreferences =  mContext.getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public int getCount()
    {
        return NumResolutionSupported();
        //return mEntries.length;
    }

    public Object getItem(int position)
    {
        return position;
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        CustomHolder holder = null;

        if(row == null)
        {
            row = mInflater.inflate(R.layout.item_camera_dialogs, parent, false);
            holder = new CustomHolder(row, position);
            row.setTag(holder);

        }

        return row;
    }

    private int NumResolutionSupported(){

        int numResolutionSupported = 0;

        boolean isPaidApp = true;
        // TODO check with flavors the app version (free/paid)

        if ( sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, false) && isPaidApp) {
            ++numResolutionSupported;
        }
        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, false) && isPaidApp) {
            ++numResolutionSupported;
        }
        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, false) && isPaidApp) {
            ++numResolutionSupported;
        }

        return numResolutionSupported;
    }


    class CustomHolder
    {
        private TextView text = null;
        private RadioButton rButton = null;

        CustomHolder(View row, int position)
        {
            text = (TextView)row.findViewById(R.id.custom_list_view_row_text_view);
            text.setText(mEntries[position]);
            rButton = (RadioButton)row.findViewById(R.id.custom_list_view_row_radio_button);
            rButton.setId(position);

            rButtonList.add(rButton);

            String prefsResolution = sharedPreferences.getString(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION, "");

            if(prefsResolution.compareTo((String)mEntryValues[position]) == 0){
                rButton.setActivated(true);
            }

            rButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                        int index = buttonView.getId();
                        String value = (String) mEntryValues[index];

                        Log.d("CameraResolutionAdapter", "CameraResolutionAdapter " + value);

                        onClickListener.onClickCameraResolutionListener(value);

                }
            });
        }
    }
}
