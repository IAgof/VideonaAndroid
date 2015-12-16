/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.customviews;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.preference.ListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraQualityAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.OnCameraQualitySelectedListener;

public class ChooseCameraQualityListPreferences extends ListPreference implements OnCameraQualitySelectedListener {

    CameraQualityAdapter cameraQualityAdapter = null;
    Context mContext;
    CharSequence[] entries;
    CharSequence[] entryValues;
    ListView listView;
    TextView textTitle;

    public ChooseCameraQualityListPreferences(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder)
    {
        entries = getEntries();
        entryValues = getEntryValues();

        if (entries == null || entryValues == null || entries.length != entryValues.length )
        {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");

        }

        cameraQualityAdapter = new CameraQualityAdapter(mContext, entries, entryValues, this);

        LayoutInflater inflater = LayoutInflater.from(mContext);//getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_choose_camera_options, null);

        listView = (ListView) v.findViewById(R.id.listViewCameraResolution);
        listView.setAdapter(cameraQualityAdapter);


        textTitle = (TextView) v.findViewById(R.id.titleDialogCamera);
        textTitle.setText(R.string.quality);

        builder.setView(v);

        builder.setPositiveButton(null, null);
        builder.setNegativeButton(null, null);
        builder.setTitle(null);
        setNegativeButton(v);
    }

    // NOTE:
    // The framework forgot to call notifyChanged() in setValue() on previous versions of android.
    // This bug has been fixed in android-4.4_r0.7.
    // Commit: platform/frameworks/base/+/94c02a1a1a6d7e6900e5a459e9cc699b9510e5a2
    // Time: Tue Jul 23 14:43:37 2013 -0700
    //
    // However on previous versions, we have to workaround it by ourselves.
    @Override
    public void setValue(String value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.setValue(value);
        } else {
            String oldValue = getValue();
            super.setValue(value);
            if (!TextUtils.equals(value, oldValue)) {
                notifyChanged();
            }
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        Log.d("ChooseCameraResolutionListPreferences", "onDialogClosed ");

    }

    private void setNegativeButton(View v) {
        View cancelButton = v.findViewById(R.id.activity_settings_cancel_resolution_dialog);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
    }

    @Override
    public void onClickCameraQualityListener(String value) {

        setValue(value);
        getDialog().dismiss();
    }
}
