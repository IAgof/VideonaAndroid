package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.JoinBetaPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.JoinBetaView;

/**
 * Created by Veronica Lago Fominaya on 12/11/2015.
 */
public class JoinBetaDialogFragment extends DialogFragment implements JoinBetaView {

    private JoinBetaPresenter joinBetaPresenter;
    private EditText email;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        joinBetaPresenter = new JoinBetaPresenter(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_join_beta, null);
        builder.setView(v);
        email = (EditText) v.findViewById(R.id.email);
        View cancelButton = v.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        View sendButton = v.findViewById(R.id.sendEmail);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                joinBetaPresenter.validateEmail(email.getText().toString());
            }
        });

        return builder.create();
    }

    @Override
    public void showMessage() {
        Toast.makeText(getActivity().getApplicationContext(), R.string.invalid_email,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void hideDialog() {
        getDialog().cancel();
    }
}
