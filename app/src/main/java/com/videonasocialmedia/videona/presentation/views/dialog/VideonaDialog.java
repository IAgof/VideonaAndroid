package com.videonasocialmedia.videona.presentation.views.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.listener.OnVideonaDialogListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Veronica Lago Fominaya on 02/02/2016.
 */
public class VideonaDialog extends DialogFragment {

    private OnVideonaDialogListener listener;
    @InjectView(R.id.positiveButton)
    Button positiveButton;
    @InjectView(R.id.negativeButton)
    Button negativeButton;
    @InjectView(R.id.titleDialog)
    TextView titleDialog;
    @InjectView(R.id.messageDialog)
    TextView messageDialog;
    private int idDialog;

    public VideonaDialog() {
    }

    public static VideonaDialog newInstance(String title, String message, String positive,
                                            String negative, int idDialog) {
        VideonaDialog frag = new VideonaDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("positiveButton", positive);
        args.putString("negativeButton", negative);
        args.putInt("idDialog", idDialog);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.VideonaDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_videona, null);
        builder.setView(v);
        ButterKnife.inject(this, v);

        setTitleDialog(getArguments().getString("title"));
        setMessageDialog(getArguments().getString("message"));
        setPositiveButton(getArguments().getString("positiveButton"));
        setNegativeButton(getArguments().getString("negativeButton"));
        idDialog = getArguments().getInt("idDialog");

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void setListener(OnVideonaDialogListener listener) {
        this.listener = listener;
    }

    public void dismissDialog() {
        this.dismiss();
    }

    private void setTitleDialog(String restTitle) {
        titleDialog.setText(restTitle);
    }

    private void setMessageDialog(String resMessage) {
        messageDialog.setText(resMessage);
    }

    private void setPositiveButton(String resPositive) {
        positiveButton.setText(resPositive);
    }

    public void hidePositiveButton() {
        positiveButton.setVisibility(View.GONE);
    }

    private void setNegativeButton(String resNegative) {
        negativeButton.setText(resNegative);
    }

    public void hideNegativeButton() {
        negativeButton.setVisibility(View.GONE);
    }


    @OnClick(R.id.positiveButton)
    public void onClickPositiveButton() {
        listener.onClickPositiveButton(idDialog);
    }

    @OnClick(R.id.negativeButton)
    public void onClickNegativeButton() {
        listener.onClickNegativeButton(idDialog);
    }


}
