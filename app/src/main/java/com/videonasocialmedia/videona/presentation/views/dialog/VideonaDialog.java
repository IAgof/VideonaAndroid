package com.videonasocialmedia.videona.presentation.views.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.content)
    LinearLayout content;
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.message)
    TextView message;
    @InjectView(R.id.positiveButton)
    Button positiveButton;
    @InjectView(R.id.negativeButton)
    Button negativeButton;
    private int idDialog;

    public VideonaDialog() {}

    public static VideonaDialog newInstance(String title, int image, String message, String positive,
                                            String negative, int idDialog) {
        VideonaDialog frag = new VideonaDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("image", image);
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

        String titleText = getArguments().getString("title");
        if(titleText == null)
            hideTitle();
        else
            setTitle(titleText);
        int imageId = getArguments().getInt("image");
        if(imageId == 0)
            hideImage();
        else
            setImage(imageId);
        String messageText = getArguments().getString("message");
        if(messageText == null)
            hideMessage();
        else
            setMessage(messageText);
        String positiveButtonText = getArguments().getString("positiveButton");
        if(positiveButtonText == null)
            hidePositiveButton();
        else
            setPositiveButton(positiveButtonText);
        String negativeButtonText = getArguments().getString("negativeButton");
        if(negativeButtonText == null)
            hideNegativeButton();
        else
            setNegativeButton(negativeButtonText);
        idDialog = getArguments().getInt("idDialog");

        return builder.create();
    }

    public void setListener(OnVideonaDialogListener listener) { this.listener = listener; }

    private void setTitle(String restTitle) { title.setText(restTitle); }

    private void hideTitle() {
        title.setVisibility(View.GONE);
    }

    private void setImage(int resImage) { image.setImageResource(resImage); }

    private void hideImage() {
        image.setVisibility(View.GONE);
    }

    private void setMessage(String resMessage) { message.setText(resMessage); }

    private void hideMessage() {
        message.setVisibility(View.GONE);
    }

    private void setPositiveButton(String resPositive) {
        positiveButton.setText(resPositive);
    }

    private void hidePositiveButton() {
        positiveButton.setVisibility(View.GONE);
    }

    private void setNegativeButton(String resNegative) {
        negativeButton.setText(resNegative);
    }

    private void hideNegativeButton() {
        negativeButton.setVisibility(View.GONE);
    }

    @OnClick(R.id.positiveButton)
    public void onClickPositiveButton() {
        if(listener != null)
            listener.onClickPositiveButton(idDialog);
    }

    @OnClick(R.id.negativeButton)
    public void onClickNegativeButton() {
        if(listener != null)
            listener.onClickNegativeButton(idDialog);
    }

}
