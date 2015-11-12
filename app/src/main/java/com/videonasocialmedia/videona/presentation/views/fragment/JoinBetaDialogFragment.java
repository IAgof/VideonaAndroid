package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.eventbus.events.survey.JoinBetaEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by Veronica Lago Fominaya on 12/11/2015.
 */
public class JoinBetaDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_join_beta, null);
        builder.setView(v);
        View cancelButton = v.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JoinBetaDialogFragment.this.getDialog().cancel();
            }
        });
        View sendButton = v.findViewById(R.id.sendEmail);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EventBus.getDefault().post(new JoinBetaEvent());
            }
        });

        return builder.create();
    }

}
