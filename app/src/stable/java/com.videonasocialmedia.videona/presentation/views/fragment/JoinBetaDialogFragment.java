package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.JoinBetaPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.JoinBetaView;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

/**
 * Created by Veronica Lago Fominaya on 12/11/2015.
 */
public class JoinBetaDialogFragment extends DialogFragment implements JoinBetaView {

    private JoinBetaPresenter joinBetaPresenter;
    private EditText email;
    private SharedPreferences sharedPreferences;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sharedPreferences = this.getActivity().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        joinBetaPresenter = new JoinBetaPresenter(this, sharedPreferences);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_join_beta, null);
        builder.setView(v);
        email = (EditText) v.findViewById(R.id.email);
        joinBetaPresenter.checkIfPreviousEmailExists();
        setNegativeButton(v);
        setPositiveButton(v);

        return builder.create();
    }

    private void setPositiveButton(View v) {
        View sendButton = v.findViewById(R.id.sendEmail);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                joinBetaPresenter.validateEmail(email.getText().toString());
            }
        });
    }

    private void setNegativeButton(View v) {
        View cancelButton = v.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
    }

    @Override
    public void setEmail(String text) {
        email.setText(text);
    }

    @Override
    public void showMessage(int messageId) {
        Toast.makeText(getActivity().getApplicationContext(), getString(messageId),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void hideDialog() {
        getDialog().cancel();
    }
}
