/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

public class ExitAppFragment extends DialogFragment {


    private SharedPreferences sharedPreferences;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sharedPreferences = this.getActivity().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_settings_exit_app, null);
        builder.setView(v);

        setNegativeButton(v);
        setPositiveButton(v);

        return builder.create();
    }

    private void setPositiveButton(View v) {
        View sendButton = v.findViewById(R.id.activity_settings_exit_confirm_dialog);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
                System.exit(0);
            }
        });
    }

    private void setNegativeButton(View v) {
        View cancelButton = v.findViewById(R.id.activity_settings_exit_close_dialog);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
    }
}
