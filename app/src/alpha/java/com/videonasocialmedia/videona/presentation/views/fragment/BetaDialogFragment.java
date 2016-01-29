/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.videonasocialmedia.videona.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BetaDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_leave_beta, null);
        builder.setView(v);

        ButterKnife.inject(this, v);

        return builder.create();
    }

    @OnClick(R.id.negativeButton)
    public void goToLeaveBeta() {
        String url = "https://play.google.com/apps/testing/com.videonasocialmedia.videona";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick({R.id.affirmativeButton, R.id.cancel})
    public void dismissBetaDialog() {
        this.dismiss();
    }

}


