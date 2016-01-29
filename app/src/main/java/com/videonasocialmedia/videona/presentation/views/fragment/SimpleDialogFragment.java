package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;

/**
 * Created by Veronica Lago Fominaya on 12/11/2015.
 */
public class SimpleDialogFragment extends DialogFragment {

    private TextView title, message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_simple, null);
        AlertDialog dialog = builder.setCancelable(true)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode,
                                         KeyEvent keyEvent) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dismiss();
                            return true;
                        }
                        return false;
                    }
                })
                .setView(dialogView)
                .create();

        TextView okButton = (TextView) dialogView.findViewById(R.id.ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        title = (TextView) dialogView.findViewById(R.id.title);
        message = (TextView) dialogView.findViewById(R.id.message);
        return dialog;
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }
}
