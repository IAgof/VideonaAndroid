package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.JoinBetaPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.JoinBetaView;
import com.videonasocialmedia.videona.presentation.views.activity.InitAppActivity;
import com.videonasocialmedia.videona.presentation.views.activity.VideonaActivity;

/**
 * Created by Veronica Lago Fominaya on 12/11/2015.
 */
public class CriticalPermissionsDeniedDialogFragment extends DialogFragment{

    private Button okButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.close_app_dialog, null);
        AlertDialog dialog = builder.setCancelable(true)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode,
                                         KeyEvent keyEvent) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            closeApp();
                            return true;
                        }
                        return false;
                    }
                })
                .setView(dialogView)
                .create();

        okButton=(Button)dialogView.findViewById(R.id.ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeApp();
            }
        });

        return dialog;
    }

    protected final void closeApp() {
        Intent intent = new Intent(getActivity().getApplicationContext(), InitAppActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }
}
